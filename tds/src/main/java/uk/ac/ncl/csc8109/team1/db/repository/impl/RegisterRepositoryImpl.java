package uk.ac.ncl.csc8109.team1.db.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.log4j.Logger;

import uk.ac.ncl.csc8109.team1.db.model.FairExchangeEntity;
import uk.ac.ncl.csc8109.team1.db.model.RegisterEntity;
import uk.ac.ncl.csc8109.team1.db.repository.DynamoDBConnectionPools;
import uk.ac.ncl.csc8109.team1.db.repository.RegisterRepository;


import java.util.HashMap;
import java.util.Map;


/**
 * Created by Huan on 2017/2/28.
 */

public class RegisterRepositoryImpl implements RegisterRepository {

    private static String tableName = "register_user";
    private Logger log = Logger.getLogger(RegisterRepositoryImpl.class);

//    @Override
    public boolean checkAlreadyExist(String id) {
        assert (null != id);

        AmazonDynamoDB dbClient = DynamoDBConnectionPools.getInstance().getConnection();

        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        RegisterEntity result = mapper.load(RegisterEntity.class,id);
        log.info("check exist result: " + result);

        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);

        if(result == null){
            return false;
        }
        return true;
    }

//    @Override
    public boolean registerUser(RegisterEntity entity) {
        assert (entity != null);
        AmazonDynamoDB dbClient = DynamoDBConnectionPools.getInstance().getConnection();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        mapper.save(entity);
        log.info("insert"+entity.getId());
        //todo check result?
        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);
        return true;
    }

//    @Override
    public String getPublicKeyById(String id) {
        assert(id != null);
        AmazonDynamoDB dbClient = DynamoDBConnectionPools.getInstance().getConnection();

        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        RegisterEntity result = mapper.load(RegisterEntity.class,id);
        log.info("check exist result: " + result);

        if(result == null){
            return null;
        }
        return result.getPublicKey();
    }

}
