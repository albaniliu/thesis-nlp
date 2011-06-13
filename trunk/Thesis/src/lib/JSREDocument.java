/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Provide methods for concatenating 2 JSRE documents and extract informations from JSRE
 * document
 * @author banhbaochay
 */
public class JSREDocument {

    static Logger logger = Logger.getLogger(JSREDocument.class);
    
    /**
     * Default constructor create an empty JSRE document instance
     */
    public JSREDocument() {
        this.lineList = new ArrayList<JSRELine>();
        this.lastID = new ID();
        this.lineCountText = 0;
        this.path = "";
    }

    /**
     * Constructor create an instance with all information about this document
     * @param file File which wants to import to
     * @throws FileNotFoundException if file does not exist
     * @throws IOException if error while import
     * @throws IllegalArgumentException if file is not JSRE format
     */
    public JSREDocument(File file) throws FileNotFoundException,
            UnsupportedEncodingException, IOException {
        this.path = file.getAbsolutePath();
        this.lineCountText = 0;
        this.lineList = new ArrayList<JSRELine>();
        this.lastID = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line = null;
        while ((line = in.readLine()) != null) {
            if (!line.equals("")) {
                /* not scan the empty line */
                JSRELine jsreLine = new JSRELine(line);
                lineList.add(jsreLine);
                this.lastID = jsreLine.getId();
            }
        }
    }

    /**
     * Constructor create an instance with all information about this document
     * @param path Path to file which wants to import
     * @throws FileNotFoundException 
     * @throws UnsupportedEncodingException 
     * @throws IOException if error while import
     * @throws IllegalArgumentException if file is not JSRE format
     */
    public JSREDocument(String path) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        this(path != null ? new File(path) : null);
    }

    public static boolean checkJSRE(String path) throws Exception {
        BufferedReader in = ReadWriteFile.readFile(path, "UTF-8");
        String line = null;
        /*
         * index cua 1 cau nam trong phan ID cua cau, index phai tang tu 1
         */
        int index = 1;
        while ((line = in.readLine()) != null) {
            if (!line.equals("")) {
                if (JSRELine.checkJSREFormat(line)) {
                    /*
                     * Dinh dang JSRE cua tung cau da dung
                     */
                    int count = Integer.parseInt(line.split("\t")[1].split("-")[1]);
                    if (count != index) {
                        logger.error("Thu tu count cua dong thu " + index + " khong dung");
                        return false;
                    }// end if count != index
                } else {
                    logger.error("Dong thu " + index + " cua file khong dung dinh dang JSRE");
                    return false;
                }// end if JSRELine.checkJSREFormat(line)
                index++;
            }// end if !line.equals("")
        }// end while (line = in.readLine()) != null
        return true;
    }
    
    /**
     * Ghep document JSRE vao document JSRE co truoc
     * @param tailDoc JSREDocument type
     * @return Kieu JSREDocument da duoc ghep
     */
    public JSREDocument concat(JSREDocument tailDoc) {
        int lineCount = length();
        int oldLineCountText = getLineCount();
        List<JSRELine> lineListTailDoc = tailDoc.getLineList();
        for (JSRELine lineTail : lineListTailDoc) {
            int newCount = lineTail.getCount() + lineCount;
            int newLineNumber = lineTail.getLineNumber() + oldLineCountText;
            ID newId = new ID(newLineNumber, newCount);
            lineTail.setId(newId);
            lineList.add(lineTail);
            lastID.replaceBy(newId);
        }
        setLineCountText(oldLineCountText + tailDoc.getLineCount());

        return this;
    }
    
    /**
     * Chia document thanh 2 phan, phan dau se co k phan tu
     * @param k
     * @return 
     */
    public JSREDocument[] split(int k) {
        
    }// end split method

    /**
     * Check if JSRE doc is empty
     * @return <code>true</code> if it is empty doc, <code>false</code> if not
     */
    public boolean isEmpty() {
        return (lineList == null) ? true : lineList.isEmpty();
    }

    /**
     * Print document with format to console:
     * Doc has <code>number</code> line in it and <code>number</code> line count in text
     * ...
     */
    public void print() {
        System.out.printf("Doc has %d line in it and %d line count in text\n", length(), lineCountText);
        for (JSRELine line : lineList) {
            System.out.println(line.getWholeLine());
        }
    }

    /**
     * Get number of lines in jsre document. It equals to count element of the last ID
     * @return number of lines in document
     */
    public int length() {
        return lastID.getCount();
    }

    /**
     * @return path of document
     */
    @Override
    public String toString() {
        return this.path;
    }
    private List<JSRELine> lineList;
    private String path;
    private ID lastID;
    private int lineCountText;

    /**
     * @return the lastID
     */
    public ID getLastID() {
        return lastID;
    }

    /**
     * @return the lineList of doc
     */
    public List<JSRELine> getLineList() {
        return lineList;
    }

    /**
     * @return number of lines in text document
     */
    public int getLineCount() {
        return lineCountText;
    }

    /**
     * @param lineCountText set number of lines in text document
     */
    public void setLineCountText(int lineCountText) {
        this.lineCountText = lineCountText;
    }

    public static void main(String[] args) throws Exception {
        DOMConfigurator.configure("log4j.xml");
        System.out.println(checkJSRE("/home/banhbaochay/live_in.test"));
    }
}
