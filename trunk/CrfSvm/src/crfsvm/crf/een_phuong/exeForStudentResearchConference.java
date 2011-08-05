/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.*;

/**
 *
 * @author Thien
 */
public class exeForStudentResearchConference {

    public static void main(String[] args) {

//        System.out.print("You are ok 1 = " + args[0] + " 2 = " + args[1]);
//        try
//        {
//        BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream("C:" + File.separator + "lll.txt"), "UTF-8"));
//        bw.write(args[0]);
//        bw.close();
//        }
//        catch(IOException ex)
//        {
//
//        }
        JVnRecognizer.mainForResearch(args[0], args[1], args[2]);
    }
}
