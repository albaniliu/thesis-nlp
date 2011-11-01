/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.Offset;
import crfsvm.crf.een_phuong.PrfCalculator;
import crfsvm.crf.een_phuong.TaggedDocument;
import crfsvm.util.Count;
import crfsvm.util.FileGarbageCollector;
import crfsvm.util.FileUtils;
import crfsvm.util.MapUtils;
import crfsvm.util.MathUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        FileGarbageCollector garbage = new FileGarbageCollector("tmp");
        garbage.markState();
        
        int B = 7;
        double threshold = MathUtils.calcEntropy(B, 3, 1, 1, 1, 1);
        String input = "tmp/test2.txt";
        String testNoTag = "tmp/test.txt";

        // Kieu doi tuong doc luu thong tin ve file test
        TaggedDocument testDoc = new TaggedDocument(input);

        CopyFile.copyfile(input, testNoTag);
        FileUtils.removeTag(testNoTag);
        CopyFile.copyfile("tmp/model/model.txt", "model/model.txt");
        Crf.predict("model", testNoTag);

        // Kieu doi tuong doc luu thong tin ve file predict do train 1 lan
        TaggedDocument preDocTrainOnce = new TaggedDocument(testNoTag + ".wseg");

        Map<Offset, Map<String, Integer>> countMap = new TreeMap<Offset, Map<String, Integer>>();
        for (int i = 0; i < 7; i++) {
            String testInBag = "tmp/t" + i + ".txt";
            CopyFile.copyfile("tmp/model/model" + i + ".txt", "model/model.txt");
            CopyFile.copyfile(testNoTag, testInBag);
            Crf.predict("model", testInBag);
            Count.countAppearLabel(countMap, testInBag + ".wseg");
        }// end for i
        Map filterMap = MapUtils.filterByEntropy(MathUtils.calcEntropy(countMap, B), threshold);
        TaggedDocument preDocBagging = testDoc.createTaggedDoc(filterMap, TaggedDocument.MapType.LABELMAP);

        preDocTrainOnce.print2File("tmp/once.txt");
        preDocBagging.print2File("tmp/bagging.txt");

        PrfCalculator calcOnce = new PrfCalculator(testDoc, preDocTrainOnce, PrfCalculator.CalcMode.LABELMODE);
        calcOnce.calc();
        PrfCalculator calcBagging = new PrfCalculator(testDoc, preDocBagging, PrfCalculator.CalcMode.LABELMODE);
        calcBagging.calc();
        System.out.println("Train 1 lan:");
        System.out.println(calcOnce);
        System.out.println("Train bagging: ");
        System.out.println(calcBagging);

        //
        for (Object entryObject : filterMap.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObject;
            Offset offset = (Offset) entry.getKey();
            String label = (String) entry.getValue();
            if (label.equals("per")) {
                // Chi xet tu duoc gan nhan per
                if (preDocTrainOnce.getLabelPosMap().containsKey(offset)
                        && testDoc.getLabelPosMap().containsKey(offset)) {
                    // Tu nay thuc te co mang nhan va trainOnce co du doan nhan
                    String labelTest = (String) testDoc.getLabelPosMap().get(offset);
                    String labelOnce = (String) preDocTrainOnce.getLabelPosMap().get(offset);
                    if (labelTest.equals(labelOnce) && !labelTest.equals(label)) {
                        // Neu trainOnce gan nhan dung va bagging gan nhan sai
                        System.out.println(offset + " | " + testDoc.wordAt(offset));
                    }
                } else if (!preDocTrainOnce.getLabelPosMap().containsKey(offset)
                        && !testDoc.getLabelPosMap().containsKey(offset)) {
                    System.out.println(offset + " | " + testDoc.wordAt(offset));
                }// end trainOnce, test ? contain offset
            }// end label = per
        }// end foreach entryObject

        // Xoa cac file moi tao, chi dung trong linux
        garbage.collectGarbage();


    }// end main class
}// end Test class

class P implements Cloneable {

    List v;

    public P(List v) {
        this.v = v;
    }

    @Override
    public Object clone() {
        try {
            P cloned = (P) super.clone();
            cloned.v = new ArrayList(v);
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "P{" + "v=" + v + '}';
    }
}