/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;


import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author Thien
 */
public class matchNounPhraseCombination
{
    public static int[] tagMultipleApperances(int n1, int n2, boolean mode,  ArrayList<entities.entity> oldModel, double lowConfi, int[] taggedSegmentsLine, List nounPhrases, int[] candidateSegmentsLine, Map candidateEntities,
            int[] saveTaggedSegments, ArrayList<entities.entity> saveEntityTagged, String[] words, int line)
    {
        try{
        if (words == null) return saveTaggedSegments;
        if (words.length == 0) return saveTaggedSegments;
        if (line < 0) return saveTaggedSegments;
        int[] nounPhraseArray = functions.getSegments(nounPhrases, line);
        
        ArrayList<entities.candidate> candiArray = null;
        candiArray = defineBoundaryForCandidates(nounPhraseArray, candidateSegmentsLine, candidateEntities);
        if (candiArray == null) return saveTaggedSegments;
        
        if (candiArray.isEmpty()) return saveTaggedSegments;
        
        for (int i = 0; i < candiArray.size(); i++)
        {
            entities.candidate cand = candiArray.get(i);
            saveTaggedSegments = tagCandidates(mode, cand, oldModel, lowConfi, taggedSegmentsLine, saveTaggedSegments, saveEntityTagged, words, line);
        }
        //out("OK IN matcahaaaaaaaaaaaaaaa");
            return saveTaggedSegments;
            
        }
        catch (Exception ex)
        {
            System.out.println("Error in matchNounPhraseCombination.tagMultipleApperances" + ex);
            return saveTaggedSegments;
        }
    }

    public static ArrayList<entities.candidate> defineBoundaryForCandidates(int[] nounPhraseArray, int[] candidateSegmentsLine, Map candidateEntities)
    {
        try{
        if ((nounPhraseArray == null) || (candidateSegmentsLine == null)) return null;
        if ((nounPhraseArray.length == 0) || (candidateSegmentsLine.length == 0)) return null;
        ArrayList<entities.candidate> result = new ArrayList<entities.candidate>();
        int tempi1 = 0, tempi2 = 0, tempj1 = 0, tempj2 = 0;
        for (int i = 0; i < candidateSegmentsLine.length; i = i + 2)
        {
            tempi1 = candidateSegmentsLine[i];
            tempi2 = candidateSegmentsLine[i + 1];
            for (int j = 0; j < nounPhraseArray.length; j = j + 2)
            {
                tempj1 = nounPhraseArray[j];
                tempj2 = nounPhraseArray[j + 1];
                if ((tempj1 <= tempi1) && (tempi1 <= tempj2))
                {
                    entities.candidate cand = new entities.candidate();
                    entities.primaryEntity entiInOldModel = (entities.primaryEntity) candidateEntities.get("#" + tempi1 + "#" + tempi2);
                    cand.upper = tempj1;
                    cand.lower = tempj2;
                    cand.start = tempi1;
                    cand.end = tempi2;
                    cand.tag = entiInOldModel.tag;
                    result.add(cand);
                    break;
                }
            }
        }
        return result;
        }
        catch (Exception ex)
        {
            System.out.println("In define" + ex);
            return null;
        }
    }

