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

public class IOB2Converter {

    static HashMap<String, Integer> entStat = new HashMap<String, Integer>();
    static private int numOfSeq = 0;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Iob2Converter [input file] [output file]");
            return;
        }
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(args[1]), "UTF-8"));

            String str = convertFileToIob2(args[0], true);
            out.write(str);

            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void mainForMe(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Iob2Converter [input file] [output file]");
            return;
        }
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(args[1]), "UTF-8"));

            String str = convertFileToIob2(args[0], true);
            out.write(str);

            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main1(String[] args)
    {

        File sour = new File(args[0]);
        File des = new File(args[1]);
        if (!sour.isDirectory() || !des.isDirectory())
            return;
        try
        {
            String[] dirs = sour.list(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return (name.endsWith(".txt"));
                }
            });

            //System.out.println("dirs = " + dirs.length);
            for (int i = 0; i < dirs.length; i++)
            {
                File tempi = new File(sour.getAbsolutePath() + File.separator + dirs[i]);
                if (!tempi.isFile()) continue;
                String nameDes = des.getCanonicalPath() + File.separator + dirs[i];
                String[] arg = {tempi.getCanonicalPath(), nameDes};
                //mainOne(arg);
            }
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static String convertFileToIob2(String path, boolean flag) {
        System.out.println("Convert file " + path + "\n");
        String str = "";

        //my code
        int count = 0;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF-8"));
            String line;

            while ((line = in.readLine()) != null) {
                count++;
                if (count == 2000)
                {
                    count = 0;
                    System.out.print("\n");
                }
                System.out.print(".");
                String ret = convertString2Iob2(line, flag).trim();

                if (ret.equalsIgnoreCase("")) {
                    continue;
                }
                str += ret + "\n\n";
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "";
        }

        return str;
    }

    public static String convertString2Iob2(String data, boolean flag) { //flag added by me
        //System.out.println(data);

        String ret = "", line;
        StringTokenizer lineTknr = new StringTokenizer(data, "\n");

        while (lineTknr.hasMoreTokens()) {
            line = lineTknr.nextToken();

            int curpos = 0;
            NamedEntity nent;
            boolean hasEntity = false;

            while ((nent = getNextEntity(line, curpos)) != null) {
                hasEntity = true;

                Integer oldCount = entStat.get(nent.type);
                if (oldCount == null) {
                    oldCount = 0;
                }

                entStat.put(nent.type, oldCount + 1);

                String aheadStr = line.substring(curpos, nent.beginIdx);

                VnStringTokenizer tk = new VnStringTokenizer(aheadStr, " {}");
                while (tk.hasMoreTokens()) {
                    ret += tk.nextToken() + "\t" + "O" + "\n";
                }

                VnStringTokenizer entTk = new VnStringTokenizer(nent.instance, " {}");
                if (entTk.hasMoreTokens()) {
                    ret += entTk.nextToken() + "\t" + "B-" + nent.type + "\n";
                    while (entTk.hasMoreTokens()) {
                        ret += entTk.nextToken() + "\t" + "I-" + nent.type + "\n";
                    }
                }
                curpos = nent.endIdx + 1;
            }

            if (curpos < line.length()) {
                String remain = line.substring(curpos, line.length());

                VnStringTokenizer tk = new VnStringTokenizer(remain, " ");
                while (tk.hasMoreTokens()) {
                    ret += tk.nextToken().replaceAll("[ \t]+", " ") + "\t" + "O" + "\n";
                }
            }
            if (flag)
            {
                if (hasEntity) {
                    ++numOfSeq;
                    ret += "\n";
                } else {
                    ret = "";
                }
            }
        }
        return ret;
    }

    public static NamedEntity getNextEntity(String data, int curpos) {
        NamedEntity nent = null;

        int beginOpenTag = data.indexOf("<", curpos);
        int endOpenTag = data.indexOf(">", beginOpenTag);

        if (beginOpenTag == -1 || endOpenTag == -1) {
            return null;
        }

        String openTag = data.substring(beginOpenTag + 1, endOpenTag).toLowerCase();
        String closeTag = "</" + openTag + ">";

        int closeTagIdx = data.indexOf(closeTag, endOpenTag + 1);

        if (closeTagIdx == -1) {
            return null;
        }

        nent = new NamedEntity();

        nent.beginIdx = beginOpenTag;
        nent.endIdx = closeTagIdx + closeTag.length();
        nent.instance = data.substring(endOpenTag + 1, closeTagIdx).trim();
        nent.type = openTag;

        return nent;
    }

    public static String convertFileToIob2_new(String path) {
        System.out.println("Convert file " + path + "\n");
        String str = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "UTF-8"));
            String line;

            while ((line = in.readLine()) != null) {
                System.out.print(".");
//                String ret = convertString2Iob2_new(line).trim();
                
                if (line.equalsIgnoreCase("")) {
                    continue;
                }
                str += line + "\n\n";
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "";
        }

        return str;
    }

    public static String convertString2Iob2_new(String data) {
        String ret = "", line;
        StringTokenizer lineTknr = new StringTokenizer(data, "\n");

        while (lineTknr.hasMoreTokens()) {
            line = lineTknr.nextToken();

            VnStringTokenizer tk = new VnStringTokenizer(line, " ");
            while (tk.hasMoreTokens()) {
                String token = tk.nextToken();
                char c = token.charAt(0);
                int ascii = (int) c;
                if (ascii == 65279) {
                    continue;
                }
                ret += token.replaceAll("[ \t]+", " ") + "\n";
            }
        }
        return ret;
    }
}

class NamedEntity {

    public String type; //time, per,org, loc
    public String instance;
    public int beginIdx;
    public int endIdx;
}
