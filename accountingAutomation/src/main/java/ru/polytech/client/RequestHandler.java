package ru.polytech.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RequestHandler {

    private String baseUrl;
    private String urlResource;
    private String httpMethod;
    private String token;

    public RequestHandler(String baseUrl) {
        setBaseUrl(baseUrl);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUrlResource(String urlResource) {
        this.urlResource = urlResource;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!baseUrl.substring(baseUrl.length() - 1).equals("/")) {
            this.baseUrl += "/";
        }
    }

    public void execute(final CallBack callBack, final String object) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlS = baseUrl + urlResource;
                    URL url = new URL(urlS);

                    httpMethod = httpMethod.toUpperCase();

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(httpMethod);
                    connection.setRequestProperty("User-Agent", "clientTerm");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    if (!urlResource.equals("auth/signIn") && !urlResource.equals("auth/signUp") &&
                            !urlResource.equals("*/all")) {
                        connection.setRequestProperty("Authorization", "Bearer " + token);
                    }
                    if (object != null && (httpMethod.equals("PUT") || httpMethod.equals("POST"))) {
                        connection.setDoOutput(true);
                        OutputStream os = connection.getOutputStream();
                        os.write(object.getBytes("UTF-8"));
                        os.close();
                    }

                    int code = connection.getResponseCode();
                    if (code != 200 && code != 201) {
                        System.err.println("connection failed");
                        System.err.println("code: " + code);
                        callBack.onFail("error", code);
                    } else {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), "utf-8"));

                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        callBack.onSuccess(response.toString());
                    }
                    connection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                    callBack.onFailure("error " + e.getMessage());
                }
            }
        });
        thread.start();
    }

}
