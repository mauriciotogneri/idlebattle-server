package com.mauriciotogneri.idlebattle.ssl;

import com.mauriciotogneri.idlebattle.server.SslConfiguration;

import org.apache.http.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.DatatypeConverter;

public class SslConfig
{
    public static @NotNull SSLContext customContext() throws Exception
    {
        SslConfiguration sslConfiguration = SslConfiguration.fromFile();

        KeyStore ks = KeyStore.getInstance("JKS");
        File kf = Paths.get(sslConfiguration.keystorePath).toFile();
        ks.load(Files.newInputStream(kf.toPath()), sslConfiguration.keystorePass.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, sslConfiguration.keystorePass.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context;
    }

    public static SSLContext apacheContext() throws Exception
    {
        SslConfiguration sslConfiguration = SslConfiguration.fromFile();

        return SSLContexts
                .custom()
                .loadKeyMaterial(Paths.get(sslConfiguration.keystorePath).toFile(), sslConfiguration.keystorePass.toCharArray(), sslConfiguration.keystorePass.toCharArray())
                //.loadTrustMaterial(Paths.get("truststore_path").toFile(), "truststore_password".toCharArray())
                .build();
    }

    public static @NotNull SSLContext letsEncryptContext() throws Exception
    {
        String password = "changeit";
        String pathname = "/etc/letsencrypt/live/zeronest.com";

        SSLContext context = SSLContext.getInstance("TLS");

        byte[] certBytes = parseDERFromPEM(getBytes(new File(pathname + File.separator + "cert.pem")),
                                           "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
        byte[] keyBytes = parseDERFromPEM(
                getBytes(new File(pathname + File.separator + "privkey.pem")),
                "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

        X509Certificate cert = generateCertificateFromDER(certBytes);
        RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null);
        keystore.setCertificateEntry("cert-alias", cert);
        keystore.setKeyEntry("key-alias", key, password.toCharArray(), new Certificate[] {cert});

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keystore, password.toCharArray());

        KeyManager[] km = kmf.getKeyManagers();

        context.init(km, null, null);

        return context;
    }

    private static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter)
    {
        String data = new String(pem);
        String[] tokens = data.split(beginDelimiter);
        tokens = tokens[1].split(endDelimiter);

        return DatatypeConverter.parseBase64Binary(tokens[0]);
    }

    private static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException
    {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    private static byte[] getBytes(@NotNull File file)
    {
        byte[] bytesArray = new byte[(int) file.length()];

        try
        {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray);
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return bytesArray;
    }
}