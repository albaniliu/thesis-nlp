/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TaggingPanel.java
 *
 * Created on Mar 9, 2011, 9:06:35 PM
 */
package thesis.view;

import feature.ENTITY;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import lib.Config;
import lib.ConvertText;
import lib.GUIFunction;
import util.MyAnnotation;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

/**
 *
 * @author banhbaochay
 */
public class TaggingPanel extends javax.swing.JPanel {

    /** Creates new form TaggingPanel
     * @param proper 
     */
    public TaggingPanel(HashMap<String, String> mapConfig) {
        this.mapConfig = mapConfig;
        clazz = KeyEvent.class;
        initComponents();
        String fontName = mapConfig.get(Config.TEXT_FONT_NAME);
        String styleString = mapConfig.get(Config.TEXT_FONT_STYLE);
        String sizeString = mapConfig.get(Config.TEXT_FONT_SIZE);
        Font fontArea = new Font(fontName, GUIFunction.string2Int(styleString), Integer.parseInt(sizeString));
        GUIFunction.setFontArea(textArea, sizeSpinner, fontArea);
        removeTagsButton.setText("Remove tags");

        setEnableButtonTag(false);
        setVisibleButtonAll(false);
        buttonReload.setVisible(false);
        buttonSave.setVisible(false);
        checkBoxChoose.setVisible(false);

//            Dung's code
        removeTagsButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), "remove");
        removeTagsButton.getActionMap().put("remove", removeTagsButton.getAction());
//            End

//        buttonPer.setForeground(Color.CYAN);
//        buttonLoc.setForeground(Color.LIGHT_GRAY);
//        buttonOrg.setForeground(Color.MAGENTA);
//        buttonNP.setForeground(Color.green);
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            public void changedUpdate(DocumentEvent e) {

                if (checkBoxMode.isSelected()) {
                    if (checkBoxChoose.isSelected()) {
                        setEnableButtonTagAll(true);
                    } else {
                        buttonNP.setEnabled(true);
                        buttonUntag.setEnabled(true);
                        buttonSave.setEnabled(true);
                        buttonReload.setEnabled(true);
                    }
                }
            }
        });



//        Dung's code
        Action browse = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                buttonBrowseClicked(e);
            }
        };
        buttonBrowse.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "browse");
        buttonBrowse.getActionMap().put("browse", browse);

        javax.swing.Action saveAs = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                buttonSaveAsActionPerformed(e);
            }
        };
        buttonSaveAs.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "saveAs");
        buttonSaveAs.getActionMap().put("saveAs", saveAs);

