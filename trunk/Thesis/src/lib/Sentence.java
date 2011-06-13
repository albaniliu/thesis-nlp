/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import context.DefaultContext;
import feature.ENTITY;
import feature.Dictionary;
import feature.POS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class storage all informations of line string. It provides some methods for:
 * <ul>
 *     <li>Get a word at any offset in line</li>
 *     <li>Get the word before or after the word</li>
 *     <li>Get amount of words in line</li>
 *     <li>Print the original input line</li>
 * </ul>
 * @author banhbaochay
 */
public class Sentence {

    /**
     * Default constructor analyze POS, IOB, not dictionary type of words
     * @param line line has format: Anh &lt;PER&gt; Nguyen_Van_Nam &lt;/PER&gt; lam tai
     * &lt;ORG&gt; cong_ty VNPT &lt;/ORG&gt;
     */
    public Sentence(String line) {
        this(line, null);
    }

    /**
     * Constructor if wants to determine dictionary type of words
     * @param line line has format: Anh &lt;PER&gt; Nguyen_Van_Nam &lt;/PER&gt; lam tai
     * &lt;ORG&gt; cong_ty VNPT &lt;/ORG&gt;
     * @param dic An dictionary object, which is loaded when need to determine dictionary
     * type of word
     */
    public Sentence(String line, Dictionary dic) {
        wordList = new ArrayList<Word>();
        entityMap = new LinkedHashMap<Integer, Object[]>();
        analyzeSentence(line, dic);
        setPOS(line);
        setIOB();
        createTokenJSREMap();
    }

    /**
     * Private constructor for create instance of Sentence from fragment.
     * @param wordList
     * @param entityMap
     */
    private Sentence(List<Word> wordList, Map<Integer, Object[]> entityMap) {
        this.wordList = new ArrayList<Word>(wordList);
        this.entityMap = entityMap;
    }

    public void setContext(int windowSize) {
        for (Word word : wordList) {
            DefaultContext context = new DefaultContext(word, this, windowSize);
            word.setContext(context);
        }
    }

    /**
     * Create array of word in the sentence. Words are separated by one or more spaces
     * @param line line has format: Anh &lt;PER&gt; Nguyen_Van_Nam &lt;/PER&gt; lam tai &lt;ORG&gt; cong_ty VNPT &lt;/ORG&gt;
     * @param dic Dictionary for determining dic type of words
     */
    private void analyzeSentence(String line, Dictionary dic) {
        /* line has format: Anh <PER> Nguyen_Van_Nam </PER> lam tai <ORG> cong_ty VNPT </ORG> */
        String[] words = line.split("\\s+");
        int beginOffset = -1;
        int endOffset = -1;
        int offset = 0;
        boolean mark = false; //true if words[] is begin tag
        int markID = -1;// id of the word at marked offset


        for (int i = 0; i < words.length; i++) {
            if (words[i].startsWith("</")) {
                /* end an entity type: </PER> */
                endOffset = i;

                if (beginOffset != -1 && endOffset - beginOffset > 1) {
                    /* filter entity name */
                    String entityName = words[i].substring(2, words[i].length() - 1);
                    ENTITY entity = ENTITY.getEntity(entityName);

                    /* Add to entityMap */
                    entityMap.put(markID, new Object[]{entity, markID, offset - 1});
                }

                beginOffset = -1;

            } else if (words[i].startsWith("<")) {
                /* begin an entity type: <PER> */
                beginOffset = i;
                mark = true;
            } else {
                /* normal word */
                Word word = (dic != null) ? new Word(words[i], offset, dic) : new Word(words[i], offset);
                wordList.add(word);
                if (mark) {
                    markID = offset;
                    mark = false;
                }
                offset++;
            }
        }
    }

