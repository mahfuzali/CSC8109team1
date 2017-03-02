package uk.ac.ncl.csc8109.team1.repository.impl;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import uk.ac.ncl.csc8109.team1.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.model.FileEntity;

import java.io.IOException;

/**
 * Created by Huan on 2017/3/2.
 */
@Repository
public class FileRepositoryImpl implements FileRepository{
    private String bucketName = "docstore";
    private Logger log = Logger.getLogger(FileRepositoryImpl.class);
    private AmazonS3 s3;
    public FileRepositoryImpl(){
        s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
        Region region = Region.getRegion(Regions.EU_WEST_1);
        s3.setRegion(region);
    }

    @Override
    public void storeFile(String key, FileEntity entity) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        log.info("store: "+entity.getFileName());
        objectMetadata.addUserMetadata("filename", entity.getFileName());
        s3.putObject(new PutObjectRequest(bucketName,key,entity.getInputStream(),objectMetadata));
    }

    @Override
    public FileEntity getFile(String key) throws IOException {
        S3Object s3Object = s3.getObject(new GetObjectRequest(bucketName, key));
        FileEntity entity = new FileEntity();
        String fileName = s3Object.getObjectMetadata().getUserMetadata().get("filename");
        log.info("get: "+s3Object.getObjectMetadata().getUserMetadata());
        entity.setFileName(fileName);
        entity.setInputStream(s3Object.getObjectContent());
        return entity;
    }

    @Override
    public void deleteFile(String key) {
        s3.deleteObject(bucketName,key);
    }
}
