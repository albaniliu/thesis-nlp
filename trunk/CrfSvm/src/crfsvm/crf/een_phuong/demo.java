/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * demo.java
 *
 * Created on Apr 19, 2011, 3:54:04 PM
 */
package crfsvm.crf.een_phuong;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import java.io.*; //my addition
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Thien
 */
public class demo extends javax.swing.JFrame {

    /** Creates new form demo */
    public demo() {
        try {
            this.setTitle("Named Entity Recognition Application - Natural Language Processing Group - Hanoi University of Science and Technology");
            this.setIconImage(Toolkit.getDefaultToolkit().getImage("." + File.separator + "src" + File.separator + "een_phuong" + File.separator + "resources" + File.separator + "taggingService.JPG"));
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            initComponents();
        } catch (Exception ex) {
            labelStatus.setText("Lỗi: " + ex.toString());
        }
    }
    //D:\EEN_Phuong_interfaceF\data\forTeacher\iob2.txt D:\EEN_Phuong_interfaceF\data\forTeacher\input.txt D:\EEN_Phuong_interfaceF\model

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelStatus = new javax.swing.JLabel();
        labelResult = new javax.swing.JLabel();
        buttonSubmit = new javax.swing.JButton();
        labelInstructor = new javax.swing.JLabel();
        buttonBrowse = new javax.swing.JButton();
        buttonExecute = new javax.swing.JButton();
        lablelPer = new javax.swing.JLabel();
        labelOrg = new javax.swing.JLabel();
        labelLoc = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        areaResult = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaSubmit = new javax.swing.JTextArea();
        fieldPath = new javax.swing.JTextField();
        buttonSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(demo.class);
        labelStatus.setFont(resourceMap.getFont("labelStatus.font")); // NOI18N
        labelStatus.setText(resourceMap.getString("labelStatus.text")); // NOI18N
        labelStatus.setName("labelStatus"); // NOI18N

        labelResult.setText(resourceMap.getString("labelResult.text")); // NOI18N
        labelResult.setName("labelResult"); // NOI18N