    /**
     * Set POS for all words in sentence
     */
    private void setPOS(String line) {
        line = ConvertText.removeTag(line);
        // now line only is segmented string
        line = ConvertText.vnTagger(line, ConvertText.ONLY_TAG);
        String[] words = line.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String[] array = words[i].split("/");
            /* array: word - POS */
            String pos = array[1];
            wordList.get(i).setPOS(POS.getPOS(pos));
        }
    }

    /**
     * Set IOB for all words in entity
     */
    private void setIOB() {
        Set<Map.Entry<Integer, Object[]>> entrySet = entityMap.entrySet();
        Iterator<Map.Entry<Integer, Object[]>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Object[]> entry = it.next();
            Object[] value = entry.getValue();
            /* value is an array object: entity type - begin offset - end offset */
            ENTITY entity = (ENTITY) value[0];
            int beginWord = (Integer) value[1];
            int endWord = (Integer) value[2];
            for (int i = beginWord; i <= endWord; i++) {
                if (i == beginWord) {
                    wordList.get(i).setIOB(entity, Word.B_TAG);
                } else {
                    wordList.get(i).setIOB(entity, Word.I_TAG);
                }
            }
        }
    }

    /**
     * Get entities map which is extracted from line. Map has Integer type for key and
     * an array Object for value.
     * <ul>
     *      <li>key: is ID and begin offset of word where entity starts</li>
     *      <li>value an array Object contains:
     *           <ul>
     *               <li>entity type: an entity object</li>
     *               <li>begin offset: an integer shows the offset of word where entity starts</li>
     *               <li>end offset: an integer shows the offset of word where entity ends</li>
     *          </ul>
     *      </li>
     * </ul>
     * @return A map stores informations about all entities in line
     */
    public Map<Integer, Object[]> getEntityMap() {
        return entityMap;
    }

    /**
     * Get word (Word type) which has offset is offset param in line
     * @param offset Offset of word want to get. Offset is based on zero
     * @return Word at offset. Return <code>null</code> if offset is less than zero or
     * greater than amount of word in this sentence
     */
    public Word wordAt(int offset) {
        if (offset < 0 || offset >= size()) {
            return null;
        } else {
            return wordList.get(offset);
        }
    }

    /**
     * Create small Sentence from other Sentence, from beginOffset to end
     * @param beginOffset
     * @return
     */
    public Sentence fragment(int beginOffset) {
        return fragment(beginOffset, wordList.size());
    }

    /**
     * Create small Sentence from other Sentence, from beginOffset to endOffset - 1
     * @param beginOffset
     * @param endOffset
     * @return
     */
    public Sentence fragment(int beginOffset, int endOffset) {
        return new Sentence(wordList.subList(beginOffset, endOffset + 1), entityMap);
    }

    /**
     * Get amount of words in this sentence
     * @return Amount of words in this sentence
     */
    public int size() {
        return wordList.size();
    }

    // <editor-fold defaultstate="collapsed" desc="to String">
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordList.size(); i++) {
            sb.append(wordList.get(i).getForm());
            if (i != wordList.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    // </editor-fold>

    public String toJSRE() {
        StringBuilder sb = new StringBuilder();
        int tokenID = 0;
        String token = null;
        String lemma = null;
        String POS = null;
        String entityType = null;
        String entityLabel = null;

        for (int i = 0; i < size(); i++) {
            if (entityMap.containsKey(i)) {
                /*
                 * word is the begin of entity
                 */
                Object[] values = entityMap.get(i);
                ENTITY entity = (ENTITY) values[0];
                int beginOffset = (Integer) values[1];
                int endOffset = (Integer) values[2];
                token = fragment(beginOffset, endOffset).toString().replaceAll(" ", "_");
                POS = "Np";
                entityType = entity.getName();
                entityLabel = "O";
                i += (endOffset - beginOffset);
            } else {
                /*
                 * word is normal word
                 */
                Word word = wordAt(i);
                token = word.getForm();
                POS = word.getPOS().getName();
                if (POS.equals("Unknown")) {
                    // khi token chi la dau cham cau
                    POS = token;
                }
                entityType = "O";
                entityLabel = "O";
            }// end if
            lemma = token;

            sb.append(tokenID);
            sb.append("&&");
            sb.append(token);
            sb.append("&&");
            sb.append(lemma);
            sb.append("&&");
            sb.append(POS);
            sb.append("&&");
            sb.append(entityType);
            sb.append("&&");
            sb.append(entityLabel);
            sb.append(" ");

            tokenID++;

        }// end for i
        return sb.toString();
    }// end toJSRE method

    public static void main(String[] args) throws Exception {
        String s = "Anh    <PER>Nguyen Van Nam</PER> sống tại <ORG>công ty VNPT</ORG>";
        String s1 = "<org> [Công ty] [Toyota] </org> [sẽ] [cực lực] [bảo vệ] [thành tích] [an toàn]";
        String s2 = "[Ban đầu] , [gia đình] [anh] [sống] [ở] [thành phố] <loc> [Ancona] </loc> [ở] "
                + "<loc> [Ý] </loc> , [rồi] [chuyển] [sang] <loc> [Argentina] </loc> [vào] [năm] [1983]";
        String s3 = "<org> [Công ty] [Toyota] </org> [sẽ] [cực lực] [bảo vệ] [thành tích] [an toàn] , [chống]";
        Sentence sen = new Sentence(ConvertText.convertForSentence(s2));
//        for (Word w : sen.wordList) {
//            System.out.print(w.getForm() + "-" + w.getIOB() + "-" + w.getPOS());
//            System.out.println();
//        }
        System.out.println(sen.toJSRE());

    }

    /* private member */
    /**
     * List of words in Sentence, based on zero
     */
    private ArrayList<Word> wordList;
    /**
     * Map of entities in line. Map has format:
     * <ul>
     *      <li>key: is ID and begin offset of word where entity starts</li>
     *      <li>value an array Object contains:
     *           <ul>
     *               <li>entity type: an entity object</li>
     *               <li>begin offset: an integer shows the offset of word where entity starts</li>
     *               <li>end offset: an integer shows the offset of word where entity ends</li>
     *          </ul>
     *      </li>
     * </ul>
     */
    private Map<Integer, Object[]> entityMap;
    /**
     * Chi ra lien he giua vi tri offset voi vi tri token trong dinh dang JSRE
     * index - value. Trong do:
     * <ul>
     *    <li>index: chi so offset cua tu bat dau la entity</li>
     *    <li>value: chi so token cua entity trong dinh dang JSRE</li>
     * </ul>
     */
    private Map<Integer, Integer> tokenJSREMap;

    private void createTokenJSREMap() {
        tokenJSREMap = new HashMap<Integer, Integer>();
        int count = 0;
        for (int i = 0; i < size(); i++) {
            if (entityMap.containsKey(i)) {
                Object[] values = entityMap.get(i);
                int beginOffset = (Integer) values[1];
                int endOffset = (Integer) values[2];
                tokenJSREMap.put(i, count);
                count ++;
                i += endOffset - beginOffset;
            } else {
                count++;
            }// end if
        }// end for i
    }// end createTokenJSREMap method

    public int getTokenID(int beginOffset) {
        if (tokenJSREMap.containsKey(beginOffset)) {
            return tokenJSREMap.get(beginOffset);
        } else {
            return -1;
        }
    }
}
