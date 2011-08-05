/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.svm.jsre;

import java.util.HashMap;

/**
 *
 * @author banhbaochay
 */
public class Test {
    public static void main(String[] args) throws Exception {
        int a = 3;
        if(a != 0){
            System.out.println("giai phuong trinh bac 2");
        } else {
            System.out.println("giai phuong trinh bac nhat");
        }
    }// end main
    
}// end Test class
class A {
    HashMap<String, Integer> map;
    A() {
        map = new HashMap<String, Integer>();
        map.put("a", 1);
    }
}

