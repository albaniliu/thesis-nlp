/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package context;

import java.util.ArrayList;
import java.util.List;
import util.ConvertText;
import util.Sentence;
import util.Word;

/**
 * This context use window size is 5 (2 word before and 2 word after)
 * @author banhbaochay
 */
public class DefaultContext extends AbstractContext {

    public static final int DEFAULT_WINDOW_SIZE = 5;

    public DefaultContext(Word word, Sentence sentence) {
        this(word, sentence, DEFAULT_WINDOW_SIZE);
        this.windowSize = DEFAULT_WINDOW_SIZE;
    }

    public DefaultContext(Word word, Sentence sentence, int windowSize) {
        this.windowSize = windowSize;
        wordContextList = new ArrayList<Word>();
        Word[] wordArray = new Word[windowSize];
        int currentOffset = word.getOffset();
        int middleOfArray = (windowSize - 1) / 2;
        for (int i = 0; i < windowSize; i++) {
            if (i == middleOfArray) {
                wordArray[i] = word;
            } else {
                wordArray[i] = sentence.wordAt(currentOffset + (i - middleOfArray));
            }
            wordContextList.add(wordArray[i]);
        }
    }

    public int getWindowSize() {
        return windowSize;
    }

    public List<Word> getWordContext() {
        return wordContextList;
    }

    private int windowSize;
    private List<Word> wordContextList;

}
