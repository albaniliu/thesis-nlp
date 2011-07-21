/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feature;

import java.awt.Color;

/**
 * Contain all specifies of ENTITY enum as name, tag of entity and its number index
 * Ex: PER entity has fields:
 * <ul>
 *      <li><code>name</code>: PER</li>
 *      <li><code>start tag</code>: &lt;PER&gt;</li>
 *      <li><code>end tag</code>: &lt;/PER&gt;</li>
 *      <li><code>b-tag</code>: &&B-PER</li>
 *      <li><code>i-tag</code>: &&I-PER</li>
 *      <li><code>number</code>: 1</li>
 * </ul>
 * <em>Notes:</em> OTHER and NOENTITY are 2 exception entity, they have not start tag and
 * end tag. Their number are 0 and -1
 * @author banhbaochay
 */
public enum ENTITY {

    /**
     * PER entity has fields:
     * <ul>
     *      <li><code>name</code>: PER</li>
     *      <li><code>start tag</code>: &lt;PER&gt;</li>
     *      <li><code>end tag</code>: &lt;/PER&gt;</li>
     *      <li><code>b-tag</code>: B-PER</li>
     *      <li><code>i-tag</code>: I-PER</li>
     *      <li><code>number</code>: 1</li>
     * </ul>
     */
    PER("PER", "<PER>", "</PER>", "B-PER", "I-PER", 1, Color.CYAN),

    /**
     * LOC entity has fields:
     * <ul>
     *      <li><code>name</code>: LOC</li>
     *      <li><code>start tag</code>: &lt;LOC&gt;</li>
     *      <li><code>end tag</code>: &lt;/LOC&gt;</li>
     *      <li><code>b-tag</code>: &&B-LOC</li>
     *      <li><code>i-tag</code>: &&I-LOC</li>
     *      <li><code>number</code>: 2</li>
     * </ul>
     */
    LOC("LOC", "<LOC>", "</LOC>", "B-LOC", "I-LOC", 2, Color.LIGHT_GRAY),

    /**
     * ORG entity has fields:
     * <ul>
     *      <li><code>name</code>: ORG</li>
     *      <li><code>start tag</code>: &lt;ORG&gt;</li>
     *      <li><code>end tag</code>: &lt;/ORG&gt;</li>
     *      <li><code>b-tag</code>: &&B-ORG</li>
     *      <li><code>i-tag</code>: &&I-ORG</li>
     *      <li><code>number</code>: 3</li>
     * </ul>
     */
    ORG("ORG", "<ORG>", "</ORG>", "B-ORG", "I-ORG", 3, Color.MAGENTA),

    /**
     * POS entity has fields:
     * <ul>
     *      <li><code>name</code>: POS</li>
     *      <li><code>start tag</code>: &lt;POS&gt;</li>
     *      <li><code>end tag</code>: &lt;/POS&gt;</li>
     *      <li><code>b-tag</code>: &&B-POS</li>
     *      <li><code>i-tag</code>: &&I-POS</li>
     *      <li><code>number</code>: 3</li>
     * </ul>
     */
    POS("POS", "<POS>", "</POS>", "B-POS", "I-POS", 4, Color.BLUE),
    
    /**
     * POS entity has fields:
     * <ul>
     *      <li><code>name</code>: POS</li>
     *      <li><code>start tag</code>: &lt;POS&gt;</li>
     *      <li><code>end tag</code>: &lt;/POS&gt;</li>
     *      <li><code>b-tag</code>: &&B-POS</li>
     *      <li><code>i-tag</code>: &&I-POS</li>
     *      <li><code>number</code>: 3</li>
     * </ul>
     */
    JOB("JOB", "<JOB>", "</JOB>", "B-JOB", "I-JOB", 5, Color.YELLOW),
    
    /**
     * POS entity has fields:
     * <ul>
     *      <li><code>name</code>: POS</li>
     *      <li><code>start tag</code>: &lt;POS&gt;</li>
     *      <li><code>end tag</code>: &lt;/POS&gt;</li>
     *      <li><code>b-tag</code>: &&B-POS</li>
     *      <li><code>i-tag</code>: &&I-POS</li>
     *      <li><code>number</code>: 3</li>
     * </ul>
     */
    DATE("DATE", "<DATE>", "</DATE>", "B-DATE", "I-DATE", 6, Color.ORANGE),

