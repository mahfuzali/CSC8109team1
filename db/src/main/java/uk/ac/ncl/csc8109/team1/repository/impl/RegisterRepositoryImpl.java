package uk.ac.ncl.csc8109.team1.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import uk.ac.ncl.csc8109.team1.crypto.Base64Coder;
import uk.ac.ncl.csc8109.team1.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.repository.DynamoDBConnectionPools;
import uk.ac.ncl.csc8109.team1.repository.RegisterRepository;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Huan on 2017/2/28.
 */
@Repository
public class RegisterRepositoryImpl implements RegisterRepository {

    private static String tableName = "register_user";
    private Logger log = Logger.getLogger(RegisterRepositoryImpl.class);

    @Override
    public boolean checkAlreadyExist(String id) {
        assert (null != id);

        AmazonDynamoDBClient dbClient = DynamoDBConnectionPools.getInstance().getConnection();

        Map<String,AttributeValue> map = new HashMap<>();
//        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
//                .withAttributeValueList(new AttributeValue().withS(id));
        map.put("id",new AttributeValue().withS(id));
        GetItemRequest getItemRequest = new GetItemRequest().withTableName(tableName).withKey(map);
        GetItemResult result = dbClient.getItem(getItemRequest);
        log.info("check exist result: " + result);

        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);

        if(result.getItem() == null){
            return false;
        }
        return true;
    }

    @Override
    public boolean registerUser(RegisterEntity entity) {
        assert (entity != null);
        AmazonDynamoDBClient dbClient = DynamoDBConnectionPools.getInstance().getConnection();

        Map<String,AttributeValue> map = new HashMap<>();
        map.put("id",new AttributeValue(entity.getId()));
        map.put("publicKey",new AttributeValue().withB(ByteBuffer.wrap(Base64Coder.decode(entity.getPublicKey()))));
        log.info("insert"+entity.getId());
        PutItemRequest putItemRequest = new PutItemRequest(tableName, map);
        PutItemResult putItemResult = dbClient.putItem(putItemRequest);
        //todo check result?
        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);
        return true;
    }

    @Override
    public byte[] getPublicKeyById(String id) {
        assert(id != null);
        AmazonDynamoDBClient dbClient = DynamoDBConnectionPools.getInstance().getConnection();

        Map<String,AttributeValue> map = new HashMap<>();
        map.put("id",new AttributeValue().withS(id));
        GetItemRequest getItemRequest = new GetItemRequest().withTableName(tableName).withKey(map);
        GetItemResult result = dbClient.getItem(getItemRequest);
        log.info("get result: " + result);
        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);
        if(result.getItem() == null){
            return null;
        }
        return result.getItem().get("publicKey").getB().array();
    }

}
