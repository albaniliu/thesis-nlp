/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 * Luu thong tin ve van ban da duoc gan nhan va cac phuong thuc de lay ra duoc 1 tu trong van ban hoac
 * 1 cau trong van ban dua vao vi tri cua tu hoac cua cau
 * @author banhbaochay
 */
public class TaggedDocument implements Cloneable {

    static Logger logger = Logger.getLogger(TaggedDocument.class);
    /**
     *  Default IOB file: tmp/temp.iob
     */
    private static final String TEMP_IOB_FILE = "tmp/temp.iob";

    private TaggedDocument() {
        sentList = new LinkedList<Sentence>();
        labelCountMap = new HashMap<String, Integer>();
        iobCountMap = new HashMap<String, Integer>();
        iobMap = new TreeMap<Offset, String>();
        labelMap = new TreeMap<Offset, String>();
    }

    /**
     * Tao doi tuong TaggedDocument tu 1 file van ban da duoc gan nhan hoac
     * tu 1 file van ban IOB co duoi .iob
     * @param filePath
     */
    public TaggedDocument(String filePath) {

        sentList = new LinkedList<Sentence>();
        labelCountMap = new HashMap<String, Integer>();
        iobCountMap = new HashMap<String, Integer>();
        iobMap = new TreeMap<Offset, String>();
        labelMap = new TreeMap<Offset, String>();
        if (filePath.endsWith(".iob")) {
            // Tao doc tu file iob
            analyze(filePath);
        } else {
            // Tao doc tu file van ban thuong
            /*
             * Kiem tra thu muc tmp, neu chua co thi tao moi
             */
            File tmp = new File("tmp");
            if (!tmp.exists() || !tmp.isDirectory()) {
                if (!tmp.mkdir()) {
                    logger.info("Can't create tmp!");
                    System.exit(0);
                }
            }// end if

            /*
             * Tao file IOB tu van ban nay
             */
            IOB2Converter.convertAllLine(filePath, TEMP_IOB_FILE);
            analyze(TEMP_IOB_FILE);

            File f = new File(TEMP_IOB_FILE);
            f.deleteOnExit();
        }
    }// end constructor

    /**
     *  Dua thong tin tu file IOB vao sentList
     */
    //<editor-fold defaultstate="collapsed" desc="analyze method">
    private void analyze(String iobFile) {
        try {
            BufferedReader in = ReadWriteFile.readFile(iobFile);
            String line = null;
            Sentence sent = new Sentence();
            int offsetWord = 0;
            int offsetSentence = 0;
            String iob = null;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    iob = line.split("\t")[1];

                    if (!iob.equals("O")) {
                        Offset offset = new Offset(offsetSentence, offsetWord);
                        iobMap.put(offset, iob);
                    }

                    Word word = new Word(line.split("\t")[0]);
                    word.setOffset(offsetWord);
                    word.setIob(iob);
                    sent.addWord(word);
                    offsetWord++;
                } else {
                    // bat dau cau moi
                    offsetWord = 0;
                    sent.setOffset(offsetSentence);
                    sentList.add(sent);
                    offsetSentence++;
                    sent = new Sentence();
                }// end if line

            }// end while

