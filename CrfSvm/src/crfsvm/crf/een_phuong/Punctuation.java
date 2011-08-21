/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

/**
 *
 * @author banhbaochay
 */
public class Punctuation {
    
    public static boolean isPunct(String string) {
        boolean isPunct = false;
        for (String punct : punctuations) {
            if (string.equals(punct)) {
                isPunct = true;
                break;
            }// end if string.equals(punct)
        }// end foreach punct
        return isPunct;
    }// end isPunct method
    
    static String[] punctuations = {".", "," , "!", "(", ")", "[", "]", "{", "}",
            "$", "?", "@", "\"", "\u2013", "-", "/", "...", ":", "'", ";", "*", "+" , "#",
            "%", "^", "&", "=", "|", "~", "`"};
}// end Punctuation class

