/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.itc.irst.tcc.sre.data.context;

import org.itc.irst.tcc.sre.data.Sentence;
import org.itc.irst.tcc.sre.data.SentenceFilter;

/**
 *
 * @author banhbaochay
 */
public class WordContext  implements  SentenceFilter {

    public WordContext() {
        this(0);
    }

    public WordContext(int windowSize) {
        this.windowSize = windowSize;
    }


    public Sentence filter(Sentence sent) {
        return filter(sent, 0);
    }

    public Sentence filter(Sentence sent, int offset) {
        int start = offset - windowSize;
        int end = offset + windowSize;
        if (start < 0) {
            start = 0;
        }// end if start
        if (end > sent.length()) {
            end = sent.length();
        }// end if end
        return sent.fragment(start, end);
    }// end filter method

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    private int windowSize;

}
