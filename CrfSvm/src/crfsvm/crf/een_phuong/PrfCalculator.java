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

    /**
     * 
     * @param testDoc
     * @param preDoc
     * @param mode
     */
    public PrfCalculator(TaggedDocument testDoc, TaggedDocument preDoc, CalcMode mode) {
        manualCount = new HashMap();
        modelCount = new HashMap();
        matchCount = new HashMap();
        labelNameList = new ArrayList<String>(testDoc.getLabelNameList());
        iobNameList = new ArrayList<String>(testDoc.getIobNameList());
        this.mode = mode;
        
        result = new HashMap();

        switch (mode) {
            case IOBMODE:
                countIob(manualCount, testDoc);
                countIob(modelCount, preDoc);
                break;
            case LABELMODE:
                countLabel(manualCount, testDoc);
                countLabel(modelCount, preDoc);
                break;
        }
        match(testDoc, preDoc);
    }// end constructor

    /**
     * Dem so luong thuc the trong van ban
     * @param count Offset, Integer
     * @param doc 
     */
    private void countLabel(Map count, TaggedDocument doc) {
        for (String label : doc.getLabelNameList()) {
            count.put(label, doc.getLabelCountByName(label));
        }// end foreach label
    }// end count method
    
    private void countIob(Map count, TaggedDocument doc) {
        for (String iob : doc.getIobNameList()) {
            count.put(iob, doc.getIobCountByName(iob));
        }// end foreach iob
    }// end countIob method

    /**
     * Tinh toan so nhan duoc gan dung o preDoc so voi testDoc
     * @param testDoc
     * @param preDoc 
     */
    private void match(TaggedDocument testDoc, TaggedDocument preDoc) {
        Map testMap = null;
        Map preMap = null;
        switch (mode) {
            case IOBMODE:
                testMap = testDoc.getIobPosMap();
                preMap = preDoc.getIobPosMap();
                break;
            case LABELMODE:
                testMap = testDoc.getLabelPosMap();
                preMap = preDoc.getLabelPosMap();
                break;
        }

        for (Object entrySet : preMap.entrySet()) {
            Map.Entry entry = (Map.Entry) entrySet;
            Offset offset = (Offset) entry.getKey();
            // Nhan duoc gan o van ban Predict
            String preLabel = (String) entry.getValue();

            if (testMap.containsKey(offset)) {
                // Neu tu nay co duoc gan nhan trong van ban test
                // Nhan duoc gan o van ban test
                String testLabel = (String) testMap.get(offset);
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

    public void calcWithLabel() {
        for (String label : labelNameList) {
            int match = matchCount.containsKey(label) ? (Integer) matchCount.get(label) : 0;
            int model = modelCount.containsKey(label) ? (Integer) modelCount.get(label) : 0;
            int manual = (Integer) manualCount.get(label);
            double P = (model != 0) ? (double) match * 100 / model : 0;
            double R = (double) match * 100 / manual;
            double F = (2 * P * R) / (P + R);
            result.put(label, new Double[]{
                        P, R, F
                    });
        }// end foreach label
    }// end calcWithLabel method
    
    public void calcWithIob() {
        for (String label : iobNameList) {
            int match = matchCount.containsKey(label) ? (Integer) matchCount.get(label) : 0;
            int model = modelCount.containsKey(label) ? (Integer) modelCount.get(label) : 0;
            int manual = (Integer) manualCount.get(label);
            double P = (model != 0) ? (double) match * 100 / model : 0;
            double R = (double) match * 100 / manual;
            double F = (2 * P * R) / (P + R);
            result.put(label, new Double[]{
                        P, R, F
                    });
        }// end foreach label
    }// end calcWithLabel method

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (mode) {
            case LABELMODE:
                for (String label : labelNameList) {
                    Double[] r = (Double[]) result.get(label);
                    sb.append(label);
                    sb.append(":\n");
                    sb.append(String.format("Manual: %d\tModel: %d\tMatch: %d\n", 
                            manualCount.get(label), modelCount.get(label), matchCount.get(label)));
                    sb.append(String.format("P = %2.2f\tR = %2.2f\tF = %2.2f\n", r[0], r[1], r[2]));
                }// end foreach label
                break;
            case IOBMODE:
                for (String label : iobNameList) {
                    Double[] r = (Double[]) result.get(label);
                    sb.append(label);
                    sb.append(":\n");
                    sb.append(String.format("Manual: %d\tModel: %d\tMatch: %d\n", 
                            manualCount.get(label), modelCount.get(label), matchCount.get(label)));
                    sb.append(String.format("P = %2.2f\tR = %2.2f\tF = %2.2f\n", r[0], r[1], r[2]));
                }// end foreach label
                break;
            default:
                throw new AssertionError();
        }
        return sb.toString();
    }
    private Map matchCount;
    private Map modelCount;
    private Map manualCount;
    private List<String> labelNameList;
    private List<String> iobNameList;
    /**
     * String - Double[]: P-R-F
     */
    private Map result;
    
    private CalcMode mode;
    
    public enum CalcMode {
        IOBMODE,
        LABELMODE
    }
}// end PrfCalculator class

