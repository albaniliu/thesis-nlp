/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public static void main(String[] args) {
        File d = new File("tmp/TrainSet");
        for (File string : d.listFiles()) {
            try {
                System.out.println(string.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(MapUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }// end foreach string
    }// end main class
}// end MapUtils class

