package crfsvm.crf.een_phuong;

public class CPPair{
	public int pos;
	public int col;
	
	public CPPair(int c, int p){
		pos = p;
		col = c;
	}
	
	public CPPair(int p){
		pos = p;
		col = 0;
	}
	
	public static CPPair getPair(int c, int p){
		CPPair pair = new CPPair(c,p);
		return pair;
	}
	
	public static CPPair getPair(int p){
		CPPair pair = new CPPair(p);
		return pair;
	}
}
