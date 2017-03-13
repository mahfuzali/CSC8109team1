package uk.ac.ncl.csc8109.team1.db.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created by Huan on 2017/2/28.
 */

@DynamoDBTable(tableName = "register_user")
public class RegisterEntity {
    @DynamoDBHashKey
    private String id;
    @DynamoDBAttribute
    private String publicKey;
    @DynamoDBAttribute
    private String queueName;
    public RegisterEntity(){}

    public RegisterEntity(String id,String publicKey, String queueName){
        this.id = id;
        this.publicKey = publicKey;
        this.queueName = queueName;
    }
    public RegisterEntity(String id,String publicKey){
        this(id,publicKey,null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
