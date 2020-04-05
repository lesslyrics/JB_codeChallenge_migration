package com.app.rest.client;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class ApacheHttpClientDelete extends ApacheHttpClient {
    public static void deleteFile(String filename) {
        try {
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setRetryHandler(retryHandler())
                    .build();
            String url = "http://localhost:8080/oldStorage/files/";
            url += filename;
            HttpDelete deleteRequest = new HttpDelete(url);

            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpClient.execute(deleteRequest, responseHandler);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}