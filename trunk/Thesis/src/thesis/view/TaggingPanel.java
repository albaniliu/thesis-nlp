/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TaggingPanel.java
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
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.undo.UndoManager;
import util.Bind;
import util.Config;
import util.ConvertText;
import util.GUIFunction;
import util.ReadWriteFile;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import util.EntityAnnotation;
import util.ShortcutAnnotation;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

/**
 *
 * @author banhbaochay
 */
public class TaggingPanel extends javax.swing.JPanel {

    /** Creates new form TaggingPanel
     * @param mapConfig 
     */
    public TaggingPanel(HashMap<String, String> mapConfig) {
        this.mapConfig = mapConfig;
        undoManager = new UndoManager();
        initComponents();
        String fontName = mapConfig.get(Config.TEXT_FONT_NAME);
        String styleString = mapConfig.get(Config.TEXT_FONT_STYLE);
        String sizeString = mapConfig.get(Config.TEXT_FONT_SIZE);
        Font fontArea = new Font(fontName, GUIFunction.string2Int(styleString), Integer.parseInt(sizeString));
        setFontArea(fontArea);
//        GUIFunction.setFontArea(textArea, fontArea);
        setShortcut();
    }

    private void setShortcut() {
        bind = new Bind(this);
        actionMap = Application.getInstance().getContext().getActionMap(this);
        setInfo(buttonBrowse);
        setInfo(buttonLoc);
        setInfo(buttonPer);
        setInfo(buttonOrg);
        setInfo(dateButton);
        setInfo(jobButton);
        setInfo(posButton);
        setInfo(combineButton);
        setInfo(splitButton);
        setInfo(buttonUntag);
        setInfo(clickCheckbox);
        setInfo(checkBoxMode);
        setInfo(undoButton);
        setInfo(redoButton);
        setInfo(buttonSave);
        setInfo(buttonSaveAs);
    }// end setShortcut method

    /**
     * Set shortcut va tooltip cho 1 component
     */
    private void setInfo(Object object) {
        if (object instanceof JButton) {
            JButton button = (JButton) object;
            try {
                Field field = TaggingPanel.class.getDeclaredField(button.getName());
                /*
                 * Lay ra cac gia tri luu trong annotation cua field
                 */
                ShortcutAnnotation shortcutAn = field.getAnnotation(ShortcutAnnotation.class);
                String actionName = shortcutAn.actionName();
                String nameInMap = shortcutAn.nameInMap();
                /*
                 * Gan action trong actionMap co ten la actionName voi shortcut key luu trong mapConfig
                 */
                bind.keyBind(actionMap.get(actionName),
                        Bind.String2KeyStroke(mapConfig.get(nameInMap)));
                /*
                 * Set tooltip
                 */
                String oldToolTip = (button.getToolTipText() == null) ? ""
                        : button.getToolTipText().replaceAll(" *\\[.*\\] *", "") + " ";
                button.setToolTipText(oldToolTip + "[" + mapConfig.get(nameInMap) + "]");
            } catch (NoSuchFieldException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }// end try catch

        } else if (object instanceof JCheckBox) {
            JCheckBox checkbox = (JCheckBox) object;
            try {
                Field field = TaggingPanel.class.getDeclaredField(checkbox.getName());
                /*
                 * Lay ra cac gia tri luu trong annotation cua field
                 */
                ShortcutAnnotation shortcutAn = field.getAnnotation(ShortcutAnnotation.class);
                String actionName = shortcutAn.actionName();
                String nameInMap = shortcutAn.nameInMap();
                /*
                 * Gan action trong actionMap co ten la actionName voi shortcut key luu trong mapConfig
                 */
                bind.keyBind(actionMap.get(actionName),
                        Bind.String2KeyStroke(mapConfig.get(nameInMap)));
                /*
                 * Set tooltip
                 */
                String oldToolTip = (checkbox.getToolTipText() == null) ? ""
                        : checkbox.getToolTipText().replaceAll(" *\\[.*\\] *", "") + " ";
                checkbox.setToolTipText(oldToolTip + "[" + mapConfig.get(nameInMap) + "]");
            } catch (NoSuchFieldException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }// end try catch
        }// end if
    }// end setInfo method

