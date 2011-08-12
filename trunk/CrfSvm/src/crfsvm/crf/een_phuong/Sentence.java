/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.crf.een_phuong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Luu thong tin ve 1 cau, cau dau vao co dang: [anh] &lt;per&gt; [Nguyen Van Nam] &lt;/per&gt; [dang] [song] [tai]...
 * Cau nay nam trong van ban dau vao cua CRF
 * @author banhbaochay
 */
public class Sentence {

    /**
     * 
     * @param line Cau co dang: [anh] &lt;per&gt; [Nguyen Van Nam] &lt;/per&gt; [dang] [song]..
     */
    public Sentence(String line) {
        wordList = new ArrayList<Word>();
        entityMap = new HashMap<String, ENTITY>();
        this.line = line;
        analyze();
    }

    private void analyze() {
        String[] arr = line.split(" ");
        int index = 0;
        StringBuilder offsetEntity = new StringBuilder();
        ENTITY entity = null;
        for (int i = 0; i < arr.length; i++) {
            StringBuilder wordForm = new StringBuilder();
            if (arr[i].startsWith("</")) {
                offsetEntity.append(index - 1);
                entityMap.put(offsetEntity.toString(), entity);
                offsetEntity.delete(0, offsetEntity.length());
                continue;
            }
            if (arr[i].startsWith("<")) {
                offsetEntity.append(index);
                offsetEntity.append("-");
                entity = ENTITY.getEntity(arr[i].replaceAll("<|>", ""));
                continue;
            }
            
            boolean next = true;
            while (next) {
                if (i == arr.length) {
                    break;
                }
                if (arr[i].endsWith("]")) {
                    next = false;
                    wordForm.append(arr[i]);
                    Word word = new Word(wordForm);
                    word.setOffset(index);
                    wordList.add(word);
                    index++;
                } else {
                    wordForm.append(arr[i]);
                    wordForm.append(" ");
                    i++;
                }
            }// end while next
        }// end for i
    }// end analyze method

    public Word getWordAt(int i) {
        return wordList.get(i);
    }// end getWordAt method
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
     * Map luu vi tri cac entity trong cau theo dang: key la start-end, value la ENTITY
     */
    private Map<String, ENTITY> entityMap;

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

    public static void main(String[] args) {
        String s = "[Giáo sư] [Tạ Quang Bửu] [là] [hiệu trưởng] [thứ] [2] [của] [trường] [Đại học] [Bách Khoa Hà Nội] . ";
        new Sentence(s);
    }// end main class
}// end Sentence class

