package com.mauriciotogneri.idlebattle.formatter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter
{
    public static @NotNull String format(@NotNull LocalDateTime date)
    {
        return date.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
