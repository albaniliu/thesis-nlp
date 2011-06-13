/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thesis;

import feature.ENTITY;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lib.ConvertText;
import lib.ID;
import lib.JSRELine;
import lib.ReadWriteFile;
import lib.Sentence;

/**
 * Input: van ban co nhan thuc the P, O, L
 * Output: van ban o dinh dang JSRE
 * Process:
 * 1. Doc file dau vao
 * 2. Luu thuc the vao 3 mang tuong ung
 * 3. Thuc hien ghep cac cap thuc the: P - A, L - T, O - T, Pos - T
 * 4. Chuyen dang JSRE
 * 5. Viet ra 3 file tuong ung voi 3 quan he work for , live in va per pos
 *
 * @author banhbaochay
 */
public class AutoConvert {

    public static void main(String[] args) throws Exception {
        AutoConvert ob = new AutoConvert();
        File input = new File(args[0]);
        File outputWorkFor = new File(args[1]);
        File outputLiveIn = new File(args[2]);
        File outputPerPos = new File(args[3]);
        BufferedReader in = ReadWriteFile.readFile(input, "UTF-8");
        PrintWriter outWorkFor = ReadWriteFile.writeFile(outputWorkFor, "UTF-8");
        PrintWriter outLiveIn = ReadWriteFile.writeFile(outputLiveIn, "UTF-8");
        PrintWriter outPerPos = ReadWriteFile.writeFile(outputPerPos, "UTF-8");
        ID id = new ID();
        ID idLiveIn = new ID();
        ID idPerPos = new ID();
        String line = null;
        int lineNumber = 1;
        int lineNumberLiveIn = 1;
        int lineNumberPerPos = 1;
        while ((line = in.readLine()) != null) {
            if (!line.equals("")) {

                Sentence sentence = new Sentence(ConvertText.convertForSentence(line));
                List<String> agentList = new ArrayList<String>();
                List<String> targetWorkForList = new ArrayList<String>();
                List<String> targetLiveInList = new ArrayList<String>();
                List<String> targetPerPosList = new ArrayList<String>();

                /*
                 * Tao danh sach agentList va targetList. 2 danh sach nay chi luu vi tri bat dau va ket
                 * thuc cua entity
                 */
                Map<Integer, Object[]> entityMap = sentence.getEntityMap();
                for (Map.Entry<Integer, Object[]> entry : entityMap.entrySet()) {
                    ENTITY entityType = (ENTITY) entry.getValue()[0];
                    int beginOffset = (Integer) entry.getKey();
                    int endOffset = (Integer) entry.getValue()[2];
                    if (entityType.getName().equals("PER")) {
                        agentList.add(beginOffset + "-" + endOffset);
                    } else if (entityType.getName().equals("ORG")) {
                        targetWorkForList.add(beginOffset + "-" + endOffset);
                    } else if (entityType.getName().equals("LOC")) {
                        targetLiveInList.add(beginOffset + "-" + endOffset);
                    } else if (entityType.getName().equals("POS")) {
                        targetPerPosList.add(beginOffset + "-" + endOffset);
                    }

                }// end for entry
                outWorkFor.print(ob.createWorkForRE(sentence, agentList, targetWorkForList, id, lineNumber));
                outLiveIn.print(ob.createLiveInRE(sentence, agentList, targetLiveInList, idLiveIn, lineNumberLiveIn));
                outPerPos.print(ob.createPerPosRE(sentence, agentList, targetPerPosList, idPerPos, lineNumberPerPos));
                lineNumber++;
                lineNumberLiveIn++;
                lineNumberPerPos++;
            }// end if line
        }// end while line

        in.close();
        outWorkFor.close();
        outLiveIn.close();
        outPerPos.close();

    }// end main method

