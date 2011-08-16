/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.JVnRecognizer;
import crfsvm.crf.een_phuong.tokenizeVietnamese;
import crfsvm.options.OptionCrf;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author banhbaochay
 */
public class Crf {
    
    public void init() {
        /*
         * Set option cho CRF
         */
        OptionCrf.inputCrfFile = "tmp/demo.txt";
        inputVnTagger = "tmp/input.txt";
        taggedFile = "tmp/tagged.txt";
        OptionCrf.inputJVnFile = taggedFile;
        OptionCrf.modelDir = "model";
    }// end init method
    
    
    /**
     * Sau khi chay vntagger, file van ban da duoc tach tu la file tagged.txt
     */
    public void runVnTagger() {
        try {
            CopyFile.copyfile(OptionCrf.inputCrfFile, inputVnTagger);
            tokenizeVietnamese.token();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Crf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Crf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Crf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end runVnTagger method
    
    
    public void predict() {
        JVnRecognizer.main(new String[] {
            "-modeldir",
            OptionCrf.modelDir,
            "-inputfile",
            OptionCrf.inputJVnFile
        });
    }// end predict method
    
    /**
     * Run chuong trinh crf.exe voi tham so mac dinh:<br/>
     * <ul>
     *   <li>Chuong trinh crf.exe nam trong thu muc model</li>
     *   <li>modelDir la thu muc model trong project</li>
     *   <li>optionFile la file option.txt trong thu muc model</li>
     * </ul>
     */
    public static void runCRF() {
        runCRF("model/crf.exe", "model", "option.txt");
    }// end runCRF method
    
    /**
     * Run chuong trinh crf.exe voi tham so modelDir va optionFile
     * @param program duong dan den chuong trinh crf.exe
     * @param modelDir thu muc model
     * @param optionFile duong dan den option file
     */
    public static void runCRF(String program, String modelDir, String optionFile) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(new String[]{
                        "wine",
                        program,
                        "-modeldir",
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
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end runCRF method
    
    /**
     * Run chuong trinh crf.exe trong thu muc model voi tham so modelDir va optionFile
     * @param modelDir thu muc model
     * @param optionFile duong dan den option file
     */
    public static void runCRF(String modelDir, String optionFile) {
        runCRF("model/crf.exe", modelDir, optionFile);
    }// end runCRF method
    
    public static void main(String[] args) {
        Crf crf = new Crf();
        crf.init();
    }// end main class
    
    private String inputVnTagger;
    private String taggedFile;
}// end Crf class

