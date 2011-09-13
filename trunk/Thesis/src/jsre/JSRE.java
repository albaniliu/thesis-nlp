/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsre;

import java.io.File;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.itc.irst.tcc.sre.Predict;
import org.itc.irst.tcc.sre.Train;
import org.itc.irst.tcc.sre.data.ExampleSet;
import org.itc.irst.tcc.sre.util.Evaluator;

/**
 * Cung cap phuong thuc de train + predict tu framework JSRE cua Giuliano
 * @author banhbaochay
 */
public class JSRE {
    
    static Logger logger = Logger.getLogger(JSRE.class);
    
    public static final String MODEL_PATH = "examples/model.model";
    
    /**
     * 
     * @param trainSet
     * @param testSet
     * @param mode Che do chay JSRE: 1=JSRE da duoc thay doi boi Dung, 0=JSRE nguyen goc
     * @throws Exception  
     */
    public static void runJSRE(ExampleSet trainSet, ExampleSet testSet, int mode) throws Exception {
        String outputPath = "examples/work_for.output";

        /*
         * Set parameters
         */
        Properties parameter = new Properties();
        parameter.setProperty("cache-size", "128");
        parameter.setProperty("kernel-type", "SL");
        parameter.setProperty("n-gram", "3");
//        parameter.setProperty("example-file", trainPath);
        parameter.setProperty("model-file", MODEL_PATH);
        logger.debug(parameter);
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
        File modelFile = new File(MODEL_PATH);
        File outputFile = new File(outputPath);

        /*
         * Predict: 1. test file, 2. model file, 3. output file
         */
        Predict predict = new Predict(null, modelFile, outputFile);
        predict.runExampleSet(testSet);

        Evaluator eval = new Evaluator(testSet, outputFile);
        logger.warn("\n" + eval);

    }// end runJSRE method
}// end JSRE class

