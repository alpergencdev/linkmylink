package com.example.demo;

import com.example.demo.models.UserType;
import com.example.demo.utils.DatabaseRepository;


public class DynamoDBDemo {
    public static void main(String[] args) {
        DatabaseRepository dbr = DatabaseRepository.getInstance();
        dbr.createURLTable();
        dbr.createUserTable();
    }
}
