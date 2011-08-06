/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Thien
 */
public class functions
{
    public static ArrayList<String> loc_dict = null;
    public static ArrayList<String> org_indicate_noun_dict = null;
    public static ArrayList<String> loc_indicate_noun_dict = null;
    public static ArrayList<String> loc_indicate_verb_dict = null;
    public static ArrayList<String> loc_indicate_adverb_dict = null;
    public static ArrayList<String> per_indicate_noun_dict = null;
    public static ArrayList<String> define_dict = null;
    public static ArrayList<String> numerical_pronoun_dict = null;
    public static ArrayList<String> addition_dict = null;
    public static ArrayList<String> conjunction_dict = null;
    public static ArrayList<String> per_loc_ambiguity_dict = null;

    public static String lexiconStorage = ".\\src\\een_phuong\\lexiconStorage\\";
    public static String loc_dict_path = lexiconStorage + "LOC-DICT.txt";
    public static String org_indicate_noun_dict_path = lexiconStorage + "ORG- INDICATE-NOUN-DICT.txt";
    public static String loc_indicate_noun_dict_path = lexiconStorage + "LOC-INDICATE-NOUN-DICT.txt";
    public static String loc_indicate_verb_dict_path = lexiconStorage + "LOC-INDICATE-VERB-DICT.txt";
    public static String loc_indicate_adverb_dict_path = lexiconStorage + "LOC-INDICATE-ADVERB-DICT.txt";
    public static String per_indicate_noun_dict_path = lexiconStorage + "PER-INDICATE-NOUN-DICT.txt";
    public static String define_dict_path = lexiconStorage + "DEFINE-DICT.txt";
    public static String numerical_pronoun_dict_path = lexiconStorage + "NUMERICAL-PRONOUN-DICT.txt";
    public static String addition_dict_path = lexiconStorage + "ADDITION-DICT.txt";
    public static String conjunction_dict_path = lexiconStorage + "CONJUNCTION-DICT.txt";
    public static String per_loc_ambiguity_dict_path = lexiconStorage + "PER-LOC-AMBIGUITY.txt";

    public static String[] punctuations = {".", "," , "!", "(", ")", "[", "]", "{", "}", "?", "@", "\"", "-", "/", "...", ":", "'", ";", "*", "+" , "#",
        "%", "^", "&", "=", "|", "~", "`"};

    public static String removeBrackets(String data)
    {
        data = data.trim();
        if (data.isEmpty()) return "";
        if (!data.startsWith("[") || !data.endsWith("]")) return "";
        return data.substring(1, data.length() - 1).trim();
    }

    public static boolean isAllCapitalized(String data)
    {
        data = removeBrackets(data);
        if (data.isEmpty()) return false;
        StringTokenizer dataTokr = new StringTokenizer(data, " ");
        String element = "";
        while (dataTokr.hasMoreElements())
        {
            element = removePunctuations(dataTokr.nextToken());
            if (element.isEmpty()) return false;
            char fistChar = element.charAt(0);
            if (!Character.isUpperCase(fistChar))
                return false;
        }
        return true;
    }

    public static boolean isAllLetterCappitalized(String data)
    {
        data = data.trim();
        if (data.isEmpty()) return false;
        for (int i = 0; i < data.length(); i++)
        {
            char ch = data.charAt(i);
            if (!Character.isLetter(ch) && (ch != '-')) continue;
            if (!Character.isUpperCase(ch)) return false;
        }
        return true;
    }

    public static boolean isPunctuations(String data)
    {
        if (data.isEmpty()) return false;
        for (int i = 0; i < punctuations.length; i++)
            if (punctuations[i].equals(data))
                return true;
        return false;
    }

    public static boolean isPunctuationsAll(String data)
    {
        if (isPunctuations(data)) return true;
        data = removeBrackets(data);
        if (data.isEmpty()) return false;
        String ch = "";
        for (int i = 0; i < data.length(); i++)
        {
            ch = data.substring(i, i + 1);
            if (!isPunctuations(ch) && !(ch.equals(" ")))
                return false;
        }
        return true;
    }

