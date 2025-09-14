package com.team.updevic001.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeoService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getLocation(String ip) {
        try {
            String city = "";
            String country = "";
            String url = "http://ip-api.com/json/" + ip;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                city = (String) response.get("city");
                country = (String) response.get("country");
            }
            return (city != null ? city : "Unknown") + ", " + (country != null ? country : "Unknown");
        } catch (Exception e) {
            return "Unknown, Unknown";
        }
    }
}
