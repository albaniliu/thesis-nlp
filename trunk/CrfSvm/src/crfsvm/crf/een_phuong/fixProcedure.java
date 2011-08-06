/**
 *
 * @author Thien
 */

package crfsvm.crf.een_phuong;

import java.io.*;
import java.util.*;
import java.util.ArrayList;


public class fixProcedure
{
    ArrayList<Integer> elementLines = null; //with an file tagged by the old model, elementLines save the numbers of Words of each
    //line in the file, each elements correspond with the number of words of a line in file
    List entityTaggedByOldModel = null;
    //save all entities  in the tagged file, each member of this list
    //correspond with one line in file (each line has some entities)
    List highConfidenceEntityTagged = null;
    //save entities with highconfidence score in the tagged file, each member of this list
    //correspond with one line in file (each line has some high confi entities)
    List addedEntity = null;
    int[] taggedSegmentsLine = null;
    //save positions of elements in a line that is tagged with high confidence score or found by rules on NP
    int[] taggedSegmentsByOldModel = null;
    //save positions of elements in a that is tagged by the old model
    int[] candidateSegmentsLine = null;
    //include postions in order of candidates
    //ArrayList<entities.primaryEntity> candidateEntities = null;
    Map candidateEntities = null;
    //may positions of new candidates to its correspondent entity
    List nounPhrases = null;
    //contains NP entitis at each line of a NP file (each element correspond with a line)
    List nounPhrasesTagged = null;
    //contains NP entitis at each line of a NP file (each element correspond with a line)
    List nounPhrasesAnalysis  = null;
    //contains NP entitis at each line to analysis of a NP file (each element correspond with a line)
    List extendedLists = null;

    double highConfidence = 0, lowConfidence = 0;
    String fileNameForError = "", fileNameForNP = "", nameFile = "";

    boolean flag = true;

    public fixProcedure(boolean logic)
    {
        nounPhrases = new ArrayList();
        nounPhrasesAnalysis = new ArrayList();
        nounPhrasesTagged = new ArrayList();
        flag = logic;
    }

    private class orgEntityAnalysis
    {
        String abbriviation = "";
        String indicateNoun = "";
        boolean hasName = false;
        ArrayList<String> loc_array_OrgEntity = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        List patterns = new ArrayList();

        public orgEntityAnalysis(String text, boolean mode)
        {
            String textForMode = text;
            int numVnWords = functions.getNumVnWords(text);
            if (numVnWords == 0) return;
            if ((numVnWords == 1) && functions.lookup_ORG_INDICATE_NOUN_DICT(functions.removeBrackets(text.trim())))
                return;
            int[] lastWordIndexs = functions.mapVnWordToChar(text, numVnWords - 1);
            //out(lastWordIndexs[0] + " : " + lastWordIndexs[1]);
            String lastVnWord = text.substring(lastWordIndexs[0], lastWordIndexs[1] + 1);
            if (lastVnWord.endsWith(")]"))
            {
                lastVnWord = lastVnWord.substring(0, lastVnWord.length() - 2);
                if (lastVnWord.startsWith("[("))
                {
                    lastVnWord = lastVnWord.substring(2);
                    //lastVnWord = lastVnWord.substring(0, lastVnWord.length() - 1);
                    lastVnWord = functions.removePunctuations(lastVnWord);
                    if (!lastVnWord.isEmpty())
                    {
                        if (!functions.lookup_LOC_DICT(lastVnWord))
                            abbriviation = lastVnWord;
                        else
                            loc_array_OrgEntity.add(lastVnWord);
                    }
                    text = text.substring(0, lastWordIndexs[0]).trim();
                    numVnWords--;
                }
            }
            else if (lastVnWord.equals(")"))
            {
                String tempAbbri = functions.getVnWord(text, numVnWords - 2);
                if (tempAbbri.isEmpty()) return;
                if (!functions.isPunctuationsAll(tempAbbri))
                {
                    tempAbbri = functions.removeBrackets(tempAbbri);
                    if (tempAbbri.isEmpty()) return;
                    tempAbbri = functions.removePunctuations(tempAbbri);
                    int[] indexsLo = functions.mapVnWordToChar(text, numVnWords - 3);
                    if ((indexsLo[0] < 0) && (indexsLo[1] < 0)) return;
                    if (text.substring(indexsLo[0], indexsLo[1] + 1).equals("("))
                    {
                        if (!functions.lookup_LOC_DICT(tempAbbri))
                            abbriviation = tempAbbri;
                        else
                            loc_array_OrgEntity.add(tempAbbri);
                        text = text.substring(0, indexsLo[0]).trim();
                        numVnWords -= 3;
                    }
                }
            }

            String[] words = functions.getVnWordArray(text);
            String[] wordsNoBrackets = functions.getVnWordArrayNoBrackets(text);
            if (words == null) return;
            int traceFirst = -1;
            for (int run = 0; run < numVnWords; run++)
            {
                String runWord = words[run];
                String noBrackets = functions.removeBrackets(runWord);
                traceFirst = run;
                if (!functions.isPunctuationsAll(runWord) && !functions.lookup_NUMERICAL_PRONOUN_DICT(noBrackets) && !functions.isNumber(noBrackets))
                    break;
            }
            
            String firstWord = words[traceFirst];
            int saveTraceFirst = traceFirst;
            if (functions.isPunctuationsAll(firstWord) || functions.lookup_NUMERICAL_PRONOUN_DICT(functions.removeBrackets(firstWord)) || functions.isNumber(functions.removeBrackets(firstWord))) return;
            
            firstWord = functions.removeBrackets(firstWord);
            if (firstWord.isEmpty()) return;
            
            firstWord = functions.removePunctuations(firstWord);
            
            if (functions.lookup_ORG_INDICATE_NOUN_DICT(firstWord))
            {
                indicateNoun = firstWord.toLowerCase();
                traceFirst++;
            }
            
            if (indicateNoun.isEmpty() && !functions.isAllCapitalized("[" + firstWord + "]")) return;
            int trace = -1;
            
            for (int run = numVnWords - 1; run >= 0; run--)
            {
                String wordRun = words[run];
                String wordRunNoBrackets = functions.removeBrackets(wordRun);
                trace = run;
                if (functions.isPunctuationsAll(wordRun) || functions.lookup_LOC_INDICATE_NOUN_DICT(wordRunNoBrackets) || functions.lookup_LOC_INDICATE_ADVERB_DICT(wordRunNoBrackets))
                    continue;
                else if (functions.lookup_LOC_DICT(wordRunNoBrackets))
                    loc_array_OrgEntity.add(functions.removePunctuations(wordRunNoBrackets));
                else
                    break;
            }
            String mayName = functions.removeBrackets(words[trace]);
            
            if (mayName.isEmpty()) return;
            
            if (functions.isPunctuationsAll("[" + mayName + "]") || functions.lookup_LOC_INDICATE_NOUN_DICT(mayName) || functions.lookup_LOC_INDICATE_ADVERB_DICT(mayName) || functions.lookup_LOC_DICT(mayName))
                return;
            mayName = functions.removePunctuations(mayName);
           
            if (mode)
                addPatterns(mayName, words, wordsNoBrackets, traceFirst, trace);
            else
            {
                abbriviation = "";
                loc_array_OrgEntity.clear();
                patterns.clear();
                names.clear();
                String[] wor = functions.getVnWordArrayNoBrackets(textForMode);
                if (saveTraceFirst >= wor.length) return;
                String[] ad = new String[wor.length -  saveTraceFirst];
                for (int ru = saveTraceFirst; ru < wor.length; ru++)
                    ad[ru - saveTraceFirst] = wor[ru];
                add(ad);
                if (!indicateNoun.isEmpty())
                {
                    if (saveTraceFirst + 1 >= wor.length)
                    {
                        String[] addd = new String[wor.length - saveTraceFirst - 1];
                        for (int ru = saveTraceFirst + 1; ru < wor.length; ru++)
                            addd[ru - saveTraceFirst - 1] = wor[ru];
                        add(addd);
                    }
                }
            }
        }

