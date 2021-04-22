package com.example.demo;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.example.demo.models.ShortenedURL;
import com.example.demo.models.User;
import com.example.demo.models.UserType;

import java.util.Arrays;

public class DynamoDBDemo {
    public static void main(String[] args) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();

        DynamoDBMapper mapper = new DynamoDBMapper(client);
        save(mapper);
    }

    private static void load(DynamoDBMapper mapper) {
        // Basic load
        ShortenedURL url = new ShortenedURL();
        url.setKey("abcde");

        ShortenedURL result = mapper.load(url);
        System.out.println("url");
    }

    private static void createUserTable(DynamoDBMapper mapper, AmazonDynamoDB client) {
        CreateTableRequest ctr = mapper.generateCreateTableRequest(User.class);
        ctr.setProvisionedThroughput( new ProvisionedThroughput(1L, 1L));
        client.createTable(ctr);
    }

    private static void createURLTable(DynamoDBMapper mapper, AmazonDynamoDB client) {
        CreateTableRequest ctr = mapper.generateCreateTableRequest(ShortenedURL.class);
        ctr.setProvisionedThroughput( new ProvisionedThroughput(1L, 1L));
        client.createTable(ctr);
    }

    private static void save(DynamoDBMapper mapper) {
        ShortenedURL url = new ShortenedURL();
        url.setUrl("google.com");
        url.setCreationDate("22-04-2020");
        url.setKey("abcde");
        url.setCreatorID(1);

        mapper.save(url);

        User u = new User();
        u.setDailyLimit(5);
        u.setUserType(UserType.B2C);
        u.setPassword("abcabc");
        u.setEmail("asdasd@asdasd.com");
        mapper.save(u);
    }
}
