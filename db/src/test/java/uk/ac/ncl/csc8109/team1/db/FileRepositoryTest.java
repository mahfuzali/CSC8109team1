package uk.ac.ncl.csc8109.team1.db;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.impl.FileRepositoryImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Huan on 2017/3/5.
 */

public class FileRepositoryTest {

    @Test
    public void testFileStoreAndGet()  {
        FileEntity fileEntity = new FileEntity();
        FileRepository fileRepository = new FileRepositoryImpl();
        File initialFile = new File("src/main/resources/sample.txt");
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            Assert.fail();
        }
        fileEntity.setFileName(initialFile.getName());
        fileEntity.setInputStream(targetStream);
        String key =UUID.randomUUID().toString();
        fileRepository.storeFile(key,fileEntity);
        FileEntity entity = fileRepository.getFile(key);
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getFileName(),initialFile.getName());
    }

    @Test
    public void testFileDelete(){
        FileEntity fileEntity = new FileEntity();
        FileRepository fileRepository = new FileRepositoryImpl();
        File initialFile = new File("src/main/resources/sample.txt");
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            Assert.fail();
        }
        fileEntity.setFileName(initialFile.getName());
        fileEntity.setInputStream(targetStream);
        String key =UUID.randomUUID().toString();
        fileRepository.storeFile(key,fileEntity);
        fileRepository.deleteFile(key);
        try {
            FileEntity entity = fileRepository.getFile(key);
        }catch (AmazonS3Exception e){
            Assert.assertTrue(true);
            return;
        }
        Assert.fail();
    }
}
