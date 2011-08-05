/*
Copyright (C) 2007 by Cam-Tu Nguyen

Email:	ncamtu@gmail.com

Department of Information System,
College of Technology	
Hanoi National University, Vietnam	
 */
package crfsvm.crf.een_phuong;

import java.util.StringTokenizer;
import java.io.*;

public class IOB2Sequence {
//Member Data

    private int iNumOfColumn;
    private int row;
    boolean label;
    String[][] seqArray = null;
    String mSeqStr;

//Methods
    public IOB2Sequence(int column, boolean lbl) {
        iNumOfColumn = column;
        label = lbl;
    }

    public void readIOB2Seq(String seqStr) {
        StringTokenizer lineTknr = new StringTokenizer(seqStr, "\n");
        seqArray = new String[lineTknr.countTokens()][iNumOfColumn];
        mSeqStr = "";

        int obsrvNo = 0;
        while (lineTknr.hasMoreTokens()) {
            String obsvr = lineTknr.nextToken();
            StringTokenizer spaceTknr = new StringTokenizer(obsvr, "\t");

            if (iNumOfColumn > spaceTknr.countTokens()) {
                iNumOfColumn = spaceTknr.countTokens() - 1;
            }

            int colNo = 0;
            while (spaceTknr.hasMoreTokens()) {
                seqArray[obsrvNo][colNo] = spaceTknr.nextToken();

                if (colNo == 0) {
                    mSeqStr += "[" + seqArray[obsrvNo][colNo].toLowerCase().replaceAll("[ ]+", " ") + "] ";
                }

                colNo++;
            }

            obsrvNo++;
        }
        row = obsrvNo;
    }

    public void readIOB2Seq_new(String seqStr) {
        int count = 0;
        VnStringTokenizer lineTknr1 = new VnStringTokenizer(seqStr, " ");
        while (lineTknr1.hasMoreTokens()) {
            String abc = lineTknr1.nextToken();
            int ascii = (int) abc.charAt(0);
            if (ascii == 65279) {
                continue;
            }
            count++;
        }
        seqArray = new String[count][iNumOfColumn];
        mSeqStr = "";

        int obsrvNo = 0;
        VnStringTokenizer lineTknr2 = new VnStringTokenizer(seqStr, " ");
        while (lineTknr2.hasMoreTokens()) {
            String obsvr = lineTknr2.nextToken();
            int ascii = (int) obsvr.charAt(0);
            if (ascii == 65279) {
                continue;
            }
            StringTokenizer spaceTknr = new StringTokenizer(obsvr, "\t");

            if (iNumOfColumn > spaceTknr.countTokens()) {
                iNumOfColumn = spaceTknr.countTokens() - 1;
            }

            int colNo = 0;
            while (spaceTknr.hasMoreTokens()) {
                seqArray[obsrvNo][colNo] = spaceTknr.nextToken();

                if (colNo == 0) {
                    mSeqStr += "[" + seqArray[obsrvNo][colNo].toLowerCase().replaceAll("[ ]+", " ") + "] ";
                }

                colNo++;
            }

            obsrvNo++;
        }
        row = obsrvNo;
    }

    public String getSeqStr() {
        return mSeqStr;
    }

    public int getNumOfColumn() {
        return iNumOfColumn;
    }

    public int length() {
        return row;
    }

    public String getToken(int c, int pos) {
        if (c >= iNumOfColumn) {
            return "";
        }
        if (pos >= row) {
            return "";
        }
        return seqArray[pos][c];
    }

    public void print() {
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < iNumOfColumn; ++j) {
                System.out.print(seqArray[i][j]);
                System.out.print("\t");
            }
            System.out.print("\n");
        }
    }

    public void print(BufferedWriter out) {
        try {
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < iNumOfColumn; ++j) {
                    out.write(seqArray[i][j]);
                    out.write("\t");
                }

                out.newLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
