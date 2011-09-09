/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsre;

import java.util.HashMap;

/**
 *
 * @author banhbaochay
 */
public class Test {
    
    private void s() {
        System.out.println(Test1.t);
    }
    
    public static void main(String[] args) throws Exception {
        
        System.out.println(Test1.t);
        
        Test1.t = 4;
        System.out.println(Test1.t);
        
        Test a = new Test();
        a.s();
    }// end main
    
}// end Test class
class A {
    HashMap<String, Integer> map;
    A() {
        map = new HashMap<String, Integer>();
        map.put("a", 1);
    }
}

