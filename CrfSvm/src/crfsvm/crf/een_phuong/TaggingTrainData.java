/*
Copyright (C) 2007 by Cam-Tu Nguyen

Email:	ncamtu@gmail.com

Department of Information System,
College of Technology	
Hanoi National University, Vietnam	
 */
//C:\EEN_Phuong_interfaceF\data\iob2\labs\test-1.txt C:\EEN_Phuong_interfaceF\data\tagged\labs\test-1.txt C:\EEN_Phuong_interfaceF\model
//C:\EEN_Phuong_interfaceF\data\folds\labs\test-1.txt C:\EEN_Phuong_interfaceF\data\iob2\labs\test-1.txt
package crfsvm.crf.een_phuong;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;

public class TaggingTrainData {

    private static ArrayList<ContextInfo> ConjCPTpltList = new ArrayList<ContextInfo>();
    private static ArrayList<ContextInfo> RegexCPTpltList = new ArrayList<ContextInfo>();
    private static ArrayList<ContextInfo> LexCPTpltList = new ArrayList<ContextInfo>();
    private static ArrayList<ContextInfo> WordFeaCPTplList = new ArrayList<ContextInfo>();
    private static BufferedReader in;
    private static BufferedWriter out;

    /**
     * Chuyen file iob thanh dang dac trung<br/>
     * Usage: TaggingTrainData [train file] [tagged file] [model dir]
     * @param av 
     */
    public static void main(String[] av) {
        if (av.length != 3) {
            System.err.println("Usage: TaggingTrainData [train file] [tagged file] [model dir]");
            return;
        }

        try {
            //Read feature template file ...
            if ((ConjCPTpltList.isEmpty()) && (RegexCPTpltList.isEmpty()) && (LexCPTpltList.isEmpty()) && (WordFeaCPTplList.isEmpty())) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                String modelDir = av[2];
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
            }

            //Read Data file and generate a tagged file
            int count = 0;
            String line, sequence = "";

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(av[0]), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(av[1]), "UTF-8"));
            LexCPGen.init(av[2] + File.separator + "LexiconStorage");

            while ((line = in.readLine()) != null) {
                if (line.trim().length() == 0)//white line -> end of previous sequence
                {
                    doCPGen(sequence, out);
                    count++;
                    if (count % 50 == 1) {
                        System.out.print("\n");
                    }
                    System.out.print(".");

                    sequence = "";
                    out.newLine();
                    continue;
                }
                sequence += line + "\n";
            }
            if (!sequence.trim().equals("")) {
                doCPGen(sequence, out);
            }
            out.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }

    }

    public static void main1(String args[]) {
        File sour = new File(args[0]);
        File des = new File(args[1]);
        if (!sour.isDirectory() || !des.isDirectory()) {
            return;
        }
        try {
            String[] dirs = sour.list(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return (name.endsWith(".txt"));
                }
            });

            //System.out.println("dirs = " + dirs.length);
            for (int i = 0; i < dirs.length; i++) {
                File tempi = new File(sour.getAbsolutePath() + File.separator + dirs[i]);
                if (!tempi.isFile()) {
                    continue;
                }
                String nameDes = des.getCanonicalPath() + File.separator + dirs[i];
                String[] arg = {tempi.getCanonicalPath(), nameDes, args[2]};
                //mainOne(arg);
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }

    public static void add2CPTpltList(Element node) {
        NodeList childrent = node.getChildNodes();
        String cpType = node.getAttribute("value");

        for (int i = 0; i < childrent.getLength(); ++i) {
            if (childrent.item(i) instanceof Element) {
                Element child = (Element) childrent.item(i);
                ContextInfo ci = JContextInfoParser.parse(child.getAttribute("value"));
                //System.out.println(child.getAttribute("value"));

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

    private static void doCPGen(String sequence, BufferedWriter out) throws IOException {
        sequence = sequence.trim();
        IOB2Sequence os = new IOB2Sequence(2, true);
        os.readIOB2Seq(sequence);

        ArrayList<String> tempCps = new ArrayList<String>();

        //generate observation-view context predicates
        for (int i = 0; i < os.length(); ++i) {
            String cp = "";
            Iterator<ContextInfo> iter = ConjCPTpltList.iterator();
            while (iter.hasNext()) {
                ContextInfo cpInfo = iter.next();
                cp = cp.trim() + " " + ConjunctCPGen.doCntxPreGen(os, i, cpInfo);
            }


            iter = LexCPTpltList.iterator();
            while (iter.hasNext()) {
                cp = cp.trim() + " " + LexCPGen.doCnxtPreGen(os, i, iter.next());
            }


            iter = RegexCPTpltList.iterator();
            while (iter.hasNext()) {
                cp = cp.trim() + " " + RegexCPGen.doCnxtPreGen(os, i, iter.next());
            }

            iter = WordFeaCPTplList.iterator();
            while (iter.hasNext()) {
                cp = cp.trim() + " " + WordFeaCPGen.doCntxPreGen(os, i, iter.next());
            }

            tempCps.add(cp);
        }

        //generate sequence-view context predicates

        //print cps to a tagged file
        for (int i = 0; i < tempCps.size(); ++i) {
            out.write(tempCps.get(i));

            if (os.label) {
                out.write(" " + os.getToken(os.getNumOfColumn() - 1, i));
            }
            out.newLine();
        }
    }
}
