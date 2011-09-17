/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

/**
 *
 * @author banhbaochay
 */
public class MathUtils {
    public static double calcEntropy(int ri, int B, int... rj) {
        double riOverB = (double) ri / B;
        double re = -riOverB * Math.log(riOverB);
        for (int j : rj) {
            double jOverB = (double) j / B;
            re -= jOverB * Math.log(jOverB);
        }// end foreach j
        return re;
    }// end calcEntropy method
    
    public static void main(String[] args) {
        System.out.println(calcEntropy(5, 7, 2));
        System.out.println(calcEntropy(5, 7, 1, 1));
        System.out.println(calcEntropy(4, 7, 3));
        System.out.println(calcEntropy(4, 7, 2, 1));
        System.out.println(calcEntropy(4, 7, 1, 1, 1));
        System.out.println(calcEntropy(3, 7, 3, 1));
        System.out.println(calcEntropy(3, 7, 2, 1, 1));
        System.out.println(calcEntropy(3, 7, 1, 1, 1, 1));
        
    }// end main class
}// end MathUtils class

