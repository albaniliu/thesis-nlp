/*
Copyright (C) 2006, Xuan-Hieu Phan

Email:	hieuxuan@ecei.tohoku.ac.jp
pxhieu@gmail.com
URL:	http://www.hori.ecei.tohoku.ac.jp/~hieuxuan

Graduate School of Information Sciences,
Tohoku University
 */
package crfsvm.crf.een_phuong;

import crfsvm.Crf;
import java.io.*;
import java.util.*;
import java.util.StringTokenizer;

public class Model {

    public Option taggerOpt = null;
    public Maps taggerMaps = null;
    public Dictionary taggerDict = null;
    public FeatureGen taggerFGen = null;
    public Viterbi taggerVtb = null;
    // feature weight
    double[] lambda = null;

    public Model() {
    }

    public Model(Option taggerOpt, Maps taggerMaps, Dictionary taggerDict,
            FeatureGen taggerFGen, Viterbi taggerVtb) {
        this.taggerOpt = taggerOpt;
        this.taggerMaps = taggerMaps;
        this.taggerDict = taggerDict;
        this.taggerFGen = taggerFGen;
        this.taggerVtb = taggerVtb;
    }

    // load the model
    public boolean init() {
        // open model file to load model here ... complete later
        BufferedReader fin = null;
        String modelFile = taggerOpt.modelDir + File.separator + taggerOpt.modelFile;

        try {
//            fin = new BufferedReader(new FileReader(modelFile));
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(modelFile), "UTF-8"));

            // read context predicate map and label map
            taggerMaps.readCpMaps(fin);

            System.gc();

            taggerMaps.readLbMaps(fin);

            System.gc();

            // read dictionary
            taggerDict.readDict(fin);

            System.gc();

            // read features
            taggerFGen.readFeatures(fin);

            System.gc();

            // close model file
            fin.close();

        } catch (IOException e) {
            System.out.println("Couldn't open model file: " + modelFile);
            System.out.println(e.toString());

            return false;
        }

        // update feature weights
        if (lambda == null) {
            int numFeatures = taggerFGen.numFeatures();
            lambda = new double[numFeatures];
            for (int i = 0; i < numFeatures; i++) {
                
                Feature f = (Feature) taggerFGen.features.get(i);
                lambda[f.idx] = f.wgt;
            }
        }

        // call init method of Viterbi object
        if (taggerVtb != null) {
            taggerVtb.init(this);
        }

        return true;
    }

    public String inference(List seq, Map lbStr2Int) { //return void initially
        return  taggerVtb.viterbiInference(seq, lbStr2Int);
    }

    public void inferenceAll(List data, Map lbStr2Int) {
        System.out.println("Starting inference ...");

        long start, stop, elapsed;
        start = System.currentTimeMillis();

        for (int i = 0; i < data.size(); i++) {
            System.out.println("sequence " + Integer.toString(i + 1));
            List seq = (List) data.get(i);
            
            inference(seq, lbStr2Int);
            
        }

        stop = System.currentTimeMillis();
        elapsed = stop - start;

        System.out.println("Inference " + Integer.toString(data.size()) + " sequences completed!");
        System.out.println("Inference time: " + Double.toString((double) elapsed / 1000) + " seconds");
    }

    // my code
    public void inferenceAll(List data, Map lbStr2Int, String outputFile) {
        BufferedWriter out = null;
        try
        {
            String write = "";
            System.out.println("Starting inference ...");

            long start, stop, elapsed;
            start = System.currentTimeMillis();
            System.out.println("model: datasize = " + data.size());
            for (int i = 0; i < data.size(); i++) {
                //System.out.println("sequence " + Integer.toString(i + 1));
                List seq = (List) data.get(i);
                
                if (i == 0)
                    write = inference(seq, lbStr2Int) + "\n";
                else
                    write = processOutputOfInference(inference(seq, lbStr2Int));
                
                if (!outputFile.isEmpty())
                {
                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
                    out.write(write);
                    out.close();
                }
            }

            stop = System.currentTimeMillis();
            elapsed = stop - start;

            System.out.println("Inference " + Integer.toString(data.size()) + " sequences completed!");
            System.out.println("Inference time: " + Double.toString((double) elapsed / 1000) + " seconds");
        }
        catch (Exception ex)
        {
            System.out.println("Error in Model.inferenceAll: " + ex);
        }
    }

    public String processOutputOfInference(String str)
    {
        String result = "";
        StringTokenizer strTokn = new StringTokenizer(str, "\n");
        int max = strTokn.countTokens() + 1, count = 0;
        while (strTokn.hasMoreTokens())
        {
            count++;
            String line = strTokn.nextToken();
            if (line.equals("########")) max = count;
            if (count < max) continue;
            result += line + "\n";
        }
        return result;
    }
    // end my code
    
    /**
     * Dung:
     * Tao model tu file train, luu thanh file model.txt trong thu muc model
     * @param trainPath 
     */
    public static void createModel(String trainPath) {
        Crf.train(Crf.MANUAL_MODE, trainPath);
    }// end createModel method
} // end of class Model

