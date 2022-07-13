package com.mauriciotogneri.idlebattle.utils;

import com.google.gson.Gson;
import com.mauriciotogneri.idlebattle.messages.InputMessage;

public class Json
{
    private static final Gson gson = new Gson();

    public static InputMessage message(String input)
    {
        return gson.fromJson(input, InputMessage.class);
    }

    public static String string(Object object)
    {
        return gson.toJson(object);
    }
}
