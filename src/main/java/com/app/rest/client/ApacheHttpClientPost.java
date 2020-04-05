package com.app.rest.client;
import java.io.File;
import java.io.FileInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;


public class ApacheHttpClientPost extends ApacheHttpClient{

	public static void postFile(String filename) {
		try {
			CloseableHttpClient httpClient = HttpClients.custom()
					.setRetryHandler(retryHandler())
					.build();
			HttpPost uploadFile = new HttpPost("http://localhost:8080/newStorage/files");

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("file", filename, ContentType.TEXT_PLAIN);

			String filePath = basePath;
			filePath += filename;
			File f = new File(filePath);
			builder.addBinaryBody(
					"file",
					new FileInputStream(f),
					ContentType.APPLICATION_OCTET_STREAM,
					f.getName()
			);

			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			CloseableHttpResponse response = httpClient.execute(uploadFile);

			// handle server failures
			int num = 0;
			while (num < MaxRetries && response.getStatusLine().getStatusCode() != 200){
				if (response.getStatusLine().getStatusCode() == 500){
					System.out.println("try request post file: " + num);
					response = httpClient.execute(uploadFile);
				}
				else throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
				num++;
			}
			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

