/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.io.*;
import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author Thien
 */
public class nounPhraseAnalysis
{
    public static void analysisInput(boolean mode, String inputFile, List nounPhrases, List nounPhrasesTagged, List nounPhrasesAnalysis, double hiConfi, double lowConfi,
            List entityTaggedByOldModel, List highConfidenceEntityTagged)
    {
        if (nounPhrases != null)
            nounPhrases.clear();
        else
            nounPhrases = new ArrayList();

        if (nounPhrasesAnalysis != null)
            nounPhrasesAnalysis.clear();
        else
            nounPhrasesAnalysis = new ArrayList();

        if (nounPhrasesTagged != null)
            nounPhrasesTagged.clear();
        else
            nounPhrasesTagged = new ArrayList();

        BufferedReader fin = null;

        try
        {
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
            String line = "";
            int count = -1;

            while ((line = fin.readLine()) != null)
            {
                //System.out.println("OK!");
                line = line.trim();
                //System.out.println("line = " + line);
                if (line.length() == 0) continue;
                count++;
                int curPosLo = 0;
                ArrayList<entities.entity> entiAdd = new ArrayList<entities.entity>();
                ArrayList<entities.entity> toNounPhraseTagged = new ArrayList<entities.entity>();
                ArrayList<entities.entity> toNounPhraseAnalysis = new ArrayList<entities.entity>();
                ArrayList<entities.entity> oldModel = (ArrayList<entities.entity>) entityTaggedByOldModel.get(count);
                int[] taggedSegments = functions.getSegments(highConfidenceEntityTagged, count);
                int[] savedSegments = null;
                String[] words = functions.getVnWordArray(line);
                do
                {
                    entities.entity eAdd = getNextNounPhrase(count, line, curPosLo);
                    //System.out.println("OK!");
                    if (eAdd == null) break;
                    
                    curPosLo = eAdd.endChar + 1;
                    eAdd.endChar = 0;
                    if (mode)
                        savedSegments = tagSegment(mode, words, count, hiConfi, lowConfi, eAdd.startVn, eAdd.endVn, oldModel, taggedSegments, savedSegments, toNounPhraseTagged, toNounPhraseAnalysis);
                    else
                        savedSegments = tagSegment(mode, words, count, hiConfi, lowConfi, eAdd.startVn, eAdd.endVn, oldModel, null, savedSegments, toNounPhraseTagged, toNounPhraseAnalysis);

                    ////if (toNounPhraseTagged.isEmpty())
                       entiAdd.add(eAdd);
                }
                while(true);
                ArrayList<entities.entity> temp1 = functions.trimArrayEntities(toNounPhraseTagged);
                nounPhrasesTagged.add(temp1);
                ArrayList<entities.entity> temp2 = functions.trimArrayEntities(toNounPhraseAnalysis);
                nounPhrasesAnalysis.add(temp2);
                nounPhrases.add(entiAdd);
            }

            fin.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error in nounPhraseAnalysis.analysisInput : " + ex);
        }
    }

    public static entities.entity getNextNounPhrase(int line, String data, int posChar)
    {
        if (posChar < 0) posChar = 0;
        entities.entity result = new entities.entity(0, 0, "", 0, 0, 0, 0, "");

        int beginOpenTag = data.indexOf("<", posChar);
        int endOpenTag = data.indexOf(">", beginOpenTag);

        if (beginOpenTag == -1 || endOpenTag == -1)
            return null;

        String openTag = data.substring(beginOpenTag + 1, endOpenTag);

        String closeTag = "</" + openTag + ">";

        int closeTagIdx = data.indexOf(closeTag, endOpenTag + 1);

        if (closeTagIdx == -1) {
            return null;
        }

        String subData = data.substring(0, endOpenTag + 1);
        int preNumEntities = functions.getNumVnWords(subData);
        subData = data.substring(0, closeTagIdx + openTag.length() + 3);
        int containedNumEntityes = functions.getNumVnWords(subData);
        String textEntity = data.substring(endOpenTag + 1, closeTagIdx);

        result.line = line;
        result.startVn = preNumEntities;
        result.endVn = containedNumEntityes - 1;
        //result.startChar = beginOpenTag;
        result.endChar = closeTagIdx + openTag.length() + 2;
        //result.tag = openTag.toLowerCase();
        result.text = textEntity.trim();
        //result.confidence = confi;

        return result;
    }

