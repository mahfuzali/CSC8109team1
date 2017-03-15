package uk.ac.ncl.csc8109.team1.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by Huan on 2017/3/15.
 */
@DynamoDBTable(tableName = "log_table")
public class LogEntity {
    @DynamoDBHashKey
    private long timestamp;
    @DynamoDBAttribute
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
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

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
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
}
