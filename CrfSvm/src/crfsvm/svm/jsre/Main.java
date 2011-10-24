/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.svm.jsre;

import crfsvm.svm.org.itc.irst.tcc.sre.Predict;
import crfsvm.svm.org.itc.irst.tcc.sre.Train;
import crfsvm.svm.org.itc.irst.tcc.sre.data.ExampleSet;
import crfsvm.svm.org.itc.irst.tcc.sre.data.ReadWriteFile;
import crfsvm.svm.org.itc.irst.tcc.sre.data.SentenceSetCopy;
import crfsvm.svm.org.itc.irst.tcc.sre.util.Evaluator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Main {

    static final int LABELED = 0;
    static final int UNLABELED = 1;
    static Logger logger = Logger.getLogger(Main.class);

    /**
     * @param args the command line arguments
     * @throws Exception  
     */
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log-config.txt");
        Main main = new Main();
        String trainPath = "data/RE/supervised/work_for.train";
        String testPath = "data/RE/supervised/work_for.test";
        /*
         * So luong bagging
         */
        int B = 5;
        /*
         * So phan lop
         */
        int C = 2;
        /*
         * batch size
         */
        int S = 80;
        /*
         * nguong cho entropy
         */
        double thresholdH = 0.277;
        /*
         * Number example in one bag
         */
        int bagSize = 120;

        ExampleSet labelSet = new SentenceSetCopy();
        ExampleSet unlabelSet = new SentenceSetCopy();
        labelSet.read(new BufferedReader(new FileReader(trainPath)));
        unlabelSet.read(new BufferedReader(new FileReader(testPath)));
        ExampleSet testSet = unlabelSet.copy();

        logger.warn("Predict tap test voi tap train goc\nTrain set: " + labelSet.size() + " cau, test set: " + testSet.size() + " cau");
        main.runJSRE(labelSet, testSet, null);
        System.exit(0);
        /*
         * Ngung lap neu so luong phan tu tap unlabel < S hoac H nho nhat lon hon nguong
         */
        while (unlabelSet.size() > S) {
            /*
             * Mang luu so lan tung cau duoc gan nhan hoac khong duoc gan nhan
             */
            int[][] count = new int[C][unlabelSet.size()];

            List trainSetList = labelSet.createBagging(B, bagSize);
            /*
             * Chay SVM cho tung input set, luu so lan dem vao mang count
             */
            for (Object o : trainSetList) {
                ExampleSet trainSet = (SentenceSetCopy) o;
                logger.warn("Run JSRE in bagging, unlabel set size: " + unlabelSet.size());
                main.runJSRE(trainSet, unlabelSet, count);
            }

            /*
             * Tinh entropy cho tung cau
             */
            double[] H = main.calculateH(C, B, count);

            /*
             * Tim vi tri S cau trong unlabel set
             */
            int[] indexS = main.findS(H, S, thresholdH);
            if (indexS[0] == -1) {
                /*
                 * Neu H be nhat van lon hon nguong
                 */
                break;
            }// end if indexS[0] == -1

            /*
             * Them S cau nay vao label set
             */
            for (int index : indexS) {
                labelSet.add(unlabelSet.x(index), unlabelSet.y(index), unlabelSet.id(index));
            }
            logger.warn("Them " + S + " cau vao tap da gan nhan, tap da gan nhan co " + labelSet.size() + " cau");

            /*
             * Xoa S cau khoi unlabel set
             */
            for (int index = indexS[indexS.length - 1]; index > 0; index--) {
                unlabelSet.remove(index);
            }
            logger.warn("Test thu voi tap da gan nhan moi, test set size: " + testSet.size());
            main.runJSRE(labelSet, testSet, null);
        }// end while

    }

    // <editor-fold defaultstate="collapsed" desc="runJSRE method">
    /**
     * Chay toan bo JSRE tu train den predict. Ket qua ve so lan duoc gan nhan tung cau hoac k duoc gan nhan
     * luu o mang count
     * @param trainSet
     * @param testSet 
     * @param count
     * @throws IndexOutOfBoundsException
     * @throws IOException
     * @throws Exception 
     */
    public void runJSRE(ExampleSet trainSet, ExampleSet testSet, int[][] count) throws IndexOutOfBoundsException, IOException, Exception {
        // TODO code application logic here
        String modelPath = "tmp/work_for.model";
        String outputPath = "tmp/work_for.output";

        /*
         * Set parameters
         */
        Properties parameter = new Properties();
        parameter.setProperty("cache-size", "128");
        parameter.setProperty("kernel-type", "SC");
        parameter.setProperty("n-gram", "3");
//        parameter.setProperty("example-file", trainPath);
        parameter.setProperty("model-file", modelPath);
        logger.debug(parameter);
        int mode = 1;
        parameter.setProperty("mode", Integer.toString(mode));

        /*
         * Train
         */
        Train train = new Train(parameter);
//        inputSet.read(new BufferedReader(new FileReader(trainPath)));
        train.runExampleSet(trainSet);

        /*
         * Predict
         */
        File modelFile = new File(modelPath);
        File outputFile = new File(outputPath);

        /*
         * Predict: 1. test file, 2. model file, 3. output file
         */
        Predict predict = new Predict(null, modelFile, outputFile);
        predict.runExampleSet(testSet);

        if (count != null) {
            countAppear(outputFile, count);
        }

        Evaluator eval = new Evaluator(testSet, outputFile);
        logger.warn("\n" + eval);

    }
    //</editor-fold>

    /**
     * Dem so lan moi cau duoc gan nhan sau khi predict
     * @param outputFile
     * @param count mang luu so lan tung cau duoc gan nhan
     */
    private void countAppear(File outputFile, int[][] count) {
        try {
            BufferedReader in = ReadWriteFile.readFile(outputFile);
            String line = null;
            int i = 0;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    double v = Double.parseDouble(line);
                    if (v == 1.0) {
                        /*
                         * Neu cau duoc classifier gan nhan
                         */
                        count[LABELED][i]++;
                    } else {
                        count[UNLABELED][i]++;
                    }// end if v == 1.0
                    i++;
                }// end if line.equals("")
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end countAppear method

    /**
     * Tinh H cho cac cau trong unlabel set
     * @param C so lop
     * @param B so bagging
     * @param count mang luu so lan duoc gan nhan va k dc gan nhan cua cac cau trong unlabel set
     * @return mang luu gia tri H cua cac cau trong unlabel set
     */
    private double[] calculateH(int C, int B, int[][] count) {
        double[] H = new double[count[LABELED].length];
        for (int i = 0; i < H.length; i++) {
            /*
             * Tinh H tren tung cau
             */
            double Hi = 0.0;
            for (int j = 0; j < C; j++) {
                double riOverB = (double) count[j][i] / B;
                if (riOverB != 0) {
                    Hi -= riOverB * Math.log(riOverB);
                }// end if riOverB != 0

            }// end for j
            H[i] = Hi;
        }// end for i

        return H;
    }

    /**
     * Tim vi tri cua S cau trong unlabel set co H nho nhat
     * @param unlabelSet
     * @param H mang luu entropy cua tung cau trong unlabel set 
     * @param S batch size
     */
    private int[] findS(double[] H, int S, double thresholdH) {
        Map HMap = new HashMap();
        for (int i = 0; i < H.length; i++) {
            HMap.put(i, H[i]);
        }// end for i
        HMap = sortMap(HMap);

        /*
         * Luu vi tri cua S cau trong unlabel set co H nho nhat vao mang result
         */
        int[] result = new int[S];
        int i = 0;
        for (Object e : HMap.keySet()) {
            Integer k = (Integer) e;
            if (i == 0 && (Double) HMap.get(k) > thresholdH) {
                result[i] = -1;
                return result;
            }// end if min > thresholdH
            result[i] = k;
            if (i == S - 1) {
                break;
            }// end if i == S - 1
            i++;
        }
        Arrays.sort(result);
        return result;
    }// end findS method

    // <editor-fold defaultstate="collapsed" desc="sortMap method: Sap xep theo value tang dan">
    /**
     * Sap xep map theo value
     * @param map
     * @return 
     */
    public static Map sortMap(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;

    }// end sortMap method
    //</editor-fold>
}
