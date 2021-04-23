package com.example.demo.url;

import com.example.demo.utils.DatabaseRepository;
import org.springframework.stereotype.Component;

@Component
public class UrlService {

    private static final String allowedString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private char[] allowedCharacters = allowedString.toCharArray();
    private int base = 62;
    private DatabaseRepository databaseRepository;

    public UrlService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public String createShortLink(){
        String shortURL = "";
        while(true){
            for(int i =0; i < 5; i++)
                shortURL += allowedCharacters[(int) (Math.random() *(base)) ];
            if(!databaseRepository.doesKeyExist(shortURL)) {
                return shortURL;
            }
        }
    }
}
