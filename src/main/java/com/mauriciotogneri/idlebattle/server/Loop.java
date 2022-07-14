package com.mauriciotogneri.idlebattle.server;

import com.mauriciotogneri.idlebattle.app.Constants;
import com.mauriciotogneri.idlebattle.utils.Logger;

public class Loop implements Runnable
{
    private final Handler handler;

    public Loop(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run()
    {
        long lastTimestamp = System.nanoTime();

        while (true)
        {
            try
            {
                Thread.sleep(Constants.GAME_LOOP_STEP);

                long currentTimestamp = System.nanoTime();
                double dt = (currentTimestamp - lastTimestamp) / 1000000000d; // in seconds
                lastTimestamp = currentTimestamp;

                handler.update(dt);
            }
            catch (Exception e)
            {
                Logger.onError(e);
            }
        }
    }
}
