/*
    Copyright (C) 2007 by Cam-Tu Nguyen
	
    Email:	ncamtu@gmail.com
	
    Department of Information System,
    College of Technology	
    Hanoi National University, Vietnam	
*/ 

package crfsvm.crf.een_phuong;

import java.util.regex.*;

public class RegexCPGen {
//	Regular Expression Pattern string
    private static String strNumberPattern = "[+-]?\\d+([,.]\\d+)*";
    private static String strShortDatePattern = "\\d+[/-:]\\d+";
    private static String strLongDatePattern = "\\d+[/-:]\\d+[/-:]\\d+";
    private static String strPercentagePattern = strNumberPattern + "%";
    private static String strCurrencyPattern = "\\p{Sc}" + strNumberPattern ;
    private static String strViCurrencyPattern = strNumberPattern + "[ \t]*\\p{Sc}";
    private static String strHourPattern="[0-9]+h[0-9]*";
    //private static String strHourPattern = "\\d{0-2}h\\d{0=2}"; 
    
//Regular Expression Pattern
    private static Pattern ptnNumber;
    private static Pattern ptnShortDate;
    private static Pattern ptnLongDate;
    private static Pattern ptnPercentage;
    private static Pattern ptnCurrency;
    private static Pattern ptnViCurrency;
    private static Pattern ptnHour;
    
    
//Methods
    public static String doCnxtPreGen(IOB2Sequence os, int curPos, ContextInfo cpInfo){
        //generate context predicates
        String cp = "";
        String prefix = "", regex = "";
        String obsrvConjStr = "";
        
        //get the context information from sequence
        for (int i = 0; i < cpInfo.size(); ++ i){
        	CPPair p = cpInfo.getPosInfoAt(i);
        	
            int pos = curPos + p.pos;
            if (pos < 0 || pos >= os.length())
                return cp;
            
            prefix += p.col + "-" + p.pos + ":";
            obsrvConjStr += os.getToken(p.col, curPos + p.pos) + " ";
        }
        obsrvConjStr = obsrvConjStr.trim().toLowerCase();
        prefix = prefix.substring(0, prefix.length() - 1);
        prefix = "re" + ":" + prefix;
        //System.out.println(obsrvConjStr);
        
        //Comparing against a specific pattern
        regex  = patternMatching(cpInfo.getName(), obsrvConjStr);
        if (!regex.equals("")) {
            cp = prefix + regex;
        }
        if (cpInfo.bSecondMarkovOrder && !cp.equals(""))cp = "#" + cp;
        return cp;
    }
    
    private static void patternCompile(){
        try{
            ptnNumber = Pattern.compile(strNumberPattern);
            ptnShortDate = Pattern.compile(strShortDatePattern);
            ptnLongDate = Pattern.compile(strLongDatePattern);
            ptnPercentage = Pattern.compile(strPercentagePattern);
            ptnCurrency = Pattern.compile(strCurrencyPattern);
            ptnViCurrency = Pattern.compile(strViCurrencyPattern);
            ptnHour = Pattern.compile(strHourPattern);
        } catch(PatternSyntaxException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        
    }
    
    private static String patternMatching(String ptnName, String input){
        String suffix = "";
        if (ptnNumber == null)patternCompile();
        
        Matcher matcher;
        if (ptnName.equals("number")){
            matcher = ptnNumber.matcher(input);
            if (matcher.matches())
                suffix = ":number";
        } else if (ptnName.equals("short_date")){
            matcher = ptnShortDate.matcher(input);
            if (matcher.matches()) suffix = ":short-date";
        } else if (ptnName.equals("long_date")){
            matcher = ptnLongDate.matcher(input);
            if (matcher.matches()) suffix = ":long-date";
        } else if (ptnName.equals("percentage")){
            matcher = ptnPercentage.matcher(input);
            if (matcher.matches()) suffix = ":percentage";
        }else if (ptnName.equals("currency")){
            matcher = ptnCurrency.matcher(input);
            if (matcher.matches()) suffix = ":currency";
            else {
                matcher = ptnViCurrency.matcher(input);
                if (matcher.matches()){
                    suffix = ":currency";
                }
            }
        }else if (ptnName.equals("hour")){
        	matcher = ptnHour.matcher(input);
        	if (matcher.matches()) suffix = ":hour";
        }
        return suffix;
    }
}
