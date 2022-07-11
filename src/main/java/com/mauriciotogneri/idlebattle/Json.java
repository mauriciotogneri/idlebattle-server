package com.mauriciotogneri.idlebattle;

import com.google.gson.Gson;

public class Json
{
    private static final Gson gson = new Gson();

    public static Message message(String input) {
        return gson.fromJson(input, Message.class);
    }

    public static String string(Object object) {
        return gson.toJson(object);
    }
}
