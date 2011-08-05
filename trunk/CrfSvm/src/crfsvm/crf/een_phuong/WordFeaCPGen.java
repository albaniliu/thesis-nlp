

package crfsvm.crf.een_phuong;

public class WordFeaCPGen {
	public static String doCntxPreGen(IOB2Sequence os, int curPos, ContextInfo cpInfo){
		String cp = "";
		String suffix = "";
		String obsrvConjStr = "";
		
		//generate prefix
		 for (int i = 0; i < cpInfo.size(); ++ i){
			CPPair p = cpInfo.getPosInfoAt(i);
            int pos = curPos + p.pos;
            
            if (pos < 0 || pos >= os.length())
                return cp;
            
            suffix += p.col + "-" + p.pos + ":";
            obsrvConjStr += os.getToken(p.col, curPos + p.pos) + " ";
        }
        obsrvConjStr = obsrvConjStr.trim();
        suffix = suffix.substring(0, suffix.length() - 1);
        
        if (cpInfo.getName().equals("initial_cap")){
            if (isInitCap(obsrvConjStr)) cp = "wf:ic:" + suffix;
        }
        else if (cpInfo.getName().equals("all_cap")){
        	if (isAllCapChar(obsrvConjStr)) cp = "wf:ac:" + suffix;
        }
        else if (cpInfo.getName().equals("all_lower_case")){
        	if (isAllCapChar(obsrvConjStr)) cp = "wf:lower:" + suffix;
        }
        else if (cpInfo.getName().equals("first_obsrv")){
            if (curPos + cpInfo.getPosInfoAt(0).pos == 0 ) cp = "wf:fo:" + cpInfo.getPosInfoAt(0).pos;
        }
        else if (cpInfo.getName().equals("mark")){
            if (isMarks(obsrvConjStr)) cp = "wf:ma:" + suffix;
        }
        else if (cpInfo.getName().equals("all_cap_and_digit")){
            if (isMixCapCharAndDigit(obsrvConjStr)) cp = "wf:mcd:" + suffix;
        }
        else if (cpInfo.getName().equals("contain_percent_sign")){
            if (containPercentSign(obsrvConjStr)) cp = "wf:cps:" + suffix;        
		}
        else if (cpInfo.getName().equals("contain_slash_sign")){
	        if (containSlashSign(obsrvConjStr)) cp = "wf:css:" + suffix;
	    }
        else if (cpInfo.getName().equals("contain_comma_sign")){
	        if (containCommaSign(obsrvConjStr)) cp = "wf:ccs:" + suffix;
	    }
        if (cpInfo.bSecondMarkovOrder && !cp.equals("")) cp = "#" + cp;
        
        return cp;
				
	}	
	 protected static boolean isInitCap(String word){
        try {
            if (isMixCapCharAndDigit(word)) return false;
            char initChar = word.charAt(0);
            return Character.isUpperCase(initChar);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }
	 }
	    
	 protected static boolean isMixCapCharAndDigit(String word){
        boolean containChar = false;
        boolean containDigit = false;
        for (int i = 0; i < word.length(); ++i){
            char c = word.charAt(i);
            if (Character.isLetter(c)){
                if (!containChar) containChar = true;
                if (Character.isLowerCase(c)) return false;
            } else if (!Character.isDigit(c)) return false;
            
            if (Character.isDigit(c) && !containDigit)
            	containDigit = true;
            	
        }
        if (!(containChar && containDigit)) return false;
        return true;
    }
    
    private static boolean isAllCapChar(String word){    	
        for (int i = 0; i < word.length(); ++i){
            char c = word.charAt(i);
            if (!Character.isLetter(c)) return false;
            
            if (Character.isLowerCase(c)) return false;
        }
        
        return true;
    }
    
    private static boolean isAllLowerCaseChar(String word){
    	for (int i = 0; i < word.length(); ++i){
            char c = word.charAt(i);
            if (!Character.isLetter(c)) return false;
            
            if (Character.isUpperCase(c)) return false;
        }
        
        return true;
    }
    
	protected static boolean isMarks(String word){
        for (int i = 0; i < word.length(); ++i){
            char c = word.charAt(i);
            if (Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }
    
	 protected static boolean containPercentSign(String word){
    	return word.indexOf("%") != -1;
    }
    
	 protected static boolean containSlashSign(String word){
    	return word.indexOf("/") != -1;
    }
    
	 protected static boolean containCommaSign(String word){
    	return word.indexOf(":") != -1;
    }
}
