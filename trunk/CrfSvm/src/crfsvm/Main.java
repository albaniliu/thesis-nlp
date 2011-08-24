/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.IOB2Converter;
import crfsvm.crf.een_phuong.Sentence;
import crfsvm.crf.een_phuong.TaggedDocument;
import crfsvm.crf.een_phuong.TaggingTrainData;
import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import crfsvm.util.Document;
import crfsvm.util.MapUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Main {

    static Logger logger = Logger.getLogger(Main.class);
    
    static {
        DOMConfigurator.configure("log-config.xml");
    }
    
    static final String PREDICT_FILE = "tmp/tagged.txt.wseg";
    /**
     * File iob duoc convert tu van ban sau khi predict - van ban co duoi iob: tmp/tagged.txt.wseg.iob
     */
    static final String IOB_FILE = "tmp/tagged.txt.wseg.iob";
    /**
     * so phan lop
     */
    static final int C = 13;
    /**
     * So luong bagging
     */
    static int B = 5;
    /**
     * batch size
     */
    static int S = 2;
    /**
     * nguong cho entropy
     */
    static double thresholdH = 0.277;
    /**
     * Number example in one bag
     */
    static int bagSize = 120;

    /**
     * Merge tat ca cac file .txt trong thu muc dirPath thanh file mergePath
     * @param dirPath Duong dan toi thu muc chua cac file van ban muon merge
     * @param mergePath Duong dan toi file output sau khi merge
     */
    // <editor-fold defaultstate="collapsed" desc="mergeFile method">
    public void mergeFile(String dirPath, String mergePath) {
        File dir = new File(dirPath);
        File[] fileList = dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        Document retDoc = new Document();
        for (File file : fileList) {
            Document doc = new Document(file);
            retDoc.append(doc);
        }// end foreach file
        retDoc.print2File(mergePath);
    }// end mergeFile method
    // </editor-fold>

    /**
     * Merge tat ca cac file duoc loc ra boi nameFilter trong dirPath thanh file mergePath
     * @param dirPath Duong dan toi thu muc chua cac file van ban muon merge
     * @param mergePath Duong dan toi file output sau khi merge
     * @param nameFilter Bo loc ten cua file trong thu muc dirPath
     */
    // <editor-fold defaultstate="collapsed" desc="mergeFile method">
    public void mergeFile(String dirPath, String mergePath, FilenameFilter nameFilter) {
        File dir = new File(dirPath);
        File[] fileList = dir.listFiles(nameFilter);
        Document retDoc = new Document();
        for (File file : fileList) {
            Document doc = new Document(file);
            retDoc.append(doc);
        }// end foreach file
        retDoc.print2File(mergePath);
    }// end mergeFile method
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="createIOB2 method">
    public void createIOB2() {
        String[] args = new String[2];
        args[0] = "data/Temp/merge.txt";
        args[1] = "data/Temp/iob2.txt";
        IOB2Converter.main(args);
    }// end createIOB2 method
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="taggingTrain method">
    public void taggingTrain() {
        String[] args = new String[3];
        args[0] = "data/Temp/iob2.txt";
        args[1] = "data/Temp/iob2-tagged.txt";
        args[2] = "model";
        TaggingTrainData.main(args);
    }// end taggingTrain method
    // </editor-fold>

    /**
     * Tao tap train trong thu muc tmp/TrainSet tu 1 document
     * @param doc
     * @param B so luong bag
     * @param bagSize so luong cau trong 1 bag
     */
    // <editor-fold defaultstate="collapsed" desc="createTrainSet method">
    public void createTrainSet(Document doc, int B, int bagSize) {
        List docList = doc.createBagging(B, bagSize);
        String trainSetDir = "tmp/TrainSet";
        File dir = new File(trainSetDir);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        } else {
            dir.mkdir();
        }
        for (Object child : docList) {
            Document docChild = (Document) child;
            docChild.print2File(trainSetDir + "/" + docChild.getFileName());
        }
        logger.info("Create train set successfull");
    }// end createTrainSet method
    // </editor-fold>

    /**
     * Tao tap test trong thu muc tmp/TestSet
     * @param doc
     * @param number chia thanh bao nhieu phan
     */
    // <editor-fold defaultstate="collapsed" desc="createTestSet method">
    public void createTestSet(Document doc, int number) {
        List docList = doc.split(number);
        String testSetDir = "tmp/TestSet";
        File dir = new File(testSetDir);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        } else {
            dir.mkdir();
        }// end if else

        for (Object child : docList) {
            Document docChild = (Document) child;
            docChild.print2File(testSetDir + "/" + docChild.getFileName());
        }// end foreach child
        logger.info("Create test set successfull");
    }// end createTestSet method
    // </editor-fold>

    /**
     * Dem so lan xuat hien cua tu duoc gan nhan trong file da duoc gan nhan, ket qua luu trong countMap
     * @param countMap Key la vi tri cua tu, co dang offsetSentence-offsetWord. Value la 1 map voi 
     * key la nhan IOB cua tu, value la so lan duoc gan
     * @param predictFilePath duong dan den file da duoc predict ( la file co duoi .wseg )
     */
    // <editor-fold defaultstate="collapsed" desc="countAppear method">
    private void countAppear(Map<String, Map<String, Integer>> countMap, String predictFilePath) {
        TaggedDocument tmpDoc = new TaggedDocument(predictFilePath);
        for (int i = 0; i < tmpDoc.size(); i++) {
            List<String> iobList = tmpDoc.getSentence(i).getIobList();
            for (String iobInfo : iobList) {
                String offset = iobInfo.split(",")[0];
                String iobLabel = iobInfo.split(",")[1];
                if (countMap.containsKey(offset)) {
                    // Tu nay da co trong map
                    Map<String, Integer> iobCountMap = countMap.get(offset);
                    if (iobCountMap.containsKey(iobLabel)) {
                        // Nhan IOB nay da duoc dem truoc do 
                        int count = iobCountMap.get(iobLabel);
                        iobCountMap.put(iobLabel, ++count);
                    } else {
                        // Bien dem cho nhan IOB nay chua xuat hien
                        iobCountMap.put(iobLabel, 1);
                    }
                } else {
                    // Them moi thong tin count cho 1 tu chua co trong countMap
                    Map<String, Integer> iobCountMap = new HashMap<String, Integer>();
                    iobCountMap.put(iobLabel, 1);
                    countMap.put(offset, iobCountMap);
                }
            }// end foreach iobInfo
        }// end for i
    }// end countAppear method
    // </editor-fold>

    /**
     * Tinh Entropy tu count co duoc
     * @param countMap danh sach dem so lan duoc gan nhan IOB cua cac tu trong van ban
     * @return Tra ve map voi key la vi tri cua tu trong van ban, value la ten cua nhan IOB 
     * duoc gan nhieu nhat cho tu do cung voi entropy
     */
    // <editor-fold defaultstate="collapsed" desc="calculateH method">
    public Map<String, Object[]> calculateH(Map<String, Map<String, Integer>> countMap) {
        Map<String, Object[]> hMap = new LinkedHashMap<String, Object[]>();

        for (Map.Entry<String, Map<String, Integer>> entry : countMap.entrySet()) {
            // Tinh H cho tung tu duoc gan nhan
            String offset = entry.getKey();
            Map<String, Integer> iobCountMap = entry.getValue();
            double H = 0;
            int countMax = 0;
            String iobLabel = "";
            for (Map.Entry<String, Integer> entryIobCount : iobCountMap.entrySet()) {
                // Tinh tung so hang trong bieu thuc H
                int ri = entryIobCount.getValue();
                double riOverB = (double) ri / B;
                H -= riOverB * Math.log(riOverB);

                // Luu lai cach gan nhan co so lan nhieu nhat
                if (countMax < ri) {
                    countMax = ri;
                    iobLabel = entryIobCount.getKey();
                }// end if countMax
            }// end foreach entryIobCount
            hMap.put(offset, new Object[]{
                        iobLabel,
                        H
                    });
        }// end foreach entry

        return hMap;
    }// end calculateH method
    // </editor-fold>

    /**
     * Tim ra S phan tu trong map co so entropy nho nhat khong vuot qua nguong
     * @param hMap Co dang key la vi tri cua tu, value la mang 2 phan tu: nhan IOB duoc gan
     * nhieu nhat cho tu do va entropy cua tu
     * @return  List cac phan tu thoa man yeu cau co entropy khong  vuot qua nguong va la nho nhat, 
     * moi phan tu la mang cac String. Mang String co dang: phan tu thu nhat la vi tri cua tu trong van ban,
     * phan tu thu 2 la nhan IOB duoc gan nhieu nhat cho tu do
     */
    // <editor-fold defaultstate="collapsed" desc="findS method">
    public List<String[]> findS(Map<String, Object[]> hMap) {
        // Sap xep lai entropy map theo thu tu tang dan cua entropy
        hMap = MapUtils.sortMap(hMap, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                Map.Entry<String, Object[]> entry1 = (Map.Entry<String, Object[]>) o1;
                Map.Entry<String, Object[]> entry2 = (Map.Entry<String, Object[]>) o2;
                Double h1 = (Double) entry1.getValue()[1];
                Double h2 = (Double) entry2.getValue()[1];
                return h1.compareTo(h2);
            }
        });

        // Lay ra S entropy thap nhat va khong vuot qua nguong
        List<String[]> sList = new LinkedList<String[]>();
        int i = 0;
        for (Map.Entry<String, Object[]> entry : hMap.entrySet()) {
            Double H = (Double) entry.getValue()[1];
            if (H > thresholdH) {
                break;
            } else {
                sList.add(new String[]{
                            entry.getKey(),
                            (String) entry.getValue()[0]
                        });
                i++;
                if (i == S) {
                    break;
                }// end if i == S
            }// end if else
        }// end foreach entry

        return sList;
    }// end findS method
    // </editor-fold>

    /**
     * Tinh toan entropy cho cac tu duoc gan nhan trong van ban dong thoi lay ra S phan tu co entropy thap nhat
     * khong vuot qua nguong
     * @param countMap danh sach dem so lan duoc gan nhan IOB cua cac tu trong van ban. countMap co dinh dang
     * luu tru theo key la vi tri cua tu trong van ban (VD: 3-54 chi ra tu thu 54 trong cau thu 3 cua van ban), value la
     * 1 map voi key la nhan IOB cua tu (B-per, I-per...) va value la so lan duoc gan nhan IOB do
     * @return List cac phan tu thoa man yeu cau co entropy khong  vuot qua nguong va la nho nhat, 
     * moi phan tu la mang cac String. Mang String co dang: phan tu thu nhat la vi tri cua tu trong van ban,
     * phan tu thu 2 la nhan IOB duoc gan nhieu nhat cho tu do
     */
    // <editor-fold defaultstate="collapsed" desc="calcAndFindS method">
    public List<String[]> calcAndFindS(Map<String, Map<String, Integer>> countMap) {
        return findS(calculateH(countMap));
    }// end calcAndFindS method
    // </editor-fold>

    /**
     * Duoc goi sau khi predict: them dac trung cua cac tu duoc gan nhan vao file dac trung ban dau
     * @param sList List cac phan tu thoa man yeu cau co entropy khong  vuot qua nguong va la nho nhat, 
     * moi phan tu la mang cac String. Mang String co dang: phan tu thu nhat la vi tri cua tu trong van ban,
     * phan tu thu 2 la nhan IOB duoc gan nhieu nhat cho tu do
     * @param predictDoc Doi tuong TaggedDocument cua file can predict
     * @param featurePath Duong dan file feature goc
     */
    // <editor-fold defaultstate="collapsed" desc="processAfterPredict method">
    public void processAfterPredict(List<String[]> sList, TaggedDocument predictDoc, String featurePath) {
        // Gan nhan IOB
        for (String[] strings : sList) {
            // set IOB cho tung tu duoc luu trong sList
            String offset = strings[0];
            String iobLabel = strings[1];
            predictDoc.setIob(iobLabel, offset);
        }// end foreach strings

        File phraseFile = new File("tmp/phrase.tmp");
        File phraseFeatureFile = new File("tmp/phrase.tmp.feature");
        try {
            PrintWriter phraseOut = ReadWriteFile.writeFile(phraseFile, "UTF-8");

            // Lay ra tu duoc gan nhan IOB va 2 tu xung quanh no
            for (String[] strings : sList) {
                String offset = strings[0];
                Sentence phrase = predictDoc.getPhrase(offset, 2);
                phraseOut.print(phrase.toIobString());
                phraseOut.print("\n");
            }// end foreach strings

            phraseOut.close();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }// end try

        // Chuyen sang dang dac trung, luu o file phrase.tmp.feature
        TaggingTrainData.main(new String[]{
                    phraseFile.getAbsolutePath(),
                    phraseFeatureFile.getAbsolutePath(),
                    "model"
                });

        // Noi file dac trung nay vao file dac trung tao ra tu file train dau tien
        try {
            CopyFile.appendFile(featurePath, phraseFeatureFile.getAbsolutePath());
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }// end try
        
        // Del file tam
        phraseFile.delete();
        phraseFeatureFile.delete();

    }// end processAfterPredict method
    // </editor-fold>

    public static void main(String[] args) {
        DOMConfigurator.configure("log-config.xml");
        Main m = new Main();
        String mainTrain = "tmp/mergeDung.txt";
        String mainTest = "tmp/dung138.txt";
        String mainTrainCopied = "tmp/train.txt";
        String mainTestCopied = "tmp/test.txt";
        // File feature goc tao ra tu file train
        String mainTrainFeature = mainTrainCopied + ".feature";
        String bagTrainCopied = "tmp/bagTrain.txt";
        String bagTestCopied = "tmp/bagTest.txt";
        
        /*
         * Tao model va file feature dau tien. File feature: oriTrain + .feature
         */
        logger.info("Tao model va file feature dau tien");
        CopyFile.copyfile(mainTrain, mainTrainCopied);
        CopyFile.copyfile(mainTest, mainTestCopied);
        Crf.train(Crf.MANUAL_MODE, mainTrainCopied, mainTestCopied);

        /*
         * Tao TrainSet: tmp/TrainSet
         */
        logger.info("Tao TrainSet");
        Document tmpDoc = new Document(mainTrain);
        m.createTrainSet(tmpDoc, B, bagSize);

        /*
         * Tao TestSet: tmp/TestSet
         */
        logger.info("Tao TestSet");
        tmpDoc = new Document(mainTest);
        m.createTestSet(tmpDoc, 3);
        tmpDoc = null;

        /*
         * Lap semi
         */
        logger.info("Bat dau lap semi");
        File testSetDir = new File("tmp/TestSet");
        for (File subTest : testSetDir.listFiles()) {
            /*
             * Bat dau thuc hien voi 1 file test trong TestSet
             */
            // Copy file test trong TestSet ra thu muc tmp
            try {
                CopyFile.copyfile(subTest.getAbsolutePath(), bagTestCopied);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }// end try
            String subTestTagged = bagTestCopied + ".tagged";
            logger.info("Thuc hien voi 1 file test");
            // Chuan bi
            logger.info("Tach tu file test");
            
            // Tao file tach tu doi voi file test
            Crf.runVnTagger(bagTestCopied, subTestTagged);
            
            TaggedDocument subTestDoc = new TaggedDocument(subTestTagged);
            Map<String, Map<String, Integer>> countMap = new HashMap<String, Map<String, Integer>>();

            // Bat dau vong lap CRF
            logger.info("Bat dau boostrapping");
            File trainSetDir = new File("tmp/TrainSet");
            for (File trainBagFile : trainSetDir.listFiles()) {
                /*
                 * Lap voi tung bag
                 */
                try {
                    CopyFile.copyfile(trainBagFile.getAbsolutePath(), bagTrainCopied);
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }// end try
                // train + predict, ket qua predict nam trong file bagTestCopied + wseg
                logger.info("Chay CRF voi file " + bagTrainCopied);
                Crf.runCrf(bagTrainCopied, bagTestCopied);
                m.countAppear(countMap, bagTestCopied + ".wseg");
            }// end foreach CRF

            // Lay ra S phan tu co entropy nho nhat lon hon nguong
            List<String[]> sList = m.calcAndFindS(countMap);

            // Them dac trung cua S tu duoc gan nhan nay vao file dac trung ban dau.
            // File dac trung ban dau co dang: mainTrain + .feature
            logger.info("Them dac trung moi vao file dac trung goc");
            m.processAfterPredict(sList, subTestDoc, mainTrainFeature);
            
            //Tao model moi tu file dac trung moi duoc them
            logger.info("Tao model moi tu file dac trung moi them");
            CopyFile.copyfile(mainTrainFeature, "model/train.txt");
            Crf.train(Crf.DEFAULT_MODE);

        }// end foreach testFile
        // Ket thuc lap semi

    }// end main class
}// end Main class

