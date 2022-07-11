package com.mauriciotogneri.idlebattle.server;

import com.google.gson.Gson;
import com.mauriciotogneri.idlebattle.game.Message;

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
