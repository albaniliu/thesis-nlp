/*
Copyright (C) 2007 by Cam-Tu Nguyen

Email:	ncamtu@gmail.com

Department of Information System,
College of Technology	
Hanoi National University, Vietnam	
 */
package crfsvm.crf.een_phuong;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TaggingInputData {
//	Data Members

    List data = null;
    ArrayList<IOB2Sequence> originalData = null; //list of raw sequence    
    private static ArrayList<ContextInfo> ConjCPTpltList = new ArrayList<ContextInfo>();
    private static ArrayList<ContextInfo> RegexCPTpltList = new ArrayList<ContextInfo>();
    private static ArrayList<ContextInfo> LexCPTpltList = new ArrayList<ContextInfo>();
    private static ArrayList<ContextInfo> WordFeaCPTplList = new ArrayList<ContextInfo>();
    private boolean isInitialized = false;

//	Function Members
    public boolean init(String modelDir) {
        try {
            isInitialized = false;
            ConjCPTpltList.clear();
            RegexCPTpltList.clear();
            LexCPTpltList.clear();
            WordFeaCPTplList.clear();
            //Read feature template file........
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream feaTplStream = new FileInputStream(modelDir + File.separator + "featuretemplate.xml");
            Document doc = builder.parse(feaTplStream);

            Element root = doc.getDocumentElement();
            NodeList childrent = root.getChildNodes();
            for (int i = 0; i < childrent.getLength(); i++) {
                if (childrent.item(i) instanceof Element) {
                    Element child = (Element) childrent.item(i);
                    add2CPTpltList(child);
                }
            }
            

            System.out.println("Loading resorces....");
            LexCPGen.init(modelDir + File.separator + "LexiconStorage");
//
//            if (true)
//             return true;
            isInitialized = true;
            return true;
        } catch (SAXException se) {
            System.out.println(se.getMessage());
            return false;
        } catch (ParserConfigurationException pce) {
            System.out.println("Error in parsing feature template file");
            return false;
        } catch (IOException io) {
            System.out.println("Couldn't open one of resouces in " + modelDir);
            return false;
        }
    }

    /**
     *  Read raw data from a specified file and convert them into IOB2-format
     *	@param dataFile file from which we read data
     */
    public void readOriginalDataFromFile(String dataFile) {
        try {
            String iob2Data = IOB2Converter.convertFileToIob2_new(dataFile);
            readOriginalDataFromString(iob2Data);
        } catch (Exception e) {
            System.out.println("Couldn't open data file" + dataFile);
            return;
        }
    }

    /**
     * Get Raw data from a string and convert them into IOB2-format
     * @param text string containing our data
     */
    public void readOriginalDataFromString(String text) {
        //initialize rawData list
        if (originalData != null) {
            originalData.clear();
        } else {
            originalData = new ArrayList<IOB2Sequence>();
        }

        //tokenize text into lines
        StringTokenizer txtTokenizer = new StringTokenizer(text, "\n\r");
        while (txtTokenizer.hasMoreTokens()) {
            String line = txtTokenizer.nextToken();

            //create new original sequence
            IOB2Sequence os = new IOB2Sequence(1, false);
            os.readIOB2Seq_new(line);
            originalData.add(os);
        }
    }

    /**
     * Add all context predicate templates into the template list
     * @param node a root node
     */
    public static void add2CPTpltList(Element node) {
        NodeList childrent = node.getChildNodes();
        String cpType = node.getAttribute("value");

        for (int i = 0; i < childrent.getLength(); ++i) {
            if (childrent.item(i) instanceof Element) {
                Element child = (Element) childrent.item(i);
                ContextInfo ci = JContextInfoParser.parse(child.getAttribute("value"));

                if (cpType.equalsIgnoreCase("Conjunction")) {
                    ConjCPTpltList.add(ci);
                } else if (cpType.equalsIgnoreCase("Lexicon")) {
                    //System.out.println(ci.getName() + " " + ci.isSecondMarkovOrder());
                    LexCPTpltList.add(ci);
                } else if (cpType.equalsIgnoreCase("Regex")) {
                    RegexCPTpltList.add(ci);
                } else if (cpType.equalsIgnoreCase("WordFeature")) {
                    //System.out.println(ci.getName() + " " + ci.isSecondMarkovOrder());
                    WordFeaCPTplList.add(ci);
                }

            }
        }
    }

//	generate context predicate for the data
    public void cpGen(Map cpStr2Int) {
        if (data != null) {
            data.clear();
        } else {
            data = new ArrayList();
        }

        try {
            //go through each original sequence and generate the approciate sequence of observations
            System.out.println("Generating cps ...");
            for (int i = 0; i < originalData.size(); i++) {
                IOB2Sequence os = (IOB2Sequence) originalData.get(i);

                ArrayList<Observation> sequence = new ArrayList<Observation>();

                for (int j = 0; j < os.length(); ++j) {
                    //genearate context predicate
                    if (!isInitialized) {
                        System.out.println("Neccesary resources haven't been loaded yet!");
                        return;
                    }

                    ArrayList<String> tempCps = new ArrayList<String>();
                    String cp = "";

                    Iterator<ContextInfo> iter = ConjCPTpltList.iterator();
                    while (iter.hasNext()) {
                        ContextInfo cpInfo = iter.next();
                        cp = ConjunctCPGen.doCntxPreGen(os, j, cpInfo);
                        if (!cp.equals("")) {
                            tempCps.add(cp);
                        }
                    }


                    iter = LexCPTpltList.iterator();
                    while (iter.hasNext()) {
                        cp = LexCPGen.doCnxtPreGen(os, j, iter.next());
                        if (!cp.equals("")) {
                            tempCps.add(cp);
                        }
                    }


                    iter = RegexCPTpltList.iterator();
                    while (iter.hasNext()) {
                        cp = RegexCPGen.doCnxtPreGen(os, j, iter.next());
                        if (!cp.equals("")) {
                            tempCps.add(cp);
                        }
                    }

                    iter = WordFeaCPTplList.iterator();
                    while (iter.hasNext()) {
                        cp = WordFeaCPGen.doCntxPreGen(os, j, iter.next());
                        if (!cp.equals("")) {
                            tempCps.add(cp);
                        }
                    }

                    ArrayList<Integer> tempCpsInt = new ArrayList<Integer>();
                    for (int k = 0; k < tempCps.size(); k++) {
                        Integer cpInt = (Integer) cpStr2Int.get(tempCps.get(k));
                        if (cpInt == null) {
                            continue;
                        }
                        tempCpsInt.add(cpInt);
                    }
                    //create a new obvervation
                    Observation obsr = new Observation();

                    for (int k = 0; k < os.getNumOfColumn(); ++k) {
                        obsr.originalData += os.getToken(k, j) + Option.inputSeparator;
                    }
                    if (obsr.originalData.endsWith(Option.inputSeparator)) {
                        obsr.originalData = obsr.originalData.substring(0, obsr.originalData.length() - 1);
                    }

                    obsr.cps = new int[tempCpsInt.size()];

                    for (int k = 0; k < tempCpsInt.size(); k++) {
                        obsr.cps[k] = ((Integer) tempCpsInt.get(k)).intValue();
                    }

                    //add this overvation to sequence
                    sequence.add(obsr);
                }
                //add sequence to data list
                data.add(sequence);
            }
            originalData.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//	get data in the format <original data>| label
    public String getLabeledData(Map lbInt2Str) {
        if (data == null) {
            return null;
        }

        String result = "";

        //main loop for getting labeled data
        for (int i = 0; i < data.size(); ++i) {
            List seq = (List) data.get(i);
            for (int j = 0; j < seq.size(); j++) {
                Observation obsr = (Observation) seq.get(j);
                result += obsr.toString(lbInt2Str) + " ";
            }

            result += "\n";
        }
        return result.trim();
    }

    public String getTaggedData(Map lbInt2Str, boolean withConfi) {
        if (data == null) {
            return null;
        }

        //String[] punctuations = {".", "," , "!", "(", ")", "[", "]", "{", "}", "$", "?", "@", "\"", "-", "/", "...", ":", "'", ";", "*", "+" , "#",
        //"%", "^", "&", "=", "|", "~", "`"};

        String result = "", originalModified = "";
        //main loop for getting tagged data
        for (int i = 0; i < data.size(); ++i) {
            List seq = (List) data.get(i);

            int state = 0;

            final String NONE_ENTITY = "";
            String curEntity = NONE_ENTITY;
            boolean seqError = false;
            String oldLabel = "";
            for (int j = 0; j < seq.size(); ++j) {
                Observation obsr = (Observation) seq.get(j);
                String curLabel = (String) lbInt2Str.get(obsr.modelLabel);
                curLabel = curLabel.toLowerCase().trim();
                

                //my code
                if (functions.isPunctuations(obsr.originalData))
                    originalModified = obsr.originalData + " ";
                else
                    originalModified = "[" + obsr.originalData + "] ";
                //end my code

                //state 0 - outside any entity
                if (state == 0) {
                    if (curLabel.startsWith("b-")) {
                        curEntity = getEntityName(curLabel);

//                        if (curEntity.equalsIgnoreCase("")) {
                        state = 1;
                        //result += "<" + curEntity + "> " + "[" + obsr.originalData + "] ";
                        if (withConfi)
                            result += "<" + curEntity + " confi=" + obsr.confidence + "> " + originalModified; //"[" + obsr.originalData + "] "
                        else
                            result += "<" + curEntity + "> " + originalModified;
                        oldLabel = curEntity;

                        //my code
                    if (j == (seq.size() -1))
                        result += "</" + curEntity + "> ";
                    //end my code

//                        }
                    } else {
                        result += originalModified; //"[" + obsr.originalData + "] "
                    }
                    continue;
                }

                //state 1 - inside an entity name
                if (state == 1) {
                    if (curLabel.startsWith("i-")) {
                        String e = getEntityName(curLabel);
                        if (e.equalsIgnoreCase(curEntity)) {
                            result += originalModified; //"[" + obsr.originalData + "] "

                            //my code
                    if (j == (seq.size() -1))
                        result += "</" + curEntity + "> ";
                    //end my code

                            continue;
                        } else {
                            result += "</" + curEntity + "> " + originalModified; //"[" + obsr.originalData + "] "
                            state = 2;
                            curEntity = "";
                        }
                    } else if (curLabel.equalsIgnoreCase("o")) {
                        result += "</" + curEntity + "> " + originalModified; //"[" + obsr.originalData + "] "
                        state = 0;

                        curEntity = "";
                    } else if (curLabel.startsWith("b-")) {
                        curEntity = getEntityName(curLabel);
                        //result += "</" + oldLabel + "> " + "<" + getEntityName(curLabel) + "> " + "[" + obsr.originalData + "] ";
                        if (withConfi)
                            result += "</" + oldLabel + "> " + "<" + getEntityName(curLabel) + " confi=" + obsr.confidence + "> " + originalModified;//"[" + obsr.originalData + "] "
                        else
                            result += "</" + oldLabel + "> " + "<" + getEntityName(curLabel) + "> " + originalModified;
                        state = 1;
                        oldLabel = curEntity;

                        //my code
                    if (j == (seq.size() -1))
                        result += "</" + curEntity + "> ";
                    //end my code

                    } //else if (j == seq.size()) {
                       // result = "[" + obsr.originalData + "] " + "</" + curEntity + "> ";
                        //state = 0;
                        //curEntity = NONE_ENTITY;
                    //}
                    continue;
                }

                //state 2: error
                if (state == 2) {
                    if (!seqError) {
                        seqError = true;
                        //System.err.println("Seq: " + i);
                    }

                    if (curLabel.startsWith("i-")) {
                        //System.err.println("\tError at " + "[" + obsr.originalData + "]");
                        result += originalModified;//"[" + obsr.originalData + "] "
                        state = 0;
                    }
                    //problem here
                    if (curLabel.equalsIgnoreCase("o")) {
                        result += originalModified; //should not include curEntity "</" + curEntity + "> " +  AND "[" + obsr.originalData + "] " initially
                        state = 0;

                        curEntity = ""; //end problem
                    } else if (curLabel.startsWith("b-")) {
                        curEntity = getEntityName(curLabel);
                        //result += "<" + getEntityName(curLabel) + "> " + "[" + obsr.originalData + "] ";
                        if (withConfi)
                            result += "<" + getEntityName(curLabel) + " confi=" + obsr.confidence + "> " + originalModified;//"[" + obsr.originalData + "] "
                        else
                            result += "<" + getEntityName(curLabel) + "> " + originalModified;
                        state = 1;
                        oldLabel = curEntity;

                        //my code
                    if (j == (seq.size() -1))
                        result += "</" + curEntity + "> ";
                    //end my code

                    }
                    continue;
                }
            }
            result += "\n\n";
        }
        return result.trim();
    }

    public void writeLabeledData(Map lbInt2Str, String outputFile) {
        //Writing data in the format <ent> token * </ent>
        if (data == null) {
            return;
        }

        BufferedWriter fout = null;

        try {
            fout = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), "UTF-8"));

            fout.write(getLabeledData(lbInt2Str));

            fout.close();

        } catch (IOException e) {
            System.out.println("Couldn't create file: " + outputFile);
            return;
        }
    }

    public void writeTaggedData(Map lbInt2Str, String outputFile) {
        //Writing data in the format <ent> token * </ent>
        if (data == null) {
            return;
        }

        BufferedWriter fout = null;

        try {
            fout = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), "UTF-8"));
            
            fout.write(getTaggedData(lbInt2Str, JVnRecognizer.withConfi)); //false to not write confidence in output file

            fout.close();

        } catch (IOException e) {
            System.out.println("Couldn't create file: " + outputFile);
            return;
        }
    }

    public static String getEntityName(String label) {
        if (label.equalsIgnoreCase("O")) {
            return "";
        }

        if (label.startsWith("b-") || label.startsWith("i-")) {
            String temp = label.substring(2);
            return temp;
        }

        return "";
    }
}