//        End
    }

    // <editor-fold defaultstate="collapsed" desc="setInfoButton - phim tat & tool tip">
    /**
     * Set phim tat va tool tip cho cac button
     */
    private void setInfoButton(Object object) {
        try {
            if (object instanceof JButton) {
                JButton button = (JButton) object;
                Field buttonField = TaggingPanel.class.getDeclaredField(button.getName());
                MyAnnotation annotation = buttonField.getAnnotation(MyAnnotation.class);
                Field fKeyEvent = clazz.getDeclaredField(
                        "VK_" + mapConfig.get(annotation.name()));
                button.setMnemonic(fKeyEvent.getInt(clazz));
                String oldToolTip = (button.getToolTipText() == null) ? ""
                        : button.getToolTipText().replaceAll(" *\\[.*\\] *", "") + " ";
                
                button.setToolTipText(oldToolTip + "[Alt + " 
                        + fKeyEvent.getName().substring(3) + "]");
            } else if (object instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) object;
                Field checkboxField = TaggingPanel.class.getDeclaredField(checkbox.getName());
                MyAnnotation annotation = checkboxField.getAnnotation(MyAnnotation.class);
                Field fKeyEvent = clazz.getDeclaredField(
                        "VK_" + mapConfig.get(annotation.name()));
                checkbox.setMnemonic(fKeyEvent.getInt(clazz));
                String oldToolTip = (checkbox.getToolTipText() == null) ? ""
                        : checkbox.getToolTipText().replaceAll(" *\\[.*\\] *", "") + " ";
                checkbox.setToolTipText(oldToolTip + "[Alt + " 
                        + fKeyEvent.getName().substring(3) + "]");
            }// end if object instanceof
        } catch (Exception ex) {
            Logger.getLogger(TaggingPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }// end setInfoButton method
    //</editor-fold>

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelPath = new javax.swing.JLabel();
        textFieldPath = new javax.swing.JTextField();
        buttonPer = new javax.swing.JButton();
        buttonLoc = new javax.swing.JButton();
        buttonOrg = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        buttonBrowse = new javax.swing.JButton();
        buttonUntag = new javax.swing.JButton();
        checkBoxEdit = new javax.swing.JCheckBox();
        labelStatus = new javax.swing.JLabel();
        buttonReload = new javax.swing.JButton();
        buttonSaveAs = new javax.swing.JButton();
        checkBoxMode = new javax.swing.JCheckBox();
        checkBoxChoose = new javax.swing.JCheckBox();
        buttonNP = new javax.swing.JButton();
        removeTagsButton = new javax.swing.JButton();
        convertButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        SpinnerModel numberModel = new SpinnerNumberModel(14, 9, 30, 2);
        sizeSpinner = new javax.swing.JSpinner(numberModel);
        posButton = new javax.swing.JButton();
        replaceButton = new javax.swing.JButton();
        jobButton = new javax.swing.JButton();
        dateButton = new javax.swing.JButton();
        clickCheckbox = new javax.swing.JCheckBox();
        mergeButton = new javax.swing.JButton();
        segButton = new javax.swing.JButton();

        labelPath.setText("Path");
        labelPath.setName("labelPath"); // NOI18N

        textFieldPath.setEditable(false);
        textFieldPath.setName("textFieldPath"); // NOI18N
        textFieldPath.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                textFieldPathMouseExited(evt);
            }
        });
        textFieldPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldPathActionPerformed(evt);
            }
        });

        buttonPer.setText("Person");
        buttonPer.setName("buttonPer"); // NOI18N
        setInfoButton(buttonPer);
        buttonPer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPerActionPerformed(evt);
            }
        });

        buttonLoc.setText("Location");
        buttonLoc.setName("buttonLoc"); // NOI18N
        setInfoButton(buttonLoc);
        buttonLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLocActionPerformed(evt);
            }
        });

        buttonOrg.setText("Organization");
        buttonOrg.setName("buttonOrg"); // NOI18N
        setInfoButton(buttonOrg);
        buttonOrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOrgActionPerformed(evt);
            }
        });

        buttonSave.setText("Save");
        buttonSave.setToolTipText("Save text in the textarea to the old file!");
        buttonSave.setEnabled(false);
        buttonSave.setName("buttonSave"); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setName("textArea"); // NOI18N
        textArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                textAreaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(textArea);

        buttonBrowse.setText("Browse...");
        buttonBrowse.setToolTipText("Choose file");
        buttonBrowse.setName("buttonBrowse"); // NOI18N
        setInfoButton(buttonBrowse);
        buttonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseClicked(evt);
            }
        });

        buttonUntag.setText("Untag");
        buttonUntag.setToolTipText("Untag selected text");
        buttonUntag.setName("buttonUntag"); // NOI18N
        setInfoButton(buttonUntag);
        buttonUntag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUntagActionPerformed(evt);
            }
        });

        checkBoxEdit.setSelected(true);
        checkBoxEdit.setText("Edit");
        checkBoxEdit.setToolTipText("Editable text area or not ?");
        checkBoxEdit.setName("checkBoxEdit"); // NOI18N
        checkBoxEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxEditActionPerformed(evt);
            }
        });

        labelStatus.setText("Status");
        labelStatus.setName("labelStatus"); // NOI18N

        buttonReload.setText("Reload");
        buttonReload.setToolTipText("Save and update changes to the display of the textarea!");
        buttonReload.setEnabled(false);
        buttonReload.setName("buttonReload"); // NOI18N
        buttonReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReloadActionPerformed(evt);
            }
        });

        buttonSaveAs.setText("Save As");
        buttonSaveAs.setToolTipText("Save text in textarea as a new file![Ctrl S]");
        buttonSaveAs.setName("buttonSaveAs"); // NOI18N
        buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveAsActionPerformed(evt);
            }
        });

        checkBoxMode.setText("Tag(selected)/WordSeg(unselected)");
        checkBoxMode.setToolTipText("Tag document mode or word segmentation mode");
        checkBoxMode.setName("checkBoxMode"); // NOI18N
        checkBoxMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxModeActionPerformed(evt);
            }
        });

        checkBoxChoose.setSelected(true);
        checkBoxChoose.setText("PerLocOrg(selected)/NP(unselected)");
        checkBoxChoose.setToolTipText("Tag person, location and organization or tag noun phrase");
        checkBoxChoose.setName("checkBoxChoose"); // NOI18N
        checkBoxChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxChooseActionPerformed(evt);
            }
        });

        buttonNP.setMnemonic('N');
        buttonNP.setText("NP");
        buttonNP.setToolTipText("Tag selected text as Noun Phrase (NP)");
        buttonNP.setEnabled(false);
        buttonNP.setName("buttonNP"); // NOI18N
        buttonNP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNPActionPerformed(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(TaggingPanel.class, this);
        removeTagsButton.setAction(actionMap.get("removeTags")); // NOI18N
        removeTagsButton.setName("removeTagsButton"); // NOI18N

        convertButton.setMnemonic('C');
        convertButton.setText("Convert");
        convertButton.setToolTipText("Convert Entity tag to NP tag[Alt+C]");
        convertButton.setName("convertButton"); // NOI18N
        convertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convertButtonActionPerformed(evt);
            }
        });
        convertButton.setVisible(false);

        jLabel2.setText("Text size:");
        jLabel2.setName("jLabel2"); // NOI18N

        sizeSpinner.setName("sizeSpinner"); // NOI18N
        sizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSpinnerStateChanged(evt);
            }
        });
        setInfoButton(sizeSpinner);

        posButton.setText("Position");
        posButton.setName("posButton"); // NOI18N
        setInfoButton(posButton);
        posButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                posButtonActionPerformed(evt);
            }
        });

        replaceButton.setMnemonic('R');
        replaceButton.setText("Replace");
        replaceButton.setToolTipText("Replace pronoun by name [Alt + R]");
        replaceButton.setName("replaceButton"); // NOI18N
        replaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceButtonActionPerformed(evt);
            }
        });

        jobButton.setText("Job");
        jobButton.setName("jobButton"); // NOI18N
        setInfoButton(jobButton);
        jobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jobButtonActionPerformed(evt);
            }
        });

        dateButton.setText("Date");
        dateButton.setName("dateButton"); // NOI18N
        setInfoButton(dateButton);
        dateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateButtonActionPerformed(evt);
            }
        });

        clickCheckbox.setText("Normal click");
        clickCheckbox.setToolTipText("Choose mode click in textarea ");
        clickCheckbox.setName("clickCheckbox"); // NOI18N
        setInfoButton(clickCheckbox);

        mergeButton.setText("Merge words");
        mergeButton.setName("mergeButton"); // NOI18N
        setInfoButton(mergeButton);
        mergeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeButtonActionPerformed(evt);
            }
        });

        segButton.setText("Seg words");
        segButton.setName("segButton"); // NOI18N
        setInfoButton(segButton);
        segButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxMode)
                    .addComponent(checkBoxChoose))
                .addGap(36, 36, 36)
                .addComponent(labelPath)
                .addGap(4, 4, 4)
                .addComponent(textFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(buttonBrowse))
            .addComponent(labelStatus)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sizeSpinner))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxEdit)
                        .addGap(6, 6, 6)
                        .addComponent(buttonPer)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(buttonLoc)
                        .addGap(2, 2, 2)
                        .addComponent(buttonNP)
                        .addGap(6, 6, 6)
                        .addComponent(buttonOrg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(posButton)
                        .addGap(6, 6, 6)
                        .addComponent(jobButton)
                        .addGap(6, 6, 6)
                        .addComponent(dateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonUntag)
                        .addGap(24, 24, 24)
                        .addComponent(buttonReload)
                        .addGap(18, 18, 18)
                        .addComponent(buttonSave)
                        .addGap(10, 10, 10)
                        .addComponent(buttonSaveAs)
                        .addGap(10, 10, 10)
                        .addComponent(removeTagsButton)
                        .addGap(18, 18, 18)
                        .addComponent(convertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(replaceButton)
                        .addGap(18, 18, 18)
                        .addComponent(clickCheckbox)
                        .addGap(18, 18, 18)
                        .addComponent(mergeButton)
                        .addGap(18, 18, 18)
                        .addComponent(segButton))))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1105, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxMode)
                        .addGap(3, 3, 3)
                        .addComponent(checkBoxChoose))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(labelPath))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(textFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(buttonBrowse)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(sizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(replaceButton)
                    .addComponent(clickCheckbox)
                    .addComponent(mergeButton)
                    .addComponent(segButton))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxEdit)
                    .addComponent(buttonPer)
                    .addComponent(buttonLoc)
                    .addComponent(buttonNP)
                    .addComponent(buttonOrg)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonUntag)
                        .addComponent(posButton)
                        .addComponent(jobButton)
                        .addComponent(dateButton))
                    .addComponent(buttonReload)
                    .addComponent(buttonSave)
                    .addComponent(buttonSaveAs)
                    .addComponent(removeTagsButton)
                    .addComponent(convertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStatus))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldPathMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textFieldPathMouseExited
        // TODO add your handling code here:
