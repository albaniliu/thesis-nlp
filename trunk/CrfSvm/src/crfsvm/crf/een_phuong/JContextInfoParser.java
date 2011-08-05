/*
    Copyright (C) 2007 by Cam-Tu Nguyen
	
    Email:	ncamtu@gmail.com
	
    Department of Information System,
    College of Technology	
    Hanoi National University, Vietnam	
*/ 

package crfsvm.crf.een_phuong;

import java.util.StringTokenizer;

public class JContextInfoParser {
	public static ContextInfo parse(String ptn){
		ContextInfo ci = new ContextInfo();
		
		try {
			StringTokenizer commaTknr = new StringTokenizer(ptn, ":");
			ci.bSecondMarkovOrder = (commaTknr.nextToken().equals("2"));
			ci.cpName = commaTknr.nextToken();
			
			while (commaTknr.hasMoreTokens()){
				String posInfo = commaTknr.nextToken();
				
				StringTokenizer posTknr = new StringTokenizer(posInfo, "|");
				int col, pos;
				pos = Integer.parseInt(posTknr.nextToken());
				
				if (posTknr.hasMoreTokens())
					col = Integer.parseInt(posTknr.nextToken());
				else {					
					col = 0;
				}				
				ci.mContextPositions.add(new CPPair(col,pos));
			}
		}
		catch (Exception e){
			System.out.println("Context info parse error");
			return null;
		}
		
		return ci;
	}
	
	public static void main(String args[]){		
		ContextInfo ci = JContextInfoParser.parse("2:word_conj:1:2");
		
		System.out.println(ci.getName() + " " + ci.isSecondMarkovOrder());
		while (ci.hasNextPosInfo()){
			CPPair p = ci.nextPosInfo();
			System.out.println("col:" + p.col + "\tpos:" + p.pos);
		}
		
		ci.resetIterator();
//		while (ci.hasNextPosInfo()){
//			CPPair p = ci.nextPosInfo();
//			System.out.println("col:" + p.col + "\tpos:" + p.pos);
//		}
//		
	}
}


