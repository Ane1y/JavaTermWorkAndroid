package  ru.polytech.client.converters;

import ru.polytech.client.entity.Departments;
import ru.polytech.client.entity.DepartmentsEmployees;
import ru.polytech.client.entity.Employees;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DepartmentsEmployeesConverter implements BaseConverter<DepartmentsEmployees> {
    @Override
    public Class getConverterClass() {
        return DepartmentsEmployees.class;
    }

    @Override
    public DepartmentsEmployees deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Long id = object.get("id").getAsLong();
        Departments department = context.deserialize(object.get("departments"), Departments.class);
        Employees employees = context.deserialize(object.get("employees"), Employees.class);

        return new DepartmentsEmployees(id,department, employees);
    }

    @Override
    public JsonElement serialize(DepartmentsEmployees src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());
        object.add("departments", context.serialize(src.getDepartments()));
        object.add("employees", context.serialize(src.getEmployees()));
        return object;
    }
}