//        try {
//            File f = new File(textFieldPath.getText());
//            File current = new File(".");
//            if (!f.isFile()) {
//                labelStatus.setText("File Path is incorrect! Default file is replaced!");
//                Date date = new Date();
//                textFieldPath.setText(current.getCanonicalPath() + File.separator + "temp_" + date.getTime() + ".txt");
//                if (checkBoxMode.isSelected()) {
//                    buttonSave.setEnabled(true);
//                    buttonReload.setEnabled(true);
//                }
//            }
//        } catch (Exception e) {
//            labelStatus.setText("Error: " + e);
//        }
}//GEN-LAST:event_textFieldPathMouseExited

    private void textFieldPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldPathActionPerformed
        // TODO add your handling code here:
//        try {
//            File f = new File(textFieldPath.getText());
//            File current = new File(".");
//            if (!f.isFile()) {
//                labelStatus.setText("File Path is incorrect! Default file is replaced!");
//                Date date = new Date();
//                textFieldPath.setText(current.getCanonicalPath() + File.separator + date + ".txt");
//                buttonSave.setEnabled(true);
//                buttonReload.setEnabled(true);
//            }
//        } catch (Exception e) {
//            labelStatus.setText("Error: " + e);
//        }
}//GEN-LAST:event_textFieldPathActionPerformed

    private void buttonPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPerActionPerformed
        // TODO add your handling code here:
        try {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();

            textArea.insert("<per> ", start);
            textArea.insert(" </per>", end + 6);

            textArea.select(start + 6, end + 6);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + 6, end + 6, (HighlightPainter) new DefaultHighlightPainter(Color.CYAN));

            labelStatus.setText("Person Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + 6 + 7);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
}//GEN-LAST:event_buttonPerActionPerformed

    private void buttonLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLocActionPerformed
        // TODO add your handling code here:
        try {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();

            textArea.insert("<loc> ", start);
            textArea.insert(" </loc>", end + 6);

            textArea.select(start + 6, end + 6);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + 6, end + 6, (HighlightPainter) new DefaultHighlightPainter(Color.LIGHT_GRAY));

            labelStatus.setText("Location Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + 6 + 7);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
}//GEN-LAST:event_buttonLocActionPerformed

    private void buttonOrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOrgActionPerformed
        // TODO add your handling code here:
        try {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();

            textArea.insert("<org> ", start);
            textArea.insert(" </org>", end + 6);

            textArea.select(start + 6, end + 6);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + 6, end + 6, (HighlightPainter) new DefaultHighlightPainter(Color.MAGENTA));

            labelStatus.setText("Organization Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + 6 + 7);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
}//GEN-LAST:event_buttonOrgActionPerformed

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(this, "This action will overwrite your old file, do you want to continue? (Press Yes to continue ,Press No to abort)", "WARNING", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            saveTextArea(textFieldPath.getText());
        }
}//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonBrowseClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseClicked
        // TODO add your handling code here:
        //String wd;
        //if (!checkBoxMode.isSelected())
        //    wd = "D:\\Document\\Informatics\\IE\\From Thani\\Data\\MapTextForm";
        //else
        //    wd = "D:\\Document\\Informatics\\IE\\From Thani\\Data\\TaggedTexts";
        try {
            File ff = new File(".");
            String fileDes = ff.getCanonicalPath() + File.separator + "data" + File.separator + "labs" + File.separator;
            Date d = new Date();
            fileDes += d.getTime() + "_";
            //            Uncomment to return org file
            //            String wd = System.getProperty("user.dir");
            //            JFileChooser choose = new JFileChooser(wd);

            //            Dung's code
            String path = mapConfig.get(Config.DIRECTORY_PATH);
            JFileChooser choose = new JFileChooser(path);

            //            End

            ExampleFileFilter filter = new ExampleFileFilter("txt", "txt File");
            choose.addChoosableFileFilter(filter);

            int f = choose.showOpenDialog(this);
            if (f == JFileChooser.APPROVE_OPTION) {
                File inFile = choose.getSelectedFile();

                //                Dung's code
                //                Save name of file for Save As action
                fileName = inFile.getName();
                //                End

                textFieldPath.setText(inFile.getPath());
                fileDes += inFile.getName();
//                if (checkBoxChoose.isSelected()) {
//                    copyFile(inFile.getPath(), fileDes);
//                }
                if (checkBoxMode.isSelected()) {
                    loadFile(textFieldPath.getText());
                } else {
                    textArea.setText(vnTagger(textFieldPath.getText()));
                    setEnableButtonTagAll(false);
                    buttonNP.setEnabled(false);
                    labelStatus.setText("Chunking Successfully!");
                }

                //                Dung's code
                textArea.setCaretPosition(0);
                //                End

            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
}//GEN-LAST:event_buttonBrowseClicked

    private void buttonUntagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUntagActionPerformed
        // TODO add your handling code here:
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        if (end > start) {
            String selected = textArea.getSelectedText();
            String replace = selected.replaceAll(" *<[^>]*> *", "");
            textArea.replaceRange(replace, start, end);
        }// end if
//        try {
//            int start = textArea.getSelectionStart();
//            int end = textArea.getSelectionEnd();
//            String selected = textArea.getSelectedText();
//            String tag = "";
//            boolean checkStart = false;
//            if (checkBoxChoose.isSelected()) {
//                for (int i = 0; i < tags.length; i++) {
//                    if (selected.startsWith("<" + tags[i] + "> ")) {
//                        checkStart = true;
//                        tag = tags[i];
//                        break;
//                    }
//                }
//                if (!checkStart) {
//                    return;
//                }
//                if (selected.endsWith(" </" + tag + ">")) {
//                    textArea.replaceRange("", start, start + tag.length() + 3);
//                    textArea.replaceRange("", end - 2 * tag.length() - 7, end - tag.length() - 3);
//                    textArea.select(start, end - 2 * tag.length() - 7);
//                    labelStatus.setText(tag + " Untagged:  " + textArea.getSelectedText());
//                    buttonSave.setEnabled(true);
//                    buttonReload.setEnabled(true);
//                }
//            } else {
//                for (int i = 0; i < chunks.length; i++) {
//                    if (selected.startsWith("<" + chunks[i] + "> ")) {
//                        checkStart = true;
//                        tag = chunks[i];
//                        break;
//                    }
//                }
//                if (!checkStart) {
//                    return;
//                }
//                if (selected.endsWith(" </" + tag + ">")) {
//                    textArea.replaceRange("", start, start + tag.length() + 3);
//                    textArea.replaceRange("", end - 2 * tag.length() - 7, end - tag.length() - 3);
//                    textArea.select(start, end - 2 * tag.length() - 7);
//                    labelStatus.setText(tag + " Untagged:  " + textArea.getSelectedText());
//                    buttonSave.setEnabled(true);
//                    buttonReload.setEnabled(true);
//                }
//            }
//        } catch (Exception e) {
//            labelStatus.setText("Error: " + e);
//        }
}//GEN-LAST:event_buttonUntagActionPerformed

    private void checkBoxEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxEditActionPerformed
        // TODO add your handling code here:
        textArea.setEditable(checkBoxEdit.isSelected());
}//GEN-LAST:event_checkBoxEditActionPerformed

    private void buttonReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReloadActionPerformed
        // TODO add your handling code here:
        File f = new File(textFieldPath.getText());
        if (f.isFile()) {
            buttonSaveActionPerformed(evt);
            loadFile(textFieldPath.getText());
        } else {
            labelStatus.setText("File Path is incorrect!");
        }
}//GEN-LAST:event_buttonReloadActionPerformed

    private void buttonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAsActionPerformed
        // TODO add your handling code here:
        //            String wd = System.getProperty("user.dir");
        //            JFileChooser fc = new JFileChooser(wd);
        //            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //        Dung's code
        String path = mapConfig.get(Config.DIRECTORY_PATH);
        JFileChooser fc = new JFileChooser(path);

        ExampleFileFilter filter = new ExampleFileFilter("txt", "Text Document");
        fc.addChoosableFileFilter(filter);
        fc.setSelectedFile(new File(fileName));
        //        End
        int rc = fc.showSaveDialog(this);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String absolutePath = file.getAbsolutePath();
            saveTextArea(absolutePath);
        }
}//GEN-LAST:event_buttonSaveAsActionPerformed

    private void checkBoxModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxModeActionPerformed
        // TODO add your handling code here:
        checkBoxModeListener();
        if (!textArea.getText().equals("")) {
            textArea.setCaretPosition(0);
        }
}//GEN-LAST:event_checkBoxModeActionPerformed

    private void checkBoxChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxChooseActionPerformed
        // TODO add your handling code here:
        checkBoxChooseListener();

        //        Dung's code
        if (!textArea.getText().equals("")) {
            textArea.setCaretPosition(0);
        }
        if (!checkBoxChoose.isSelected()) {
            convertButton.setVisible(true);
        } else {
            convertButton.setVisible(false);
        }
        //        End
    }//GEN-LAST:event_checkBoxChooseActionPerformed

    private void buttonNPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNPActionPerformed
        // TODO add your handling code here:
        try {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();

            textArea.insert("<NP> ", start);
            textArea.insert(" </NP>", end + 5);

            textArea.select(start + 5, end + 5);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + 5, end + 5, (HighlightPainter) new DefaultHighlightPainter(Color.green));

            labelStatus.setText("NP Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + 5 + 6);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
}//GEN-LAST:event_buttonNPActionPerformed

    private void convertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertButtonActionPerformed
        // TODO add your handling code here:
        String input = textArea.getText();
        String output = input.replaceAll("</[^>]*>", "</NP>");
        output = output.replaceAll("<[^/>]*>", "<NP>").trim();
        textArea.setText(output);
        Document doc = textArea.getDocument();
        try {
            String text = doc.getText(0, doc.getLength());
            Highlighter hili = textArea.getHighlighter();
            for (int i = 0; i < chunks.length; i++) {
                highlightTag(text, hili, chunks[i], colorsChunk[i]);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        textArea.setCaretPosition(0);
    }//GEN-LAST:event_convertButtonActionPerformed

    private void sizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeSpinnerStateChanged
        // TODO add your handling code here:
        int size = (Integer) sizeSpinner.getValue();
        Font oldFont = textArea.getFont();
        Font changedFont = oldFont.deriveFont((float) size);
        GUIFunction.setFontArea(textArea, changedFont);
}//GEN-LAST:event_sizeSpinnerStateChanged

    private void posButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_posButtonActionPerformed
        // TODO add your handling code here:
        try {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();

            textArea.insert("<pos> ", start);
            textArea.insert(" </pos>", end + 6);

            textArea.select(start + 6, end + 6);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + 6, end + 6, (HighlightPainter) new DefaultHighlightPainter(Color.blue));

            labelStatus.setText("Position Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + 6 + 7);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
    }//GEN-LAST:event_posButtonActionPerformed

    private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceButtonActionPerformed
        String text = textArea.getText();
        HashMap<String, Integer> perMap = new HashMap<String, Integer>();
        int position = -1;
        int offset = 0;
        while ((position = text.indexOf("<per>", offset)) != -1) {
            int start = position + 5;
            int end = text.indexOf("</per>", start);
            if (start < end) {
                String name = text.substring(start, end).trim().replaceAll("\\[|\\]", "");
                if (!perMap.containsKey(name)) {
                    perMap.put(name, 1);
                }// end if contain key
                offset = end;
            } else {
                offset = start;
            }// end if start < end
        }// end while position
        Object[] radioArray = new Object[perMap.size() + 1];
        ButtonGroup group = new ButtonGroup();
        radioArray[0] = new JLabel("Select name to replace:");
        int i = 1;
        for (Map.Entry<String, Integer> entry : perMap.entrySet()) {
            String name = entry.getKey();
            JRadioButton radio = new JRadioButton(name);
            group.add(radio);
            radioArray[i] = radio;
            i++;
        }// end for entry
        int res = JOptionPane.showConfirmDialog(null, radioArray, "Replace Name",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            int startSelected = textArea.getSelectionStart();
            int endSelected = textArea.getSelectionEnd();
            String nameReplaced = null;
            if (group.getSelection() == null) {
                JOptionPane.showMessageDialog(null, "No name to choose for replacing",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                Enumeration e = group.getElements();
                while (e.hasMoreElements()) {
                    Object element = e.nextElement();
                    if (element instanceof JRadioButton) {
                        JRadioButton radio = (JRadioButton) element;
                        if (radio.getModel() == group.getSelection()) {
                            String name = radio.getText();
                            nameReplaced = "<per> [" + name + "] </per>";
                        }// end if radio is selected
                    }// end if element
                }// end while e
                textArea.replaceRange(nameReplaced, startSelected, endSelected);
                /*
                 * Set highlight cho tu vua duoc thay the
                 */
                Highlighter hilit = textArea.getHighlighter();
                HighlightPainter painter = new DefaultHighlightPainter(Color.CYAN);
                try {
                    hilit.addHighlight(startSelected + 6, startSelected + nameReplaced.length() - 7, painter);
                } catch (BadLocationException ex) {
                    Logger.getLogger(TaggingPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }// end if group

        }// end if OK_OPTION

    }//GEN-LAST:event_replaceButtonActionPerformed

    private void jobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jobButtonActionPerformed
        try {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();
            int offset = ENTITY.JOB.getStartLength() + 1;

            textArea.insert(ENTITY.JOB.getStartTag().toLowerCase() + " ", start);
            textArea.insert(" " + ENTITY.JOB.getEndTag().toLowerCase(), end + offset);

            textArea.select(start + offset, end + offset);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + offset, end + offset, (HighlightPainter) new DefaultHighlightPainter(Color.YELLOW));

            labelStatus.setText("Job Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + offset + ENTITY.JOB.getEndLength() + 1);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
    }//GEN-LAST:event_jobButtonActionPerformed

    private void dateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateButtonActionPerformed
        try {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();
            int offset = ENTITY.DATE.getStartLength() + 1;

            textArea.insert(ENTITY.DATE.getStartTag().toLowerCase() + " ", start);
            textArea.insert(" " + ENTITY.DATE.getEndTag().toLowerCase(), end + offset);

            textArea.select(start + offset, end + offset);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + offset, end + offset, (HighlightPainter) new DefaultHighlightPainter(Color.ORANGE));

            labelStatus.setText("Date Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + offset + ENTITY.DATE.getEndLength() + 1);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
    }//GEN-LAST:event_dateButtonActionPerformed

    private void textAreaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textAreaMouseReleased
        // TODO add your handling code here:
        if (!clickCheckbox.isSelected()) {
            int selectionStart = textArea.getSelectionStart();
            int selectionEnd = textArea.getSelectionEnd();
            if (selectionEnd >= selectionStart) {
                if (!evt.isControlDown()) {
                    int start = -1;
                    int end = -1;
                    String text = textArea.getText();
                    String previous = text.substring(0, selectionStart);
                    start = previous.lastIndexOf("[");
                    end = text.indexOf("]", selectionEnd);
                    textArea.select(start, end + 1);
                } else {
                    int start = -1;
                    int end = -1;
                    String text = textArea.getText();
                    String previous = text.substring(0, selectionStart);
                    start = previous.lastIndexOf("<");
                    end = text.indexOf(">", selectionEnd);
                    textArea.select(start, end + 1);
                }
            }// end if
        }
    }//GEN-LAST:event_textAreaMouseReleased

    private void mergeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeButtonActionPerformed
        // TODO add your handling code here:
        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();
        if (selectionEnd >= selectionStart) {
            String text = textArea.getSelectedText();
            textArea.replaceRange(ConvertText.mergeWords(text), selectionStart, selectionEnd);
        }// end if
    }//GEN-LAST:event_mergeButtonActionPerformed

    private void segButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segButtonActionPerformed
        // TODO add your handling code here:
        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();
        if (selectionEnd >= selectionStart) {
            String text = textArea.getSelectedText();
            textArea.replaceRange(ConvertText.segWords(text), selectionStart, selectionEnd);
        }// end if
    }//GEN-LAST:event_segButtonActionPerformed

    public void saveTextArea(String file) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            out.write(textArea.getText());
            out.close();
            buttonSave.setEnabled(false);
            labelStatus.setText("Save SUCCESSFULLY!");
        } catch (Exception e) {
            buttonSave.setEnabled(false);
            labelStatus.setText("Save UNSUCCESSFULLY!: " + e);
        }
    }

    public void highlightTag(String text, Highlighter hili, String tag, Color color) {
        try {
            HighlightPainter colorHiLi = (HighlightPainter) new DefaultHighlightPainter(color);
            int posStart = 0;
            int posEnd = 0;
            String start = "<" + tag + "> ";
            String end = " </" + tag + ">";
            while ((posStart = text.indexOf(start, posStart)) >= 0) {
                posEnd = text.indexOf(end, posStart);
                hili.addHighlight(posStart + tag.length() + 3, posEnd, colorHiLi);
                posStart = posEnd + tag.length() + 3;
            }
        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
    }

    /**
     * Save the your trace of the last done with this file
     * @param text
     * @param hili
     */
    public void traceDone(String text, Highlighter hili) {
        try {
            ArrayList<Object> indexs = new ArrayList<Object>();
            int lineStart = 0, lineEnd = 0, posLine = 0, maxPos = 0;
            while ((posLine = text.indexOf('\n', posLine)) >= 0) {
                lineStart = lineEnd;
                lineEnd = posLine;
                int[] arr = {lineStart, lineEnd - 1};
                indexs.add(arr);
                posLine++;
            }

            if (checkBoxChoose.isSelected()) {
                for (int i = 0; i < tags.length; i++) {
                    int result = positionLastTag(text, tags[i], indexs);
                    if (result > maxPos) {
                        maxPos = result;
                    }
                }
            } else {
                for (int i = 0; i < chunks.length; i++) {
                    int result = positionLastTag(text, chunks[i], indexs);
                    if (result > maxPos) {
                        maxPos = result;
                    }
                }
            }
            int[] array = (int[]) indexs.get(maxPos);
            hili.addHighlight(array[0], array[1], (HighlightPainter) new DefaultHighlightPainter(Color.YELLOW));
        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
    }

    public int positionLastTag(String text, String tag, ArrayList<Object> arr) {
        int pos = 0, prepos = 0, result = -1;
        while ((pos = text.indexOf(" <" + tag + ">", pos)) >= 0) {
            prepos = pos;
            pos++;
        }
        for (int i = 0; i < arr.size(); i++) {
            int[] array = (int[]) arr.get(i);
            if ((array[0] <= prepos) && (array[1] >= prepos)) {
                return i;
            }
        }
        return result;
    }

    public final void setEnableButtonTag(boolean state) {
        buttonPer.setEnabled(state);
        buttonLoc.setEnabled(state);
        buttonOrg.setEnabled(state);
        buttonUntag.setEnabled(state);
    }

    public void setEnableButtonTagAll(boolean state) {
        setEnableButtonTag(state);
        buttonReload.setEnabled(state);
        buttonSave.setEnabled(state);
    }

    public void setVisibleButtonTag(boolean state) {
        buttonPer.setVisible(state);
        buttonLoc.setVisible(state);
        buttonOrg.setVisible(state);
    }

    public final void setVisibleButtonAll(boolean state) {
        setVisibleButtonTag(state);
        buttonNP.setVisible(state);
        buttonUntag.setVisible(state);
    }

    public void loadFile(String file) {
        textArea.setText("");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            String line;

            while ((line = in.readLine()) != null) {
                if (checkBoxChoose.isSelected()) {
                    textArea.append(eliminateTags(line, chunks) + "\n");
                } else {
                    textArea.append(eliminateTags(line, tags) + "\n");
                }
            }

            Document doc = textArea.getDocument();
            String text = doc.getText(0, doc.getLength());
            Highlighter hili = textArea.getHighlighter();

            if (checkBoxChoose.isSelected()) {
                for (int i = 0; i < tags.length; i++) {
                    highlightTag(text, hili, tags[i], colors[i]);
                }
            } else {
                for (int i = 0; i < chunks.length; i++) {
                    highlightTag(text, hili, chunks[i], colorsChunk[i]);
                }
            }
//                Uncomment this line if want to trace your last done
            //traceDone(text, hili);

            in.close();
            buttonSave.setEnabled(false);
            buttonReload.setEnabled(false);
            if (checkBoxChoose.isSelected()) {
                setEnableButtonTag(true);
            } else {
                buttonNP.setEnabled(true);
                buttonUntag.setEnabled(true);
            }
        } catch (Exception ex) {
            labelStatus.setText("Can not open file " + textFieldPath.getName() + ": " + ex);
        }
    }

    public void checkBoxChooseListener() {
//        Uncomment to return original code by Thien
//        if (checkBoxChoose.isSelected()) {
//            int choi = JOptionPane.showConfirmDialog(this, "Do you want to save the work you did? (Press OK to come back and save it by yourself, Press Cancel to continue WITHOUT saving anything?", "WARNING", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
//            if (choi != JOptionPane.OK_OPTION) {
//                textFieldPath.setText("");
//                textArea.setText("");
//                setVisibleButtonTag(true);
//                buttonNP.setVisible(false);
//                setEnableButtonTagAll(false);
//                buttonNP.setEnabled(false);
//            } else {
//                checkBoxChoose.setSelected(false);
//            }
//        } else {
//            int choi = JOptionPane.showConfirmDialog(this, "Do you want to save the work you did? (Press OK to come back and save it by yourself, Press Cancel to continue WITHOUT saving anything?", "WARNING", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
//            if (choi != JOptionPane.OK_OPTION) {
//                textFieldPath.setText("");
//                textArea.setText("");
//                setVisibleButtonTag(false);
//                setEnableButtonTagAll(false);
//                buttonNP.setVisible(true);
//                buttonNP.setEnabled(false);
//            } else {
//                checkBoxChoose.setSelected(true);
//            }
//        }

//        Dung's code
        if (checkBoxChoose.isSelected()) {
            setVisibleButtonTag(true);  //per, loc, org
            buttonNP.setVisible(false);
            buttonNP.setEnabled(false);
            setEnableButtonTagAll(false); //per, loc, org, untag, reload, save
            setEnableButtonTag(true);

        } else {
            setVisibleButtonTag(false);
            setEnableButtonTagAll(false);
            buttonNP.setVisible(true);
            buttonNP.setEnabled(true);
            buttonUntag.setEnabled(true);
        }
//        End

    }

    public void checkBoxModeListener() {
        if (checkBoxMode.isSelected()) {

//            Uncomment to return original code by Thien
//            int choice = JOptionPane.showConfirmDialog(this, "Do you want to save the result?", "WARNING", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
//            if (choice == JOptionPane.OK_OPTION) {
//                String wd = System.getProperty("user.dir");
//                JFileChooser fc = new JFileChooser(wd);
//                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                int rc = fc.showSaveDialog(this);
//                if (rc == JFileChooser.APPROVE_OPTION) {
//                    File file = fc.getSelectedFile();
//                    String filename = file.getAbsolutePath();
//                    saveTextArea(filename);
//                }
//            }
//            textFieldPath.setText("");
//            textArea.setText("");
            checkBoxChoose.setVisible(true);
            checkBoxChoose.setSelected(true);
            buttonUntag.setVisible(true);
            buttonReload.setVisible(true);
            buttonSave.setVisible(true);
            setEnableButtonTagAll(false);

//            Dung's code
            setEnableButtonTag(true);
//            End

            buttonNP.setEnabled(false);
            if (checkBoxChoose.isSelected()) {
                setVisibleButtonTag(true);
                buttonNP.setVisible(false);
            } else {
                setVisibleButtonTag(false);
                buttonNP.setVisible(true);
            }
        } else {
//            Uncomment to return original code by Thien
//            int choi = JOptionPane.showConfirmDialog(this, "Do you want to save the work you did? (Press OK to come back and save it by yourself, Press Cancel to continue WITHOUT saving anything?", "WARNING", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
//            if (choi != JOptionPane.OK_OPTION) {
//                textFieldPath.setText("");
//                textArea.setText("");
//                buttonReload.setVisible(false);
//                buttonSave.setVisible(false);
//                setEnableButtonTagAll(false);
//                buttonNP.setEnabled(false);
//                checkBoxChoose.setVisible(false);
//                setVisibleButtonAll(false);
//            } else {
//                checkBoxMode.setSelected(true);
//            }

//            Dung's code
            buttonReload.setVisible(false);
            buttonSave.setVisible(false);
            setEnableButtonTagAll(false);
            buttonNP.setEnabled(false);
            checkBoxChoose.setVisible(false);
            setVisibleButtonAll(false);
//            End
        }
    }

    public String vnTagger(String filename) {
//        Runtime rt = Runtime.getRuntime();
//        Process process = null;
//        String dir = "." + File.separator + "vnTagger" + File.separator + "vnTagger.bat";
        String fileSource = "tempTagging.service", fileDestination = "resultTagging.service";
        try {
            File f = new File(".");
            fileSource = f.getCanonicalPath() + File.separator + fileSource;
            fileDestination = f.getCanonicalPath() + File.separator + fileDestination;
            try {
                copyFile(filename, fileSource);
            } catch (Exception e) {
                return e.toString();
            }
            //System.out.println("Starting....\n + source " + fileSource + " \nDes: " + fileDestination);
//            process = rt.exec(dir + " -i " + fileSource + " -o " + fileDestination + " -u" + " -p");
//
//            process.waitFor();
//            System.out.println("Done!");

//            Dung's code
            TaggerOptions.UNDERSCORE = true;
            TaggerOptions.PLAIN_TEXT_FORMAT = true;
            VietnameseMaxentTagger vietnamesemaxentTagger = new VietnameseMaxentTagger();
            vietnamesemaxentTagger.tagFile(fileSource, fileDestination);
//            End
            f = new File(fileSource);
            f.delete();
//            return modifyvnTagger(fileDestination, true);
            return tokenizeVietnamese.seperateSentencesInString(modifyvnTagger(fileDestination, true));
            //return "Done!!!!!!!!!!!!";
        } catch (Exception ex) {
            return ex.toString() + "2";
        }
    }

    public String modifyvnTagger(String fileSource, boolean delete) {
        String[] punctuations = {".", ",", "!", "(", ")", "[", "]", "{", "}", "$", "?", "@", "\"", "-", "/", "...", ":", "'", ";"};

        try {
            String line = "";
            String ret = "";

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileSource), "UTF-8"));

            while ((line = in.readLine()) != null) {
                StringTokenizer lineTknr = new StringTokenizer(line, " ");

                while (lineTknr.hasMoreTokens()) {
                    line = lineTknr.nextToken();

                    int i = line.lastIndexOf("/");
                    line = line.substring(0, i);

                    if (line.indexOf("_") != 0) {
                        line = line.replaceAll("_", " ");
                    }
                    line = "[" + line + "]";

                    char c = line.charAt(1);
                    int ascii = (int) c;

                    if (ascii != 65279) {
                        String toAdd = checkPunctuation(punctuations, line);
                        if (toAdd.length() != 0) {
                            ret += toAdd + " ";
                        } else {
                            ret += line + " ";
                        }
                    }
                }
                if (ret.length() != 1) {
                    ret += "\n" + "\n";
                }
            }

            in.close();
            File toDelete = new File(fileSource);
            if (delete) {
                toDelete.delete();
            }

            return ret;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public String checkPunctuation(String[] punctuations, String line) {
        String result = "";
        for (int i = 0; i < punctuations.length; i++) {
            if (("[" + punctuations[i] + "]").equals(line)) {
                result = punctuations[i];
                break;
            }
        }
        return result;
    }

    public void copyFile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String eliminateTags(String data, String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            data = data.replace("<" + tags[i] + "> ", "");
            data = data.replace(" </" + tags[i] + ">", "");
        }
        for (int i = 0; i < tags.length; i++) {
            data = data.replace("<" + tags[i] + ">", "");
            data = data.replace("</" + tags[i] + ">", "");
        }
        return data;
    }

    /**
     * Thay doi font cua textarea. Phuong thuc nay de co the thay doi font textarea
     * tu class khac
     */
    public void setFontArea(Font font) {
        GUIFunction.setFontArea(textArea, sizeSpinner, font);
    }// end setFontArea method

    /**
     * removeTagsButton's Action: call it by getAction() method.
     * This method removes all of the tags in text area, ex: <NP>, <per>, <loc>..
     */
    @org.jdesktop.application.Action
    public void removeTags() {
        String input = textArea.getText();
        String output = eliminateTags(eliminateTags(input, chunks), tags);
        textArea.setText(output);
        textArea.moveCaretPosition(0);
    }
    private String[] tags = {"per", "loc", "org", "pos", "job", "date"};
    private String[] chunks = {"NP"};
    private Color[] colors = {Color.CYAN, Color.LIGHT_GRAY, Color.MAGENTA, Color.BLUE, Color.YELLOW, Color.ORANGE};
    private Color[] colorsChunk = {Color.green};
