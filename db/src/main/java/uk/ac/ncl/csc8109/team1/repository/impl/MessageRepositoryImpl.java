package uk.ac.ncl.csc8109.team1.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import uk.ac.ncl.csc8109.team1.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.repository.DynamoDBConnectionPools;
import uk.ac.ncl.csc8109.team1.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.repository.MessageRepository;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Huan on 2017/3/2.
 */
@Repository
public class MessageRepositoryImpl implements MessageRepository{
    private String tableName = "MessageTable";
    private Logger log = Logger.getLogger(MessageRepositoryImpl.class);
    private FileRepository fileDao = new FileRepositoryImpl();


    @Override
    public void storeMessage(UUID uuid, FairExchangeEntity entity) {
        AmazonDynamoDBClient dbClient = DynamoDBConnectionPools.getInstance().getConnection();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        mapper.save(entity);
        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);
    }


    @Override
    public FairExchangeEntity getMessage(UUID uuid) {
        AmazonDynamoDBClient dbClient = DynamoDBConnectionPools.getInstance().getConnection();
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
        AmazonDynamoDBClient dbClient = DynamoDBConnectionPools.getInstance().getConnection();
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
