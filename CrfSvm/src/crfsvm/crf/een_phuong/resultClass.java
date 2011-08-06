/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.awt.Component;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Thien
 */
public class resultClass
{
    private static double highConfidence = 0.9, lowConfidence = 0.85;
    private static int timeLoop = 1, numFilePerTime = 10;
    private static int fileDone = 0, thresholdFileDone = 30;
    private static javax.swing.JTextArea component = null;

    public resultClass(double hiConfi, double lowConfi, int timeLoop, int numFilePerTime, int fileDone, int thresholdFileDone, javax.swing.JTextArea component)
    {
        this.highConfidence = hiConfi;
        this.lowConfidence = lowConfi;
        this.timeLoop = timeLoop;
        this.numFilePerTime = numFilePerTime;
        this.fileDone = fileDone;
        this.thresholdFileDone = thresholdFileDone;
        this.component = component;
    }

    public resultClass(javax.swing.JTextArea component)
    {
        this.component = component;
    }

    public static String getPath(String sub, boolean flag)
    {
        try
        {
            File f = new File(".");
            if (flag)
                return f.getCanonicalPath() + File.separator + "data" + File.separator + "dataToRetrain" + File.separator + sub + File.separator;
            return f.getCanonicalPath() + File.separator + "data" + File.separator + "dataToRetrain" + File.separator + sub;
        }
        catch (Exception ex)
        {
            out("Error: " + ex.toString());
            return "";
        }
    }

    public static void callCRF()
    {
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        String dir = "." + File.separator + "crf.exe";
        try
        {
            process = rt.exec(dir + " -all" + " -d ./model" + " -o option.txt");
            process.waitFor();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(vnTokenizer.class.getName()).log(Level.SEVERE, null, ex);

        }
        catch (InterruptedException interup)
        {
            
        }
    }

    public static void appentToTrainingFile(String input, int time)
    {
        if (time < 1) return;
        BufferedWriter bw = null;
        BufferedReader br = null, brn = null;
        try
        {
             String toAppend = "", save = "", line = "";
             br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
             while ((line = br.readLine()) != null)
             {
                 if (line.trim().isEmpty())
                 {

                     toAppend += save.trim() + "\n\n";
                     save = "";
                     continue;
                 }
                 save += line + "\n";
             }

             String temp = toAppend;
             toAppend = "";
             for (int i = 0; i < time; i++)
                 toAppend += temp;

             br.close();

             String option = "." + File.separator + "model" + File.separator + "option.txt";
             brn = new BufferedReader(new InputStreamReader(new FileInputStream(option), "UTF-8"));
             line = brn.readLine();
             brn.close();
             String nameTrainingFile = line.substring(line.indexOf("=") + 1, line.length());
             String trainingFile = "." + File.separator + "model" + File.separator + nameTrainingFile;
             bw = new BufferedWriter(new FileWriter(trainingFile, true));
             bw.write(toAppend);
             bw.flush();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            if (bw != null) try
            {
               bw.close();
            }
            catch (IOException ioe2)
            {
            }
        }
    }

    public static boolean updateTraninigData(int number)
    {
        try
        {
            if (number < 1)
            {
                out("No data added........");
                return false;
            }
            File f = new File(getPath("segmented", false));
            String[] dirsTemp = f.list(new filterTxt());
            if (dirsTemp == null)
            {
                out("No file found in data pool => no data added.......");
                return false;
            }
            if (dirsTemp.length < number)
            {
                out("Not enough data in data pool => no data added.......");
                return false;
            }
            String[] dirs = new String[number];
            for (int i = 0; i < number; i++)
                dirs[i] = dirsTemp[i];
            CopyFile copy = new CopyFile();
            String taggedPath = "." + File.separator + "input.txt.wseg";
            String produced = "." + File.separator + "data" + File.separator + "newTrainingData" + File.separator + "produced_";
            for (int i = 0; i < number; i++)
            {
                functions.tagFileChunked(getPath("segmented", true) + dirs[i]);
                copy.copyfile(taggedPath, getPath("taggedByOldModel", true) + dirs[i]);
                fileDone++;
                boolean logi = false;
                if (fileDone > thresholdFileDone)
                    logi = true;
                fixProcedure fix = new fixProcedure(logi);
                fix.fixOperation(getPath("taggedByOldModel", true) + dirs[i], getPath("NP", true) + dirs[i], highConfidence, lowConfidence, logi);
                procedureNewTrainingData.action(fix);
                appentToTrainingFile(produced + dirs[i], timeLoop);
            }
            moveFiles(dirs);
            out("Append new data to training data SUCCESFULLLY => RUN CRF TO GET NEW MODEL");
            //out("Added " + fileDone + " docs to training data");
            //out("Preparing to call CRF to retrain the model................");
            //out("This may take 3 - 4 minutes, please be patient.................");
            return true;
        }
        catch (Exception ex)
        {
            out("Error in resultClass.updateTraninigData: " + ex.toString());
            return false;
        }
    }

