/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.IOB2Converter;
import crfsvm.crf.een_phuong.TaggingTrainData;
import crfsvm.util.Document;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Main {

    static Logger logger = Logger.getLogger(Main.class);

    /**
     * Merge tat ca cac file .txt trong thu muc dirPath thanh file mergePath
     * @param dirPath Duong dan toi thu muc chua cac file van ban muon merge
     * @param mergePath Duong dan toi file output sau khi merge
     */
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

    /**
     * Merge tat ca cac file duoc loc ra boi nameFilter trong dirPath thanh file mergePath
     * @param dirPath Duong dan toi thu muc chua cac file van ban muon merge
     * @param mergePath Duong dan toi file output sau khi merge
     * @param nameFilter Bo loc ten cua file trong thu muc dirPath
     */
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

    public void createIOB2() {
        String[] args = new String[2];
        args[0] = "data/Temp/merge.txt";
        args[1] = "data/Temp/iob2.txt";
        IOB2Converter.main(args);
    }// end createIOB2 method

    public void taggingTrain() {
        String[] args = new String[3];
        args[0] = "data/Temp/iob2.txt";
        args[1] = "data/Temp/iob2-tagged.txt";
        args[2] = "model";
        TaggingTrainData.main(args);
    }// end taggingTrain method

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
     * Chay CRF voi file train va file test, mac dinh phai co file crf.exe va option.txt trong thu muc model.
     * Sau khi chay trong thu muc model se xuat hien file model.txt duoc tao tu file train
     * @param trainPath
     * @param testPath 
     */
    public void runCRF(String trainPath, String testPath) {
        
        try {
            /*
             * Copy file train va file test vao thu muc model, doi ten thanh train.txt va test.txt
             */
            CopyFile.copyfile(trainPath, "tmp/train.txt");
            CopyFile.copyfile(testPath, "tmp/test.txt");
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }// end try catch
        
        /*
         * Run CRF
         */
        Crf.runCRF();
    }// end runCRF method

    public static void main(String[] args) {
        DOMConfigurator.configure("log-config.xml");
        Main m = new Main();
        int loop = 3;
        String trainPath = "tmp/mergeDung.txt";
        String testPath = "";
        /*
         * So luong bagging
         */
        int B = 5;
        /*
         * So phan lop
         */
        int C = 2;
        /*
         * batch size
         */
        int S = 80;
        /*
         * nguong cho entropy
         */
        double thresholdH = 0.277;
        /*
         * Number example in one bag
         */
        int bagSize = 120;

        Document doc = new Document(trainPath);
        /*
         * Bat dau lap CRF
         * Moi khi muon chay CRF thi can copy file train va file test vao thu muc model
         * va doi ten thanh train.txt, test.txt
         * 
         */
        for (int i = 0; i < loop; i++) {
        }// end for i
    }// end main class
}// end Main class

