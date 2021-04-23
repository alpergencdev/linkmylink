package com.example.demo.utils;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.demo.models.ShortenedURL;
import com.example.demo.models.User;
import com.example.demo.models.UserType;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.JSONObject;
@Component
public class DatabaseRepository {

    private static String accessKey = AWSKeys.ACCESS_KEY;

    private static String secretKey = AWSKeys.SECRET_KEY;

    private static DatabaseRepository instance = null;

    private static final long THIRTY_DAYS_IN_MSEC = 30L * 24L * 60L * 60L * 1000L;

    private AmazonDynamoDB client;

    private DynamoDBMapper mapper;

    private DatabaseRepository() {
        AWSCredentialsProvider creds = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey)
        );

        client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(creds)
                .withRegion(Regions.EU_CENTRAL_1)
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
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            String passwordHash = DigestUtils.sha256Hex(password);
            eav.put(":em", new AttributeValue().withS(email));
            eav.put(":pw", new AttributeValue().withS(passwordHash));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("email = :em and password = :pw")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<User> scanResult = mapper.scan(User.class, scanExpression);
            if(scanResult.isEmpty()) {
                return "INVALID_LOGIN";
            }
            else {
                return scanResult.get(0).getUserID();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String signUp(String email, String password, UserType userType) {
        try {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":em", new AttributeValue().withS(email));


            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("email = :em")
                    .withExpressionAttributeValues(eav)
                    .withLimit(1);

            List<User> scanResult = mapper.scan(User.class, scanExpression);
            if(!scanResult.isEmpty()) {
                return "EMAIL_EXISTS";
            }
            else {
                User u = new User();
                u.setEmail(email);
                String passwordHash = DigestUtils.sha256Hex(password);
                u.setPassword(passwordHash);
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
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean doesUserExist(String userID) {
        User u = new User();
        u.setUserID(userID);
        u = mapper.load(u);
        return u != null;
    }

    public boolean doesKeyExist(String shortKey) {
        ShortenedURL s = new ShortenedURL();
        s.setKey(shortKey);
        s = mapper.load(s);
        return s != null;
    }

    public String shortenURL(String shortKey, String url, String userID) {
        try {
            User u = new User();

            u.setUserID(userID);
            u = mapper.load(u);
            if(u == null){
                return "NO_SUCH_USER";
            }
            if(u.getDailyLimit() <= 0) {
                return "DAILY_LIMIT_EXCEEDED";
            }

            ShortenedURL s = new ShortenedURL();
            s.setCreatorID(userID);
            s.setKey(shortKey);
            s.setCreationDate(DateTime.now().toString("dd-MM-yyyy"));
            s.setUrl(url);

            mapper.save(s);

            u.setDailyLimit(u.getDailyLimit() - 1);
            mapper.save(u);

            return s.getKey();
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getURL(String shortKey) {
        try {
            ShortenedURL s = new ShortenedURL();
            s.setKey(shortKey);
            s = mapper.load(s);
            long nowMillis = new Date().getTime();
            long startMillis = new SimpleDateFormat("dd-MM-yyyy").parse(s.getCreationDate()).getTime();
            System.out.println(nowMillis - startMillis);
            System.out.println(THIRTY_DAYS_IN_MSEC);
            return (s == null) ? "NO_SUCH_URL" : (nowMillis - startMillis >= THIRTY_DAYS_IN_MSEC) ? "URL_EXPIRED" : s.getUrl();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<ShortenedURL> getAllUserURLs(String userID) {
        try {

            User u = new User();
            u.setUserID(userID);
            u = mapper.load(u);

            if(u == null)
                return null;

            DynamoDBScanExpression scanExpression;

            if(u.getUserType() == UserType.ADMIN) {
                scanExpression = new DynamoDBScanExpression();
            }
            else {
                Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                eav.put(":cid", new AttributeValue().withS(userID));

                scanExpression = new DynamoDBScanExpression()
                        .withFilterExpression("creatorID = :cid")
                        .withExpressionAttributeValues(eav);
            }
            List<ShortenedURL> scanResult = mapper.scan(ShortenedURL.class, scanExpression);

            List<ShortenedURL> result = new ArrayList<ShortenedURL>();

            for(ShortenedURL su : scanResult) {
                result.add(su);
            }

            return result;
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public Boolean incrementVisit(String shortenedURL){
        try {
            ShortenedURL url = new ShortenedURL();
            url.setKey(shortenedURL);
            url = mapper.load(url);
            if( url == null) {
                return false;
            }
            url.setVisitTime(url.getVisitTime() + 1);
            mapper.save(url);
            return true;
        } catch (Exception e) {
            System.out.println("An error has occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    @Scheduled(cron = "0 0 5 * * *", zone = "Europe/Istanbul")
    public boolean refreshDailyLimits() {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

            List<User> users = mapper.scan(User.class, scanExpression);

            for(User u : users) {
                if(u.getUserType() == UserType.ADMIN || u.getUserType() == UserType.B2B) {
                    u.setDailyLimit(20);
                }
                else if(u.getUserType() == UserType.B2C) {
                    u.setDailyLimit(5);
                }
                mapper.save(u);
            }

            return true;
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUserTable() {
        try {
            CreateTableRequest ctr = mapper.generateCreateTableRequest(User.class);
            ctr.setProvisionedThroughput( new ProvisionedThroughput(1L, 1L));
            client.createTable(ctr);
            return true;
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean createURLTable() {
        try {
            CreateTableRequest ctr = mapper.generateCreateTableRequest(ShortenedURL.class);
            ctr.setProvisionedThroughput( new ProvisionedThroughput(1L, 1L));
            client.createTable(ctr);
            return true;
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}