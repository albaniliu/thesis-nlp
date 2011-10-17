/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author banhbaochay
 */
public class PrfCalculator {

    public PrfCalculator(TaggedDocument testDoc, TaggedDocument preDoc) {
        manualCount = new HashMap();
        modelCount = new HashMap();
        matchCount = new HashMap();
        labelList = new ArrayList<String>(testDoc.getLabelList());
        result = new HashMap();

        countLabel(manualCount, testDoc);
        countLabel(modelCount, preDoc);
        match(testDoc, preDoc);
    }// end constructor

    /**
     * Dem so luong thuc the trong van ban
     * @param count Offset, Integer
     * @param doc 
     */
    private void countLabel(Map count, TaggedDocument doc) {
        for (String label : doc.getLabelList()) {
            count.put(label, doc.getLabelCountByName(label));
        }// end foreach label
    }// end count method

    /**
     * Tinh toan so nhan duoc gan dung o preDoc so voi testDoc
     * @param testDoc
     * @param preDoc 
     */
    private void match(TaggedDocument testDoc, TaggedDocument preDoc) {
        Map testLabelMap = testDoc.getLabelPosMap();
        Map preLabelMap = preDoc.getLabelPosMap();

        for (Object entrySet : preLabelMap.entrySet()) {
            Map.Entry entry = (Map.Entry) entrySet;
            Offset offset = (Offset) entry.getKey();
            // Nhan duoc gan o van ban Predict
            String preLabel = (String) entry.getValue();

            if (testLabelMap.containsKey(offset)) {
                // Neu tu nay co duoc gan nhan trong van ban test
                // Nhan duoc gan o van ban test
                String testLabel = (String) testLabelMap.get(offset);
                if (preLabel.equals(testLabel)) {
                    // Truong hop predict dung
                    if (matchCount.containsKey(preLabel)) {
                        // Nhan nay da duoc dem truoc do
                        int count = (Integer) matchCount.get(preLabel);
                        matchCount.put(preLabel, ++count);
                    } else {
                        matchCount.put(preLabel, 1);
                    }
                }// end preLabel = testLabel
            }// end if testLabelMap.containsKey(offset)
        }// end foreach entrySet
    }// end match method

    public void calc() {
        for (String label : labelList) {
            int match = (Integer) matchCount.get(label);
            int model = (Integer) modelCount.get(label);
            int manual = (Integer) manualCount.get(label);
            double P = (double) match * 100 / model;
            double R = (double) match * 100 / manual;
            double F = (2 * P * R) / (P + R);
            result.put(label, new Double[]{
                        P, R, F
                    });
        }// end foreach label

    }// end calc method

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String label : labelList) {
            Double[] r = (Double[]) result.get(label);
            sb.append(label);
            sb.append(":\n");
            sb.append(String.format("Manual: %d\tModel: %d\tMatch: %d\n", 
                    manualCount.get(label), modelCount.get(label), matchCount.get(label)));
            sb.append(String.format("P = %2.2f\tR = %2.2f\tF = %2.2f\n", r[0], r[1], r[2]));
        }// end foreach label
        return sb.toString();
    }
    private Map matchCount;
    private Map modelCount;
    private Map manualCount;
    private List<String> labelList;
    /**
     * String - Double[]: P-R-F
     */
    private Map result;
}// end PrfCalculator class

