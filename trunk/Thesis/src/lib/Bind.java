/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author banhbaochay
 */
class Bind {

    public Bind(JComponent component) {
        inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = component.getActionMap();
    }
    
    public void keyBind(javax.swing.Action action, KeyStroke keyStroke) {
        String actionName = (String) action.getValue(javax.swing.Action.NAME);
        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, action);
    }
    
    public void removeKey(javax.swing.Action action) {
        List<KeyStroke> keyList = keys(action);
        String actionName = (String) action.getValue(javax.swing.Action.NAME);
        for (KeyStroke keyStroke : keyList) {
            inputMap.remove(keyStroke);
            actionMap.remove(actionName);
        }// end foreach keyStroke
    }// end removeKey method
    
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
    
    private ActionMap actionMap;
    private InputMap inputMap;
    
    public static void main(String[] args) {
        KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_1,KeyEvent.CTRL_DOWN_MASK + KeyEvent.ALT_DOWN_MASK);
        System.out.println(k);
        System.out.println(KeyEvent.getKeyText(KeyEvent.VK_BACK_SLASH));
    }// end main class
}// end Bind class

