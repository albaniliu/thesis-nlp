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
        startEntity = false;
        endEntity = false;
        label = "O";
    }

    public Word(StringBuilder form) {
        this.form = form.toString();
        iob = "O";
        offset = -1;
        startEntity = false;
        endEntity = false;
        label = "O";
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
    
    private boolean startEntity;
    
    private boolean endEntity;
    
    private String label;

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

    /**
     * @return the startEntity
     */
    public boolean isStartEntity() {
        return startEntity;
    }

    /**
     * @param startEntity the startEntity to set
     */
    public void setStartEntity(boolean startEntity) {
        this.startEntity = startEntity;
    }

    /**
     * @return the endEntity
     */
    public boolean isEndEntity() {
        return endEntity;
    }

    /**
     * @param endEntity the endEntity to set
     */
    public void setEndEntity(boolean endEntity) {
        this.endEntity = endEntity;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
}// end Word class

