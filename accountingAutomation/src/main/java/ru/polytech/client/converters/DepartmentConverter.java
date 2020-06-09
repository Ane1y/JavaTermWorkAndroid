package  ru.polytech.client.converters;

import ru.polytech.client.entity.Departments;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

public class DepartmentConverter implements BaseConverter<Departments> {
    @Override
    public Class getConverterClass() {
        return Departments.class;
    }

    @Override
    public Departments deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Long id = object.get("id").getAsLong();
        String name = object.get("name").getAsString();
        return new Departments(id, name);
    }

    @Override
    public JsonElement serialize(Departments src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());
        object.addProperty("name", src.getName());
        return object;
    }
}