            in.close();
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }// end try catch

        // Tao iobCountMap
        createIobCountMap();
        // Tao labelPosMap
        createLabelMap();
    }// end analyze method
    // </editor-fold>

    /**
     * Tao doi tuong tagged doc moi tu label map
     * @param labelMap
     * @return 
     */
    public TaggedDocument createTaggedDoc(Map labelMap) {
        TaggedDocument re = new TaggedDocument();
        re.labelMap = labelMap;
        re.sentList = this.sentList;
        re.createLabelCountMap();
        return re;
    }// end createTaggedDoc method

    /**
     * Tao labelPosMap tu iobPosMap
     */
    //<editor-fold defaultstate="collapsed" desc="createLabelPosMap method">
    private void createLabelMap() {
        labelMap = new TreeMap<Offset, String>();
        Offset prevOffset = null;
        Offset startOffset = null;
        Offset endOffset = null;
        String label = null;
        if (iobMap.size() > 0) {
            for (Object object : iobMap.entrySet()) {
                Map.Entry<Offset, String> entry = (Map.Entry<Offset, String>) object;
                Offset curOffset = entry.getKey();
                String curIob = entry.getValue();
                if (prevOffset == null) {
                    if (curIob.startsWith("B-")) {
                        label = curIob.split("-")[1];
                        startOffset = curOffset;
                    } else {
                        System.err.println("Phan tich IOB bi loi, bat dau bang nhan I");
                        System.exit(0);
                    }// end if curIob.startsWith("B-")
                } else {
                    if (curIob.startsWith("B-")) {
                        endOffset = prevOffset;
                        labelMap.put(Offset.createOffset(startOffset, endOffset), label);
                        startOffset = curOffset;
                        label = curIob.split("-")[1];
                    }// end if curIob is B-
                }// end prevOffset ? null

                prevOffset = curOffset;
            }// end foreach object
            labelMap.put(Offset.createOffset(startOffset, prevOffset), label);

            // Tao doi tuong labelCountMap
            createLabelCountMap();
        } else {
            logger.error("Map iob pos khong co phan tu nao");
        }// end if iobPosMap size ? 0

    }// end createLabelPosMap method
    //</editor-fold>

    /**
     * Tao doi tuong labelCountMap sau khi doi tuong labelPosMap da co du lieu ve vi tri cac cum tu gan nhan thuc the
     */
    //<editor-fold defaultstate="collapsed" desc="createLabelCountMap method">
    private void createLabelCountMap() {
        labelCountMap = new HashMap<String, Integer>();
        labelList = new ArrayList<String>();
        for (Object key : labelMap.keySet()) {
            String label = (String) labelMap.get(key);
            if (labelCountMap.containsKey(label)) {
                // Nhan thuc the nay da duoc dem
                int count = (Integer) labelCountMap.get(label);
                labelCountMap.put(label, ++count);
            } else {
                // Nhan thuc the nay chua duoc dem
                labelCountMap.put(label, 1);
                labelList.add(label);
            }
        }// end foreach key
    }// end createLabelCountMap method
    //</editor-fold>

    /**
     * Tao doi tuong IobCountMap sau khi doi tuong iobPosMap da luu thong tin vi tri cac tu duoc gan nhan IOB trong van ban
     */
    //<editor-fold defaultstate="collapsed" desc="createIobCountMap method">
    private void createIobCountMap() {
        for (Object key : iobMap.keySet()) {
            String iob = (String) iobMap.get(key);
            if (iobCountMap.containsKey(iob)) {
                // Nhan iob nay da duoc dem
                int count = (Integer) iobCountMap.get(iob);
                iobCountMap.put(iob, ++count);
            } else {
                // Nhan iob nay chua duoc dem
                iobCountMap.put(iob, 1);
            }
        }// end foreach key
    }// end createIobCountMap
    //</editor-fold>

    /**
     * Lay 1 cau thu i trong van ban
     * @param offset vi tri cua cau trong van ban
     * @return Tra ve null neu offset nho hon 0 hoac lon hon so cau cua van ban
     */
    public Sentence getSentence(int offset) {
        return (offset < 0 || offset >= size()) ? null : sentList.get(offset);
    }// end getSentence method

    /**
     * Lay ra 1 cum tu thuoc 1 cau trong van ban, bat dau tu offsetWordStart den offsetWordEnd - 1
     * @param offsetSentence vi tri cua cau trong van ban
     * @param offsetWordStart vi tri bat dau cua cum tu trong cau
     * @param offsetWordEnd vi tri ket thuc cua cum tu trong cau
     * @return  Tra ve null neu khong the lay duoc cau o vi tri offsetSentence hoac offsetWordStart, 
     * offsetWordEnd nam ngoai khoang cho phep cua cau
     */
    public Sentence getPhrase(int offsetSentence, int offsetWordStart, int offsetWordEnd) {
        Sentence sent = getSentence(offsetSentence);
        if (sent != null) {
            return sent.fragment(offsetWordStart, offsetWordEnd);
        } else {
            return null;
        }
    }// end getPhrase method

    /**
     * Lay ra 1 cum tu xung quanh 1 tu trong van ban
     * @param offset Chi ra vi tri cua tu trong van ban theo dang: vi tri cau - vi tri tu trong cau
     * @param windowSize Chi ra se lay bao nhieu tu xung quanh tu goc
     * @return 
     */
    public Sentence getPhrase(String offset, int windowSize) {
        int offsetSentence = Integer.parseInt(offset.split("-")[0]);
        int offsetWord = Integer.parseInt(offset.split("-")[1]);
        int start = offsetWord - windowSize;
        int end = offsetWord + windowSize + 1;

        return getPhrase(offsetSentence, start, end);
    }// end getPhrase method

    /**
     * Lay ra 1 tu trong van ban
     * @param offsetSentence vi tri cua cau trong van ban
     * @param offsetWord vi tri tu trong cau
     * @return Tra ve null neu vi tri cau trong van ban nam ngoai khoang cua van ban hoac
     * vi tri cua tu nam ngoai khoang cua cau
     */
    public Word getWord(int offsetSentence, int offsetWord) {
        Sentence sent = getSentence(offsetSentence);
        if (sent == null) {
            return null;
        } else {
            return sent.wordAt(offsetWord);
        }
    }// end getWord method

    /**
     * Lay ra 1 tu trong van ban
     * @param offset Chi ra vi tri cua tu trong van ban theo dinh dang: vi tri cau trong van ban - vi tri tu trong cau
     * @return Tra ve null neu vi tri cau trong van ban nam ngoai khoang cua van ban hoac
     * vi tri cua tu nam ngoai khoang cua cau
     */
    public Word getWord(String offset) {
        int offsetSentence = Integer.parseInt(offset.split("-")[0]);
        int offsetWord = Integer.parseInt(offset.split("-")[1]);
        return getWord(offsetSentence, offsetWord);
    }// end getWord method

    /**
     * Set nhan IOB cho 1 tu trong van ban
     * @param iobLabel Nhan IOB gan cho tu
     * @param offsetSentence vi tri cua cau trong van ban
     * @param offsetWord vi tri cua tu trong van ban
     * @return Tra ve true neu set thanh cong 
     */
    public boolean setIob(String iobLabel, int offsetSentence, int offsetWord) {
        Word word = getWord(offsetSentence, offsetWord);
        if (word == null) {
            return false;
        } else {
            word.setIob(iobLabel);
            return true;
        }
    }// end setIob method

    /**
     * Set nhan IOB cho 1 tu trong van ban
     * @param iobLabel Nhan IOB gan cho tu
     * @param offset Chi ra vi tri cua tu trong van ban theo dinh dang: vi tri cau trong van ban - vi tri tu trong cau
     * @return Tra ve true neu set thanh cong 
     */
    public boolean setIob(String iobLabel, String offset) {
        int offsetSentence = Integer.parseInt(offset.split("-")[0]);
        int offsetWord = Integer.parseInt(offset.split("-")[1]);
        return setIob(iobLabel, offsetSentence, offsetWord);
    }// end setIob method

    public boolean setIob(String iob, Offset offset) {
        return setIob(iob, offset.getOffsetSent(), offset.getOffsetWord());
    }// end setIob method

    /**
     * Tra ve so luong cau trong van ban
     * @return 
     */
    public int size() {
        return sentList.size();
    }// end size method

    /**
     * @return the iobPosMap
     */
    public Map getIobPosMap() {
        return iobMap;
    }

    /**
     * @return Luu thong tin vi tri cua tu hoac cum tu duoc gan nhan.
     * Key la doi tuong Offset chi ra vi tri cum tu trong van ban, value la nhan cua tu do
     */
    public Map getLabelPosMap() {
        return labelMap;
    }

    /**
     * Tra ve so luong nhan iob co trong van ban
     * @param iob Ten nhan iob can tim so luong
     * @return 
     */
    public Integer getIobCountByName(String iob) {
        return (Integer) iobCountMap.get(iob);
    }// end getIobCountByName method

    /**
     * Tra ve kieu iterator cac nhan thuc the co trong van ban
     * @return 
     */
    public List<String> getLabelList() {
        return labelList;
    }// end getLabelList method

    /**
     * Tra ve so luong nhan thuc the co ten <code>label</code> co trong van ban
     * @param label Ten nhan thuc the muon tim so luong
     * @return 
     */
    public Integer getLabelCountByName(String label) {
        return (Integer) labelCountMap.get(label);
    }// end getLabelCountByName method

    /**
     * Set iobPosMap cho doi tuong, dong thoi tao thong tin ve iobCOunt, labelPos, labelCount.. Chua tao thong tin ve sentList
     * @param iobPosMap Key la doi tuong Offset chi ra vi tri cua tu trong van ban, value la nhan iob cua tu do
     */
    public void setIobPosMap(Map iobPosMap) {
        this.iobMap = iobPosMap;
        createIobCountMap();
        createLabelMap();
    }// end setIobPosMap method

    public void setLabel(Offset offset, String label) {
        labelMap.put(offset, label);
    }// end setLabel method

    @Override
    public Object clone() {
        try {
            TaggedDocument cloned = (TaggedDocument) super.clone();
            cloned.sentList = new LinkedList<Sentence>(sentList);
            cloned.iobMap = new TreeMap(iobMap);
            cloned.iobCountMap = new HashMap(iobCountMap);
            cloned.labelMap = new TreeMap(labelMap);
            cloned.labelCountMap = new HashMap(labelCountMap);
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }// end try
    }// end clone method
    
    /**
     * In van ban ra man hinh: moi tu duoc bao trong cap ngoac [ ]
     */
    public void print() {
        for (Sentence sentence : sentList) {
            System.out.println(sentence.toString("[", "]"));
        }// end foreach sentence
    }// end print method

    /**
     * List cac sentence trong van ban
     */
    private List<Sentence> sentList;
    // Chua so luong nhan duoc gan trong van ban
    // Key la ten nhan (String), value la so luong nhan duoc gan voi ten nhan do (Integer)
    private Map labelCountMap;
    /**
     * Chu so luong iob duoc gan trong van ban
     * Key la ten iob, value la so luong tu duoc gan voi iob do (Integer)
     */
    private Map iobCountMap;
    /**
     * Luu thong tin vi tri cua tu duoc gan nhan IOB theo dang.
     * Key la doi tuong Offset chi ra vi tri tu trong van ban, value la nhan IOB cua tu do
     */
    private Map iobMap;
    /**
     * Luu thong tin vi tri cua tu hoac cum tu duoc gan nhan
     * Key la doi tuong Offset chi ra vi tri cum tu trong van ban, value la nhan cua tu do
     */
    private Map labelMap;
    
    /**
     * List cac nhan thuc the trong van ban
     */
    private List<String> labelList;
    
}// end TaggedDocument class

