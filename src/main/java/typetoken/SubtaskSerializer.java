package typetoken;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Subtask;

import java.lang.reflect.Type;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("id", subtask.getId());
        json.addProperty("name", subtask.getName());
        json.addProperty("description", subtask.getDescription());
        json.addProperty("startTime", subtask.getStartTime().toString());
        json.addProperty("duration", subtask.getDuration().toString());
        // Сериализуем эпик только по id, чтобы избежать циклической зависимости
        if (subtask.getEpic() != null) {
            json.addProperty("epicId", subtask.getEpic().getId());
        }
        return json;
    }
}