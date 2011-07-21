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
 * Annotation chi dung cho field, quy dinh field do lien he voi Entity nao
 * @author banhbaochay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EntityAnnotation {
    
    /**
     * Chi ra ten cua entity
     * @return 
     */
    public String entityName();
}
