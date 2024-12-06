package ioc.dam.meteoevents.aemet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.repository.MunicipiRepository;
import ioc.dam.meteoevents.service.EsdevenimentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AemetService {
    //Error en la resposta
    private static final String AEMET_ERROR = "Error en la resposta de la Aemet";

    @Autowired
    private EsdevenimentsService esdevenimentsService;

    @Autowired
    private MunicipiRepository municipiRepository;

    public String calcularMeteo(Integer idEsdeveniment) {
        Optional<Esdeveniment> esdevenimentOptional = esdevenimentsService.obtenirEsdevenimentPerId(idEsdeveniment);

        if (esdevenimentOptional.isPresent()) {
            // Recollim els valors necessaris per fer la consulta a l'AEMET
            String esdevenimentPoblacio = esdevenimentOptional.get().getPoblacio();
            String esdevenimentData = esdevenimentOptional.get().getDataEsde();
            String esdevenimentHoraInici = esdevenimentOptional.get().getHoraInici();
            String esdevenimentHoraFi = esdevenimentOptional.get().getHoraFi();
            String horaBuscada = "22";
            String periodeBuscat = "0107";

            String municipiCodi = municipiRepository.findCodiByMunicipi(esdevenimentPoblacio);

            if (municipiCodi != null) {
                try {
                    // Genera la petició a l'Aemet i rep la resposta.
                    AemetRequest request = new AemetRequest();
                    String response = request.aemetForecastRequest(municipiCodi);

                    if (!AEMET_ERROR.equals(response)) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<AemetResponse> aemetResponses = objectMapper.readValue(response, new TypeReference<List<AemetResponse>>() {});

                        AemetResponse aemetResponse = aemetResponses.get(0);
                        ObjectMapper objectMapperResposta = new ObjectMapper();
                        ObjectNode resultatJSON = objectMapperResposta.createObjectNode();

                        // Iterar sobre els dies
                        List<AemetResponse.Prediccion.Dia> dies = aemetResponse.getPrediccion().getDia();

                        for (AemetResponse.Prediccion.Dia dia : dies) {
                            System.out.println(dia.getFecha());
                            // Verifica que la data del dia coincideix amb la data de l'esdeveniment
                            if (dia.getFecha().contains(esdevenimentData)) {
                                // Inicia un objecte per aquest dia
                                ObjectNode diaJSON = resultatJSON.putObject(dia.getFecha());

                                // Processem les dades del dia
                                processarDia(dia, horaBuscada, periodeBuscat, diaJSON);

                                // Retorna la resposta amb la informació de tots els dies processats
                                return objectMapperResposta.writerWithDefaultPrettyPrinter().writeValueAsString(resultatJSON);
                            }
                        }
                    } else {
                        return response;
                    }

                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            } else {
                return "Municipi no trobat";
            }

        } else {
            return "Esdeveniment no trobat";
        }
        return "No s'ha pogut generar el JSON";
    }

    private void processarDia(AemetResponse.Prediccion.Dia dia, String horaBuscada, String periodeBuscat, ObjectNode diaJSON) {
        // Vent
        if (dia.getVientoAndRachaMax() != null) {
            for (AemetResponse.Prediccion.Dia.Viento vent : dia.getVientoAndRachaMax()) {
                if (horaBuscada.equals(vent.getPeriodo())) {
                    if (vent.getVelocidad() != null && !vent.getVelocidad().isEmpty()) {
                        int windAverage = Integer.parseInt(vent.getVelocidad().get(0));
                        int windAverageAlert = new AlertLevel().checkAverageWind(windAverage);
                        diaJSON.put("VelocitatMitjaVent", windAverage)
                                .put("AlertaVentMitja", windAverageAlert);
                    }
                    if (vent.getValue() != null) {
                        int windMax = Integer.parseInt(vent.getValue());
                        int windMaxAlert = new AlertLevel().checkMaxWind(windMax);
                        diaJSON.put("RatxaMaximaVent", windMax)
                                .put("AlertaRatxaMaxima", windMaxAlert);
                    }
                }
            }
        }

        // Pluja
        if (dia.getProbPrecipitacion() != null) {
            for (AemetResponse.Prediccion.Dia.Probabilidad probabilitat : dia.getProbPrecipitacion()) {
                if (periodeBuscat.equals(probabilitat.getPeriodo())) {
                    int rainProbability = Integer.parseInt(probabilitat.getValue());
                    diaJSON.put("ProbabilitatPluja", rainProbability);
                }
            }
        }

        if (dia.getPrecipitacion() != null) {
            for (AemetResponse.Prediccion.Dia.Precipitacion precipitacio : dia.getPrecipitacion()) {
                if (horaBuscada.equals(precipitacio.getPeriodo())) {
                    float rainAmount = Float.parseFloat(precipitacio.getValue());
                    int rainAlert = new AlertLevel().checkRain(rainAmount);
                    diaJSON.put("Precipitacio", rainAmount)
                            .put("AlertaPluja", rainAlert);
                }
            }
        }

        // Tempesta
        if (dia.getProbTormenta() != null) {
            for (AemetResponse.Prediccion.Dia.ProbTormenta tempesta : dia.getProbTormenta()) {
                if (periodeBuscat.equals(tempesta.getPeriodo())) {
                    int stormProbability = tempesta.getValue();
                    diaJSON.put("ProbabilitatTempesta", stormProbability);
                }
            }
        }

        // Neu
        if (dia.getNieve() != null) {
            for (AemetResponse.Prediccion.Dia.Nieve neu : dia.getNieve()) {
                if (horaBuscada.equals(neu.getPeriodo())) {
                    float snowAmount = Float.parseFloat(neu.getValue());
                    int snowAlert = new AlertLevel().checkSnow(snowAmount);
                    diaJSON.put("Neu", snowAmount)
                            .put("AlertaNeu", snowAlert);
                }
            }
        }

        if (dia.getProbNieve() != null) {
            for (AemetResponse.Prediccion.Dia.probNieve probNeu : dia.getProbNieve()) {
                if (periodeBuscat.equals(probNeu.getPeriodo())) {
                    int snowProbability = Integer.parseInt(probNeu.getValue());
                    diaJSON.put("ProbabilitatNevada", snowProbability);
                }
            }
        }

        // Temperatura
        if (dia.getTemperatura() != null) {
            for (AemetResponse.Prediccion.Dia.Temperatura temperatura : dia.getTemperatura()) {
                if (horaBuscada.equals(temperatura.getPeriodo())) {
                    int temperature = Integer.parseInt(temperatura.getValue());
                    int temperatureHighAlert = new AlertLevel().checkHighTemperatureLevel(temperature);
                    int temperatureLowAlert = new AlertLevel().checkLowTemperatureLevel(temperature);
                    diaJSON.put("Temperatura", temperature)
                            .put("AlertaAltaTemperatura", temperatureHighAlert)
                            .put("AlertaBaixaTemperatura", temperatureLowAlert);
                }
            }
        }

        // Humitat
        if (dia.getHumedadRelativa() != null) {
            for (AemetResponse.Prediccion.Dia.humedadRelativa humitatRelativa : dia.getHumedadRelativa()) {
                if (horaBuscada.equals(humitatRelativa.getPeriodo())) {
                    int relativeHumidity = Integer.parseInt(humitatRelativa.getValue());
                    diaJSON.put("HumitatRelativa", relativeHumidity);
                }
            }
        }
    }
}
