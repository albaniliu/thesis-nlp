/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.JTextArea;

/**
 * Cung cap cac funtion dung chung trong GUI
 * @author banhbaochay
 */
public class GUIFunction {
    /**
     * Set font cho JTextArea
     */
    public static void setFontArea(JTextArea area, Font font) {
        area.setFont(font);
    }// end setFontArea method
    
    /**
     * Set font cho JTextArea, dong thoi thay doi gia tri cua spinner bieu thi
     * cho size cua area
     * @param area
     * @param spinnerSize spinner phai co model kieu Number
     * @param font 
     */
    public static void setFontArea(JTextArea area, JSpinner spinnerSize, Font font) {
        area.setFont(font);
        spinnerSize.setValue(font.getSize());
    }// end setFontArea method
    
    /**
     * Chuyen doi String cua font style thanh kieu int
     * @param style Bold, Plain...
     * @return 
     */
    public static int string2Int(String style) {
        if (style.equalsIgnoreCase("bold")) {
            return Font.BOLD;
        } else if (style.equalsIgnoreCase("italic")) {
            return Font.ITALIC;
        } else if (style.equalsIgnoreCase("bold italic")) {
            return Font.BOLD + Font.ITALIC;
        } else {
            return Font.PLAIN;
        }
    }// end string2Int method
}
