/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

    public Document() {
        lineList = new ArrayList<String>();
    }

    public Document(File file) {
        lineList = new ArrayList<String>();
        try {
            BufferedReader in = ReadWriteFile.readFile(file, "UTF-8");
            String line = null;
            int i = 0;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    lineList.add(line);
                }// end if line.equals("")
            }
        } catch (Exception ex) {
            Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
     * @param k so cau trong 1 bagging
     * @return list cac bagging
     */
    public List createBagging(int B, int k) {
        List list = new ArrayList();
        int n = size();
        for (int i = 0; i < B; i++) {
            Document doc = new Document();
            for (int j = 0; j < k; j++) {
                Random random = new Random();
                int index = random.nextInt(n);
                doc.add(lineList.get(index));
            }// end for j
            list.add(doc);
        }// end for i
        return list;
    }// end createBagging method
    
    private List<String> lineList;
    
    public static void main(String[] args) {
        File f = new File("data/dataToRetrain/entity/Dung1.txt");
        Document doc = new Document(f);
        List list = doc.createBagging(2, 30);
        
    }// end main class
}// end Document class

