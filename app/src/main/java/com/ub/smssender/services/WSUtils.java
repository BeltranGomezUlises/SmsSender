package com.ub.smssender.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ub.smssender.models.ModelMensaje;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulises on 22/02/17.
 */

public class WSUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static volatile List<ModelMensaje> mensajes;

    public static ServiceMensajes webServices(){
        return SRetrofit.getIntance().create(ServiceMensajes.class);
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

    public static void removeMensaje(String id){
        for (int i = 0; i < mensajes.size(); i++) {
            if (mensajes.get(i).get_id().equals(id)){
                mensajes.remove(i);
                System.out.println("removido: " + id);
                break;
            }
        }
    }

}
