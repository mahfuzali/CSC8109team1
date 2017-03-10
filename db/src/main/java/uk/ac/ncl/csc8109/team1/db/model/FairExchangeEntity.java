package uk.ac.ncl.csc8109.team1.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.UUID;

/**
 * Created by Huan on 2017/3/2.
 */
@DynamoDBTable(tableName = "message_table")
public class FairExchangeEntity {
    @DynamoDBHashKey
    private String uuid;
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
    private String protocol;
    @DynamoDBAttribute
    private String senderqueue;
    @DynamoDBAttribute
    private String receiverqueue;
    @DynamoDBAttribute
    private long timestamp;

    public FairExchangeEntity() {
    }

    public FairExchangeEntity(String uuid, String toID, String fromID) {
        this.uuid = uuid;
        this.toID = toID;
        this.fromID = fromID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSenderqueue() {
        return senderqueue;
    }

    public void setSenderqueue(String senderqueue) {
        this.senderqueue = senderqueue;
    }

    public String getReceiverqueue() {
        return receiverqueue;
    }

    public void setReceiverqueue(String receiverqueue) {
        this.receiverqueue = receiverqueue;
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



    public void setToID(String toID) {
        this.toID = toID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
