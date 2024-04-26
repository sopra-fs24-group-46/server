package ch.uzh.ifi.hase.soprafs24.endpoint.controller.helpers;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestExecutor {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient httpClient;
    public final String serverURL;
    final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public RequestExecutor(String serverURL) {
        this.httpClient = new OkHttpClient();
        this.serverURL = serverURL;
    }

    public String getResponseBody(Response response) throws IOException {
        StringBuilder responseString = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseString.append(line).append("\n");
            }
            String json = responseString.toString();
            json = json.replace("\n", "");
            return json;
        }
    }

    public void failAndPrint(String prefix, String json) {
        try {
            String message;
            try {
                JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
                if (jsonNode.has("message")) {
                    message = jsonNode.get("message").asText();
                } else {
                    message = json;
                }
            } catch (Exception e) {
                message = json;
            }
            message = prefix + ": " + message;
            System.out.println(message);
            fail(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage());
        }
    }

    public String executeRequest(Request request, int expectedResponseCode, String failMsg) {
        String json = "empty";
        try (Response response = httpClient.newCall(request).execute()) {
            json = getResponseBody(response);
            if (response.code() != expectedResponseCode) {
                failAndPrint(failMsg + ", HttpCode: " + response.code() + " Response: ", json);
            }
        } catch (Exception e) {
            fail(e);
        }
        return json;
    }

    public RequestBody body(String json) {
        return RequestBody.create(json, JSON);
    }

    public void checkIfHostIsRunning() {
        Request request = new Request.Builder()
                .url(serverURL)
                .get()
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (response.code() != 200) {
                fail("Server is not running");
            }

        } catch (Exception e) {

            fail("Server is not running"); // run local host to use this tests
        }
    }
}