package com.mauriciotogneri.idlebattle.server;

import com.mauriciotogneri.idlebattle.app.Constants;

public class Loop implements Runnable
{
    private final MessageHandler messageHandler;

    public Loop(MessageHandler messageHandler)
    {
        this.messageHandler = messageHandler;
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

                messageHandler.update(dt);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
