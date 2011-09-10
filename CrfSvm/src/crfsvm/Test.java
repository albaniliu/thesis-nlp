/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Crf.calcFScore("tmp/trainDung.txt", "tmp/testDung.txt");
    }// end main class
}// end Test class

