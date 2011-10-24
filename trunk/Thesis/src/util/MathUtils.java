/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.lang.Integer;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author banhbaochay
 */
public class MathUtils {
    
    public static final int NORMAL_MODE = 0;
    
    public static final int JSRE_MODE = 1;
    
    /**
     * Tinh tong so dong cua cac file trong 1 bang
     * @param table
     * @param mode
     * @return 
     */
    public static int calcLength(JTable table, int mode) {
        int totalLength = 0;
        if (mode == NORMAL_MODE) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (Object row : model.getDataVector()) {
                java.util.Vector rowVector = (java.util.Vector) row;
                Integer length = (Integer) rowVector.elementAt(1);
                totalLength += length;
            }// end foreach row
        } else {
        }
        return totalLength;
    }// end calcLength method
}// end MathUtils class

