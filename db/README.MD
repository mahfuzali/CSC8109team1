### DB by huan
To simply , I remove framework and other layers. 

*to use the code, place your own credentials in resources/AwsCredentials.properties*

Three Interface for accessing to db. The details of db is defined in Entity.
- FileRepository for s3
```java
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
```
- Register Repository for DynamoDB register table
```java
 /**
     * check if user already exists
     * @param id
     * @return <code>boolean</code>
     */
    boolean checkAlreadyExist(String id);

    /**
     * register user to database
     * @return <code>boolean</code>
     */
    boolean registerUser(RegisterEntity entity);

    /**
     * get public key by given id
     * @param id
     * @return <code>String/code>
     */
    String getPublicKeyById(String id);
```
- Message Repository for DynamoDB message table
```java
/**
     *  store message
     * @param uuid
     * @param entity
     */
    void storeMessage(UUID uuid, FairExchangeEntity entity);

    /**
     *  get message by id
     * @param uuid
     * @return
     */
    FairExchangeEntity getMessage(UUID uuid);

    /**
     * todo
     * @return
     */
    Map<UUID,FairExchangeEntity> getUnfinishedMessage();

    /**
     * delete message by id
     * @param uuid
     */
    void deleteMessage(UUID uuid);

    /**
     * todo 
     */
    void deleteExpiredMessage();
```

For details of usage, please check the test case.