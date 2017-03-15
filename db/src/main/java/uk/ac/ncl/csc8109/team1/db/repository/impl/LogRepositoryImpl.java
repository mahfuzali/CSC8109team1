package uk.ac.ncl.csc8109.team1.db.repository.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.log4j.Logger;
import uk.ac.ncl.csc8109.team1.db.model.LogEntity;
import uk.ac.ncl.csc8109.team1.db.repository.DynamoDBConnectionPools;
import uk.ac.ncl.csc8109.team1.db.repository.FileRepository;
import uk.ac.ncl.csc8109.team1.db.repository.LogRepository;

/**
 * Created by Huan on 2017/3/15.
 */
public class LogRepositoryImpl implements LogRepository{
    private Logger log = Logger.getLogger(LogRepositoryImpl.class);

    @Override
    public void storeLog(LogEntity entity) {
        AmazonDynamoDB dbClient = DynamoDBConnectionPools.getInstance().getConnection();
        DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
        mapper.save(entity);
        DynamoDBConnectionPools.getInstance().returnConnection(dbClient);
    }
}
