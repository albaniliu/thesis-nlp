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
    protected ID clone() throws CloneNotSupportedException {
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
