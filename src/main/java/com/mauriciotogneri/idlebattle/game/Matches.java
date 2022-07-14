package com.mauriciotogneri.idlebattle.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matches
{
    private final Map<String, Match> matchesById = new HashMap<>();
    private final Map<WebSocketSession, Match> matchesBySocket = new HashMap<>();
    private final List<Match> matchesList = new ArrayList<>();

    public void add(Match match)
    {
        matchesById.put(match.id(), match);

        for (Player player : match.players())
        {
            matchesBySocket.put(player.webSocket(), match);
        }

        matchesList.add(match);
    }

    public void remove(@NotNull Match match)
    {
        matchesById.remove(match.id());

        while (matchesBySocket.values().remove(match))
        {
        }

        matchesList.remove(match);
    }

    public List<Match> list()
    {
        return matchesList;
    }

    @Nullable
    public Match get(WebSocketSession webSocket)
    {
        return matchesBySocket.get(webSocket);
    }

    @Nullable
    public Match get(WebSocketSession webSocket, String matchId)
    {
        Match match = matchesById.get(matchId);

        if ((match != null) && match.hasConnection(webSocket))
        {
            return match;
        }
        else
        {
            return null;
        }
    }
}
