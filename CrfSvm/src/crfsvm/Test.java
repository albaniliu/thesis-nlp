/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        File f = new File("tmp/t.txt");
        PrintWriter o = ReadWriteFile.writeFile(f, "UTF-8");
        o.print("\n\r");
        o.print("Hello world");
        o.close();
    }// end main class
}// end Test class

