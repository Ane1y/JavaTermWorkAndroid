package ru.polytech.client;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResultConverter<T> {
    private Gson gson;

    public ResultConverter(Gson gson) {
        this.gson = gson;
    }

    public List<T> getListFromResult(String result, Type listType, Type type) {
        List<T> list = new ArrayList<>();
        System.out.println(result);
        try {
            list = gson.fromJson(result, listType);
        } catch (JsonIOException | JsonSyntaxException ex) {
            T obj = gson.fromJson(result, type);
            list.add(obj);
        }
        System.out.println(list.size());
        return list;
    }
}