    public static boolean isAllPunctuationsInSequence(String[] words, int start, int end)
    {
        if (words == null) return true;
        if (words.length == 0) return true;
        if (start < 0) start = 0;
        if (end >= words.length) end = words.length - 1;
        if (start > end) return true;
        for (int i = start; i <= end; i++)
            if (!isPunctuationsAll(words[i]))
                return false;
        return true;
    }

    public static boolean isNumber(String data)
    {
        if (data.isEmpty()) return false;
        char ch = ' ';
        for (int i = 0; i < data.length(); i++)
        {
            ch = data.charAt(i);
            if (!Character.isDigit(ch) && (ch != '.') && (ch != ','))
                return false;
        }
        return true;
    }

    public static String getAbbriviationForWord(String data)
    {
        if (data.isEmpty()) return "";
        String result = "", temp = "";
        String ch = "";
        StringTokenizer dataTokr = new StringTokenizer(data, " ");
        while (dataTokr.hasMoreElements())
        {
            temp = dataTokr.nextToken();
            for (int i = 0; i < temp.length(); i++)
            {
                ch = temp.substring(i, i + 1);
                if (isPunctuations(ch)) continue;
                result += ch.toUpperCase();
                break;
            }
        }
        return result;
    }

    public static String getAbbriviationExceptLast(String data)
    {
        if (data.isEmpty()) return "";
        String result = "";
        String temp = "", ch = "";
        StringTokenizer dataTokr = new StringTokenizer(data, " ");
        int count = 0, numTokr = dataTokr.countTokens(), accepted = 0;
        if (numTokr == 0) return "";
        while (dataTokr.hasMoreElements())
        {
            temp = dataTokr.nextToken();
            count++;
            int trace = -1;
            for (int i = 0; i < temp.length(); i++)
            {
                trace = i;
                ch = temp.substring(i, i + 1);
                if (isPunctuations(ch)) continue;
                break;
            }
            if ((trace == temp.length() - 1) && isPunctuations(ch))
                continue;
            accepted++;
            if (count < numTokr)
                result += ch.toUpperCase();
            else
                result += removePunctuations(temp.substring(trace));
        }

        if (accepted <= 1) result = "";
        return result;
    }

