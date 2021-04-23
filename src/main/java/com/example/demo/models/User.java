package com.example.demo.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.demo.utils.UserTypeTranslator;
import lombok.*;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "users")
public class User {

    @DynamoDBHashKey(attributeName = "userID")
    @DynamoDBAutoGeneratedKey
    public String userID;

    @DynamoDBAttribute(attributeName = "email")
    public String email;

    @DynamoDBAttribute(attributeName = "password")
    public String password;

    @DynamoDBAttribute(attributeName = "userType")
    @DynamoDBTypeConverted(converter = UserTypeTranslator.class)
    public UserType userType;

    @DynamoDBAttribute(attributeName = "dailyLimit")
    public int dailyLimit;

    @DynamoDBAttribute(attributeName = "totalRequests")
    public int totalRequests;
}
