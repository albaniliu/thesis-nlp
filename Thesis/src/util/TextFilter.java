/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author banhbaochay
 */
public class TextFilter extends FileFilter{
    private final String ACCEP_EXT = "txt";

        @Override
        public boolean accept(File f) {
            boolean accept = false;
            if (f.isDirectory()) {
                return true;
            }

            String fileName = f.getName();
            if (fileName.toLowerCase().endsWith(ACCEP_EXT)) {
                accept = true;
            }
            return accept;
        }

        @Override
        public String getDescription() {
            return "Text Files";
        }
}
