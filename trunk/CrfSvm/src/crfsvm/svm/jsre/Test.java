/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.svm.jsre;

import crfsvm.svm.org.itc.irst.tcc.sre.util.SparseVector;
import crfsvm.svm.org.itc.irst.tcc.sre.util.Vector;
import java.util.HashMap;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Vector v = new SparseVector();
        v.add(1, 23);
        v.add(2, 12);
        System.out.println(v);
        v.set(1, 24);
        System.out.println(v.norm());
        v.normalize();
        System.out.println(v);
        System.out.println(v.norm());
    }// end main
    
}// end Test class
class A {
    HashMap<String, Integer> map;
    A() {
        map = new HashMap<String, Integer>();
        map.put("a", 1);
    }
}

