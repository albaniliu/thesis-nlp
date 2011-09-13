/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.IOB2Converter;
import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws Exception {
        CopyFile.copyfile("data/dataToRetrain/EntityDung/dung100.txt", "tmp/dung100.txt");
        IOB2Converter.main(new String[] {
            "tmp/dung100.txt",
            "tmp/dung100.iob"
        });
    }// end main class
}// end Test class

