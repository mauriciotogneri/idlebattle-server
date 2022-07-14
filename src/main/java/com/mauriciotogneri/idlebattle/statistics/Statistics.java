package com.mauriciotogneri.idlebattle.statistics;

import com.mauriciotogneri.idlebattle.formatter.DateFormatter;
import com.mauriciotogneri.idlebattle.types.EndReason;
import com.mauriciotogneri.idlebattle.utils.Json;
import com.mauriciotogneri.idlebattle.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics
{
    private final String matchId;
    private final String fileName;
    private final LocalDateTime timestampStart;
    private LocalDateTime timestampEnd;

    //private final Lane[] lanes;
    //private final MatchConfiguration configuration;

    public Statistics(String matchId)
    {
        this.matchId = matchId;
        this.timestampStart = LocalDateTime.now();
        this.fileName = String.format("%s_%s", DateFormatter.format(timestampStart), matchId);
    }

    private @NotNull String content(EndReason endReason, PlayerStats[] players)
    {
        Duration duration = Duration.between(timestampStart, timestampEnd);
        int matchTime = (int) duration.getSeconds();

        MatchStats matchStats = new MatchStats(matchId,
                                               DateFormatter.format(timestampStart),
                                               DateFormatter.format(timestampEnd),
                                               matchTime,
                                               endReason,
                                               players);

        return Json.string(matchStats);
    }

    public void collect(EndReason endReason, PlayerStats[] players)
    {
        this.timestampEnd = LocalDateTime.now();

        try
        {
            FileWriter fileWriter = new FileWriter("stats/" + fileName + ".json");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content(endReason, players));
            bufferedWriter.flush();
        }
        catch (Exception e)
        {
            Logger.onError(e);
        }
    }
}
