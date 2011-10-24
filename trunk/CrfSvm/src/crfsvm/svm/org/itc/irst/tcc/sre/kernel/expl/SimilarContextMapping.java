/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.svm.org.itc.irst.tcc.sre.kernel.expl;

import crfsvm.svm.org.itc.irst.tcc.sre.data.ExampleSet;
import crfsvm.svm.org.itc.irst.tcc.sre.data.Sentence;
import crfsvm.svm.org.itc.irst.tcc.sre.data.SentenceSetCopy;
import crfsvm.svm.org.itc.irst.tcc.sre.data.VectorSet;
import crfsvm.svm.org.itc.irst.tcc.sre.data.Word;
import crfsvm.svm.org.itc.irst.tcc.sre.util.FeatureIndex;
import crfsvm.svm.org.itc.irst.tcc.sre.util.SparseVector;
import crfsvm.svm.org.itc.irst.tcc.sre.util.Vector;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author banhbaochay
 */
public class SimilarContextMapping implements Mapping, ContextMapping {

    static Logger logger = Logger.getLogger(SimilarContextMapping.class.getName());
    
    private static final int NUMBER_OF_SUBSPACES = 1;
    
    public SimilarContextMapping() {
        logger.debug("SimilarContextMapping constructor");
    }
    
    @Override
    public void setParameters(Properties parameters) {
        
    }// end setParameters method

    /**
     * Tao khong gian dac trung tu van ban
     * @param data
     * @param index
     * @return VectorSet co instance la vector dac trung
     * @throws IllegalArgumentException 
     */
    @Override
    public VectorSet map(ExampleSet data, FeatureIndex[] index) throws IllegalArgumentException {
        boolean b = (data instanceof SentenceSetCopy);
        if (!b) {
            throw new IllegalArgumentException();
        }

        VectorSet result = new VectorSet();
        
        for (int i = 0; i < data.size() - 1; i++) {
            for (int j = i + 1; j < data.size(); j++) {
                Object x1 = data.x(i);
                Object y = data.y(i);
                Object id1 = data.id(i);
                Object x2 = data.x(j);
                Object id2 = data.id(j);
                
                String featureName = id1.toString() + ":" + id2.toString();
                Vector space = createSpace(x1, x2, featureName, index[0]);
                result.add(space, y, id1);
            }// end for j
        }// end for i
        
        return result;
    }// end map method

    @Override
    public int subspaceCount() {
        return NUMBER_OF_SUBSPACES;
    }// end subspaceCount method
    
    private Vector createSpace(Object x1, Object x2, String feature, FeatureIndex index) {
        boolean b = (x1 instanceof Sentence && x2 instanceof Sentence);
        if (!b) {
            throw new IllegalArgumentException();
        }
        
        Sentence sent1 = (Sentence) x1;
        Sentence sent2 = (Sentence) x2;
        
        Vector space = new SparseVector();
        updateVector(space, index, feature, calcSimilar(sent1, sent2));
        return space;
    }// end createSpace method
    
    
    /**
     * Update thong so cho vector dac trung va lien he voi feature index thong qua string feature
     * @param vector
     * @param index
     * @param feature 
     */
    private void updateVector(Vector vector, FeatureIndex index, String feature, double value) {
        int i = index.put(feature);
        if (i != -1) {
            vector.add(i, value);
        }// end if i
    }// end updateVector method
    
    /**
     * Tinh do tuong tu giua 2 cau
     * @param sent1
     * @param sent2
     * @return 
     */
    private double calcSimilar(Sentence sent1, Sentence sent2) {
        Vector similarVector = new SparseVector();
        for (int i = 0; i < sent1.length(); i++) {
            for (int j = 0; j < sent2.length(); j++) {
                Word word1 = sent1.wordAt(i);
                Word word2 = sent2.wordAt(j);
                updateSimilarVector(similarVector, word1, word2);
            }// end for j
        }// end for i
        similarVector.normalize();
        return similarVector.norm();
    }// end calcSimilar method

    /**
     * Them thong so vao vector do tuong tu giua 2 cau
     * @param word1
     * @param word2 
     */
    private void updateSimilarVector(Vector similar, Word word1, Word word2) {
        if (word1.getPos().equals(word2.getPos())) {
            similar.add(1, 1);
        }
    }// end updateSimilarVector

}// end SimilarContextMapping class

