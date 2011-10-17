/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import crfsvm.crf.een_phuong.Offset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author banhbaochay
 */
public class MathUtils {

    /**
     * Tinh entropy cua bo so ri
     * @param B So bag
     * @param ri So cac bag du doan ung voi tung nhom. Yeu cau tong so ri phai bang B, khong co so hang ri nao bang 0
     * @return 
     */
    public static double calcEntropy(int B, int... ri) {
        double re = 0;
        for (int i : ri) {
            if (i == 0) {
                continue;
            }
            double riOverB = (double) i / B;
            re -= riOverB * Math.log(riOverB);
        }// end foreach i
        return re;
    }// end calcEntropy method

    /**
     * Tinh entropy cua cac tu duoc gan nhan trong countMap
     * @param countMap Map co kieu (Offset, Map(String, Integer)). Trong do key la kieu Offset chi ra vi tri cua tu/cum tu
     * value la 1 map voi key la nhan thuc the hoac iob gan cho tu va value la so lan duoc gan nhan thuc the do
     * @param B so bag
     * @return Tra ve map voi key la vi tri cua tu (Offset), value la mang Object luu nhan iob hoac thuc the gan cho tu va entropy cua tu
     */
    public static Map calcEntropy(Map countMap, int B) {
        Map entropyMap = new LinkedHashMap();
        for (Object entrySet : countMap.entrySet()) {
            Map.Entry entry = (Map.Entry) entrySet;
            Offset offset = (Offset) entry.getKey();
            String labelWithCountMax = "";
            int countMax = 0;
            Map value = (Map) entry.getValue();
            // De 1 vi tri luu so bag gan nhan O cho tu/cum tu
            int[] r = new int[value.size() + 1];

            int index = 0;
            // so bag du doan nhan O
            int bagOther = B;
            for (Object object : value.entrySet()) {
                Map.Entry entryLabelCount = (Map.Entry) object;
                int ri = (Integer) entryLabelCount.getValue();
                r[index++] = ri;
                bagOther -= ri;
                if (countMax < ri) {
                    countMax = ri;
                    labelWithCountMax = (String) entryLabelCount.getKey();
                }// end if countMax < ri

            }// end foreach entryLabelCount

            // Kiem tra co bao nhieu bag gan nhan O cho tu/cum tu nay
            r[r.length - 1] = bagOther;
            if (countMax < bagOther) {
                countMax = bagOther;
                labelWithCountMax = "O";
            }// end if countMax < bagOther

            entropyMap.put(offset, new Object[]{
                        labelWithCountMax,
                        calcEntropy(B, r)
                    });
        }// end foreach entrySet
        return entropyMap;
    }// end calcEntropy method
}// end MathUtils class

