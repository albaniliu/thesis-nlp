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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author banhbaochay
 */
public class Main {
    
    public void mergeFile() {
        File dir = new File("data/dataToRetrain/entity");
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
        retDoc.print2File("data/Temp/merge.txt");
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
    
    public static void main(String[] args) {
        Main m = new Main();
        m.createIOB2();
        m.taggingTrain();
    }// end main class
    
}// end Main class

