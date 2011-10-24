/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.jdesktop.beansbinding.Converter;

/**
 * Convert tu so luong row trong table ra kieu boolean
 * @author banhbaochay
 */
public class IntBooleanConverter extends Converter<Integer, Boolean> {

    @Override
    public Boolean convertForward(Integer value) {
        System.out.println("IntBooleanCOnverter");
        System.out.println(value);
        int v = value;
        if (v == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Integer convertReverse(Boolean value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}// end IntBooleanConverter class

