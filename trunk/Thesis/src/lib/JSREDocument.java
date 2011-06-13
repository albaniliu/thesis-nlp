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

/**
 * Provide methods for concatenating 2 JSRE documents and extract informations from JSRE
 * document
 * @author banhbaochay
 */
public class JSREDocument {

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
     * @throws IOException if error while import
     * @throws IllegalArgumentException if file is not JSRE format
     */
    public JSREDocument(String path) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        this(path != null ? new File(path) : null);
    }

    /**
     * Concatenates another doc to this doc
     * @param tailDoc JSREDocument type
     * @return the concatenated doc with <code>tailDoc</code>
     */
    public JSREDocument concat(JSREDocument tailDoc) {
        int lineCount = length();
        int oldLineCountText = getLineCountText();
        List<JSRELine> lineListTailDoc = tailDoc.getLineList();
        for (JSRELine lineTail : lineListTailDoc) {
            int newCount = lineTail.getCount() + lineCount;
            int newLineNumber = lineTail.getLineNumber() + oldLineCountText;
            ID newId = new ID(newLineNumber, newCount);
            lineTail.setId(newId);
            lineList.add(lineTail);
            lastID.replaceBy(newId);
        }
        setLineCountText(oldLineCountText + tailDoc.getLineCountText());

        return this;
    }

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
    public int getLineCountText() {
        return lineCountText;
    }

    /**
     * @param lineCountText set number of lines in text document
     */
    public void setLineCountText(int lineCountText) {
        this.lineCountText = lineCountText;
    }

    public static void main(String[] args) throws Exception {
        JSREDocument doc1 = new JSREDocument("/home/banhbaochay/Temp/1.txt");
        JSREDocument doc2 = new JSREDocument("/home/banhbaochay/Temp/2.txt");
        doc1.setLineCountText(28);
        doc2.setLineCountText(30);
        ID tmpId = doc2.getLastID();
        System.out.println("Document 1:");
        doc1.print();
        System.out.println("Document 2:");
        doc2.print();
        doc1.concat(doc2);
        System.out.println("Document after concat:");
        doc1.print();
    }
}
