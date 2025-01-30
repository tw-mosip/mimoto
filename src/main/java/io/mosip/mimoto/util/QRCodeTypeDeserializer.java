package io.mosip.mimoto.util;

import com.google.gson.*;
import io.mosip.mimoto.model.QRCodeType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class QRCodeTypeDeserializer implements JsonDeserializer<QRCodeType> {
    @Override
    public QRCodeType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String value = json.getAsString();

        for (QRCodeType type : QRCodeType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new JsonParseException("Issuer QRCodeType is invalid - " + value + ". Allowed values are: " + Arrays.toString(QRCodeType.values()));
    }
}