     public static List getAbbriviations(String[] data, int start, int end)
     {
        if ((start > end) || (start < 0) || (end >= data.length)) return null;
        ArrayList<String> save = new ArrayList<String>();
        String temp = "", toAdd = "", preMark = "";
        boolean isCross = false;
        for (int i = start; i <= end; i++)
        {
            temp = data[i];
            if (!isPunctuations(temp))
                temp = removeBrackets(temp);
            if (temp.length() == 0) continue;
            //int trace = -1;
            //for (int run = 0; run < temp.length(); run++)
            //{
            //    String str = temp.substring(run, run + 1);
            //    if(!isPunctuations(str))
            //    {
            //        trace = run;
            //        break;
            //    }
            //}
            temp = removePunctuations(temp);
            if (temp.isEmpty())
            {
                if (i == end)
                {
                    if (toAdd.endsWith("&&")) continue;
                    if (toAdd.length() > 0)
                    {
                        if (toAdd.startsWith("&"))
                        {
                            toAdd = toAdd.substring(1);
                            if (toAdd.length() > 0)
                                save.add("và");
                        }
                    }
                    if (toAdd.length() > 0)
                    {
                        if (toAdd.endsWith(("&")))
                            toAdd = toAdd.substring(0, toAdd.length() - 1);
                    }
                    if (toAdd.length() > 0)
                        save.add(toAdd);
                }
                continue;
            }
            //temp = temp.substring(trace);
            if (temp.equalsIgnoreCase("và") && !isCross) continue;
            isCross = true;
            if (temp.equalsIgnoreCase("và"))
                toAdd += "&";
            else if (!isAllLetterCappitalized(temp))
                toAdd += getAbbriviationForWord(temp);
            else
            {
                if (preMark.equalsIgnoreCase("và"))
                {
                    toAdd = toAdd.substring(0, toAdd.length() - 1);
                    if ((toAdd.length() > 0))
                    {
                        if (toAdd.startsWith("&"))
                        {
                            toAdd = toAdd.substring(1);
                            save.add("và");
                        }
                    }
                    if (toAdd.length() > 0)
                    {
                        save.add(toAdd);
                        save.add("và");
                    }
                }
                else
                {
                    if ((toAdd.length() > 0))
                    {
                        if (toAdd.startsWith("&"))
                        {
                            toAdd = toAdd.substring(1);
                            save.add("và");
                        }
                        if (toAdd.length() > 0)
                            save.add(toAdd);
                    }
                }
                save.add(temp);
                toAdd = "";
            }
            if (i == end)
            {
                if (toAdd.endsWith("&&")) continue;
                if (toAdd.length() > 0)
                {
                    if (toAdd.startsWith("&"))
                    {
                        toAdd = toAdd.substring(1);
                        if (toAdd.length() > 0)
                            save.add("và");
                    }
                }
                if (toAdd.length() > 0)
                {
                    if (toAdd.endsWith(("&")))
                        toAdd = toAdd.substring(0, toAdd.length() - 1);
                }
                if (toAdd.length() > 0)
                    save.add(toAdd);
            }
            preMark = temp;
        }
        return getAbbriAfterProcessing(save);
    }

     public static List getAbbriAfterProcessing(ArrayList<String> data)
    {
        if (data == null) return null;
        if (data.isEmpty()) return null;
        String[] dataArray = new String[data.size()];
        data.toArray(dataArray);
        List result = new ArrayList();
        result.add(dataArray);
        for (int i = 0; i < dataArray.length - 1; i++)
        {
            if (!dataArray[i].equals("và") && !dataArray[i + 1].equals("và"))
            {
                String[] dataAdd =new String[dataArray.length - 1];
                for (int j = 0; j < i; j++)
                    dataAdd[j] = dataArray[j];
                dataAdd[i] = dataArray[i] + "&" + dataArray[i + 1];
                for (int j = i + 1; j < dataAdd.length; j++)
                    dataAdd[j] = dataArray[j + 1];
                result.add(dataAdd);
            }
        }
        return result;
    }

    public static int[] mapVnWordToChar(String data, int pos)
    {
        int[] result = {-1, -1};
        int numVnWord = getNumVnWords(data);
        if ((numVnWord <= 0) ||(pos < 0) || (pos > numVnWord - 1)) return result;

        int curPosChar = 0, nextOpenBracket = 0, nextCloseBraket = 0, curNumVn = 0, oldNumVn = 0;
        do
        {
            nextOpenBracket = data.indexOf("[", curPosChar);
            if (nextOpenBracket < 0) break;
            String before = data.substring(curPosChar, nextOpenBracket);
            oldNumVn = curNumVn;
            curNumVn += getNumVnWords(before);
            if (pos < curNumVn)
            {
                int[] temp = mapVnWordToCharNoBrackets(before, pos - oldNumVn);
                result[0] = curPosChar + temp[0];
                result[1] = curPosChar + temp[1];
                return result;
            }
            nextCloseBraket = data.indexOf("]", nextOpenBracket);
            if (nextCloseBraket < 0) return result;
            oldNumVn = curNumVn;
            curNumVn++;
            if (pos < curNumVn)
            {
                result[0] = nextOpenBracket;
                result[1] = nextCloseBraket;
                return result;
            }
            curPosChar = nextCloseBraket + 1;
        }
        while(true);
        if (curPosChar == data.length()) return result;
        String after = data.substring(curPosChar);
        oldNumVn = curNumVn;
        curNumVn += getNumVnWords(after);
        if (pos < curNumVn)
        {
            int[] temp = mapVnWordToCharNoBrackets(after, pos - oldNumVn);
            result[0] = curPosChar + temp[0];
            result[1] = curPosChar + temp[1];
            return result;
        }
        return result;
    }

