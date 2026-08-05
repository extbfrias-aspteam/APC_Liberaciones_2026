package mx.net.asp.vf_core_api.utilerias;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convierte un String JSON a un objeto de tipo T.
     */
    public <T> T fromStringToJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear JSON a " + clazz.getSimpleName());
        }
    }

    /**
     * Convierte un String JSON a un tipo complejo (listas, mapas, arrays, etc.)
     */
    public <T> T fromStringToJson(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear JSON a tipo complejo");
        }
    }

    /**
     * Convierte un objeto a String JSON.
     */
    public String toStringJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir objeto a JSON");
        }
    }
}
