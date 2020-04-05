package com.app.rest.client;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;

import javax.net.ssl.SSLException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

public class ApacheHttpClient {

    protected static int MaxRetries = 3;
    /** base path to the directory to store temp file **/
    protected static String basePath = "{your path here}/src/main/java/com/mkyong/rest/client/data/";

    /** repeat requests if the server is not responding **/
    static HttpRequestRetryHandler retryHandler(){
        return (exception, executionCount, context) -> {

            if (executionCount > MaxRetries) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            return !(request instanceof HttpEntityEnclosingRequest);
        };
    }
}
