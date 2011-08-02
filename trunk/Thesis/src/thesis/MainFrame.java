/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on Nov 22, 2010, 12:53:01 PM
 */
package thesis;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import thesis.view.MergePanel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import util.Config;
import org.apache.log4j.xml.DOMConfigurator;
import thesis.view.ConvertPanel;
import thesis.view.ShortcutKeyDialog;
import thesis.view.TaggingPanelThien;
import thesis.view.TaggingPanel;

/**
 * MainFrame program
 * @author banhbaochay
 */
public class MainFrame extends javax.swing.JFrame {

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MainFrame.class);
    /**
     * Key trong file config: luu ten font
     */
    public static final String TEXT_FONT_NAME = "text.font.name";
    /**
     * Key trong file config: luu kieu cua font
     */
    public static final String TEXT_FONT_STYLE = "text.font.style";
    /**
     * Key trong file config: luu size cua font
     */
    public static final String TEXT_FONT_SIZE = "text.font.size";

    /** Creates new form MainFrame */
    public MainFrame() {
        DOMConfigurator.configure("log4j.xml");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equalsIgnoreCase(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        proper = new Properties();
        try {
            proper.load(new FileReader("config"));
        } catch (FileNotFoundException ex) {
            try {
                proper.store(new PrintWriter("config"), "New config file");
            } catch (Exception ex1) {
                System.err.println("Can't create config file");
            }
        } catch (IOException e) {
            System.err.println("Can't load config file. Programs can not save config for other");
        }
        loadFont.execute();
        loadMapConfig();
        initComponents();
        addPanel("Convert", new ConvertPanel(mapConfig), "Convert normal text file to JSRE format");
        addPanel("Merge", new MergePanel(mapConfig), "Merge multiple JSRE files to one JSRE file");
        tagPanel = new TaggingPanel(mapConfig);
        addPanel("Tag", tagPanel, "Manually tag entity for normal text file");
    }

    private void addPanel(String title, JPanel panel, String tooltip) {
        mainTab.addTab(title, null, panel, tooltip);
    }
    
    public final void loadMapConfig() {
        String per = proper.getProperty(Config.PER_SHORTCUT, Config.PER_SHORTCUT_DEFAULT);
        String loc = proper.getProperty(Config.LOC_SHORTCUT, Config.LOC_SHORTCUT_DEFAULT);
        String org = proper.getProperty(Config.ORG_SHORTCUT, Config.ORG_SHORTCUT_DEFAULT);
        String pos = proper.getProperty(Config.POS_SHORTCUT, Config.POS_SHORTCUT_DEFAULT);
        String job = proper.getProperty(Config.JOB_SHORTCUT, Config.JOB_SHORTCUT_DEFAULT);
        String date = proper.getProperty(Config.DATE_SHORTCUT, Config.DATE_SHORTCUT_DEFAULT);
        String untag = proper.getProperty(Config.UNTAG_SHORTCUT, Config.UNTAG_SHORTCUT_DEFAULT);
        String merge = proper.getProperty(Config.COMBINE_SHORTCUT, Config.COMBINE_SHORTCUT_DEFAULT);
        String seg = proper.getProperty(Config.SPLIT_SHORTCUT, Config.SPLIT_SHORTCUT_DEFAULT);
        String click = proper.getProperty(Config.CLICK_SHORTCUT, Config.CLICK_SHORTCUT_DEFAULT);
        String textFontName = proper.getProperty(Config.TEXT_FONT_NAME, Config.TEXT_FONT_NAME_DEFAULT);
        String textFontStyle = proper.getProperty(Config.TEXT_FONT_STYLE, Config.TEXT_FONT_STYLE_DEFAULT);
        String textFontSize = proper.getProperty(Config.TEXT_FONT_SIZE, Config.TEXT_FONT_SIZE_DEFAULT);
        String lineFontSize = proper.getProperty(Config.LINE_FONT_SIZE, Config.LINE_FONT_SIZE_DEFAULT);
        String directoryPath = proper.getProperty(Config.DIRECTORY_PATH, Config.DIRECTORY_PATH_DEFAULT);
        String browse = proper.getProperty(Config.BROWSE_SHORTCUT, Config.BROWSE_SHORTCUT_DEFAULT);
        String mode = proper.getProperty(Config.MODE_SHORTCUT, Config.MODE_SHORTCUT_DEFAULT);
        String undo = proper.getProperty(Config.UNDO_SHORTCUT, Config.UNDO_SHORTCUT_DEFAULT);
        String redo = proper.getProperty(Config.REDO_SHORTCUT, Config.REDO_SHORTCUT_DEFAULT);
        String save = proper.getProperty(Config.SAVE_SHORTCUT, Config.SAVE_SHORTCUT_DEFAULT);
        String saveAs = proper.getProperty(Config.SAVE_AS_SHORTCUT, Config.SAVE_AS_SHORTCUT_DEFAULT);
                
        mapConfig = new HashMap<String, String>();
        mapConfig.put(Config.PER_SHORTCUT, per);
        mapConfig.put(Config.LOC_SHORTCUT, loc);
        mapConfig.put(Config.ORG_SHORTCUT, org);
        mapConfig.put(Config.POS_SHORTCUT, pos);
        mapConfig.put(Config.JOB_SHORTCUT, job);
        mapConfig.put(Config.DATE_SHORTCUT, date);
        mapConfig.put(Config.UNTAG_SHORTCUT, untag);
        mapConfig.put(Config.COMBINE_SHORTCUT, merge);
        mapConfig.put(Config.SPLIT_SHORTCUT, seg);
        mapConfig.put(Config.CLICK_SHORTCUT, click);
        mapConfig.put(Config.TEXT_FONT_NAME, textFontName);
        mapConfig.put(Config.TEXT_FONT_SIZE, textFontSize);
        mapConfig.put(Config.TEXT_FONT_STYLE, textFontStyle);
        mapConfig.put(Config.LINE_FONT_SIZE, lineFontSize);
        mapConfig.put(Config.DIRECTORY_PATH, directoryPath);
        mapConfig.put(Config.BROWSE_SHORTCUT, browse);
        mapConfig.put(Config.MODE_SHORTCUT, mode);
        mapConfig.put(Config.UNDO_SHORTCUT, undo);
        mapConfig.put(Config.REDO_SHORTCUT, redo);
        mapConfig.put(Config.SAVE_SHORTCUT, save);
        mapConfig.put(Config.SAVE_AS_SHORTCUT, saveAs);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        preferencesDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        directoryTextField = new javax.swing.JTextField();
        browseDialogButton = new javax.swing.JButton();
        applyDialogButton = new javax.swing.JButton();
        cancelDialogButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        fontCbSetting = new javax.swing.JComboBox();
        styleCbSetting = new javax.swing.JComboBox();
        sizeCbSetting = new javax.swing.JComboBox();
        previewText = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        mainTab = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        preferenceItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        preferencesDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        preferencesDialog.setTitle("Preferences");
        preferencesDialog.setMinimumSize(new java.awt.Dimension(570, 350));
        preferencesDialog.setModal(true);
        preferencesDialog.setName("preferencesDialog"); // NOI18N

        jLabel1.setText("Default directory for open/save:");
        jLabel1.setName("jLabel1"); // NOI18N

        directoryTextField.setEditable(false);
        directoryTextField.setName("directoryTextField"); // NOI18N
        directoryTextField.setText(mapConfig.get(Config.DIRECTORY_PATH));

        browseDialogButton.setText("Browse");
        browseDialogButton.setName("browseDialogButton"); // NOI18N
        browseDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseDialogButtonActionPerformed(evt);
            }
        });

        applyDialogButton.setText("Apply");
        applyDialogButton.setName("applyDialogButton"); // NOI18N
        applyDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyDialogButtonActionPerformed(evt);
            }
        });

        cancelDialogButton.setText("Cancel");
        cancelDialogButton.setName("cancelDialogButton"); // NOI18N
        cancelDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelDialogButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TextArea Settings", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fontCbSetting.setName("fontCbSetting"); // NOI18N
        fontCbSetting.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fontCbSettingItemStateChanged(evt);
            }
        });
        jPanel1.add(fontCbSetting, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, 190, -1));

        styleCbSetting.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Plain", "Bold", "Italic", "Bold Italic" }));
        styleCbSetting.setName("styleCbSetting"); // NOI18N
        styleCbSetting.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                styleCbSettingItemStateChanged(evt);
            }
        });
        jPanel1.add(styleCbSetting, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, 100, -1));
        String style = proper.getProperty(TEXT_FONT_STYLE, "plain");
        int styleIndex = Font.PLAIN;
        if (style.equalsIgnoreCase("bold")) {
            styleIndex = 1;
        } else if (style.equalsIgnoreCase("italic")) {
            styleIndex = 2;
        } else if (style.equalsIgnoreCase("bold italic")) {
            styleIndex = 3;
        }
        styleCbSetting.setSelectedIndex(styleIndex);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 10; i < 30; i = i + 2) {
            model.addElement(i);
        }
        sizeCbSetting.setModel(model);
        sizeCbSetting.setName("sizeCbSetting"); // NOI18N
        sizeCbSetting.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sizeCbSettingItemStateChanged(evt);
            }
        });
        jPanel1.add(sizeCbSetting, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 40, -1, -1));
        sizeCbSetting.setSelectedItem(Integer.parseInt(proper.getProperty(TEXT_FONT_SIZE, "14")));

        previewText.setText("Văn bản thử nghiệm bằng tiếng Việt");
        previewText.setName("previewText"); // NOI18N
        jPanel1.add(previewText, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, -1, -1));

        jLabel3.setText("Preview:");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        jLabel4.setText("Font:");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jLabel5.setText("Font style:");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 40, -1, -1));

        jLabel6.setText("Size:");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, -1, -1));

        javax.swing.GroupLayout preferencesDialogLayout = new javax.swing.GroupLayout(preferencesDialog.getContentPane());
        preferencesDialog.getContentPane().setLayout(preferencesDialogLayout);
        preferencesDialogLayout.setHorizontalGroup(
            preferencesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferencesDialogLayout.createSequentialGroup()
                .addGroup(preferencesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preferencesDialogLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(directoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(browseDialogButton))
                    .addGroup(preferencesDialogLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, preferencesDialogLayout.createSequentialGroup()
                .addContainerGap(173, Short.MAX_VALUE)
                .addComponent(applyDialogButton)
                .addGap(82, 82, 82)
                .addComponent(cancelDialogButton)
                .addGap(171, 171, 171))
        );
        preferencesDialogLayout.setVerticalGroup(
            preferencesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferencesDialogLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(preferencesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preferencesDialogLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel1))
                    .addGroup(preferencesDialogLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(directoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(browseDialogButton))
                .addGap(38, 38, 38)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(preferencesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyDialogButton)
                    .addComponent(cancelDialogButton))
                .addGap(22, 22, 22))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Main View");
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainTab.setName("mainTab"); // NOI18N

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        fileMenu.setText("File");
        fileMenu.setName("fileMenu"); // NOI18N

        preferenceItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        preferenceItem.setText("Preferences");
        preferenceItem.setName("preferenceItem"); // NOI18N
        preferenceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferenceItemActionPerformed(evt);
            }
        });
        fileMenu.add(preferenceItem);

        exitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        exitItem.setText("Exit");
        exitItem.setName("exitItem"); // NOI18N
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");
        editMenu.setName("editMenu"); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Shortcut");
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        editMenu.add(jMenuItem1);

        jMenuBar1.add(editMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTab, javax.swing.GroupLayout.PREFERRED_SIZE, 965, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(651, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void browseDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseDialogButtonActionPerformed
        // TODO add your handling code here:--
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            directoryTextField.setText(file.getPath());
        }
}//GEN-LAST:event_browseDialogButtonActionPerformed

    private void applyDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyDialogButtonActionPerformed
        mapConfig.put(Config.DIRECTORY_PATH, directoryTextField.getText());
        String fontName = (String) fontCbSetting.getSelectedItem();
        String styleString = (String) styleCbSetting.getSelectedItem();
        Integer size = (Integer) sizeCbSetting.getSelectedItem();
        mapConfig.put(Config.TEXT_FONT_NAME, fontName);
        mapConfig.put(Config.TEXT_FONT_STYLE, styleString);
        mapConfig.put(Config.TEXT_FONT_SIZE, size.toString());

        tagPanel.setFontArea(new Font(fontName, string2Int(styleString), size));
        storeConfig(mapConfig);
        preferencesDialog.dispose();
}//GEN-LAST:event_applyDialogButtonActionPerformed

    private void cancelDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelDialogButtonActionPerformed
        // TODO add your handling code here:
        preferencesDialog.dispose();
}//GEN-LAST:event_cancelDialogButtonActionPerformed

    private void preferenceItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferenceItemActionPerformed
        // TODO add your handling code here:
        preferencesDialog.pack();
        preferencesDialog.setLocationRelativeTo(null);
        directoryTextField.setText(mapConfig.get(Config.DIRECTORY_PATH));
        preferencesDialog.setVisible(true);
    }//GEN-LAST:event_preferenceItemActionPerformed

    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItemActionPerformed
        storeConfig(mapConfig);
        System.exit(0);
    }//GEN-LAST:event_exitItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        new ShortcutKeyDialog(this, false, mapConfig, tagPanel).setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void fontCbSettingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fontCbSettingItemStateChanged
        // TODO add your handling code here:
        setFontPreview();
    }//GEN-LAST:event_fontCbSettingItemStateChanged

    private void styleCbSettingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_styleCbSettingItemStateChanged
        // TODO add your handling code here:
        setFontPreview();
    }//GEN-LAST:event_styleCbSettingItemStateChanged

    private void sizeCbSettingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sizeCbSettingItemStateChanged
        // TODO add your handling code here:
        setFontPreview();
    }//GEN-LAST:event_sizeCbSettingItemStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        storeConfig(mapConfig);
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void setFontPreview() {
        String fontName = (String) fontCbSetting.getSelectedItem();
        String styleString = (String) styleCbSetting.getSelectedItem();
        int size = (Integer) sizeCbSetting.getSelectedItem();
        previewText.setFont(new Font(fontName, string2Int(styleString), size));
    }// end setFontPreview method

    /**
     * Chuyen doi String cua style thanh kieu int
     * @param style Bold, Plain...
     * @return 
     */
    public static int string2Int(String style) {
        if (style.equalsIgnoreCase("bold")) {
            return Font.BOLD;
        } else if (style.equalsIgnoreCase("italic")) {
            return Font.ITALIC;
        } else if (style.equalsIgnoreCase("bold italic")) {
            return Font.BOLD + Font.ITALIC;
        } else {
            return Font.PLAIN;
        }
    }// end string2Int method

    /**
     * Luu cac gia tri config vao file config.
     * Goi tu bat cu lop nao
     * @param mapConfig 
     */
    public static void storeConfig(HashMap<String, String> mapConfig) {
        try {
            if (Config.checkChanged(mapConfig, proper)) {
                proper.store(new PrintWriter("config"), "Config is changed");
            }
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end storeConfig method

    /**
     * Luu cac gia tri config vao file config.
     * Goi tu bat cu lop nao
     */
    public static void storeConfig() {
        try {
            proper.store(new PrintWriter("config"), "Save config");
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end storeConfig method

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyDialogButton;
    private javax.swing.JButton browseDialogButton;
    private javax.swing.JButton cancelDialogButton;
    private javax.swing.JTextField directoryTextField;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JComboBox fontCbSetting;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane mainTab;
    private javax.swing.JMenuItem preferenceItem;
    private javax.swing.JDialog preferencesDialog;
    private javax.swing.JLabel previewText;
    private javax.swing.JComboBox sizeCbSetting;
    private javax.swing.JComboBox styleCbSetting;
    // End of variables declaration//GEN-END:variables
    private static Properties proper;
    private TaggingPanel tagPanel;
    private String[] allFontsName;
    private HashMap<String, String> mapConfig;
    private SwingWorker<String[], Void> loadFont = new SwingWorker<String[], Void>() {

        @Override
        protected String[] doInBackground() throws Exception {
            GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts = e.getAvailableFontFamilyNames();
            return fonts;
        }

        @Override
        protected void done() {
            try {
                allFontsName = get();
                fontCbSetting.setModel(new DefaultComboBoxModel(allFontsName));
                fontCbSetting.setSelectedItem(proper.getProperty(TEXT_FONT_NAME, "Times New Roman"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Can't load fonts in system",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    };
}
