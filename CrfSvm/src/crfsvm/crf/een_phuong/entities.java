/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package crfsvm.crf.een_phuong;

/**
 *
 * @author Thien
 */
public class entities
{
    public static class primaryEntity
    {
        public int startVn = 0, endVn = 0, line = 0;
        public String text = "", tag = "";
        public primaryEntity(int start, int end, int line, String tag, String text)
        {
            this.startVn = start;
            this.endVn = end;
            this.line = line;
            this.tag = tag;
            this.text = text;
        }
    }
    public static class entity extends primaryEntity
    {
        int startChar = 0, endChar = 0;
        double confidence = 0;

        public entity(int startChar, int endChar, String text, double confidence, int startVn, int endVn, int line, String tag)
        {
            super(startVn, endVn, line, tag, text);
            this.startChar = startChar;
            this.endChar = endChar;
            this.confidence = confidence;
        }
    }

    public static class candidate
    {
        int upper = 0, lower = 0, start = 0, end = 0;
        String tag = "";
    }
}
