/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

/**
 * Luu thong tin ve ban than tu va vi tri cua tu trong cau. Su dung cho lop TaggedDocument
 * @author banhbaochay
 */
public class Word {

    /**
     * 
     * @param form
     */
    public Word(String form) {
        this.form = form;
        iob = "O";
        offset = -1;
    }

    public Word(StringBuilder form) {
        this.form = form.toString();
        iob = "O";
        offset = -1;
    }
    
    @Override
    public String toString() {
        return form;
    }// end print method
    
    /**
     * Ban than tu
     */
    private String form;
    /**
     * Vi tri cua tu trong cau
     */
    private int offset;
    
    /**
     * Nhan duoc gan cho tu: B-per, I-per...
     */
    private String iob;

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

    /**
     * @return the label
     */
    public String getIob() {
        return iob;
    }

    /**
     * @param iobLabel the label to set
     */
    public void setIob(String iobLabel) {
        this.iob = iobLabel;
    }
    
    /**
     * Tra ve true neu tu duoc gan nhan IOB va nguoc lai
     * @return 
     */
    public boolean isIOB() {
        return !iob.equals("O");
    }// end isIOB method
}// end Word class

