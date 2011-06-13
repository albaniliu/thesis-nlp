/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package context;

import feature.FeatureIndex;
import feature.Orthographic;
import lib.Sentence;
import lib.Word;
import util.SparseVector;
import util.Vector;

/**
 *
 * @author banhbaochay
 */
public class LocalContextMapping {
    /**
     * Tao vector dac trung cho mot tu
     * @param sentence Mot phan cua cau chua ban than tu dang xet va cac tu xung quanh no
     * @param featureIndex Map de luu anh xa tu ten feature voi chi so cua feature do
     * @return Tra ve vector dac trung cho tu do
     */
    public Vector createVector(Sentence sentence, FeatureIndex featureIndex) {
        Vector vector = new SparseVector();
        /*
         * targetOffset chinh la vi tri cua tu can xet
         */
        int targetOffset = sentence.size() / 2;
        for (int i = 0; i < sentence.size(); i++) {
            Word word = sentence.wordAt(i);
            /*
             * Lay ra cac dac trung cua tu
             */
            String form = word.getForm();
            String pos = word.getPOS().getName();
            String iob = word.getIOB();
            String dicType = word.getDictionaryType();
            int offset = i - targetOffset;

            /*
             * Add cac feature binh thuong cua tu
             */
            addFeature(form + Orthographic.WORD_FORM, offset, vector, featureIndex);
            addFeature(dicType + Orthographic.DICTIONARY, offset, vector, featureIndex);
            addFeature(pos + Orthographic.POS, offset, vector, featureIndex);
            addFeature(iob + Orthographic.IOB, offset, vector, featureIndex);

            /*
             * Add cac feature lien quan den nhan dang chinh ta
             */
            if (Orthographic.isAllCap(form)) {
                addFeature(Orthographic.ALL_CAPS, offset, vector, featureIndex);
            }// end if all cap

            if (Orthographic.isLowerCase(form)) {
                addFeature(Orthographic.LOWER_CASE, offset, vector, featureIndex);
            }// end if lower case

            if (Orthographic.isDigit(form)) {
                addFeature(Orthographic.DIGIT, offset, vector, featureIndex);
            }// end if digit

            if (Orthographic.isPunct(form)) {
                addFeature(Orthographic.PUNCTUATION, offset, vector, featureIndex);
            }// end if punct

            if (Orthographic.isInitCap(form)) {
                addFeature(Orthographic.INIT_CAP, offset, vector, featureIndex);
            }// end if init cap
        }// end for with word

        /*
         * Normalize vector
         */
        vector.normalize();

        return vector;
    }// end method createVector

    /**
     * Them feature vao featureIndex, dong thoi khai bao thanh phan tuong ung voi feature nay
     * cho vector
     * @param featureName
     * @param offset
     * @param vector
     * @param featureIndex
     */
    private void addFeature(String featureName, int offset, Vector vector, FeatureIndex featureIndex) {
        if (offset > 0) {
            featureName = featureName + "+" + offset;
        } else {
            featureName = featureName + offset;
        }// end if

        int index = featureIndex.put(featureName);
        vector.add(index, 1);

    }

    public static void main(String[] args) {
        FeatureIndex featureIndex = new FeatureIndex(0);
        Sentence sentence = new Sentence("Anh <per> Nguyễn_Văn_Nam </per> đang");
        System.out.println(sentence);
        LocalContextMapping localContext = new LocalContextMapping();
        Vector vector = localContext.createVector(sentence, featureIndex);
        System.out.println(vector);
    }// end main

} // end class LocalContextMapping
