/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation cho danh dau field co shortcut key
 * @author banhbaochay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ShortcutAnnotation {
    
    /**
     * type 1: Cac field co shortcut key
     * 
     * @return 
     */
    public int type();
    
    /**
     * Luu ten shortcut trong file config: perShortcut, locShortcut...
     * @return 
     */
    public String nameInMap();
    
    /**
     * Gia tri default shortcut cho field
     * @return 
     */
    public String defaultShortcut();
    
    /**
     * Ten action mac dinh
     * @return 
     */
    public String actionName();
    
}// end ShortcutAnnotation class

