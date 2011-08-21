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
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Luu thong tin ve van ban da duoc gan nhan va cac phuong thuc de lay ra duoc 1 tu trong van ban hoac
 * 1 cau trong van ban dua vao vi tri cua tu hoac cua cau
 * @author banhbaochay
 */
public class TaggedDocument {

    static Logger logger = Logger.getLogger(TaggedDocument.class);

    static {
        DOMConfigurator.configure("log-config.xml");
    }
    /**
     *  Default IOB file: tmp/temp.iob
     */
    public static final String TEMP_IOB_FILE = "tmp/temp.iob";

    /**
     * Tao doi tuong TaggedDocument tu 1 file van ban da duoc gan nhan hoac
     * tu 1 file van ban IOB co duoi .iob
     * @param filePath
     */
    public TaggedDocument(String filePath) {

        sentList = new LinkedList<Sentence>();
        if (filePath.endsWith(".iob")) {
            // Tao doc tu file iob
            createSentence(filePath);
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
            createSentence(TEMP_IOB_FILE);
            
            File f = new File(TEMP_IOB_FILE);
            f.deleteOnExit();
        }
    }// end constructor
    
    /**
     *  Dua thong tin tu file IOB vao sentList
     */
    private void createSentence(String IobFile) {
        try {
            BufferedReader in = ReadWriteFile.readFile(IobFile);
            String line = null;
            Sentence sent = new Sentence();
            int offsetWord = 0;
            int offsetSentence = 0;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    Word word = new Word(line.split("\t")[0]);
                    word.setOffset(offsetWord);
                    word.setIob(line.split("\t")[1]);
                    sent.addWord(word);
                    offsetWord++;
                } else {
                    offsetWord = 0;
                    sent.setOffset(offsetSentence);
                    sentList.add(sent);
                    offsetSentence++;
                    sent = new Sentence();
                }
                
            }
            in.close();
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }
    
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
    
    /**
     * Tra ve so luong cau trong van ban
     * @return 
     */
    public int size() {
        return sentList.size();
    }// end size method
    
    /**
     * In van ban ra man hinh: moi tu duoc bao trong cap ngoac [ ]
     */
    public void print() {
        for (Sentence sentence : sentList) {
            System.out.println(sentence.toString("[", "]"));
        }// end foreach sentence
    }// end print method
    
    private List<Sentence> sentList;
    
    public static void main(String[] args) {
        TaggedDocument doc = new TaggedDocument("tmp/tagged.txt");
        doc.setIob("B-per", 0, 0);
        Word word = doc.getWord("0-0");
        System.out.println(word.getIob());
        System.out.println(doc.getPhrase("0-1", 2));
    }// end main class
    
}// end TaggedDocument class