    /**
     * Danh dau quan he work for cho tung cau
     * @param sentence
     * @param agentList
     * @param targetWorkForList
     * @param id
     * @param lineNumber
     * @return
     */
    private StringBuilder createWorkForRE(Sentence sentence, List<String> agentList,
            List<String> targetWorkForList, ID id, int lineNumber) {
        StringBuilder sb = new StringBuilder();
        JSRELine jsreLine = new JSRELine("0\t0-0\t" + sentence.toJSRE());

        id.setLineNumber(lineNumber);
        for (String elementAgent : agentList) {
            int beginOffsetAgent = Integer.parseInt(elementAgent.split("-")[0]);
            int tokenIDAgent = sentence.getTokenID(beginOffsetAgent);
            if (tokenIDAgent != -1) {
                jsreLine.setEntityLabel(tokenIDAgent, "A");
            }
            for (String elementTarget : targetWorkForList) {
                int beginOffsetTarget = Integer.parseInt(elementTarget.split("-")[0]);
                int tokenIDTarget = sentence.getTokenID(beginOffsetTarget);
                if (tokenIDTarget != -1) {
                    jsreLine.setEntityLabel(tokenIDTarget, "T");
                }
                id.increase();
                jsreLine.setId(new ID(id));
                sb.append(jsreLine.toString());
                sb.append("\r\n");
            }
        }

        return sb;
    }// end createWorkForRE method

    /**
     * Danh dau quan he Live in cho tung cau
     * @param sentence
     * @param agentList
     * @param targetLiveInList
     * @param id
     * @param lineNumber
     * @return
     */
    private StringBuilder createLiveInRE(Sentence sentence, List<String> agentList,
            List<String> targetLiveInList, ID id, int lineNumber) {
        StringBuilder sb = new StringBuilder();
        JSRELine jsreLine = new JSRELine("0\t0-0\t" + sentence.toJSRE());

        id.setLineNumber(lineNumber);
        for (String elementAgent : agentList) {
            int beginOffset = Integer.parseInt(elementAgent.split("-")[1]);
            int tokenID = sentence.getTokenID(beginOffset);
            if (tokenID != -1) {
                jsreLine.setEntityLabel(tokenID, "A");
            }
            for (String elementTarget : targetLiveInList) {
                int beginOffsetTarget = Integer.parseInt(elementTarget.split("-")[1]);
                int tokenIDTarget = sentence.getTokenID(beginOffsetTarget);
                if (tokenIDTarget != -1) {
                    jsreLine.setEntityLabel(tokenIDTarget, "T");
                }
                id.increase();
                jsreLine.setId(new ID(id));
                sb.append(jsreLine.toString());
                sb.append("\r\n");
            }
        }

        return sb;
    }// end createLiveInRE method

    /**
     * Danh dau quan he Per pos cho tung cau
     * @param sentence
     * @param agentList
     * @param targetPerPosList
     * @param id
     * @param lineNumber
     * @return
     */
    private StringBuilder createPerPosRE(Sentence sentence, List<String> agentList,
            List<String> targetPerPosList, ID id, int lineNumber) {
        StringBuilder sb = new StringBuilder();
        JSRELine jsreLine = new JSRELine("0\t0-0\t" + sentence.toJSRE());

        id.setLineNumber(lineNumber);
        for (String elementAgent : agentList) {
            int beginOffset = Integer.parseInt(elementAgent.split("-")[1]);
            int tokenID = sentence.getTokenID(beginOffset);
            if (tokenID != -1) {
                jsreLine.setEntityLabel(tokenID, "A");
            }
            for (String elementTarget : targetPerPosList) {
                int beginOffsetTarget = Integer.parseInt(elementTarget.split("-")[1]);
                int tokenIDTarget = sentence.getTokenID(beginOffsetTarget);
                if (tokenIDTarget != -1) {
                    jsreLine.setEntityLabel(tokenIDTarget, "T");
                }
                id.increase();
                jsreLine.setId(new ID(id));
                sb.append(jsreLine.toString());
                sb.append("\r\n");
            }
        }

        return sb;
    }// end createPerPosRE method

}// end class

