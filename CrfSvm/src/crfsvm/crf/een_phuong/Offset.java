/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

/**
 * Dung de dinh nghia vi tri cua 1 tu hoac cum tu trong van ban
 * @author banhbaochay
 */
public class Offset implements Comparable<Offset> {

    /**
     * Tao doi tuong Offset tu chuoi offset co dang: vi tri cau - vi tri tu
     * @param offset 
     */
    public Offset(String offset) {
        int offsetSent = Integer.parseInt(offset.split("-")[0]);
        int offsetWord = Integer.parseInt(offset.split("-")[1]);
        this.offsetSent = offsetSent;
        this.offsetWord = offsetWord;
        this.offsetWordEnd = offsetWord;
    }// end constructor

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Offset other = (Offset) obj;
        if (this.offsetSent != other.offsetSent) {
            return false;
        }
        if (this.offsetWord != other.offsetWord) {
            return false;
        }
        if (this.offsetWordEnd != other.offsetWordEnd) {
            return false;
        }
        return true;
    }// end equals method

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.offsetSent;
        hash = 11 * hash + this.offsetWord;
        hash = 11 * hash + this.offsetWordEnd;
        return hash;
    }// end hashCode method

    /**
     * Tao doi tuong offset cho vi tri cua 1 tu trong van ban
     * @param offsetSent Vi tri cau trong van ban
     * @param offsetWord Vi tri tu trong van ban
     */
    public Offset(int offsetSent, int offsetWord) {
        this.offsetSent = offsetSent;
        this.offsetWord = offsetWord;
        this.offsetWordEnd = offsetWord;
    }// end constructor 2 args

    /**
     * Tao doi tuong offset cho vi tri 1 cum tu trong van ban
     * @param offsetSent Vi tri cau trong van ban
     * @param offsetWord Vi tri tu dau tien trong cum tu
     * @param offsetWordEnd Vi tri tu ket thuc trong cum tu
     */
    public Offset(int offsetSent, int offsetWord, int offsetWordEnd) {
        this.offsetSent = offsetSent;
        this.offsetWord = offsetWord;
        this.offsetWordEnd = offsetWordEnd;
    }// end constructor 3 args
    /**
     * Vi tri cau trong van ban
     */
    private int offsetSent;
    /**
     * Vi tri tu trong cau
     */
    private int offsetWord;
    /**
     * Vi tri ket thuc cum tu trong cau
     */
    private int offsetWordEnd;

    /**
     * @return the offsetSent
     */
    public int getSent() {
        return offsetSent;
    }

    /**
     * @return the offsetWord
     */
    public int wordStart() {
        return offsetWord;
    }

    /**
     * @return the offsetWordEnd
     */
    public int wordEnd() {
        return offsetWordEnd;
    }

    /**
     * So sanh offset
     * @param o
     * @return Tra ve -1 neu vi tri cau nho hon hoac cung cau nhung vi tri tu nho hon,
     * tra ve 1 neu vi tri cau lon hon hoac cung cau va vi tri tu lon hon, tra ve 0 neu 2 offset giong nhau
     */
    @Override
    public int compareTo(Offset o) {
        if (getSent() < o.getSent()) {
            return -1;
        } else if (getSent() == o.getSent()) {
            if (wordStart() < o.wordStart()) {
                return -1;
            } else if (wordStart() == o.wordStart()) {
                if (wordEnd() < o.wordEnd()) {
                    return -1;
                } else if (wordEnd() == o.wordEnd()) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }// end compareTo method

    /**
     * So sanh 2 offset co thuoc cung 1 cau khong
     * @param o
     * @return Tra ve 0 neu thuoc cung 1 cau, nho hon 0 neu offset o truoc va lon hon 0 neu o sau
     */
    public int compareSent(Offset o) {
        return new Integer(offsetSent).compareTo(o.offsetSent);
    }// end compareSent method

    public static Offset createOffset(Offset startOffset, Offset endOffset) {
        int offsetSent = startOffset.getSent();
        int start = startOffset.wordStart();
        int end = endOffset.wordEnd();
        return new Offset(offsetSent, start, end);
    }// end createOffset method

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(offsetSent);
        sb.append(":");
        sb.append(offsetWord);
        sb.append("-");
        sb.append(offsetWordEnd);
        return sb.toString();
    }// end toString method
}// end Offset class