    public static int[] tagSegment(boolean mode, String[] words, int line, double highConfi, double lowConfi, int start, int end, ArrayList<entities.entity> oldModel,
            int[] taggedSegments, int[] savedSegments, ArrayList<entities.entity> toNounPhraseTagged, ArrayList<entities.entity> toNounPhraseAnalysis)
    {
        if (words.length == 0) return savedSegments;
        if (start < 0) start = 0;
        if (end >= words.length) end = words.length - 1;
        if (start > end) return savedSegments;
        int traceFirst = -1;
        String orgLocDetermine = "";
        if (start >= 1)
            orgLocDetermine = functions.removePunctuations(functions.removeBrackets(words[start - 1]));
        for (int run = start; run <= end; run++)
        {
            String runWord = words[run];
            String noBrackets = functions.removeBrackets(runWord);
            traceFirst = run;
            if (!functions.isPunctuationsAll(runWord) && !functions.lookup_NUMERICAL_PRONOUN_DICT(noBrackets) && !functions.isNumber(noBrackets))
                break;
        }

       String firstWord = words[traceFirst];
       if (functions.isPunctuationsAll(firstWord) || functions.lookup_NUMERICAL_PRONOUN_DICT(functions.removeBrackets(firstWord)) || functions.isNumber(functions.removeBrackets(firstWord)))
           return savedSegments;

       int traceLast = end;

       if (traceLast < traceFirst) return savedSegments;

       return tagSegmentWhenEliminated(mode, words, orgLocDetermine, line, highConfi, lowConfi, traceFirst, traceLast, oldModel, taggedSegments, savedSegments, toNounPhraseTagged, toNounPhraseAnalysis);
    }

