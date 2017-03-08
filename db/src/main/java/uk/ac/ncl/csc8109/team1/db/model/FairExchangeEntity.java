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
    private String lastMessage;
    @DynamoDBAttribute
    private int stage;
    @DynamoDBAttribute
    private String fileKey;
    @DynamoDBAttribute
    private String regque;
    @DynamoDBAttribute
    private String proque;

    public FairExchangeEntity() {
    }

    public FairExchangeEntity(UUID uuid, String toID, String fromID) {
        this.uuid = uuid;
        this.toID = toID;
        this.fromID = fromID;
    }

    public String getRegque() {
        return regque;
    }

    public void setRegque(String regque) {
        this.regque = regque;
    }

    public String getProque() {
        return proque;
    }

    public void setProque(String proque) {
        this.proque = proque;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
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

    public UUID getUuid() {
        return uuid;
    }

    public String getToID() {
        return toID;
    }

    public String getFromID() {
        return fromID;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
}
