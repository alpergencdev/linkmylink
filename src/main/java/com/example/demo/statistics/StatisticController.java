package com.example.demo.statistics;

import com.example.demo.utils.DatabaseRepository;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/statistics")

public class StatisticController {
    private final DatabaseRepository databaseRepository;

    public StatisticController(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }
    @GetMapping()
    public JSONObject getStatistics(@RequestParam String userID){
        return databaseRepository.getUsageStatistics(userID);
    }
}