    private static void moveFiles(String[] dirs)
    {
        CopyFile copy = new CopyFile();
        for (int i = 0; i < dirs.length; i++)
        {
            moveOneFiles(copy, getPath("segmented", true) + dirs[i], getPath("segmented", true) + "done" + File.separator + dirs[i]);
            moveOneFiles(copy, getPath("NP", true) + dirs[i], getPath("NP", true) + "done" + File.separator + dirs[i]);
        }
    }

    private static void moveOneFiles(CopyFile copy, String source, String des)
    {
        try
        {
            File f = new File(source);
            copy.copyfile(source, des);
            f.delete();
        }
        catch (Exception ex)
        {
            out("Can't not move file: " + source + " to: " + des);
        }
    }

    private static class filterTxt implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".txt");
        }
    }

    public static boolean saveResult()
    {
        String log = "." + File.separator + "model" + File.separator + "trainlog.txt";
        String model = "." + File.separator + "model" + File.separator + "model.txt";
        String result = "." + File.separator + "data" + File.separator + "result";
        String modelResult = result + File.separator + "models";
        BufferedReader fin = null;
        BufferedWriter fout = null;
        String line = "", save = "", loc = "", per = "", org = "";
        boolean flag = false;
        int count = 0, isSucess = 0, repeat = 0;
        try
        {
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(log), "UTF-8"));
            while ((line = fin.readLine()) != null)
            {
                if (line.trim().isEmpty()) continue;
                char ch =  line.charAt(0);
                if (!Character.isLetter(ch) && !flag) continue;
                if (line.startsWith("Number of training iterations:"))
                {
                    save += line + "\n\n\n";
                    String te = line.substring(31).trim();
                    repeat = Integer.parseInt(te);
                    continue;
                }
                if (line.startsWith("Iteration:"))
                {
                    isSucess++;
                    continue;
                }
                if (line.startsWith("The training process elapsed:"))
                {
                    flag = true;
                    continue;
                }
                if(flag)
                {
                    count++;
                    if ((count == 2) || (count == 3) || (count == 4))
                        save += line + "\n";
                    if ((count == 11) || (count == 12))
                        per += line + "\n";
                    if ((count == 13) || (count == 14))
                        org += line + "\n";
                    if ((count == 8) || (count == 17))
                        loc += line + "\n";
                }
            }
            if (isSucess < repeat)
            {
                out("CRF Call encounter an error => the process can not continue");
                return false;
            }
            save += "\n" + per + "\n" + org + "\n" + loc;
            fin.close();

            File re = new File(result);
            String[] dirs = re.list(new filterTxt());
            String name = "log_1.txt", nameModel = "model_1.txt";
            if (dirs != null)
            {
                if (dirs.length > 0)
                {
                    name = "log_" + (dirs.length + 1) + ".txt";
                    nameModel = "model_" + (dirs.length + 1) + ".txt";
                }
            }

            fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result + File.separator + name), "UTF-8"));
            fout.write(save);
            fout.close();
            CopyFile copy = new CopyFile();
            copy.copyfile(model, modelResult + File.separator + nameModel);
            out("Save " + name + " and " + nameModel + " SUCCESFULLY");
            out("Starting a new loop ....................");
            return true;
        }
        catch (Exception ex)
        {
            out("Error in ResultClass.saveResult: " + ex.toString());
            return false;
        }
    }

    public static void out(String o)
    {
        //System.out.println(o);
        component.append(o);
    }

    public static void done()
    {
        if(!saveResult()) return;
        do
        {
            if(!updateTraninigData(numFilePerTime))
            {
                out("COMPLETED");
                return;
            }
            callCRF();
            out("Trained new model Succesfully with " + fileDone + " docs added to training data");
            out("Saving model and log...............");
            if(!saveResult())
            {
                return;
            }
        }
        while(true);
    }

    public static void doneManual()
    {
        if(!saveResult()) return;
        if(!updateTraninigData(numFilePerTime))
            {
                out("COMPLETED");
                return;
            }
    }

    public static void main(String[] args)
    {
        doneManual();
        //out("EXPERIMENT COMPLETE......");
        //out("CHECK YOUR RESULT!!!!!!!!!!");
        //saveResult();
    }
}