        private void addPatterns(String mayName, String words[], String[] wordsNoBrackets, int traceFirst, int trace)
        {
            //out("mayName in addPatterns = " + mayName);
            //out("trace = " + trace + " traceFirst = " + traceFirst);
            if (functions.isAllCapitalized("[" + mayName + "]"))
            {
                hasName = true;
                int toSave = -1;
                String temp = "";
                for (int i = trace; i >= traceFirst; i--)
                {
                    toSave = i;
                    temp = words[i];
                    if (functions.isPunctuationsAll(temp))
                        continue;
                    if (functions.isAllCapitalized(temp))
                    {
                        names.add(functions.removePunctuations(functions.removeBrackets(temp)));
                        continue;
                    }
                    break;
                }
                temp = words[toSave];
                if (functions.isPunctuations(temp) || functions.isAllCapitalized(temp))
                    trace = toSave - 1;
                else
                    trace = toSave;
                //trace--;
                //String[] namePatterns = {mayName};
                //add(namePatterns);
                subForAddPatters(mayName, words, wordsNoBrackets, traceFirst, trace);
            }
            else
            {
                hasName = false;
                if (loc_array_OrgEntity.isEmpty()) mayName = "";
                else
                {
                    mayName = loc_array_OrgEntity.get(loc_array_OrgEntity.size() - 1);
                    loc_array_OrgEntity.remove(loc_array_OrgEntity.size() - 1);
                }
                subForAddPatters(mayName, words, wordsNoBrackets, traceFirst, trace);
            }
        }

        private void subForAddPatters(String last, String[] words, String[] wordsNoBrackets, int traceFirst, int trace)
        {
            //out("trace = " + trace + " traceFirst = " + traceFirst);
            String indiAbbri = functions.getAbbriviationForWord(indicateNoun);
            String anotherAbbriIndi = functions.getAbbriviationExceptLast(indicateNoun);
            if (!indicateNoun.isEmpty())
            {
                if ((trace >= traceFirst) && !functions.isAllPunctuationsInSequence(words, traceFirst, trace))
                {
                    for (int runAbbri = traceFirst; runAbbri <= trace; runAbbri++)
                    {
                        List abbrii = functions.getAbbriviations(words, traceFirst, runAbbri);
                        if (abbrii == null) continue;
                        for (int j = 0; j < abbrii.size(); j++)
                        {
                            String[] lij = (String[]) abbrii.get(j);
                            int lenAdd = 1 + lij.length + trace - runAbbri;
                            if (!last.isEmpty())
                                lenAdd++;
                            String[] addAbbri = new String[lenAdd];
                            if (hasName)
                            {
                                System.arraycopy(lij, 0, addAbbri, 1, lij.length);
                                if (trace > runAbbri)
                                    System.arraycopy(wordsNoBrackets, runAbbri + 1, addAbbri, 1 + lij.length, trace - runAbbri);
                                addHasName(addAbbri, indiAbbri, anotherAbbriIndi);
                            }
                            else
                            {
                                if (!last.isEmpty())
                                {
                                    System.arraycopy(lij, 0, addAbbri, 1, lij.length);
                                    if (trace > runAbbri)
                                        System.arraycopy(wordsNoBrackets, runAbbri + 1, addAbbri, 1 + lij.length, trace - runAbbri);
                                    addNotEmpty(addAbbri, indiAbbri, anotherAbbriIndi, last);
                                }
                                else
                                {
                                    System.arraycopy(lij, 0, addAbbri, 1, lij.length);
                                    if (trace > runAbbri)
                                        System.arraycopy(wordsNoBrackets, runAbbri + 1, addAbbri, 1 + lij.length, trace - runAbbri);
                                    addIsEmpty(addAbbri, indiAbbri, anotherAbbriIndi);
                                }
                            }
                        }
                    }
                    int lenAddAll = 2 + trace - traceFirst;
                    if (!last.isEmpty())
                        lenAddAll++;
                    String[] addAll = new String[lenAddAll];
                    if (hasName)
                    {
                        System.arraycopy(wordsNoBrackets, traceFirst, addAll, 1, trace - traceFirst + 1);
                        addHasName(addAll, indiAbbri, anotherAbbriIndi);
                        addLoopCapitalized(addAll, names);
                    }
                    else
                    {
                        if (!last.isEmpty())
                        {
                            System.arraycopy(wordsNoBrackets, traceFirst, addAll, 1, trace - traceFirst + 1);
                            addNotEmpty(addAll, indiAbbri, anotherAbbriIndi, last);
                            addAll[0] = indicateNoun;
                            addAll[addAll.length - 1] = last;
                            addCapitalized(addAll);
                            addLoopCapitalized(addAll, loc_array_OrgEntity);
                        }
                        else
                        {
                            System.arraycopy(wordsNoBrackets, traceFirst, addAll, 1, trace - traceFirst + 1);
                            addIsEmpty(addAll, indiAbbri, anotherAbbriIndi);
                            addLoopCapitalized(addAll, loc_array_OrgEntity);
                        }
                    }
                }

                String[] indiName = {indicateNoun, last};
                if (hasName)
                {
                    addLoop(indiName, names);
                    indiName[0] = indiAbbri;
                    if (indiName[0].length() > 1)
                    {
                        for (int i = 0; i < names.size(); i++)
                        {
                            indiName[1] = names.get(i);
                            add(indiName);
                        }
                    }
                    if (!anotherAbbriIndi.isEmpty())
                    {
                        indiName[0] = anotherAbbriIndi;
                        addLoop(indiName, names);
                    }
                }
                else if (functions.isAllPunctuationsInSequence(words, traceFirst, trace) && !last.isEmpty())
                {
                    addLoopPlusOne(indiName, loc_array_OrgEntity, last);
                    indiName[0] = indiAbbri;
                    if (indiName[0].length() > 1)
                    {
                        indiName[1] = last;
                        add(indiName);
                        for (int i = 0; i < loc_array_OrgEntity.size(); i++)
                        {
                            indiName[1] = loc_array_OrgEntity.get(i);
                            add(indiName);
                        }
                    }
                    if (!anotherAbbriIndi.isEmpty())
                    {
                        indiName[0] = anotherAbbriIndi;
                        addLoopPlusOne(indiName, loc_array_OrgEntity, last);
                    }
                }
            }
            if (hasName)
                for (int i = 0; i < names.size(); i++)
                {
                    String[] addHasName = {names.get(i)};
                    add(addHasName);
                }
        }

