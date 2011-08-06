/*
    Copyright (C) 2006, Xuan-Hieu Phan
    
    Email:	hieuxuan@ecei.tohoku.ac.jp
		pxhieu@gmail.com
    URL:	http://www.hori.ecei.tohoku.ac.jp/~hieuxuan
    
    Graduate School of Information Sciences,
    Tohoku University
*/

package crfsvm.crf.een_phuong;

import java.io.*;
import java.util.*;
import java.util.ArrayList;

public class Viterbi {
    public Model model = null;
    int numLabels = 0;
    
    DoubleMatrix Mi = null;
    DoubleVector Vi = null;
    
    public class PairDblInt {
	public double first = 0.0;
	public int second = -1;
    } // enf of class PairDblInt    

    public int memorySize = 0;
    public PairDblInt[][] memory = null;

    public int normConstantSize = 0;
    public double normConstant[][] = null;
    public double normConfidence[][] = null;
    public double scale[] = null;
    
    public Viterbi() {
    }
    
    public void init(Model model) {
	this.model = model;
	
	numLabels = model.taggerMaps.numLabels();
	
	Mi = new DoubleMatrix(numLabels, numLabels);
	Vi = new DoubleVector(numLabels);
	
	allocateMemory(100);
	
	// compute Mi once at initialization
	computeMi();
    }
    
    public void allocateMemory(int memorySize) {
	this.memorySize = memorySize;
	memory = new PairDblInt[memorySize][numLabels];
	
	for (int i = 0; i < memorySize; i++) {
	    for (int j = 0; j < numLabels; j++) {
		memory[i][j] = new PairDblInt();
	    }
	}

        //my code
        this.normConstantSize = memorySize;
        normConstant = new double[memorySize][numLabels];
        normConfidence = new double[memorySize][numLabels];

        for (int i = 0; i < memorySize; i++) {
	    for (int j = 0; j < numLabels; j++) {
		normConstant[i][j] = 0.0;
                normConfidence[i][j] = 0.0;
	    }
	}

        scale = new double[memorySize];
        //end my code
    }
    
    public void computeMi() {
	Mi.assign(0.0);
	
	model.taggerFGen.startScanEFeatures();
	while (model.taggerFGen.hasNextEFeature()) {
	    Feature f = model.taggerFGen.nextEFeature();
	    
	    if (f.ftype == Feature.EDGE_FEATURE1) {
		Mi.mtrx[f.yp][f.y] += model.lambda[f.idx] * f.val;
	    }
	}
	
	for (int i = 0; i < Mi.rows; i++) {
	    for (int j = 0; j < Mi.cols; j++) {
		Mi.mtrx[i][j] = Math.exp(Mi.mtrx[i][j]);
	    }
	}
    }
    
    public void computeVi(List seq, int pos, DoubleVector Vi, boolean isExp) {
	Vi.assign(0.0);
	
	// start scan features for sequence "seq" at position "pos"
	model.taggerFGen.startScanSFeaturesAt(seq, pos);
	// examine all features at position "pos"
	while (model.taggerFGen.hasNextSFeature()) {
	    Feature f = model.taggerFGen.nextSFeature();
	    
	    if (f.ftype == Feature.STAT_FEATURE1) {
		Vi.vect[f.y] += model.lambda[f.idx] * f.val;
	    }
	}
	
	// take exponential operator
	if (isExp) {
	    for (int i = 0; i < Vi.len; i++) {
		Vi.vect[i] = Math.exp(Vi.vect[i]);
	    }
	}
    }
    
    // list is a List of PairDblInt    
    public double sum(PairDblInt[] cols) {
	double res = 0.0;
	
	for (int i = 0; i < numLabels; i++) {
	    res += cols[i].first;
	}
	
	if (res < 1 && res > -1) {
	    res = 1;
	}
	
	return res;
    }

    // list is a List of PairDblInt
    public void divide(PairDblInt[] cols, double val) {
	for (int i = 0; i < numLabels; i++) {
	    cols[i].first /= val;
	}
    }

    //my code
    public double sum(double[] cols)
    {
        double res = 0.0;

        for (int i = 0; i < numLabels; i++)
        {
            res += cols[i];
        }

        if (res < 1 && res > -1)
        {
            res = 1;
        }

        return res;
    }

    public void divide(double[] cols, double val)
    {
        if (val == 0) return;
        for (int i = 0; i < numLabels; i++)
        {
            cols[i] /= val;
        }
    }
    //end my code
    
    // list is a List of PairDblInt
    public int findMax(PairDblInt[] cols) {
	int maxIdx = 0;
	double maxVal = -1.0;
	
	for (int i = 0; i < numLabels; i++) {
	    if (cols[i].first > maxVal) {
		maxVal = cols[i].first;
		maxIdx = i;
	    }
	}
	
	return maxIdx;
    }
    
