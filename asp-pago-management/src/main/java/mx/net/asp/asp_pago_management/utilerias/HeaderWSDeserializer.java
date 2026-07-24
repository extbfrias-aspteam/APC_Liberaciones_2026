package mx.net.asp.asp_pago_management.utilerias;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import mx.net.asp.asp_pago_management.model.HeaderWS;

import java.io.IOException;

public class HeaderWSDeserializer extends JsonDeserializer<HeaderWS> {
    @Override
    public HeaderWS deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return (value == null || value.trim().isEmpty()) ? null : new HeaderWS(); 
    }
}