        private void addLoop(String[] addAbbri, ArrayList<String> array)
        {
            String abbri = "";
            for (int thro = 0; thro < array.size(); thro++)
            {
                addAbbri[addAbbri.length - 1] = array.get(thro);
                add(addAbbri);
                //addCapitalized(addAbbri);
                abbri = functions.getAbbriviationForWord(addAbbri[addAbbri.length - 1]);
                if (abbri.length() > 1)
                {
                    addAbbri[addAbbri.length - 1] = abbri;
                    add(addAbbri);
                }
            }
        }

        private void addCapitalized(String[] data)
        {
            if (data.length == 0) return;
            String save = "";
            for (int i = 0; i < data.length; i++)
                save += data[i] + " ";
            save = save.trim();
            save = functions.getAbbriviationForWord(save);
            if (save.length() > 1)
            {
                String[] temp = {save};
                add(temp);
            }
        }

        private void addLoopCapitalized(String[] data, ArrayList<String> array)
        {
            data[0] = indicateNoun;
            if (array == null) return;
            if (array.isEmpty())
            {
                addCapitalized(data);
                return;
            }
            if (data.length == 0) return;
            for (int i = 0; i < array.size(); i++)
            {
                data[data.length - 1] = array.get(i);
                addCapitalized(data);
            }
        }

        private void addLoopPlusOne(String[] addAbbri, ArrayList<String> array, String last)
        {
            String abbri = "";
            addAbbri[addAbbri.length - 1] = last;
            add(addAbbri);
            //addCapitalized(addAbbri);
            abbri = functions.getAbbriviationForWord(last);
            if (abbri.length() > 1)
            {
                addAbbri[addAbbri.length - 1] = abbri;
                add(addAbbri);
            }
            addLoop(addAbbri, array);
        }

        private void addHasName(String[] toAdd, String indiAbbri, String anotherAbbriIndi)
        {
            toAdd[0] = indicateNoun;
            addLoop(toAdd, names);
            //addLoopWithCapitalized(toAdd, names);
            toAdd[0] = indiAbbri;
            if (toAdd[0].length() > 1)
                addLoop(toAdd, names);
            if (!anotherAbbriIndi.isEmpty())
            {
                toAdd[0] = anotherAbbriIndi;
                addLoop(toAdd, names);
            }
        }

        private void addNotEmpty(String[] toAdd, String indiAbbri, String anotherAbbriIndi, String last)
        {
            toAdd[0] = indicateNoun;
            addLoopPlusOne(toAdd, loc_array_OrgEntity, last);
            toAdd[0] = indiAbbri;
            if (toAdd[0].length() > 1)
                addLoopPlusOne(toAdd, loc_array_OrgEntity, last);
            if (!anotherAbbriIndi.isEmpty())
            {
                toAdd[0] = anotherAbbriIndi;
                addLoopPlusOne(toAdd, loc_array_OrgEntity, last);
            }
        }

        private void addIsEmpty(String[] toAdd, String indiAbbri, String anotherAbbriIndi)
        {
            toAdd[0] = indicateNoun;
            add(toAdd);
            //addCapitalized(toAdd);
            toAdd[0] = indiAbbri;
            if (toAdd[0].length() > 1)
                add(toAdd);
            if (!anotherAbbriIndi.isEmpty())
            {
                toAdd[0] = anotherAbbriIndi;
                add(toAdd);
            }
        }

        private void add(String[] data)
        {
            if (data == null) return;
            if (data.length == 0) return;
            String[] toAdd = new String[data.length];
            System.arraycopy(data, 0, toAdd, 0, data.length);
            patterns.add(toAdd);
        }
    }

    public void checkOrgAnalysis(String data, boolean mode)
    {
        orgEntityAnalysis org = new orgEntityAnalysis(data, mode);
        out("----------Abbriviation------------------");
        if (org.abbriviation.isEmpty())
            out("Abbriviation is empty!!!");
        else
            out("abbriviation = " + org.abbriviation);
        out("--------------IndicateNoun----------------");
        if (org.indicateNoun.isEmpty())
            out("IndicateNoun is empty!!!!");
        else
            out("indicateNoun = " + org.indicateNoun);
        out("----------------HasName---------------------");
        out(org.hasName);
        out("---------------NAMES-------------------");
        if (org.names.isEmpty())
            out("NAMES is empty!!!!");
        else
            for (int i = 0; i < org.names.size(); i++)
                out(org.names.get(i));
        out("---------------LOCS-------------------");
        if (org.loc_array_OrgEntity.isEmpty())
            out("LOCs is empty!!!!");
        else
            for (int i = 0; i < org.loc_array_OrgEntity.size(); i++)
                out(org.loc_array_OrgEntity.get(i));
        out("---------------PATTERNS-----------------");
        if (org.patterns.size() == 0)
            out("PATTERNS is empty!!!!");
        else
        {
            out("Patterns size = " + org.patterns.size());
            for (int i = 0; i < org.patterns.size(); i++)
            {
                out("++++++++Pattern number : " + (i+1));
                String[] temp = (String[]) org.patterns.get(i);
                for (int j = 0; j < temp.length; j++)
                    out(temp[j]);
            }
        }
    }

    private class locEntityAnalysis
    {
        List patterns = new ArrayList();
        
        public locEntityAnalysis(String text, boolean mode)
        {
            if (text.isEmpty()) return;
            String[] words = functions.getVnWordArray(text);
            String temp = "";
            for (int i = 0; i < words.length; i++)
            {
                temp =  words[i];
                if (functions.isPunctuationsAll(temp)) continue;
                temp = functions.removeBrackets(temp);
                if (temp.isEmpty()) continue;
                temp = functions.removePunctuations(temp);
                if (functions.lookup_LOC_INDICATE_NOUN_DICT(temp) || functions.lookup_NUMERICAL_PRONOUN_DICT(temp)
                        || functions.lookup_LOC_INDICATE_ADVERB_DICT(temp) || functions.lookup_LOC_INDICATE_VERB_DICT(temp)) continue;
                if (functions.isAllCapitalized("[" + temp + "]") && !patterns.contains(temp))
                {
                    patterns.add(temp);
                    if (!mode) return;
                }
            }
        }
    }
    
    public void checkLocAnalysis(String data, boolean mode)
    {
        locEntityAnalysis loc = new locEntityAnalysis(data, mode);
        out("------------------PATTERNS-LOC------------");
        if (loc.patterns.size() == 0)
            out("patterns is empty!!!");
        else
        {
            for (int i = 0; i < loc.patterns.size(); i++)
                out((String)loc.patterns.get(i));
        }
    }

    private class perEntityAnalysis
    {
        List patterns = new ArrayList();

