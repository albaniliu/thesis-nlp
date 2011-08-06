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
public class calculateConfidence
{
    List viPool = null;
    List normPool = null;
    List modelLabelObsrPool = null;
    List denominatorPool = null;

    DoubleMatrix Mi = null;
    DoubleVector Vi = null;
    double normConstant[][] = null;
    int modelLabelObsr[] = null;
    double denominator = 0;

    public boolean init(String storeFile)
    {
        if (viPool != null)
            viPool.clear();
        else
            viPool = new ArrayList();

        if (normPool != null)
            normPool.clear();
        else
            normPool = new ArrayList();

        if (modelLabelObsrPool != null)
            modelLabelObsrPool.clear();
        else
            modelLabelObsrPool = new ArrayList();

        if (denominatorPool != null)
            denominatorPool.clear();
        else
            denominatorPool = new ArrayList();

        BufferedReader fin = null;

        try
        {
            fin = new BufferedReader(new InputStreamReader(new FileInputStream(storeFile), "UTF-8"));

            String line = "";
            int count = 0, numLabelMi = 0, maxLine = Integer.MAX_VALUE, maxLo = Integer.MAX_VALUE, numLabel = 0, seqLen = 0;
            DoubleVector viAdd[] = null;
            int modelLabelAdd[] = null;
            double normAdd[][] = null;


            while ((line = fin.readLine()) != null)
            {
                if (line.length() == 0) continue;
                count++;
                if (line.equals("########")) maxLine = count;
                if (count < maxLine)
                {
                    if (count == 1)
                    {
                        line = line.substring(0, line.indexOf("#"));
                        if (line.length() == 0) return false;
                        numLabelMi = Integer.parseInt(line);
                        Mi = new DoubleMatrix(numLabelMi, numLabelMi);
                    }
                    else
                    {
                        if (line.equals("----------")) continue;
                        StringTokenizer miTokr = new StringTokenizer(line, "#");
                        int col = -1;
                        while (miTokr.hasMoreElements())
                        {
                            col++;
                            Mi.mtrx[count - 3][col] = Double.parseDouble(miTokr.nextToken());
                        }
                    }
                }
                else
                {
                    if (line.equals("########")) continue;
                    if (count == (maxLine + 1))
                    {
                        StringTokenizer numTokr = new StringTokenizer(line, "#");
                        double deno = 0;
                        if (!numTokr.hasMoreElements()) return false;
                        numLabel = Integer.parseInt(numTokr.nextToken());
                        if (!numTokr.hasMoreElements()) return false;
                        seqLen = Integer.parseInt(numTokr.nextToken());
                        if (!numTokr.hasMoreElements()) return false;
                        deno = Double.parseDouble(numTokr.nextToken());
                        if (numTokr.hasMoreElements()) return false;
                        normAdd = new double[seqLen][numLabel];
                        viAdd = new DoubleVector[seqLen];
                        modelLabelAdd = new int[seqLen];
                        denominatorPool.add(deno);
                        continue;
                    }
                    if (line.equals("----------"))
                    {
                        maxLo = count;
                        continue;
                    }
                    if (maxLo == (maxLine + 2))
                    {
                        StringTokenizer viTokr = new StringTokenizer(line, "#");
                        DoubleVector viele = new DoubleVector(numLabel);
                        int runvi = -1;
                        while (viTokr.hasMoreElements())
                        {
                            runvi++;
                            viele.vect[runvi] = Double.parseDouble(viTokr.nextToken());
                        }
                        viAdd[count - maxLo - 1] = viele;
                    }
                    else if (maxLo == (maxLine + seqLen + 3))
                    {
                        StringTokenizer normTokr = new StringTokenizer(line, "#");
                        int runnorm = -1;
                        while (normTokr.hasMoreElements())
                        {
                            runnorm++;
                            normAdd[count - maxLo -1][runnorm] = Double.parseDouble(normTokr.nextToken());
                        }
                    }
                    else if (maxLo == (maxLine + 2*seqLen + 4))
                    {
                        StringTokenizer labelTokr = new StringTokenizer(line, "#");
                        int runlabel = -1;
                        while (labelTokr.hasMoreElements())
                        {
                            runlabel++;
                            modelLabelAdd[runlabel] = Integer.parseInt(labelTokr.nextToken());
                        }
                        viPool.add(viAdd);
                        normPool.add(normAdd);
                        modelLabelObsrPool.add(modelLabelAdd);
                    }
                }
            }

            fin.close();
            return true;
        }
        catch(Exception ex)
        {
            System.out.println("Error: " + ex);
            return false;
        }
    }

    public void computeVi(int seq, int pos)
    {
        DoubleVector[] viSeq = (DoubleVector[]) viPool.get(seq);
        Vi = viSeq[pos];
    }

    public void computeNorm(int seq)
    {
        normConstant = (double[][]) normPool.get(seq);
    }

    public void computeModelLabel(int seq)
    {
        modelLabelObsr = (int[]) modelLabelObsrPool.get(seq);
    }

    public void computeDenominator(int seq)
    {
        denominator = Double.parseDouble(denominatorPool.get(seq).toString());
    }

    public double confidenceOf(int seq, int start, int end)
    {
        if ((seq < 0) || (start < 0) || (end < 0)) return -1;
        computeDenominator(seq);
        computeNorm(seq);
        computeModelLabel(seq);
        int numLabel = normConstant[0].length;
        int seqLen = modelLabelObsr.length;
        if (seqLen == 0) return -1;
        if ((start >= seqLen) || (end >= seqLen)) return -1;
        double normConfidence[][] = new double[seqLen][numLabel];
        for (int i = 0; i < seqLen; i++)
            for (int j = 0; j < numLabel; j++)
                normConfidence[i][j] = 0;

        computeVi(seq, 0);

        if (start == 0)
             for (int i = 0; i < numLabel; i++)
                 if (i == modelLabelObsr[start])
                        normConfidence[0][i] = Vi.vect[i];
        if (start > 0)
        {
                for (int i = 0; i < start; i++)
                    for (int j = 0; j < numLabel; j++)
                        normConfidence[i][j] = normConstant[i][j];
                computeVi(seq, start);
                for (int j = 0; j < numLabel; j++)
                    if (j == modelLabelObsr[start])
                    {
                        for (int k = 0; k < numLabel; k++)
                            normConfidence[start][j] += normConfidence[start - 1][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code
                        break;
                    }
        }

        if (end > start)
        {
                for (int i = start + 1; i <= end; i++)
                {
                    computeVi(seq, i);
                    for (int j = 0; j < numLabel; j++)
                        if (j == modelLabelObsr[i])
                        {
                            for (int k = 0; k < numLabel; k++)
                                normConfidence[i][j] += normConfidence[i - 1][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code
                            break;
                        }
                }
         }
         if (end < seqLen - 1)
         {
            for (int i = end + 1; i < seqLen; i++)
                {
                    computeVi(seq, i);
                    for (int j = 0; j < numLabel; j++)
                        for (int k = 0; k < numLabel; k++)
                            normConfidence[i][j] += normConfidence[i - 1][k]*Mi.mtrx[k][j]*Vi.vect[j]; //my code
                }
         }

         double numerator = 0.0;

         for (int tocal = 0; tocal < numLabel; tocal++)
         {
             numerator += normConfidence[seqLen - 1][tocal];
         }

         double confidence = numerator/denominator;
         return confidence;
    }
}
