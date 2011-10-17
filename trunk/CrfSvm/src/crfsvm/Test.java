/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.Offset;
import crfsvm.crf.een_phuong.TaggedDocument;
import crfsvm.util.FileUtils;
import crfsvm.util.MapUtils;
import crfsvm.util.MathUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Test {

    public static void main(String[] args) throws Exception {
        DOMConfigurator.configure("log-config.xml");
        List v = new LinkedList();
        v.add("213");
        P p = new P(v);
        P p1 = (P) p.clone();
        System.out.println(p1);
        System.out.println(p1.v.getClass());
        p.v.add("3223");
        System.out.println(p1);
        System.out.println(p1.v.getClass());
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