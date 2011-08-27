/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.util.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.mergeFile("data/dataToRetrain/EntityDung", "tmp/trainDung.txt", new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches("dung10[0-9]\\.txt");
            }
        });
        m.mergeFile("data/dataToRetrain/EntityDung", "tmp/testDung.txt", new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches("dung11[0-9]\\.txt");
            }
        });
    }// end main class
}// end Test class