    public static int[] tagCandidates(boolean mode, entities.candidate cand, ArrayList<entities.entity> oldModel, double lowConfi,  int[] taggedSegmentsLine, int[] saveTaggedSegments, ArrayList<entities.entity> saveEntityTagged,
            String[] words, int line)
    {
        try{
            
        if (cand == null) return saveTaggedSegments;
        if (cand.tag.equalsIgnoreCase("per"))
        {
            
            saveTaggedSegments = functions.addToNounPhraseTagged(mode, words, cand.start, cand.end, cand.end, line, oldModel, lowConfi, "per", taggedSegmentsLine, saveTaggedSegments, saveEntityTagged);
            
            return saveTaggedSegments;
        }

        if (cand.tag.equalsIgnoreCase("org"))
        {
            
            int traceOrg = cand.end;
            if (cand.end < words.length - 1)
            {
                if (words[cand.end + 1].equals("("))
                {
                    for (int i = cand.end + 1; i < words.length; i++)
                        if (words[i].equals(")"))
                        {
                            traceOrg = i;
                            break;
                        }
                }
            }
            int firstOrg = cand.start;
            String extendOrg = "";
            for (int i = cand.start - 1; i >= cand.upper; i--)
            {
                extendOrg = words[i];
                if (functions.isPunctuationsAll(extendOrg)) continue;
                extendOrg = functions.removePunctuations(functions.removeBrackets(extendOrg));
                if (functions.lookup_ORG_INDICATE_NOUN_DICT(extendOrg))
                {
                    firstOrg = i;
                    break;
                }
            }
            if (firstOrg == 0)
            {
                saveTaggedSegments = functions.addToNounPhraseTagged(mode, words, firstOrg, cand.end, traceOrg, line, oldModel, lowConfi, "org", taggedSegmentsLine, saveTaggedSegments, saveEntityTagged);
                return saveTaggedSegments;
            }
            String orgLocDetermine = functions.removePunctuations(functions.removeBrackets(words[firstOrg - 1]));
            if (functions.lookup_LOC_INDICATE_VERB_DICT(orgLocDetermine) || functions.lookup_LOC_INDICATE_ADVERB_DICT(orgLocDetermine))
                saveTaggedSegments = functions.addToNounPhraseTagged(mode, words, firstOrg, cand.end, traceOrg, line, oldModel, lowConfi, "loc", taggedSegmentsLine, saveTaggedSegments, saveEntityTagged);
            else
            {
                saveTaggedSegments = functions.addToNounPhraseTagged(mode, words, firstOrg, cand.end, traceOrg, line, oldModel, lowConfi, "org", taggedSegmentsLine, saveTaggedSegments, saveEntityTagged);
            }
            return saveTaggedSegments;
        }

        if (cand.tag.equalsIgnoreCase("loc"))
        {
            
            String tempLoc = "";

            int firstLoc = cand.start;
            for (int i = cand.start - 1; i >= cand.upper; i--)
            {
                tempLoc = words[i];
                if (functions.isPunctuationsAll(tempLoc)) continue;
                tempLoc = functions.removePunctuations(functions.removeBrackets(tempLoc));
                if (functions.lookup_LOC_INDICATE_NOUN_DICT(tempLoc) || functions.lookup_PER_LOC_AMBIGUITY_DICT(tempLoc))
                {
                    firstLoc = i;
                    break;
                }
            }


            int traceLoc = cand.end;
            if (functions.lookup_PER_LOC_AMBIGUITY_DICT(functions.removeBrackets(words[firstLoc])))
            {
                for (int cc = firstLoc; cc < words.length; cc++)
                {
                    tempLoc = words[cc];
                    if (functions.isPunctuationsAll(tempLoc) || functions.lookup_CONJUNCTION_DICT(functions.removeBrackets(tempLoc))) continue;
                    if (!functions.isAllCapitalized(tempLoc) && !functions.lookup_PER_LOC_AMBIGUITY_DICT(functions.removeBrackets(tempLoc))) break;
                    if (functions.lookup_PER_LOC_AMBIGUITY_DICT(functions.removeBrackets(tempLoc))) continue;
                    saveTaggedSegments = functions.addToNounPhraseTagged(mode, words, cc, cc, cc, line, oldModel, lowConfi, "loc", taggedSegmentsLine, saveTaggedSegments, saveEntityTagged);
                }
                return saveTaggedSegments;
            }
            else
            {
                for (int i = cand.end + 1; i < words.length; i++)
                {
                    tempLoc = words[i];
                    if (functions.isPunctuationsAll(tempLoc) || functions.lookup_CONJUNCTION_DICT(functions.removeBrackets(tempLoc))) continue;
                    if (!functions.lookup_LOC_INDICATE_NOUN_DICT(functions.removeBrackets(tempLoc)) && !functions.isAllCapitalized(tempLoc) && !functions.isNumber(functions.removeBrackets(tempLoc))
                            && !functions.lookup_LOC_INDICATE_ADVERB_DICT(functions.removeBrackets(tempLoc))) break;
                    if (functions.lookup_LOC_INDICATE_NOUN_DICT(functions.removeBrackets(tempLoc)) || functions.lookup_LOC_INDICATE_ADVERB_DICT(functions.removeBrackets(tempLoc))) continue;
                    traceLoc = i;
                }
                saveTaggedSegments = functions.addToNounPhraseTagged(mode, words, firstLoc, cand.end, traceLoc, line, oldModel, lowConfi, "loc", taggedSegmentsLine, saveTaggedSegments, saveEntityTagged);
                
            }
            return saveTaggedSegments;
        }
        return saveTaggedSegments;
        }
        catch (Exception ex)
        {
            System.out.println("error In tagCandidates:" + ex);
            return saveTaggedSegments;
        }
    }

    public static void out(Object o)
    {
        System.out.println(o);
    }
}
