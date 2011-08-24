/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Case
 */
public class CopyFile {

    /**
     * Copy tu file srFile vao file dtFile
     * @param srFile
     * @param dtFile 
     */
    public static void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied:" + srFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// end copyFile method
    
    /**
     * Copy tu file srFile vao dtFile, sau khi copy thi xoa file srFile neu chon tham so thu 3 la true
     * @param srFile
     * @param dtFile
     * @param deleteAfterCopy 
     */
    public static void copyfile(String srFile, String dtFile, boolean deleteAfterCopy) {
        copyfile(srFile, dtFile);
        if (deleteAfterCopy) {
            File f = new File(srFile);
            f.delete();
        }
    }// end copyfile method
    
    /**
     * Noi 2 file
     * @param targetFile Duong dan file bi noi
     * @param appendFile Duong dan file noi
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void appendFile(String targetFile, String appendFile) 
            throws FileNotFoundException, IOException {
        File tFile = new File(targetFile);
        File aFile = new File(appendFile);
        OutputStream out = new FileOutputStream(tFile, true);
        InputStream in = new FileInputStream(aFile);
        
        byte[] buf = new byte[1024];
        // Ghi 1 dong trang
        buf[0] = 10;
        out.write(buf, 0, 1);
        // Bat dau noi file
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }// end while
        in.close();
        out.close();
    }// end appendFile method
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        appendFile("tmp/1.txt", "tmp/2.txt");
    }// end main class
}
