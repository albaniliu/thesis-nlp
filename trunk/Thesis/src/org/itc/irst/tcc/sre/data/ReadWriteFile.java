/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.itc.irst.tcc.sre.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author banhbaochay
 */
public class ReadWriteFile {

    /**
     * Create a BufferedReader object from path of file.
     * <em>Notes:</em> close this object after reading file
     * @param filePath Path of file wants to read
     * @return BufferedReader type to prepare reading
     * @throws FileNotFoundException if filePath is invalid
     */
    public static BufferedReader readFile(String filePath) throws FileNotFoundException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        return in;
    }

    /**
     * Create a BufferedReader object from file.
     * <em>Notes:</em> close this object after reading file
     * @param inputFile File wants to read
     * @return BufferedReader type to prepare reading
     * @throws FileNotFoundException if file is not found
     */
    public static BufferedReader readFile(File inputFile) throws FileNotFoundException {
        return new BufferedReader(new FileReader(inputFile));
    }

    /**
     * Create a BufferedReader object from path of file and charset.
     * <em>Notes:</em> close this object after reading file
     * @param filePath Path of file wants to read
     * @param charSet string wants to encode file
     * @return BufferedReader type to prepare reading
     * @throws UnsupportedEncodingException if charset is unsupported by java
     * @throws FileNotFoundException if filePath is invalid
     */
    public static BufferedReader readFile(String filePath, String charSet) throws UnsupportedEncodingException, FileNotFoundException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), charSet));
        return in;
    }

    /**
     * Create a BufferedReader object from file with charset encoding.
     * <em>Notes:</em> close this object after reading file
     * @param inputFile File wants to read
     * @param charSet string wants to encode file
     * @return BufferedReader type to prepare reading
     * @throws FileNotFoundException if file is not found
     * @throws UnsupportedEncodingException if charset is unsupported by java
     */
    public static BufferedReader readFile(File inputFile, String charSet) throws FileNotFoundException, UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), charSet));
    }

    /**
     * Create default PrintWriter object from filePath, charSet is set by System OS.
     * <em>Notes:</em> close this object after writing file
     * @param filePath Path of file wants to write
     * @return PrintWriter object to prepare writing
     * @throws FileNotFoundException if filePath is invalid
     */
    public static PrintWriter writeFile(String filePath) throws FileNotFoundException {
        return new PrintWriter(filePath);
    }

    /**
     * Create default PrintWriter object from file, charSet is set by System OS.
     * <em>Notes:</em> close this object after writing file
     * @param outputFile File wants to write
     * @return PrintWriter object to prepare writing
     * @throws FileNotFoundException if file is not found
     */
    public static PrintWriter writeFile(File outputFile) throws FileNotFoundException {
        return new PrintWriter(outputFile);
    }

    /**
     * Create PrintWriter object from filePath with charSet encoding.
     * <em>Notes:</em> close this object after writing file
     * @param filePath Path of file wants to write
     * @param charSet string wants to encode file
     * @return PrintWriter object to prepare writing
     * @throws FileNotFoundException if filePath is invalid
     * @throws UnsupportedEncodingException if charSet is not supported by java
     */
    public static PrintWriter writeFile(String filePath, String charSet) throws FileNotFoundException, UnsupportedEncodingException {
        return new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), charSet));
    }

     /**
     * Create PrintWriter object from file with charSet encoding.
     * <em>Notes:</em> close this object after writing file
     * @param outputFile File wants to write
     * @param charSet string wants to encode file
     * @return PrintWriter object to prepare writing
     * @throws FileNotFoundException if filePath is invalid
     * @throws UnsupportedEncodingException if charSet is not supported by java
     */
    public static PrintWriter writeFile(File outputFile, String charSet) throws FileNotFoundException, UnsupportedEncodingException {
        return new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charSet));
    }

}
