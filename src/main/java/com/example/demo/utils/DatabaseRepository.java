package com.example.demo.utils;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.demo.models.ShortenedURL;
import com.example.demo.models.User;
import com.example.demo.models.UserType;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseRepository {

    private static String endpoint = "http://localhost:8000/";

    private static String accessKey = "cs443acckey";

    private static String secretKey = "cs443seckey";

    private static String region = "home";

    private static DatabaseRepository instance = null;

    private AmazonDynamoDB client;

    private DynamoDBMapper mapper;

    private DatabaseRepository() {
        AWSCredentialsProvider creds = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey)
        );

        client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(creds)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();

        mapper = new DynamoDBMapper(client);
    }

    public static DatabaseRepository getInstance() {
        if(instance == null) {
            instance = new DatabaseRepository();
        }

        return instance;
    }

    public String login(String email, String password) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":em", new AttributeValue().withS(email));
        eav.put(":pw", new AttributeValue().withS(password));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :em and password = :pw")
                .withExpressionAttributeValues(eav)
                .withLimit(1);

        List<User> scanResult = mapper.scan(User.class, scanExpression);
        if(scanResult.isEmpty()) {
            return null;
        }
        else {
            return scanResult.get(0).getUserID();
        }
    }

    public String signUp(String email, String password, UserType userType) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":em", new AttributeValue().withS(email));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :em")
                .withExpressionAttributeValues(eav)
                .withLimit(1);

        List<User> scanResult = mapper.scan(User.class, scanExpression);
        if(!scanResult.isEmpty()) {
            return null;
        }
        else {
            User u = new User();
            u.setEmail(email);
            u.setPassword(password);
            u.setUserType(userType);
            switch(userType) {
                case B2B:
                case ADMIN:
                    u.setDailyLimit(20);
                    break;
                case B2C:
                    u.setDailyLimit(5);
                    break;
                default:
                    u.setDailyLimit(0);
            }

            mapper.save(u);
            return u.getUserID();
        }
    }

    public void createUserTable() {
        CreateTableRequest ctr = mapper.generateCreateTableRequest(User.class);
        ctr.setProvisionedThroughput( new ProvisionedThroughput(1L, 1L));
        client.createTable(ctr);
    }

    public void createURLTable() {
        CreateTableRequest ctr = mapper.generateCreateTableRequest(ShortenedURL.class);
        ctr.setProvisionedThroughput( new ProvisionedThroughput(1L, 1L));
        client.createTable(ctr);
    }
}