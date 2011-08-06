/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Thien
 */
public class filterFile
{
    public static void filterSentenceWithTag(String source, String destination)
    {
        String[] tags = {"per", "loc", "org"};
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destination), "UTF-8"));
            String line = "", result = "";
            while ((line = in.readLine()) != null)
            {
                if (line.trim().length() == 0) continue;
                Object[] obj = buildIndexOfTags(tags, line);
                boolean state = true;
                for (int i = 0; i < obj.length; i++)
                    if (!checkIntegrityOfOneTag(obj[i]))
                    {
                        state = false;
                        break;
                    }
                if (!state) continue;
                for (int i = 0; i < obj.length; i++)
                    for (int j = i+1; j < obj.length; j++)
                        if (!checkIntegrityOfTowTags(obj[i], obj[j]))
                        {
                            state = false;
                            break;
                        }
                if (!state) continue;
                state = false;
                for (int i = 0; i < obj.length; i++)
                {
                    ArrayList<Object> obji = (ArrayList<Object>) obj[i];
                    if (!obji.isEmpty())
                    {
                        state = true;
                        break;
                    }
                }

                if (state) result += line + "\n\n";
            }

            result = result.trim();

            out.write(result);

            in.close();
            out.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex);
        }
    }

    public static Object[] buildIndexOfTags(String[] tags, String str)
    {
        Object[] result = new Object[tags.length];
        int open = 0, close = 0, pos = 0;
        for (int i = 0; i < tags.length; i++)
        {
            ArrayList<Object> indexsi = new ArrayList<Object>();
            pos = 0;
            do
            {
                open = str.indexOf("<" + tags[i] + ">", pos);
                if (open >= 0)
                {
                    close = str.indexOf("</" + tags[i] + ">", open + 1);
                    int[] ind = {open, close};
                    indexsi.add(ind);
                    if (close >= 0) pos = open + tags[i].length() + 2;
                    else break;
                }
            }
            while (open >= 0);
            result[i] = (Object) indexsi;
        }
        return result;
    }

    public static boolean checkIntegrityOfOneTag(Object obj)
    {
        ArrayList<Object> index = (ArrayList<Object>) obj;

        if (index.isEmpty()) return true;

        int[] array = new int[2*index.size()];

        for (int i = 0; i < index.size(); i++)
        {
            int[] temp = (int[]) index.get(i);
            array[2*i] = temp[0];
            array[2*i + 1] = temp[1];
        }

        for (int i = 1; i < array.length; i++)
        {
            if (array[i] <= array[i - 1]) return false;
        }

        return true;
    }

    public static boolean checkIntegrityOfTowTags(Object obj1, Object obj2)
    {
        ArrayList<Object> indexs1 = (ArrayList<Object>) obj1;
        ArrayList<Object> indexs2 = (ArrayList<Object>) obj2;
        if ((indexs1.isEmpty()) || (indexs2.isEmpty())) return true;
        int[] array = new int[2*indexs1.size() + 2*indexs2.size()];
        int[] array1 = new int[2*indexs1.size()];
        int[] array2 = new int[2*indexs2.size()];
        try {
        for (int i = 0; i < indexs1.size() ; i++)
        {
            int[] temp = (int[]) indexs1.get(i);
            array1[2*i] = temp[0];
            array1[2*i+1] = temp[1];
        }

         for (int i = 0; i < indexs2.size() ; i++)
        {
            int[] temp = (int[]) indexs2.get(i);
            array2[2*i] = temp[0];
            array2[2*i+1] = temp[1];
        }
            }
        catch (Exception e)
        {
            System.out.println("Error OK1");
        }

        int run1 = 0, run2 = 0, run = 0;
        while ((run1 < indexs1.size()) && (run2 < indexs2.size()))
        {
            if (array1[2*run1] < array2[2*run2])
            {
                array[2*run] = array1[2*run1];
                array[2*run + 1] = array1[2*run1 + 1];
                run1++;
            }
            else
            {
                array[2*run] = array2[2*run2];
                array[2*run + 1] = array2[2*run2 + 1];
                run2++;
            }
            run++;
        }
            
        if (run1 < indexs1.size())
            for (int i = run1; i < indexs1.size(); i++)
            {
                array[2*run] = array1[2*i];
                array[2*run + 1] = array1[2*i + 1];
                run++;
            }
        else
            for (int i = run2; i < indexs2.size(); i++)
            {
                array[2*run] = array2[2*i];
                array[2*run + 1] = array2[2*i + 1];
                run++;
            }

        for (int i = 1; i < array.length; i++)
        {
            if (array[i] <= array[i - 1]) return false;
        }

        return true;
    }

    public static void main(String[] args)
    {
        String source = "C:\\labs\\FilterTagged.txt";
        String des = "C:\\labs\\filterDone.txt";
        filterSentenceWithTag(source, des);
        /*
        String[] tags = {"per", "loc", "org"};
        String str = "[Hầu] [tòa] [với] <per>[ông] <per> [Dũng]</per> </per> [là] [4] [thuộc cấp] <per> [Vũ Mạnh Tiên] </per> ( [Phó] [chánh văn phòng] )";
        Object[] obj = buildIndexOfTags(tags, str);
        ArrayList<Object> o = (ArrayList<Object>) obj[0];
        for (int i = 0; i < o.size(); i++)
        {
            int[] temp = (int[]) o.get(i);
            System.out.println(i + ": " + temp[0] + " , " + temp[1]);
        }
        System.out.println(checkIntegrityOfOneTag(obj[0]));
         *
         */
    }
}
