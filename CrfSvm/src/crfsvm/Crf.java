/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.IOB2Converter;
import crfsvm.crf.een_phuong.JVnRecognizer;
import crfsvm.crf.een_phuong.tokenizeVietnamese;
import crfsvm.options.OptionCrf;
import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
     * 
     */
    public void train() {
        
    }// end train method
    
    public static void main(String[] args) {
        Crf crf = new Crf();
        crf.init();
    }// end main class
    
    private String inputVnTagger;
    private String taggedFile;
}// end Crf class

