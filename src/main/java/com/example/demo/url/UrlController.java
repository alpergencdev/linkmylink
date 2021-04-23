package com.example.demo.url;

import com.example.demo.models.ShortenedURL;
import com.example.demo.utils.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.net.URL;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="/api")
public class UrlController {

    // Dependency Injection
    private final UrlService urlService;
    private final DatabaseRepository databaseRepository;

    @Autowired
    public UrlController(UrlService urlService, DatabaseRepository databaseRepository){
        this.urlService = urlService;
        this.databaseRepository = databaseRepository;
    }
    @PostMapping(path="/links")
    public ResponseEntity<String> shortenURL( @RequestParam String userID, @RequestParam String url,HttpServletResponse response ) throws IOException {

        try {
            URL u = new URL(url);
            url = u.toURI().toString();

        }
        catch( Exception e){
            url = "https://" + url;
        }
        String shortLink = urlService.createShortLink();
        String result = databaseRepository.shortenURL(shortLink, url, userID);
        System.out.println(result);
        if(result == null){
            return new ResponseEntity<String>(
                "A server error has occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        else if(result.equals("DAILY_LIMIT_EXCEEDED")){
            return new ResponseEntity<String>(
                    "You have fulfilled your daily limit.",
                    HttpStatus.BAD_REQUEST
            );
        }
        else if(result.equals("NO_SUCH_USER")){
            return new ResponseEntity<String>(
                    "No user found with given credentials.",
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<String>(
                result,
                HttpStatus.CREATED
        );

    }

    @GetMapping(path="/links")
    public ResponseEntity<String> getLinks(@RequestParam String userID, HttpServletResponse response){
        List<ShortenedURL> urlsOfUser = databaseRepository.getAllUserURLs(userID);
        if(!databaseRepository.doesUserExist(userID)){
            return new ResponseEntity<String>(
                    "No such user exists.",
                    HttpStatus.BAD_REQUEST
            );
        }
        if(urlsOfUser == null){
            return new ResponseEntity<String>(
                    "A server error has occurred.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        String results = "";
        for(var url : urlsOfUser){
            results = results + url.toString() + "\n";
        }
        return new ResponseEntity<String>(
                results,
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/{shortURL}")
    public ResponseEntity<String> redirect(@PathVariable String shortURL, HttpServletResponse response) throws IOException {
        if(!databaseRepository.doesKeyExist(shortURL)) {
            return new ResponseEntity<String>(
                    "No such user exists.",
                    HttpStatus.BAD_REQUEST
            );
        }
        String result = databaseRepository.getURL(shortURL);

        if(result == null) {
            return new ResponseEntity<String>(
                    "A server error has occurred.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        if(result.equals("NO_SUCH_URL")) {
            return new ResponseEntity<String>(
                    "No such URL exists.",
                    HttpStatus.BAD_REQUEST
            );
        }
        else if(result.equals("URL_EXPIRED")) {
            return new ResponseEntity<String>(
                    "This URL has expired.",
                    HttpStatus.BAD_REQUEST
            );
        }
        databaseRepository.incrementVisit(shortURL); //increment the visited time
        response.sendRedirect(result);
        return null;
    }


}
