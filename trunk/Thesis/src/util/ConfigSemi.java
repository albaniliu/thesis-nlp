/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 * Luu cac thong so phuc vu cho semi nhu: so luong bag, so cau trong bag...
 * @author banhbaochay
 */
public class ConfigSemi {

    /**
     * Tao default config
     */
    public ConfigSemi() {
        B = 5;
        bagSize = 120;
        C = 2;
        S = 80;
        threshold = 0.227;
    }// end constructor

    /**
     * Tao config tu 1 file
     * @param configFile
     */
    public ConfigSemi(String configFile) {
    }// end constructor

    // So luong cau trong 1 bag
    private int bagSize;

    /**
     * Get the value of bagSize
     *
     * @return the value of bagSize
     */
    public int getBagSize() {
        return bagSize;
    }

    /**
     * Set the value of bagSize
     *
     * @param bagSize new value of bagSize
     */
    public void setBagSize(int bagSize) {
        this.bagSize = bagSize;
    }
    
    // So phan lop
    private int C;

    /**
     * Get the value of C
     *
     * @return the value of C
     */
    public int getC() {
        return C;
    }

    /**
     * Set the value of C
     *
     * @param C new value of C
     */
    public void setC(int C) {
        this.C = C;
    }
    
    // So luong cau trong 1 lan them vao tap train
    private int S;

    /**
     * Get the value of S
     *
     * @return the value of S
     */
    public int getS() {
        return S;
    }

    /**
     * Set the value of S
     *
     * @param S new value of S
     */
    public void setS(int S) {
        this.S = S;
    }
    
    // Nguong entropy, chi lay entropy nho hon nguong
    private double threshold;

    /**
     * Get the value of threshold
     *
     * @return the value of threshold
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Set the value of threshold
     *
     * @param threshold new value of threshold
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    
    // So luong bag
    private int B;

    /**
     * Get the value of B
     *
     * @return the value of B
     */
    public int getB() {
        return B;
    }

    /**
     * Set the value of B
     *
     * @param B new value of B
     */
    public void setB(int B) {
        this.B = B;
    }

}// end ConfigSemi class

