package com.mauriciotogneri.idlebattle.types;

public enum Event
{
    // receive
    JOIN_PUBLIC,
    CREATE_PRIVATE,
    JOIN_PRIVATE,
    INCREASE_MINE,
    INCREASE_ATTACK,
    LAUNCH_UNITS,

    // send
    WAITING,
    MATCH_STARTED,
    PLAYER_UPDATE,
    MATCH_UPDATE,
    INVALID_MATCH_ID,
    INVALID_PLAYER_NAME,
    PLAYER_DISCONNECTED
}
