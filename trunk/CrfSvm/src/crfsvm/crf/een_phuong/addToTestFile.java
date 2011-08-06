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
public class addToTestFile
{
    private static String name = "addToTestFile.txt";
    private static String input = "." + File.separator + "data" + File.separator + "dataToRetrain" + File.separator + "entity";

    private static class filterTxt implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".txt");
        }
    }

    public static void operate(String folder)
    {
        try
        {
            File f = new File(folder);
            String[] dirs = f.list(new filterTxt());
            if (dirs == null) return;
            if (dirs.length == 0) return;
            String save = "";
            int count = 0;
            for (int i = 0; i < dirs.length; i++)
            {
                //createData(folder + File.separator + "doc-" + (i + 1) + ".txt");
                createData(folder + File.separator + dirs[i]);
                save += getString(getPath("tagged", true) + name);
                if ((i%5) == 4)
                {
                    count++;
                    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getPath("toAddTestData", true) + "toAdd" + count + ".txt"), "UTF-8"));
                    wr.write(save);
                    wr.close();
                    save = "";
                }
            }
            System.out.println("Saving testData successfully");
        }
        catch(Exception ex)
        {
            System.out.println("Error in addToTestFile.operate: " + ex);
        }
    }

    public static void createData(String input)
    {
        try
        {
            CopyFile copy = new CopyFile();
            copy.copyfile(input, getPath("folds", true) + name);
            String[] argsIob2 = {getPath("folds", true) + name, getPath("iob2", true) + name};
            IOB2Converter.mainForMe(argsIob2);
            File fi = new File(".");
            String model = fi.getCanonicalPath() + File.separator + "model";
            String[] argsTagged = {getPath("iob2", true) + name, getPath("tagged", true) + name, model};
            TaggingTrainData.main(argsTagged);
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static String getString(String input)
    {
        BufferedReader br = null;
        try
        {
             String save = "", line = "";
             br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
             while ((line = br.readLine()) != null)
             {
                 save += line + "\n";
             }
             return save;
        }
        catch (IOException ioe)
        {
            return "";
        }
    }

    public static String getPath(String sub, boolean flag)
    {
        try
        {
            File f = new File(".");
            if (flag)
                return f.getCanonicalPath() + File.separator + "data" + File.separator + sub + File.separator;
            else
                return f.getCanonicalPath() + File.separator + "data" + File.separator + sub;
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public static void main(String[] args)
    {
        operate(input);
        
    }
}
