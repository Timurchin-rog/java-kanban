package service.http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localTime) throws IOException {
        jsonWriter.value(localTime.format(timeFormatter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), timeFormatter);
    }
}