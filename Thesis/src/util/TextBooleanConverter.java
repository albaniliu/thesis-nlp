/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author banhbaochay
 */
public class TextBooleanConverter extends Converter<String, Boolean> {

    @Override
    public Boolean convertForward(String value) {
        return !value.equals("");
    }

    @Override
    public String convertReverse(Boolean value) {
        return "b";
    }

    
}
