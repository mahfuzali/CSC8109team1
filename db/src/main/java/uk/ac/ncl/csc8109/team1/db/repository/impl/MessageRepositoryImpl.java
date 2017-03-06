package uk.ac.ncl.csc8109.team1.db.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.log4j.Logger;

import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.repository.DynamoDBConnectionPools;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.MessageRepository;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Huan on 2017/3/2.
 */

public class MessageRepositoryImpl implements MessageRepository{
    private String tableName = "message_table";
    private Logger log = Logger.getLogger(MessageRepositoryImpl.class);
    private FileRepository fileDao = new FileRepositoryImpl();


    @Override
    public void storeMessage(UUID uuid, FairExchangeEntity entity) {
        AmazonDynamoDB dbClient = DynamoDBConnectionPools.getInstance().getConnection();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        mapper.save(entity);
        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);
    }


    @Override
    public FairExchangeEntity getMessage(UUID uuid) {
        AmazonDynamoDB dbClient = DynamoDBConnectionPools.getInstance().getConnection();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        FairExchangeEntity result = mapper.load(FairExchangeEntity.class,uuid);
        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);
        return result;
    }

    @Override
    public Map<UUID, FairExchangeEntity> getUnfinishedMessage() {
        //todo is this necessary?
        return null;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        AmazonDynamoDB dbClient = DynamoDBConnectionPools.getInstance().getConnection();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        FairExchangeEntity result = mapper.load(FairExchangeEntity.class,uuid);
        if(result!=null){
            mapper.delete(result);
        }

    }

    @Override
    public void deleteExpiredMessage() {
        //todo set expired time
    }
}
