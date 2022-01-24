package com.assignment.addressBook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class KafkaConsumerService {


    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    // total records * *
    private int t_add_err = 0, t_update_err = 0, t_del_err = 0;
    private int t_add_succ = 0, t_update_succ = 0, t_del_succ = 0;

    @KafkaListener(topics = "ab_deleted", containerFactory = "kafkaListenerFactory")
    public void consumeDeleted(String s){
        Map<String, Object> map = string2JSON(s);
        if(map != null && map.containsKey("status")){
            if(map.containsValue("success"))
                t_del_succ++;
            else
                t_del_err++;
            logger();
            return;
        }
        System.out.println("Kafka(Consumer) : Error parsing consumeDeleted Json");
    }

    @KafkaListener(topics = "ab_updated", containerFactory = "kafkaListenerFactory")
    public void consumeUpdated(String s){
        Map<String, Object> map = string2JSON(s);

        if(map != null && map.containsKey("status")){
            if(map.containsValue("success"))
                t_update_succ++;
            else
                t_update_err++;
            logger();
            return;
        }
        System.out.println("Kafka(Consumer) : Error parsing consumeUpdated Json");
    }

    @KafkaListener(topics = "ab_added", containerFactory = "kafkaListenerFactory")
    public void consumeAdded(String s){
        Map<String, Object> map = string2JSON(s);
        if(map != null && map.containsKey("status")){
            if(map.containsValue("success"))
                t_add_succ += Integer.parseInt(map.getOrDefault("totalRecords", 0).toString());
            else
                t_add_err++;
            logger();
            return;
        }
        int error_count = findErrorCount(s);
        if(error_count > 0)
            t_add_err += error_count;
        else
            t_add_succ++;
        logger();
        System.out.println(s);
        return;
    }


    private Map<String, Object> string2JSON(String json_str){
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(json_str, Map.class);
            return map;
        } catch (IOException e) {
            return null;
        }
    }

    // A simple workaround for multi error json received though consumeAdd...
    // Quite error-prone but works for understanding Kafka logic, maybe...
    private int findErrorCount(String s){
        int i = 0;
        for(char c : s.toCharArray()){
            if(c == '{')
                i++;
        }
        return i/2;
    }


    @Override
    public String toString(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm z");
        String beautify =
                "\nAnalytics from " + zonedDateTime.format(dtf) + " (Kafka) :-\n" +
                "\nAdded " + t_add_succ + " records & had " + t_add_err + " errors." +
                "\nUpdated " + t_update_succ + " records & had " + t_update_err + " errors." +
                "\nDeleted " + t_del_succ + " records & had " + t_del_err + " errors.\n" ;
        return beautify;
    }

    public void logger(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS z");

        System.out.println("\nKafka Listener ("+ zonedDateTime.format(dtf) +") > Added " + t_add_succ + "(" + t_add_err + ") | " +
                "Updated " + t_update_succ + "(" + t_update_err + ") | " +
                "Deleted " + t_del_succ + "(" + t_del_err + ")" );
    }
}