    public static int[] tagSegmentWhenEliminated(boolean mode, String[] words, String orgLocDetermine, int line, double highConfi, double lowConfi,  int start, int end, ArrayList<entities.entity> oldModel,
            int[] taggedSegments, int[] saveSegments, ArrayList<entities.entity> toNounPhraseTagged, ArrayList<entities.entity> toNounPhraseAnalysis)
    {
        String firstWord = functions.removeBrackets(words[start]);
        int traceStart = start, traceEnd = start;
        if (functions.lookup_ORG_INDICATE_NOUN_DICT(firstWord))
        {
            if (start == end) return saveSegments;
            int traceOrg = end;
            if (start > 0)
            {
                String numberWord = functions.removeBrackets(words[start - 1]);
                String tagTrue = "";
                if (start == 1)
                    tagTrue = "org";
                else if (functions.lookup_LOC_INDICATE_VERB_DICT(functions.removeBrackets(words[start - 2])) || functions.lookup_LOC_INDICATE_ADVERB_DICT(functions.removeBrackets(words[start - 2])))
                {
                    tagTrue = "loc";
                }
                else
                    tagTrue = "org";
                if (functions.lookup_NUMERICAL_PRONOUN_DICT(numberWord) || functions.isNumber(numberWord))
                {
                    if (functions.isAllCapitalized(words[end]))
                    {
                        int loopFirst = start, loopLast = end, traceLoopLast = end, pre = end;
                        do
                        {
                            if (loopLast < words.length - 1)
                            {
                                if (words[loopLast + 1].equals("("))
                                {
                                    for (int runi = loopLast + 1; runi < words.length; runi++)
                                        if (words[runi].equals(")"))
                                        {
                                            traceLoopLast = runi;
                                            break;
                                        }
                                }
                            }
                            saveSegments = addToNounPhrases(mode, words, loopFirst, loopLast, traceLoopLast, line, oldModel, lowConfi, tagTrue, taggedSegments, saveSegments, toNounPhraseTagged, toNounPhraseAnalysis);
                            loopLast = traceLoopLast;
                            if (loopLast >= words.length) break;
                            boolean isExit = false;
                            for (int k = loopLast + 1; k < words.length; k++)
                            {
                                if (functions.isPunctuationsAll(words[k])) continue;
                                if (!functions.isAllCapitalized(words[k]))
                                {
                                    isExit = true;
                                    break;
                                }
                                loopFirst = k;
                                loopLast = k;
                                traceLoopLast = k;
                                break;
                            }
                            if (isExit) break;
                            if (pre == loopLast) break;
                            pre = loopLast;
                        }
                        while(true);
                        return saveSegments;
                    }
                }
            }
            if (end < words.length - 1)
            {
                if (words[end + 1].equals("("))
                {
                    for (int i = end + 1; i < words.length; i++)
                        if (words[i].equals(")"))
                        {
                            traceOrg = i;
                            break;
                        }
                }
            }

            if (functions.lookup_LOC_INDICATE_VERB_DICT(orgLocDetermine) || functions.lookup_LOC_INDICATE_ADVERB_DICT(orgLocDetermine))
                saveSegments = functions.addToNounPhraseTagged(mode, words, start, end, traceOrg, line, oldModel, lowConfi, "loc", taggedSegments, saveSegments, toNounPhraseTagged);
            else
                saveSegments = functions.addToNounPhraseTagged(mode, words, start, end, traceOrg, line, oldModel, lowConfi, "org", taggedSegments, saveSegments, toNounPhraseTagged);

            int toEliminate = 0; //no initial
            for (int run = start + 1; run <= traceOrg; run++) //initial end
            {
                String temp = functions.removePunctuations(functions.removeBrackets(words[run]));
                if (temp.isEmpty() && (run != traceOrg)) //no initial
                    continue; //no initial
                if ((run != traceOrg) || !temp.isEmpty())
                    toEliminate = run; //no initial
                if (functions.lookup_ORG_INDICATE_NOUN_DICT(temp))
                {
                    traceEnd = toEliminate - 1; //run - 1 initial
                    if (traceStart == traceEnd)
                    {
                        traceStart = run;
                        continue;
                    }
                    String textLocal = functions.getTextInArray(words, traceStart, traceEnd);
                    entities.entity en = new entities.entity(0, 0, textLocal, 0, traceStart, traceEnd, line, "org");
                    toNounPhraseAnalysis.add(en);
                    traceStart = run;
                }
                if (run == traceOrg) //initial end
                {
                    traceEnd = toEliminate; //inital run
                    if (traceStart == traceEnd) break;
                    String textLocal = functions.getTextInArray(words, traceStart, traceEnd);
                    entities.entity en = new entities.entity(0, 0, textLocal, 0, traceStart, traceEnd, line, "org");
                    toNounPhraseAnalysis.add(en);
                }
            }
            if (start == traceOrg)
            {
                String textLocal = functions.getTextInArray(words, start, start);
                entities.entity en = new entities.entity(0, 0, textLocal, 0, start, start, line, "org");
                toNounPhraseAnalysis.add(en);
            }
            return saveSegments;
        }

        if (functions.lookup_PER_INDICATE_NOUN_DICT(firstWord))
        {
            
            if (start == end) return saveSegments;
            String tempPer = "", tagPer = "per";
            
            for (int i = start; i < words.length; i++)
            {
                tempPer = words[i];
                if (functions.isPunctuationsAll(tempPer) || functions.lookup_CONJUNCTION_DICT(functions.removeBrackets(tempPer))) continue;
                if (!functions.lookup_PER_INDICATE_NOUN_DICT(functions.removeBrackets(tempPer)) && !functions.isAllCapitalized(tempPer)) break;
                if (functions.lookup_PER_INDICATE_NOUN_DICT(functions.removeBrackets(tempPer)))
                {
                    tagPer = "per";
                    if (functions.lookup_PER_LOC_AMBIGUITY_DICT(functions.removeBrackets(words[i])))
                        tagPer = "loc";
                    continue;
                }
                if (tagPer.equals("loc") && functions.lookup_LOC_DICT(functions.removeBrackets(words[i])))
                    tagPer = "loc";
                else
                    tagPer = "per";
                saveSegments = addToNounPhrases(mode, words, i, i, i, line, oldModel, lowConfi, tagPer, taggedSegments, saveSegments, toNounPhraseTagged, toNounPhraseAnalysis);
            }

            return saveSegments;
        }

        if (functions.lookup_LOC_INDICATE_NOUN_DICT(firstWord))
        {
            if (start == end) return saveSegments;
            //String strLoc = words[end];
            //if (functions.isPunctuationsAll(strLoc)) return saveSegments;
            //if (!functions.isAllCapitalized(strLoc)) return saveSegments;
            String tempLoc = "";
            int traceLoc = start;

            for (int i = start; i < words.length; i++) //end + 1
                {
                    tempLoc = words[i];
                    if (functions.isPunctuationsAll(tempLoc) || functions.lookup_CONJUNCTION_DICT(functions.removeBrackets(tempLoc))) continue;
                    if (!functions.lookup_LOC_INDICATE_NOUN_DICT(functions.removeBrackets(tempLoc)) && !functions.isAllCapitalized(tempLoc) && !functions.isNumber(functions.removeBrackets(tempLoc))
                            && !functions.lookup_LOC_INDICATE_ADVERB_DICT(functions.removeBrackets(tempLoc))) break;
                    if (functions.lookup_LOC_INDICATE_NOUN_DICT(functions.removeBrackets(tempLoc)) || functions.lookup_LOC_INDICATE_ADVERB_DICT(functions.removeBrackets(tempLoc))) continue;
                    traceLoc = i;
                }
            
            if (traceLoc > start)
            {
                if ((traceLoc + 1) < words.length)
                {
                    if ((words[traceLoc + 1].equals(")") || words[traceLoc + 1].equals("}")) && !functions.checkBracket(words, start, traceLoc))
                        traceLoc++;
                }
                saveSegments = addToNounPhrases(mode, words, start, traceLoc, traceLoc, line, oldModel, lowConfi, "loc", taggedSegments, saveSegments, toNounPhraseTagged, toNounPhraseAnalysis);
            }
            return saveSegments;                              //end
        }

        if ((start != end) || !functions.isAllCapitalized("[" + firstWord + "]")) return saveSegments;

        return noIndicateDetermination(mode, words, line, start, oldModel, lowConfi, taggedSegments, saveSegments, toNounPhraseTagged, toNounPhraseAnalysis);
    }

