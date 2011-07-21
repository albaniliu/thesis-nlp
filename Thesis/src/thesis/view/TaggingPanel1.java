/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TaggingPanel1.java
 *
 * Created on Jun 28, 2011, 11:04:45 PM
 */
package thesis.view;

import feature.ENTITY;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
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
import lib.ReadWriteFile;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.beansbinding.Converter;
import util.EntityAnnotation;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

/**
 *
 * @author banhbaochay
 */
public class TaggingPanel1 extends javax.swing.JPanel {

    /** Creates new form TaggingPanel1 */
    public TaggingPanel1(HashMap<String, String> mapConfig) {
        this.mapConfig = mapConfig;
        initComponents();
//        String fontName = mapConfig.get(Config.TEXT_FONT_NAME);
//        String styleString = mapConfig.get(Config.TEXT_FONT_STYLE);
//        String sizeString = mapConfig.get(Config.TEXT_FONT_SIZE);
//        Font fontArea = new Font(fontName, GUIFunction.string2Int(styleString), Integer.parseInt(sizeString));
//        GUIFunction.setFontArea(textArea, fontArea);
        setShortcut();
    }
    
    private void setShortcut() {
        keyBind("browse", "F2");
        keyBind("save", "F3");
    }// end setShortcut method

    // <editor-fold defaultstate="collapsed" desc="saveTextArea method">
    public void saveTextArea(String file) {
        try {
            PrintWriter out = ReadWriteFile.writeFile(file, "UTF-8");
            out.write(textArea.getText());
            out.close();
            buttonSave.setEnabled(false);
            labelStatus.setText("Save SUCCESSFULLY!");
        } catch (Exception e) {
            buttonSave.setEnabled(false);
            labelStatus.setText("Save UNSUCCESSFULLY!: " + e);
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="vnTagger method">
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
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="modifyvnTagger method">
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
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="checkPunctuation method">
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
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="copyFile method">
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
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="eliminateTags method">
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
    //</editor-fold>

    /*
     * Actions
     */
    @Action
    // <editor-fold defaultstate="collapsed" desc="browse action">
    public void browse() {
        try {
//            File ff = new File(".");
//            String fileDes = ff.getCanonicalPath() + File.separator + "data" + File.separator + "labs" + File.separator;
//            Date d = new Date();
//            fileDes += d.getTime() + "_";
//            //            Uncomment to return org file
//            //            String wd = System.getProperty("user.dir");
//            //            JFileChooser choose = new JFileChooser(wd);

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
//                fileDes += inFile.getName();
//                if (checkBoxChoose.isSelected()) {
//                    copyFile(inFile.getPath(), fileDes);
//                }
                if (checkBoxMode.isSelected()) {
                    loadFile(textFieldPath.getText());
                } else {
                    textArea.setText(vnTagger(textFieldPath.getText()));
//                    setEnableButtonTagAll(false);
//                    buttonNP.setEnabled(false);
                    labelStatus.setText("Chunking Successfully!");
                }

                //                Dung's code
                textArea.setCaretPosition(0);
                //                End

            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }// end browse method
    // </editor-fold>

    @Action
    public void save() {
        if (JOptionPane.showConfirmDialog(this,
                "This action will overwrite your old file, do you want to continue? "
                + "(Press Yes to continue ,Press No to abort)", "WARNING",
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            saveTextArea(textFieldPath.getText());
        }
    }// end save method

    @Action
    public void saveAs() {
        // Dung's code
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
    }// end saveAs method

    // <editor-fold defaultstate="collapsed" desc="loadFile method">
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
//            buttonReload.setEnabled(false);
//            if (checkBoxChoose.isSelected()) {
//                setEnableButtonTag(true);
//            } else {
//                buttonNP.setEnabled(true);
//                buttonUntag.setEnabled(true);
//            }
        } catch (Exception ex) {
            labelStatus.setText("Can not open file " + textFieldPath.getName() + ": " + ex);
        }
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="highlightTag method">
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
    //</editor-fold>

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        textBooleanConverter2 = new util.TextBooleanConverter();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        checkBoxEdit = new javax.swing.JCheckBox();
        clickCheckbox = new javax.swing.JCheckBox();
        checkBoxMode = new javax.swing.JCheckBox();
        checkBoxChoose = new javax.swing.JCheckBox();
        jToolBar1 = new javax.swing.JToolBar();
        buttonBrowse = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        buttonSaveAs = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        buttonPer = new javax.swing.JButton();
        buttonLoc = new javax.swing.JButton();
        buttonOrg = new javax.swing.JButton();
        posButton = new javax.swing.JButton();
        jobButton = new javax.swing.JButton();
        dateButton = new javax.swing.JButton();
        buttonNP = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        mergeButton = new javax.swing.JButton();
        segButton = new javax.swing.JButton();
        buttonUntag = new javax.swing.JButton();
        replaceButton = new javax.swing.JButton();
        labelStatus = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        textFieldPath = new javax.swing.JTextField();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setName("textArea"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxEdit, org.jdesktop.beansbinding.ELProperty.create("${selected}"), textArea, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        textArea.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                textAreaMouseWheelMoved(evt);
            }
        });
        textArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                textAreaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(textArea);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                change();
            }
            public void removeUpdate(DocumentEvent e) {
                change();
            }
            public void changedUpdate(DocumentEvent e) {
                change();
            }
            private void change() {
                buttonSave.setEnabled(true);
            }
        });

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 144, 840, 370));

        checkBoxEdit.setSelected(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(TaggingPanel1.class);
        checkBoxEdit.setText(resourceMap.getString("checkBoxEdit.text")); // NOI18N
        checkBoxEdit.setName("checkBoxEdit"); // NOI18N
        add(checkBoxEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        clickCheckbox.setText(resourceMap.getString("clickCheckbox.text")); // NOI18N
        clickCheckbox.setName("clickCheckbox"); // NOI18N
        add(clickCheckbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, -1, -1));

        checkBoxMode.setText(resourceMap.getString("checkBoxMode.text")); // NOI18N
        checkBoxMode.setName("checkBoxMode"); // NOI18N
        add(checkBoxMode, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        checkBoxChoose.setSelected(true);
        checkBoxChoose.setText(resourceMap.getString("checkBoxChoose.text")); // NOI18N
        checkBoxChoose.setName("checkBoxChoose"); // NOI18N
        add(checkBoxChoose, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(TaggingPanel1.class, this);
        buttonBrowse.setAction(actionMap.get("browse")); // NOI18N
        buttonBrowse.setIcon(resourceMap.getIcon("buttonBrowse.icon")); // NOI18N
        buttonBrowse.setText(resourceMap.getString("buttonBrowse.text")); // NOI18N
        buttonBrowse.setToolTipText(resourceMap.getString("buttonBrowse.toolTipText")); // NOI18N
        buttonBrowse.setName("buttonBrowse"); // NOI18N
        jToolBar1.add(buttonBrowse);

        buttonSave.setAction(actionMap.get("save")); // NOI18N
        buttonSave.setIcon(resourceMap.getIcon("buttonSave.icon")); // NOI18N
        buttonSave.setText(resourceMap.getString("buttonSave.text")); // NOI18N
        buttonSave.setToolTipText(resourceMap.getString("buttonSave.toolTipText")); // NOI18N
        buttonSave.setName("buttonSave"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, textFieldPath, org.jdesktop.beansbinding.ELProperty.create("${text}"), buttonSave, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        binding.setConverter(textBooleanConverter2);
        bindingGroup.addBinding(binding);

        buttonSave.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                buttonSavePropertyChange(evt);
            }
        });
        jToolBar1.add(buttonSave);

        buttonSaveAs.setAction(actionMap.get("saveAs")); // NOI18N
        buttonSaveAs.setIcon(resourceMap.getIcon("buttonSaveAs.icon")); // NOI18N
        buttonSaveAs.setText(resourceMap.getString("buttonSaveAs.text")); // NOI18N
        buttonSaveAs.setToolTipText(resourceMap.getString("buttonSaveAs.toolTipText")); // NOI18N
        buttonSaveAs.setName("buttonSaveAs"); // NOI18N
        jToolBar1.add(buttonSaveAs);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        buttonPer.setAction(actionMap.get("tagPer")); // NOI18N
        buttonPer.setIcon(resourceMap.getIcon("buttonPer.icon")); // NOI18N
        buttonPer.setText(resourceMap.getString("buttonPer.text")); // NOI18N
        buttonPer.setToolTipText(resourceMap.getString("buttonPer.toolTipText")); // NOI18N
        buttonPer.setName("buttonPer"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxChoose, org.jdesktop.beansbinding.ELProperty.create("${selected}"), buttonPer, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(buttonPer);

        buttonLoc.setAction(actionMap.get("tagLoc")); // NOI18N
        buttonLoc.setIcon(resourceMap.getIcon("buttonLoc.icon")); // NOI18N
        buttonLoc.setText(resourceMap.getString("buttonLoc.text")); // NOI18N
        buttonLoc.setToolTipText(resourceMap.getString("buttonLoc.toolTipText")); // NOI18N
        buttonLoc.setName("buttonLoc"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxChoose, org.jdesktop.beansbinding.ELProperty.create("${selected}"), buttonLoc, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(buttonLoc);

        buttonOrg.setAction(actionMap.get("tagOrg")); // NOI18N
        buttonOrg.setIcon(resourceMap.getIcon("buttonOrg.icon")); // NOI18N
        buttonOrg.setText(resourceMap.getString("buttonOrg.text")); // NOI18N
        buttonOrg.setToolTipText(resourceMap.getString("buttonOrg.toolTipText")); // NOI18N
        buttonOrg.setName("buttonOrg"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxChoose, org.jdesktop.beansbinding.ELProperty.create("${selected}"), buttonOrg, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(buttonOrg);

        posButton.setAction(actionMap.get("tagPos")); // NOI18N
        posButton.setIcon(resourceMap.getIcon("posButton.icon")); // NOI18N
        posButton.setText(resourceMap.getString("posButton.text")); // NOI18N
        posButton.setToolTipText(resourceMap.getString("posButton.toolTipText")); // NOI18N
        posButton.setName("posButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxChoose, org.jdesktop.beansbinding.ELProperty.create("${selected}"), posButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(posButton);

        jobButton.setAction(actionMap.get("tagJob")); // NOI18N
        jobButton.setIcon(resourceMap.getIcon("jobButton.icon")); // NOI18N
        jobButton.setText(resourceMap.getString("jobButton.text")); // NOI18N
        jobButton.setToolTipText(resourceMap.getString("jobButton.toolTipText")); // NOI18N
        jobButton.setName("jobButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxChoose, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jobButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(jobButton);

        dateButton.setAction(actionMap.get("tagDate")); // NOI18N
        dateButton.setIcon(resourceMap.getIcon("dateButton.icon")); // NOI18N
        dateButton.setText(resourceMap.getString("dateButton.text")); // NOI18N
        dateButton.setToolTipText(resourceMap.getString("dateButton.toolTipText")); // NOI18N
        dateButton.setName("dateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxChoose, org.jdesktop.beansbinding.ELProperty.create("${selected}"), dateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(dateButton);

        buttonNP.setIcon(resourceMap.getIcon("buttonNP.icon")); // NOI18N
        buttonNP.setText(resourceMap.getString("buttonNP.text")); // NOI18N
        buttonNP.setToolTipText(resourceMap.getString("buttonNP.toolTipText")); // NOI18N
        buttonNP.setName("buttonNP"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, checkBoxChoose, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), buttonNP, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(buttonNP);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar1.add(jSeparator2);

        mergeButton.setAction(actionMap.get("mergeWord")); // NOI18N
        mergeButton.setIcon(resourceMap.getIcon("mergeButton.icon")); // NOI18N
        mergeButton.setText(resourceMap.getString("mergeButton.text")); // NOI18N
        mergeButton.setToolTipText(resourceMap.getString("mergeButton.toolTipText")); // NOI18N
        mergeButton.setName("mergeButton"); // NOI18N
        jToolBar1.add(mergeButton);

        segButton.setAction(actionMap.get("segWord")); // NOI18N
        segButton.setIcon(resourceMap.getIcon("segButton.icon")); // NOI18N
        segButton.setText(resourceMap.getString("segButton.text")); // NOI18N
        segButton.setToolTipText(resourceMap.getString("segButton.toolTipText")); // NOI18N
        segButton.setName("segButton"); // NOI18N
        jToolBar1.add(segButton);

        buttonUntag.setAction(actionMap.get("untag")); // NOI18N
        buttonUntag.setIcon(resourceMap.getIcon("buttonUntag.icon")); // NOI18N
        buttonUntag.setText(resourceMap.getString("buttonUntag.text")); // NOI18N
        buttonUntag.setToolTipText(resourceMap.getString("buttonUntag.toolTipText")); // NOI18N
        buttonUntag.setName("buttonUntag"); // NOI18N
        jToolBar1.add(buttonUntag);

        replaceButton.setAction(actionMap.get("replaceName")); // NOI18N
        replaceButton.setIcon(resourceMap.getIcon("replaceButton.icon")); // NOI18N
        replaceButton.setText(resourceMap.getString("replaceButton.text")); // NOI18N
        replaceButton.setToolTipText(resourceMap.getString("replaceButton.toolTipText")); // NOI18N
        replaceButton.setName("replaceButton"); // NOI18N
        jToolBar1.add(replaceButton);

        add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 2, 840, 40));

        labelStatus.setText(resourceMap.getString("labelStatus.text")); // NOI18N
        labelStatus.setName("labelStatus"); // NOI18N
        add(labelStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 520, -1, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 100, -1, 30));

        textFieldPath.setEditable(false);
        textFieldPath.setName("textFieldPath"); // NOI18N
        add(textFieldPath, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 100, 400, -1));

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void textAreaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textAreaMouseReleased
        // TODO add your handling code here:
        if (!clickCheckbox.isSelected()) {
            /*
             * Che do super click
             */
            int selectionStart = textArea.getSelectionStart();
            int selectionEnd = textArea.getSelectionEnd();
            if (selectionEnd >= selectionStart) {
                if (!evt.isControlDown()) {
                    /*
                     * Neu an them control
                     */
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

    private void textAreaMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_textAreaMouseWheelMoved
        // TODO add your handling code here:
        int notches = evt.getWheelRotation();
        Font oldFold = textArea.getFont();
        int oldSize = oldFold.getSize();
        if (notches < 0) {
            /*
             * Scroll UP
             */
            Font changedFont = oldFold.deriveFont((float) oldSize + 2);
            GUIFunction.setFontArea(textArea, changedFont);
        } else {
            /*
             * Scroll DOWN
             */
            Font changedFont = oldFold.deriveFont((float) oldSize - 2);
            GUIFunction.setFontArea(textArea, changedFont);
        }// end if notches
    }//GEN-LAST:event_textAreaMouseWheelMoved

    private void buttonSavePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_buttonSavePropertyChange
        // TODO add your handling code here:
        if (evt.getPropertyName().equals("enabled")) {
            
        }// end if propertyName == enabled
    }//GEN-LAST:event_buttonSavePropertyChange

    /**
     * Danh nhan ung voi tung button
     * @param button 
     */
    // <editor-fold defaultstate="collapsed" desc="tag method">
    public void tag(JButton button) {
        try {
            EntityAnnotation annotation = TaggingPanel1.class.getDeclaredField(button.getName()).
                    getAnnotation(EntityAnnotation.class);
            String entityName = annotation.entityName();
            ENTITY entity = ENTITY.getEntity(entityName);
            int start = textArea.getSelectionStart();

            int end = textArea.getSelectionEnd();
            int offset = entity.getStartLength() + 1;

            textArea.insert(entity.getStartTag().toLowerCase() + " ", start);
            textArea.insert(" " + entity.getEndTag().toLowerCase(), end + offset);

            textArea.select(start + offset, end + offset);

            Highlighter hili = textArea.getHighlighter();
            hili.addHighlight(start + offset, end + offset,
                    (HighlightPainter) new DefaultHighlightPainter(entity.getColor()));

            labelStatus.setText("Tagged:  " + textArea.getSelectedText());
            buttonSave.setEnabled(true);
//            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + offset + entity.getEndLength() + 1);
            //end
        } catch (Exception ex) {
            Logger.getLogger(TaggingPanel1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end tag method
    //</editor-fold>

    /*
     * Tat ca action trong class
     */
    @Action
    //<editor-fold defaultstate="collapsed" desc="tagPer action">
    public void tagPer() {
        tag(buttonPer);
    }// end tagPer method
    //</editor-fold>
    
    @Action
    //<editor-fold defaultstate="collapsed" desc="tagLoc action">
    public void tagLoc() {
        tag(buttonLoc);
    }// end tagLoc method
    //</editor-fold>
    
    @Action
    //<editor-fold defaultstate="collapsed" desc="tagOrg action">
    public void tagOrg() {
        tag(buttonOrg);
    }// end tagOrg method
    //</editor-fold>
    
    @Action
    // <editor-fold defaultstate="collapsed" desc="tagPos action">
    public void tagPos() {
        tag(posButton);
    }// end tagPos method
    // </editor-fold>
    
    @Action
    // <editor-fold defaultstate="collapsed" desc="tagJob action">
    public void tagJob() {
        tag(jobButton);
    }// end tagJob method
    // </editor-fold>
    
    @Action
    // <editor-fold defaultstate="collapsed" desc="tagDate action">
    public void tagDate() {
        tag(dateButton);
    }// end tagDate method
    // </editor-fold>
    
    @Action
    //<editor-fold defaultstate="collapsed" desc="tagNP action">
    public void tagNP() {
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
//            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + 5 + 6);
            //end

        } catch (Exception e) {
            labelStatus.setText("Error: " + e);
        }
    }// end tagNP method
    //</editor-fold>

    @Action
    //<editor-fold defaultstate="collapsed" desc="mergeWord action">
    public void mergeWord() {
        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();
        if (selectionEnd > selectionStart) {
            String text = textArea.getSelectedText();
            textArea.replaceRange(ConvertText.mergeWords(text), selectionStart, selectionEnd);
        }// end if selectionEnd >= selectionStart
    }// end mergeWord method
    //</editor-fold>

    @Action
    // <editor-fold defaultstate="collapsed" desc="segWord action">
    public void segWord() {
        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();
        if (selectionEnd > selectionStart) {
            String text = textArea.getSelectedText();
            textArea.replaceRange(ConvertText.segWords(text), selectionStart, selectionEnd);
        }// end if selectionEnd >= selectionStart
    }// end segWord method
    //</editor-fold>

    @Action
    // <editor-fold defaultstate="collapsed" desc="untag action">
    public void untag() {
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        if (end > start) {
            String selected = textArea.getSelectedText();
            String replace = selected.replaceAll(" *<[^>]*> *", "");
            textArea.replaceRange(replace, start, end);
        }// end if end > start
    }// end untag method
    //</editor-fold>

    @Action
    // <editor-fold defaultstate="collapsed" desc="replaceName action">
    public void replaceName() {
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

        /*
         * Dinh nghia cac thanh phan duoc liet ke trong dialog mo ra
         */
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
    }// end replaceName method
    //</editor-fold>
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBrowse;
    private javax.swing.JButton buttonLoc;
    private javax.swing.JButton buttonNP;
    private javax.swing.JButton buttonOrg;
    @EntityAnnotation(entityName="PER")
    private javax.swing.JButton buttonPer;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonSaveAs;
    private javax.swing.JButton buttonUntag;
    private javax.swing.JCheckBox checkBoxChoose;
    private javax.swing.JCheckBox checkBoxEdit;
    private javax.swing.JCheckBox checkBoxMode;
    private javax.swing.JCheckBox clickCheckbox;
    private javax.swing.JButton dateButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jobButton;
    private javax.swing.JLabel labelStatus;
    private javax.swing.JButton mergeButton;
    private javax.swing.JButton posButton;
    private javax.swing.JButton replaceButton;
    private javax.swing.JButton segButton;
    private javax.swing.JTextArea textArea;
    private util.TextBooleanConverter textBooleanConverter2;
    private javax.swing.JTextField textFieldPath;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private String[] tags = {"per", "loc", "org", "pos", "job", "date"};
    private String[] chunks = {"NP"};
    private Color[] colors = {Color.CYAN, Color.LIGHT_GRAY, Color.MAGENTA, Color.BLUE, Color.YELLOW, Color.ORANGE};
    private Color[] colorsChunk = {Color.green};
//    Dung's code
    private String fileName = "";
    private HashMap<String, String> mapConfig;
    private Class clazz;

    /**
     * Gan phim tat cho 1 action
     * @param actionName
     * @param keyStroke 
     */
    private void keyBind(String actionName, String keyStroke) {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), actionName);
        getActionMap().put(actionName, actionMap.get(actionName));
    }// end keyBind method
    
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TaggingPanel1 p = new TaggingPanel1(new HashMap<String, String>());
        f.add(p);
        f.pack();
        f.setVisible(true);
    }// end main class
}
