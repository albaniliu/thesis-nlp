/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package feature;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.ReadWriteFile;
import util.Word;

/**
 * Provide functions to check a string is in dictionary or not
 * An instance of this class will be created once time when the program load first
 * @author banhbaochay
 */
public class Dictionary {

    /**
     * Path separator in system OS. EX: windows is \, linux is /
     */
    public static final String PATH_SEPARATOR = System.getProperty("file.separator");
    /**
     * Default path to dictionary directory: outerlib/Dictionary in project
     */
    public static final String DEFAULT_PATH = "outerLib" + PATH_SEPARATOR + "Dictionary";
    /**
     * Name of manifest file. This file contains all dictionaries filename wants to load
     */
    public static final String MANIFEST_FILENAME = "list.manifest";

    /**
     * Default constructor with default dict directory's path
     * All dictionaries are loaded
     */
    public Dictionary() {
        this(DEFAULT_PATH);
    }

    /**
     * Constructor with dict directory's path is set by argument
     * All dictionaries are loaded
     * @param dicDirectoryPath
     */
    public Dictionary(String dicDirectoryPath) {
        this.dicDirectoryPath = dicDirectoryPath;
        this.manifestPath = this.dicDirectoryPath + PATH_SEPARATOR + MANIFEST_FILENAME;
        this.dicMap = new HashMap<String, List<String>>();
        loadNameDictionaries();
        loadDictionaries();
    }

    /**
     * Get dictionary type of string
     * @param string string wants to check. Note: word separator is space, not _
     * @return Dictionary type of string
     */
    public String getDictionaryType(String string) {
        if (!dicMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : dicMap.entrySet()) {
                string = string.trim().toLowerCase();
                if (entry.getValue().contains(string)) {
                    return entry.getKey().toUpperCase();
                }
            }
            return "NON";
        } else {
            return "No dictionary storage";
        }
    }

    /**
     * Load all filename of dictionaries which is storage in list.manifest
     */
    private void loadNameDictionaries() {
        this.dicNameList = new ArrayList<String>();
        String charset = "UTF-8";
        BufferedReader in = null;
        try {
            in = ReadWriteFile.readFile(manifestPath, charset);
            String line = null;
            while ((line = in.readLine()) != null) {
                if (!line.equals("")) {
                    getDicList().add(line.trim());
                }
            }
            in.close();
            System.out.println("Load all name of file dictionary");
        } catch (UnsupportedEncodingException e) {
            System.err.println(charset + " is not supported");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load dictionary to dicMap object
     */
    private void loadDictionaries() {
        for (String dicName : this.dicNameList) {
            /* Read a dictionary file */
            BufferedReader in = null;
            String charset = "UTF-8";
            try {
                in = ReadWriteFile.readFile(this.dicDirectoryPath + PATH_SEPARATOR + dicName + ".txt" , charset);
                String line = null;
                List<String> elements = new ArrayList<String>();
                while ((line = in.readLine()) != null) {
                    /* Add elements to list */
                    elements.add(line.trim().toLowerCase());
                }
                dicMap.put(dicName, elements);
                in.close();
                System.out.println(dicName + " dictionary is loaded successfull");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
        Word w = new Word("Dũng");
        String s = "công ty";
        System.out.println(dict.getDictionaryType(s));
//        System.out.println(System.getProperty("file.separator"));
    }

    /*
     * Path of directory which contains manifest file and all dictionaries file
     */
    private String dicDirectoryPath;

    /*
     * Path of manifest file, it lists dicitonaries's name by line
     */
    private String manifestPath;

    /*
     * Store all name of dictionary
     */
    private List<String> dicNameList;

    /*
     * Store all dictionaries with key is dictionary's name and value is list of word in the dictionary
     */
    private Map<String, List<String>> dicMap;


    /* Getter and Setter */
    /**
     * @return Path of dictionary directory
     */
    public String getDicDirectoryPath() {
        return dicDirectoryPath;
    }

    /**
     * @param dicDirectoryPath Path of dictionary directory to load
     */
    public void setDicDirectoryPath(String dicDirectoryPath) {
        this.dicDirectoryPath = dicDirectoryPath;
    }

    /**
     * @return All name of dictionaries file
     */
    public List<String> getDicList() {
        return dicNameList;
    }

    @Override
    public String toString() {
        return dicMap.toString();
    }
}
