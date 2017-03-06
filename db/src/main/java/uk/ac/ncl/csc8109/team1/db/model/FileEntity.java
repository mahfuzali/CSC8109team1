package uk.ac.ncl.csc8109.team1.db.model;

import java.io.InputStream;

/**
 * Created by Huan on 2017/3/2.
 */
public class FileEntity {
    private String fileName;
    private InputStream inputStream;

    public FileEntity(){}

    public FileEntity(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
