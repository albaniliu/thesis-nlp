/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.JVnRecognizer;
import crfsvm.crf.een_phuong.tokenizeVietnamese;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Crf {
    
    static Logger logger = Logger.getLogger(Crf.class);
    static {
        DOMConfigurator.configure("log-config.xml");
    }

    /**
     * Default model dir: thu muc model trong project
     */
    public static final String MODEL_DIR = "model";

    /**
     * Sau khi chay vntagger, file van ban da duoc tach tu la file tagged.txt
     * @param inputFile 
     */
    public static void runVnTagger(String inputFile) {
        try {
            CopyFile.copyfile(inputFile, "tmp/input.txt");
            tokenizeVietnamese.token();
            /*
             * Ket qua sau khi tag la file tmp/tagged.txt
             */
            logger.info("Tag file " + inputFile + " successfull");
            logger.info("File tagged: tmp/tagged.txt");
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            logger.debug(ex.getMessage());
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }
    }// end runVnTagger method

    /**
     * Predict file inputFile, ket qua luu trong file tagged.txt.wseg.
     * Model dir mac dinh la thu muc model trong project root
     */
    public static void predict() {
        predict(MODEL_DIR);
    }// end predict method

    /**
     *  Predict file inputFile, ket qua luu trong file tagged.txt.wseg
     * @param modelDir
     */
    public static void predict(String modelDir) {
        
        File f = new File("tmp/tagged.txt");
        if (!f.exists()) {
            logger.debug("No tagged file to predict");
            return;
        }// end if f.exists()
        JVnRecognizer.main(new String[]{
                    "-modeldir",
                    modelDir,
                    "-inputfile",
                    "tmp/tagged.txt"
                });
        logger.info("File after predict: tagged.txt.wseg");
    }// end predict method

    /**
     * Run chuong trinh crf.exe voi tham so mac dinh:<br/>
     * <ul>
     *   <li>Chuong trinh crf.exe nam trong thu muc model</li>
     *   <li>modelDir la thu muc model trong project</li>
     *   <li>optionFile la file option.txt trong thu muc model</li>
     * </ul>
     */
    public static void train() {
        train("model/crf.exe", "model", "option.txt");
    }// end train method

    /**
     * Run chuong trinh crf.exe voi tham so modelDir va optionFile
     * @param program duong dan den chuong trinh crf.exe
     * @param modelDir thu muc model
     * @param optionFile duong dan den option file
     */
    public static void train(String program, String modelDir, String optionFile) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(new String[]{
                        "wine",
                        program,
                        "-all",
                        "-d",
                        modelDir,
                        "-o",
                        optionFile
                    });
            InputStream inputStream = proc.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(reader);
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }
    }// end train method

    /**
     * Run chuong trinh crf.exe trong thu muc model voi tham so modelDir va optionFile
     * @param modelDir thu muc model
     * @param optionFile duong dan den option file
     */
    public static void train(String modelDir, String optionFile) {
        train("model/crf.exe", modelDir, optionFile);
    }// end train method

    public static void main(String[] args) {
        
    }// end main class
}// end Crf class

