package com.example.demo.url;

import com.example.demo.models.ShortenedURL;
import com.example.demo.utils.DatabaseRepository;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
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
    public JSONObject shortenURL( @RequestParam String userID, @RequestParam String url, @RequestParam(required = false) String customKey) {

        try {
            URL u = new URL(url);
            url = u.toURI().toString();

        }
        catch( Exception e){
            url = "https://" + url;
        }
        String shortLink;
        if(customKey == null) {
            shortLink = urlService.createShortLink();
        }
        else {
            if(!customKey.matches("[a-zA-Z0-9]+")) {
                JSONObject o = new JSONObject();
                o.put("status", 400);
                o.put("message", "The specified custom link must be alphanumeric.");
                return o;
            }
            else if ( databaseRepository.doesKeyExist(customKey)) {
                JSONObject o = new JSONObject();
                o.put("status", 400);
                o.put("message", "The specified custom link already exists in the database.");
                return o;
            }
            else {
                shortLink = customKey;
            }
        }
        String result = databaseRepository.shortenURL(shortLink, url, userID);
        System.out.println(result);
        if(result == null){
            JSONObject o = new JSONObject();
            o.put("status", 500);
            o.put("message", "A server error has occurred.");
            return o;
        }
        else if(result.equals("DAILY_LIMIT_EXCEEDED")){
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "You have exceeded your daily limit.");
            return o;
        }
        else if(result.equals("NO_SUCH_USER")){
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "There is no user with the specified ID.");
            return o;
        }
        JSONObject o = new JSONObject();
        o.put("status", 200);
        o.put("urlKey", result);
        return o;

    }

    @GetMapping(path="/links")
    public JSONObject getLinks(@RequestParam String userID){
        List<ShortenedURL> urlsOfUser = databaseRepository.getAllUserURLs(userID);
        if(!databaseRepository.doesUserExist(userID)){
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "There is no user with the specified ID.");
            return o;
        }
        if(urlsOfUser == null){
            JSONObject o = new JSONObject();
            o.put("status", 500);
            o.put("message", "A server error has occurred.");
            return o;
        }
        JSONArray results = new JSONArray();
        for(var url : urlsOfUser){
            JSONObject cur = new JSONObject();
            cur.put("key", url.getKey());
            cur.put("url", url.getUrl());
            cur.put("creationDate", url.getCreationDate());
            cur.put("visitTime", url.getVisitTime());
            results.add(cur);
        }
        JSONObject o = new JSONObject();
        o.put("status", 200);
        o.put("keys", results);
        return o;
    }

    @GetMapping(path = "/{shortURL}")
    public JSONObject redirect(@PathVariable String shortURL, HttpServletResponse response) throws IOException {
        if(!databaseRepository.doesKeyExist(shortURL)) {
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "No such URL exists.");
            return o;
        }
        String result = databaseRepository.getURL(shortURL);

        if(result == null) {
            JSONObject o = new JSONObject();
            o.put("status", 500);
            o.put("message", "A server error has occurred.");
            return o;
        }
        if(result.equals("NO_SUCH_URL")) {
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "No such URL exists.");
            return o;
        }
        else if(result.equals("URL_EXPIRED")) {
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "This URL has expired.");
            return o;
        }
        databaseRepository.incrementVisit(shortURL); //increment the visited time
        response.sendRedirect(result);
        JSONObject o = new JSONObject();
        o.put("status", 200);
        o.put("url", result);
        return o;
    }


}
