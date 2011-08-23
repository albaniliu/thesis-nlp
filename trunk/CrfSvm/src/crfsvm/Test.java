/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Main m = new Main();
        CopyFile.copyfile("tmp/TrainSet/bag0.txt", "tmp/train.txt");
        CopyFile.copyfile("tmp/TestSet/test0.txt", "tmp/test.txt");
        Crf.runCrf("tmp/train.txt", "tmp/test.txt");
    }// end main class
}// end Test class

