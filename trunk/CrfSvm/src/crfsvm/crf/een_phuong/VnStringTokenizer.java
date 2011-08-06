package crfsvm.crf.een_phuong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class VnStringTokenizer {	
	private ArrayList<String> tokens = null;
	private Iterator<String> it = null;
	
	public VnStringTokenizer(String str, String delimiters){
		tokens = new ArrayList<String>();
		
		int nextOpenSquareBracketIdx = -1, nextCloseSquareBraketIdx = -1;
		
		int curPos = 0; //the next processing position		
		while (curPos < str.length()){
			nextOpenSquareBracketIdx = str.indexOf("[", curPos);
			
			if (nextOpenSquareBracketIdx == -1){
				String temp = str.substring(curPos);
				StringTokenizer tk = new StringTokenizer(temp, delimiters);
				
				while (tk.hasMoreTokens()){
					tokens.add(tk.nextToken());
				}
				
				curPos = str.length();
				break;
			}
			
			nextCloseSquareBraketIdx = str.indexOf("]", nextOpenSquareBracketIdx);
			
			if (nextCloseSquareBraketIdx == -1){
				String temp = str.substring(curPos);
				StringTokenizer tk = new StringTokenizer(temp, delimiters + "[");
				
				while (tk.hasMoreTokens()){
					tokens.add(tk.nextToken());
				}
				
				curPos = str.length();
				break;
			}
			
			//tokenize from curpos to nextOpenSquareBraketIdx (include curpos, not include open bracket)
			//get string from nextOpenSquareBraketIdx to nextCloseSquareBraketIdx (not include open and close bracket)
			//curPos = nextCloseSquareBraketIdx + 1
			String temp = str.substring(curPos, nextOpenSquareBracketIdx);
			StringTokenizer tk = new StringTokenizer(temp, delimiters);
			
			while (tk.hasMoreTokens()){
				tokens.add(tk.nextToken());
			}
			
			String w = str.substring(nextOpenSquareBracketIdx + 1, nextCloseSquareBraketIdx);
			tokens.add(w);
			curPos = nextCloseSquareBraketIdx + 1;
		}//end while
		
		it = tokens.iterator();
	}
	
	public boolean hasMoreTokens(){
		if (it == null)
			return false;
	
		return it.hasNext();
	}
	
	public String nextToken(){
		return it.next().toString();
	}
	
	public static void main(String [] args){
		String test = "[chủ tịch nước] [Nguyễn Minh Triết]     ,   .    [được] [bầu] [hồi] [tháng hai], [phát biểu]."; //"[hoc sinh] , .[hoc] [sinh hoc]";
		VnStringTokenizer tk = new VnStringTokenizer(test, " ");
		
		while(tk.hasMoreTokens())
			System.out.println(tk.nextToken());
	}
}
