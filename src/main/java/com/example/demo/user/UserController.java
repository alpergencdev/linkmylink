package com.example.demo.user;

import com.example.demo.models.UserType;
import com.example.demo.utils.DatabaseRepository;
import org.springframework.web.bind.annotation.*;
import org.json.simple.*;


@RestController
@RequestMapping(path="api/user")
public class UserController {
    private DatabaseRepository databaseRepository;

    public UserController(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }
    @GetMapping(path="")
    public JSONObject login(@RequestParam String email, @RequestParam String password){
        String result = databaseRepository.login(email, password);

        if( result == null) {
            JSONObject o = new JSONObject();
            o.put("status", 500);
            o.put("message", "A server error has occurred.");
            return o;
        }
        else if( result.equals("INVALID_LOGIN")) {
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "Invalid login.");
            return o;
        }
        else {
            JSONObject o = new JSONObject();
            o.put("status", 200);
            o.put("userID", result);
            return o;
        }
    }
    @PostMapping()
    public JSONObject signup(@RequestParam String email, @RequestParam String password, @RequestParam String userType){
        UserType ut;
        switch(userType) {
            case "B2B": ut = UserType.B2B; break;
            case "B2C": ut = UserType.B2C; break;
            default: JSONObject o = new JSONObject();
                o.put("status", 400);
                o.put("message", "Invalid user type.");
                return o;
        }
        String result = databaseRepository.signUp(email, password, ut);

        if( result == null) {
            JSONObject o = new JSONObject();
            o.put("status", 500);
            o.put("message", "A server error has occurred.");
            return o;
        }
        else if (result.equals("EMAIL_EXISTS")) {
            JSONObject o = new JSONObject();
            o.put("status", 400);
            o.put("message", "A user with the given e-mail address already exists.");
            return o;
        }
        else {
            JSONObject o = new JSONObject();
            o.put("status", 200);
            o.put("userID", result);
            return o;
        }
    }
}
