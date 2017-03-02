package uk.ac.ncl.csc8109.team1.model;

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
    private final UUID uuid;
    @DynamoDBAttribute
    private final String toID;
    @DynamoDBAttribute
    private final String fromID;
    @DynamoDBAttribute
    private final byte[] fPublicKey;
    @DynamoDBAttribute
    private final byte[] tPublicKey;
    @DynamoDBAttribute
    private final String originHash;
    @DynamoDBAttribute
    private String receiptHash;
    @DynamoDBAttribute
    private final long createTime;
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute
    private FairExchangeStage stage;
    @DynamoDBAttribute
    private String fileKey;

    public FairExchangeEntity(UUID uuid, String toID, String fromID, byte[] fPublicKey, byte[] tPublicKey, String originHash, long createTime) {
        this.uuid = uuid;
        this.toID = toID;
        this.fromID = fromID;
        this.fPublicKey = fPublicKey;
        this.tPublicKey = tPublicKey;
        this.originHash = originHash;
        this.createTime = createTime;
        this.stage = FairExchangeStage.STAGE1;
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

    public byte[] getfPublicKey() {
        return fPublicKey;
    }

    public byte[] gettPublicKey() {
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
