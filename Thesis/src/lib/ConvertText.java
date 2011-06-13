/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import feature.ENTITY;
import context.DefaultContext;
import feature.Dictionary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

/**
 * Provides methods for convert string
 * @author banhbaochay
 */
public class ConvertText {

    /**
     * Only use segment function of vntagger 4.2.0
     */
    public static final int ONLY_SEGMENT = 0;
    /**
     * Only use tag function of vntagger 4.2.0
     */
    public static final int ONLY_TAG = 1;
    /**
     * Use segment then tag function of vntagger 4.2.0
     */
    public static final int SEGMENT_AND_TAG = 2;

    /**
     * Remove all spaces after start tags and before end tags
     * @param line String as: &lt;loc&gt;  Ha Noi  &lt;/loc&gt;
     * @return String after remove: &lt;loc&gt;Ha Noi&lt;/loc&gt;
     */
    public static String removeSpace(String line) {
        Matcher matcher = Pattern.compile("(<[^/>]*>)\\s*|\\s*(</[^>]*>)").matcher(line);
        return matcher.replaceAll("$1$2");
    }

    /**
     * Remove all square character in document. Ex: [blah] is changed to blah
     * @param line String with square character
     * @return String which is removed square character
     */
    static String removeSquare(String line) {
        return line.replaceAll("\\[|\\]|\\(|\\)", "");
    }

    /**
     * Remove all tags in line
     * @param line String with tags. Ex: per, loc, org, np...
     * @return String has no tag.
     */
    public static String removeTag(String line) {
//        return line.replaceAll("<[^>]*>", "");
        return removeSpace(line).replaceAll("<[^>]*>", "");
    }

    public static Double convertToDouble(Object o) {
        return o.hashCode() / 1000000d;
    }

    /**
     * Convert one line to SVM format as: class [space] feature1:value1 [space] feature2:value2...
     * @param orgLine String has format: [Anh] <per>[Nguyen Van A]</per> [dang] [song]...
     */
    public static String convertToSVMFormat(String orgLine, Dictionary dic) {

        String inputForSentence = convertForSentence(orgLine);
        /* Now, linePrepare has format: Anh <PER>Nam</PER> dang song ... */

        int windowSize = DefaultContext.DEFAULT_WINDOW_SIZE;
        StringBuilder sb = new StringBuilder();
        Sentence sentence = new Sentence(inputForSentence, dic);

        sentence.setContext(windowSize);
        int classtify;
        for (int i = 0; i < sentence.size(); i++) {
            Word word = sentence.wordAt(i);

            List<Word> wordContextList = (List<Word>) word.getContext();
            classtify = (word.isIOB()) ? 1 : -1;
            String IOB = word.getIOB();

            String dicType = word.getDictionaryType();
            String POS = word.getPOS().getName();

            sb.append(classtify);
            sb.append(" ");
            sb.append(1);
            sb.append(":");
            sb.append(IOB.hashCode());
            sb.append(" ");
            sb.append(2);
            sb.append(":");
            sb.append(POS.hashCode());
            sb.append(" ");
            sb.append(3);
            sb.append(":");
            sb.append(dicType.hashCode());

            int count = 4;
            for (Word w : wordContextList) {
                if (w != null) {
                    sb.append(" ");
                    sb.append(count);
                    sb.append(":");
                    sb.append(convertToDouble(w).toString());
                }
                count++;
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Convert all line in File to SVM Format
     * @param file
     * @param dic
     * @return 
     */
    public static String convertToSVMFormat(File file, Dictionary dic) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    sb.append(convertToSVMFormat(line, dic));
                    sb.append("\n\r");
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Process a string by using vntagger 4.2.0
     * @param input String wants to process by vntagger
     * @param mode one of these modes below:
     * <ul>
     *     <li><code>ONLY_SEGMENT:</code> only use segment function of vntagger 4.2.0</li>
     *     <li><code>ONLY_TAG:</code> only use tag function of vntagger 4.2.0</li>
     *     <li><code>SEGMENT_AND_TAG:</code> use segment then tag funtion of vntagger 4.2.0</li>
     * </ul>
     * @return A processed string by vntagger 4.2.0
     */
    public static String vnTagger(String input, int mode) {
        TaggerOptions.PLAIN_TEXT_FORMAT = true;
        TaggerOptions.UNDERSCORE = true;
        switch (mode) {
            case ONLY_SEGMENT:
                return VietnameseMaxentTagger.getTokenizer().segment(input);
            case ONLY_TAG:
                return new VietnameseMaxentTagger().tagText4(input);
            case SEGMENT_AND_TAG:
                return new VietnameseMaxentTagger().tagText(input);
            default:
                throw new IllegalArgumentException(mode + " is not legal for this function. Read doc again please");
        }
    }

    /**
     * Convert string for input to sentence constructor
     * @param input Thien's format: [Anh] &lt;per&gt; [Nguyen Van A] &lt;/per&gt; [dang]
     * [song] [tai] &lt;loc&gt ;[Ha Noi] &lt;/loc&gt;
     * @return A string has format: Anh &lt;PER&gt;Nam&lt;/PER&gt; dang
     * song tai &lt;LOC&gt;Ha Noi&lt;/LOC&gt;
     */
    public static String convertForSentence(String input) {
//        String output = removeSquare(input.trim());
//        return removeSpace(output);
        StringBuffer result = new StringBuffer();
        Pattern pattern = Pattern.compile("\\[[^\\]]*\\]", Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String token = matcher.group();
            token = token.replaceAll("\\s+", "_").replaceAll("\\[|\\]", "");
            matcher.appendReplacement(result, token);
        }
        matcher.appendTail(result);
        return result.toString();
    }

//    public static String
    public static void main(String[] args) {
        String line = "[Ban đầu] [gia đình] [anh] [sống] [ở] [thành phố] <loc> [Ancona] </loc> ";
        System.out.println(convertForSentence(line));
    }
}
