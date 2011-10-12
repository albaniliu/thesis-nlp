/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm;

import crfsvm.crf.een_phuong.CopyFile;
import crfsvm.crf.een_phuong.Offset;
import crfsvm.crf.een_phuong.TaggedDocument;
import crfsvm.util.Count;
import crfsvm.util.Document;
import crfsvm.util.FileUtils;
import crfsvm.util.MapUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author banhbaochay
 */
public class Compare {

    static Logger logger = Logger.getLogger(Compare.class);

    /**
     * 
     * @param trainPath
     * @param testPath
     */
    public void runBagging(String trainPath, String testPath) {
	// Ban sao file train
	String mainTrainCopied = "tmp/train.txt";
	// Ban sao file test
	String mainTestCopied = "tmp/test.txt";
	// File feature goc tao ra tu file train
	String mainTrainFeature = mainTrainCopied + ".feature";
	// File bag train
	String bagTrainCopied = "tmp/bagTrain.txt";
	// File de predict, chi duoc tach tu, chua duoc gan nhan
	String mainTestNoTagLabel = "tmp/test-notag.txt";
	Main m = new Main();

	int B = 7;

	int bagSize = 180;

	// Lap danh sach cac file ban dau trong thu muc tmp
	List<File> oriFiles = new ArrayList<File>();
	oriFiles.addAll(Arrays.asList(new File("tmp").listFiles()));

	/*
	 * Tao model va file feature dau tien. File feature: oriTrain + .feature
	 */
	logger.info("Tao model va file feature dau tien, dong thoi tinh toan P-R-F");
	CopyFile.copyfile(trainPath, mainTrainCopied);
	CopyFile.copyfile(testPath, mainTestCopied);
	Crf.calcFScore(mainTrainCopied, mainTestCopied);
	
	/*
	 * Tao TrainSet: tmp/TrainSet
	 */
	logger.info("Tao TrainSet");
	Document tmpDoc = new Document(mainTrainCopied);
	FileUtils.createTrainSet(tmpDoc, B, bagSize);
	tmpDoc = null;

	// Tao file test chua tag label de gan nhan
	CopyFile.copyfile(mainTestCopied, mainTestNoTagLabel);
	FileUtils.removeTag(mainTestNoTagLabel);

	TaggedDocument taggedDoc = new TaggedDocument(mainTestCopied);
//	Map<String, Map<String, Integer>> countMap = new HashMap<String, Map<String, Integer>>();
	Map<Offset, Map<String, Integer>> countMap = new HashMap<Offset, Map<String, Integer>>();

	// Bat dau vong lap CRF
	logger.info("Bat dau boostrapping");
	File trainSetDir = new File("tmp/TrainSet");
	for (File trainBagFile : trainSetDir.listFiles()) {
	    /*
	     * Lap voi tung bag
	     */
	    CopyFile.copyfile(trainBagFile.getAbsolutePath(), bagTrainCopied);
	    // train + predict, ket qua predict nam trong file mainTestCopied + wseg
	    logger.info("Chay CRF voi file " + bagTrainCopied);
	    Crf.runCrf(bagTrainCopied, mainTestNoTagLabel);
//	    TaggedDocument testTaggedDoc = new TaggedDocument(mainTestNoTagLabel + ".wseg");
	    
//	    m.countAppear(countMap, mainTestNoTagLabel + ".wseg");
	    Count.countAppearLabel(countMap, mainTestNoTagLabel + ".wseg");
	}// end foreach CRF

	// Lay ra cac phan tu co entropy k vuot qua nguong
	List<String[]> sList = m.calcAndFindS(countMap);
	
	// Sua lai
	Map iobPosMap = new TreeMap();
	for (String[] strings : sList) {
	    Offset offset = new Offset(strings[0]);
	    String iob = strings[1];
	    iobPosMap.put(offset, iob);
	}// end foreach strings
//	logger.info("IOB map moi: " + iobPosMap);
	taggedDoc.setIobPosMap(iobPosMap);
	System.out.printf("Ket qua cuoi cung:\nPer: %d, loc: %d, org: %d",
		taggedDoc.getLabelCountByName("per"), taggedDoc.getLabelCountByName("loc"),
		taggedDoc.getLabelCountByName("org"));


	// Xoa cac file moi tao, chi dung trong linux
	for (File file : new File("tmp").listFiles()) {
	    if (!oriFiles.contains(file)) {
		FileUtils.removeFile(file.getAbsolutePath());
	    }
	}// end foreach file

    }// end runBagging method

    /**
     * 
     * @param trainPath
     * @param testPath  
     */
    public void runConfidenScore(String trainPath, String testPath) {
	// Ban sao file train
	String mainTrainCopied = "tmp/train.txt";
	// Ban sao file test
	String mainTestCopied = "tmp/test.txt";
	// File feature goc tao ra tu file train
	String mainTrainFeature = mainTrainCopied + ".feature";
	// File bag train
	String bagTrainCopied = "tmp/bagTrain.txt";
	// File de predict, chi duoc tach tu, chua duoc gan nhan
	String mainTestNoTag = "tmp/test-notag.txt";

	CopyFile.copyfile(testPath, mainTestCopied);
	FileUtils.removeTag(mainTestCopied);

	// Tao file co nhan voi confidence score: giong ten 
	Crf.predict(Crf.DEFAULT_MODEL_DIR, mainTestCopied, true);




    }// end runConfidenScore method

    public static void main(String[] args) {
	DOMConfigurator.configure("log-config.xml");
	Compare c = new Compare();
	c.runBagging("tmp/trainThien200doivoibackup.txt", "tmp/testThien1888doivoibackup.txt");
    }// end main class
    
    public void processAfterRunCrf(TaggedDocument taggedDoc) {
	
    }// end processAfterRunCrf method
}// end Compare class

