/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import crfsvm.crf.een_phuong.Offset;
import crfsvm.crf.een_phuong.TaggedDocument;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author banhbaochay
 */
public class Count {
    /**
     * Dem so lan duoc gan nhan IOB cua cac tu trong van ban
     * @param countMap Map co kieu (String, Map(String, Integer)). Trong do key la vi tri cua tu duoc bieu dien: offsetSentence-offsetWord.
     * value la 1 map voi key la nhan IOB gan cho tu va value la so lan duoc gan nhan IOB do
     * @param predictPath duong dan den file predict la file da duoc gan nhan
     */
    public static void countAppearIOB(Map countMap, String predictPath) {
        TaggedDocument tmpDoc = new TaggedDocument(predictPath);
        for (int i = 0; i < tmpDoc.size(); i++) {
            List<String> iobList = tmpDoc.getSentence(i).getIobList();
            for (String iobInfo : iobList) {
                String offset = iobInfo.split(",")[0];
                String iobLabel = iobInfo.split(",")[1];
                if (countMap.containsKey(offset)) {
                    // tu nay da co trong map
                    Map<String, Integer> iobCountMap = (Map<String, Integer>) countMap.get(offset);
                    if (iobCountMap.containsKey(iobLabel)) {
                        // Nhan IOB nay da duoc dem truoc do 
                        int count = iobCountMap.get(iobLabel);
                        iobCountMap.put(iobLabel, ++count);
                    } else {
                        // Bien dem cho nhan IOB nay chua xuat hien
                        iobCountMap.put(iobLabel, 1);
                    }
                } else {
                    // Them moi thong tin count cho 1 tu chua co trong countMap
                    Map<String, Integer> iobCountMap = new HashMap<String, Integer>();
                    iobCountMap.put(iobLabel, 1);
                    countMap.put(offset, iobCountMap);
                }
            }// end foreach iobInfo
        }// end for i
    }// end countAppearIOB method
    
    /**
     * Dem so lan duoc gan nhan thuc the cua cac tu trong van ban
     * @param countMap Map co kieu (Offset, Map(String, Integer)). Trong do key la vi tri cua tu duoc bieu dien: offsetSentence-offsetWord.
     * value la 1 map voi key la nhan thuc the gan cho tu va value la so lan duoc gan nhan thuc the do
     * @param predictPath duong dan den file predict la file da duoc gan nhan
     */
    public static void countAppearLabel(Map countMap, String predictPath) {
        TaggedDocument tmpDoc = new TaggedDocument(predictPath);
	for (Object entrySet : tmpDoc.getLabelPosMap().entrySet()) {
	    Map.Entry entry = (Map.Entry) entrySet;
	    Offset offset = (Offset) entry.getKey();
	    String label = (String) entry.getValue();
	    
	    if (countMap.containsKey(offset)) {
		// Vi tri nay da duoc liet ke trong countMap
		Map labelCountMap = (Map) countMap.get(offset);
		if (labelCountMap.containsKey(label)) {
		    int count = (Integer) labelCountMap.get(label);
		    labelCountMap.put(label, ++count);
		} else {
		    labelCountMap.put(label, 1);
		}// end if labelCountMap ? label
	    } else {
		// Vi tri nay chua duoc liet ke trong countMap
		Map labelCountMap = new HashMap();
		labelCountMap.put(label, 1);
		countMap.put(offset, labelCountMap);
	    }// end if countMap ? key offset
	    
	}// end foreach entrySet
    }// end countAppearLabel method
}// end Count class

