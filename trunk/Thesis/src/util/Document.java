/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author banhbaochay
 */
public class Document {

    public static final String DEFAULT_FILE_PATH = "tmp/doc.txt";

    /**
     * Constructor empty
     */
    public Document() {
        lineList = new ArrayList<String>();
        filePath = DEFAULT_FILE_PATH;
        fileName = DEFAULT_FILE_PATH.substring(DEFAULT_FILE_PATH.indexOf("/") + 1);
    }

    /**
     * Tao Document tu duong dan 1 file. Doc file bang UTF-8
     * @param filePath 
     */
    public Document(String filePath) {
        this(new File(filePath));
    }

    /**
     * Tao Document tu 1 file. Doc file bang UTF-8
     * @param file 
     */
    public Document(File file) {
        filePath = file.getAbsolutePath();
        fileName = file.getName();
        lineList = new ArrayList<String>();
        try {
            BufferedReader in = ReadWriteFile.readFile(file, "UTF-8");
            String line = null;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    lineList.add(line);
                }// end if !line.equals("")
            }
        } catch (Exception ex) {
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected Document clone() {
        Document doc = new Document();
        doc.setFilePath(getFilePath());
        doc.lineList = new ArrayList<String>(this.lineList);
        return doc;
    }

    /**
     * Noi cac document vao 1 document co truoc. 
     * Document ban dau bi thay doi
     * @param docs
     */
    public void append(Document... docs) {
        if (docs.length > 0) {
            for (Document document : docs) {
                for (String line : document.lineList) {
                    add(line);
                }// end foreach line
            }// end foreach document
        }// end if docs.length > 0
    }// end append method

    /**
     * In document ra file. Cac dong trong file phan tach boi 1 dong trang
     * @param file
     * @throws FileNotFoundException  
     */
    public void print2File(File file) throws FileNotFoundException {

        PrintWriter out = null;
        try {
            out = ReadWriteFile.writeFile(file, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        }
        for (String line : lineList) {
            out.print(line);
            out.print("\n\r");
        }// end foreach line
        out.close();
    }// end print2File method

    /**
     * In document ra file. Cac dong trong file phan tach boi 1 dong trang
     * @param filePath duong dan den file ghi
     * @throws FileNotFoundException  
     */
    public void print2File(String filePath) throws FileNotFoundException {
        print2File(new File(filePath));
    }// end print2File method

    /**
     * In document ra file co file name da duoc set trong document
     * @throws FileNotFoundException 
     */
    public void print2File() throws FileNotFoundException {
        print2File(filePath);
    }// end print2File method

    /**
     * 
     * @return So dong trong van ban
     */
    public int size() {
        return lineList.size();
    }// end size method

    public void add(String line) {
        if (!line.equals("")) {
            lineList.add(line);
        }// end if line.equals("")
    }// end add method

    /**
     * Tao Bagging tu 1 document
     * @param B so bagging
     * @param bagSize so cau trong 1 bagging
     * @return list cac bagging
     */
    public List createBagging(int B, int bagSize) {
        List list = new ArrayList();
        int n = size();
        for (int i = 0; i < B; i++) {
            Document doc = new Document();
            for (int j = 0; j < bagSize; j++) {
                Random random = new Random();
                int index = random.nextInt(n);
                doc.add(lineList.get(index));
            }// end for j
            doc.setFileName("bag" + i + ".txt");
            list.add(doc);
        }// end for i
        return list;
    }// end createBagging method

    /**
     * Chia 1 document thanh number phan co so luong cau bang nhau
     * @param number
     * @return list cac doc da duoc chia
     */
    public List split(int number) {
        List list = new ArrayList();
        int n = size();
        // so luong cau trong moi phan
        int k = n / number;
        int start = 0;
        int end = k;
        for (int i = 0; i < number; i++) {
            Document doc = new Document();
            for (int j = start; j < end; j++) {
                doc.add(lineList.get(j));
                doc.setFileName("test" + i + ".txt");
            }// end for j
            start = end;
            end += k;
            list.add(doc);
        }// end for i
        return list;
    }// end split method

    @Override
    public String toString() {
        return fileName;
    }
    private String filePath;
    private String fileName;
    private List<String> lineList;

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}// end Document class

