package crfsvm.crf.een_phuong;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ExtractNames {
	public static void main(String [] args){
		if (args.length != 1){
			System.out.println("Usage: ExtractNames [inputfile]");
			return;
		}
		
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(args[0]),"UTF-8"));
			BufferedWriter firstOut = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("vnFirstNames.txt"), "UTF-8"));
			BufferedWriter midOut = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("vnMiddleNames.txt"), "UTF-8"));			
			BufferedWriter lastOut = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("vnLastNames.txt"), "UTF-8"));
			
			HashMap<String, Integer> firstNameList = new HashMap<String, Integer>();
			HashMap<String, Integer> middleNameList = new HashMap<String, Integer>();
			HashMap<String, Integer> lastNameList = new HashMap<String, Integer>();
			
			String line;
			while ((line = in.readLine()) != null){
				StringTokenizer tknr = new StringTokenizer(line, " \t");
				
				if (tknr.hasMoreTokens()){
					String tk = tknr.nextToken().toLowerCase();
					System.out.println(tk);
					
					if (!firstNameList.containsKey(tk))
						firstNameList.put(tk,1);
					else {
						Integer count = firstNameList.get(tk);
						firstNameList.put(tk, count.intValue() + 1);
					}
				}
				
				String tk = "";
		//		String tkconj = "";
				while (tknr.hasMoreTokens()){
					tk = tknr.nextToken().toLowerCase();					
					if (tk.length() == 1)
						break;
					
					if (!tknr.hasMoreTokens()){
						
						if (!tk.trim().equals("")){
							if (!lastNameList.containsKey(tk))
								lastNameList.put(tk,1);
							else {
								int count = lastNameList.get(tk);
								lastNameList.put(tk, ++count);
							}
						}
					}
					else {
					//	tkconj += tk + " ";		
						if (!tk.trim().equals("")){
							if (!middleNameList.containsKey(tk))
								middleNameList.put(tk,1);
							else {
								int count = middleNameList.get(tk);
								middleNameList.put(tk, ++count);
							}
						}
					
					}
				}
				
			}
			
			for (Iterator<String> it = firstNameList.keySet().iterator(); it.hasNext();){
				String key = it.next();
				int value = firstNameList.get(key);
				
				if (value > 2)
					firstOut.write(key + "\n");
			}
			
			for (Iterator<String> it = middleNameList.keySet().iterator(); it.hasNext();){
				String key = it.next();
				int value = middleNameList.get(key);
				
				if (value > 4)
					midOut.write(key + "\n");
			}
			
			for (Iterator<String> it = lastNameList.keySet().iterator(); it.hasNext();){
				String key = it.next();
				int value = lastNameList.get(key);
				
				if (value > 5)
					lastOut.write(key + "\n");
			}
		
			in.close();
			firstOut.close();
			midOut.close();
			lastOut.close();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
	}
}