        public perEntityAnalysis(String text, boolean mode)
        {
            
            if (text.isEmpty()) return;
            ArrayList<String> saveNames = new ArrayList<String>();
            String[] words = functions.getVnWordArray(text);
            String temp = "", e = "";
            for (int i = words.length - 1; i >= 0; i--)
            {
                temp = words[i];
                if (functions.isPunctuationsAll(temp)) continue;
                temp = functions.removeBrackets(temp);
                if (temp.isEmpty()) continue;
                if (functions.lookup_PER_INDICATE_NOUN_DICT(temp) || functions.lookup_NUMERICAL_PRONOUN_DICT(temp) || functions.isNumber(temp)) continue;
                if (functions.isAllCapitalized("[" + temp + "]"))
                    saveNames.add(temp);
            }

            if (saveNames.isEmpty()) return;
            for (int i = 0; i < saveNames.size(); i++)
            {
                temp = saveNames.get(i);
                StringTokenizer temTokr = new StringTokenizer(temp, " ");
                String[] temStr = new String[temTokr.countTokens()];
                int count = -1;
                while (temTokr.hasMoreElements())
                {
                    count++;
                    e = functions.removePunctuations(temTokr.nextToken());
                    temStr[count] = e;
                }
                //if (temStr.length != 0) patterns.add(temStr[0]);
                //if (temStr.length > 2) patterns.add(temStr[0] + temStr[temStr.length - 1]);

                if (!mode)
                {
                    for (int j = 0; j < temStr.length; j++)
                        e += temStr[j] + " ";
                    patterns.add(e.trim());
                    patterns.add(temStr[temStr.length - 1]);
                    continue;
                }

                for (int j = 0; j < temStr.length; j++)
                {
                    e = "";
                    for (int k = j; k < temStr.length; k++)
                        if (!temStr[k].isEmpty())
                            e += temStr[k] + " ";
                    patterns.add(e.trim());
                }
                for (int j = 0; j < temStr.length - 1; j++)
                {
                    e = "";
                    for (int k = 0; k <= j; k++)
                        if (!temStr[k].isEmpty())
                            e += temStr[k] + " ";
                    patterns.add(e.trim());
                }
            }
        }
    }

    public void checkPerAnalysis(String data, boolean mode)
    {
        perEntityAnalysis per = new perEntityAnalysis(data, mode);
        out("------------------PATTERNS-PER------------");
        if (per.patterns.size() == 0)
            out("patterns is empty!!!");
        else
        {
            for (int i = 0; i < per.patterns.size(); i++)
                out((String)per.patterns.get(i));
        }
    }

    public void out(Object o)
    {
        System.out.println(o);
    }

    public entities.entity getNextEntity(int line, String data, int posChar)
    {
        if (posChar < 0) posChar = 0;
        entities.entity result = new entities.entity(0, 0, "", 0, 0, 0, 0, "");

        int beginOpenTag = data.indexOf("<", posChar);
        int endOpenTag = data.indexOf(">", beginOpenTag);

        if (beginOpenTag == -1 || endOpenTag == -1)
            return null;

        String temp = data.substring(beginOpenTag + 1, endOpenTag).toLowerCase();
        int index = temp.indexOf(" ");
        String openTag = temp.substring(0, index);
        String confiStr = temp.substring(index + 1);
        confiStr = confiStr.substring(confiStr.indexOf("=") + 1);
        if (confiStr.equalsIgnoreCase("nan")) confiStr = "0";
        //out("confiStr = " + confiStr);
        double confi = Double.parseDouble(confiStr);
        //out("confi in getNextEntity = " + confi);
        //if (!openTag.equalsIgnoreCase("loc") && !openTag.equalsIgnoreCase("org") && !openTag.equalsIgnoreCase("per")) return null;

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
        result.startChar = beginOpenTag;
        result.endChar = closeTagIdx + openTag.length() + 2;
        result.tag = openTag.toLowerCase();
        result.text = textEntity.trim();
        result.confidence = confi;

        return result;
    }

    public entities.entity getNextHighConfidenceEntity(int line, String data, int posChar, double hiConfi)
    {
        do
        {
            entities.entity temp = getNextEntity(line, data, posChar);
            if (temp == null) return null;
            posChar = temp.endChar + 1;
            if (temp.tag.equalsIgnoreCase("org") || temp.tag.equalsIgnoreCase("loc") || temp.tag.equalsIgnoreCase("per"))
            {
                if (temp.confidence >= hiConfi)
                    return temp;
            }
        }
        while(true);
    }

    public void init(double hiConfi, double lowConfi, String inputFile)
    {
        if (elementLines != null)
            elementLines.clear();
        else
            elementLines = new ArrayList<Integer>();

        if (entityTaggedByOldModel != null)
            entityTaggedByOldModel.clear();
        else
            entityTaggedByOldModel = new ArrayList();

        if (highConfidenceEntityTagged != null)
            highConfidenceEntityTagged.clear();
        else
            highConfidenceEntityTagged = new ArrayList();

        if (addedEntity != null)
            addedEntity.clear();
        else
            addedEntity = new ArrayList();

        if (extendedLists != null)
            extendedLists.clear();
        else
            extendedLists = new ArrayList();

        highConfidence = hiConfi;
        lowConfidence = lowConfi;

        BufferedReader fin = null;

        try
        {
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
            String line = "";
            int count = -1;
            File fi = new File(inputFile);
            nameFile = fi.getName();
            while ((line = fin.readLine()) != null)
            {
                line = line.trim();
                if (line.length() == 0) continue;
                count++;
                int num = functions.getNumVnWords(line);
                elementLines.add(num);
                int curPosLo = 0;
                ArrayList<entities.entity> entiAdd = new ArrayList<entities.entity>();
                do
                {
                    entities.entity eAdd = getNextHighConfidenceEntity(count, line, curPosLo, highConfidence);
                    if (eAdd == null) break;
                    curPosLo = eAdd.endChar + 1;
                    entiAdd.add(eAdd);
                    //highConfidenceEntityTagged.add(eAdd);
                }
                while(true);
                highConfidenceEntityTagged.add(entiAdd);

                curPosLo = 0;
                ArrayList<entities.entity> entiOldModel = new ArrayList<entities.entity>();
                do
                {
                    entities.entity addOld = getNextEntity(count, line, curPosLo);
                    if (addOld == null) break;
                    curPosLo = addOld.endChar + 1;
                    entiOldModel.add(addOld);
                }
                while(true);
                entityTaggedByOldModel.add(entiOldModel);
            }

            //out(">>>>>>>>>>>>>>>>>>>>>>>>>> entityOldModel " + entityTaggedByOldModel.size());

            fin.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error in fixProcedure.init: " + ex);
        }
    }

    public int[] findTaggedSegmentLineSure(int line)
    {
        if (line < 0) return null;

        ArrayList<Integer> arrTemp = new ArrayList<Integer>();

        if (flag)
        {
            int[] hi = functions.getSegments(highConfidenceEntityTagged, line);
            if (hi != null)
                for (int i = 0; i < hi.length; i++)
                    arrTemp.add(hi[i]);
        }

        int[] nounPh = functions.getSegments(nounPhrasesTagged, line);
        if (nounPh != null)
            for (int i = 0; i < nounPh.length; i++)
                arrTemp.add(nounPh[i]);

        if (arrTemp.isEmpty()) return null;

        int[] result = new int[arrTemp.size()];

        for (int i = 0; i < arrTemp.size(); i++)
            result[i] = (int) arrTemp.get(i);

        Arrays.sort(result);
        return result;
    }