    public static int[] addToNounPhrases(boolean mode, String[] words, int start, int end, int traceEnd, int line, ArrayList<entities.entity> oldModel, double lowConfi, String tag,
            int[] taggedSegments, int[] savedSegments, ArrayList<entities.entity> toNounPhraseTagged, ArrayList<entities.entity> toNounPhraseAnalysis)
    {
        savedSegments = functions.addToNounPhraseTagged(mode, words, start, end, traceEnd, line, oldModel, lowConfi, tag, taggedSegments, savedSegments, toNounPhraseTagged);

        int toEliminate = 0, tail = traceEnd, head = start; //no initial
        if (tag.equalsIgnoreCase("org"))
        {
            for (int run = start + 1; run <= traceEnd; run++) //initial end
            {
                String temp = functions.removePunctuations(functions.removeBrackets(words[run]));
                if (temp.isEmpty() && (run != traceEnd)) //no initial
                    continue; //no initial
                if ((run != traceEnd) || !temp.isEmpty())
                    toEliminate = run; //no initial
                if (functions.lookup_ORG_INDICATE_NOUN_DICT(temp))
                {
                    tail = toEliminate - 1; //run - 1 initial
                    if (head == tail)
                    {
                        head = run;
                        continue;
                    }
                    String textLocal = functions.getTextInArray(words, head, tail);
                    entities.entity en = new entities.entity(0, 0, textLocal, 0, head, tail, line, "org");
                    toNounPhraseAnalysis.add(en);
                    head = run;
                }
                if (run == traceEnd) //initial end
                {
                    tail = toEliminate; //inital run
                    if (head == tail) break;
                    String textLocal = functions.getTextInArray(words, head, tail);
                    entities.entity en = new entities.entity(0, 0, textLocal, 0, head, tail, line, "org");
                    toNounPhraseAnalysis.add(en);
                }
            }
            if (start == traceEnd)
            {
                String textLocal = functions.getTextInArray(words, start, start);
                entities.entity en = new entities.entity(0, 0, textLocal, 0, start, start, line, "org");
                toNounPhraseAnalysis.add(en);
            }
        }
        else
        {
            String textLocal = functions.getTextInArray(words, start, traceEnd);
            entities.entity en = new entities.entity(0, 0, textLocal, 0, start, traceEnd, line, tag);
            toNounPhraseAnalysis.add(en);
        }
        return savedSegments;
    }

