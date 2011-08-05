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
public class extendAddedEntity
{
    public static ArrayList<entities.primaryEntity> extendLine(String[] words, int line, List highConfidenceEntityTagged, List nounPhrasesTagged, List addedEntity)
    {
        if (words == null) return null;
        if (words.length == 0) return null;
        ArrayList<entities.primaryEntity> result = new ArrayList<entities.primaryEntity>();
        ArrayList<entities.entity> addedArray = (ArrayList<entities.entity>) addedEntity.get(line);
        for (int i = 0; i < addedArray.size(); i++)
        {
            entities.entity en = addedArray.get(i);
            entities.primaryEntity enExtended = extendOneEntity(words, line, en, highConfidenceEntityTagged, nounPhrasesTagged, addedEntity);
            if (enExtended != null) result.add(enExtended);
        }
        ArrayList<entities.entity> nounPh = (ArrayList<entities.entity>) nounPhrasesTagged.get(line);
        for (int i = 0; i < nounPh.size(); i++)
        {
            entities.entity en = nounPh.get(i);
            entities.primaryEntity enExtended = extendOneEntity(words, line, en, highConfidenceEntityTagged, nounPhrasesTagged, addedEntity);
            if (enExtended != null) result.add(enExtended);
        }
        return result;
    }

    public static boolean isOthers(String data)
    {
        data = data.trim();
        if (data.isEmpty()) return true;
        if (functions.isPunctuationsAll(data)) return true;
        data = functions.removeBrackets(data);
        if (data.isEmpty()) return true;
        if (!functions.hasALetterCaptialized(data) && !functions.isNumber(data) && !functions.lookup_ORG_INDICATE_NOUN_DICT(data) &&
                !functions.lookup_LOC_INDICATE_NOUN_DICT(data) && !functions.lookup_PER_INDICATE_NOUN_DICT(data)) return true;
        return false;
    }

    public static entities.primaryEntity extendOneEntity(String[] words, int line, entities.entity en, List highConfidenceEntityTagged, List nounPhrasesTagged,
            List addedEntity)
    {
        if ((words == null) || (en == null)) return null;
        if (words.length == 0) return null;
        if (line != en.line) return null;
        int start = en.startVn, end = en.endVn;
        String temp = "";
        int[] high = functions.getSegments(highConfidenceEntityTagged, line);
        int[] noun = functions.getSegments(nounPhrasesTagged, line);
        int[] added = functions.getSegments(addedEntity, line);
        for (int i = en.endVn + 1; i < words.length; i++)
        {
            temp = words[i];
            if (functions.checkInTaggedSegment(noun, i) || functions.checkInTaggedSegment(added, i))
            {
                end = i;
                continue;
            }
            if (functions.checkInTaggedSegment(high, i)) break;
            if (isOthers(temp))
            {
                end = i;
                continue;
            }
            break;
        }
        for (int i = en.startVn - 1; i >= 0; i--)
        {
            temp = words[i];
            if (functions.checkInTaggedSegment(noun, i) || functions.checkInTaggedSegment(added, i))
            {
                start = i;
                continue;
            }
            if (functions.checkInTaggedSegment(high, i)) break;
            if (isOthers(temp))
            {
                start = i;
                continue;
            }
            break;
        }
        entities.primaryEntity result = new entities.primaryEntity(start, end, line, "", "");
        return result;
    }

    public static void out(Object o)
    {
        System.out.println(o);
    }
}
