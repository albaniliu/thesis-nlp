/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Provides methods for store and extract information from a line (JSRE format)
 * @author banhbaochay
 */
public class JSRELine {
    static Logger logger = Logger.getLogger(JSRELine.class);
    /**
     * Default constructor, all fields are set to default value. <code>label</code> is 0,
     * <code>id</code> is a default instance of ID class, <code>body</code> is empty string
     */
    protected JSRELine() {
        this.label = 0;
        this.id = new ID();
        this.body = "";
    }

    /**
     * Constructor to create an instance of JSRELine class. It stores all information of line
     * @param line String has 3 field which are separated by tab
     * @throws IllegalArgumentException if line is not legal for JSRE format
     */
    public JSRELine(String line) {
        String[] elements = line.split("\t+");
        if (elements.length != 3) {
            throw new IllegalArgumentException("Line in document is illegal for JSRE format");
        } else {
            this.label = Integer.parseInt(elements[0]);
            this.id = new ID(elements[1]);
            this.body = elements[2];
            createTokenMap();
        }
    }

    /**
     * Kiem tra cau dua vao co phai theo dinh dang JSRE khong
     * @param line
     * @return 
     */
    public static boolean checkJSREFormat(String line) {
        String[] elements = line.split("\t+");
        if (elements.length != 3) {
            logger.error("Example khong chia thanh 3 phan ngan cach boi dau tab");
            return false;
        } else {
            try {
                int classify = Integer.parseInt(elements[0]);
                if (classify != 0 && classify != 1) {
                    logger.error("Class cua example khong phai la 0 hoac 1");
                    return false;
                }// end if classify != 0 || classify != 1
                if (!elements[1].matches("[1-9][0-9]*-[1-9][0-9]*")) {
                    logger.error("ID cua cau khong dung");
                    return false;
                } else {
                    String[] body = elements[2].split("\\s+");
                    int i = 0;
                    for (String token : body) {
                        String[] child = token.split("&&");
                        if (child.length != 6) {
                            logger.error("Token thu " + i + " cua cau khong du 6 thanh phan");
                            return false;
                        } else {
                            int countToken = Integer.parseInt(child[0]);
                            if (countToken != i) {
                                logger.error("Token thu " + i + " cua cau sai tokenID");
                                return false;
                            }// end if countToken != i
                            i++;
                        }// end if child.length != 6
                    }// end for token
                }// end if elements[1].matches("[1-9][0-9]*-[1-9][0-9]*")
            } catch (NumberFormatException numberFormatException) {
                logger.error("Class cua example hoac tokenID cua body khong phai la so");
                return false;
            }
            return true;
        }// end if elements.length != 3
    }// end checkJSREFormat method
    
    /**
     * Tao tokenMap tu body cua cau
     */
    private void createTokenMap() {
        tokenMap = new LinkedHashMap<Integer, String[]>();
        String[] arr = body.split("\\s+");
        for (String element : arr) {
            /*
             * element co dinh dang: tokenID&&token&&lemma&&POS&&entityType&&entityLabel
             */
            String[] values = element.split("&&");
            tokenMap.put(Integer.parseInt(values[0]), values);
        }// end for element
    }// end createTokenMap method

    /**
     * @return the whole line as: label \t id \t body
     */
    public String getWholeLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(label);
        sb.append("\t");
        sb.append(id);
        sb.append("\t");
        sb.append(getBody());
        return sb.toString();
    }

    /**
     * @return the whole line as: label \t id \t body
     */
    @Override
    public String toString() {
        return getWholeLine();
    }

    private int label;
    private ID id;
    private String body;
    /**
     * tokenMap luu tung thanh phan trong body. Cac thanh phan trong body ngan cach
     * boi khoang trang
     */
    private Map<Integer, String[]> tokenMap;

    /**
     * @return Label of line
     */
    public int getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(int label) {
        this.label = label;
    }

    /**
     * @return An instance of ID class in line
     */
    public ID getId() {
        return id.clone();
    }

    /**
     * Set to <code>id</code> of line
     * @param id An instance of ID class
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Get body string of line
     * @return body string of JSRE format
     */
    public String getBody() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String[]> entry : tokenMap.entrySet()) {
            String[] values = entry.getValue();
            sb.append(values[0]);
            sb.append("&&");
            sb.append(values[1]);
            sb.append("&&");
            sb.append(values[2]);
            sb.append("&&");
            sb.append(values[3]);
            sb.append("&&");
            sb.append(values[4]);
            sb.append("&&");
            sb.append(values[5]);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Get <code>line number</code> from <code>id</code> of line
     * @return line number from id
     */
    public int getLineNumber() {
        return this.id.getLineNumber();
    }

    /**
     * Set to <code>lineNumber</code> of <code>id</code> of line
     * @param lineNumber An integer greater than 0
     */
    public void setLineNumber(int lineNumber) {
        this.id.setLineNumber(lineNumber);
    }

    /**
     * Set <code>count</code> of <code>id</code> of line
     * @param count An integer greater than 0
     */
    public void setCount(int count) {
        this.id.setCount(count);
    }

    /**
     * Get <code>count</code> from <code>id</code> of line
     * @return <code>count</code> from <code>id</code> of line
     */
    public int getCount() {
        return this.id.getCount();
    }

    public void setEntityLabel(int tokenID, String entityLabel) {
        String[] values = tokenMap.get(tokenID);
        values[5] = entityLabel;
        tokenMap.put(tokenID, values);

    }

    @Override
    protected JSRELine clone() {
        JSRELine copy = new JSRELine();
        copy.body = body;
        copy.id = id.clone();
        copy.label = label;
        for (Map.Entry<Integer, String[]> entry : tokenMap.entrySet()) {
            copy.tokenMap.put(entry.getKey(), entry.getValue());
        }// end foreach entry
        return copy;
    }

}