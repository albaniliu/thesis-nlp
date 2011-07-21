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
 *
 * @author anhdung
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MyAnnotation {
    /**
     * Type 1: cac button per, loc, org, pos, job, date
     * Type 0: cac field con lai
     * @return 
     */
    public int type();
    /**
     * Luu ten shortcut cua cac button. Vi du: perShortcut, logShortcut
     * @return 
     */
    public String name();
    /**
     * Gia tri default shortcut cua cac button
     * @return 
     */
    public String defaultShortcut();
}// end MyAnnotation class

