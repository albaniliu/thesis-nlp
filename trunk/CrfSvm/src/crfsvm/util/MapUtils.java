/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import crfsvm.crf.een_phuong.Offset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author banhbaochay
 */
public class MapUtils {

    /** Sap xep map theo thu tu tang dan value (so sanh truc tiep value)
     * @param map
     * @return 
     */
    public static Map sortMap(Map map) {
        return sortMap(map, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

    }// end sortMap method

    /** Sap xep map theo thu tu nguoi dung tu dinh nghia doi voi value
     * @param map
     * @param comparator 
     * @return 
     */
    public static Map sortMap(Map map, Comparator comparator) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, comparator);

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;

    }// end sortMap method

    /**
     * Tim ra cac phan tu trong map co so entropy hong vuot qua nguong
     * @param entropyMap Co dang key la vi tri cua tu (Offset), value la mang 2 phan tu: nhan thuc the hoac iob duoc gan
     * nhieu nhat cho tu do (String) va entropy cua tu (Double)
     * @param threshold 
     * @return  Map cac phan tu thoa man yeu cau co entropy khong  vuot qua nguong, trong do key la 
     * vi tri cua tu trong van ban (Offset), value la nhan thuc the hoac iob duoc gan nhieu nhat cho tu do (String)
     */
    public static Map filterByEntropy(Map entropyMap, double threshold) {
        Map map = new TreeMap();
        for (Object entrySet : entropyMap.entrySet()) {
            Map.Entry entry = (Map.Entry) entrySet;
            Object[] value = (Object[]) entry.getValue();
            Offset offset = (Offset) entry.getKey();
            Double entropy = (Double) value[1];
            String label = (String) value[0];
            if (label.equals("O")) {
                continue;
            }
            if (entropy <= threshold) {
                map.put(offset, (String) value[0]);
            }// end if entropy < threshold
        }// end foreach entrySet
        return map;
    }// end filterByEntropy method
}// end MapUtils class

