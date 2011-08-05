/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.io.*;
import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author Thien
 */
public class procedureNewTrainingData
{
    private static String nameFile = "toCreateNewTraningData.txt";

    public static String getPath(String sub)
    {
        try
        {
            File f = new File(".");
            return f.getCanonicalPath() + File.separator + "data" + File.separator + sub + File.separator;
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public static String action(fixProcedure fix)
    {
        if (fix == null) return "";

        BufferedWriter fout = null;
        try
        {
            fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getPath("folds") + nameFile), "UTF-8"));
            fout.write(fix.getStringForIob2(fix.fileNameForError));
            fout.close();
            String[] argsIob2 = {getPath("folds") + nameFile, getPath("iob2") + nameFile};
            IOB2Converter.main(argsIob2);
            File fi = new File(".");
            String model = fi.getCanonicalPath() + File.separator + "model";
            String[] argsTagged = {getPath("iob2") + nameFile, getPath("tagged") + nameFile, model};
            TaggingTrainData.main(argsTagged);
            return create(fix.extendedLists, getPath("tagged") + nameFile, fix.nameFile);
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public static String create(List li, String inputFile, String name)
    {
        BufferedWriter fout = null;
        String result = "";
        try
        {
            fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getPath("newTrainingData") + "produced_" + name), "UTF-8"));
            if (li == null) return "";
            if (li.isEmpty()) return "";
            for (int i = 0; i < li.size(); i++)
            {
                ArrayList<entities.primaryEntity> ens = (ArrayList<entities.primaryEntity>) li.get(i);
                if (ens.isEmpty()) continue;
                for (int run = 0; run < ens.size(); run++)
                {
                    entities.primaryEntity en = ens.get(run);
                    result += getLines(inputFile, en.startVn, en.endVn, en.line) + "\n\n";
                }
            }
            fout.write(result);
            fout.close();
            return result.trim();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public static String getLines(String inputFile, int start, int end, int pos)
    {
        BufferedReader fin = null;
        String result = "";
        if ((start < 0) || (end < 0) || (pos < 0) || (start > end)) return "";
        try
        {
            int countToken = -1, countLine = 0;
            String line = "";
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
            while((line = fin.readLine()) != null)
            {
                if (line.trim().length() == 0)
                {
                    countLine++;
                    continue;
                }
                if (countLine > pos) break;
                if (pos == countLine)
                {
                    countToken++;
                    if ((start <= countToken) && (countToken <= end))
                       result += line + "\n";
                    if (countToken > end) break;
                }
            }
            fin.close();
            return result.trim();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public static void main(String[] args)
    {
        fixProcedure fix = new fixProcedure(false);
        fix.fixOperation("saveOurLife.txt", "toSaveOurLife.txt", 0.9, 0.7, false);
        System.out.println(action(fix));
        /*
        String in = "." + File.separator + "data" + File.separator + "tagged" + File.separator + "help.txt";
        System.out.println(getLines(in, 0, 3, 0));
        ArrayList<entities.primaryEntity> ens = new ArrayList<entities.primaryEntity>();
        entities.primaryEntity en1 = new entities.primaryEntity(0, 3, 0, "", "");
        ens.add(en1);
        ArrayList<entities.primaryEntity> enss = new ArrayList<entities.primaryEntity>();
        entities.primaryEntity en2 = new entities.primaryEntity(0, 2, 1, "", "");
        enss.add(en2);
        List li = new ArrayList();
        li.add(ens);
        li.add(enss);
        System.out.println(create(li, in, "HELP.txt"));
         *
         */
    }
}
