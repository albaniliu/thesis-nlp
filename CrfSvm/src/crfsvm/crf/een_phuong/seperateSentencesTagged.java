/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;
import java.io.*;
import java.util.StringTokenizer;

/**
 *
 * @author Thien
 */
public class seperateSentencesTagged
{
    public String seperateSentencesInFile(String source, String Destination)
    {
        String[] dots = {".", "?", "!", "..."};
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Destination), "UTF-8"));
            String line = "", result = "", sentence = "",  prePart = "", subLine = "";
            while ((line = in.readLine()) != null)
            {
                if (line.trim().length() == 0) continue;
                int pos = 0;
                int[] find;
                do
                {
                    find = findPattern(dots, line, pos);
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

            out.write(result);

            in.close();
            out.close();

            return Destination;
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
            return "";
        }
    }

    public int[] findPattern(String[] dots, String str, int pos)
    {
        int[] result = {str.length() + 2, dots.length + 2};
        for (int i = 0; i < dots.length; i++)
        {
            int temp = str.indexOf(" " + dots[i] + " ", pos);
            if (temp < 0) continue;
            if (temp < result[0])
            {
                result[0] = temp;
                result[1] = i;
            }
        }
        return result;
    }

    public static void main(String[] args)
    {
        seperateSentencesTagged se = new seperateSentencesTagged();
        String source = "C:\\labs\\testSeperate1.txt";
        String des = "C:\\labs\\resultSeperate1.txt";
        se.seperateSentencesInFile(source, des);
        System.out.println("done!");
    }
}
