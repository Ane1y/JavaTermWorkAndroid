package ru.polytech.client.converters;

import ru.polytech.client.entity.Employees;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

public class EmployeeConverter implements BaseConverter<Employees> {
    @Override
    public Class getConverterClass() {
        return Employees.class;
    }

    @Override
    public Employees deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Long id = object.get("id").getAsLong();
        String first = object.get("firstName").getAsString();
        String last = object.get("lastName").getAsString();
        String pather = object.get("patherName").getAsString();
        String position = object.get("position").getAsString();
        Float salary = object.get("salary").getAsFloat();
        return new Employees(id, first, last, pather, position, salary);
    }

    @Override
    public JsonElement serialize(Employees src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());
        object.addProperty("firstName", src.getFirstName());
        object.addProperty("lastName", src.getLastName());
        object.addProperty("patherName", src.getPatherName());
        object.addProperty("position", src.getPosition());
        object.addProperty("salary", src.getSalary());
        return object;
    }
}
