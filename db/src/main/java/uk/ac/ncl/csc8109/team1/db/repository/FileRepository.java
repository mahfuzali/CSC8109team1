package uk.ac.ncl.csc8109.team1.db.repository;



import uk.ac.ncl.csc8109.team1.db.model.FileEntity;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Huan on 2017/3/2.
 */
public interface FileRepository {
    /**
     *  store File into S3
     * @param key
     * @param fileEntity
     */
    void storeFile(String key,FileEntity fileEntity);

    /**
     * get File out of S3
     * @param key
     * @return
     * @throws IOException
     */
    FileEntity getFile(String key) ;

    /**
     * Delete file
     * @param key
     */
    void deleteFile(String key);

    /**
     *  generate presigned url
     * @param key
     * @return
     */
    URL generatePreSignedUrl(String key);
}
