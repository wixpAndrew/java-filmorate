package ru.yandex.practicum.filmorate.UtilClasses;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

import java.io.IOException;
import java.time.Duration;

public class DurationUtil extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String durationString = jsonParser.getText();
        return Duration.parse(durationString);
    }
}
