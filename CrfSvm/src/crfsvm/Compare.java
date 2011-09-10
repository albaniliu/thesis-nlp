/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.JVnRecognizer;
import crfsvm.crf.een_phuong.TaggedDocument;
import crfsvm.util.Document;
import crfsvm.util.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Compare {

    static Logger logger = Logger.getLogger(Compare.class);

    static {
        DOMConfigurator.configure("log-config.xml");
    }

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
        // File feature goc tao ra tu file train
        String mainTrainFeature = mainTrainCopied + ".feature";
        // File bag train
        String bagTrainCopied = "tmp/bagTrain.txt";
        // File de predict, chi duoc tach tu, chua duoc gan nhan
        String mainTestNoTagLabel = "tmp/test-notag.txt";
        Main m = new Main();

        int B = 5;
        int bagSize = 60;


        /*
         * Tao model va file feature dau tien. File feature: oriTrain + .feature
         */
        logger.info("Tao model va file feature dau tien, dong thoi tinh toan P-R-F");
        CopyFile.copyfile(trainPath, mainTrainCopied);
        CopyFile.copyfile(testPath, mainTestCopied);
        Crf.calcFScore(mainTrainCopied, mainTestCopied);

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

        TaggedDocument subTestDoc = new TaggedDocument(mainTestCopied);
        Map<String, Map<String, Integer>> countMap = new HashMap<String, Map<String, Integer>>();

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
            m.countAppear(countMap, mainTestNoTagLabel + ".wseg");
        }// end foreach CRF

        // Lay ra S phan tu co entropy nho nhat lon hon nguong
        List<String[]> sList = m.calcAndFindS(countMap);

        // Them dac trung cua S tu duoc gan nhan nay vao file dac trung ban dau.
        // File dac trung ban dau co dang: mainTrain + .feature
        logger.info("Them dac trung moi vao file dac trung goc");
        m.processAfterPredict(sList, subTestDoc, mainTrainFeature);

        //Tao model moi tu file dac trung moi duoc them + tinh toan P-R-F
        logger.info("Tao model moi tu file dac trung moi them");
        logger.info("Tinh toan P-R-F voi model moi");
        Crf.calcFScore(mainTrainFeature, mainTestCopied);

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
        
        
        
        
    }// end runConfidenScore method
    
    public static void main(String[] args) {
        Compare c = new Compare();
        c.runBagging("tmp/trainDung.txt", "tmp/testDung.txt");
    }// end main class
    
}// end Compare class