    public String viterbiInference(List seq, Map lbStr2Int) { //return void initially

        try{
        String resultForCalculateConfidece = ""; //my code

	int i, j, k;

        List lstForConfi = null;
	
	int seqLen = seq.size();
        //System.out.println("seqLen = " + seqLen);
	if (seqLen <= 0) {
	    return "";
	}	
	
	if (memorySize < seqLen) {
	    allocateMemory(seqLen);
	}
	//System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	// compute Vi for the first position in the sequence
	computeVi(seq, 0, Vi, true);
	for (j = 0; j < numLabels; j++) {
	    memory[0][j].first = Vi.vect[j];
	    memory[0][j].second = j;

            //my code
            normConstant[0][j] = Vi.vect[j];
            //System.out.println("Vi.vect[" + j + "] = " + Vi.vect[j]);

            //end my code
	}	
	
	// scaling for the first position
	divide(memory[0], sum(memory[0]));
        //my code
        scale[0] = sum(normConstant[0]);
        divide(normConstant[0], scale[0]);
        //end my code
	
	// the main loop
	for (i = 1; i < seqLen; i++) {
	    // compute Vi at the position i
	    computeVi(seq, i, Vi, true);
	    
	    // for all possible labels at the position i
	    for (j = 0; j < numLabels; j++) {
		memory[i][j].first = 0.0;
		memory[i][j].second = 0;

                normConstant[i][j] = 0.0; //my code

                //if ((i == 1) && (j == 0))
                //{
                //    for (int co = 0; co < numLabels; co++)
                //        System.out.println("normConstant[0][" + co + "] = " + normConstant[0][co]);
                //}
		
		// find the maximal value and its index and store them in memory
		// for later tracing back to find the best path
		for (k = 0; k < numLabels; k++) {
		    double tempVal = memory[i - 1][k].first *
					Mi.mtrx[k][j] * Vi.vect[j];
                    normConstant[i][j] += normConstant[i - 1][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code
		    if (tempVal > memory[i][j].first) {
			memory[i][j].first = tempVal;
			memory[i][j].second = k;
		    }
		}
                //System.out.println("normConstant[" + i + "][" + j + "] = " + normConstant[i][j]);
	    }
	    
	    // scaling for memory at position i
	    divide(memory[i], sum(memory[i]));

            //my code => scaling for normConstant
            scale[i] = sum(normConstant[i]);
            divide(normConstant[i], scale[i]);
            //end my code
	}
	
	// viterbi backtrack to find the best label path
        
	int maxIdx = findMax(memory[seqLen - 1]);
	((Observation)seq.get(seqLen - 1)).modelLabel = maxIdx;
	for (i = seqLen - 2; i >= 0; i--) {
	    ((Observation)seq.get(i)).modelLabel = 
			memory[i + 1][maxIdx].second;
	    maxIdx = ((Observation)seq.get(i)).modelLabel;
	}

        //my code
        double denominator = 0.0;
        
        for (int run = 0; run < numLabels; run++)
        {
            denominator += normConstant[seqLen - 1][run];
            //System.out.println("normConstant[seqLen - 1][" + run + "] = " + normConstant[seqLen - 1][run]);
        }
        //System.out.println("denominator = " + denominator);
        //my code for remember for calculating confidence
        resultForCalculateConfidece = numLabels + "#" + "\n";
        resultForCalculateConfidece += "----------" + "\n";
        for (int ro = 0; ro < numLabels; ro++)
        {
            for (int co = 0; co < numLabels; co++)
                resultForCalculateConfidece += Mi.mtrx[ro][co] + "#";
            resultForCalculateConfidece += "\n";
        }
        resultForCalculateConfidece += "########" + "\n";
        resultForCalculateConfidece += numLabels + "#" + seqLen + "#" + denominator + "#" + "\n";
        resultForCalculateConfidece += "----------" + "\n";
        
        for (int ro = 0; ro < seqLen; ro++)
        {
            computeVi(seq, ro, Vi, true);
            for (int co = 0; co < numLabels; co++)
                resultForCalculateConfidece += Vi.vect[co] + "#";
            resultForCalculateConfidece += "\n";
        }
        resultForCalculateConfidece += "----------" + "\n";
        
        for (int ro = 0; ro < seqLen; ro++)
        {
            for (int co = 0; co < numLabels; co++)
                resultForCalculateConfidece += normConstant[ro][co] + "#";
            resultForCalculateConfidece += "\n";
        }

        resultForCalculateConfidece += "----------" + "\n";
        for (int ro = 0; ro < seqLen; ro++)
            resultForCalculateConfidece += scale[ro] + "#";
        resultForCalculateConfidece += "\n";
        
        resultForCalculateConfidece += "----------" + "\n";
        for (int co = 0; co < seqLen; co++)
            resultForCalculateConfidece += ((Observation)seq.get(co)).modelLabel + "#";
        
        //System.out.println("Viterbi: " + resultForCalculateConfidece);
        // end my code

        //System.out.println("denominator: " + denominator);
        lstForConfi = analysizeObserTag(seq, lbStr2Int);
        for (int run = 0; run < lstForConfi.size(); run++)
        {
            for (i = 0; i < seqLen; i++)
                for (j = 0; j < numLabels; j++)
                    normConfidence[i][j] = 0.0;
            int[] elementi = (int[]) lstForConfi.get(run);

            computeVi(seq, 0, Vi, true);

            if (elementi[0] == 0)
            {
                for (j = 0; j < numLabels; j++)
                    if (j == elementi[1])
                        normConfidence[0][j] = Vi.vect[j];
                
                divide(normConfidence[0], scale[0]);
            }
            if (elementi[0] > 0)
            {
                for (i = 0; i < elementi[0]; i++)
                    for (j = 0; j < numLabels; j++)
                        normConfidence[i][j] = normConstant[i][j];
                computeVi(seq, elementi[0], Vi, true);
                for (j = 0; j < numLabels; j++)
                    if (j != elementi[1])
                        continue;
                    else
                    {
                        for (k = 0; k < numLabels; k++)
                            normConfidence[elementi[0]][j] += normConfidence[elementi[0] - 1][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code
                        break;
                    }

                divide(normConfidence[elementi[0]], scale[elementi[0]]);
            }
            if (elementi[3] > elementi[0])
            {
                for (i = elementi[0] + 1; i <= elementi[3]; i++)
                {
                    computeVi(seq, i, Vi, true);
                    for (j = 0; j < numLabels; j++)
                        if (j != elementi[2])
                            continue;
                        else
                        {
                            for (k = 0; k < numLabels; k++)
                            normConfidence[i][j] += normConfidence[i - 1][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code
                            break;
                        }

                    divide(normConfidence[i], scale[i]);
                }
            }
            if (elementi[3] < seq.size() - 1)
            {
                    computeVi(seq, elementi[3] + 1, Vi, true);
                    for (j = 0; j < numLabels; j++)
                        if (j == elementi[2])
                            continue;
                        else
                            for (k = 0; k < numLabels; k++)
                            normConfidence[elementi[3] + 1][j] += normConfidence[elementi[3]][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code

                    divide(normConfidence[elementi[3] + 1], scale[elementi[3] + 1]);

                    for (i = elementi[3] + 2; i < seq.size(); i++)
                    {
                        computeVi(seq, i, Vi, true);
                        for (j = 0; j < numLabels; j++)
                            for (k = 0; k < numLabels; k++)
                                normConfidence[i][j] += normConfidence[i - 1][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code

                        divide(normConfidence[i], scale[i]);
                    }
            }

            double numerator = 0.0;

            for (int tocal = 0; tocal < numLabels; tocal++)
            {
                numerator += normConfidence[seqLen - 1][tocal];
            }

            double confidence = numerator/denominator;

            for (i = elementi[0]; i <= elementi[3]; i++)
                ((Observation)seq.get(i)).confidence = confidence;

        }

        return resultForCalculateConfidece;
        //end my code
        }
        catch (Exception ex)
        {
            functions.out("Problem in viterbi.java: " + ex);
            return "";
        }
    }

    public List analysizeObserTag(List seq, Map lbStr2Int)
    {
        try
        {
            List result = new ArrayList();
            String[] tags = {"per", "loc", "org", "time", "num", "cur", "misc", "pct"};
            for (int i = 0; i < tags.length; i++)
            {
                String tag = tags[i];
                Integer startin = (Integer) lbStr2Int.get("B-" + tag);
                if (startin == null) continue;
                Integer endin = (Integer) lbStr2Int.get("I-" + tag);
                if (endin == null) continue;
                int start = startin.intValue();
                int end = endin.intValue();
                int state = 0, save = 0;
                for (int ob = 0; ob < seq.size(); ob++)
                {
                    Observation obsr = (Observation) seq.get(ob);
                    if (state == 0)
                    {
                        if (obsr.modelLabel == start)
                        {
                            save = ob;
                            state = 1;
                            if (ob == seq.size() - 1)
                            {
                                int[] ele = {save, start, end, save};
                                result.add(ele);
                            }
                        }
                        continue;
                    }
                    if (state == 1)
                    {
                        if (obsr.modelLabel == end)
                        {
                            if (ob == seq.size() - 1)
                            {
                                int[] ele = {save, start, end, ob};
                                result.add(ele);
                            }
                        }
                        else if (obsr.modelLabel == start)
                        {
                            int[] ele = {save, start, end, ob - 1};
                            result.add(ele);
                        }
                        else
                        {
                            int[] ele = {save, start, end, ob - 1};
                            result.add(ele);
                            state = 0;
                        }
                    }
                }
            }

            return result;
        }
        catch (Exception ex)
        {
            functions.out("Problem in analysizeObserTag in viterbi " + ex);
            return null;
        }
    }
    
} // end of class Viterbi

