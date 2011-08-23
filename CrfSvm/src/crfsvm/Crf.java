/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.IOB2Converter;
import crfsvm.crf.een_phuong.JVnRecognizer;
import crfsvm.crf.een_phuong.TaggingTrainData;
import crfsvm.crf.een_phuong.tokenizeVietnamese;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Crf {

    static Logger logger = Logger.getLogger(Crf.class);

    static {
        DOMConfigurator.configure("log-config.xml");
    }
    public static final int DEFAULT_MODE = 0;
    public static final int MANUAL_MODE = 1;
    /**
     * Thu muc model mac dinh: thu muc model trong project
     */
    public static final String DEFAULT_MODEL_DIR = "model";
    /**
     * Duong dan mac dinh chuong trinh crf.exe: model/crf.exe
     */
    public static final String DEFAULT_PROGRAM_CRF = "model/crf.exe";
    /**
     * Ten file option mac dinh: option.txt
     */
    public static final String DEFAULT_OPTION_FILE = "option.txt";

    /**
     * Chay toan bo CRF cho mot lan train + predict. Ket qua nam o file trung ten voi file can predict + .wseg
     * @param trainFilePath duong dan file train
     * @param predictFilePath duong dan file can predict da tach tu hoac chua tach tu
     */
    public static void runCrf(String trainFilePath, String predictFilePath) {

        // Train model duoc file model.txt trong thu muc model
        train(MANUAL_MODE, trainFilePath);

        // Predict: luu ket qua trong file co ten predictFilePath + .wseg
        if (predictFilePath.contains("tagged")) {
            predict(DEFAULT_MODEL_DIR, predictFilePath);
        } else {
            // Neu predictFile la file van ban binh thuong chua tach tu
            runVnTagger(predictFilePath, predictFilePath + ".tagged");
            predict(DEFAULT_MODEL_DIR, predictFilePath + ".tagged");
        }
    }// end runCrf method

    /**
     * Tach tu doi voi van ban inputFile, luu ket qua tach tu vao file taggedFile
     * @param inputPath
     * @param taggedPath  
     */
    public static void runVnTagger(String inputPath, String taggedPath) {
        try {
            tokenizeVietnamese.token(inputPath, taggedPath);
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(Crf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }
    }// end runVnTagger method

    /**
     * Tach tu doi voi van ban inputFile, luu ket qua tach tu vao file tmp/tagged.txt
     * @param inputPath
     */
    public static void runVnTagger(String inputPath) {
        try {
            tokenizeVietnamese.token(inputPath, "tmp/tagged.txt");
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            logger.debug(ex.getMessage());
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }
    }// end runVnTagger method

    /**
     * Predict file da duoc tach tu: tagged.txt, ket qua luu trong file tagged.txt.wseg.
     * Model dir mac dinh la thu muc model trong project root
     */
    public static void predict() {
        predict(DEFAULT_MODEL_DIR);
    }// end predict method

    /**
     *  Predict file da duoc tach tu: tagged.txt, ket qua luu trong file tagged.txt.wseg
     * @param modelDir
     */
    public static void predict(String modelDir) {
        predict(modelDir, "tmp/tagged.txt");
    }// end predict method

    /**
     *  Predict file da duoc tach tu, ket qua luu trong file trung ten voi file dau vao + duoi .wseg
     * @param modelDir duong dan den thu muc model
     * @param taggedFile File da duoc tach tu 
     */
    public static void predict(String modelDir, String taggedFile) {

        File f = new File(taggedFile);
        if (!f.exists()) {
            logger.debug("No tagged file to predict");
            return;
        }// end if f.exists()
        JVnRecognizer.main(new String[]{
                    "-modeldir",
                    modelDir,
                    "-inputfile",
                    taggedFile
                });
        logger.info("File after predict: " + taggedFile + ".wseg");
    }// end predict method

    /**
     * Run chuong trinh crf.exe voi tham so modelDir va optionFile.
     * File dau vao can copy vao thu muc model voi ten train.txt va test.txt.
     * Sau khi train se duoc file model.txt trong thu muc model
     * @param program duong dan den chuong trinh crf.exe
     * @param modelDir thu muc model
     * @param optionFile duong dan den option file
     */
    // <editor-fold defaultstate="collapsed" desc="train private method">
    private static void train(String program, String modelDir, String optionFile) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(new String[]{
                        "wine",
                        program,
                        "-all",
                        "-d",
                        modelDir,
                        "-o",
                        optionFile
                    });
            InputStream inputStream = proc.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(reader);
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            logger.debug(ex.getMessage());
        }
    }// end train method
    // </editor-fold>

    /**
     * Tao file model.txt trong thu muc model tu file train.txt, test.txt trong thu muc model hoac tu file train bat ky.
     * Luu y qua trinh train chi can 1 file, khong can file test
     * @param mode 
     * <ul>
     *    <li>DEFAULT_MODE: che do train file train.txt va test.txt co trong thu muc model. Gom co cac thamso:
     *        <ul>
     *            <li>Khong tham so: file crf.exe va option.txt nam trong thu muc model, thu muc model nam trong thu muc root</li>
     *        </ul>
     *        <ul>
     *            <li>1 tham so: chi ra duong dan den file crf.exe. Yeu cau co thu muc model va file option.txt trong do</li>
     *        </ul>
     *        <ul>
     *            <li>2 tham so: chi ra duong dan den file crf.exe va thu muc model. Yeu cau co file option.txt trong model</li>
     *        </ul>
     *        <ul>
     *            <li>3 tham so: chi ra duong dan den file crf.exe, thu muc model va ten file option trong thu muc model</li>
     *        </ul>
     *    </li>
     *    <li>MANUAL_MODE: che do train voi file train va file test bat ky. Gom co cac tuy chon:
     *        <ul>
     *            <li>1 tham so: chi ra duong dan file train. Yeu cau can co thu muc model, trong do co file
     * crf.exe, option.txt</li>
     *        </ul>
     *        <ul>
     *            <li>4 tham so: chi ra duong dan den file train, crf.exe, thu muc model va ten file option trong model</li>
     *        </ul>
     *    </li>
     * </ul>
     * @param args
     */
    // <editor-fold defaultstate="collapsed" desc="train method">
    public static void train(int mode, String... args) {
        if (args.length > 4) {
            logger.info("Invalid arguments! Stop training");
            return;
        }// end if args.length > 4

        switch (mode) {
            case DEFAULT_MODE:
                // Che do mac dinh chay CRF voi 2 file train.txt va test.txt co san trong thu muc model
                if (args.length == 0) {
                    // Chay CRF voi toan bo tham so mac dinh
                    train(DEFAULT_PROGRAM_CRF, DEFAULT_MODEL_DIR, DEFAULT_OPTION_FILE);
                } else if (args.length == 1) {
                    // Chay CRF voi 1 tham so chi ra duong dan den chuong trinh CRF
                    train(args[0], DEFAULT_MODEL_DIR, DEFAULT_OPTION_FILE);
                } else if (args.length == 2) {
                    // Chay CRF voi 2 tham so:
                    //     - duong dan chuong trinh CRF
                    //     - duong dan den thu muc model
                    train(args[0], args[1], DEFAULT_OPTION_FILE);
                } else if (args.length == 3) {
                    // Chay CRF voi 3 tham so: 
                    //     - duong dan den chuong trinh CRF
                    //     - duong dan den thu muc model
                    //     - ten file option
                    train(args[0], args[1], args[2]);
                }
                break;
            case MANUAL_MODE:
                // Che do chay CRF cho phep chon flle train bat ky
                if (args.length == 1) {
                    // Chay CRF 1 tham so:
                    //     - duong dan file train
                    String iobFile = args[0] + ".iob";
                    String featureFile = args[0] + ".feature";
                    // Convert sang dang iob
                    IOB2Converter.main(new String[]{
                                args[0],
                                iobFile
                            });
                    // Chuyen thanh dang dac trung
                    TaggingTrainData.main(new String[]{
                                iobFile,
                                featureFile,
                                DEFAULT_MODEL_DIR
                            });
                    try {
                        CopyFile.copyfile(featureFile, "model/train.txt");
                        CopyFile.copyfile(featureFile, "model/test.txt");
                    } catch (IOException iOException) {
                        logger.debug(iOException.getMessage());
                    }// end try catch
                    train(DEFAULT_PROGRAM_CRF, DEFAULT_MODEL_DIR, DEFAULT_OPTION_FILE);
                } else if (args.length == 4) {
                    // Chay CRF 4 tham so:
                    //     - duong dan file train
                    //     - duong dan den chuong trinh CRF
                    //     - duong dan den thu muc model
                    //     - ten file option
                    
                    String iobFile = args[0] + ".iob";
                    String featureFile = args[0] + ".feature";
                    // Convert sang dang iob
                    IOB2Converter.main(new String[]{
                                args[0],
                                iobFile
                            });
                    // Chuyen thanh dang dac trung
                    TaggingTrainData.main(new String[]{
                                iobFile,
                                featureFile,
                                args[2]
                            });
                    try {
                        CopyFile.copyfile(featureFile, "model/train.txt");
                        CopyFile.copyfile(featureFile, "model/test.txt");
                    } catch (IOException iOException) {
                        logger.debug(iOException.getMessage());
                    }// end try catch
                    train(args[1], args[2], args[3]);
                } else {
                    logger.info("Invalid arguments! Stop training");
                    return;
                }// end if
                break;
            default:
                logger.info("Invalid mode! Stop training");
                return;
        }
    }// end train method
    // </editor-fold>
}// end Crf class

