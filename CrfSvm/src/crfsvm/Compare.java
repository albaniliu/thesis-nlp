/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.Offset;
import crfsvm.crf.een_phuong.PrfCalculator;
import crfsvm.crf.een_phuong.TaggedDocument;
import crfsvm.util.Count;
import crfsvm.util.Document;
import crfsvm.util.FileUtils;
import crfsvm.util.MapUtils;
import crfsvm.util.MathUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Compare {

    static Logger logger = Logger.getLogger(Compare.class);

    /**
     * 
     * @param trainPath
     * @param testPath
     */
    public void runBagging(String trainPath, String testPath) {
        // Ban sao file train
        String mainTrainCopied = "tmp/train.txt";
        // Ban sao file test
        String mainTestCopied = "tmp/test.txt";
        // File bag train
        String bagTrainCopied = "tmp/bagTrain.txt";
        // File de predict, chi duoc tach tu, chua duoc gan nhan
        String mainTestNoTagLabel = "tmp/test-notag.txt";

        int B = 5;

        int bagSize = 180;

        double threshold = MathUtils.calcEntropy(B, 3, 1, 1);

        // Lap danh sach cac file ban dau trong thu muc tmp
        List<File> oriFiles = new ArrayList<File>();
        oriFiles.addAll(Arrays.asList(new File("tmp").listFiles()));

        /*
         * Tao model va file feature dau tien. File feature: oriTrain + .feature
         */
        logger.info("Tao model va file feature dau tien, dong thoi tinh toan P-R-F");
        CopyFile.copyfile(trainPath, mainTrainCopied);
        CopyFile.copyfile(testPath, mainTestCopied);
        TaggedDocument testDoc = new TaggedDocument(mainTestCopied);
        System.out.println("Vi tri label test:" + testDoc.getIobPosMap());
        Crf.calcFScore(mainTrainCopied, mainTestCopied);
//        CopyFile.copyfile(mainTestCopied, "tmp/t.txt");
//        FileUtils.removeTag("tmp/t.txt");
//        Crf.runCrf(mainTrainCopied, "tmp/t.txt");
//        TaggedDocument d = new TaggedDocument("tmp/t.txt.wseg");
//        System.out.println(d.getIobPosMap());
//        System.exit(0);
        
        /*
         * Tao TrainSet: tmp/TrainSet
         */
        logger.info("Tao TrainSet");
        Document tmpDoc = new Document(mainTrainCopied);
        FileUtils.createTrainSet(tmpDoc, B, bagSize);
        tmpDoc = null;

        // Tao file test chua tag label de gan nhan
        CopyFile.copyfile(mainTestCopied, mainTestNoTagLabel);
        FileUtils.removeTag(mainTestNoTagLabel);

//	Map<String, Map<String, Integer>> countMap = new HashMap<String, Map<String, Integer>>();
        Map<Offset, Map<String, Integer>> countMap = new TreeMap<Offset, Map<String, Integer>>();

        // Bat dau vong lap CRF
        logger.info("Bat dau boostrapping");
        File trainSetDir = new File("tmp/TrainSet");
        for (File trainBagFile : trainSetDir.listFiles()) {
            /*
             * Lap voi tung bag
             */
            CopyFile.copyfile(trainBagFile.getAbsolutePath(), bagTrainCopied);
            // train + predict, ket qua predict nam trong file mainTestCopied + wseg
            logger.info("Chay CRF voi file " + bagTrainCopied);
            Crf.runCrf(bagTrainCopied, mainTestNoTagLabel);
            TaggedDocument testTaggedDoc = new TaggedDocument(mainTestNoTagLabel + ".wseg");
            System.out.println("Vi tri label bag:" + testTaggedDoc.getLabelPosMap());
//            Count.countAppearLabel(countMap, mainTestNoTagLabel + ".wseg");
            Count.countAppearIOB(countMap, mainTestNoTagLabel + ".wseg");
        }// end foreach CRF

        // Lay ra cac phan tu co entropy k vuot qua nguong
        Map filterMap = MapUtils.filterByEntropy(MathUtils.calcEntropy(countMap, B), threshold);
        System.out.println("So luong thuc the tim duoc: " + filterMap.size());
        System.out.println("Vi tri thuc the tim duoc: " + filterMap);

        TaggedDocument preDoc = testDoc.createTaggedDoc(filterMap, TaggedDocument.MapType.IOBMAP);

        PrfCalculator prfCalculator = new PrfCalculator(testDoc, preDoc, PrfCalculator.CalcMode.IOBMODE);
        prfCalculator.calcWithIob();
        System.out.println(prfCalculator);


        // Xoa cac file moi tao, chi dung trong linux
        for (File file : new File("tmp").listFiles()) {
            if (!oriFiles.contains(file)) {
                FileUtils.removeFile(file.getAbsolutePath());
            }
        }// end foreach file

    }// end runBagging method

    /**
     * 
     * @param trainPath
     * @param testPath  
     */
    public void runConfidenScore(String trainPath, String testPath) {
        // Ban sao file train
        String mainTrainCopied = "tmp/train.txt";
        // Ban sao file test
        String mainTestCopied = "tmp/test.txt";
        // File feature goc tao ra tu file train
        String mainTrainFeature = mainTrainCopied + ".feature";
        // File bag train
        String bagTrainCopied = "tmp/bagTrain.txt";
        // File de predict, chi duoc tach tu, chua duoc gan nhan
        String mainTestNoTag = "tmp/test-notag.txt";

        CopyFile.copyfile(testPath, mainTestCopied);
        FileUtils.removeTag(mainTestCopied);

        // Tao file co nhan voi confidence score: giong ten 
        Crf.predict(Crf.DEFAULT_MODEL_DIR, mainTestCopied, true);

//        FileUtils.convert2IOB(mainTestCopied);


    }// end runConfidenScore method
    
    private void runOnce(String trainPath, String testPath) {
        Crf.calcFScore(trainPath, testPath);
    }// end runOnce method

    public static void main(String[] args) {
        DOMConfigurator.configure("log-config.xml");
        FileUtils.removeFile("tmp/TrainSet");
        Compare c = new Compare();
        c.runBagging("tmp/trainThien200doivoibackup.txt", "tmp/testThien1888doivoibackup.txt");
//        c.runOnce("tmp/trainThien.txt", "tmp/testThien.txt");
//        c.runConfidenScore("tmp/trainThien1000.txt", "tmp/testThien958.txt");
    }// end main class
}// end Compare class

