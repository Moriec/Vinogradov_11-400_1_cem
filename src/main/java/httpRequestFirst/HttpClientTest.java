package httpRequestFirst;

import java.util.HashMap;
import java.util.Map;

/**
 * Тестовый класс для демонстрации работы HTTP клиента
 */
public class HttpClientTest {

    public static void main(String[] args) {
        HttpClient httpClient = new HttpClientImpl();

        testGetRequest(httpClient);

        testPostRequest(httpClient);

        testPutRequest(httpClient);

        testDeleteRequest(httpClient);
    }

    private static void testGetRequest(HttpClient httpClient) {
        System.out.println("Тест GET запроса");
        
        try {
            Map<String, String> params = new HashMap<>();
            params.put("page", "1");
            params.put("per_page", "10");

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "HttpClientTest/1.0");

            String response = httpClient.get("http://127.0.0.1:8080/hello", headers, params);
            System.out.println("GET Response: " + response.substring(0, Math.min(200, response.length())) + "...");
            
        } catch (Exception e) {
            System.err.println("Ошибка при GET запросе: " + e.getMessage());
        }
        
        System.out.println();
    }

    private static void testPostRequest(HttpClient httpClient) {
        System.out.println("Тест POST запроса");
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("title", "Test Post");
            data.put("body", "This is a test post created by HttpClient");
            data.put("userId", "1");

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "HttpClientTest/1.0");

            String response = httpClient.post("http://127.0.0.1:8080/hello", headers, data);
            System.out.println("POST Response: " + response);
            
        } catch (Exception e) {
            System.err.println("Ошибка при POST запросе: " + e.getMessage());
        }
        
        System.out.println();
    }

    private static void testPutRequest(HttpClient httpClient) {
        System.out.println("Тест PUT запроса");
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("id", "1");
            data.put("title", "Updated Post");
            data.put("body", "This post has been updated by HttpClient");
            data.put("userId", "1");

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "HttpClientTest/1.0");

            String response = httpClient.put("http://127.0.0.1:8080/hello", headers, data);
            System.out.println("PUT Response: " + response);
            
        } catch (Exception e) {
            System.err.println("Ошибка при PUT запросе: " + e.getMessage());
        }
        
        System.out.println();
    }

    private static void testDeleteRequest(HttpClient httpClient) {
        System.out.println("Тест DELETE запроса");
        
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "HttpClientTest/1.0");

            String response = httpClient.delete("http://127.0.0.1:8080/hello", headers, null);
            System.out.println("DELETE Response: " + response);
            
        } catch (Exception e) {
            System.err.println("Ошибка при DELETE запросе: " + e.getMessage());
        }
        
        System.out.println();
    }
}
