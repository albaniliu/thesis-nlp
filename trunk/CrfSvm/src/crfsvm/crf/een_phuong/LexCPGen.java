
package crfsvm.crf.een_phuong;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;


public class LexCPGen {
	protected static HashMap<String, HashSet<String>> lexicons = new HashMap<String, HashSet<String>>();
	
	public static void init(String lexiconStorage)
	{
		System.out.println("Initializing lexicons");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(lexiconStorage + File.separator + Option.lexiconManifest));
                     //   BufferedReader reader = new BufferedReader(new FileReader("d:\\list.manife));
			String line;
			while ((line = reader.readLine()) != null){
				if (line.equals("")) continue;
				if (lexicons.containsKey(line)) continue;
				
				loadLexicon(line, lexiconStorage);
			}

                        
			reader.close();
		}
		catch (Exception e){
			System.err.println("Error while reading manifest in lexiconStorage dir");
                        System.out.println(e.getMessage());
			return;
		}
	}

	
//	Methods
    public static String doCnxtPreGen(IOB2Sequence os, int curPos, ContextInfo cpInfo)
    {          
//		generate context predicates
        String cp = "";
        String suffix = "";
        String word = "";
        
        for (int i = 0; i < cpInfo.size(); ++ i){
        	CPPair p = cpInfo.getPosInfoAt(i);
        	
            int pos = curPos + p.pos;
            if (pos < 0 || pos >= os.length())
                return cp;
            
            suffix += p.col + "-" + p.pos + ":";
            word += os.getToken(p.col, pos) + " ";
        }
        
        word = word.trim();
        suffix = suffix.substring(0, suffix.length() - 1);
        
        StringTokenizer tk = new StringTokenizer(cpInfo.getName(), "_");
        String lookupType;
        
        if (!tk.hasMoreTokens()) return "";
        else lookupType = tk.nextToken();
        
        if (!tk.hasMoreTokens()) return "";
        
    	String lexiconName = tk.nextToken();    	
    	if (!lexicons.containsKey(lexiconName)) return "";
    	
    	if (lookupType.equalsIgnoreCase("in")){
    		if (isAnEntryOf(lexiconName, word))
    			cp = "lex:" + cpInfo.getName() + ":" + suffix;
    	}
    	else if (lookupType.equalsIgnoreCase("has")){
    		if (!isAllSyllableInitCaped(word))
    			return "";
    		if (containAnEntryOf(lexiconName, word))
    			cp = "lex:" + cpInfo.getName() + ":" + suffix;
    	}
    	
        if (cpInfo.bSecondMarkovOrder && !cp.equals("")) cp = "#" + cp;
        return cp;
    }
	
	private static boolean loadLexicon(String name, String path){
		try {			
			path = path + File.separator +  name + ".txt";
			System.out.print("Loading " + path + "\t: ");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(path), "UTF-8"));
			
			HashSet<String> list; 
			if (lexicons.containsKey(name)){
				list = lexicons.get(name);
			}
			else {
				list = new HashSet<String>();
				lexicons.put(name,list);
			}
			
			String line;
			while ((line = in.readLine()) != null){				
				list.add(line.trim().toLowerCase());
			}
			
			System.out.println(list.size() + " entires loaded");							
			in.close();
			return true;		
		}
		catch (IOException ioe){
			System.err.println("Error while loading " + name);
			return false;
		}
	}
	
	private static boolean isAnEntryOf(String name, String word){
		if (!lexicons.containsKey(name)){
			return false;
		}
		else {
			return lexicons.get(name).contains(word.toLowerCase());
		}
	}
	
	private static boolean containAnEntryOf(String name, String word){
		
		if (!lexicons.containsKey(name))
			return false;

		if (isAnEntryOf(name, word))
			return true;
		
		HashSet<String> list= lexicons.get(name);		
		
		StringTokenizer tk = new StringTokenizer(word, " \t");
		String [] tkArray = new String [tk.countTokens()];
		
		int i = 0;
		while (tk.hasMoreTokens())
			tkArray[i++] = tk.nextToken();
		
		for (int count = 1; count < tkArray.length; ++count){
			String tkConj = "";
			
			for (int j = 0; j < tkArray.length - count; ++j){
				tkConj +=  tkArray[j] + " ";
			}
			
			if (list.contains(tkConj.trim().toLowerCase()))
				return true;
		}
		return false;
	}	
	
	private static boolean isAllSyllableInitCaped(String word){
		StringTokenizer tk = new StringTokenizer(word, " ");
		
		while (tk.hasMoreTokens()){
			String syllable = tk.nextToken();
			if (Character.isLetter(syllable.charAt(0)) && !Character.isUpperCase(syllable.charAt(0)))
				return false; 
		}
		return true;
	}
}
