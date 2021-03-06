package com.mauriciotogneri.idlebattle.statistics;

import com.mauriciotogneri.idlebattle.formatter.DateFormatter;
import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;
import com.mauriciotogneri.idlebattle.types.EndReason;
import com.mauriciotogneri.idlebattle.utils.Json;
import com.mauriciotogneri.idlebattle.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Statistics
{
    private final String matchId;
    private final String fileName;
    private final MatchConfiguration configuration;
    private final LocalDateTime timestampStart;
    private final List<ActionStats> actions = new ArrayList<>();
    private LocalDateTime timestampEnd;

    public Statistics(String matchId, MatchConfiguration configuration)
    {
        this.matchId = matchId;
        this.configuration = configuration;
        this.timestampStart = LocalDateTime.now();
        this.fileName = String.format("%s_%s", DateFormatter.format(timestampStart), matchId);
    }

    public void addAction(ActionStats action)
    {
        actions.add(action);
    }

    private @NotNull String content(int matchTime, EndReason endReason, PlayerStats[] players)
    {
        MatchStats matchStats = new MatchStats(matchId,
                                               DateFormatter.format(timestampStart),
                                               DateFormatter.format(timestampEnd),
                                               matchTime,
                                               endReason,
                                               configuration,
                                               players,
                                               actions);

        return Json.string(matchStats);
    }

    public void collect(int matchTime, EndReason endReason, PlayerStats[] players)
    {
        this.timestampEnd = LocalDateTime.now();

        try
        {
            FileWriter fileWriter = new FileWriter("stats/" + fileName + ".json");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content(matchTime, endReason, players));
            bufferedWriter.flush();
        }
        catch (Exception e)
        {
            Logger.onError(e);
        }
    }
}
