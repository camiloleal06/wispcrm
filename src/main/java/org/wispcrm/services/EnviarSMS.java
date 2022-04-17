package org.wispcrm.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

@Service
public class EnviarSMS {
    public void enviarSMS(String numero, String mensaje) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(60000)
                .build();
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(config);
        CloseableHttpClient httpClient = builder.build();
        HttpPost post = new HttpPost("http://www.altiria.net/api/http");
        List<NameValuePair> parametersList = new ArrayList<>();
        parametersList.add(new BasicNameValuePair("cmd", "sendsms"));
        parametersList.add(new BasicNameValuePair("login", "ingenierocamiloleal@gmail.com"));
        parametersList.add(new BasicNameValuePair("passwd", "Camilo2020+-"));
        parametersList.add(new BasicNameValuePair("dest", "57" + numero));
        parametersList.add(new BasicNameValuePair("msg", mensaje));

        try {
            post.setEntity(new UrlEncodedFormEntity(parametersList, "UTF-8"));
        } catch (UnsupportedEncodingException uex) {
            uex.printStackTrace();
        }

        try {
            CloseableHttpResponse response = httpClient.execute(post);
            }
        catch (Exception e) {
            e.printStackTrace();
              }

        finally {
            post.releaseConnection();
        }
    }
}
