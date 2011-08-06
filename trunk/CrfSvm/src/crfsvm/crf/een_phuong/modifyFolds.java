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
public class modifyFolds
{
    private static String file = "." + File.separator + "data" + File.separator + "foldsmodified";
    private static String folds = file + File.separator + "folds";
    private static String foldseliminated = file + File.separator + "foldseliminated";
    private static String tagged = file + File.separator + "tagged";
    private static String taggedmodifed = file + File.separator + "taggedmodifed";
    private static String[] tags = {"per", "loc", "org", "time", "num", "cur", "misc", "pct"};
    private static String[] punctuations = {".", "," , "!", "(", ")", "[", "]", "{", "}", "?", "@", "\"", "-", "/", "...", ":", "'", ";", "*", "+" , "#",
        "%", "^", "&", "=", "|", "~", "`"};
    public static void eliminateFold(String fileSource, String pathDestination)
    {
        try
        {
            String name = fileSource.substring(fileSource.lastIndexOf("\\") + 1, fileSource.length());
            String fileDestination = pathDestination + File.separator + name;
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileSource), "UTF-8"));
            BufferedWriter bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDestination), "UTF-8"));
            String line = "";
            while ((line = bufReader.readLine()) != null)
            {
                if (line.trim().length() == 0) continue;
                //for (int i = 0;i < tags.length; i++)
                //{
                //    line = line.replace("<" + tags[i] + "> ", "");
                //    line = line.replace(" </" + tags[i] + ">", "");
                //}

                line = line.replace("[", "");
                line = line.replace("]", "");

                bufWriter.write(line);
                bufWriter.write("\n\n");
            }

            bufWriter.close();
            bufReader.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }

    private static void eliminateFolds() //folds -> foldseliminated
    {
        try
        {
            File foldsFile = new File(folds);

            FileFilter filter = new FileFilter() {

                public boolean accept(File pathname) {
                    try
                    {
                        return ((pathname.isFile()) && (pathname.getCanonicalPath().endsWith(".txt")));
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Error: " + ex);
                        return false;
                    }
                }
            };

            File[] children = foldsFile.listFiles(filter);

            for (int i = 0; i < children.length; i++)
                eliminateFold(children[i].getCanonicalPath(), foldseliminated);
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static void vnTaggerFile(String fileSource, String pathDestination)
    {
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        String dir = "." + File.separator + "vnTagger" + File.separator + "vnTagger.bat";
        String name = fileSource.substring(fileSource.lastIndexOf("\\") + 1, fileSource.length());
        String fileDestination = pathDestination + File.separator + name;
        try
        {
            //System.out.println("Start tagging....");
            process = rt.exec(dir + " -i " + fileSource + " -o " + fileDestination + " -u" + " -p");
            //System.out.println("Done");
            process.waitFor();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static void vnTaggerFiles() //foldsmodified -> tagged
    {
        try
        {
            File foldsFile = new File(foldseliminated);

            FileFilter filter = new FileFilter() {

                public boolean accept(File pathname) {
                    try
                    {
                        return ((pathname.isFile()) && (pathname.getCanonicalPath().endsWith(".txt")));
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Error: " + ex);
                        return false;
                    }
                }
            };

            File[] children = foldsFile.listFiles(filter);
            File fi = new File(tagged);

            for (int i = 0; i < children.length; i++)
                vnTaggerFile(children[i].getCanonicalPath(), fi.getCanonicalPath());
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static void modifyvnTagger(String fileSource, String pathDestination)
    {
        //String[] punctuations = {".", "," , "!", "(", ")", "[", "]", "{", "}", "$", "?", "@", "\"", "-", "/", "...", ":"};
        try
        {
            String name = fileSource.substring(fileSource.lastIndexOf("\\") + 1, fileSource.length());
            String fileDestination = pathDestination + File.separator + name;
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileSource), "UTF-8"));
            String line = "";
            String ret = "";

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
                    ret += "\n" + "\n";
                }
            }

            BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDestination), "UTF-8"));

            f.write(ret);
            f.close();
            in.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static void modifyvnTaggers()
    {
        try
        {
            File foldsFile = new File(tagged);

            FileFilter filter = new FileFilter() {

                public boolean accept(File pathname) {
                    try
                    {
                        return ((pathname.isFile()) && (pathname.getCanonicalPath().endsWith(".txt")));
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Error: " + ex);
                        return false;
                    }
                }
            };

            File[] children = foldsFile.listFiles(filter);
            File fi = new File(taggedmodifed);

            for (int i = 0; i < children.length; i++)
                modifyvnTagger(children[i].getCanonicalPath(), fi.getCanonicalPath());
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
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
        if (result.length() != 0) return result;
        for (int i = 0; i < tags.length; i++)
        {
            if (("[<" + tags[i] + ">]").equals(line))
            {
                result = "<" + tags[i] + ">";
                break;
            }
            if (("[</" + tags[i] + ">]").equals(line))
            {
                result = "</" + tags[i] + ">";
                break;
            }
        }
        return result;
    }

    public static void eliminateLabels(String in, String out)
    {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try
        {
             String save = "", line = "",toAppend = "";
             char c = 'a';
             int len = 0;
             br = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
             while ((line = br.readLine()) != null)
             {
                 line = line.trim();
                 System.out.println(line);
                 toAppend = "";
                 c = 'a';
                 len = line.length();
                 if (len != 0)
                 {
                     while (c != ' ')
                     {
                         c = line.charAt(len - 1);
                         len--;
                     }
                     save += line.substring(0, len).trim() + "\n";
                 }
                else
                    save += "\n";
             }

             br.close();

             bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"));
             bw.write(save);
             bw.flush();
             bw.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }


    public static void filterSentence(String in, String out, int num)
    {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try
        {
             String save = "", line = "", toAppend = "";
             int len = 0, count = 0;
             br = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
             while ((line = br.readLine()) != null)
             {
                 line = line.trim();
                 System.out.println(line);
                 len = line.length();
                 if (len != 0)
                 {
                     count++;
                     toAppend += line.trim() + "\n";
                 }
                else
                 {
                    if (count <= num)
                        save += toAppend + "\n";
                    count = 0;
                    toAppend = "";
                 }
             }

             br.close();

             bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"));
             bw.write(save);
             bw.flush();
             bw.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static void extractSentence(String in, String out, int num)
    {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try
        {
             String save = "", line = "", toAppend = "";
             int len = 0, count = 0;
             br = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
             while ((line = br.readLine()) != null)
             {
                 line = line.trim();
                 System.out.println(line);
                 len = line.length();
                 if (len != 0)
                 {
                     //count++;
                     toAppend += line.trim() + "\n";
                 }
                else
                 {
                    count++;
                    if (count <= num)
                    {
                        save += toAppend + "\n";
                        toAppend = "";
                    }
                    else
                        break;
                 }
             }

             br.close();

             bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"));
             bw.write(save);
             bw.flush();
             bw.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
          //  vnTaggerFiles();//test - unlabeled - filtered - given
        String in = "D:\\FlexCRFs\\FlexCRFs\\release\\model\\unlabeled\\toAdd";
        String out = "D:\\FlexCRFs\\FlexCRFs\\release\\model\\unlabeled\\toAdd-unlabeled-";
        for (int i = 2; i <= 12; i++)
        {
          eliminateLabels(in + i + ".txt", out + i + ".txt");
        }
          System.out.println("Done!");
        //eliminateFolds();
    }
}
