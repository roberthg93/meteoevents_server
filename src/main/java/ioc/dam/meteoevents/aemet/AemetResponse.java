package ioc.dam.meteoevents.aemet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Classe principal que representa la resposta de l'API d'AEMET.
 * Conté informació sobre l'origen i la predicció meteorològica.
 *
 * @author mrodriguez
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AemetResponse {

    private Origen origen;
    private Prediccion prediccion;

    // Getters y Setters
    public Origen getOrigen() {
        return origen;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public Prediccion getPrediccion() {
        return prediccion;
    }

    public void setPrediccion(Prediccion prediccion) {
        this.prediccion = prediccion;
    }


    /**
     * Classe interna que representa l'origen de la informació.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Origen {
        private String productor;
        private String web;
        private String enlace;
        private String language;

        // Getters y Setters
        public String getProductor() {
            return productor;
        }

        public void setProductor(String productor) {
            this.productor = productor;
        }

        public String getWeb() {
            return web;
        }

        public void setWeb(String web) {
            this.web = web;
        }

        public String getEnlace() {
            return enlace;
        }

        public void setEnlace(String enlace) {
            this.enlace = enlace;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }


    /**
     * Classe interna que representa la predicció meteorològica.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prediccion {
        private List<Dia> dia;

        // Getters y Setters
        public List<Dia> getDia() {
            return dia;
        }

        public void setDia(List<Dia> dia) {
            this.dia = dia;
        }


        /**
         * Classe interna que representa un dia concret dins la predicció.
         */
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Dia {
            private String fecha;
            private String orto;
            private String ocaso;
            private List<EstadoCielo> estadoCielo;
            private List<Precipitacion> precipitacion;
            private List<Probabilidad> probPrecipitacion;
            private List<Viento> vientoAndRachaMax;
            private List<ProbTormenta> probTormenta;
            private List<Nieve> nieve;
            private List<probNieve> probNieve;
            private List<Temperatura> temperatura;
            private List<sensTermica> sensTermica;
            private List<humedadRelativa> humedadRelativa;

            // Getters and setters
            public List<ProbTormenta> getProbTormenta() {
                return probTormenta;
            }

            public void setProbTormenta(List<ProbTormenta> probTormenta) {
                this.probTormenta = probTormenta;
            }

            public String getFecha() {
                return fecha;
            }

            public void setFecha(String fecha) {
                this.fecha = fecha;
            }

            public String getOrto() {
                return orto;
            }

            public void setOrto(String orto) {
                this.orto = orto;
            }

            public String getOcaso() {
                return ocaso;
            }

            public void setOcaso(String ocaso) {
                this.ocaso = ocaso;
            }

            public List<EstadoCielo> getEstadoCielo() {
                return estadoCielo;
            }

            public void setEstadoCielo(List<EstadoCielo> estadoCielo) {
                this.estadoCielo = estadoCielo;
            }

            public List<Precipitacion> getPrecipitacion() {
                return precipitacion;
            }

            public void setPrecipitacion(List<Precipitacion> precipitacion) {
                this.precipitacion = precipitacion;
            }

            public List<Probabilidad> getProbPrecipitacion() {
                return probPrecipitacion;
            }

            public void setProbPrecipitacion(List<Probabilidad> probPrecipitacion) {
                this.probPrecipitacion = probPrecipitacion;
            }

            public List<Viento> getVientoAndRachaMax() {
                return vientoAndRachaMax;
            }

            public void setVientoAndRachaMax(List<Viento> vientoAndRachaMax) {
                this.vientoAndRachaMax = vientoAndRachaMax;
            }

            public List<Nieve> getNieve() {
                return nieve;
            }

            public void setNieve(List<Nieve> nieve) {
                this.nieve = nieve;
            }

            public List<Dia.probNieve> getProbNieve() {
                return probNieve;
            }

            public void setProbNieve(List<Dia.probNieve> probNieve) {
                this.probNieve = probNieve;
            }

            public List<Temperatura> getTemperatura() {
                return temperatura;
            }

            public void setTemperatura(List<Temperatura> temperatura) {
                this.temperatura = temperatura;
            }

            public List<Dia.sensTermica> getSensTermica() {
                return sensTermica;
            }

            public void setSensTermica(List<Dia.sensTermica> sensTermica) {
                this.sensTermica = sensTermica;
            }

            public List<Dia.humedadRelativa> getHumedadRelativa() {
                return humedadRelativa;
            }

            public void setHumedadRelativa(List<Dia.humedadRelativa> humedadRelativa) {
                this.humedadRelativa = humedadRelativa;
            }


            /**
             * Classe interna que representa l'estat del cel en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class EstadoCielo {
                private String value;
                private String periodo;
                private String descripcion;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }

                public String getDescripcion() {
                    return descripcion;
                }

                public void setDescripcion(String descripcion) {
                    this.descripcion = descripcion;
                }
            }


            /**
             * Classe interna que representa les precipitacions en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Precipitacion {
                private String value;
                private String periodo;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }
            }


            /**
             * Classe interna que representa la probabilitat en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Probabilidad {
                private String value;
                private String periodo;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }
            }



            /**
             * Classe interna que representa el vent en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Viento {
                private List<String> direccion;
                private List<String> velocidad;
                private String periodo;
                private String value;

                // Getters y Setters
                public List<String> getDireccion() {
                    return direccion;
                }

                public void setDireccion(List<String> direccion) {
                    this.direccion = direccion;
                }

                public List<String> getVelocidad() {
                    return velocidad;
                }

                public void setVelocidad(List<String> velocidad) {
                    this.velocidad = velocidad;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }


            /**
             * Classe interna que representa la probabilitat de tempesta en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ProbTormenta {
                private String periodo;
                private int value;

                // Getters y Setters
                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }


            /**
             * Classe interna que representa la possibilitat de neu en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Nieve {
                private String value;
                private String periodo;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }
            }


            /**
             * Classe interna que representa la probabilitat de neu en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class probNieve {
                private String value;
                private String periodo;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }
            }


            /**
             * Classe interna que representa la temperatura en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Temperatura {
                private String value;
                private String periodo;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }
            }


            /**
             * Classe interna que representa la sensació tèrmica en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class sensTermica {
                private String value;
                private String periodo;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }
            }


            /**
             * Classe interna que representa la humitat relativa en un moment donat.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class humedadRelativa {
                private String value;
                private String periodo;

                // Getters y Setters
                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getPeriodo() {
                    return periodo;
                }

                public void setPeriodo(String periodo) {
                    this.periodo = periodo;
                }
            }
        }
    }
}