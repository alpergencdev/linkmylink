package com.example.demo.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName="urls")
public class ShortenedURL {
    // Attributes
    @DynamoDBHashKey(attributeName = "key")
    private String key;

    @DynamoDBAttribute(attributeName = "url")
    private String url;

    @DynamoDBAttribute(attributeName = "creatorID")
    private int creatorID;

    @DynamoDBAttribute(attributeName = "creationDate")
    private String creationDate;

}