    /**
     * OTHER entity has fields:
     * <ul>
     *      <li><code>name</code>: O</li>
     *      <li><code>start tag</code>: <code>null</code></li>
     *      <li><code>end tag</code>: <code>null</code></li>
     *      <li><code>b-tag</code>: null</li>
     *      <li><code>i-tag</code>: null</li>
     *      <li><code>number</code>: 0</li>
     * </ul>
     */
    OTHER("O", null, null, null, null, 0, null),

    /**
     * NOENTITY entity has fields:
     * <ul>
     *      <li><code>name</code>: NOENTITY</li>
     *      <li><code>start tag</code>: <code>null</code></li>
     *      <li><code>end tag</code>: <code>null</code></li>
     *       <li><code>b-tag</code>: null</li>
     *      <li><code>i-tag</code>: null</li>
     *      <li><code>number</code>: -1</li>
     * </ul>
     */
    NOENTITY("NOENTITY", null, null, null, null, -1, Color.BLACK);

    private ENTITY(String name, String startTag, String endTag, String bTag,
            String iTag, int number, Color color) {
        this.name = name;
        this.startTag = startTag;
        this.endTag = endTag;
        this.number = number;
        this.bTag = bTag;
        this.iTag = iTag;
        this.color = color;
    }

    /**
     * Check if entityName is one of ENTITY: PER, LOC, ORG... then return ENTITY
     * @param entityName name of entity want to get.
     * @return Return <code>ENTITY</code> which has name is entityName. Return <code>
     * NOENTITY</code> if has no any <code>ENTITY</code> has name is entityName
     */
    public static ENTITY getEntity(String entityName) {
        try {
            return valueOf(entityName.toUpperCase());
        } catch (Exception e) {
            return NOENTITY;
        }
    }

    /**
     * Get b-tag of an ENTITY. Ex: B-PER
     * @return b-tag of an ENTITY
     */
    public String getBTag() {
        return bTag;
    }

    /**
     * Get i-tag of an ENTITY. Ex: I-PER
     * @return i-tag of an ENTITY
     */
    public String getITag() {
        return iTag;
    }

    /**
     * Check if input string is IOB tag
     * @param input String needs to check
     * @return Return <code>true</code> if is IOB tag, <code>false</code> if not
     */
    public static boolean isIOBFormat(String input) {
        if (input.equals(ENTITY.PER.getBTag()) || input.equals(ENTITY.PER.getITag())
                || input.equals(ENTITY.LOC.getBTag()) || input.equals(ENTITY.LOC.getITag())
                || input.equals(ENTITY.ORG.getBTag()) || input.equals(ENTITY.ORG.getITag())
                || input.equals(ENTITY.POS.getBTag()) || input.equals(ENTITY.POS.getITag())
                || input.equals(ENTITY.JOB.getBTag()) || input.equals(ENTITY.JOB.getITag())
                || input.equals(ENTITY.DATE.getBTag()) || input.equals(ENTITY.DATE.getITag())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get name of an ENTITY. Can use ENTITY.PER.getName() instead of ENTITY.PER.toString()
     * @return name of an ENTITY
     */
    public String getName() {
        return name;
    }

    /**
     * Get start tag of an ENTITY. Ex: get start tag of PER entity is: &lt;PER&gt;
     * @return start tag of an ENTITY
     */
    public String getStartTag() {
        return startTag;
    }

    /**
     * Get end tag of an ENTITY. Ex: get end tag of PER entity is: &lt;/PER&gt;
     * @return End tag of an ENTITY
     */
    public String getEndTag() {
        return endTag;
    }

    /**
     * Get length of name ENTITY.
     * @return length of name ENTITY
     */
    public int getLength() {
        return name.length();
    }

    /**
     * Get length of start tag
     * @return length of start tag
     */
    public int getStartLength() {
        return startTag.length();
    }

    /**
     * Get length of end tag
     * @return length of end tag
     */
    public int getEndLength() {
        return endTag.length();
    }

    /**
     * Get number index of ENTITY
     * @return Number index of ENTITY
     */
    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return name;
    }
    private String name;
    private String startTag;
    private String endTag;
    private String bTag;
    private String iTag;
    private int number;
    private Color color;

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }
}
