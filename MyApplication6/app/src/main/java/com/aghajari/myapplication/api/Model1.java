package com.aghajari.myapplication.api;

import com.google.gson.Gson;

import java.util.List;

public class Model1 {

    public List<Model2> users;
    public List<Model2> fields;

    public static Model1 generate(String json) {
        return new Gson().fromJson(json, Model1.class);
    }
}