    public static int getNumVnWords(String data)
    {
        data = removeTags(data);
        VnStringTokenizer dataTokrAll = new VnStringTokenizer(data, " ");
        int numVnWord = 0;
        while (dataTokrAll.hasMoreTokens())
        {
            numVnWord++;
            dataTokrAll.nextToken();
        }
        return numVnWord;
    }

    public static String[] getVnWordArray(String data)
    {
        int num = getNumVnWords(data);
        if (num <= 0) return null;
        data = removeTags(data);
        String[] result = new String[num];
        VnStringTokenizer dataTokrAll = new VnStringTokenizer(data, " ");
        int count = -1;
        while (dataTokrAll.hasMoreTokens())
        {
            count++;
            result[count] = dataTokrAll.nextToken();
            if (!isPunctuationsAll("[" + result[count] + "]"))
            {
                result[count] = "[" + removePunctuations(result[count]) + "]";
                continue;
            }
            if (!isPunctuations(result[count]))
                result[count] = "[" + result[count] + "]";
        }
        return result;
    }

    public static String[] getVnWordArrayNoBrackets(String data)
    {
        int num = getNumVnWords(data);
        if (num <= 0) return null;
        data = removeTags(data);
        String[] result = new String[num];
        VnStringTokenizer dataTokrAll = new VnStringTokenizer(data, " ");
        int count = -1;
        while (dataTokrAll.hasMoreTokens())
        {
            count++;
            result[count] = dataTokrAll.nextToken();
            if (!isPunctuationsAll("[" + result[count] + "]"))
                result[count] = removePunctuations(result[count]);
        }
        return result;
    }

    public static String getVnWord(String data, int pos)
    {
        String[] words = getVnWordArray(data);
        if ((words == null) || (pos < 0)) return "";
        if (pos >= words.length) return "";
        return words[pos];
    }

    public static int[] mapVnWordToCharNoBrackets(String data, int pos)
    {
        int[] result = {-1, -1};
        if (pos < 0) return result;
        char cur = ' ';
        int curNumVnWord = 0;
        boolean state = true, toExit = false, isTag = false;
        for (int i = 0; i < data.length(); i++)
        {
            cur = data.charAt(i);
            if (cur == ' ')
            {
                if (toExit)
                {
                    result[1] = i - 1;
                    return result;
                }
                if (!isTag)
                    state = true;
            }
            else if(state)
            {
                if (cur == '<')
                {
                    isTag = true;
                    continue;
                }
                if (isTag)
                {
                    if (cur == '>')
                        isTag = false;
                    continue;
                }
                curNumVnWord++;
                if (pos == curNumVnWord - 1)
                {
                    result[0] = i;
                    toExit = true;
                }
                state = false;
            }
            else if (!state)
            {
                if (cur == '<')
                {
                    isTag = true;
                    if (toExit)
                    {
                        result[1] = i - 1;
                        return result;
                    }
                }
                if (cur == '>')
                {
                    state = true;
                    isTag = false;
                }
            }
            if ((i == data.length() - 1) && toExit)
            {
                result[1] = i;
                return result;
            }
        }
        return result;
    }

    public static String removeTags(String data)
    {
        int nextOpenBracket = 0, nextCloseBraket = 0;
        do
        {
            nextOpenBracket = data.indexOf("<");
            if (nextOpenBracket < 0) break;
            nextCloseBraket = data.indexOf(">", nextOpenBracket);
            if (nextCloseBraket < 0) return data;
            data = data.substring(0, nextOpenBracket) + " " + data.substring(nextCloseBraket + 1);
        }
        while(true);
        data = data.replace("   ", " ");
        data = data.replace("  ", " ");
        return data;
    }

