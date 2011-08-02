/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Dung cho viec gan shortcut key cho action
 * @author banhbaochay
 */
public class Bind {

    /**
     * Constructor khoi tao cac InputMap va ActionMap
     * @param component component muon gan shortcut key, neu la frame thi nen dung
     * getContentPane ve dang JPanel
     */
    public Bind(JComponent component) {
        inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = component.getActionMap();
    }
    
    /**
     * Gan 1 action voi 1 shortcut key
     * @param action
     * @param keyStroke 
     */
    public void keyBind(javax.swing.Action action, KeyStroke keyStroke) {
        String actionName = (String) action.getValue(javax.swing.Action.NAME);
        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, action);
    }
    
    /**
     * Bo tat ca shortcut key cua 1 action
     * @param action 
     */
    public void removeKey(javax.swing.Action action) {
        List<KeyStroke> keyList = keys(action);
        String actionName = (String) action.getValue(javax.swing.Action.NAME);
        for (KeyStroke keyStroke : keyList) {
            inputMap.remove(keyStroke);
            actionMap.remove(actionName);
        }// end foreach keyStroke
    }// end removeKey method
    
    /**
     * Dua ra tat ca cac shortcut ung voi action
     * @param action
     * @return 
     */
    public List<KeyStroke> keys(javax.swing.Action action) {
        List<KeyStroke> keyList = new ArrayList<KeyStroke>();
        String actionName = (String) action.getValue(javax.swing.Action.NAME);
        for (KeyStroke keyStroke : inputMap.keys()) {
            String name = (String) inputMap.get(keyStroke);
            if (actionName.equals(name)) {
                /*
                 * Ten map voi keyStroke trung voi ten cua action
                 */
                keyList.add(keyStroke);
            }// end if actionName.equals(name)
        }// end foreach keyStroke
        return keyList;
    }// end keys method
    
    /**
     * Chuyen KeyStroke sang dang String thong thuong. VD: ctrl alt pressed K --> ctrl alt + K
     * @param keyStroke
     * @return 
     */
    public static String KeyStroke2String(KeyStroke keyStroke) {
        String ret = keyStroke.toString();
        return ret.replace("pressed", "+");
    }// end KeyStroke2String method
    
    /**
     * Chuyen 1 String ve dang KeyStroke.
     * @param str ctrl alt + K hoac shift + S
     * @return 
     */
    public static KeyStroke String2KeyStroke(String str) {
        return KeyStroke.getKeyStroke(str.replace("+", "pressed"));
    }// end String2KeyStroke method
    
    private ActionMap actionMap;
    private InputMap inputMap;
    
    public static void main(String[] args) {
        KeyStroke k = String2KeyStroke("ctrl alt shift + 4");
        System.out.println(k.toString());
    }// end main class
    
}// end Bind class

