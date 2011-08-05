/*
    Copyright (C) 2007 by Cam-Tu Nguyen
 
    Email:	ncamtu@gmail.com
 
    Department of Information System,
    College of Technology
    Hanoi National University, Vietnam
 */

package crfsvm.crf.een_phuong;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class VnLexicon {
	HashMap<String, VnWord> lexicon = null;
	
	public VnLexicon(){
		lexicon = new HashMap<String, VnWord>();
	}
	
	public int size()
	{
		return lexicon.size();
	}
	
	public void readLexicon(String filename){
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "UTF-8"));
			
			String line = "";
			while((line = in.readLine()) != null){
				line = line.trim();
				if (line.equals("")) continue;
				
				StringTokenizer tabTk = new StringTokenizer(line, "\t");
				
				if (!tabTk.hasMoreTokens()) continue;
				
				String wstr = tabTk.nextToken();
				
				VnWord word = new VnWord(wstr);
//				System.out.print(wstr + "\t");
				
				if (tabTk.hasMoreTokens()){
					String posStr = tabTk.nextToken();
					StringTokenizer posTk = new StringTokenizer(posStr, ", ");
					while (posTk.hasMoreTokens()){
						String pos = posTk.nextToken();						
						word.addPOS(pos);
					}
//					System.out.print("\n");
				}
				lexicon.put(wstr,word);
			}
			
			in.close();
		}
		catch (Exception e){
			System.out.println("Reading lexicon fail");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public boolean isNoun(String w){
		w = w.trim().toLowerCase();
		if (contains(w)){
			VnWord word = lexicon.get(w);
			return word.isNoun();
		}
		else return false;
	}
	
	public boolean isVerb(String w){
		w = w.trim().toLowerCase();		
		if (contains(w)){			
			VnWord word = lexicon.get(w);
			return word.isVerb();
		}
		else return false;
	}
	
	public boolean contains(String w){
		w = w.trim().toLowerCase();		
		return lexicon.containsKey(w);
	}
	
	public boolean isProNoun(String w){
		w = w.trim().toLowerCase();		
		if (contains(w)){			
			VnWord word = lexicon.get(w);
			return word.isProNoun();
		}
		else return false;
	}
	
	
}

class VnWord{
	private ArrayList<String> POS = null;
	private String _word;
	
	public VnWord(String _w){
		POS = new ArrayList<String>();		
		_word = _w.trim().toLowerCase();
	}
	
	public String getWord(){
		return _word;		
	}
	
	public boolean isNoun(){
		return POS.contains("N");
	}
	
	public boolean isVerb(){
		return POS.contains("V");
	}
	
	public boolean isProNoun(){
		return POS.contains("Pp");
	}
	public void addPOS(String p){
		POS.add(p);
	}
}

