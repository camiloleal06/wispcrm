package org.wispcrm.services;


import java.io.IOException;
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
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class EnviarSMS {
public void enviarSMS(String numero,String mensaje){
RequestConfig config = RequestConfig.custom()
 .setConnectTimeout(5000)
 .setSocketTimeout(60000)
 .build();
HttpClientBuilder builder = HttpClientBuilder.create();
builder.setDefaultRequestConfig(config);
CloseableHttpClient httpClient = builder.build();
HttpPost post = new HttpPost("http://www.altiria.net/api/http");
List<NameValuePair> parametersList = new ArrayList <NameValuePair>();
parametersList.add(new BasicNameValuePair("cmd", "sendsms"));
parametersList.add(new BasicNameValuePair("login", "ingenierocamiloleal@gmail.com"));
parametersList.add(new BasicNameValuePair("passwd", "Camilo2020+-"));
parametersList.add(new BasicNameValuePair("dest", "57"+numero));
parametersList.add(new BasicNameValuePair("msg", mensaje));

try {
 post.setEntity(new UrlEncodedFormEntity(parametersList,"UTF-8"));
}
catch(UnsupportedEncodingException uex) {
 System.out.println("ERROR: codificacion de caracteres no soportada");
}
	
CloseableHttpResponse response = null;
	
try {
 System.out.println("Enviando petici�n");
 response = httpClient.execute(post);
 String resp = EntityUtils.toString(response.getEntity());
 if (response.getStatusLine().getStatusCode()!=200){
   System.out.println("ERROR: C�digo de error HTTP:  " + response.getStatusLine().getStatusCode());
   System.out.println("Compruebe que ha configurado correctamente la direccion/url ");
   System.out.println("suministrada por Altiria");
   return;
 }else {
   if (resp.startsWith("ERROR")){
    System.out.println(resp);
    System.out.println("C�digo de error de Altiria. Compruebe las especificaciones");
   } else
    System.out.println(resp);
   }
 }
 catch (Exception e) {
   System.out.println("Excepci�n");
   e.printStackTrace();
   return;
 }
 finally {
    post.releaseConnection();
    if(response!=null) {
     try {
       response.close();
     }
     catch(IOException ioe) {
       System.out.println("ERROR cerrando conexion");
     }
    }
 }
}
}
