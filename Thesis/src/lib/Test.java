/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.security.NoSuchAlgorithmException;

/**
 *
 * @author banhbaochay
 */
public class Test {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String s = "asnh [Alt + p]";
        System.out.println(s.replaceAll(" *\\[.*\\] *", ""));
    }
}
