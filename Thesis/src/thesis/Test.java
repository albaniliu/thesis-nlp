/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thesis;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author banhbaochay
 */
public class Test {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Font org = new Font("Times New Roman", Font.ITALIC, 24);
        System.out.println("Thay doi size:");
        Font o1 = org.deriveFont(20.0f);
        System.out.printf("Style: %d, size: %d\n", org.getStyle(), org.getSize());
        System.out.printf("Style: %d, size: %d\n", o1.getStyle(), o1.getSize());
        
    }
}
