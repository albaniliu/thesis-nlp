/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.IOB2Converter;
import crfsvm.crf.een_phuong.TaggingTrainData;
import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import crfsvm.util.Document;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Main {

    static Logger logger = Logger.getLogger(Main.class);
    static final String PREDICT_FILE = "tmp/tagged.txt.wseg";
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
    static int S = 80;
    /**
     * nguong cho entropy
     */
    static double thresholdH = 0.277;
    /**
     * Number example in one bag
     */
    static int bagSize = 120;
    
    static final int B_PER = 0;
    static final int I_PER = 1;
    static final int B_LOC = 2;
    static final int I_LOC = 3;
    static final int B_ORG = 4;
    static final int I_ORG = 5;
    static final int B_POS = 6;
    static final int I_POS = 7;
    static final int B_JOB = 8;
    static final int I_JOB = 9;
    static final int B_DATE = 10;
    static final int I_DATE = 11;
    static final int OTHER = 12;

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
        Crf.train();
    }// end train method

    private int[][] initCount() {
        LinkedList<String> list = new LinkedList<String>();
        try {
            BufferedReader in = ReadWriteFile.readFile(IOB_FILE, "UTF-8");
            String line = null;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    list.add(line);
                }// end if !line.equals("")
            }// end while
        } catch (UnsupportedEncodingException ex) {
            logger.debug(ex.getMessage());
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }// end try catch

        int[][] count = new int[C][list.size()];
        return count;
    }// end initCount method
    
    private int[][] countAppear() {

        LinkedList<String> list = new LinkedList<String>();
        try {
            BufferedReader in = ReadWriteFile.readFile(IOB_FILE, "UTF-8");
            String line = null;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    list.add(line);
                }// end if !line.equals("")
            }// end while
        } catch (UnsupportedEncodingException ex) {
            logger.debug(ex.getMessage());
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }// end try catch

        int[][] count = new int[C][list.size()];
        int i = 0;
        for (String line : list) {
            String type = line.split("\t")[1];
            if (type.equalsIgnoreCase("O")) {
                count[OTHER][i]++;
            } else if (type.equalsIgnoreCase("B-per")) {
                count[B_PER][i]++;
            } else if (type.equalsIgnoreCase("I-per")) {
                count[I_PER][i]++;
            } else if (type.equalsIgnoreCase("B-loc")) {
                count[B_LOC][i]++;
            } else if (type.equalsIgnoreCase("I-loc")) {
                count[I_LOC][i]++;
            } else if (type.equalsIgnoreCase("B-org")) {
                count[B_ORG][i]++;
            } else if (type.equalsIgnoreCase("I-org")) {
                count[I_ORG][i]++;
            } else if (type.equalsIgnoreCase("B-pos")) {
                count[B_POS][i]++;
            } else if (type.equalsIgnoreCase("I-pos")) {
                count[I_POS][i]++;
            } else if (type.equalsIgnoreCase("B-job")) {
                count[B_JOB][i]++;
            } else if (type.equalsIgnoreCase("I-job")) {
                count[I_JOB][i]++;
            } else if (type.equalsIgnoreCase("B-date")) {
                count[B_DATE][i]++;
            } else if (type.equalsIgnoreCase("I-date")) {
                count[I_DATE][i]++;
            }
            i++;
        }// end foreach line

        return count;
    }// end countAppear method

    public static void main(String[] args) {
        DOMConfigurator.configure("log-config.xml");
        Main m = new Main();
        int loop = 3;
        String trainPath = "tmp/mergeDung.txt";
        String testPath = "";


        /*
         * Bat dau lap CRF
         * 
         */
        Crf.runVnTagger("tmp/demo_org.txt");
//        Crf.predict();
//        IOB2Converter.convertAllLine(PREDICT_FILE, IOB_FILE);
//        int[][] count = m.initCount();
//        m.countAppear();
    }// end main class
}// end Main class

