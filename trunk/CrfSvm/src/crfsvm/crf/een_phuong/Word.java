/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

/**
 * Luu cac thong tin va phuong thuc tac dong len 1 tu trong cau
 * @author banhbaochay
 */
public class Word {

    /**
     * 
     * @param form
     */
    public Word(String form) {
        this.form = form;
    }

    public Word(StringBuilder form) {
        this.form = form.toString();
    }
    
    /**
     * Ban than tu
     */
    private String form;
    /**
     * Vi tri cua tu trong cau
     */
    private int offset;

    /**
     * @return the form
     */
    public String getForm() {
        return form;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}// end Word class

