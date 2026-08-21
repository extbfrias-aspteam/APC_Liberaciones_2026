package mx.net.asp.asp_pago_payments.utilerias;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH); // "Oct 8, 2012"

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return formatter.parse(json.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException("Error parsing date: " + json.getAsString(), e);
        }
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(src));
    }
}