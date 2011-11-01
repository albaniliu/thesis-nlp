
//-modeldir C:\EEN_Phuong_interfaceF\model -inputfile C:\EEN_Phuong_interfaceF\vnexperss.txt

package crfsvm.crf.een_phuong;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JVnRecognizer {

    public static boolean withConfi = false;

    private String modelDir = "";
    static private Maps taggerMaps = null;
    private Dictionary taggerDict = null;
    private FeatureGen taggerFGen = null;
    private Viterbi taggerVtb = null;
    static private Model taggerModel = null;

    public boolean init(String modelDir) {
        Option taggerOpt = new Option(modelDir);
        if (!taggerOpt.readOptions()) {
            return false;
        }
        
        // Dung
        this.modelDir = modelDir;
        taggerMaps = new Maps();
        taggerDict = new Dictionary();
        taggerFGen = new FeatureGen(taggerMaps, taggerDict);
        taggerVtb = new Viterbi();

        taggerModel = new Model(taggerOpt, taggerMaps, taggerDict, taggerFGen, taggerVtb);
        if (!taggerModel.init()) {
            System.out.println("Couldn't load the model");
            System.out.println("Check the <model directory> and the <model file> again");
            return false;
        }
        return true;
    }

//  do entity recognizer and return string with tokens labeled
    //this is useful for further processing 
    public String entityRecognize(String text) {
        TaggingInputData taggerData = new TaggingInputData();
        if (!taggerData.init(modelDir)) {
            return null;
        }


        taggerData.readOriginalDataFromString(text);
        taggerData.cpGen(taggerMaps.cpStr2Int);

        // inference
        taggerModel.inferenceAll(taggerData.data, taggerMaps.lbStr2Int);
        return taggerData.getLabeledData(taggerMaps.lbInt2Str);
    }

    public String entityRecognize(TaggingInputData taggerData) {
        //generate context predicates
        taggerData.cpGen(taggerMaps.cpStr2Int);

        //inference
        taggerModel.inferenceAll(taggerData.data, taggerMaps.lbStr2Int);
        return taggerData.getLabeledData(taggerMaps.lbInt2Str);
    }

    public static void entityRecognize(TaggingInputData taggerData, String outputFile) {
        //generate context predicates
        taggerData.cpGen(taggerMaps.cpStr2Int);

        //inference
        taggerModel.inferenceAll(taggerData.data, taggerMaps.lbStr2Int, outputFile + ".confi"); // no outputFile + ".confi" in arguments initially
        taggerData.writeLabeledData(taggerMaps.lbInt2Str, outputFile);
    }

    //do the entity recognizer and return string with word boundaries marked
    //this is useful for visual representation
    public String entityBoundaryMark(String text) {
        TaggingInputData taggerData = new TaggingInputData();
        if (!taggerData.init(modelDir)) {
            return null;
        }


        taggerData.readOriginalDataFromString(text);
        taggerData.cpGen(taggerMaps.cpStr2Int);

        // inference
        taggerModel.inferenceAll(taggerData.data, taggerMaps.lbStr2Int);
        return taggerData.getTaggedData(taggerMaps.lbInt2Str, withConfi);
    }

    public String entityBoundaryMark(TaggingInputData taggerData) {
        //generate context predicates
        taggerData.cpGen(taggerMaps.cpStr2Int);

        // inference
        taggerModel.inferenceAll(taggerData.data, taggerMaps.lbStr2Int);
        return taggerData.getTaggedData(taggerMaps.lbInt2Str, withConfi);
    }

    public void entityBoundaryMark(TaggingInputData taggerData, String outputFile) {
        //generate context predicates
        taggerData.cpGen(taggerMaps.cpStr2Int);

        //inference
        taggerModel.inferenceAll(taggerData.data, taggerMaps.lbStr2Int, ""); //no outputFile + ".confi" in arguments initially; outputFile + ".confi"
        taggerData.writeTaggedData(taggerMaps.lbInt2Str, outputFile);
    //taggerData.writeLabeledData(taggerMaps.lbInt2Str, outputFile);
    }

    /*main method for using this tool from command line
     */
    public static void main(String[] args) {
       

        if (!checkArgs(args)) {
            displayHelp();
            return;
        }

        String modelDir = args[1];
        boolean isInputFile = true;
        if (args[2].compareToIgnoreCase("-inputfile") != 0) {
            isInputFile = false;
        }
        String inputFile = "";
        String inputDir = "";
        if (isInputFile) {
            inputFile = args[3];
        } else {
            inputDir = args[3];
        }

        Option taggerOpt = new Option(modelDir);
        if (!taggerOpt.readOptions()) {
            return;
        }

        JVnRecognizer nrcnr = new JVnRecognizer();
        if (!nrcnr.init(modelDir)) {
            return;
        }

        TaggingInputData taggerData = new TaggingInputData();
        if (!taggerData.init(modelDir)) {
            return;
        }

        if (isInputFile) {
            // doc file da duoc tach tu boi vntagger
            taggerData.readOriginalDataFromFile(inputFile);
//            entityRecognize(taggerData, "output.txt");
            nrcnr.entityBoundaryMark(taggerData, inputFile + ".wseg");
            //nrcnr.entityRecognize(taggerData, inputFile + ".label");
        }

        if (!isInputFile) {
            if (inputDir.endsWith(File.separator)) {
                inputDir = inputDir.substring(0, inputDir.length() - 1);
            }

            File dir = new File(inputDir);
            String[] children = dir.list(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".sent");
                }
            });

            for (int i = 0; i < children.length; i++) {
                String filename = inputDir + File.separator + children[i];
                if ((new File(filename)).isDirectory()) {
                    continue;
                }

                taggerData.readOriginalDataFromFile(filename);
            //ws.wordBoundaryMark(taggerData, filename + ".wseg");
            }
        }

    } // end of the main method

    public static void mainForResearch(String path, String in, String out) {

        try
        {
            //System.out.print("Over here");
            //File fi = new File(".");
            //System.out.print("fiiiiiiiii = " + fi.getCanonicalPath());
            String tagg = path + File.separator + "vnTagger" + File.separator + "input.txt";
            CopyFile.copyfile(in, tagg);
            //System.out.print("tagg = " + tagg);
            //vnTokenizer vntokenizer = new vnTokenizer();
                //vntokenizer.token();
                tokenizeVietnamese.token(path);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        String modelDir = path + File.separator + "model";

        String inputFile = path + File. separator + "input.txt";

        
        Option taggerOpt = new Option(modelDir);
        if (!taggerOpt.readOptions()) {
            return;
        }

        JVnRecognizer nrcnr = new JVnRecognizer();
        if (!nrcnr.init(modelDir)) {
            return;
        }

        
        TaggingInputData taggerData = new TaggingInputData();
        
        if (!taggerData.init(modelDir)) {
            return;
        }

        taggerData.readOriginalDataFromFile(inputFile);
//            entityRecognize(taggerData, "output.txt");
        nrcnr.entityBoundaryMark(taggerData, out);
            //nrcnr.entityRecognize(taggerData, inputFile + ".label");

    } // end of the main method

    public static boolean checkArgs(String[] args) {
        // case 1: CRFChunker -modeldir <model directory> -inputfile <input data file>
        // case 2: CRFChunker -modeldir <model directory> -inputdir <input data directory>

        if (args.length < 4) {
            return false;
        }

        if (args[0].compareToIgnoreCase("-modeldir") != 0) {
            return false;
        }

        if (!(args[2].compareToIgnoreCase("-inputfile") == 0 ||
                args[2].compareToIgnoreCase("-inputdir") == 0)) {
            return false;
        }

        return true;
    }

    

    public static void displayHelp() {
        System.out.println("Usage:");
        System.out.println("\tCase 1: JVnRecognizer -modeldir <model directory> -inputfile <input data file>");
        System.out.println("\tCase 2: JVnRecognizer -modeldir <model directory> -inputdir <input data directory>");
        System.out.println("Where:");
        System.out.println("\t<model directory> is the directory contain the model and option files");
        System.out.println("\t<input data file> is the file containing input sentences that need to");
        System.out.println("\tbe tagged (each sentence on a line)");
        System.out.println("\t<input data directory> is the directory containing multiple input .sent files");
        System.out.println();
    }
}