    // <editor-fold defaultstate="collapsed" desc="saveTextArea method">
    public void saveTextArea(String file) {
        try {
            PrintWriter out = ReadWriteFile.writeFile(file, "UTF-8");
            out.write(textArea.getText());
            out.close();
//            buttonSave.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Save successfull");
        } catch (Exception e) {
//            buttonSave.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Save failed!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            //Uncomment this line if want to trace your last done
            //traceDone(text, hili);
            in.close();
//            buttonSave.setEnabled(false);
//            buttonReload.setEnabled(false);
//            if (checkBoxChoose.isSelected()) {
//                setEnableButtonTag(true);
//            } else {
//                buttonNP.setEnabled(true);
//                buttonUntag.setEnabled(true);
//            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Can not open file " + textFieldPath.getName() 
                    + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        buttonPer = new javax.swing.JButton();
        buttonLoc = new javax.swing.JButton();
        buttonOrg = new javax.swing.JButton();
        posButton = new javax.swing.JButton();
        jobButton = new javax.swing.JButton();
        dateButton = new javax.swing.JButton();
        buttonNP = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        combineButton = new javax.swing.JButton();
        splitButton = new javax.swing.JButton();
        buttonUntag = new javax.swing.JButton();
        replaceButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        textFieldPath = new javax.swing.JTextField();

        setMinimumSize(new java.awt.Dimension(1105, 548));
        setPreferredSize(new java.awt.Dimension(1105, 548));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
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
        textArea.getDocument().addUndoableEditListener(new UndoListener());

        checkBoxEdit.setSelected(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(TaggingPanel.class);
        checkBoxEdit.setText(resourceMap.getString("checkBoxEdit.text")); // NOI18N
        checkBoxEdit.setName("checkBoxEdit"); // NOI18N

        clickCheckbox.setText(resourceMap.getString("clickCheckbox.text")); // NOI18N
        clickCheckbox.setName("clickCheckbox"); // NOI18N

        checkBoxMode.setText(resourceMap.getString("checkBoxMode.text")); // NOI18N
        checkBoxMode.setName("checkBoxMode"); // NOI18N

        checkBoxChoose.setSelected(true);
        checkBoxChoose.setText(resourceMap.getString("checkBoxChoose.text")); // NOI18N
        checkBoxChoose.setName("checkBoxChoose"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(TaggingPanel.class, this);
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, textFieldPath, org.jdesktop.beansbinding.ELProperty.create("${text}"), buttonSave, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        binding.setConverter(textBooleanConverter2);
        bindingGroup.addBinding(binding);

        jToolBar1.add(buttonSave);

        buttonSaveAs.setAction(actionMap.get("saveAs")); // NOI18N
        buttonSaveAs.setIcon(resourceMap.getIcon("buttonSaveAs.icon")); // NOI18N
        buttonSaveAs.setText(resourceMap.getString("buttonSaveAs.text")); // NOI18N
        buttonSaveAs.setToolTipText(resourceMap.getString("buttonSaveAs.toolTipText")); // NOI18N
        buttonSaveAs.setName("buttonSaveAs"); // NOI18N
        jToolBar1.add(buttonSaveAs);

        undoButton.setIcon(resourceMap.getIcon("undoButton.icon")); // NOI18N
        undoButton.setText(resourceMap.getString("undoButton.text")); // NOI18N
        undoButton.setEnabled(false);
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setName("undoButton"); // NOI18N
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(undoButton);

        redoButton.setIcon(resourceMap.getIcon("redoButton.icon")); // NOI18N
        redoButton.setText(resourceMap.getString("redoButton.text")); // NOI18N
        redoButton.setEnabled(false);
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setName("redoButton"); // NOI18N
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(redoButton);

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

        combineButton.setAction(actionMap.get("combineWord")); // NOI18N
        combineButton.setIcon(resourceMap.getIcon("combineButton.icon")); // NOI18N
        combineButton.setText(resourceMap.getString("combineButton.text")); // NOI18N
        combineButton.setToolTipText(resourceMap.getString("combineButton.toolTipText")); // NOI18N
        combineButton.setName("combineButton"); // NOI18N
        jToolBar1.add(combineButton);

        splitButton.setAction(actionMap.get("splitWord")); // NOI18N
        splitButton.setIcon(resourceMap.getIcon("splitButton.icon")); // NOI18N
        splitButton.setText(resourceMap.getString("splitButton.text")); // NOI18N
        splitButton.setToolTipText(resourceMap.getString("splitButton.toolTipText")); // NOI18N
        splitButton.setName("splitButton"); // NOI18N
        jToolBar1.add(splitButton);

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

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        textFieldPath.setEditable(false);
        textFieldPath.setName("textFieldPath"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(clickCheckbox)
                .addGap(23, 23, 23)
                .addComponent(checkBoxMode)
                .addGap(38, 38, 38)
                .addComponent(checkBoxChoose)
                .addGap(50, 50, 50)
                .addComponent(checkBoxEdit))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(textFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1093, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clickCheckbox)
                    .addComponent(checkBoxMode)
                    .addComponent(checkBoxChoose)
                    .addComponent(checkBoxEdit))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Event Mouse Released in TextArea">
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
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Event Mouse Wheel in TextArea">
    private void textAreaMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_textAreaMouseWheelMoved
        // TODO add your handling code here:
        int notches = evt.getWheelRotation();
        JScrollBar vBar = jScrollPane1.getVerticalScrollBar();
        int vBarPos = vBar.getValue();
        Font oldFont = textArea.getFont();
        int oldSize = oldFont.getSize();
        if (evt.isControlDown()) {
            if (notches < 0) {
                /*
                 * Scroll UP
                 */
                if (oldSize <= 28) {
                    Font changedFont = oldFont.deriveFont((float) oldSize + 2);
                    GUIFunction.setFontArea(textArea, changedFont);
                }// end if oldSize
            } else {
                /*
                 * Scroll DOWN
                 */
                if (oldSize >= 11) {
                    Font changedFont = oldFont.deriveFont((float) oldSize - 2);
                    GUIFunction.setFontArea(textArea, changedFont);
                }// end if oldSize
            }// end if notches
        } else {
            if (notches < 0) {
                // UP
                vBar.setValue(vBarPos - 50);
            } else {
                // Down
                vBar.setValue(vBarPos + 50);
            }
        }// end if ctrl
    }//GEN-LAST:event_textAreaMouseWheelMoved
    //</editor-fold>
    
    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        if (undoManager.canUndo()) {
            undoManager.undo();
        }// end if undo.canUndo()
        updateStateUndoButton();
    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
        if (undoManager.canRedo()) {
            undoManager.redo();
        }// end if undo.canRedo()
        updateStateUndoButton();
    }//GEN-LAST:event_redoButtonActionPerformed

    /**
     * Danh nhan ung voi tung button
     * @param button 
     */
    // <editor-fold defaultstate="collapsed" desc="tag method">
    public void tag(JButton button) {
        try {
            EntityAnnotation annotation = TaggingPanel.class.getDeclaredField(button.getName()).
                    getAnnotation(EntityAnnotation.class);
            String entityName = annotation.entityName();
            ENTITY entity = ENTITY.getEntity(entityName);
            int start = textArea.getSelectionStart();

            int end = textArea.getSelectionEnd();
            int offset = 0;
            if (end > start) {
                offset = entity.getStartLength() + 1;

                textArea.insert(entity.getStartTag().toLowerCase() + " ", start);
                textArea.insert(" " + entity.getEndTag().toLowerCase(), end + offset);

                textArea.select(start + offset, end + offset);

                Highlighter hili = textArea.getHighlighter();
                hili.addHighlight(start + offset, end + offset,
                        (HighlightPainter) new DefaultHighlightPainter(entity.getColor()));

                //Dung change
                textArea.requestFocus();
                textArea.select(start, end + offset + entity.getEndLength() + 1);
                //end
            } else {
                JOptionPane.showMessageDialog(null, "No selected words to tag");
            }
//            buttonSave.setEnabled(true);
//            buttonReload.setEnabled(true);

        } catch (Exception ex) {
            Logger.getLogger(TaggingPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end tag method
    //</editor-fold>

    /**
     * Thay doi font cua textarea. Phuong thuc nay de co the thay doi font textarea
     * tu class khac
     * @param font 
     */
    // <editor-fold defaultstate="collapsed" desc="setFontArea method">
    public final void setFontArea(Font font) {
        GUIFunction.setFontArea(textArea, font);
    }// end setFontArea method
    //</editor-fold>

    /**
     * Set lai trang thai cua 2 nut undo va redo
     * Duoc goi moi khi thuc hien viec undo hoac redo
     */
    // <editor-fold defaultstate="collapsed" desc="updateStateUndoButton method">
    private void updateStateUndoButton() {
        if (undoManager.canUndo()) {
            undoButton.setEnabled(true);
        } else {
            undoButton.setEnabled(false);
        }// end if undo
        
        if (undoManager.canRedo()) {
            redoButton.setEnabled(true);
        } else {
            redoButton.setEnabled(false);
        }// end if redo
    }// end updateStateUndoButton method
    //</editor-fold>
    
    /*
     * Tat ca action trong class
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

                // Dung's code
                // Save nameInMap of file for Save As action
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
    // <editor-fold defaultstate="collapsed" desc="save action">
    public void save() {
        if (JOptionPane.showConfirmDialog(this,
                "This action will overwrite your old file, do you want to continue? "
                + "(Press Yes to continue ,Press No to abort)", "WARNING",
                JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            saveTextArea(textFieldPath.getText());
        }
    }// end save method
    //</editor-fold>

    @Action
    // <editor-fold defaultstate="collapsed" desc="saveAs action">
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
    //</editor-fold>

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

//            labelStatus.setText("NP Tagged:  " + textArea.getSelectedText());
//            buttonSave.setEnabled(true);
//            buttonReload.setEnabled(true);

            //Dung change
            textArea.requestFocus();
            textArea.select(start, end + 5 + 6);
            //end

        } catch (Exception e) {
//            labelStatus.setText("Error: " + e);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// end tagNP method
    //</editor-fold>

    @Action
    //<editor-fold defaultstate="collapsed" desc="combineWord action">
    public void combineWord() {
        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();
        if (selectionEnd > selectionStart) {
            String text = textArea.getSelectedText();
            textArea.replaceRange(ConvertText.combineWord(text), selectionStart, selectionEnd);
        }// end if selectionEnd >= selectionStart
    }// end combineWord method
    //</editor-fold>

    @Action
    // <editor-fold defaultstate="collapsed" desc="splitWord action">
    public void splitWord() {
        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();
        if (selectionEnd > selectionStart) {
            String text = textArea.getSelectedText();
            textArea.replaceRange(ConvertText.splitWord(text), selectionStart, selectionEnd);
        }// end if selectionEnd >= selectionStart
    }// end splitWord method
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
                    Logger.getLogger(TaggingPanelThien.class.getName()).log(Level.SEVERE, null, ex);
                }
            }// end if group

        }// end if OK_OPTION
    }// end replaceName method
    //</editor-fold>
    
    @Action
    // <editor-fold defaultstate="collapsed" desc="changeCLickState action">
    public void changeClickState() {
        clickCheckbox.setSelected(!clickCheckbox.isSelected());
    }// end changeClickState method
    //</editor-fold>
    
    @Action
    // <editor-fold defaultstate="collapsed" desc="changeMode action">
    public void changeMode() {
        checkBoxMode.setSelected(!checkBoxMode.isSelected());
    }// end changeMode method
    //</editor-fold>
    
    @Action
    // <editor-fold defaultstate="collapsed" desc="undo action">
    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }// end if undo.canUndo()
        updateStateUndoButton();
    }// end undo method
    //</editor-fold>
    
    @Action
    // <editor-fold defaultstate="collapsed" desc="redo action">
    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }// end if
        updateStateUndoButton();
    }// end redo method
    //</editor-fold>
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    @ShortcutAnnotation(type=1, nameInMap=Config.BROWSE_SHORTCUT, defaultShortcut=Config.BROWSE_SHORTCUT_DEFAULT, actionName=Config.BROWSE_ACTION)
    private javax.swing.JButton buttonBrowse;
    @EntityAnnotation(entityName="LOC")
    @ShortcutAnnotation(type=1, nameInMap=Config.LOC_SHORTCUT, defaultShortcut=Config.LOC_SHORTCUT_DEFAULT, actionName=Config.LOC_ACTION)
    private javax.swing.JButton buttonLoc;
    private javax.swing.JButton buttonNP;
    @EntityAnnotation(entityName="ORG")
    @ShortcutAnnotation(type=1, nameInMap=Config.ORG_SHORTCUT, defaultShortcut=Config.ORG_SHORTCUT_DEFAULT, actionName=Config.ORG_ACTION)
    private javax.swing.JButton buttonOrg;
    @EntityAnnotation(entityName="PER")
    @ShortcutAnnotation(type=1, nameInMap=Config.PER_SHORTCUT, defaultShortcut=Config.PER_SHORTCUT_DEFAULT, actionName=Config.PER_ACTION)
    private javax.swing.JButton buttonPer;
    @ShortcutAnnotation(type=1, nameInMap=Config.SAVE_SHORTCUT, defaultShortcut=Config.SAVE_SHORTCUT_DEFAULT, actionName=Config.SAVE_ACTION)
    private javax.swing.JButton buttonSave;
    @ShortcutAnnotation(type=1, nameInMap=Config.SAVE_AS_SHORTCUT, defaultShortcut=Config.SAVE_AS_SHORTCUT_DEFAULT, actionName=Config.SAVE_AS_ACTION)
    private javax.swing.JButton buttonSaveAs;
    @ShortcutAnnotation(type=1, nameInMap=Config.UNTAG_SHORTCUT, defaultShortcut=Config.UNTAG_SHORTCUT_DEFAULT, actionName=Config.UNTAG_ACTION)
    private javax.swing.JButton buttonUntag;
    private javax.swing.JCheckBox checkBoxChoose;
    private javax.swing.JCheckBox checkBoxEdit;
    @ShortcutAnnotation(type=1, nameInMap=Config.MODE_SHORTCUT, defaultShortcut=Config.MODE_SHORTCUT_DEFAULT, actionName=Config.MODE_ACTION)
    private javax.swing.JCheckBox checkBoxMode;
    @ShortcutAnnotation(type=1, nameInMap=Config.CLICK_SHORTCUT, defaultShortcut=Config.CLICK_SHORTCUT_DEFAULT, actionName=Config.CLICK_ACTION)
    private javax.swing.JCheckBox clickCheckbox;
    @ShortcutAnnotation(type=1, nameInMap=Config.COMBINE_SHORTCUT, defaultShortcut=Config.COMBINE_SHORTCUT_DEFAULT, actionName=Config.COMBINE_ACTION)
    private javax.swing.JButton combineButton;
    @EntityAnnotation(entityName="DATE")
    @ShortcutAnnotation(type=1, nameInMap=Config.DATE_SHORTCUT, defaultShortcut=Config.DATE_SHORTCUT_DEFAULT, actionName=Config.DATE_ACTION)
    private javax.swing.JButton dateButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    @EntityAnnotation(entityName="JOB")
    @ShortcutAnnotation(type=1, nameInMap=Config.JOB_SHORTCUT, defaultShortcut=Config.JOB_SHORTCUT_DEFAULT, actionName=Config.JOB_ACTION)
    private javax.swing.JButton jobButton;
    @EntityAnnotation(entityName="POS")
    @ShortcutAnnotation(type=1, nameInMap=Config.POS_SHORTCUT, defaultShortcut=Config.POS_SHORTCUT_DEFAULT, actionName=Config.POS_ACTION)
    private javax.swing.JButton posButton;
    @ShortcutAnnotation(type=1, nameInMap=Config.REDO_SHORTCUT, defaultShortcut=Config.REDO_SHORTCUT_DEFAULT, actionName=Config.REDO_ACTION)
    private javax.swing.JButton redoButton;
    private javax.swing.JButton replaceButton;
    @ShortcutAnnotation(type=1, nameInMap=Config.SPLIT_SHORTCUT, defaultShortcut=Config.SPLIT_SHORTCUT_DEFAULT, actionName=Config.SPLIT_ACTION)
    private javax.swing.JButton splitButton;
    private javax.swing.JTextArea textArea;
    private util.TextBooleanConverter textBooleanConverter2;
    private javax.swing.JTextField textFieldPath;
    @ShortcutAnnotation(type=1, nameInMap=Config.UNDO_SHORTCUT, defaultShortcut=Config.UNDO_SHORTCUT_DEFAULT, actionName=Config.UNDO_ACTION)
    private javax.swing.JButton undoButton;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private String[] tags = {"per", "loc", "org", "pos", "job", "date"};
    private String[] chunks = {"NP"};
    private Color[] colors = {Color.CYAN, Color.LIGHT_GRAY, Color.MAGENTA, Color.BLUE, Color.YELLOW, Color.ORANGE};
    private Color[] colorsChunk = {Color.green};
//    Dung's code
    private String fileName = "";
    private HashMap<String, String> mapConfig;
    private Bind bind;
    private ActionMap actionMap;
    private UndoManager undoManager;

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TaggingPanel p = new TaggingPanel(new HashMap<String, String>());
        f.add(p);
        f.pack();
        f.setVisible(true);
    }// end main class
    
    /**
     * Phuc vu cho viec su dung undo manager voi document
     */
    class UndoListener implements UndoableEditListener {
        
        /**
         * Duoc goi khi xuat hien su kien undoable trong document
         * @param e 
         */
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            updateStateUndoButton();
        }
        
    }// end UndoListener class
}// end TaggingPanel class
