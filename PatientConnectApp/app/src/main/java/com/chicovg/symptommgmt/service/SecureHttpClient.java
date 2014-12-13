package com.chicovg.symptommgmt.service;

import android.content.Context;
import com.chicovg.symptommgmt.R;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created by victorguthrie on 11/15/14.
 */
public class SecureHttpClient extends DefaultHttpClient {

    final Context context;

    public SecureHttpClient(Context context) {
        this.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
        registry.register(new Scheme("https", newSslSocketFactory(), 8443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = context.getResources().openRawResource(R.raw.certstore);
            try {
                trusted.load(in, "password".toCharArray());
            } finally {
                in.close();
            }
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
