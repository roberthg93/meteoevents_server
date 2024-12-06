package ioc.dam.meteoevents.aemet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AemetRequest {

    private static final String URL = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/" +
            "horaria/";
    private static final String AEMET_TOKEN = "/?api_key=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtcmdhcnJpZ2FAZ21haWwuY29tIiwianRpIjoiZDEwMT" +
            "MyNGEtNTE4ZC00OTQ4LTk2NzYtYTBiYjBhMjU5OTZjIiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE3MjU0NzE1NTksInVzZXJJZCI6" +
            "ImQxMDEzMjRhLTUxOGQtNDk0OC05Njc2LWEwYmIwYTI1OTk2YyIsInJvbGUiOiIifQ.MeI-InPKbLkkDibtj4KWpT8V-Tz3eoz" +
            "Zfn3CwiquLdQ";

    private HttpClient httpClient;
    private HttpResponse<String> response;
    private HttpRequest request;

    public AemetRequest(){
        this.httpClient = HttpClient.newHttpClient();
    }

    public String aemetForecastRequest(String codiMunicipi) throws Exception{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL + codiMunicipi + AEMET_TOKEN))
                .GET()
                .header("cache-control", "no-cache")
                .build();

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode()==200){
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            String data = jsonNode.get("datos").asText();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(data))
                    .GET()
                    .header("cache-control", "no-cache")
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode()==200){
                return response.body();
            }else{
                return "Error en la resposta de la Aemet";
            }
        }else{
            return "Error en la resposta de la Aemet";
        }
    }
}
