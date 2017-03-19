package uk.ac.ncl.csc8109.team1.db.repository.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.apache.log4j.Logger;

import uk.ac.ncl.csc8109.team1.db.model.FileEntity;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Huan on 2017/3/2.
 */

public class FileRepositoryImpl implements FileRepository {
    private String bucketName = "csc8109team1docs";
    private Logger log = Logger.getLogger(FileRepositoryImpl.class);
    private static AmazonS3 s3;
    public FileRepositoryImpl(){
        if(s3 == null) {
//            s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).withCredentials(new ClasspathPropertiesFileCredentialsProvider()).build();
            s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
            s3.setRegion(Region.getRegion(Regions.EU_WEST_1));
        }
    }

    @Override
    public void storeFile(String key, FileEntity entity) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        log.info("store: "+entity.getFileName());
        objectMetadata.addUserMetadata("filename", entity.getFileName());
        s3.putObject(new PutObjectRequest(bucketName,key,entity.getInputStream(),objectMetadata));
    }

    @Override
    public FileEntity getFile(String key)  {
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

    @Override
    public URL generatePreSignedUrl(String key) {
        java.util.Date expiration = new java.util.Date();
        long msec = expiration.getTime();
        msec += 1000 * 60 * 60*6; // 6 hour.
        expiration.setTime(msec);
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, key);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
        generatePresignedUrlRequest.setExpiration(expiration);

        return s3.generatePresignedUrl(generatePresignedUrlRequest);

    }
}
