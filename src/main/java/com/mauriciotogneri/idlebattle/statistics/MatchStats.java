package com.mauriciotogneri.idlebattle.statistics;

import com.mauriciotogneri.idlebattle.types.EndReason;
import com.mauriciotogneri.idlebattle.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchStats
{
    private final String fileName;
    private final LocalDateTime timestampStart;
    private LocalDateTime timestampEnd;

    public MatchStats(String id)
    {
        LocalDateTime date = LocalDateTime.now();
        this.fileName = String.format("%s_%s", date.format(DateTimeFormatter.ISO_DATE_TIME), id);

        this.timestampStart = LocalDateTime.now();
    }

    private @NotNull String content(EndReason endReason)
    {
        Duration duration = Duration.between(timestampStart, timestampEnd);
        long matchTime = duration.getSeconds();

        return "";
    }

    public void collect(EndReason endReason)
    {
        this.timestampEnd = LocalDateTime.now();

        try
        {
            FileWriter fileWriter = new FileWriter("stats/" + fileName + ".json");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content(endReason));
            bufferedWriter.flush();
        }
        catch (Exception e)
        {
            Logger.onError(e);
        }
    }
}
