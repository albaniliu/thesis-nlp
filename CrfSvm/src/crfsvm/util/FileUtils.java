/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import crfsvm.crf.een_phuong.IOB2Converter;
import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class FileUtils {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FileUtils.class);

    static {
        DOMConfigurator.configure("log-config.xml");
    }

    public static void removeTag(String filePath) {
        removeTag(new File(filePath));
    }// end removeTag method

    /**
     * Bo tat ca cac nhan entity trong file
     * @param file 
     */
    public static void removeTag(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = ReadWriteFile.readFile(file, "UTF-8");
            String line = null;
            while ((line = in.readLine()) != null) {
                line = line.replaceAll(" </[^>]*>", "");
                line = line.replaceAll("<[^>]*> ", "");
                sb.append(line);
                sb.append("\n");
            }// end while
            in.close();
        } catch (Exception ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }// end try
        try {
            PrintWriter out = ReadWriteFile.writeFile(file, "UTF-8");
            out.print(sb);
            out.close();
        } catch (Exception ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }// end try
    }// end removeTag method

    /**
     * Tao tap train trong thu muc tmp/TrainSet tu 1 document
     * @param doc
     * @param B so luong bag
     * @param bagSize so luong cau trong 1 bag
     */
    // <editor-fold defaultstate="collapsed" desc="createTrainSet method">
    public static void createTrainSet(Document doc, int B, int bagSize) {
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
    public static void createTestSet(Document doc, int number) {
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
     * Xoa file hoac thu muc, chi dung trong linux
     * @param file 
     */
    public static void removeFile(String file) {
        try {
            Process p = Runtime.getRuntime().exec("rm -rf " + file);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end removeDirectory method

    /**
     * Convert file thanh dang IOB, khong phu thuoc vao co nhung nhan nao
     * @param inputFilePath
     * @param outputFilePath 
     */
    public static void convert2IOB(String inputFilePath, String outputFilePath) {
        IOB2Converter.main(new String[]{
                    inputFilePath,
                    outputFilePath
                });
    }// end convert2IOB method
    
    /**
     * Convert file thanh dang IOB, luu trong file trung ten voi file dau vao va co duoi .iob.
     * Khong phu thuoc co nhung nhan nao
     * @param inputFilePath 
     */
    public static void convert2IOB(String inputFilePath) {
        convert2IOB(inputFilePath, inputFilePath + ".iob");
    }// end convert2IOB method

    public static void main(String[] args) {
        removeFile("tmp/TestSet");
    }// end main class
}// end FileUtils class

