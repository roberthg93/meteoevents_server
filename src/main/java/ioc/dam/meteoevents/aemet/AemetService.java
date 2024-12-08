package ioc.dam.meteoevents.aemet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ioc.dam.meteoevents.entity.Esdeveniment;
import ioc.dam.meteoevents.entity.EsdevenimentUsuari;
import ioc.dam.meteoevents.entity.MesuraEsdeveniment;
import ioc.dam.meteoevents.repository.EsdevenimentUsuariRepository;
import ioc.dam.meteoevents.repository.MesuraEsdevenimentRepository;
import ioc.dam.meteoevents.repository.MunicipiRepository;
import ioc.dam.meteoevents.service.EsdevenimentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servei per gestionar la integració amb l'API de l'Aemet i processar dades meteorològiques
 * associades a un esdeveniment.
 *
 * @author mrodriguez i rhospital
 */
@Service
public class AemetService {
    //Error en la resposta
    private static final String AEMET_ERROR = "Error en la resposta de la Aemet";

    @Autowired
    private EsdevenimentsService esdevenimentsService;

    @Autowired
    private MunicipiRepository municipiRepository;

    @Autowired
    private MesuraEsdevenimentRepository mesuraEsdevenimentRepository;

    @Autowired
    private EsdevenimentUsuariRepository esdevenimentUsuariRepository;


