package ioc.dam.meteoevents.aemet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Classe per gestionar les sol·licituds a l'API de AEMET per obtenir informació meteorològica.
 * Utilitza el client HTTP de Java per realitzar peticions a l'API de AEMET i processar-ne la resposta.
 *
 * @author mrodriguez
 */
public class AemetRequest {

    /**
     * URL base per accedir a l'API d'AEMET per obtenir la predicció específica d'un municipi.
     */
    private static final String URL = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/" +
            "horaria/";

    /**
     * Token d'accés a l'API d'AEMET. S'utilitza per autenticar-se.
     */
    private static final String AEMET_TOKEN = "/?api_key=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtcmdhcnJpZ2FAZ21haWwuY29tIiwianRpIjoiZDEwMT" +
            "MyNGEtNTE4ZC00OTQ4LTk2NzYtYTBiYjBhMjU5OTZjIiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE3MjU0NzE1NTksInVzZXJJZCI6" +
            "ImQxMDEzMjRhLTUxOGQtNDk0OC05Njc2LWEwYmIwYTI1OTk2YyIsInJvbGUiOiIifQ.MeI-InPKbLkkDibtj4KWpT8V-Tz3eoz" +
            "Zfn3CwiquLdQ";

    private HttpClient httpClient;
    private HttpResponse<String> response;
    private HttpRequest request;

    /**
     * Constructor per inicialitzar el client HTTP de la classe.
     * Crea una instància de {@link HttpClient} per realitzar sol·licituds HTTP.
     */
    public AemetRequest() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Realitza una sol·licitud per obtenir la previsió meteorològica d'un municipi específic a partir del seu codi.
     *
     * @param codiMunicipi El codi del municipi pel qual es vol obtenir la previsió meteorològica.
     * @return Una cadena amb la informació meteorològica obtinguda de l'API d'AEMET.
     * @throws Exception Si ocorre un error durant la sol·licitud HTTP o la resposta no és vàlida.
     */
    public String aemetForecastRequest(String codiMunicipi) throws Exception {
        // Construcció de la primera sol·licitud per accedir a la URL de l'API d'AEMET.
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL + codiMunicipi + AEMET_TOKEN))
                .GET()
                .header("cache-control", "no-cache")
                .build();

        // Enviament de la primera sol·licitud.
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Comprovació de l'estat de la resposta.
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            String data = jsonNode.get("datos").asText();

            // Realització d'una segona sol·licitud amb la URL obtinguda.
            request = HttpRequest.newBuilder()
                    .uri(URI.create(data))
                    .GET()
                    .header("cache-control", "no-cache")
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Comprovació de l'estat de la segona resposta.
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "Error en la resposta de la Aemet";
            }
        } else {
            return "Error en la resposta de la Aemet";
        }
    }
}
