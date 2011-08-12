/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.IOB2Converter;
import crfsvm.crf.een_phuong.TaggingTrainData;
import crfsvm.util.Document;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 *
 * @author banhbaochay
 */
public class Main {
    
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
    
    public void createTrainSet(Document doc, int B, int k) {
        List docList = doc.createBagging(B, k);
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
    }// end createTrainSet method
    
    public static void main(String[] args) {
        Main m = new Main();
        String trainPath = "tmp/mergeDung.txt";
        String testPath = "";
        /*
         * So luong bagging
         */
        int B = 2;
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
        m.createTrainSet(doc, B, S);
    }// end main class
    
}// end Main class

