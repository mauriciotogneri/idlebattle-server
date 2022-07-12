package com.mauriciotogneri.idlebattle.app;

public class Constants
{
    public static final int GAME_LOOP_STEP = 1000 / 60;

    public static int LANES = 5;
    public static double INITIAL_WALL_VALUE = 0.5;
    public static int READY_TIMEOUT = 3; // in seconds
    public static int MATCH_TIMEOUT = 60 * 5; // in seconds

    public static int INITIAL_MINE = 1;
    public static int INITIAL_ATTACK = 1;
    public static int MINE_COST_MULTIPLIER = 100;
    public static int ATTACK_COST_MULTIPLIER = 100;
    public static int INITIAL_MONEY = 500;
    public static int MONEY_RATE = 20;
    public static int LOST_LANE_MONEY = 500;
    public static int LINE_REWARD = 500;

    public static int UNIT_COST = 100;
    public static double BLOCK_MULTIPLIER = 50;
    public static double UNIT_SPEED = 0.2;
    public static double UNIT_BASE_DAMAGE = 0.01;

    public static int DIRECTION_UP = 1;
    public static int DIRECTION_DOWN = -1;
}