        buttonSubmit.setText(resourceMap.getString("buttonSubmit.text")); // NOI18N
        buttonSubmit.setName("buttonSubmit"); // NOI18N
        buttonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSubmitActionPerformed(evt);
            }
        });

        labelInstructor.setText(resourceMap.getString("labelInstructor.text")); // NOI18N
        labelInstructor.setName("labelInstructor"); // NOI18N

        buttonBrowse.setText(resourceMap.getString("buttonBrowse.text")); // NOI18N
        buttonBrowse.setName("buttonBrowse"); // NOI18N
        buttonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseActionPerformed(evt);
            }
        });

        buttonExecute.setText(resourceMap.getString("buttonExecute.text")); // NOI18N
        buttonExecute.setName("buttonExecute"); // NOI18N
        buttonExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExecuteActionPerformed(evt);
            }
        });

        lablelPer.setFont(resourceMap.getFont("lablelPer.font")); // NOI18N
        lablelPer.setForeground(resourceMap.getColor("lablelPer.foreground")); // NOI18N
        lablelPer.setText(resourceMap.getString("lablelPer.text")); // NOI18N
        lablelPer.setName("lablelPer"); // NOI18N

        labelOrg.setFont(resourceMap.getFont("labelOrg.font")); // NOI18N
        labelOrg.setForeground(resourceMap.getColor("labelOrg.foreground")); // NOI18N
        labelOrg.setText(resourceMap.getString("labelOrg.text")); // NOI18N
        labelOrg.setName("labelOrg"); // NOI18N

        labelLoc.setFont(resourceMap.getFont("labelLoc.font")); // NOI18N
        labelLoc.setForeground(resourceMap.getColor("labelLoc.foreground")); // NOI18N
        labelLoc.setText(resourceMap.getString("labelLoc.text")); // NOI18N
        labelLoc.setName("labelLoc"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        areaResult.setColumns(20);
        areaResult.setLineWrap(true);
        areaResult.setRows(5);
        areaResult.setName("areaResult"); // NOI18N
        jScrollPane2.setViewportView(areaResult);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        areaSubmit.setColumns(20);
        areaSubmit.setLineWrap(true);
        areaSubmit.setRows(5);
        areaSubmit.setName("areaSubmit"); // NOI18N
        jScrollPane1.setViewportView(areaSubmit);

        fieldPath.setText(resourceMap.getString("fieldPath.text")); // NOI18N
        fieldPath.setName("fieldPath"); // NOI18N

        buttonSave.setText(resourceMap.getString("buttonSave.text")); // NOI18N
        buttonSave.setName("buttonSave"); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(354, Short.MAX_VALUE)
                .addComponent(buttonExecute)
                .addGap(5, 5, 5)
                .addComponent(lablelPer)
                .addGap(5, 5, 5)
                .addComponent(labelOrg)
                .addGap(5, 5, 5)
                .addComponent(labelLoc)
                .addGap(235, 235, 235))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(labelInstructor))
                            .addComponent(buttonBrowse)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(labelResult)))
                        .addGap(38, 38, 38))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonSubmit)
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(fieldPath, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 769, Short.MAX_VALUE)
                        .addComponent(buttonSave)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonBrowse)
                    .addComponent(fieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInstructor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .addComponent(buttonSubmit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonExecute)
                            .addComponent(lablelPer)
                            .addComponent(labelOrg)
                            .addComponent(labelLoc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(labelResult)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonSave)
                    .addComponent(labelStatus))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExecuteActionPerformed
        // TODO add your handling code here:
        tagInput(areaResult);
}//GEN-LAST:event_buttonExecuteActionPerformed

    private void buttonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseActionPerformed
        // TODO add your handling code here:
        JFileChooser choose = new JFileChooser("." + File.separator + "data");
        ExampleFileFilter filter = new ExampleFileFilter("txt", "txt File");
        choose.addChoosableFileFilter(filter);
        int f = choose.showOpenDialog(this);
        if (f == JFileChooser.APPROVE_OPTION) {
            File inFile = choose.getSelectedFile();
            fieldPath.setText(inFile.getPath());

            BufferedReader br = null;
            try {
                String save = "", line = "";
                br = new BufferedReader(new InputStreamReader(new FileInputStream(fieldPath.getText()), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    save += line + "\n";
                }

                br.close();
                areaSubmit.setText(save);

                CopyFile.copyfile(fieldPath.getText(), inputData);
                labelStatus.setText("Đã nạp dữ liệu vào chương trình: " + fieldPath.getText());
            } catch (IOException ioe) {
                ioe.printStackTrace();
                labelStatus.setText("Không thể mở được file: " + fieldPath.getText());
            }
        }
}//GEN-LAST:event_buttonBrowseActionPerformed

    private void buttonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSubmitActionPerformed
        // TODO add your handling code here:
        if (areaSubmit.getText().length() == 0) {
            labelStatus.setText("Hãy nhập vào dữ liệu đầu vào!");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(inputData), "UTF-8"));
            bw.write(areaSubmit.getText());
            bw.close();
            labelStatus.setText("Đã nạp dữ liệu vào chương trình thành công!");
        } catch (Exception ex) {
            labelStatus.setText("Không thể nạp được dữ liệu vào chương trình: " + ex.toString());
        }
}//GEN-LAST:event_buttonSubmitActionPerformed

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        // TODO add your handling code here:
        String wd = "";
        System.getProperty("user.dir");
        //wd = "D:\\Document\\Informatics\\IE\\From Thani\\TrainingDoc";
        JFileChooser fc = new JFileChooser(wd);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rc = fc.showSaveDialog(this);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String filename = file.getAbsolutePath();
            try {
                CopyFile.copyfile("." + File.separator + "input.txt.wseg", filename);
                labelStatus.setText("Lưu thành công vào : " + filename);
            } catch (Exception e) {
                labelStatus.setText("Lưu thất bại : " + e);
            }
        }
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void tagInput(javax.swing.JTextArea textArea) {
        // TODO add your handling code here:
        try {

            textArea.setText("");
//            String tagg = "." + File.separator + "vnTagger" + File.separator + "input.txt";
            String tagg = "tmp/input.txt";
            CopyFile.copyfile(inputData, tagg);
            vnTokenizer vntokenizer = new vnTokenizer();
            try {
                //vntokenizer.token();
                tokenizeVietnamese.token();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EEN_PhuongView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(EEN_PhuongView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EEN_PhuongView.class.getName()).log(Level.SEVERE, null, ex);
            }
            JVnRecognizer jVnRecognizer = new JVnRecognizer();
            String[] args = new String[4];
            args[0] = "-modeldir";
            args[1] = "./model";
            args[2] = "-inputfile";
            args[3] = "tmp/tagged.txt";
            jVnRecognizer.main(args);
            BufferedReader in = null;
            String line = null;
            try {
                try {
                    in = new BufferedReader(new InputStreamReader(new FileInputStream("tmp/input.txt.wseg"), "UTF-8"));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(EEN_PhuongView.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(EEN_PhuongView.class.getName()).log(Level.SEVERE, null, ex);
            }
            String ret = "";
            Highlighter hilite = textArea.getHighlighter();


            while ((line = in.readLine()) != null) {
                if (line.trim().length() == 0) {
                    ret += "\n\n";
                    continue;
                }
                //NamedEntity nent;
                nameEntity nent;
                int curpos = 0;
                while ((nent = getNextEntity(line, curpos)) != null) { //nent = getNextEntity(line, curpos)
                    String aheadStr = line.substring(curpos, nent.beginIdx);
                    VnStringTokenizer tk = new VnStringTokenizer(aheadStr, " {}");
                    while (tk.hasMoreTokens()) {
                        String token = tk.nextToken();
                        token += " ";
                        ret += token;
                        //myret += token;
                        textArea.append(token);
                    }
                    VnStringTokenizer entTk = new VnStringTokenizer(nent.instance, " {}");
                    while (entTk.hasMoreTokens()) {
                        String token = entTk.nextToken();
                        String retOld = ret;
                        token += " ";
                        ret += token;
                        //myret += token;
                        textArea.append(token);
                        try {
                            highlight(textArea, token.trim(), retOld.length(), hilite, nent.type);
                        } catch (BadLocationException ex) {
                            Logger.getLogger(EEN_PhuongView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    curpos = nent.endIdx + 1;
                }
                if (curpos < line.length()) {
                    String remain = line.substring(curpos, line.length());
                    VnStringTokenizer tk = new VnStringTokenizer(remain, " ");
                    while (tk.hasMoreTokens()) {
                        String token = tk.nextToken();
                        token += " ";
                        ret += token;
                        //myret += token;
                        textArea.append(token);
                    }
                }
                textArea.append("\n\n");
                labelStatus.setText("Phát hiện thực thể thành công");
            }
        } catch (IOException ex) {
            Logger.getLogger(EEN_PhuongView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class nameEntity //copied from EEN_PhuongView.java
    {

        public String type; //time, per,org, loc
        public String instance;
        public int beginIdx;
        public int endIdx;
    }

    private nameEntity getNextEntity(String data, int curpos) {

        nameEntity nent = null;

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

        nent = new nameEntity();

        nent.beginIdx = beginOpenTag;
        nent.endIdx = closeTagIdx + closeTag.length();
        nent.instance = data.substring(endOpenTag + 1, closeTagIdx).trim();
        nent.type = openTag;

        return nent;
    }

    private boolean checkPunctuation(String line) {
        boolean result = false;
        String[] punctuations = {".", ",", "!", "(", ")", "[", "]", "{", "}", "$", "?", "@", "\"", "-", "/", "...", ":", "'", ";", "*", "+", "#",
            "%", "^", "&", "=", "|", "~", "`"};

        for (int i = 0; i < punctuations.length; i++) {
            if (punctuations[i].equals(line)) {
                result = true;
                break;
            }
        }

        return result;
    }

    public void highlight(JTextComponent textComp, String pattern, int i, Highlighter hilite, String color) throws BadLocationException {
//        removeHighlights(textComp);
        try {
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
//            text = text.substring(i, text.length());
            int pos = i;
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                if (color.equalsIgnoreCase("num")) {
                    hilite.addHighlight(pos, pos + pattern.length(), numHighlightPainter);
                    pos += pattern.length();
                } else if (color.equalsIgnoreCase("time")) {
                    hilite.addHighlight(pos, pos + pattern.length(), timeHighlightPainter);
                    pos += pattern.length();
                } else if (color.equalsIgnoreCase("per")) {
                    hilite.addHighlight(pos, pos + pattern.length(), perHighlightPainter);
                    pos += pattern.length();
                } else if (color.equalsIgnoreCase("misc")) {
                    hilite.addHighlight(pos, pos + pattern.length(), miscHighlightPainter);
                    pos += pattern.length();
                } else if (color.equalsIgnoreCase("pct")) {
                    hilite.addHighlight(pos, pos + pattern.length(), pctHighlightPainter);
                    pos += pattern.length();
                } else if (color.equalsIgnoreCase("cur")) {
                    hilite.addHighlight(pos, pos + pattern.length(), curHighlightPainter);
                    pos += pattern.length();
                } else if (color.equalsIgnoreCase("loc")) {
                    hilite.addHighlight(pos, pos + pattern.length(), locHighlightPainter);
                    pos += pattern.length();
                } else if (color.equalsIgnoreCase("org")) {
                    hilite.addHighlight(pos, pos + pattern.length(), orgHighlightPainter);
                    pos += pattern.length();
                }
            }
        } catch (BadLocationException e) {
        }
    }
    Highlighter.HighlightPainter numHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.GREEN);
    Highlighter.HighlightPainter timeHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.RED);
    Highlighter.HighlightPainter perHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.CYAN);
    Highlighter.HighlightPainter miscHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.YELLOW);
    Highlighter.HighlightPainter pctHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.GRAY);
    Highlighter.HighlightPainter curHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.blue);
    Highlighter.HighlightPainter locHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.LIGHT_GRAY);
    Highlighter.HighlightPainter orgHighlightPainter = (HighlightPainter) new MyHighlightPainter(Color.MAGENTA);

    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new demo().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaResult;
    private javax.swing.JTextArea areaSubmit;
    private javax.swing.JButton buttonBrowse;
    private javax.swing.JButton buttonExecute;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonSubmit;
    private javax.swing.JTextField fieldPath;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelInstructor;
    private javax.swing.JLabel labelLoc;
    private javax.swing.JLabel labelOrg;
    private javax.swing.JLabel labelResult;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JLabel lablelPer;
    // End of variables declaration//GEN-END:variables
    private File fi = new File("");
    private String inputData = fi.getAbsolutePath() + File.separator + "tmp"
            + File.separator + "demo.txt";
    //private String tagged = fi.getAbsolutePath() + File.separator + "tagged.txt";
}