    public void computeAddedSegmentsLine(int line)
    {
        taggedSegmentsLine = findTaggedSegmentLineSure(line);
    }

    public void computeTaggedSegmentsByOldModel(int line)
    {
        taggedSegmentsByOldModel = functions.getSegments(entityTaggedByOldModel, line);
    }

    public void searchCandidatesForOne(Object o, String[] words, int line, String type)
    {
        try
        {
        //String[] words = getVnWordArray(text);
        if (type.equalsIgnoreCase("org"))
        {
            orgEntityAnalysis org = (orgEntityAnalysis) o;
            if (!org.patterns.isEmpty())
            {
                for (int i = 0; i < org.patterns.size(); i++)
                {
                    String[] pat = (String[]) org.patterns.get(i);
                    searchForCandidateForOnePattern(pat, words, "org", line, org.loc_array_OrgEntity);
                }
            }
            if (!org.abbriviation.isEmpty())
            {
                String[] abbri = {org.abbriviation};
                searchForCandidateForOnePattern(abbri, words, "org", line, org.loc_array_OrgEntity);
            }
            return;
        }
        if (type.equalsIgnoreCase("loc"))
        {
            locEntityAnalysis loc = (locEntityAnalysis) o;
            if (!loc.patterns.isEmpty())
            {
                for (int i = 0; i < loc.patterns.size(); i++)
                {
                    String str = (String) loc.patterns.get(i);
                    String[] pat = {str};
                    searchForCandidateForOnePattern(pat, words, "loc", line, null);
                }
            }
            return;
        }
        if (type.equalsIgnoreCase("per"))
        {
            perEntityAnalysis per = (perEntityAnalysis) o;
            if (!per.patterns.isEmpty())
            {
                for (int i = 0; i < per.patterns.size(); i++)
                {
                    String str = (String) per.patterns.get(i);
                    String[] pat = {str};
                    searchForCandidateForOnePattern(pat, words, "per", line, null);
                }
            }
            return;
        }
        }
        catch (Exception ex)
        {
            out("Error in fix.searchCandidatesForOne: " + ex);
        }
    }
    
    public void checkSearchCandidate(String text, int line, String strPat, String type, boolean mode)
    {
        String[] words = functions.getVnWordArray(text);
        Object o = null;
        if (type.equalsIgnoreCase("org"))
            o = new orgEntityAnalysis(strPat, mode);
        if (type.equalsIgnoreCase("loc"))
            o = new locEntityAnalysis(strPat, mode);
        if (type.equalsIgnoreCase("per"))
            o = new perEntityAnalysis(strPat, mode);
        searchCandidatesForOne(o, words, line, type);
        if (candidateSegmentsLine == null)
        {
            out("No element found!");
            return;
        }
        for (int i = 0; i < candidateSegmentsLine.length; i = i + 2)
        {
            for (int j = candidateSegmentsLine[i]; j <= candidateSegmentsLine[i + 1]; j++)
                System.out.print(words[j] + " ");
            out(" ");
        }
    }

    public void searchForCandidateForOnePattern(String[] patterns, String[] words, String tag, int line, ArrayList<String> loc_org)
    {
        if ((patterns == null) || (words == null)) return;
        if ((patterns.length == 0) || (words.length == 0)) return;
        if (!isExistInArray(words, patterns[0])) return;
        int state = 0, start = 0, end = 0, current = 0;
        String next = functions.removePunctuations(patterns[current]), temp = "";
        for (int i = 0; i < words.length; i++)
        {
            if (functions.checkInTaggedSegment(candidateSegmentsLine, i))
            {
                state = 0;
                current = 0;
                next = functions.removePunctuations(patterns[current]);
                continue;
            }
            temp = words[i];
            if (functions.isPunctuationsAll(temp)) continue;
            temp = functions.removePunctuations(functions.removeBrackets(temp));
            if (temp.isEmpty())
            {
                state = 0;
                current = 0;
                next = functions.removePunctuations(patterns[current]);
                continue;
            }
            if (state == 0)
            {
                if (temp.equalsIgnoreCase(next))
                {
                    state = 1;
                    start = i;
                    current = nextNotPunctuations(patterns, current + 1);
                    if (current == -1)
                    {
                        end = i;
                        if (loc_org != null)
                        {
                            if (!loc_org.isEmpty())
                            {
                                int locTry = extendLocForOrg(end + 1, words, loc_org);
                                if (locTry != -1)
                                {
                                    end = locTry;
                                    i = end;
                                }
                            }
                        }
                        updateCandidateSegmentsLine(start, end, line, tag);
                        state = 0;
                        current = 0;
                        next = functions.removePunctuations(patterns[current]);
                    }
                }
                continue;
            }
            if (state == 1)
            {
                next = functions.removePunctuations(patterns[current]);
                if (temp.equalsIgnoreCase(next))
                {
                    current = nextNotPunctuations(patterns, current + 1);
                    if (current == -1)
                    {
                        end = i;
                        if (loc_org != null)
                        {
                            if (!loc_org.isEmpty())
                            {
                                int locTry = extendLocForOrg(end + 1, words, loc_org);
                                if (locTry != -1)
                                {
                                    end = locTry;
                                    i = end;
                                }
                            }
                        }
                        updateCandidateSegmentsLine(start, end, line, tag);
                        state = 0;
                        current = 0;
                        next = functions.removePunctuations(patterns[current]);
                    }
                }
                else
                {
                    state = 0;
                    current = 0;
                    next = functions.removePunctuations(patterns[current]);
                    if (temp.equalsIgnoreCase(next))
                    {
                        state = 1;
                        start = i;
                        current = nextNotPunctuations(patterns, current + 1);
                    }
                }
            }
        }
    }
    
    public int extendLocForOrg(int end, String[] words, ArrayList<String> loc_org)
    {
        //out("Accessed ExtendLocOrg");
        if (words.length == 0) return -1;
        if (end < 0) end = 0;
        if (end >= words.length) return -1;
        int endNew = end - 1;
        String temp = "";
        for (int i = end; i < words.length; i++)
        {
            if (functions.checkInTaggedSegment(candidateSegmentsLine, i))
                return endNew;
            
            temp = words[i];
            if (functions.isPunctuationsAll(temp))
                continue;
            
            temp = functions.removePunctuations(functions.removeBrackets(temp));
            if (temp.isEmpty()) return endNew;
            
            if (functions.lookup_LOC_INDICATE_NOUN_DICT(temp) || functions.lookup_LOC_INDICATE_ADVERB_DICT(temp))
                continue;
            //out("temp enxtend = " + temp);
            if (functions.isAllCapitalized("[" + temp + "]") && loc_org.contains(temp))
            {
                endNew = i;
                //out("be here");
            }
            else
                break;
        }
        //if (endNew != end)
        //    out("different");
        return endNew;
    }

