/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thesis;

import java.security.NoSuchAlgorithmException;

import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lib.ConvertText;
import lib.Word;
//>>>>>>> .r40

/**
 *
 * @author banhbaochay
 */
public class Test {

    public static void main(String[] args) throws NoSuchAlgorithmException {
//<<<<<<< .mine
        String input = "";

        TaggerOptions.PLAIN_TEXT_FORMAT = true;
        TaggerOptions.UNDERSCORE = true;
        String result = VietnameseMaxentTagger.getTokenizer().segment(input);
        System.out.println(result);
//=======
        String input1 = "ÁBnh12";
        Pattern p = Pattern.compile("[A-Z]");
        Matcher m = p.matcher(input);
        System.out.println(m.find());

    }
}
