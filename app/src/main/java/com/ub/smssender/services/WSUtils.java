package com.ub.smssender.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by ulises on 22/02/17.
 */

public class WSUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();


    public static IServiceMensajes webServices(){
        return SingletonRetrofit.getIntance().create(IServiceMensajes.class);
    }

    public static String asStringJson(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T readValue(Object content, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return MAPPER.readValue(MAPPER.writeValueAsString(content), valueTypeRef);
    }

    public static <T> T readValue(Object content, Class<T> clase) throws IOException, JsonParseException, JsonMappingException {
        return MAPPER.readValue(MAPPER.writeValueAsString(content), clase);
    }

}
