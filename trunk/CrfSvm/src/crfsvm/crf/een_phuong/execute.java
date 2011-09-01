/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * execute.java
 *
 * Created on Sep 15, 2010, 10:46:27 AM
 */

package crfsvm.crf.een_phuong;

import java.nio.ByteOrder;
import javax.imageio.stream.IIOByteBuffer;
import javax.swing.UIManager;
import org.jdesktop.application.Action;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.JTextComponent;
import java.util.Date;
import java.util.StringTokenizer;

/**
 *
 * @author Thien
 */
public class execute extends javax.swing.JFrame {


    /** Creates new form execute */
    public execute() {
        try
        {
            this.setTitle("Execute FrameWork");
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            initComponents();
        }
        catch (Exception ex)
        {

        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setName("textArea"); // NOI18N
        jScrollPane1.setViewportView(textArea);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(execute.class);
        button.setText(resourceMap.getString("button.text")); // NOI18N
        button.setName("button"); // NOI18N
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(button)
                .addContainerGap(172, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionPerformed
        // TODO add your handling code here:
        doneManual();
    }//GEN-LAST:event_buttonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new execute().setVisible(true);
            }
        });
    }

    public String getPath(String sub, boolean flag)
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

    public void appentToTrainingFile(String input, int time)
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
                    if (!save.isEmpty())
                    {
                        toAppend += save.trim() + "\n\n";
                        save = "";
                    }
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

    public boolean updateTraninigData(int number)
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
                dirs[i] = dirsTemp[i]; //"doc-" + (fileDone + i + 1) + ".txt";
            CopyFile copy = new CopyFile();
            String taggedPath = "." + File.separator + "input.txt.wseg";
            String produced = "." + File.separator + "data" + File.separator + "newTrainingData" + File.separator + "produced_";
            //String save = "";
            for (int i = 0; i < number; i++)
            {
                functions.tagFileChunked(getPath("segmented", true) + dirs[i]);
                copy.copyfile(taggedPath, getPath("taggedByOldModel", true) + dirs[i]);
                fileDone++;
                boolean logi = false;
                //if (fileDone > thresholdFileDone)
                //    logi = true;
                fixProcedure fix = new fixProcedure(logi);
                fix.fixOperation(getPath("taggedByOldModel", true) + dirs[i], getPath("NP", true) + dirs[i], highConfidence, lowConfidence, true);
                //fix.fixOperation(getPath("segmented", true) + dirs[i], getPath("NP", true) + dirs[i], highConfidence, lowConfidence);
                //save += procedureNewTrainingData.action(fix) + "\n\n";
                //
                procedureNewTrainingData.action(fix);
                appentToTrainingFile(produced + dirs[i], timeLoop);
            }
            //File ff = new File("." + File.separator + "data" + File.separator + "toAddTrainData");
            //String[] ffdirs = ff.list(new filterTxt());
            //String nameToAddTrain = "trainAdd_1.txt";
            //if (ffdirs != null)
            //{
            //    if (ffdirs.length != 0)
            //    {
            //        nameToAddTrain = "trainAdd_" + (ffdirs.length + 1) + ".txt";
            //    }
            //}
            //String outfi = "." + File.separator + "data" + File.separator + "toAddTrainData" + File.separator + nameToAddTrain;
            //BufferedWriter wrr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfi), "UTF-8"));
            //wrr.write(save);
            //wrr.close();
            moveFiles(dirs);
            out("Appended new data to training data SUCCESFULLLY => RUN CRF TO GET NEW MODEL");
            out("You are working at loop : " + (fileDone/numFilePerTime));
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

    private void moveFiles(String[] dirs)
    {
        CopyFile copy = new CopyFile();
        for (int i = 0; i < dirs.length; i++)
        {
            moveOneFiles(copy, getPath("segmented", true) + dirs[i], getPath("segmented", true) + "done" + File.separator + dirs[i]);
            moveOneFiles(copy, getPath("NP", true) + dirs[i], getPath("NP", true) + "done" + File.separator + dirs[i]);
        }
    }

    private void moveOneFiles(CopyFile copy, String source, String des)
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

    private class filterTxt implements FilenameFilter
    {
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".txt");
        }
    }

    public boolean saveResult()
    {
        String log = "." + File.separator + "model" + File.separator + "trainlog.txt";
        String model = "." + File.separator + "model" + File.separator + "model.txt";
        String train = "." + File.separator + "model" + File.separator + "train.txt";
        String result = "." + File.separator + "data" + File.separator + "result";
        String modelResult = result + File.separator + "models";
        String trainResult = result + File.separator + "trains";
        BufferedReader fin = null;
        String line = "";
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
                    String te = line.substring(31).trim();
                    repeat = Integer.parseInt(te);
                    continue;
                }
                if (line.startsWith("Iteration:"))
                {
                    isSucess++;
                    continue;
                }
            }
            if (isSucess < repeat)
            {
                out("CRF Call encounter an error => the process can not continue");
                return false;
            }
            fin.close();

            File re = new File(result);
            String[] dirs = re.list(new filterTxt());
            String name = "log_1.txt", nameModel = "model_1.txt", nameTrain = "train_1.txt";
            if (dirs != null)
            {
                if (dirs.length > 0)
                {
                    name = "log_" + (dirs.length + 1) + ".txt";
                    nameModel = "model_" + (dirs.length + 1) + ".txt";
                    nameTrain = "train_" + (dirs.length + 1) + ".txt";
                }
            }
            CopyFile copy = new CopyFile();
            copy.copyfile(log, result + File.separator + name);
            copy.copyfile(train, trainResult + File.separator + nameTrain);
            copy.copyfile(model, modelResult + File.separator + nameModel);
            out("Save " + name + " and " + nameModel + " and " + nameTrain + "SUCCESFULLY");
            out("Start a new loop ....................");
            return true;
        }
        catch (Exception ex)
        {
            out("Error in ResultClass.saveResult: " + ex.toString());
            return false;
        }
    }

    public  void out(String o)
    {
        //System.out.println(o);
        textArea.append(o + "\n\n");
    }

    public void doneManual()
    {
        if(!saveResult()) return;
        if(updateTraninigData(numFilePerTime))
            {
                out("COMPLETED!!!!!!!!!!!!!!!!!");
                return;
            }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
    private static double highConfidence = 0.9, lowConfidence = 0.85;
    private static int timeLoop = 1, numFilePerTime = 5;
    private static int fileDone = 0, thresholdFileDone = 20;
}
