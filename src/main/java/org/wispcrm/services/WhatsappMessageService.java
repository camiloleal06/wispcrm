package org.wispcrm.services;
import java.io.IOException;

import okhttp3.*;
import org.springframework.stereotype.Service;
import org.wispcrm.utils.ConstantMensaje;

@Service
public class WhatsappMessageService {
    public WhatsappMessageService() {
    }

      public void sendSimpleMessage(String clientNumber, String msg) throws IOException {
        //Enviando WhatsApp Message
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
     ///   RequestBody body = RequestBody.create(mediaType, token+"&to="+clientNumber+"&body="+msg+"&priority=10&referenceId=");

        RequestBody body = new FormBody.Builder()
                .add("token", ConstantMensaje.TOKE_API_WHATSAPP)
                .add("to", "+57"+clientNumber)
                .add("body", msg)
                .build();

        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/instance40701/messages/chat")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        //////////////////////////////////////
        System.out.println("===================");
        System.out.println(response.body().string());
    }

    public void sendDocumentAndMessage(String clientNumber, String msg, String fileName, String pathDocument) throws IOException {
        //Enviando WhatsApp Message
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
  //      RequestBody body = RequestBody.create(mediaType, token+"&to="+clientNumber+"&filename="+fileName+"&document="+pathDocument+"&priority=10&referenceId=");
        RequestBody body = new FormBody.Builder()
                .add("token", "hzgoj7lz9wyb3ri6")
                .add("to", "+57"+clientNumber)
                .add("filename", fileName)
                .add("document", pathDocument)
                .add("caption", msg)
                .build();
        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/instance40701/messages/document")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        Response response = client.newCall(request).execute();
        //////////////////////////////////////
        System.out.println("===================");
        System.out.println(response.body().string());
    }
}