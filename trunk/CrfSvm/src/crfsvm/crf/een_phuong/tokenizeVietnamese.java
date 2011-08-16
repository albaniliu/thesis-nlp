/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

/**
 *
 * @author Thien
 */
public class tokenizeVietnamese
{
    public static void token() throws FileNotFoundException, UnsupportedEncodingException, IOException {
//        Runtime rt = Runtime.getRuntime();
//        Process process = null;
//        String dir = "." + File.separator + "vnTagger" + File.separator + "vnTagger.bat";
//        try {
//            process = rt.exec(dir + " -i" + " input.txt" + " -o" + " outputTok.txt" + " -u" + " -p");
//            process.waitFor();
//        } catch (IOException ex) {
//            Logger.getLogger(vnTokenizer.class.getName()).log(Level.SEVERE, null, ex);
//
//        } catch (InterruptedException itex) {
//            itex.toString();
//        }
        //Dung
        VietnameseMaxentTagger maxen = new VietnameseMaxentTagger();
        TaggerOptions.PLAIN_TEXT_FORMAT = true;
        TaggerOptions.UNDERSCORE = true;
        maxen.tagFile("tmp/input.txt", "tmp/outputTok.txt");

//        String input = "." + File.separator + "vnTagger" + File.separator + "outputTok.txt";
        String input = "tmp/outputTok.txt";

        String ret = modifyvnTagger(input, false);
//        System.out.println("---------------------------------modifyvnTagger\n\n" + ret);
        String retu = seperateSentencesInString(ret);
//        System.out.println("---------------------------------seperateSentence\n\n" + retu);

//        String output = "." + File.separator + "input.txt";
        String output = "tmp/tagged.txt";
        BufferedWriter f = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output), "UTF-8"));

        f.write(retu);
        f.close();
    }

    public static String modifyvnTagger(String fileSource, boolean delete)
    {
        String[] punctuations = {".", "," , "!", "(", ")", "[", "]", "{", "}", "$", "?", "@", "\"", "-", "/", "...", ":", "'", ";", "*", "+" , "#",
        "%", "^", "&", "=", "|", "~", "`"};

        try
        {
            String line = "";
            String ret = "";

            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileSource), "UTF-8"));

            while ((line = in.readLine()) != null) {
                StringTokenizer lineTknr = new StringTokenizer(line, " ");

                while (lineTknr.hasMoreTokens())
                {
                    line = lineTknr.nextToken();

                    int i = line.lastIndexOf("/");
                    line = line.substring(0, i);

                    if (line.indexOf("_") != 0)
                    {
                        line = line.replaceAll("_", " ");
                    }
                    line = "[" + line + "]";

                    char c = line.charAt(1);
                    int ascii = (int) c;

                    if (ascii != 65279)
                    {
                        String toAdd = checkPunctuation(punctuations, line);
                        if (toAdd.length() != 0)
                        {
                            ret += toAdd + " ";
                        }
                        else
                        {
                            ret += line + " ";
                        }
                    }
                }
                if (ret.length() != 1)
                {
                    ret += "\n";
                }
            }

            in.close();
            File toDelete = new File(fileSource);
            if (delete)
                toDelete.delete();

            return ret;
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public static String checkPunctuation(String[] punctuations, String line)
    {
        String result = "";
        for (int i = 0; i < punctuations.length; i++)
            if (("[" + punctuations[i] + "]").equals(line))
            {
                result = punctuations[i];
                break;
            }
        return result;
    }

    public static String seperateSentencesInString(String str)
    {
        String[] dots = {".", "?", "!", "..."};
        String line = "", result = "", sentence = "",  prePart = "", subLine = "";
        //int count = 0;
        int[] find = {0, 0};
        try
        {
            StringTokenizer strtok = new StringTokenizer(str, "\n");
            while (strtok.hasMoreTokens())
            {
                line = strtok.nextToken();
                if (line.trim().length() == 0) continue;
                //count++;
                int pos = 0;
                do
                {
                    //try{
                    find = findPattern(dots, line, pos);
                    //}
                    //catch (Exception ex)
                    //{
                    //    System.out.println("right here, line = " + line + " pos = " + pos);
                    //}
                    //if (count == 22) System.out.println("find: " + find[0] + " : " + find[1] + " : pre = " + prePart);

                    if (find[1] > dots.length)
                    {
                        prePart += line.substring(pos).trim();
                        break;
                    }
                    subLine = line.substring(pos, find[0] + dots[find[1]].length() + 2);
                    if (prePart.length() != 0)
                    {
                        sentence = prePart + " " + subLine;
                        prePart = "";
                    }
                    else
                        sentence = subLine;
                    result += sentence + "\n\n";
                    pos = find[0] + dots[find[1]].length() + 2;
                    
                }
                while(true);
            }

            result = result.trim();

            return result;
        }
        catch (Exception ex)
        {
            //System.out.println("Error: " + ex + " at line " + count);
            
            return "";
        }
    }

    public static int[] findPattern(String[] dots, String str, int pos)
    {
        String[] others = {"]", ")", "}", "'", "\""};
        String[] completeOthers = {"[", "(", "{", "'", "\""};
        String match = "";
        int con = dots.length + 2;
        boolean state = false;
        int[] first = {str.length() + 2, con};
        //System.out.println("reach the start!");
        for (int i = 0; i < dots.length; i++)
        {
            int temp = str.indexOf(" " + dots[i] + " ", pos);
            if (temp < 0) continue;
            if (temp < first[0])
            {
                first[0] = temp;
                first[1] = i;
            }

        }
        if (first[1] == con) return first;
        //return first;

        if ((first[0] + dots[first[1]].length() + 2) == str.length()) return first;

        match = str.substring(first[0] + dots[first[1]].length() + 2, first[0] + dots[first[1]].length() + 3);
        //System.out.println("reach the first!");

        for (int i = 0; i < others.length; i++)
            if (match.equals(others[i]))
            {
                if ((countAppearance(completeOthers[i], str, pos, first[0])%2) == 1)
                {
                    try{
                    first = findPattern(dots, str, first[0] + dots[first[1]].length() + 3);
                    //System.out.println("Odd");
                    }
                    catch (Exception et)
                    {
                        System.out.println("bub bub : " + first[0] + " : " + first[1]);
                    }
                }
                //System.out.println(countAppearance(completeOthers[i], str, pos, first[0]));
                //System.out.println(match);
                state = true;
                break;
            }
        if (state) 
        {
            //System.out.println("this way: " + first[0] + " : " + first[1]);
            return first;
        }
        //System.out.println("as you guess" + match);
       // System.out.println("reach the second!");
        state = false;

        for (int i = 0; i < dots.length; i++)
            if (match.equals(dots[i]))
            {
                first[0] += dots[first[1]].length() + 1;
                first[1] = i;
                state = true;
                break;
            }

        if (state) return first;

        //System.out.println("reach the third!");

        char ch = str.charAt(first[0] + dots[first[1]].length() + 3);

       if (Character.isDigit(ch) || Character.isUpperCase(ch))
           return first;

        //System.out.println("reach the end!");

        return findPattern(dots, str, first[0] + dots[first[1]].length() + 2);
    }



    public static int countAppearance(String pattern,  String str, int first, int end)
    {
        if (first < 0) first = 0;
        String sub = str.substring(first, end + 1);
        int count = 0, run = 0;

        while (run > -1)
        {
            run = sub.indexOf(pattern, run);
            if (run > -1)
            {
                count++;
                run++;
            }
        }

        return count;
    }

    public static void main(String[] args)
    {
        String[] dots = {".", "?", "!", "..."};
        String line = "[Mới đây] , [trong] [cuốn] [sách] [dày] [hơn] [500] [trang] [nhan đề] [Why Vietnam] ? ( [Tại sao] [Việt Nam] ? ) , [ông] [Archimedes] [L. A.] [Patti] , [một] [người] [Mỹ] [vốn] [là] [đại tá] [tình báo] , [miêu tả] [những] [con người] [và] [sự kiện] [ở] [Hà Nội] [vào] [năm] [1945] , [trong] [đó] [có] [đoạn] :";
        try{
        int[] find = findPattern(dots, line, 0);
        System.out.println("RESULT HERE: " + find[0] + " : And: " + find[1]);
        }
        catch (Exception e)
        {
            System.out.println("heeeee!" + e);
        }
    }


    public static void token(String path) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        String dir = path + File.separator + "vnTagger" + File.separator + "vnTagger.bat";
        try {
            process = rt.exec(dir + " -i" + " input.txt" + " -o" + " outputTok.txt" + " -u" + " -p");
            process.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(vnTokenizer.class.getName()).log(Level.SEVERE, null, ex);

        } catch (InterruptedException itex) {
            itex.toString();
        }

        String input = path + File.separator + "vnTagger" + File.separator + "outputTok.txt";

        String ret = modifyvnTagger(input, false);
        //System.out.println("---------------------------------modifyvnTagger\n\n" + ret);
        String retu = seperateSentencesInString(ret);
        //System.out.println("---------------------------------seperateSentence\n\n" + retu);

        String output = path + File.separator + "input.txt";
        BufferedWriter f = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output), "UTF-8"));

        f.write(retu);
        f.close();
    }
}

