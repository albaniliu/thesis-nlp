/*
 * Copyright 2005 FBK-irst (http://www.fbk.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.itc.irst.tcc.sre.kernel.expl;

import org.apache.log4j.Logger;
import java.util.*;
import org.itc.irst.tcc.sre.data.Sentence;
import org.itc.irst.tcc.sre.data.Word;
import org.itc.irst.tcc.sre.data.context.ForeBetweenContext;
import org.itc.irst.tcc.sre.data.context.BetweenContext;
import org.itc.irst.tcc.sre.data.context.BetweenAfterContext;
import org.itc.irst.tcc.sre.data.context.WordContext;
import org.itc.irst.tcc.sre.util.Vector;
import org.itc.irst.tcc.sre.util.SparseVector;
import org.itc.irst.tcc.sre.util.FeatureIndex;
import org.itc.irst.tcc.sre.util.Orthographic;

/**
 * TO DO
 *
 * @author 	Claudio Giuliano
 * @version %I%, %G%
 * @since		1.0
 */
public class GlobalContextMapping extends AbstractMapping implements ContextMapping {

    /**
     * Define a static logger variable so that it references the
     * Logger instance named <code>GlobalContextMapping</code>.
     */
    static Logger logger = Logger.getLogger(GlobalContextMapping.class.getName());
    //
    private int ngram;
    //default = 3
    private static final int NUMBER_OF_SUBSPACES = 1;
    //
    public static final int DEFAULT_NGRAM = 3;
    //
    private static final int FORE_BETWEEN_SPACE = 0;
    //
    private static final int BETWEEN_SPACE = 1;
    //
    private static final int BETWEEN_AFTER_SPACE = 2;
    //
    private ForeBetweenContext foreBetweenContext;
    //
    private BetweenContext betweenContext;
    //
    private BetweenAfterContext betweenAfterContext;
    /*
     * Dung
     */
    private int mode;

    //
    public GlobalContextMapping() {
        logger.debug("GlobalContextMapping");

        this.ngram = DEFAULT_NGRAM;

        foreBetweenContext = ForeBetweenContext.getInstance();
        betweenContext = BetweenContext.getInstance();
        betweenAfterContext = BetweenAfterContext.getInstance();

        //logger.debug("GlobalContextMapping");
        //logger.debug("ngram: " + ngram);

    } // end constructor

    //
    public void setParameters(Properties parameters) {
        try {
            mode = Integer.parseInt(parameters.getProperty("mode"));
        } catch (NumberFormatException numberFormatException) {
            mode = 0;
        }
        logger.debug("GlobalContextMapping.setParameters");
        String n = parameters.getProperty("n-gram");
        if (n != null) {
            ngram = Integer.parseInt(n);
        }

        logger.debug("n-gram: " + ngram);
    } // end setParameters

    //
    public int subspaceCount() {
        // default la return NUMBER_OF_SUBSPACES
        return 1;
    } // end subspaceCount

