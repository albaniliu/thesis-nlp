/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crfsvm.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Cung cap phuong thuc xoa cac file tam sinh ra trong qua trinh chay chuong trinh trong 1 thu muc bat ky
 * @author banhbaochay
 */
public class FileGarbageCollector {

    static Logger logger = Logger.getLogger(FileGarbageCollector.class);

    /**
     * Goi phuong thuc markState tai thoi diem truoc khi sinh file tam, khi can xoa cac file tam thi goi collectGarbage
     * @param dirPath Duong dan thu muc se thuc hien viec xoa file tam
     */
    public FileGarbageCollector(String dirPath) {
        this(new File(dirPath));
    }// end constructor

    /**
     * Goi phuong thuc markState tai thoi diem truoc khi sinh file tam, khi can xoa cac file tam thi goi collectGarbage
     * @param dir Thu muc se thuc hien viec xoa file tam
     */
    public FileGarbageCollector(File dir) {
        this.dir = dir;
        beforeFiles = new ArrayList<File>();

        if (!dir.exists() || dir.isFile()) {
            logger.error("Thu muc " + dir.getAbsolutePath() + " khong ton tai");
            canGarbage = false;
        }

    }// end constructor

    /**
     * Luu thoi diem truoc khi sinh file tam
     */
    public void markState() {
        if (canGarbage) {
            beforeFiles.addAll(Arrays.asList(dir.listFiles()));
        } else {
            logger.info("Khong the khoi tao doi tuong GarbageCollecter!");
        }
    }// end markState method

    /**
     * Tien hanh xoa cac file hoac thu muc rac sinh ra sau khi markState
     */
    public void collectGarbage() {
        if (canGarbage) {
            for (File file : dir.listFiles()) {
                if (!beforeFiles.contains(file)) {
                    FileUtils.removeFile(file.getAbsolutePath());
                }
            }// end foreach file
        } else {
            logger.info("Khong the thuc thi collectGarbage!");
        }
    }// end collectGarbage method
    private List<File> beforeFiles;
    private File dir;
    private boolean canGarbage = true;
}// end FileGarbageCollector class

