package uk.ac.ncl.csc8109.team1.db.repository;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Huan on 2017/2/28.
 */
public class DynamoDBConnectionPools {
    private static DynamoDBConnectionPools ourInstance = new DynamoDBConnectionPools();
    public static DynamoDBConnectionPools getInstance(){
        return ourInstance;
    }
    private int minNum = 1;
    private int maxNum = 100;
    private int counter = 0;
    private LinkedList<AmazonDynamoDB> pool;
    private ArrayList<AmazonDynamoDB> working;
    private DynamoDBConnectionPools(){
        pool = new LinkedList<>();
        working = new ArrayList<>();
        for(int i=0;i<minNum;i++){
            AmazonDynamoDB client = createConnection();
            pool.add(client);
        }
    }

    public AmazonDynamoDB getConnection(){
        if(pool.size()>0){
            AmazonDynamoDB client = pool.getFirst();
            working.add(client);
            return client;
        } else {
            AmazonDynamoDB client = createConnection();
            working.add(client);
            return client;
        }
    }

    public void returnConnection(AmazonDynamoDB client){
        if(working.contains(client)){
            working.remove(working.indexOf(client));
            if(counter<maxNum){
                pool.add(client);
            }else {
                closeConnection(client);
            }
        }
    }

    private void closeConnection(AmazonDynamoDB client){
        client.shutdown();
        counter--;
    }


    private AmazonDynamoDB createConnection(){
        counter++;
        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion(Regions.EU_WEST_1).withCredentials(new ClasspathPropertiesFileCredentialsProvider()).build();
        //AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());

        return client;
    }
}
