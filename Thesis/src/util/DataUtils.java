/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Cung cap cac phuong thuc lay du lieu
 * @author banhbaochay
 */
public class DataUtils {

    /**
     * Lay toan bo du lieu tren 1 cot
     * @param table
     * @param col So thu tu cot
     * @return Tra ve 1 list chua cac du lieu 1 cot
     */
    public static List getDataColFromTable(JTable table, int col) {
        List rowList = new ArrayList();
        TableModel model = table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            rowList.add(model.getValueAt(i, col));
        }// end for i
        return rowList;
    }// end getDataColFromTable method

    /**
     * Chuyen cac row duoc select tu tableSource sang tableTarget. Dieu kien can phai la 2 table co cung kieu luu tru tren cac cot
     * @param tableSource
     * @param tableTarget
     * @return Tra ve so row duoc move 
     */
    public static int moveRows(JTable tableSource, JTable tableTarget) {
        int[] selectedRows = tableSource.getSelectedRows();
        if (selectedRows.length == 0) {
            return 0;
        } else {
            DefaultTableModel srcModel = (DefaultTableModel) tableSource.getModel();
            java.util.Vector data = srcModel.getDataVector();
            DefaultTableModel tgtModel = (DefaultTableModel) tableTarget.getModel();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                java.util.Vector row = (java.util.Vector) data.elementAt(selectedRows[i]);
                tgtModel.addRow(row);
                srcModel.removeRow(selectedRows[i]);
            }// end for i
            return selectedRows.length;
        }
    }// end moveRows method
}// end DataUtils class