//    Dung's code
    private String fileName = "";
    private HashMap<String, String> mapConfig;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    @MyAnnotation(type=1, name=Config.BROWSE_SHORTCUT, defaultShortcut="B")
    private javax.swing.JButton buttonBrowse;
    @MyAnnotation(type = 1, name = Config.LOC_SHORTCUT, defaultShortcut = "L")
    private javax.swing.JButton buttonLoc;
    private javax.swing.JButton buttonNP;
    @MyAnnotation(type = 1, name = Config.ORG_SHORTCUT, defaultShortcut = "O")
    private javax.swing.JButton buttonOrg;
    @MyAnnotation(type = 1, name = Config.PER_SHORTCUT, defaultShortcut = "P")
    private javax.swing.JButton buttonPer;
    private javax.swing.JButton buttonReload;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonSaveAs;
    @MyAnnotation(type=1, name=Config.UNTAG_SHORTCUT, defaultShortcut="U")
    private javax.swing.JButton buttonUntag;
    private javax.swing.JCheckBox checkBoxChoose;
    private javax.swing.JCheckBox checkBoxEdit;
    private javax.swing.JCheckBox checkBoxMode;
    @MyAnnotation(type = 1, name = Config.CLICK_SHORTCUT, defaultShortcut = "N")
    private javax.swing.JCheckBox clickCheckbox;
    private javax.swing.JButton convertButton;
    @MyAnnotation(type = 1, name = Config.DATE_SHORTCUT, defaultShortcut = "D")
    private javax.swing.JButton dateButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    @MyAnnotation(type = 1, name = Config.JOB_SHORTCUT, defaultShortcut = "J")
    private javax.swing.JButton jobButton;
    private javax.swing.JLabel labelPath;
    private javax.swing.JLabel labelStatus;
    @MyAnnotation(type = 1, name = Config.MERGE_SHORTCUT, defaultShortcut = "M")
    private javax.swing.JButton mergeButton;
    @MyAnnotation(type = 1, name = Config.POS_SHORTCUT, defaultShortcut = "S")
    private javax.swing.JButton posButton;
    private javax.swing.JButton removeTagsButton;
    private javax.swing.JButton replaceButton;
    @MyAnnotation(type = 1, name = Config.SEG_SHORTCUT, defaultShortcut = "G")
    private javax.swing.JButton segButton;
    private javax.swing.JSpinner sizeSpinner;
    private javax.swing.JTextArea textArea;
    private javax.swing.JTextField textFieldPath;
    // End of variables declaration//GEN-END:variables
    private Class clazz;
}

