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

/**
 *
 * @author Minh The
 */
public class vnTokenizer {

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        String dir = "." + File.separator + "vnTagger" + File.separator + "vnTagger.bat";
        try {
            process = rt.exec(dir + " -i" + " input.txt" + " -o" + " outputTok.txt" + " -u" + " -p");
            process.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(vnTokenizer.class.getName()).log(Level.SEVERE, null, ex);

        } catch (InterruptedException itex) {
            itex.toString();
        }

        String input = "." + File.separator + "vnTagger" + File.separator + "outputTok.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(input), "UTF-8"));
        String line = "";
        String ret = "";

        while ((line = in.readLine()) != null) {
            StringTokenizer lineTknr = new StringTokenizer(line, " ");

            while (lineTknr.hasMoreTokens()) {
                line = lineTknr.nextToken();

                int i = line.lastIndexOf("/");
                line = line.substring(0, i);

                if (line.indexOf("_") != 0) {
                    line = line.replaceAll("_", " ");
                }
                line = "[" + line + "]";

                char c = line.charAt(1);
                int ascii = (int) c;

                if (ascii != 65279) {
                    if ("[.]".equals(line)) {
                        ret += ".";
                    } else {
                        ret += line + " ";
                    }
                }
            }
            if (ret.length() != 1) {
                ret += "\n" + "\n";
            }
        }

        String output = "." + File.separator + "vnTagger" + File.separator + "outputToken.txt";
        BufferedWriter f = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output), "UTF-8"));

        f.write(ret);
        f.close();
    }

    public static void token() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        String dir = "." + File.separator + "vnTagger" + File.separator + "vnTagger.bat";
        try {
            process = rt.exec(dir + " -i" + " input.txt" + " -o" + " outputTok.txt" + " -u" + " -p");
            process.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(vnTokenizer.class.getName()).log(Level.SEVERE, null, ex);

        } catch (InterruptedException itex) {
            itex.toString();
        }

        String input = "." + File.separator + "vnTagger" + File.separator + "outputTok.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(input), "UTF-8"));
        String line = "";
        String ret = "";

        while ((line = in.readLine()) != null) {
            StringTokenizer lineTknr = new StringTokenizer(line, " ");

            while (lineTknr.hasMoreTokens()) {
                line = lineTknr.nextToken();

                int i = line.lastIndexOf("/");
                line = line.substring(0, i);

                if (line.indexOf("_") != 0) {
                    line = line.replaceAll("_", " ");
                }
                line = "[" + line + "]";

                char c = line.charAt(1);
                int ascii = (int) c;

                if (ascii != 65279) {
                    if ("[.]".equals(line)) {
                        ret += ".";
                    } else {
                        ret += line + " ";
                    }
                }
            }
            if (ret.length() != 1) {
                ret += "\n" + "\n";
            }
        }

        String output = "." + File.separator + "input.txt";
        BufferedWriter f = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output), "UTF-8"));

        f.write(ret);
        f.close();
    }
}
