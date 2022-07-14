package com.mauriciotogneri.idlebattle.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class MatchTimes
{
    private final BufferedWriter bufferedWriter;

    public MatchTimes()
    {
        try
        {
            FileWriter fileWriter = new FileWriter("match_times.txt", true);
            bufferedWriter = new BufferedWriter(fileWriter);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void write(int time)
    {
        try
        {
            bufferedWriter.write(String.format("%s%n", time));
            bufferedWriter.flush();
        }
        catch (Exception e)
        {
            Logger.onError(e);
        }
    }
}