    /**
     * Calcula la informació meteorològica per a un esdeveniment basant-se en les dades de l'API de l'Aemet.
     *
     * @param idEsdeveniment l'identificador de l'esdeveniment.
     * @return un JSON en format String amb les dades meteorològiques i mesures associades.
     */
    public String calcularMeteo(Integer idEsdeveniment) {
        Optional<Esdeveniment> esdevenimentOptional = esdevenimentsService.obtenirEsdevenimentPerId(idEsdeveniment);

        if (esdevenimentOptional.isPresent()) {
            System.out.println("Test passo aquí");
            // Recollim els valors necessaris per fer la consulta a l'AEMET
            String esdevenimentPoblacio = esdevenimentOptional.get().getPoblacio();
            String esdevenimentData = esdevenimentOptional.get().getDataEsde();
            Integer esdevenimentHoraInici = Integer.valueOf(esdevenimentOptional.get().getHoraInici().substring(0,2));
            Integer esdevenimentHoraFi = Integer.valueOf(esdevenimentOptional.get().getHoraFi().substring(0,2));
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
                        ObjectNode diaJSON;

                        // Iterar sobre els dies
                        List<AemetResponse.Prediccion.Dia> dies = aemetResponse.getPrediccion().getDia();

                        for (AemetResponse.Prediccion.Dia dia : dies) {
                            // Verifica que la data del dia coincideix amb la data de l'esdeveniment
                            if (dia.getFecha().contains(esdevenimentData)) {

                                // Com que sabem que hi ha informació per l'Esdeveniment, afgim els usuaris participants també
                                ArrayNode usuarisArrayJSON = objectMapperResposta.createArrayNode();
                                List<EsdevenimentUsuari> usuarisEsdev = esdevenimentUsuariRepository.findByIdEsdeveniment(Long.valueOf(idEsdeveniment));
                                for (EsdevenimentUsuari usuariEsdev : usuarisEsdev) {
                                    usuarisArrayJSON.add(usuariEsdev.getUsuari().getNomUsuari());
                                }

                                resultatJSON.set("Usuaris participants",usuarisArrayJSON);

                                for (int i = esdevenimentHoraInici; i <= esdevenimentHoraFi; i++ ) {
                                    // Tornem a convertir l'hora a String amb format "08"
                                    String horaBuscada = String.format("%02d", i);
                                    // Busquem el valor de periodeBuscat
                                    if (Integer.valueOf(horaBuscada) >= 22 || Integer.valueOf(horaBuscada) < 4) {
                                        periodeBuscat = "0107";
                                    } else if (Integer.valueOf(horaBuscada) >= 4 && Integer.valueOf(horaBuscada) < 10) {
                                        periodeBuscat = "0713";
                                    } else if (Integer.valueOf(horaBuscada) >= 10 && Integer.valueOf(horaBuscada) < 16) {
                                        periodeBuscat = "1319";
                                    } else if (Integer.valueOf(horaBuscada) >= 16 && Integer.valueOf(horaBuscada) < 22) {
                                        periodeBuscat = "1901";
                                    }

                                    // Inicia un objecte per aquest dia i hora
                                    String diaHora = LocalDateTime.parse(dia.getFecha()).toLocalDate().toString() + "T" + horaBuscada;
                                    diaJSON = resultatJSON.putObject(diaHora);

                                    // Processem les dades del dia
                                    processarDia(idEsdeveniment, dia, horaBuscada, periodeBuscat, diaJSON);
                                }

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

    /**
     * Processa les dades meteorològiques per un dia específic.
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     * @param dia            objecte que representa el dia meteorològic.
     * @param horaBuscada    hora específica a processar.
     * @param periodeBuscat  període de temps associat.
     * @param diaJSON        objecte JSON per afegir les dades processades.
     */
    private void processarDia(Integer idEsdeveniment, AemetResponse.Prediccion.Dia dia, String horaBuscada, String periodeBuscat, ObjectNode diaJSON) {
        // Vent
        if (dia.getVientoAndRachaMax() != null) {
            for (AemetResponse.Prediccion.Dia.Viento vent : dia.getVientoAndRachaMax()) {
                if (horaBuscada.equals(vent.getPeriodo())) {
                    if (vent.getVelocidad() != null && !vent.getVelocidad().isEmpty()) {
                        int windAverage = Integer.parseInt(vent.getVelocidad().get(0));
                        int windAverageAlert = new AlertLevel().checkAverageWind(windAverage);
                        diaJSON.put("VelocitatMitjaVent", windAverage)
                                .put("AlertaVentMitja", windAverageAlert);
                        // Busquem mesures
                        List<String> mesura = mesuraPerAlerta(idEsdeveniment, "Vent",windAverageAlert);
                        if (!mesura.isEmpty() && !mesura.contains("No mesures")) {
                            // Creem l'objecte JSON per MesuraVent
                            ObjectNode mesuraVentJSON;
                            mesuraVentJSON = diaJSON.putObject("MesuresVent");

                            // Afegim les accions com a elements del JSON
                            for (int i = 0; i < mesura.size(); i++) {
                                mesuraVentJSON.put("Accio" + (i + 1), mesura.get(i));
                            }
                        }
                    }
                    if (vent.getValue() != null) {
                        int windMax = Integer.parseInt(vent.getValue());
                        int windMaxAlert = new AlertLevel().checkMaxWind(windMax);
                        diaJSON.put("RatxaMaximaVent", windMax)
                                .put("AlertaRatxaMaxima", windMaxAlert);
                        // Busquem mesures
                        List<String> mesura = mesuraPerAlerta(idEsdeveniment, "Vent Max",windMaxAlert);
                        if (!mesura.isEmpty() && !mesura.contains("No mesures")) {
                            // Creem l'objecte JSON per MesuraVent
                            ObjectNode mesuraVentJSON;
                            mesuraVentJSON = diaJSON.putObject("MesuresVentMax");

                            // Afegim les accions com a elements del JSON
                            for (int i = 0; i < mesura.size(); i++) {
                                mesuraVentJSON.put("Accio" + (i + 1), mesura.get(i));
                            }
                        }
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
                    // Busquem mesures
                    List<String> mesura = mesuraPerAlerta(idEsdeveniment, "Precipitacio",rainAlert);
                    if (!mesura.isEmpty() && !mesura.contains("No mesures")) {
                        // Creem l'objecte JSON per MesuraVent
                        ObjectNode mesuraVentJSON;
                        mesuraVentJSON = diaJSON.putObject("MesuresPrecipitacio");

                        // Afegim les accions com a elements del JSON
                        for (int i = 0; i < mesura.size(); i++) {
                            mesuraVentJSON.put("Accio" + (i + 1), mesura.get(i));
                        }
                    }
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
                    // Busquem mesures
                    List<String> mesura = mesuraPerAlerta(idEsdeveniment, "Neu",snowAlert);
                    if (!mesura.isEmpty() && !mesura.contains("No mesures")) {
                        // Creem l'objecte JSON per MesuraVent
                        ObjectNode mesuraVentJSON;
                        mesuraVentJSON = diaJSON.putObject("MesuresNeu");

                        // Afegim les accions com a elements del JSON
                        for (int i = 0; i < mesura.size(); i++) {
                            mesuraVentJSON.put("Accio" + (i + 1), mesura.get(i));
                        }
                    }
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
                    // Busquem mesures
                    List<String> mesuraTA = mesuraPerAlerta(idEsdeveniment, "Temperatura Alta",temperatureHighAlert);
                    if (!mesuraTA.isEmpty() && !mesuraTA.contains("No mesures")) {
                        // Creem l'objecte JSON per MesuraVent
                        ObjectNode mesuraVentJSON;
                        mesuraVentJSON = diaJSON.putObject("MesuresTemperaturaAlta");

                        // Afegim les accions com a elements del JSON
                        for (int i = 0; i < mesuraTA.size(); i++) {
                            mesuraVentJSON.put("Accio" + (i + 1), mesuraTA.get(i));
                        }
                    }
                    // Busquem mesures
                    List<String> mesuraTB = mesuraPerAlerta(idEsdeveniment, "Temperatura Baixa",temperatureLowAlert);
                    if (!mesuraTB.isEmpty() && !mesuraTB.contains("No mesures")) {
                        // Creem l'objecte JSON per MesuraVent
                        ObjectNode mesuraVentJSON;
                        mesuraVentJSON = diaJSON.putObject("MesuresTemperaturaBaixa");

                        // Afegim les accions com a elements del JSON
                        for (int i = 0; i < mesuraTB.size(); i++) {
                            mesuraVentJSON.put("Accio" + (i + 1), mesuraTB.get(i));
                        }
                    }
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

    /**
     * Cerca mesures associades a un nivell d'alerta i condició meteorològica específics.
     *
     * @param idEsdeveniment identificador de l'esdeveniment.
     * @param mesura         tipus de mesura (Vent, Pluja, Neu, etc.).
     * @param alerta         nivell d'alerta per a la condició.
     * @return una llista d'accions corresponents a les mesures trobades o una llista amb "No mesures".
     */
    private List<String> mesuraPerAlerta(Integer idEsdeveniment, String mesura, Integer alerta) {
        // Creem una llista per emmagatzemar les accions trobades
        List<String> accions = new ArrayList<>();

        // Obtenim les mesures assignades a un esdeveniment
        List<MesuraEsdeveniment> mesuresEsdev = mesuraEsdevenimentRepository.findByIdEsdeveniment(idEsdeveniment);

        for (MesuraEsdeveniment mesuraEsdev : mesuresEsdev) {
            // Comprovem si existeixen mesures per aquella condició meteorològica
            if (mesuraEsdev.getMesura().getCondicio().equals(mesura)) {
                // Comprovem si existeixen mesures per aquell nivell d'alerta
                if (mesuraEsdev.getMesura().getNivellMesura() == alerta) {
                    accions.add(mesuraEsdev.getMesura().getAccio());
                }
            }
        }
        // Retornem la llista d'accions, o un missatge si està buida
        return accions.isEmpty() ? List.of("No mesures") : accions;
    }
}
