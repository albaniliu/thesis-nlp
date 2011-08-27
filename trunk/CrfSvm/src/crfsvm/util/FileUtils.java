/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author banhbaochay
 */
public class FileUtils {
    public static void removeTag(String filePath) {
        removeTag(new File(filePath));
    }// end removeTag method
    
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
}// end FileUtils class

