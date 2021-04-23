package com.example.demo.user;

import com.example.demo.models.UserType;
import com.example.demo.utils.DatabaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="api/user")
public class UserController {
    private DatabaseRepository databaseRepository;

    public UserController(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }
    @GetMapping(path="")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password){
        String result = databaseRepository.login(email, password);

        if( result == null) {
            return new ResponseEntity<String>(
                    "A server error has occurred.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        else if( result.equals("INVALID_LOGIN")) {
            return new ResponseEntity<String>(
                    "No user found with given credentials.",
                    HttpStatus.BAD_REQUEST
            );
        }
        else {
            return new ResponseEntity<String>(
                    result,
                    HttpStatus.OK
            );
        }
    }
    @PostMapping()
    public ResponseEntity<String> signup(@RequestParam String email, @RequestParam String password, @RequestParam String userType){
        UserType ut;
        switch(userType) {
            case "B2B": ut = UserType.B2B; break;
            case "B2C": ut = UserType.B2C; break;
            default: return new ResponseEntity<String>(
                    "No type.",
                    HttpStatus.BAD_REQUEST
            );
        }
        String result = databaseRepository.signUp(email, password, ut);

        if( result == null) {
            return new ResponseEntity<String>(
                    "A server error has occurred.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        else if (result.equals("EMAIL_EXISTS")) {
            return new ResponseEntity<String>(
                    "A user with the given e-mail address already exists.",
                    HttpStatus.BAD_REQUEST
            );
        }
        else {
            return new ResponseEntity<String>(
                    result,
                    HttpStatus.CREATED
            );
        }
    }
}