    //
    public Vector[] map(Object x, Object id, FeatureIndex[] index) throws IllegalArgumentException {
        //logger.debug("GlobalContextMapping.map");

        boolean b = (x instanceof Sentence);
        if (!b) {
            throw new IllegalArgumentException();
        }

        Sentence sent = (Sentence) x;
        Vector[] subspaces = new Vector[NUMBER_OF_SUBSPACES];

        /*
         * Dung
         */
        Sentence fore = foreBetweenContext.filter(sent);
        Sentence between = betweenContext.filter(sent);
        Sentence after = betweenAfterContext.filter(sent);
        switch (mode) {
            case 0:
                /*
                 * GC
                 */
                subspaces[FORE_BETWEEN_SPACE] = createSubspace(fore, index[FORE_BETWEEN_SPACE]);
                subspaces[BETWEEN_SPACE] = createSubspace(between, index[BETWEEN_SPACE]);
                subspaces[BETWEEN_AFTER_SPACE] = createSubspace(after, index[BETWEEN_AFTER_SPACE]);
                break;
            case 1:
                /*
                 * GC4: default co ca 3 dong nay, arg dau tien la fore
                 */
                subspaces[FORE_BETWEEN_SPACE] = createSubspace4(sent, index[FORE_BETWEEN_SPACE], FORE_BETWEEN_SPACE);
//                subspaces[BETWEEN_SPACE] = createSubspace4(between, index[BETWEEN_SPACE], BETWEEN_SPACE);
//                subspaces[BETWEEN_AFTER_SPACE] = createSubspace4(after, index[BETWEEN_AFTER_SPACE], BETWEEN_AFTER_SPACE);
                break;
            default:
                subspaces[FORE_BETWEEN_SPACE] = createSubspace(fore, index[FORE_BETWEEN_SPACE]);
                subspaces[BETWEEN_SPACE] = createSubspace(between, index[BETWEEN_SPACE]);
                subspaces[BETWEEN_AFTER_SPACE] = createSubspace(after, index[BETWEEN_AFTER_SPACE]);
        }// end switch mode
        /*
         * end Dung
         */
        
//        // fore-between
//        Sentence fore = foreBetweenContext.filter(sent);
//        subspaces[FORE_BETWEEN_SPACE] = createSubspace(fore, index[FORE_BETWEEN_SPACE]);
//        //subspaces[FORE_BETWEEN_SPACE] = new SparseVector();
//
//        // between
//        Sentence between = betweenContext.filter(sent);
//        subspaces[BETWEEN_SPACE] = createSubspace(between, index[BETWEEN_SPACE]);
//
//        // between-after
//        Sentence after = betweenAfterContext.filter(sent);
//        subspaces[BETWEEN_AFTER_SPACE] = createSubspace(after, index[BETWEEN_AFTER_SPACE]);
//        //subspaces[BETWEEN_AFTER_SPACE] = new SparseVector();

        // normalize subspaces
        // default la 3 dong comment duoi day
//        for (int j = 0; j < NUMBER_OF_SUBSPACES; j++) {
//            subspaces[j].normalize();
//        }
        subspaces[FORE_BETWEEN_SPACE].normalize();

        //
        return subspaces;
    } // end map

    //
    protected Vector createSubspace(Sentence sent, FeatureIndex index) {
        //logger.debug("createSubspace");
        Vector vec = new SparseVector();

        // unigram
        for (int i = 0; i < sent.length(); i++) {
            //String t = "WF:" + sent.wordAt(i).getForm() + "-" + sent.wordAt(i).getWPos();
            String t = sent.wordAt(i).getForm();
            updateVector(vec, index, t);

            //String p = sent.wordAt(i).getWPos();
            //updateVector(vec, index, p);
        } // end for i

        if (ngram > 1) {
            // bigram
            for (int i = 0; i < sent.length() - 1; i++) {
                //String t1 = sent.wordAt(i).getForm() + "-" + sent.wordAt(i).getWPos();
                //String t2 = sent.wordAt(i+1).getForm() + "-" + sent.wordAt(i).getWPos();
                String t1 = sent.wordAt(i).getForm();
                String t2 = sent.wordAt(i + 1).getForm();
                String t = t1 + "_" + t2;
                updateVector(vec, index, t);

                //String p1 = sent.wordAt(i).getWPos();
                //String p2 = sent.wordAt(i+1).getWPos();
                //String p = p1 + "_" + p2;
                //updateVector(vec, index, p);

            } // end for i
        }

        if (ngram > 2) {
            // trigram
            for (int i = 0; i < sent.length() - 2; i++) {
                //String t1 = sent.wordAt(i).getForm() + "-" + sent.wordAt(i).getWPos();
                //String t2 = sent.wordAt(i+1).getForm() + "-" + sent.wordAt(i).getWPos();
                //String t3 = sent.wordAt(i+2).getForm() + "-" + sent.wordAt(i).getWPos();
                String t1 = sent.wordAt(i).getForm();
                String t2 = sent.wordAt(i + 1).getForm();
                String t3 = sent.wordAt(i + 2).getForm();
                String t = t1 + "_" + t2 + "_" + t3;

                updateVector(vec, index, t);

                //String p1 = sent.wordAt(i).getWPos();
                //String p2 = sent.wordAt(i+1).getWPos();
                //String p3 = sent.wordAt(i+2).getWPos();
                //String p = p1 + "_" + p2 + "_" + p3;
                //updateVector(vec, index, p);

                // sparse bigrams
                // add more precision
                //t = t1 + "_" + t3;
                //updateVector(vec, index, t);

            } // end for i
        }

        return vec;
    } // end createSubspace

