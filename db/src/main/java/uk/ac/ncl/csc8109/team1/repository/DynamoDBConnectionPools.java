package uk.ac.ncl.csc8109.team1.repository;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
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
    private LinkedList<AmazonDynamoDBClient> pool;
    private ArrayList<AmazonDynamoDBClient> working;
    private DynamoDBConnectionPools(){
        pool = new LinkedList<>();
        working = new ArrayList<>();
        for(int i=0;i<minNum;i++){
            AmazonDynamoDBClient client = createConnection();
            pool.add(client);
        }
    }

    public AmazonDynamoDBClient getConnection(){
        if(pool.size()>0){
            AmazonDynamoDBClient client = pool.getFirst();
            working.add(client);
            return client;
        } else {
            AmazonDynamoDBClient client = createConnection();
            working.add(client);
            return client;
        }
    }

    public void returnConnection(AmazonDynamoDBClient client){
        if(working.contains(client)){
            working.remove(working.indexOf(client));
            if(counter<maxNum){
                pool.add(client);
            }else {
                closeConnection(client);
            }
        }
    }

    private void closeConnection(AmazonDynamoDBClient client){
        client.shutdown();
        counter--;
    }


    private AmazonDynamoDBClient createConnection(){
        counter++;
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());
        Region region = Region.getRegion(Regions.EU_WEST_1);
        client.setRegion(region);
        return client;
    }
}
