/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsre;

import java.io.File;
import java.net.URI;
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
        
        File f = new File("data/semi");
        System.out.println(f.exists());
    }// end main
    
}// end Test class
class A {
    HashMap<String, Integer> map;
    A() {
        map = new HashMap<String, Integer>();
        map.put("a", 1);
    }
}

