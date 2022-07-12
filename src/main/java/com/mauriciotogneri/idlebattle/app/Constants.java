package com.mauriciotogneri.idlebattle.app;

public class Constants
{
    public static final int GAME_LOOP_STEP = 1000 / 60;

    public static int MINE_COST_MULTIPLIER = 100; // TODO: add to configuration?
    public static int ATTACK_COST_MULTIPLIER = 100; // TODO: add to configuration?
    public static double BLOCK_MULTIPLIER = 50; // TODO: add to configuration?
    public static double UNIT_BASE_DAMAGE = 0.01; // TODO: add to configuration?

    public static int INITIAL_MONEY = 500;
    public static int LOST_LANE_MONEY = 500;
    public static int LINE_REWARD = 500;

    public static int DIRECTION_UP = 1;
    public static int DIRECTION_DOWN = -1;
}