    public int nextNotPunctuations(String[] patterns, int curpos)
    {
        if (patterns.length == 0) return -1;
        if ((curpos < 0)) curpos = 0;
        if (curpos >= patterns.length) return -1;
        for (int i = curpos; i < patterns.length; i++)
            if (!functions.isPunctuationsAll("[" + patterns[i] + "]"))
                return i;
        return -1;
    }

    public boolean isExistInArray(String[] words, String data)
    {
        String temp = "";
        data = functions.removePunctuations(data);
        for (int i = 0; i < words.length; i++)
        {
            temp = words[i];
            if (functions.isPunctuationsAll(temp)) continue;
            temp = functions.removePunctuations(functions.removeBrackets(temp));
            if (temp.isEmpty()) continue;
            if (temp.equalsIgnoreCase(data) && !functions.checkInTaggedSegment(candidateSegmentsLine, i))
                return true;
        }
        return false;
    }

    //chu y gan candidateSegmentsLine va candidateEntities = null moi khi chuyen sang xet dong moi
    public void updateCandidateSegmentsLine(int start, int end, int line, String tag)
    {
        if (start > end) return;
        if (candidateEntities == null)
            candidateEntities = new HashMap();
        entities.primaryEntity prima = new entities.primaryEntity(start, end, line, tag, "");
        candidateEntities.put("#" + start + "#" + end, prima);
        if (candidateSegmentsLine == null)
        {
            candidateSegmentsLine = new int[2];
            candidateSegmentsLine[0] = start;
            candidateSegmentsLine[1] = end;
        }
        else
        {
            if (candidateSegmentsLine.length == 2)
            {
                if (start > candidateSegmentsLine[1])
                {
                    int[] temp = {candidateSegmentsLine[0], candidateSegmentsLine[1], start, end};
                    candidateSegmentsLine = temp;
                }
                else
                {
                    int[] temp = {start, end, candidateSegmentsLine[0], candidateSegmentsLine[1]};
                    candidateSegmentsLine = temp;
                }
                return;
            }
            if (end < candidateSegmentsLine[0])
            {
                int[] temp = new int[candidateSegmentsLine.length + 2];
                temp[0] = start;
                temp[1] = end;
                System.arraycopy(candidateSegmentsLine, 0, temp, 2, candidateSegmentsLine.length);
                candidateSegmentsLine = temp;
                return;
            }
            int flag = -1;
            for (int i = 1; i <= candidateSegmentsLine.length - 2; i = i + 2)
                if ((start > candidateSegmentsLine[i]) && (start < candidateSegmentsLine[i + 1]))
                {
                    flag = i;
                    break;
                }
            if (flag > 0)
            {
                int[] temp = new int[candidateSegmentsLine.length + 2];
                System.arraycopy(candidateSegmentsLine, 0, temp, 0, flag + 1);
                temp[flag + 1] = start;
                temp[flag + 2] = end;
                for (int i = flag + 3; i < temp.length; i++)
                    temp[i] = candidateSegmentsLine[i - 2];
                System.arraycopy(candidateSegmentsLine, flag + 1, temp, flag + 3, temp.length - flag - 3);
                candidateSegmentsLine = temp;
                return;
            }
            else
            {
                int[] temp = new int[candidateSegmentsLine.length + 2];
                System.arraycopy(candidateSegmentsLine, 0, temp, 0, candidateSegmentsLine.length);
                temp[temp.length - 2] = start;
                temp[temp.length - 1] = end;
                candidateSegmentsLine = temp;
            }
        }
    }

    public ArrayList<entities.entity> fixOneLine(String[] words, int line, boolean mode)
    {
        try{
        ArrayList<entities.entity> result = new ArrayList<entities.entity>();
        computeAddedSegmentsLine(line);
        computeTaggedSegmentsByOldModel(line);
        ArrayList<entities.entity> oldModel = (ArrayList<entities.entity>) entityTaggedByOldModel.get(line);
        int[] saveTaggedSegmentsAdded = null;
        for (int i = 0; i < nounPhrasesAnalysis.size(); i++)
        {
            
            ArrayList<entities.entity> nounPh = (ArrayList<entities.entity>) nounPhrasesAnalysis.get(i);
            
            for (int run = 0; run < nounPh.size(); run++)
            {
                entities.entity en = nounPh.get(run);
                candidateEntities = null;
                candidateSegmentsLine = null;
                
                searchCandidatesForOne(getCorrespondentObject(en, mode), words, line, en.tag);
                saveTaggedSegmentsAdded = matchNounPhraseCombination.tagMultipleApperances(i, run, flag, oldModel, lowConfidence, taggedSegmentsLine, nounPhrases, candidateSegmentsLine, candidateEntities, saveTaggedSegmentsAdded, result, words, line);
                
            }
        }
        
        for (int i = 0; i < highConfidenceEntityTagged.size(); i++)
        {

            ArrayList<entities.entity> hii = (ArrayList<entities.entity>) highConfidenceEntityTagged.get(i);
            
            for (int run = 0; run < hii.size(); run++)
            {
                entities.entity en = hii.get(run);
                
                candidateEntities = null;
                candidateSegmentsLine = null;
                searchCandidatesForOne(getCorrespondentObject(en, mode), words, line, en.tag);
                saveTaggedSegmentsAdded = matchNounPhraseCombination.tagMultipleApperances(i, run, flag, oldModel, lowConfidence, taggedSegmentsLine, nounPhrases, candidateSegmentsLine, candidateEntities, saveTaggedSegmentsAdded, result, words, line);                   
            }
            
        }

         
        return result;
        }
        catch (Exception ex)
        {
            System.out.println("Error in fixProcedure.fixOneLien: " + ex);
            return null;
        }
    }

    public Object getCorrespondentObject(entities.entity en, boolean mode)
    {
        Object o = null;
        if (en.tag.equalsIgnoreCase("org"))
            o = new orgEntityAnalysis(en.text, mode);
        if (en.tag.equalsIgnoreCase("loc"))
            o = new locEntityAnalysis(en.text, mode);
        if (en.tag.equalsIgnoreCase("per"))
            o = new perEntityAnalysis(en.text, mode);
        return o;
    }

    public boolean fixOperation(String fileTaggedByOldModel, String fileNounPhraseChunked, double hiConfi, double lowConfi, boolean mode)
    {
        BufferedReader finAdded = null;
        BufferedReader finExtend = null;

        try
        {
            init(hiConfi, lowConfi, fileTaggedByOldModel);
            nounPhraseAnalysis.analysisInput(flag, fileNounPhraseChunked, nounPhrases, nounPhrasesTagged, nounPhrasesAnalysis, hiConfi, lowConfi, entityTaggedByOldModel, highConfidenceEntityTagged);
            finAdded = new BufferedReader(new InputStreamReader(new FileInputStream(fileTaggedByOldModel), "UTF-8"));
            fileNameForError = fileTaggedByOldModel;
            fileNameForNP = fileNounPhraseChunked;
            String line = "";
            int count = -1;

             
            while ((line = finAdded.readLine()) != null)
            {
                
                line = line.trim();
                if (line.length() == 0) continue;
                count++;
                
                String words[] = functions.getVnWordArray(line);
                
                ArrayList<entities.entity> addedInLine = functions.trimArrayEntities(fixOneLine(words, count, mode));
                
                addedEntity.add(addedInLine);
                
            }

           

            finAdded.close();

            finExtend = new BufferedReader(new InputStreamReader(new FileInputStream(fileTaggedByOldModel), "UTF-8"));
            line = "";
            count = -1;

            while ((line = finExtend.readLine()) != null)
            {
                line = line.trim();
                if (line.length() == 0) continue;
                count++;
                String words[] = functions.getVnWordArray(line);
                ArrayList<entities.primaryEntity> extendInLine = extendAddedEntity.extendLine(words, count, highConfidenceEntityTagged, nounPhrasesTagged, addedEntity);
                extendedLists.add(extendInLine);
            }

            finExtend.close();
            return true;
        }
        catch (Exception ex)
        {
            System.out.println("Error in fixProcedure.fixOperation: " + ex);
            return false;
        }
    }

