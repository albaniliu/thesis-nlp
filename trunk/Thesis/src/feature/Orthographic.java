/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package feature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All orthographic features of word. Ex:
 * <ul>
 *     <li>Init Cap: Chu tich</li>
 *     <li>All Cap: VNPT</li>
 *     <li>Lower Case: hoat dong</li>
 *     <li>Mark: , . !..</li>
 *     <li>Digit: 2, 4, 3, ...</li>
 *     <li>...</li>
 * </ul>
 * @author banhbaochay
 */
public class Orthographic {

    /*
     * Dung theo orthographic cua JSRE
     */
    public static final String WORD_FORM = "_word";
    public static final String POS = "_POS";
    public static final String DICTIONARY = "_dic";
    public static final String IOB = "_iob";
    public static final String INIT_CAP = "_Cap";
    public static final String ALL_CAPS = "_upper";
    public static final String LOWER_CASE = "_lower";
    public static final String PUNCTUATION = "_punc";
    public static final String DIGIT = "_digit";

    /*
     * Chua dung den
     */
    public static final String CAPS_AND_HYPHEN = "CAPS_AND_HYPHEN";
    public static final String CAPS_AND_PERIOD = "CAPS_AND_PERIOD";
    public static final String CAPS_AND_DIGITS = "CAPS_AND_DIGITS";
    public static final String LETTERS_AND_DIGITS = "LETTERS_AND_DIGITS";
    public static final String INIT_CAP_AND_DIGITS = "INIT_CAP_AND_DIGITS";
    public static final String INIT_CAP_AND_PERIOD = "INIT_CAP_AND_PERIOD";
    public static final String INIT_CAP_AND_HYPHEN = "INIT_CAP_AND_HYPHEN";
    public static final String OPEN_PAREN = "OPEN_PAREN";
    public static final String CLOSE_PAREN = "CLOSE_PAREN";
    public static final String BRACE = "BRACE";
    public static final String PERCENT = "PERCENT";
    public static final String HYPHEN = "HYPHEN";
    public static final String SLASH = "SLASH";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";

    /**
     * Check if the first char is uppercase
     * @param string string only contains letters
     * @return Return <code>true</code> if the first char is uppercase, <code>false</code>
     * if not
     */
    public static boolean isInitCap(String string) {
        int codepoint = string.codePointAt(0);
        if (containPunct(string)) {
            return false;
        }
        return Character.isUpperCase(codepoint);
    }

    /**
     * Check if all chars in string are uppercase
     * @param string
     * @return Return <code>true</code> if all chars in are uppercase, <code>false</code>
     * if not
     */
    public static boolean isAllCap(String string) {
        for (int i = 0; i < string.length(); i++) {
            int codepoint = string.codePointAt(i);
            if (!Character.isLetter(codepoint)) {
                return false;
            } else if (Character.isLowerCase(codepoint)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all chars are lower case
     * @param string
     * @return Return <code>true</code> if all chars are lower case, <code>false</code>
     * if not
     */
    public static boolean isLowerCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            int codepoint = string.codePointAt(i);
            if (!Character.isLetter(codepoint)) {
                return false;
            } else if (Character.isUpperCase(codepoint)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if string is totally number
     * @param string
     * @return Return <code>true</code> if all chars are digit, <code>false</code>
     * if not
     */
    public static boolean isDigit(String string) {
        for (int i = 0; i < string.length(); i++) {
            int codepoint = string.codePointAt(i);

            if (!Character.isDigit(codepoint)) {
                return false;
            }
        }
        return true;
    }

        /**
     * Check if string is punctuation
     * @param string String want to check
     * @return Return <code>true</code> if all chars is punctuation, <code>false</code>
     * if other
     */
    public static boolean isPunct(String string) {
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOpenParen(String string) {
        return string.equals("(");
    }

    public static boolean isCloseParen(String string) {
        return string.equals(")");
    }

    /**
     * Check if all characters in string are uppercase and string contains symbol
     * @param string string wants to checks
     * @param symbol a char type: period (.), hyphen (-)
     * @return <true> if all chars are uppercase and contains symbol, <code>false</code>
     * if not
     */
    public static boolean isCapsAndSymbol(String string, char symbol)  {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!Character.isUpperCase(c) && c != symbol) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all characters in string are uppercase and string contains digits
     * @param string string wants to checks
     * @return <true> if all chars are uppercase and contains digits, <code>false</code>
     * if not
     */
    public static boolean isCapsAndDigits(String string) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!Character.isUpperCase(c) && Character.isDigit(c)) {
                return false;
            }
        }
         return true;
    }

    /**
     * Check if string starts with uppercase and contains hyphen
     * @param string string wants to check
     * @param symbol Symbol as: hyphen (-), period (.)
     * @return <code>true</code> if starts with uppercase and contains symbol, <code>
     * false</code> if not
     */
    public static boolean isInitCapAndSymbol(String string, String symbol) {
        if (Character.isUpperCase(string.charAt(0))) {
            return string.contains(symbol);
        } else {
            return false;
        }
    }

    /**
     * Check if string contains any punctuation
     * @param string
     * @return Return <code>true</code> if contains any punctuation, <code>false</code>
     * if not
     */
    public static boolean containPunct(String string) {
        String regex = ",|\\.|:|;|%|@|!|#|\\$|/|\\^|\\(|\\)|<|>|\\?";
        Matcher matcher = Pattern.compile(regex, Pattern.UNICODE_CASE).matcher(string);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if string has percent sign
     * @param string
     * @return Return <code>true</code> if has any percent sign in, <code>false</code>
     * if not
     */
    public static boolean containPercentSign(String string) {
        return string.indexOf("%") != -1;
    }

    /**
     * Check if string has slash sign
     * @param string
     * @return Return <code>true</code> if has any slash sign in, <code>false</code>
     * if not
     */
     public static boolean containSlashSign(String string) {
        return string.indexOf("/") != -1;
    }

     /**
      * Check if string has colon sign
      * @param string
      * @return Return <code>true</code> if has any colon sign in, <code>false</code>
      * if not
      */
    public static boolean containColonSign(String string) {
        return string.indexOf(":") != -1;
    }

    public static boolean isYear(String string) {
        if (string.length() == 4) {
            return isDigit(string);
        } else {
            return false;
        }
    }

}
