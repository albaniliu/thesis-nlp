/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.io.*;

/**
 *
 * @author Thien
 */
public class eliminateTags
{
    private static String[] tags = {"per", "loc", "org", "time", "num", "cur", "misc", "pct"};
    private static String[] punctBefore = {".", ",", "!", "?", ":", ")", "}", "]"};
    private static String[] punctAfter = {"(", "{", "["};

    public static void main(String[] args)
    {
        if (args.length != 2) {
            System.out.println("Usage: eliminateTags [input file] [output file]");
            return;
        }
        try
        {
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
            BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));
            String line = "";
            while ((line = bufReader.readLine()) != null)
            {
                if (line.trim().length() == 0) continue;
                for (int i = 0;i < tags.length; i++)
                {
                    line = line.replace("<" + tags[i] + "> ", "");
                    line = line.replace(" </" + tags[i] + ">", "");
                }

                line = line.replace("[", "");
                line = line.replace("]", "");

                for (int i = 0; i < punctBefore.length; i++)
                    line = line.replace(" " + punctBefore[i], punctBefore[i]);

                for (int i = 0; i < punctAfter.length; i++)
                    line = line.replace(punctAfter[i] + " ", punctAfter[i]);
                
                bufWriter.write(line);
                bufWriter.write("\n\n");
            }
            bufReader.close();
            bufWriter.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