    public String getStringForIob2(String inputFile)
    {
        String result = "", temp = "";
        BufferedReader fin = null;

        try
        {
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
            String line = "";
            int count = -1;

            while ((line = fin.readLine()) != null)
            {

                line = line.trim();
                if (line.length() == 0) continue;
                count++;
                temp = "";
                String words[] = functions.getVnWordArray(line);
                Map map = new HashMap();
                functions.getMaps(nounPhrasesTagged, count, map);
                functions.getMaps(addedEntity, count, map);
                if (flag)
                    functions.getMaps(highConfidenceEntityTagged, count, map);
                ArrayList<Integer> arr = new ArrayList<Integer>();

                int[] array1 = functions.getSegments(highConfidenceEntityTagged, count);
                int[] array2 = functions.getSegments(nounPhrasesTagged, count);
                if (array2 != null)
                    for (int i = 0; i < array2.length; i++)
                    {
                        arr.add(array2[i]);
                    }
                
                int[] array3 = functions.getSegments(addedEntity, count);
                if (array3 != null)
                    for (int i = 0; i < array3.length; i++)
                    {
                        arr.add(array3[i]);
                    }
                if (flag)
                {
                    if (array1 != null)
                    for (int i = 0; i < array1.length; i++)
                        arr.add(array1[i]);
                }
                else
                {
                    ArrayList<entities.entity> ens = (ArrayList<entities.entity>) highConfidenceEntityTagged.get(count);
                    
                    if (array1 != null)
                    {
                        for (int a = 0; a < array1.length; a = a + 2)
                            if (!functions.checkIfHaveSimilarPoint(array2, array1[a], array1[a + 1]) && !functions.checkIfHaveSimilarPoint(array3, array1[a], array1[a + 1]))
                            {
                                arr.add(array1[a]);
                                arr.add(array1[a + 1]);
                                for (int rr = 0; rr < ens.size(); rr++)
                                {
                                    entities.entity en = ens.get(rr);
                                    if ((en.startVn == array1[a]))
                                    {
                                        
                                        entities.primaryEntity enAdd = new entities.primaryEntity(en.startVn, en.endVn, count, en.tag, "");
                                        map.put("#" + array1[a] + "#" + array1[a + 1], enAdd);
                                        break;
                                    }
                                }
                            }
                    }
                }
                int[] array = new int[arr.size()];
                if (!arr.isEmpty())
                {
                    
                    for (int i = 0; i < arr.size(); i++)
                        array[i] = (int) arr.get(i);
                    Arrays.sort(array);
                    for (int i = 0; i < array.length; i = i + 2)
                    {
                        entities.primaryEntity prima = (entities.primaryEntity) map.get("#" + array[i] + "#" + array[i + 1]);
                        //if (prima == null) out("prima is null");
                        int sta = 0;
                        if (i > 0) sta = array[i - 1] + 1;
                        for (int j = sta; j < array[i]; j++)
                            temp += words[j] + " ";
                        temp += "<" + prima.tag + "> " + words[array[i]] + " ";
                        for (int j = array[i] + 1; j <= array[i + 1]; j++)
                        {
                            temp += words[j] + " ";
                        }
                        temp += "</" + prima.tag + "> ";
                    }
                    
                }
                int te = 0;
                if (!arr.isEmpty()) te = array[array.length - 1] + 1;
                
                for (int i = te; i < words.length; i++)
                    temp += words[i] + " ";
                
                temp = temp.trim();
                result += temp + "\n\n";
            }

            fin.close();
            return result.trim();
        }
        catch (Exception ex)
        {
            System.out.println("Error in fixProcedure.getStringForIob2: " + ex);
            return "";
        }
    }

    public void outList (List li)
    {
        out("##############################################################");
        if (li == null)
        {
            out("Empty!");
            return;
        }
        if (li.isEmpty())
        {
            out("Empty!");
            return;
        }
        for (int i = 0; i < li.size(); i++)
        {
            ArrayList<entities.entity> en = (ArrayList<entities.entity>) li.get(i);
            out("-------Line:" + i + "------------");
            out("<<<<<<The number of line " + i + " is : " + en.size());
            if (en.isEmpty())
            {
                out("line " + i + " is empty");
                continue;
            }
            for (int j = 0; j < en.size(); j++)
            {
                out("--------------Element:" + j + "--------------");
                entities.entity o = en.get(j);
                out("startVn = " + o.startVn);
                out("endVn = " + o.endVn);
                out("tag = " + o.tag);
                out("text = " + o.text);
                out("line = " + o.line);
            }
        }
    }

    public void outListPri (List li)
    {
        out("##############################################################");
        if (li == null)
        {
            out("Empty!");
            return;
        }
        if (li.isEmpty())
        {
            out("Empty!");
            return;
        }
        for (int i = 0; i < li.size(); i++)
        {
            ArrayList<entities.primaryEntity> en = (ArrayList<entities.primaryEntity>) li.get(i);
            out("-------Line:" + i + "------------");
            out("<<<<<<The number of line " + i + " is : " + en.size());
            if (en.isEmpty())
            {
                out("line " + i + " is empty");
                continue;
            }
            for (int j = 0; j < en.size(); j++)
            {
                out("--------------Element:" + j + "--------------");
                entities.primaryEntity o = en.get(j);
                out("startVn = " + o.startVn);
                out("endVn = " + o.endVn);
                out("tag = " + o.tag);
                out("text = " + o.text);
                out("line = " + o.line);
            }
        }
    }