    //
    private void updateVector(Vector vec, FeatureIndex index, String t) {
        //logger.debug("updateVector");
        int j = index.put(t);
        //logger.debug(t + " " + j);

        // Roth and Semeval-re
        if (j != -1) {
            vec.add(j, 1);
        }

        // AImed
/*
        if (j != -1)
        {
        if (vec.existsIndex(j))
        vec.set(j, vec.get(j) + 1); // tf
        else
        vec.add(j, 1);
        }
         */
    } // end updateVector

    //
    public String toString() {
        return "GlobalContextMapping";
    } // end toString
    
    /*
     * Dung
     */
    protected Vector createSubspace4(Sentence sent, FeatureIndex index, int mode) {
        //logger.debug("createSubspace");
        Vector vec = new SparseVector();
        int windowSizeForWordContext = -1;
        int startOffset = -1;
        int endOffset = -1;
        for (int i = 0; i < sent.length(); i++) {
            if (sent.wordAt(i).getRole().equals(Word.AGENT_LABEL) || sent.wordAt(i).getRole().equals(Word.TARGET_LABEL)) {
                startOffset = i;
            }// end if sent.wordAt(i).getRole().equals(Word.AGENT_LABEL) || sent.wordAt(i).getRole()
        }// end for i

        if (startOffset != -1) {
            if (mode == FORE_BETWEEN_SPACE) {
                endOffset = sent.length() - 1;
                windowSizeForWordContext = endOffset - startOffset;
            }// end if mode = FORE_BETWEEN_SPACE
            if (mode == BETWEEN_AFTER_SPACE) {
                endOffset = startOffset;
                startOffset = 0;
                windowSizeForWordContext = endOffset - startOffset;
            }// end if mode == BETWEEN_AFTER_SPACE
        } else {
            windowSizeForWordContext = sent.length();
        }// end if startOffset != -1

        // unigram
        for (int i = 0; i < sent.length(); i++) {
            //String t = "WF:" + sent.wordAt(i).getForm() + "-" + sent.wordAt(i).getWPos();
            String t = sent.wordAt(i).getForm();
            updateVector(vec, index, t);
            //Ngoc
            //them dac trung cho 1 tu
            String role = sent.wordAt(i).getRole();
            if (role.equals(Word.AGENT_LABEL) || role.equals(Word.TARGET_LABEL)) {
                WordContext wordContext = new WordContext();

                wordContext.setWindowSize(windowSizeForWordContext);
                Sentence context = wordContext.filter(sent, i);
                for (int j = 0; j < context.length(); j++) {
                    int offset = i - findTargetOffset(sent);
                    Word word = context.wordAt(j);
                    String form = word.getForm(false);
                    String pos = word.getPos();
                    String entityType = word.getType();

                    // bat dau dac trung cua rieng tu
                    addFeature(form + Orthographic.WORD_FORM, offset, vec, index);
                    addFeature(pos + Orthographic.PART_OF_SPEECH, offset, vec, index);
                    addFeature(entityType + Orthographic.TYPE, offset, vec, index);
                    if (Orthographic.isUpperCase(form)) //addFeature(form + Orthographic.form, offset, vec, index);
                    {
                        addFeature(Orthographic.UPPER_CASE, offset, vec, index);
                    }

                    // lowercase
                    if (Orthographic.isLowerCase(form)) //addFeature(form + Orthographic.UPPER_CASE, offset, vec, index);
                    {
                        addFeature(Orthographic.LOWER_CASE, offset, vec, index);
                    }

                    // punctuation
                    if (Orthographic.isPunctuation(form)) //addFeature(form + Orthographic.PUNCTUATION, offset, vec, index);
                    {
                        addFeature(Orthographic.PUNCTUATION, offset, vec, index);
                    }

                    // capitalized
                    if (Orthographic.isCapitalized(form)) //addFeature(form + Orthographic.CAPITALIZED, vec, index);
                    {
                        addFeature(Orthographic.CAPITALIZED, offset, vec, index);
                    }

                    // numeric
                    if (Orthographic.isNumeric(form)) //addFeature(form + Orthographic.NUMERIC, vec, index);
                    {
                        addFeature(Orthographic.NUMERIC, offset, vec, index);
                    }
                    // ket thuc them dac trung cua rieng tu
                }// end for j
            }// end if role
            String pos = t + "_" + sent.wordAt(i).getPos();
            updateVector(vec, index, pos);
            //end Ngoc

        } // end for i

        if (ngram > 1) {
            // bigram
            for (int i = 0; i < sent.length() - 1; i++) {
                //String t1 = sent.wordAt(i).getForm() + "-" + sent.wordAt(i).getWPos();
                //String t2 = sent.wordAt(i+1).getForm() + "-" + sent.wordAt(i).getWPos();
                String t1 = sent.wordAt(i).getForm();
                String t2 = sent.wordAt(i + 1).getForm();
                String t = t1 + "_" + t2;
                //file goc
                String pos = sent.wordAt(i).getPos() + "_" + sent.wordAt(i + 1).getPos();
                pos = t + "_" + pos;

                updateVector(vec, index, t);
                //Ngoc
                updateVector(vec, index, pos);

            } // end for i
        }

        if (ngram > 2) {
            // trigram
            for (int i = 0; i < sent.length() - 2; i++) {
                //String t1 = sent.wordAt(i).getForm() + "-" + sent.wordAt(i).getWPos();
                //String t2 = sent.wordAt(i+1).getForm() + "-" + sent.wordAt(i).getWPos();
                //String t3 = sent.wordAt(i+2).getForm() + "-" + sent.wordAt(i).getWPos();
                String t1 = sent.wordAt(i).getForm();
                String t2 = sent.wordAt(i + 1).getForm();
                String t3 = sent.wordAt(i + 2).getForm();
                String t = t1 + "_" + t2 + "_" + t3;

                // Ngoc
                String pos = sent.wordAt(i).getPos() + "_" + sent.wordAt(i + 1).getPos() + "_" + sent.wordAt(i + 2).getPos();
                pos = t + "_" + pos;

                updateVector(vec, index, t);

                updateVector(vec, index, pos);

            } // end for i
        }

        return vec;
    } // end createSubspace
    
    // method addFeature cua LC
    protected void addFeature(String feat, int i, Vector vec, FeatureIndex index) {
        //logger.info("feat: " + feat);
        String f = feat;
        if (i > 0) {
            f = feat + "+" + i;
        } else if (i < 0) {
            f = feat + i;
        }

        int j = index.put(f);


        if (j != -1) {
            vec.add(j, 1);
        }


        //logger.debug("added feat: " + f);
    } // end addFeature

    // method findTargetOffset
    protected int findTargetOffset(Sentence sent) {
        for (int i = 0; i < sent.length(); i++) {
            if (sent.wordAt(i).getRole().equals(Word.TARGET_LABEL)
                    || sent.wordAt(i).getRole().equals(Word.AGENT_LABEL)) {
                return i;
            }
        } // end for i

        return -1;
    } // end findTargetOffset
    /*
     * End Dung
     */
} // end class GlobalContextMapping
