package uk.ac.ncl.csc8109.team1.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;

import java.util.UUID;

/**
 * Created by Huan on 2017/3/2.
 */
@DynamoDBTable(tableName = "message_table")
public class FairExchangeEntity {
    @DynamoDBHashKey
    private UUID uuid;
    @DynamoDBAttribute
    private String toID;
    @DynamoDBAttribute
    private String fromID;
    @DynamoDBAttribute
    private String fPublicKey;
    @DynamoDBAttribute
    private String tPublicKey;
    @DynamoDBAttribute
    private String originHash;
    @DynamoDBAttribute
    private String receiptHash;
    @DynamoDBAttribute
    private long createTime;
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute
    private FairExchangeStage stage;
    @DynamoDBAttribute
    private String fileKey;

    public FairExchangeEntity() {
    }

    public FairExchangeEntity(UUID uuid, String toID, String fromID, String fPublicKey, String tPublicKey, String originHash, long createTime) {
        this.uuid = uuid;
        this.toID = toID;
        this.fromID = fromID;
        this.fPublicKey = fPublicKey;
        this.tPublicKey = tPublicKey;
        this.originHash = originHash;
        this.createTime = createTime;
        this.stage = FairExchangeStage.STAGE1;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public void setfPublicKey(String fPublicKey) {
        this.fPublicKey = fPublicKey;
    }

    public void settPublicKey(String tPublicKey) {
        this.tPublicKey = tPublicKey;
    }

    public void setOriginHash(String originHash) {
        this.originHash = originHash;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getToID() {
        return toID;
    }

    public String getFromID() {
        return fromID;
    }

    public String getfPublicKey() {
        return fPublicKey;
    }

    public String gettPublicKey() {
        return tPublicKey;
    }

    public String getOriginHash() {
        return originHash;
    }

    public String getReceiptHash() {
        return receiptHash;
    }

    public void setReceiptHash(String receiptHash) {
        this.receiptHash = receiptHash;
    }

    public long getCreateTime() {
        return createTime;
    }

    public FairExchangeStage getStage() {
        return stage;
    }

    public void setStage(FairExchangeStage stage) {
        this.stage = stage;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
}
