package com.edu.university;
 
import com.edu.university.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
 
public class ApiResponseTest {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        ApiResponse<String> response = ApiResponse.created("Hello", "World");
        System.out.println(mapper.writeValueAsString(response));
    }
}
