/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.Offset;
import crfsvm.crf.een_phuong.TaggedDocument;
import crfsvm.util.FileUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws Exception {
        DOMConfigurator.configure("log-config.xml");
	CopyFile.copyfile("tmp/test.txt", "tmp/test0.txt");
	FileUtils.removeTag("tmp/test0.txt");
	TaggedDocument d = new TaggedDocument("tmp/test.txt");
	System.out.println(d.getLabelPosMap());
	
//	CopyFile.copyfile("tmp/test0.txt", "tmp/test1.txt");
//	CopyFile.copyfile("tmp/test0.txt", "tmp/test2.txt");
//	CopyFile.copyfile("tmp/test0.txt", "tmp/test3.txt");
//	CopyFile.copyfile("tmp/test0.txt", "tmp/test4.txt");
//	CopyFile.copyfile("tmp/test0.txt", "tmp/test5.txt");
//	CopyFile.copyfile("tmp/test0.txt", "tmp/test6.txt");
//	CopyFile.copyfile("tmp/test0.txt", "tmp/test7.txt");
//	
//	Crf.runCrf("tmp/train.txt", "tmp/test0.txt");
//	TaggedDocument doc0 = new TaggedDocument("tmp/test0.txt.wseg");
//	
//	Crf.runCrf("tmp/TrainSet/bag0.txt", "tmp/test1.txt");
//	TaggedDocument doc = new TaggedDocument("tmp/test1.txt.wseg");
//	Crf.runCrf("tmp/TrainSet/bag1.txt", "tmp/test2.txt");
//	TaggedDocument doc1 = new TaggedDocument("tmp/test2.txt.wseg");
//	Crf.runCrf("tmp/TrainSet/bag2.txt", "tmp/test3.txt");
//	TaggedDocument doc2 = new TaggedDocument("tmp/test3.txt.wseg");
//	Crf.runCrf("tmp/TrainSet/bag3.txt", "tmp/test4.txt");
//	TaggedDocument doc3 = new TaggedDocument("tmp/test4.txt.wseg");
//	Crf.runCrf("tmp/TrainSet/bag4.txt", "tmp/test5.txt");
//	TaggedDocument doc4 = new TaggedDocument("tmp/test5.txt.wseg");
//	Crf.runCrf("tmp/TrainSet/bag5.txt", "tmp/test6.txt");
//	TaggedDocument doc5 = new TaggedDocument("tmp/test6.txt.wseg");
//	Crf.runCrf("tmp/TrainSet/bag6.txt", "tmp/test7.txt");
//	TaggedDocument doc6 = new TaggedDocument("tmp/test7.txt.wseg");
//	
//	System.out.println(doc0.getLabelPosMap());
//	System.out.println(doc.getLabelPosMap());
//	System.out.println(doc1.getLabelPosMap());
//	System.out.println(doc2.getLabelPosMap());
//	System.out.println(doc3.getLabelPosMap());
//	System.out.println(doc4.getLabelPosMap());
//	System.out.println(doc5.getLabelPosMap());
//	System.out.println(doc6.getLabelPosMap());
    }// end main class
    
}// end Test class

