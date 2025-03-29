package com.example.spring_template.util.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

    public static String convertToJSON(Object object)
    {
        var ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return convertToJSON(e);
        }
        return json;
    }


    public static <T> T convertToObject(Class<T> clazz,String jsonString)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return (T)mapper.readValue(jsonString, clazz);
        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}