    public static void main(String[] args)
    {
        fixProcedure fix = new fixProcedure(false);

        String ch = "[đường] [Hàm Long], [xã] [Xuân Đỉnh]";
        fix.checkLocAnalysis(ch, false);

        //fix.out(fix.removeNumericalPronoun("........các tập đoàn Đại Phát, Thành Đông..."));
        //String data = "[Nối]  .. .  [tour] [cho] <per confi=999></per> <per confi=999></per>[khách]<per confi=999></per> [MICE] [sang] <loc confi=0.47303526261205286> [Thái Lan] </loc> , "
          //      + "<per confi=0.2829513194007667> [Campuchia] </pe r> ...";
        //data = "<per confi=999></per>   <loc confi=9>    <loc><org confi=2332>   </loc>  <loc confi=3>  <org>   ";
        //data = "[afa] [adddddddda] [aaa]   ";

        /*

        String data = "[đại học] [khoa học] [xã hội] [và] [nhân văn] ( [Hà Nội] )";
        data = "[đại sứ quán] [Hàn Quốc] [tại] [Hà Nội]";
        //data = "[xã] [Tiến Đức] [huyện] [Nội Thanh] [tỉnh] [Hưng Yên] [thành phố] [Hưng Yên] ";
        //data = "[Hà Nội] - [Việt Nam]";
        //data = "[sông] [Sài Gòn]";
        //data = ", , , [một] [23] [các] [công ty] [Long Mạnh] , [Hùng Lan] ...";
        data = ". , , ,,, 54 [,,,tập đoàn] [điện lực] [Việt Nam] [(EVN)]";
        data = "[công ty] [đầu tư] [phát triển] [BKI]  [Anh Vũ]";
        data = "[Bộ] <loc> [giáo dục], <per confi=4>[và]</per> [đào tạo]";
        //data =  "[đầu tư][Trần Anh]";
        //data = "[ĐH] [Quốc gia] [Hà Nội]";
        //data = "[các] , . // //  [....Đường] [,,,,Nguyễn Trãi], [Nguyễn Thiện Thuật]";
        //data = "[tất cả] [các] [ông bà] [Ngô ...Trần     ..Thế.....], [,,,,Vũ    ///Lan Hương], [///Lê ...Bảo Quốc]";
        //fix.out(fix.getAbbriviationExceptLast("...hợp ... *** * ...... ,,, ...??"));
        //data = "[ĐH] [Quốc gia] ( [Hà Nội] )";
        //data= "[ông] [,,Phan ,  ,Văn,  ,  , ,Thành,  //?  ,] , / ;";d
        data = "[Chính Phủ]";
        data = " ,  , [Liên đoàn] [bóng đá] [Việt Nam] [Hà Nội] ( [VFF] )";
        String text = "[Chính] [điều] [này] [làm] [nhức] [đầu] <org> [LĐBĐVN] </org> [và] [những] [LĐ] , , [BĐ] . [Việt Nam] [Hà Nội] [chuyên gia] [trong] [việc] [tìm kiếm] [nhân tài] [cho] <org> [đội tuyển] [VN] </org> [VFF] [Hà Nội]";
        String pat = "[liên đoàn] [bóng đá] [Việt Nam] [Hà Nội] ( [VFF] )";
        text = "<NP> [Henry Ford] [rất] [ưa thích] [các] [loại] [chim] [và] [các] [vũ điệu] [bình dân] . ";
        //pat = "[Henry Ford]";
        //data = "[ông] [Zhang Shi Lie]";
        data = "[tập đoàn] [Trần Anh]";
        data = "[CLB] [Hải Phòng]";
        data = "[viện] [Hàn lâm] [khoa học] [Hoàng gia] [Anh]";
        data = "[bộ]";
        data = "[không quân] [Đức]";
        data = "[Hội]";

        //fix.checkOrgAnalysis(data);
        //fix.checkPerAnalysis(data);
        //return;
        //fix.checkSearchCandidate(text, 0, pat, "org");
        //fix.checkOrgAnalysis(pat);
        //return;
        //for (int i = 0; i < fix.getVnWordArray(data).length; i++)
        //    fix.out(fix.getVnWordArray(data)[i]);
        /*
        String[] patterns = {"công ty", "ĐT&PT...",",", "?", "...", "..Trần Anh]"};
        String[] patterns1 = {"tRần Anh", "đã"};
        String[] words = {"[Sáng nay]", "[công ty]","[công ty]", ".", "?", "[...đt&pt.]", "*", "[Trần Anh]", "[đã]", "[kí kết]", "[gói thầu]", "[với]", "[công ty]", "[đt&PT]", "[Trần Anh]"};
        fix.searchForCandidateForOnePattern(patterns, words, 8, null);
        if (fix.candidateSegmentsLine == null)
        {
            fix.out("Noone exists");
        }
        for (int i = 0; i < fix.candidateSegmentsLine.length; i = i + 2)
        {
            for (int j = fix.candidateSegmentsLine[i]; j <= fix.candidateSegmentsLine[i + 1]; j++)
                System.out.print(words[j]);
            System.out.println(" ");
        }
         *
         */
        //fix.out("---------------another--------");
        //fix.searchForCandidateForOnePattern(patterns1, words, 8,);
        //if (fix.candidateSegmentsLine == null)
        //{
        //    fix.out("Noone exists");
        //    return;
        //}
        //for (int i = 0; i < fix.candidateSegmentsLine.length; i = i + 2)
        //{
         //   for (int j = fix.candidateSegmentsLine[i]; j <= fix.candidateSegmentsLine[i + 1]; j++)
         //       System.out.print(words[j]);
         //   System.out.println(" ");
        //}
        //fix.out(fix.lookup_PER_INDICATE_NOUN_DICT("ông"));
        //fix.out(fix.removePunctuations(" ........ . , , , ?   "));
        //data.trim();
        //String result = fix.removeTags(data);
        //int Words = fix.getNumVnWords(data);
        //String[] segs = {"và", ".", ".", ")", "&", "?", "[đầu tư]",  "[và]", "[phát triển]", ".", "và", ",", ".", "(",  "[NDDA]"};
        //String[] segs = {".", ","};
        //int[] map = fix.mapVnWordToChar(data, 10);
        //if ((map[0] < 0) && (map[1] < 0)) System.out.println("=-1");
        //else
        //System.out.println(fix.getAbbriviation(",đầu ,,,,tư ,, ,, ,phát ,,,,,triển..."));
        //System.out.println(fix.isAllLetterCappitalized("#a@@ADBA"));
        /*
        List li = fix.getAbbriviations(segs, 0, 2);
        if (li == null)
        {
            System.out.println("Null");
            return;
        }
        int size = li.size();
        for (int i = 0; i < size; i++)
        {
            String[] tempi = (String[]) li.get(i);
            System.out.println("Phan tu thu : " + (i + 1));
            for (int j = 0; j < tempi.length; j++)
                System.out.println(tempi[j]);
        }
        String[] arr = fix.getAbbriviationForWordOfLengthTow(".....cong . . . . ,,,, ");
        if (arr == null)
        {
            System.out.println("arr is null");
            return;
        }
        System.out.println(arr[0] + ":" + arr[1]);
         *
         */

        //calculateConfidence confi = new calculateConfidence();
        //confi.init("input.txt.wseg.confi");
        //fix.init(0.9, 0.7, "input.txt.wseg");
        //nounPhraseAnalysis.analysisInput("testSegment.txt", fix.nounPhrases, fix.nounPhrasesTagged, fix.nounPhrasesAnalysis, 0.9, 0.7, fix.entityTaggedByOldModel, null);
        /*

        fix.fixOperation("input.txt.wseg", "Dung1.txt", 0.9, 0.8, true);
        fix.outListPri(fix.addedEntity);
        fix.out(fix.getStringForIob2("input.txt.wseg"));
         *
         */
    }

}

