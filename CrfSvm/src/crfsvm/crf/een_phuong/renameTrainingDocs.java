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
public class renameTrainingDocs
{
    public static void rename(String source, String destination)
    {
        String result = "";

        File sour = new File(source);
        File des = new File(destination);
        if (!sour.isDirectory() || !des.isDirectory())
            return;
        try
        {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(destination + File.separator + "MAPFILE.txt"), "UTF-8"));

            String[] dirs = sour.list(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return (name.endsWith(".txt"));
                }
            });

            //System.out.println("dirs = " + dirs.length);
            int count = 0;
            for (int i = 0; i < dirs.length; i++)
            {
                File tempi = new File(sour.getAbsolutePath() + File.separator + dirs[i]);
                if (!tempi.isFile()) continue;
                count++;
                String nameDes = des.getCanonicalPath() + File.separator + "train-" + count + ".txt";
                copyfile(tempi.getCanonicalPath(), nameDes);
                result += count + ":" + dirs[i].substring(0, dirs[i].length() - 4) + "\n";
            }

            out.write(result);
            out.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static void copyfile(String srFile, String dtFile) throws FileNotFoundException, IOException {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            //System.out.println("File copied:" + srFile);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        String source = "D:\\Document\\Informatics\\IE\\From Thani\\Data\\TextForm";
        String des = "D:\\Document\\Informatics\\IE\\From Thani\\Data\\MapTextForm";
        rename(source, des);
    }
}