    public static String removePunctuations(String data)
    {
        data = data.trim();
        if (data.isEmpty()) return "";
        int start = -1, end = -1;
        String ch = "";
        for (int i = 0; i < data.length(); i++)
        {
            start = i;
            ch = data.substring(i, i + 1);
            if (!isPunctuations(ch) && !ch.equals(" ")) break;
        }
        for (int i = data.length() - 1; i >= 0; i--)
        {
            end = i;
            ch = data.substring(i, i + 1);
            if (!isPunctuations(ch) && !ch.equals(" ")) break;
        }
        if (start > end) return "";
        if ((start == end) && (isPunctuations(data.substring(start, start + 1)))) return "";
        return data.substring(start, end + 1);
    }

    public static void loadLexicon(String lexicon, ArrayList<String> array)
    {
        BufferedReader fin = null;
        try
        {
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(lexicon), "UTF-8"));
            String line = "";
            while ((line = fin.readLine()) != null)
            {
                if (line.length() == 0) continue;
                array.add(line.trim().toLowerCase());
            }
            fin.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.loadLexicon: " + ex);
        }
    }

    public static boolean lookup_LOC_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (loc_dict == null)
            {
                loc_dict = new ArrayList<String>();
                loadLexicon(loc_dict_path, loc_dict);
            }
            if (word.isEmpty()) return false;
            return (loc_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_LOC_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_ORG_INDICATE_NOUN_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (org_indicate_noun_dict == null)
            {
                org_indicate_noun_dict = new ArrayList<String>();
                loadLexicon(org_indicate_noun_dict_path, org_indicate_noun_dict);
            }
            if (word.isEmpty()) return false;
            return (org_indicate_noun_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_ORG_INDICATE_NOUN_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_LOC_INDICATE_NOUN_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (loc_indicate_noun_dict == null)
            {
                loc_indicate_noun_dict = new ArrayList<String>();
                loadLexicon(loc_indicate_noun_dict_path, loc_indicate_noun_dict);
            }
            if (word.isEmpty()) return false;
            return (loc_indicate_noun_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_LOC_INDICATE_NOUN_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_LOC_INDICATE_VERB_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (loc_indicate_verb_dict == null)
            {
                loc_indicate_verb_dict = new ArrayList<String>();
                loadLexicon(loc_indicate_verb_dict_path, loc_indicate_verb_dict);
            }
            if (word.isEmpty()) return false;
            return (loc_indicate_verb_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_LOC_INDICATE_VERB_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_LOC_INDICATE_ADVERB_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (loc_indicate_adverb_dict == null)
            {
                loc_indicate_adverb_dict = new ArrayList<String>();
                loadLexicon(loc_indicate_adverb_dict_path, loc_indicate_adverb_dict);
            }
            if (word.isEmpty()) return false;
            return (loc_indicate_adverb_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_LOC_INDICATE_ADVERB_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_PER_INDICATE_NOUN_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (per_indicate_noun_dict == null)
            {
                per_indicate_noun_dict = new ArrayList<String>();
                loadLexicon(per_indicate_noun_dict_path, per_indicate_noun_dict);
            }
            if (word.isEmpty()) return false;
            return (per_indicate_noun_dict.contains(word.toLowerCase()) || lookup_PER_LOC_AMBIGUITY_DICT(word));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_PER_INDICATE_NOUN_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_DEFINE_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (define_dict == null)
            {
                define_dict = new ArrayList<String>();
                loadLexicon(define_dict_path, define_dict);
            }
            if (word.isEmpty()) return false;
            return (define_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_DEFINE_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_NUMERICAL_PRONOUN_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (numerical_pronoun_dict == null)
            {
                numerical_pronoun_dict = new ArrayList<String>();
                loadLexicon(numerical_pronoun_dict_path, numerical_pronoun_dict);
            }
            if (word.isEmpty()) return false;
            return (numerical_pronoun_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_NUMERICAL_PRONOUN_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_ADDITION_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (addition_dict == null)
            {
                addition_dict = new ArrayList<String>();
                loadLexicon(addition_dict_path, addition_dict);
            }
            if (word.isEmpty()) return false;
            return (addition_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_ADDITION_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_CONJUNCTION_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (conjunction_dict == null)
            {
                conjunction_dict = new ArrayList<String>();
                loadLexicon(conjunction_dict_path, conjunction_dict);
            }
            if (word.isEmpty()) return false;
            return (conjunction_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_CONJUNCTION_DICT: " + ex);
            return false;
        }
    }

    public static boolean lookup_PER_LOC_AMBIGUITY_DICT(String word)
    {
        word = functions.removePunctuations(word);
        try
        {
            if (per_loc_ambiguity_dict == null)
            {
                per_loc_ambiguity_dict = new ArrayList<String>();
                loadLexicon(per_loc_ambiguity_dict_path, per_loc_ambiguity_dict);
            }
            if (word.isEmpty()) return false;
            return (per_loc_ambiguity_dict.contains(word.toLowerCase()));
        }
        catch (Exception ex)
        {
            System.out.println("Error in functions.lookup_PER_LOC_AMBIGUITY_DICT: " + ex);
            return false;
        }
    }

    public static boolean checkInTaggedSegment(int[] segments, int check)
    {
        if (segments == null) return false;
        if ((segments[0] > check) || (segments[segments.length - 1] < check)) return false;

        for (int i = 0; i < segments.length; i = i + 2)
            if ((segments[i] <= check) && (check <= segments[i + 1])) return true;
        return false;
    }

    public static boolean checkIfHaveSimilarPoint(int[] segments, int start, int end)
    {
        if (start > end) return false;
        for (int i = start; i <= end; i++)
        {
            if (checkInTaggedSegment(segments, i)) return true;
        }
        return false;
    }

    public static int[] addSegmentToArray(int[] initial, int start, int end)
    {
        if (initial == null)
        {
            int[] temp = new int[] {start, end};
            return temp;
        }
        ArrayList<Integer> array = new ArrayList<Integer>();
        for (int i = 0; i < initial.length; i++)
            array.add(initial[i]);
        array.add(start);
        array.add(end);

        int[] result = new int[array.size()];

        for (int i = 0; i < array.size(); i++)
            result[i] = (int) array.get(i);

        Arrays.sort(result);
        return result;
    }

    public static double computeConfiForNounPhrase(boolean mode, ArrayList<entities.entity> oldModel, String tag, int start, int end)
    {
        if (!mode) return 0;
        if (oldModel == null) return 0;
        if (oldModel.isEmpty()) return 0;
        for (int i = 0; i < oldModel.size(); i++)
        {
            entities.entity en = oldModel.get(i);
            if ((en.startVn != start) || (en.endVn != end)) continue;
            if (!tag.equalsIgnoreCase(en.tag)) continue;
            return en.confidence;
        }
        return 0;
    }

    public static String getTextInArray(String[] words, int start, int end)
    {
        if (words.length == 0) return "";
        if (start < 0) start = 0;
        if (end >= words.length) end = words.length - 1;
        if (start > end) return "";
        String temp = "";
        for (int i = start; i <= end; i++)
            temp += words[i] + " ";
        return temp.trim();
    }

    public static int[] addToNounPhraseTagged(boolean mode, String[] words, int start, int end, int traceEnd, int line, ArrayList<entities.entity> oldModel, double lowConfi, String tag,
            int[] taggedSegments, int[] savedSegments, ArrayList<entities.entity> toNounPhraseTagged)
    {
        if (words == null) return savedSegments;
        if ((start < 0) || (end >= words.length) || (traceEnd >= words.length) || (end > traceEnd)) return savedSegments;
        double valueCon = computeConfiForNounPhrase(mode, oldModel, tag, start, traceEnd);
        String textIn = getTextInArray(words, start, traceEnd);
        if (textIn.isEmpty()) return savedSegments;
        if (!checkIfHaveSimilarPoint(savedSegments, start, traceEnd) && !checkIfHaveSimilarPoint(taggedSegments, start, traceEnd) && (valueCon < lowConfi))
        {
            entities.entity en = new entities.entity(0, 0, textIn, 0, start, traceEnd, line, tag);
            toNounPhraseTagged.add(en);
            savedSegments = addSegmentToArray(savedSegments, start, traceEnd);
            return savedSegments;
        }
        if (end == traceEnd) return savedSegments;
        valueCon = computeConfiForNounPhrase(mode, oldModel, tag, start, end);
        if (!checkIfHaveSimilarPoint(savedSegments, start, end) && !checkIfHaveSimilarPoint(taggedSegments, start, end) && (valueCon < lowConfi))
        {
            entities.entity en = new entities.entity(0, 0, textIn, 0, start, end, line, tag);
            toNounPhraseTagged.add(en);
            savedSegments = addSegmentToArray(savedSegments, start, end);
        }
        return savedSegments;
    }

    public static void addToNounPhraseTaggedForNounPhrase(boolean mode, String[] words, int start, int end, int traceEnd, int line, ArrayList<entities.entity> oldModel, double lowConfi, String tag,
            int[] taggedSegments, int[] savedSegments, ArrayList<entities.entity> toNounPhraseTagged)
    {
        if (words == null) return;
        if ((start < 0) || (end >= words.length) || (traceEnd >= words.length) || (end > traceEnd)) return;
        double valueCon = computeConfiForNounPhrase(mode, oldModel, tag, start, traceEnd);
        //if (line == 0) out("confi = " + valueCon);
        String textIn = getTextInArray(words, start, traceEnd);
        if (textIn.isEmpty()) return;
        if (!checkInTaggedSegment(savedSegments, start))
        {
            if (!checkInTaggedSegment(savedSegments, traceEnd) && (valueCon < lowConfi))
            {
                entities.entity en = new entities.entity(0, 0, textIn, 0, start, traceEnd, line, tag);
                toNounPhraseTagged.add(en);
                savedSegments = addSegmentToArray(savedSegments, start, traceEnd);
            }
            else if (end != traceEnd)
            {
                valueCon = computeConfiForNounPhrase(mode, oldModel, tag, start, end);
                textIn = getTextInArray(words, start, end);
                if (textIn.isEmpty()) return;
                if (!checkInTaggedSegment(savedSegments, end) && (valueCon < lowConfi))
                {
                    entities.entity en = new entities.entity(0, 0, textIn, 0, start, end, line, tag);
                    toNounPhraseTagged.add(en);
                    savedSegments = addSegmentToArray(savedSegments, start, end);
                }
            }
        }
        //if (line == 3)
        //{
        //    if (savedSegments == null)
         //       out("add in 3 : = null");
         //   else
        //    {
        //        out("addd in 3");
        //        for (int i = 0; i < savedSegments.length; i++)
        //            out("add in 3: " + i + " = " + savedSegments[i]);
        //    }
       // }
        return;
    }

    public static void out(Object o)
    {
        System.out.println(o);
    }

    public static boolean hasALetterCaptialized(String data)
    {
        data = data.trim();
        if (data.isEmpty()) return false;
        char ch = ' ';
        for (int i = 0; i < data.length(); i++)
        {
            ch = data.charAt(i);
            if (Character.isUpperCase(ch)) return true;
        }
        return false;
    }

    public static int[] getSegments(List li, int line)
    {
        if (li == null) return null;
        if ((line < 0) || (line >= li.size())) return null;
        ArrayList<Integer> arrTemp = new ArrayList<Integer>();
        ArrayList<entities.entity> oldModel = (ArrayList<entities.entity>) li.get(line);
        for (int i = 0; i < oldModel.size(); i++)
        {
            entities.entity en = oldModel.get(i);
            int startVn = en.startVn;
            int endVn = en.endVn;
            arrTemp.add(startVn);
            arrTemp.add(endVn);
        }

        if (arrTemp.isEmpty()) return null;

        int[] result = new int[arrTemp.size()];

        for (int i = 0; i < arrTemp.size(); i++)
            result[i] = (int) arrTemp.get(i);

        Arrays.sort(result);
        return result;
    }

    public static void getMaps(List li, int line, Map map)
    {
        //if (map == null)
        //    map = new HashMap();
        if (li == null) return;
        if ((line < 0) || (line >= li.size())) return;
        ArrayList<entities.entity> ens = (ArrayList<entities.entity>) li.get(line);
        for (int i = 0; i < ens.size(); i++)
        {
            entities.entity en = ens.get(i);
            int startVn = en.startVn;
            int endVn = en.endVn;
            entities.primaryEntity prima = new entities.primaryEntity(startVn, endVn, line, en.tag, "");
            map.put("#" + startVn + "#" + endVn, prima);
        }
    }

    public static boolean compareEntities(entities.entity en1, entities.entity en2)
    {
        if ((en1 == null) && (en2 == null)) return true;
        if ((en1 == null) || (en2 == null)) return false;
        if (en1.startVn != en2.startVn) return false;
        if (en1.endVn != en2.endVn) return false;
        if (en1.line != en2.line) return false;
        return true;
    }

    public static boolean isEntityInArray(ArrayList<entities.entity> ens, entities.entity en)
    {
        if (ens == null) return false;
        if (ens.isEmpty()) return false;
        if (en == null) return false;
        for (int i = 0; i < ens.size(); i++)
        {
            entities.entity temp = ens.get(i);
            if (compareEntities(en, temp)) return true;
        }
        return false;
    }

    public static ArrayList<entities.entity> trimArrayEntities(ArrayList<entities.entity> in)
    {
        ArrayList<entities.entity> result = new ArrayList<entities.entity>();
        if (in == null) return result;
        if (in.isEmpty()) return result;
        for (int i = 0; i < in.size(); i++)
        {
            entities.entity temp = in.get(i);
            if (!isEntityInArray(result, temp))
                result.add(temp);
        }
        return result;
    }

    public static void tagFileChunked(String inputFile)
    {
        try
        {
            CopyFile copyFile = new CopyFile();
            String tagg = "."  + File.separator + "input.txt";
            copyFile.copyfile(inputFile, tagg);

            JVnRecognizer jVnRecognizer = new JVnRecognizer();
            String[] args = new String[4];
            args[0] = "-modeldir";
            args[1] = "./model";
            args[2] = "-inputfile";
            args[3] = "input.txt";
            jVnRecognizer.main(args);
        }
        catch (Exception ex)
        {
            System.out.println("Error in function.tag..: " + ex);
        }
    }

    public static boolean checkBracket(String[] words, int start, int end)
    {
        if (words == null) return false;
        if (words.length == 0) return false;
        if (start < 0) start = 0;
        if (end >= words.length) end = words.length - 1;
        if (start > end) return false;
        for (int i = end; i >= start; i++)
        {
            if (words[i].equals("(") || words[i].equals("{"))
                return true;
            if (words[i].equals(")") || words[i].equals("}"))
                return false;
        }
        return false;
    }

    public static void main(String[] args)
    {
        int[] arr = null;
        arr = addSegmentToArray(arr, 1, 1);
        arr = addSegmentToArray(arr, 2, 5);
        for (int i = 0; i < arr.length; i++)
            out(arr[i]);
    }

}
