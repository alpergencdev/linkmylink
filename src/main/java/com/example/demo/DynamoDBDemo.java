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
import com.example.demo.utils.DatabaseRepository;


public class DynamoDBDemo {
    public static void main(String[] args) {

        DatabaseRepository dbr = DatabaseRepository.getInstance();
        dbr.createUserTable();
        dbr.createURLTable();
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

}
