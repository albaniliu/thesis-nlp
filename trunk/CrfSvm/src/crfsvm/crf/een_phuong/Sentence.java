/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Luu thong tin ve 1 cau, cu the la thong tin cua cac tu trong cau do va vi tri cua cau trong van ban.
 * Dung trong class TaggedDocument
 * @author banhbaochay
 */
public class Sentence {

    public Sentence() {
        wordList = new LinkedList<Word>();
        offset = -1;
    }// end constructor

    /**
     * Lay ra 1 tu o vi tri i trong cau
     * @param i vi tri cua tu trong cau, bat dau tu 0
     * @return Tra ve null neu vi tri cua tu nam ngoai khoang cua cau, tra ve Word neu dung
     */
    public Word wordAt(int i) {
        return (i < 0 || i >= size()) ? null : wordList.get(i);
    }// end wordAt method

    /**
     * Them 1 tu vao cau
     * @param word 
     */
    public void addWord(Word word) {
        wordList.add(word);
    }// end addWord method
    
    /**
     * Lay ra 1 phan cua cau bat dau tu vi tri start den vi tri end - 1.
     * Neu start &lt; 0 thi se lay bat dau tu tu dau tien cua cau. Neu end &gt; size thi
     * se lay den het cau
     * @param start vi tri bat dau lay
     * @param end vi tri sau vi tri ket thuc
     * @return Tra ve null neu start &lt; end hoac start &gt; size hoac end &lt; 0
     */
    public Sentence fragment(int start, int end) {
        if (start < end && start < size() && end > 0) {
            if (start < 0) {
                start = 0;
            }// end if start < 0
            
            if (end > size()) {
                end = size();
            }// end if end > size()
            
            Sentence sent = new Sentence();
            for (int i = start; i < end; i++) {
                sent.addWord(wordAt(i));
            }// end for i
            return sent;
        } else {
            
            return null;
        }
    }// end fragment method
    
    /**
     * Tra ve list vi tri cac tu duoc gan nhan IOB trong cau cung voi nhan IOB duoc gan cho tu do
     * @return Moi 1 vi tri duoc bieu dien duoi dang: offsetSentence,offsetWord,nhan IOB cua tu
     */
    public List<String> getIobList() {
        List<String> offsetList = new ArrayList<String>();
        for (Word word : wordList) {
            if (word.isIOB()) {
                StringBuilder sb = new StringBuilder();
                sb.append(getOffset());
                sb.append("-");
                sb.append(word.getOffset());
                sb.append(",");
                sb.append(word.getIob());
                offsetList.add(sb.toString());
            }// end if word.isIOB()
        }// end foreach word
        return offsetList;
    }// end getIobList method

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Word word : wordList) {
            sb.append(word.toString());
            sb.append(" ");
        }// end foreach word
        return sb.toString();
    }// end toString method

    /**
     * Tra ve so luong tu trong cau
     * @return 
     */
    public int size() {
        return wordList.size();
    }// end size method
    
    /**
     * In ra theo dang IOB
     * @return 
     */
    public String toIobString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Word word : wordList) {
            sb.append(word.getForm());
            sb.append("\t");
            sb.append(word.getIob());
            if (i != wordList.size() - 1) {
                sb.append("\n");
            }
        }// end foreach word
        return sb.toString();
    }// end toIobString method
    
    /**
     * In cau voi moi tu duoc bao trong cap ky tu boundStart va boundEnd. Cac dau cham cau khong duoc bao boi cac ky tu
     * @param boundStart
     * @param boundEnd
     * @return 
     */
    public String toString(String boundStart, String boundEnd) {
        StringBuilder sb = new StringBuilder();
        for (Word word : wordList) {
            String wordForm = word.getForm();
            if (Punctuation.isPunct(wordForm)) {
                sb.append(wordForm);
                sb.append(" ");
            } else {
                sb.append(boundStart);
                sb.append(wordForm);
                sb.append(boundEnd);
                sb.append(" ");
            }
        }// end foreach word
        return sb.toString().trim();
    }// end toString method
    /**
     * list cac word trong cau
     */
    private List<Word> wordList;
    /**
     * So thu tu cua cau trong van ban
     */
    private int offset;
    /**
     * Toan bo cau
     */
    private String line;

    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @return the line
     */
    public String getLine() {
        return line;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}// end Sentence class

