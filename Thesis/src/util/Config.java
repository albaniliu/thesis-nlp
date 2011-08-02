/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Dinh nghia tat ca cac chuoi static trong chuong trinh
 * @author banhbaochay
 */
public class Config {
    /**
     * Key de luu per shortcut trong map: per.shortcut
     */
    public static final String PER_SHORTCUT = "per.shortcut";
    
    /**
     * Gia tri mac dinh cua per shortcut trong map: alt + P
     */
    public static final String PER_SHORTCUT_DEFAULT = "alt + P";
    
    /**
     * Ten action mac dinh lien ket voi button per: tagPer
     */
    public static final String PER_ACTION = "tagPer";
    
    /**
     * Key de luu loc shortcut trong map: loc.shortcut
     */
    public static final String LOC_SHORTCUT = "loc.shortcut";
    
    /**
     * Gia tri mac dinh cua loc shortcut trong map: alt + L
     */
    public static final String LOC_SHORTCUT_DEFAULT = "alt + L";
    
    /**
     * Ten action mac dinh lien ket voi button loc: tagLoc
     */
    public static final String LOC_ACTION = "tagLoc";
    
    /**
     * Key de luu org shortcut trong map: org.shortcut
     */
    public static final String ORG_SHORTCUT = "org.shortcut";
    
    /**
     * Gia tri mac dinh cua org shortcut trong map: alt + O
     */
    public static final String ORG_SHORTCUT_DEFAULT = "alt + O";
    
    /**
     * Ten action mac dinh lien ket voi button org: tagORG
     */
    public static final String ORG_ACTION = "tagOrg";
    
    /**
     * Key de luu pos shortcut trong map: pos.shortcut
     */
    public static final String POS_SHORTCUT = "pos.shortcut";
    
    /**
     * Gia tri mac dinh cua pos shortcut trong map: alt + S
     */
    public static final String POS_SHORTCUT_DEFAULT = "alt + S";
    
    /**
     * Ten action mac dinh lien ket voi button pos: tagPos
     */
    public static final String POS_ACTION = "tagPos";
    
    /**
     * Key de luu job shortcut trong map: job.shortcut
     */
    public static final String JOB_SHORTCUT = "job.shortcut";
    
    /**
     * Gia tri mac dinh cua job shortcut trong map: alt + J
     */
    public static final String JOB_SHORTCUT_DEFAULT = "alt + J";
    
    /**
     * Ten action mac dinh lien ket voi button job: tagJob
     */
    public static final String JOB_ACTION = "tagJob";
    
    /**
     * Key de luu date shortcut trong map: date.shortcut
     */
    public static final String DATE_SHORTCUT = "date.shortcut";
    
    /**
     * Gia tri mac dinh cua date shortcut trong map: alt + D
     */
    public static final String DATE_SHORTCUT_DEFAULT = "alt + D";
    
    /**
     * Ten action mac dinh lien ket voi button date: tagDate
     */
    public static final String DATE_ACTION = "tagDate";
    
    /**
     * Key de luu untag shortcut trong map: untag.shortcut
     */
    public static final String UNTAG_SHORTCUT = "untag.shortcut";
    
    /**
     * Gia tri mac dinh cua untag shortcut trong map: alt + U
     */
    public static final String UNTAG_SHORTCUT_DEFAULT = "alt + U";
    
    /**
     * Ten action mac dinh lien ket voi button untag: untag
     */
    public static final String UNTAG_ACTION = "untag";
    
    /**
     * Key de luu merge shortcut trong map: merge.shortcut
     */
    public static final String COMBINE_SHORTCUT = "merge.shortcut";
    
    /**
     * Gia tri mac dinh cua merge shortcut trong map: alt + M
     */
    public static final String COMBINE_SHORTCUT_DEFAULT = "alt + M";
    
    /**
     * Ten action mac dinh lien ket voi button combie: combineWord
     */
    public static final String COMBINE_ACTION = "combineWord";
    
    /**
     * Key de luu seg shortcut trong map: seg.shortcut
     */
    public static final String SPLIT_SHORTCUT = "seg.shortcut";
    
    /**
     * Gia tri mac dinh cua seg shortcut trong map: alt + G
     */
    public static final String SPLIT_SHORTCUT_DEFAULT = "alt + G";
    
    /**
     * Ten action mac dinh lien ket voi button split: splitWord
     */
    public static final String SPLIT_ACTION = "splitWord";
    
    /**
     * Key de luu click shortcut trong map: click.shortcut
     */
    public static final String CLICK_SHORTCUT = "click.shortcut";
    
    /**
     * Gia tri mac dinh cua click shortcut trong map: alt + C
     */
    public static final String CLICK_SHORTCUT_DEFAULT = "alt + C";
    
    /**
     * Ten action mac dinh lien ket voi button click: 
     */
    public static final String CLICK_ACTION = "changeClickState";
    
    /**
     * Key de luu click shortcut trong map: click.shortcut
     */
    public static final String MODE_SHORTCUT = "mode.shortcut";
    
    /**
     * Gia tri mac dinh cua click shortcut trong map: alt + C
     */
    public static final String MODE_SHORTCUT_DEFAULT = "alt + M";
    
    /**
     * Ten action mac dinh lien ket voi button click: 
     */
    public static final String MODE_ACTION = "changeMode";
    
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
    public static final String BROWSE_SHORTCUT_DEFAULT = "alt + B";
    
    /**
     * Ten action mac dinh lien ket voi button browse: browse
     */
    public static final String BROWSE_ACTION = "browse";
    
    /**
     * Key de luu shortcut cho nut undo
     */
    public static final String UNDO_SHORTCUT = "undo.shortcut";
    
    /**
     * Gia tri shortcut mac dinh cho nut undo: ctrl Z
     */
    public static final String UNDO_SHORTCUT_DEFAULT = "ctrl + Z";
    
    /**
     * Ten action mac dinh lien ket voi button undo: undo
     */
    public static final String UNDO_ACTION = "undo";
    
    /**
     * Key de luu shortcut cho nut redo
     */
    public static final String REDO_SHORTCUT = "redo.shortcut";
    
    /**
     * Gia tri shortcut mac dinh cho nut redo: ctrl shift Z
     */
    public static final String REDO_SHORTCUT_DEFAULT = "ctrl shift + Z";
    
    /**
     * Ten action mac dinh lien ket voi button redo: redo
     */
    public static final String REDO_ACTION = "redo";
    
    /**
     * Key de luu shortcut cho nut save
     */
    public static final String SAVE_SHORTCUT = "save.shortcut";
    
    /**
     * Gia tri shortcut mac dinh cho nut save: ctrl S
     */
    public static final String SAVE_SHORTCUT_DEFAULT = "ctrl + S";
    
    /**
     * Ten action mac dinh lien ket voi button save: save
     */
    public static final String SAVE_ACTION = "save";
    
    /**
     * Key de luu shortcut cho nut save as
     */
    public static final String SAVE_AS_SHORTCUT = "save.as.shortcut";
    
    /**
     * Gia tri shortcut mac dinh cho nut save as: ctrl shift S
     */
    public static final String SAVE_AS_SHORTCUT_DEFAULT = "ctrl shift + S";
    
    /**
     * Ten action mac dinh lien ket voi button save as: saveAs
     */
    public static final String SAVE_AS_ACTION = "saveAs";
    
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
