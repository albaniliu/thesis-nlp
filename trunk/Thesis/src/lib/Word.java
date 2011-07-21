/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lib;

import context.AbstractContext;
import context.DefaultContext;
import feature.Dictionary;
import feature.POS;
import feature.ENTITY;
import java.util.LinkedHashMap;
import util.SparseVector;

/**
 * This class storage all informations of words in line. Ex: offset, POS, Entity and feature
 * @author banhbaochay
 */
public class Word {

    /**
     * B-tag mode for entity
     */
    public static final int B_TAG = 0;

    /**
     * I-tag mode for entity
     */
    public static final int I_TAG = 1;

    /**
     * Constructor create orthographic, dictionary features for word, <code>offset</code> is set -1
     * @param string Only one word in Vietnamese. Ex: lam_viec, anh, em...
     */
    public Word(String string) {
        this(string, -1);
    }

    /**
     * Constructor create orthographic, dictionary features and offset in sentence for word
     * @param string Only one word in Vietnamese. Ex: lam_viec, anh, em...
     * @param offset Order of word in line
     */
    public Word(String string, int offset) {
        this(string, offset, null);
    }

    public Word(String string, Dictionary dic) {
        this(string, -1, dic);
    }

    public Word(String string, int offset, Dictionary dic) {
        this.offset = offset;
        this.form = string;
        IOB = "O";
        POS = POS.X; // unknow POS
        dicType = (dic == null) ? "NON" : dic.getDictionaryType(string);
    }

    /**
     * Check if the word is IOB
     * @return Return <code>true</code> if is IOB, <code>false</code> if not
     */
    public boolean isIOB() {
        if (IOB.startsWith("B-") || IOB.startsWith("I-")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get window size for this word
     * @return
     */
    public int getWindowSize() {
        return ((DefaultContext) context).getWindowSize();
    }

    public boolean createFeatureVector(LinkedHashMap<String, Integer> featureIndex) {
        boolean state = false;

        return state;
    }

    @Override
    public String toString() {
        return form;
    }

    private String form;
    private int offset;
    private POS POS;
    private String IOB;
    private String dicType;
    private AbstractContext context;
    private String orthographic;
    
    /**
     * Set IOB for a word
     * @param IOB A string has format: B-PER..
     * @throws IllegalArgumentException If IOB string is not legal
     */
    public void setIOB(String IOB) {
        if (IOB.equals(ENTITY.PER.getBTag()) || IOB.equals(ENTITY.PER.getITag())
                || IOB.equals(ENTITY.LOC.getBTag()) || IOB.equals(ENTITY.LOC.getITag())
                || IOB.equals(ENTITY.ORG.getBTag()) || IOB.equals(ENTITY.ORG.getITag())
                || IOB.equals(ENTITY.POS.getBTag()) || IOB.equals(ENTITY.POS.getITag())
                || IOB.equals("O")) {
            this.IOB = IOB;
        } else {
            throw new IllegalArgumentException(IOB + " is not B-tag or I-tag of entities or other entity");
        }
    }

    /**
     * Set entity IOB for a word
     * @param entity
     * @param IOBMode An integer shows IOB is B_TAG type or I_TAG type. There are
     * 2 types:
     * <ul>
     *     <li>Word.B_TAG: b-tag of entity</li>
     *     <li>Word.I_TAG: i-tag of entity</li>
     * </ul>
     * @throws IllegalArgumentException If IOBMode is not legal
     */
    public void setIOB(ENTITY entity, int IOBMode) {
        switch (IOBMode) {
            case B_TAG:
                this.IOB = entity.getBTag();
                break;
            case I_TAG:
                this.IOB = entity.getITag();
                break;
            default:
                throw new IllegalArgumentException(IOBMode + " is not legal for B-tag or I-tag");
        }
    }

    /**
     * Get IOB of a word
     * @return A String about IOB. Ex: B-PER
     */
    public String getIOB() {
       return IOB;
    }

    /**
     * Get dictionary type of word
     * @return A string name of dictionary which it belongs. Return <code>null</code> if
     * dictionary's never been set
     */
    public String getDictionaryType() {
        return dicType;
    }

    /**
     * Set POS for a word
     * @param POS A part of speech type
     */
    public void setPOS(POS POS) {
        this.POS = POS;
    }

    /**
     * Get Part-of-Speech (POS) of this word
     * @return Return X POS if its POS is strange. Return <code>null</code> if has error
     * while create feature
     */
    public POS getPOS() {
        return POS;
    }

    /**
     * Set offset for word
     * @param offset Order of word in line
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Get offset of Word in the line
     * Offset is based on zero
     * @return
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Get exactly word in Vietnamese. Ex: lam_viec, song..
     * @return Return <code>null</code> if can't extract string from word
     */
    public String getForm() {
        return this.form;
    }

    /**
     * Get list words in context
     * @return
     */
    public Object getContext() {
        return context.getWordContext();
    }

    /**
     * Set context for word
     * @param context
     */
    public void setContext(AbstractContext context) {
        this.context = context;
    }

    /**
     * @return the orthographic
     */
    public String getOrthographic() {
        return orthographic;
    }

    /**
     * @param orthographic the orthographic to set
     */
    public void setOrthographic(String orthographic) {
        this.orthographic = orthographic;
    }
}
