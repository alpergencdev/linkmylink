package com.example.demo.utils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.example.demo.models.UserType;

public class UserTypeTranslator implements DynamoDBTypeConverter<Integer, UserType> {
    @Override
    public Integer convert(UserType object) {
        return object.ordinal();
    }

    @Override
    public UserType unconvert(Integer object) {
        if(object.intValue() == UserType.B2B.ordinal()) {
            return UserType.B2B;
        }
        else if(object.intValue() == UserType.B2C.ordinal()) {
            return UserType.B2C;
        }
        else if(object.intValue() == UserType.ADMIN.ordinal()) {
            return UserType.ADMIN;
        }

        return null;
    }
}