    public static int[] noIndicateDetermination(boolean mode, String[] words, int line, int start, ArrayList<entities.entity> oldModel, double lowConfi,
            int[] taggedSegments, int[] savedSegments, ArrayList<entities.entity> toNoundPhraseTagged, ArrayList<entities.entity> toNounPhraseAnalysis)
    {
        
        int state = 0;
        String temp = "", tag = "";
        //out("--------noIndi: " + line + "------------");
        //out(words[start]);
        int te = start, saveStart = start;
        
        if ((te + 1) < words.length)
        {
            if (words[te + 1].equals("("))
            {
                
                if ((te + 2) < words.length)
                {
                    String tagTry = showTag(functions.removeBrackets(words[te + 2]));
                    if (!tagTry.isEmpty() && !tagTry.equalsIgnoreCase("loc"))
                    {
                        savedSegments = addToNounPhrases(mode, words, start, start, start, line, oldModel, lowConfi, tagTry, taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
                        return savedSegments;
                    }
                    for (int ru = te + 1; ru < words.length; ru++)
                        if (words[ru].equals(")"))
                        {
                            te = ru;
                            break;
                        }
                }
            }
        }
        
        start = te;
        for (int i = start + 1; i < words.length; i++)
        {
            
            temp = words[i];
            if (functions.isPunctuationsAll(temp)) break;
            temp = functions.removeBrackets(temp);
            
            if (state == 0) //addition
            {
                //out("In state = 0");
                if (functions.lookup_ADDITION_DICT(temp)) continue;
                if (functions.lookup_DEFINE_DICT(temp))
                {
                    state = 1;
                    continue;
                }
                
                break;
            }
            if (state == 1) //define
            {
                //out("In state = 1");
                if (functions.lookup_DEFINE_DICT(temp)) continue;
                if (functions.lookup_NUMERICAL_PRONOUN_DICT(temp) || functions.isNumber(temp))
                {
                    state = 2;
                    continue;
                }
                tag = showTag(temp);
                break;
            }
            if (state == 2) //number
            {
                //out("In state = 2");
                //out("temp in state 2 = " + temp);
                if (functions.lookup_NUMERICAL_PRONOUN_DICT(temp) || functions.isNumber(temp)) continue;
                tag = showTag(temp);
                break;
            }
        }

        
        if (!tag.isEmpty())
        {
            savedSegments = addToNounPhrases(mode, words, saveStart, saveStart, saveStart, line, oldModel, lowConfi, tag, taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
            return savedSegments;
        }
        
        start = saveStart;
        if (start >= 1)
        {
            String isLoc = "";
            isLoc = functions.removePunctuations(functions.removeBrackets(words[start - 1]));
            if (functions.lookup_LOC_INDICATE_VERB_DICT(isLoc) || functions.lookup_LOC_INDICATE_ADVERB_DICT(isLoc))
            {
                
                int laLoc = start;
                for (int th = start + 1; th < words.length; th++)
                {
                    String locT = words[th];
                    if (functions.isPunctuationsAll(locT)) continue;
                    locT = functions.removeBrackets(locT);
                    if (locT.isEmpty()) continue;
                    if (!functions.lookup_LOC_INDICATE_NOUN_DICT(locT) && !functions.isAllCapitalized("[" + locT + "]")
                            && !functions.lookup_LOC_INDICATE_ADVERB_DICT(locT))
                        break;
                    if (functions.lookup_LOC_INDICATE_NOUN_DICT(locT) || functions.lookup_LOC_INDICATE_ADVERB_DICT(locT)) continue;
                    laLoc = th;
                }
                if ((laLoc + 1) < words.length)
                {
                    if ((words[laLoc + 1].equals(")") || words[laLoc + 1].equals("}")) && !functions.checkBracket(words, saveStart, laLoc))
                        laLoc++;
                }
                savedSegments = addToNounPhrases(mode, words, saveStart, saveStart, laLoc, line, oldModel, lowConfi, "loc", taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
                return savedSegments;
            }
        }

        if ((te + 1) < words.length)
        {
            int pu = te + 1;
            if (words[pu].equals("-") || words[pu].equals(","))
            {
                int tem = pu;
                for (int rr = pu + 1; rr < words.length; rr++)
                {
                    if (functions.lookup_NUMERICAL_PRONOUN_DICT(functions.removeBrackets(words[rr])) || functions.isNumber(functions.removeBrackets(words[rr])))
                    {
                        tem = rr;
                        continue;
                    }
                    break;
                }
                pu = tem;
                if ((pu + 1) < words.length)
                {
                    for (int r = pu + 1; r < words.length; r++)
                    {
                        String tr = words[r];
                        if (functions.isPunctuationsAll(tr)) break;
                        tr = functions.removeBrackets(tr);
                        if (functions.lookup_NUMERICAL_PRONOUN_DICT(tr) || functions.isNumber(tr))
                        {
                            pu = r;
                            continue;
                        }
                        break;
                    }
                    String tagTry = showTag(functions.removeBrackets(words[pu + 1]));
                    if (!tagTry.isEmpty())
                    {
                        savedSegments = addToNounPhrases(mode, words, saveStart, saveStart, saveStart, line, oldModel, lowConfi, tagTry, taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
                        return savedSegments;
                    }
                }
            }
        }
        
        if (start >= 1)
        {
            String test1 = words[start - 1];
            if (test1.equals(":") || functions.removeBrackets(test1).equals("như") || functions.removeBrackets(test1).equals("gồm"))
            {
                
                if (start >= 2)
                {
                    int nu = start - 2;
                    String test2 = words[start - 2];
                    if (test2.equals(":") || functions.removeBrackets(test2).equals("như") || functions.removeBrackets(test2).equals("gồm"))
                        nu--;
                    if (nu >= 0)
                    {
                        int co = 0;
                        do
                        {
                            co++;
                            String testTag = showTag(functions.removeBrackets(words[nu]));
                            if (!testTag.isEmpty())
                            {
                                for (int to = start; to < words.length; to++)
                                {
                                    String toi = words[to];
                                    if (functions.isPunctuationsAll(toi) || functions.lookup_CONJUNCTION_DICT(functions.removeBrackets(toi))) continue;
                                    if (!functions.isAllCapitalized(toi)) break;
                                    savedSegments = addToNounPhrases(mode, words, to, to, to, line, oldModel, lowConfi, testTag, taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
                                }
                                return savedSegments;
                            }
                            nu--;
                        }
                        while((co < 2) && (nu >= 0));
                    }
                }
            }
        }

        if (functions.lookup_LOC_DICT(functions.removeBrackets(words[start])))
        {
                int laLoc = start;
                for (int th = start + 1; th < words.length; th++)
                {
                    String locT = words[th];
                    if (functions.isPunctuationsAll(locT)) continue;
                    locT = functions.removeBrackets(locT);
                    if (locT.isEmpty()) continue;
                    if (!functions.lookup_LOC_INDICATE_NOUN_DICT(locT) && !functions.isAllCapitalized("[" + locT + "]")
                            && !functions.lookup_LOC_INDICATE_ADVERB_DICT(locT))
                        break;
                    if (functions.lookup_LOC_INDICATE_NOUN_DICT(locT) || functions.lookup_LOC_INDICATE_ADVERB_DICT(locT)) continue;
                    laLoc = th;
                }
                if ((laLoc + 1) < words.length)
                {
                    if ((words[laLoc + 1].equals(")") || words[laLoc + 1].equals("}")) && !functions.checkBracket(words, saveStart, laLoc))
                        laLoc++;
                }
                savedSegments = addToNounPhrases(mode, words, saveStart, saveStart, laLoc, line, oldModel, lowConfi, "loc", taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
                return savedSegments;
        }

        if (start >= 1)
        {
            int nae = start - 1;
            String test1 = words[start - 1];
            if (functions.lookup_DEFINE_DICT(functions.removeBrackets(test1)))
            {
                nae--;
                
            }
                if (nae >= 0)
                {
                    String test2 = words[nae];
                    
                    if (test2.equalsIgnoreCase("[tên]") && (nae >= 1))
                    {
                        int co = 0, nu = nae - 1;
                        do
                        {
                            co++;
                            String testTag = showTag(functions.removeBrackets(words[nu]));
                            if (!testTag.isEmpty())
                            {
                                if ((nu == 0))
                                {
                                    savedSegments = addToNounPhrases(mode, words, start, start, start, line, oldModel, lowConfi, testTag, taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
                                }
                                else if (functions.lookup_NUMERICAL_PRONOUN_DICT(functions.removeBrackets(words[nu - 1])) || functions.isNumber(functions.removeBrackets(words[nu - 1])))
                                {
                                    for (int to = start; to < words.length; to++)
                                    {
                                        String toi = words[to];
                                        if (functions.isPunctuationsAll(toi) || functions.lookup_CONJUNCTION_DICT(functions.removeBrackets(toi))) continue;
                                        if (!functions.isAllCapitalized(toi)) break;
                                        savedSegments = addToNounPhrases(mode, words, to, to, to, line, oldModel, lowConfi, testTag, taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);
                                    }
                                }
                                else
                                    savedSegments = addToNounPhrases(mode, words, start, start, start, line, oldModel, lowConfi, testTag, taggedSegments, savedSegments, toNoundPhraseTagged, toNounPhraseAnalysis);

                                return savedSegments;
                            }
                            nu--;
                        }
                        while((co < 2) && (nu >= 0));
                    }
            }
        }


        return savedSegments;
    }

    public static String showTag(String temp)
    {
        if (temp.isEmpty()) return "";
        if (functions.lookup_LOC_INDICATE_NOUN_DICT(temp))
            return "loc";
        if (functions.lookup_PER_INDICATE_NOUN_DICT(temp))
            return "per";
        if (functions.lookup_ORG_INDICATE_NOUN_DICT(temp))
            return "org";
        return "";
    }

    public static void out(Object o)
    {
        System.out.println(o);
    }
}
