package httpRequestFirst;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientImpl implements HttpClient {

    private final ObjectMapper objectMapper;

    public HttpClientImpl() {
        this.objectMapper = new ObjectMapper();
    }

    private static String readResponse(HttpURLConnection connection) {
        if (connection == null) {
            return null;
        }
        
        try {
            int responseCode = connection.getResponseCode();

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            
            try (reader) {
                StringBuilder content = new StringBuilder();
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input).append("\n");
                }
                return content.toString().trim();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении ответа: " + e.getMessage(), e);
        }
    }

    private String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        // Если уже есть параметры, то не добавляем вопросительный знак
        urlBuilder.append(baseUrl.contains("?") ? "&" : "?");

        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                urlBuilder.append("&");
            }
            try {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                         .append("=")
                         .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при кодировании параметров URL", e);
            }
            first = false;
        }

        return urlBuilder.toString();
    }

    private HttpURLConnection setupConnection(String url, String method, Map<String, String> headers) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            return connection;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании HTTP соединения: " + e.getMessage(), e);
        }
    }

    private void sendRequestBody(HttpURLConnection connection, Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        try {
            connection.setDoOutput(true);
            String jsonData = objectMapper.writeValueAsString(data);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при сериализации данных в JSON: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при отправке данных: " + e.getMessage(), e);
        }
    }

    @Override
    public String get(String url, Map<String, String> headers, Map<String, String> params) {
        String fullUrl = buildUrlWithParams(url, params);
        HttpURLConnection connection = setupConnection(fullUrl, "GET", headers);
        
        try {
            return readResponse(connection);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении GET запроса: " + e.getMessage(), e);
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public String post(String url, Map<String, String> headers, Map<String, String> data) {
        HttpURLConnection connection = setupConnection(url, "POST", headers);
        sendRequestBody(connection, data);
        
        try {
            return readResponse(connection);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении POST запроса: " + e.getMessage(), e);
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public String put(String url, Map<String, String> headers, Map<String, String> data) {
        HttpURLConnection connection = setupConnection(url, "PUT", headers);
        sendRequestBody(connection, data);
        
        try {
            return readResponse(connection);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении PUT запроса: " + e.getMessage(), e);
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public String delete(String url, Map<String, String> headers, Map<String, String> data) {
        HttpURLConnection connection = setupConnection(url, "DELETE", headers);
        sendRequestBody(connection, data);
        
        try {
            return readResponse(connection);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении DELETE запроса: " + e.getMessage(), e);
        } finally {
            connection.disconnect();
        }
    }
}