class ExampleFileFilter extends javax.swing.filechooser.FileFilter {

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";
    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    public ExampleFileFilter() {
        this.filters = new Hashtable();
    }

    public ExampleFileFilter(String extension) {
        this(extension, null);
    }

    public ExampleFileFilter(String extension, String description) {
        this();
        if (extension != null) {
            addExtension(extension);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    public ExampleFileFilter(String[] filters) {
        this(filters, null);
    }

    public ExampleFileFilter(String[] filters, String description) {
        this();
        for (int i = 0; i < filters.length; i++) {
            addExtension(filters[i]);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null && filters.get(getExtension(f)) != null) {
                return true;
            };
        }
        return false;
    }

    public String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            };
        }
        return null;
    }

    public void addExtension(String extension) {
        if (filters == null) {
            filters = new Hashtable(5);
        }
        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }

    public String getDescription() {
        if (fullDescription == null) {
            if (description == null || isExtensionListInDescription()) {
                fullDescription = description == null ? "(" : description + " (";
                Enumeration extensions = filters.keys();
                if (extensions != null) {
                    fullDescription += "." + (String) extensions.nextElement();
                    while (extensions.hasMoreElements()) {
                        fullDescription += ", ." + (String) extensions.nextElement();
                    }
                }
                fullDescription += ")";
            } else {
                fullDescription = description;
            }
        }
        return fullDescription;
    }

    public void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }

    public void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        fullDescription = null;
    }

    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }
}

