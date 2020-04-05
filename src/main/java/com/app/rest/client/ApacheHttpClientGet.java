package com.app.rest.client;

import java.io.*;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLException;

public class ApacheHttpClientGet extends ApacheHttpClient {

    public static ArrayList<String> getFilesList(String path) {
        try {
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setRetryHandler(retryHandler())
                    .build();
            HttpGet getRequest = new HttpGet("http://localhost:8080/" + path + "/files");
            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(getRequest);

            // handle server failure
            int num = 0;
            while (num < MaxRetries && response.getStatusLine().getStatusCode() != 200) {
                if (response.getStatusLine().getStatusCode() == 500) {
                    System.out.println("try request get filesList: " + num);
                    response = httpClient.execute(getRequest);
                } else throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
                num++;
            }
            if (response.getStatusLine().getStatusCode() != 200)
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (response.getEntity().getContent())));

            //read filenames to the list
            ArrayList<String> filesArray = new ArrayList<>();
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                Gson gson = new Gson();
                Type userListType = new TypeToken<ArrayList<String>>() {
                }.getType();
                filesArray = gson.fromJson(output, userListType);
            }
            httpClient.getConnectionManager().shutdown();

            return filesArray;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getFile(String filename) {
        try {
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setRetryHandler(retryHandler())
                    .build();
            String baseUrl = "http://localhost:8080/oldStorage/files/";
            String pathToFile = basePath;
            pathToFile += filename;
            Path path = Paths.get(pathToFile);
            baseUrl += filename;
            HttpGet getRequest = new HttpGet(baseUrl);
            getRequest.addHeader("accept", "application/octet-stream");
            HttpResponse response = httpClient.execute(getRequest);

            //handle server failures
            int num = 0;
            while (num < 5 && response.getStatusLine().getStatusCode() != 200) {
                if (response.getStatusLine().getStatusCode() == 500) {
                    System.out.println("try request get file: " + num);
                    response = httpClient.execute(getRequest);
                } else throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
                num++;
            }
            if (response.getStatusLine().getStatusCode() != 200)
                throw new RuntimeException("Failed : HTTP  error code : "
                        + response.getStatusLine().getStatusCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (response.getEntity().getContent())));
            String output;
            final File file = path.toFile();
            while ((output = br.readLine()) != null) {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                writer.write(output);
                writer.close();
            }
            httpClient.getConnectionManager().shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}