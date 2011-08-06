 

package crfsvm.crf.een_phuong;

public class ConjunctCPGen {
	public static String doCntxPreGen(IOB2Sequence os, int curPos, ContextInfo cpInfo){
		String cp = "";
		String prefix = "cj:w:";
		
		//generate prefix
		cpInfo.resetIterator();
		while (cpInfo.hasNextPosInfo()){
			CPPair p = cpInfo.nextPosInfo();			
			int pos = curPos + p.pos;			
		
			if (pos < 0 || pos >= os.length())
				return cp;
			prefix += p.col + "-" + p.pos + ":";
		}
		
		//generate suffix
		cpInfo.resetIterator();
		String suffix = "";
		while (cpInfo.hasNextPosInfo()){
			CPPair p = cpInfo.nextPosInfo();
			int pos = curPos + p.pos;
			
			String curToken = os.getToken(p.col, pos);
			curToken = curToken.replaceAll("([ \t]+)", " ");
			curToken = curToken.replaceAll(" ", "-"); //Vietnamese word has white space
			
			if (!cpInfo.hasNextPosInfo()){ //last pos info
				suffix += curToken;
			}
			else suffix += curToken + ":";
		}
		cp = prefix + suffix;
		
	     if (cpInfo.bSecondMarkovOrder && !cp.equals("")) cp = "#" + cp;
		return cp;
	}
}
