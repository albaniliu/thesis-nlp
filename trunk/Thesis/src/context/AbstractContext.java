/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package context;

import java.util.List;
import util.Word;

/**
 *
 * @author banhbaochay
 */
public abstract class AbstractContext {

    public abstract List<Word> getWordContext();
}