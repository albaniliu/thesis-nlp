/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Dinh nghia tat ca cac chuoi static trong chuong trinh
 * @author banhbaochay
 */
public class Config {
    /**
     * Key de luu per shortcut trong map
     */
    public static final String PER_SHORTCUT = "per.shortcut";
    
    /**
     * Gia tri mac dinh cua per shortcut trong map: P
     */
    public static final String PER_SHORTCUT_DEFAULT = "P";
    
    /**
     * Key de luu loc shortcut trong map
     */
    public static final String LOC_SHORTCUT = "loc.shortcut";
    
    /**
     * Gia tri mac dinh cua loc shortcut trong map: L
     */
    public static final String LOC_SHORTCUT_DEFAULT = "L";
    
    /**
     * Key de luu org shortcut trong map
     */
    public static final String ORG_SHORTCUT = "org.shortcut";
    
    /**
     * Gia tri mac dinh cua org shortcut trong map: O
     */
    public static final String ORG_SHORTCUT_DEFAULT = "O";
    
    /**
     * Key de luu pos shortcut trong map
     */
    public static final String POS_SHORTCUT = "pos.shortcut";
    
    /**
     * Gia tri mac dinh cua pos shortcut trong map: S
     */
    public static final String POS_SHORTCUT_DEFAULT = "S";
    
    /**
     * Key de luu job shortcut trong map
     */
    public static final String JOB_SHORTCUT = "job.shortcut";
    
    /**
     * Gia tri mac dinh cua job shortcut trong map: J
     */
    public static final String JOB_SHORTCUT_DEFAULT = "J";
    
    /**
     * Key de luu date shortcut trong map
     */
    public static final String DATE_SHORTCUT = "date.shortcut";
    
    /**
     * Gia tri mac dinh cua date shortcut trong map: D
     */
    public static final String DATE_SHORTCUT_DEFAULT = "D";
    
    /**
     * Key de luu untag shortcut trong map
     */
    public static final String UNTAG_SHORTCUT = "untag.shortcut";
    
    /**
     * Gia tri mac dinh cua untag shortcut trong map: U
     */
    public static final String UNTAG_SHORTCUT_DEFAULT = "U";
    
    /**
     * Key de luu merge shortcut trong map
     */
    public static final String MERGE_SHORTCUT = "merge.shortcut";
    
    /**
     * Gia tri mac dinh cua merge shortcut trong map: M
     */
    public static final String MERGE_SHORTCUT_DEFAULT = "M";
    
    /**
     * Key de luu seg shortcut trong map
     */
    public static final String SEG_SHORTCUT = "seg.shortcut";
    
    /**
     * Gia tri mac dinh cua seg shortcut trong map: G
     */
    public static final String SEG_SHORTCUT_DEFAULT = "G";
    
    /**
     * Key de luu click shortcut trong map
     */
    public static final String CLICK_SHORTCUT = "click.shortcut";
    
    /**
     * Gia tri mac dinh cua click shortcut trong map: C
     */
    public static final String CLICK_SHORTCUT_DEFAULT = "C";
    
    /**
     * Key de luu font name cua text area trong Tagging panel
     */
    public static final String TEXT_FONT_NAME = "text.font.name";
    
    /**
     * Gia tri mac dinh cua text font name: Times New Roman
     */
    public static final String TEXT_FONT_NAME_DEFAULT = "Times New Roman";
    
    /**
     * Key de luu font size cua text area trong Tagging panel
     */
    public static final String TEXT_FONT_SIZE = "text.font.size";
    
    /**
     * Gia tri mac dinh cua text font size: 14
     */
    public static final String TEXT_FONT_SIZE_DEFAULT = "14";
    
    /**
     * Key de luu font style cua text area trong Tagging panel
     */
    public static final String TEXT_FONT_STYLE = "text.font.style";
    
    /**
     * Gia tri mac dinh cua text font style: Plain
     */
    public static final String TEXT_FONT_STYLE_DEFAULT = "Plain";
    
    /**
     * Key de luu font size cua line area trong Convert panel
     */
    public static final String LINE_FONT_SIZE = "line.font.size";
    
    /**
     * Gia tri mac dinh cua line font size: 14
     */
    public static final String LINE_FONT_SIZE_DEFAULT = "14";
    
    /**
     * Key de luu duong dan thu muc mac dinh
     */
    public static final String DIRECTORY_PATH = "directory.path";
    
    /**
     * Gia tri mac dinh cua duong thu muc: System.getProperty("user.dir")
     */
    public static final String DIRECTORY_PATH_DEFAULT = System.getProperty("user.dir");
    
    /**
     * Key de luu shortcut cho nut browse
     */
    public static final String BROWSE_SHORTCUT = "browse.shortcut";
    
    /**
     * Gia tri shortcut mac dinh cho nut browse: B
     */
    public static final String BROWSE_SHORTCUT_DEFAULT = "B";
    
    /**
     * Kiem tra su thay doi giua mapConfig va proper
     * @param mapConfig
     * @param proper
     * @return 
     */
    public static boolean checkChanged(HashMap<String, String> mapConfig, Properties proper) {
        boolean isChanged = false;
        for (Entry<String, String> entry : mapConfig.entrySet()) {
            if (!entry.getValue().equalsIgnoreCase(proper.getProperty(entry.getKey()))) {
                proper.put(entry.getKey(), entry.getValue());
                isChanged = true;
            }
        }// end foreach entry
        return isChanged;
    }
}
