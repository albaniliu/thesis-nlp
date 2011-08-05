/*
    Copyright (C) 2007 by Cam-Tu Nguyen
	
    Email:	ncamtu@gmail.com
	
    Department of Information System,
    College of Technology	
    Hanoi National University, Vietnam	
*/ 

package crfsvm.crf.een_phuong;

import java.util.ArrayList;
import java.util.Iterator;

public class ContextInfo{
	protected boolean bSecondMarkovOrder = false;
	protected String cpName = "";
	protected ArrayList<CPPair> mContextPositions = new ArrayList<CPPair>();
	protected Iterator<CPPair> it = null;
	
	public boolean isSecondMarkovOrder()
	{
		return bSecondMarkovOrder;
	}
	
	public String getName(){
		return cpName;		
	}
	
	public CPPair nextPosInfo(){
		if (it == null){
			it = mContextPositions.listIterator();
		}
		if (it.hasNext()){
			return it.next();
		}
		else return null; 
	}
	
	public boolean hasNextPosInfo(){
		if (it == null){
			it = mContextPositions.listIterator();
		}
		
		if (it.hasNext())
			return true;
		else return false;
	}
	public void resetIterator(){
		it = mContextPositions.listIterator(0);
	}
	public CPPair getPosInfoAt(int i){
		return mContextPositions.get(i);		
	}
	
	public int size(){
		return mContextPositions.size();
	}
}
