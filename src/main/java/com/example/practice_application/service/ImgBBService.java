package com.example.practice_application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
public class ImgBBService {

    @Value("${imgbb.api.key}")
    private String imgbbApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String uploadImage(MultipartFile imageFile, int expirationSeconds) throws Exception {
        String url = "https://api.imgbb.com/1/upload?key=" + imgbbApiKey + "&expiration=" + expirationSeconds;
        String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("image", base64Image);
        formData.add("name", imageFile.getOriginalFilename());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.has("data") && root.get("data").has("display_url")) {
                    return root.get("data").get("display_url").asText();
                } else {
                    throw new Exception("ImgBB response does not contain display_url: " + response.getBody());
                }
            } catch (Exception e) {
                throw new Exception("Failed to parse ImgBB response: " + e.getMessage() + ", Raw response: " + response.getBody());
            }
        } else {
            throw new Exception("ImgBB upload failed with status: " + response.getStatusCode());
        }
    }
}