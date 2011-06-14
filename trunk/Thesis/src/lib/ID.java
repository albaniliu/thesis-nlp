/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lib;

/**
 *
 * @author banhbaochay
 */
public class ID {

    public ID() {
        this.count = 0;
        this.lineNumber = 0;
    }

    public ID(ID id) {
        this.count = id.count;
        this.lineNumber = id.lineNumber;
    }

    public ID(int lineNumber, int count) {
        this.lineNumber = lineNumber;
        this.count = count;
    }

    public ID(String lineNumberAndCount) {
        String[] elements = lineNumberAndCount.split("-");
        this.lineNumber = Integer.parseInt(elements[0]);
        this.count = Integer.parseInt(elements[1]);
    }

    @Override
    protected ID clone() {
        ID id = new ID();
        id.count = count;
        id.lineNumber = lineNumber;
        return id;
    }// end clone method

    public ID replaceBy(String lineNumberAndCount) {
        ID id = new ID();
        String[] elements = lineNumberAndCount.split("-");
        id.setLineNumber(Integer.parseInt(elements[0]));
        id.setCount(Integer.parseInt(elements[1]));
        return id;
    }

    public ID replaceBy(ID newId) {
        ID id = new ID();
        id.setLineNumber(newId.getLineNumber());
        id.setCount(newId.getCount());
        return id;
    }
    
    /**
     * Cong 2 ID, ket qua tra ve luu o ID goi ham`
     * @param id
     * @return 
     */
    public ID plus(ID id) {
        lineNumber += id.lineNumber;
        count += id.count;
        return this;
    }// end plus method
    
    /**
     * Cong 2 ID, ket qua tra ve la ID moi
     * @param id1
     * @param id2
     * @return 
     */
    public static ID plus(ID id1, ID id2)  {
        return new ID(id1.lineNumber + id2.lineNumber, id1.count + id2.count);
    }// end static plus method

    /**
     * Tru 2 ID
     * @param id
     * @return 
     */
    public ID minus(ID id) {
        lineNumber -= id.lineNumber;
        count -= id.count;
        if (lineNumber < 0) {
            lineNumber = 0;
        }// end if lineNumber < 0
        if (count < 0) {
            count = 0;
        }// end if count < 0
        return this;
    }// end plus method
    
    public static ID minus(ID id1, ID id2) {
        ID result = new ID();
        result.lineNumber = (id1.lineNumber - id2.lineNumber < 0) ? id1.lineNumber - id2.lineNumber : 0;
        result.count = (id1.count - id2.count < 0) ? id1.count - id2.count : 0;
        return result;
    }
    
    /**
     * @return <code>true</code> if lineNumber and count of 2 ID are the same, <code>
     * false</code> if not
     */
    public boolean compareTo(ID id) {
        if (this.lineNumber == id.lineNumber && this.count == id.count) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tang count cua ID len 1
     * @return
     */
    public ID increase() {
        ID id = new ID();
        id.lineNumber = lineNumber;
        id.setCount(count++);
        return id;
    }

    /**
     * Only compare line number of 2 ID
     * @return <code>true</code> if lineNumber of 2 ID are the same, <code>
     * false</code> if not
     */
    public boolean compareLineNumberTo(ID id) {
        if (this.lineNumber == id.lineNumber) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lineNumber);
        sb.append("-");
        sb.append(count);
        return sb.toString();
    }

    private int lineNumber;
    private int count;

    /**
     * @return the number of line in normal text
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the id in jsre format text
     */
    public int getCount() {
        return count;
    }

    /**
     * @param id the id to set
     */
    public void setCount(int id) {
        this.count = id;
    }
}