class tokenizeVietnamese {

    public static void token() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        String dir = "." + File.separator + "vnTagger" + File.separator + "vnTagger.bat";
        try {
            process = rt.exec(dir + " -i" + " input.txt" + " -o" + " outputTok.txt" + " -u" + " -p");
            process.waitFor();
        } catch (IOException ex) {
            ex.printStackTrace();

        } catch (InterruptedException itex) {
            itex.toString();
        }

        String input = "." + File.separator + "vnTagger" + File.separator + "outputTok.txt";

        String ret = modifyvnTagger(input, false);
        System.out.println("---------------------------------modifyvnTagger\n\n" + ret);
        String retu = seperateSentencesInString(ret);
        System.out.println("---------------------------------seperateSentence\n\n" + retu);

        String output = "." + File.separator + "input.txt";
        BufferedWriter f = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output), "UTF-8"));

        f.write(retu);
        f.close();
    }

    public static String modifyvnTagger(String fileSource, boolean delete) {
        String[] punctuations = {".", ",", "!", "(", ")", "[", "]", "{", "}", "$", "?", "@", "\"", "-", "/", "...", ":", "'", ";", "*", "+", "#",
            "%", "^", "&", "=", "|", "~", "`"};

        try {
            String line = "";
            String ret = "";

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileSource), "UTF-8"));

            while ((line = in.readLine()) != null) {
                StringTokenizer lineTknr = new StringTokenizer(line, " ");

                while (lineTknr.hasMoreTokens()) {
                    line = lineTknr.nextToken();

                    int i = line.lastIndexOf("/");
                    line = line.substring(0, i);

                    if (line.indexOf("_") != 0) {
                        line = line.replaceAll("_", " ");
                    }
                    line = "[" + line + "]";

                    char c = line.charAt(1);
                    int ascii = (int) c;

                    if (ascii != 65279) {
                        String toAdd = checkPunctuation(punctuations, line);
                        if (toAdd.length() != 0) {
                            ret += toAdd + " ";
                        } else {
                            ret += line + " ";
                        }
                    }
                }
                if (ret.length() != 1) {
                    ret += "\n";
                }
            }

            in.close();
            File toDelete = new File(fileSource);
            if (delete) {
                toDelete.delete();
            }

            return ret;
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public static String checkPunctuation(String[] punctuations, String line) {
        String result = "";
        for (int i = 0; i < punctuations.length; i++) {
            if (("[" + punctuations[i] + "]").equals(line)) {
                result = punctuations[i];
                break;
            }
        }
        return result;
    }

    public static String seperateSentencesInString(String str) {
        String[] dots = {".", "?", "!", "..."};
        String line = "", result = "", sentence = "", prePart = "", subLine = "";
        //int count = 0;
        int[] find = {0, 0};
        try {
            StringTokenizer strtok = new StringTokenizer(str, "\n");
            while (strtok.hasMoreTokens()) {
                line = strtok.nextToken();
                if (line.trim().length() == 0) {
                    continue;
                }
                //count++;
                int pos = 0;
                do {
                    //try{
                    find = findPattern(dots, line, pos);
                    //}
                    //catch (Exception ex)
                    //{
                    //    System.out.println("right here, line = " + line + " pos = " + pos);
                    //}
                    //if (count == 22) System.out.println("find: " + find[0] + " : " + find[1] + " : pre = " + prePart);

                    if (find[1] > dots.length) {
                        prePart += line.substring(pos).trim();
                        break;
                    }
                    subLine = line.substring(pos, find[0] + dots[find[1]].length() + 2);
                    if (prePart.length() != 0) {
                        sentence = prePart + " " + subLine;
                        prePart = "";
                    } else {
                        sentence = subLine;
                    }
//                    System.out.println("resut at seperate sentence = " + sentence);
                    result += sentence + "\n\n";
                    pos = find[0] + dots[find[1]].length() + 2;

                } while (true);
            }

            result = result.trim();

            return result;
        } catch (Exception ex) {
            //System.out.println("Error: " + ex + " at line " + count);

            return ex.toString();
        }
    }

    public static int[] findPattern(String[] dots, String str, int pos) {
        String[] others = {"]", ")", "}", "'", "\""};
        String[] completeOthers = {"[", "(", "{", "'", "\""};
        String match = "";
        int con = dots.length + 2;
        boolean state = false;
        int[] first = {str.length() + 2, con};
        //System.out.println("reach the start!");
        for (int i = 0; i < dots.length; i++) {
            int temp = str.indexOf(" " + dots[i] + " ", pos);
            if (temp < 0) {
                continue;
            }
            if (temp < first[0]) {
                first[0] = temp;
                first[1] = i;
            }

        }
        if (first[1] == con) {
            return first;
        }
        //return first;

        if ((first[0] + dots[first[1]].length() + 2) == str.length()) {
            return first;
        }

        match = str.substring(first[0] + dots[first[1]].length() + 2, first[0] + dots[first[1]].length() + 3);
        //System.out.println("reach the first!");

        for (int i = 0; i < others.length; i++) {
            if (match.equals(others[i])) {
                if ((countAppearance(completeOthers[i], str, pos, first[0]) % 2) == 1) {
                    try {
                        first = findPattern(dots, str, first[0] + dots[first[1]].length() + 3);
                        //System.out.println("Odd");
                    } catch (Exception et) {
                        System.out.println("bub bub : " + first[0] + " : " + first[1]);
                    }
                }
                //System.out.println(countAppearance(completeOthers[i], str, pos, first[0]));
                //System.out.println(match);
                state = true;
                break;
            }
        }
        if (state) {
            //System.out.println("this way: " + first[0] + " : " + first[1]);
            return first;
        }
        //System.out.println("as you guess" + match);
        // System.out.println("reach the second!");
        state = false;

        for (int i = 0; i < dots.length; i++) {
            if (match.equals(dots[i])) {
                first[0] += dots[first[1]].length() + 1;
                first[1] = i;
                state = true;
                break;
            }
        }

        if (state) {
            return first;
        }

        //System.out.println("reach the third!");

        char ch = str.charAt(first[0] + dots[first[1]].length() + 3);

        if (Character.isDigit(ch) || Character.isUpperCase(ch)) {
            return first;
        }

        //System.out.println("reach the end!");

        return findPattern(dots, str, first[0] + dots[first[1]].length() + 2);
    }

    public static int countAppearance(String pattern, String str, int first, int end) {
        if (first < 0) {
            first = 0;
        }
        String sub = str.substring(first, end + 1);
        int count = 0, run = 0;

        while (run > -1) {
            run = sub.indexOf(pattern, run);
            if (run > -1) {
                count++;
                run++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        String[] dots = {".", "?", "!", "..."};
        String line = "[Mới đây] , [trong] [cuốn] [sách] [dày] [hơn] [500] [trang] [nhan đề] [Why Vietnam] ? ( [Tại sao] [Việt Nam] ? ) , [ông] [Archimedes] [L. A.] [Patti] , [một] [người] [Mỹ] [vốn] [là] [đại tá] [tình báo] , [miêu tả] [những] [con người] [và] [sự kiện] [ở] [Hà Nội] [vào] [năm] [1945] , [trong] [đó] [có] [đoạn] :";
        try {
            int[] find = findPattern(dots, line, 0);
            System.out.println("RESULT HERE: " + find[0] + " : And: " + find[1]);
        } catch (Exception e) {
            System.out.println("heeeee!" + e);
        }
    }
}