/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import crfsvm.crf.een_phuong.Offset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author banhbaochay
 */
public class MathUtils {
    public static double calcEntropy(int ri, int B, int... rj) {
        double riOverB = (double) ri / B;
        double re = -riOverB * Math.log(riOverB);
        for (int j : rj) {
            double jOverB = (double) j / B;
            re -= jOverB * Math.log(jOverB);
        }// end foreach j
        return re;
    }// end calcEntropy method
    
    /**
     * Tinh entropy cua cac tu duoc gan nhan trong countMap
     * @param countMap Map co kieu (Offset, Map(String, Integer)). Trong do key la kieu Offset chi ra vi tri cua tu/cum tu
     * value la 1 map voi key la nhan thuc the hoac iob gan cho tu va value la so lan duoc gan nhan thuc the do
     * @return Tra ve map voi key la vi tri cua tu (Offset), value la mang Object luu nhan iob hoac thuc the gan cho tu va entropy cua tu
     */
    public static Map calcEntropy(Map countMap) {
	Map entropyMap = new LinkedHashMap();
	for (Object entrySet : countMap.entrySet()) {
	    Map.Entry entry = (Map.Entry) entrySet;
	    Offset offset = (Offset) entry.getKey();
	    Map value = (Map) entry.getValue();
	    
	}// end foreach entrySet
	return entropyMap;
    }// end calcEntropy method
    
}// end MathUtils class

