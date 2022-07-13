package com.mauriciotogneri.idlebattle.server;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class SslConfiguration
{
    public final String keystorePath;
    public final String keystorePass;

    private SslConfiguration(String keystorePath, String keystorePass)
    {
        this.keystorePath = keystorePath;
        this.keystorePass = keystorePass;
    }

    @NotNull
    public static SslConfiguration fromFile()
    {
        try
        {
            InputStream inputStream = Files.newInputStream(Paths.get("ssl.properties"));
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();

            String keystorePath = properties.getProperty("KEYSTORE_PATH");
            String keystorePass = properties.getProperty("KEYSTORE_PASS");

            return new SslConfiguration(keystorePath, keystorePass);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
