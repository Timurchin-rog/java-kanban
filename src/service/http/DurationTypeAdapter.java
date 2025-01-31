package service.http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.toMinutes());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return Duration.ofMinutes(Integer.parseInt(jsonReader.nextString().substring(2, jsonReader.nextString().length() - 1)));
    }
}
