package ioc.dam.meteoevents.aemet;

/**
 * Classe on es defineixen els diferents valors dels nivells d'alerta en funció dels valors obtinguts per
 * l'API de l'AEMET
 *
 * @author mrodriguez
 */
public class AlertLevel{
    private static final int WIND_SPEED_AVERAGE_1 = 10;
    private static final int WIND_SPEED_AVERAGE_2 = 14;
    private static final int WIND_SPEED_AVERAGE_3 = 18;
    private static final int WIND_SPEED_AVERAGE_4 = 22;

    private static final int WIND_SPEED_MAX_1 = 18;
    private static final int WIND_SPEED_MAX_2 = 21;
    private static final int WIND_SPEED_MAX_3 = 27;
    private static final int WIND_SPEED_MAX_4 = 33;

    private static final float RAIN_AMOUNT_1 = 0;
    private static final float RAIN_AMOUNT_2 = 0.5F;
    private static final float RAIN_AMOUNT_3 = 1;
    private static final float RAIN_AMOUNT_4 = 5;

    private static final float SNOW_AMOUNT_1 = 0;
    private static final float SNOW_AMOUNT_2 = 0.5F;
    private static final float SNOW_AMOUNT_3 = 1;
    private static final float SNOW_AMOUNT_4 = 5;

    private static final int TEMPERATURE_HIGH_1 = 25;
    private static final int TEMPERATURE_HIGH_2 = 28;
    private static final int TEMPERATURE_HIGH_3 = 30;
    private static final int TEMPERATURE_HIGH_4 = 35;

    private static final int TEMPERATURE_LOW_1 = 5;
    private static final int TEMPERATURE_LOW_2 = 2;
    private static final int TEMPERATURE_LOW_3 = 0;
    private static final int TEMPERATURE_LOW_4 = -5;
    private static final int TEMPERATURE_LOW_5 = -10;

    private int windAverage;
    private int windMax;
    private float rainAmount;
    private float snowAmount;
    private int temperature;

    public AlertLevel(){}

    // Getters y Setters
    public int getWindAverage() {
        return windAverage;
    }

    public void setWindAverage(int windAverage) {
        this.windAverage = windAverage;
    }

    public int getWindMax() {
        return windMax;
    }

    public void setWindMax(int windMax) {
        this.windMax = windMax;
    }

    public float getRainAmount() {
        return rainAmount;
    }

    public void setRainAmount(float rainAmount) {
        this.rainAmount = rainAmount;
    }

    public float getSnowAmount() {
        return snowAmount;
    }

    public void setSnowAmount(float snowAmount) {
        this.snowAmount = snowAmount;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int checkHighTemperatureLevel(int temperature){
        if (temperature > 5 && temperature <= TEMPERATURE_HIGH_1){
            return 1;
        }else if (temperature > 5 && temperature <= TEMPERATURE_HIGH_2) {
            return 2;
        }else if (temperature > 5 && temperature <= TEMPERATURE_HIGH_3) {
            return 3;
        }else if (temperature > 5 && temperature <= TEMPERATURE_HIGH_4) {
            return 4;
        }else if (temperature > 5 && temperature > TEMPERATURE_HIGH_4) {
            return 5;
        }else{
            return -1;
        }
    }

    public int checkLowTemperatureLevel(int temperature){
        if (temperature > TEMPERATURE_LOW_1){
            return 1;
        }else if (temperature >= TEMPERATURE_LOW_2) {
            return 2;
        }else if (temperature >= TEMPERATURE_LOW_3) {
            return 3;
        }else if (temperature >= TEMPERATURE_LOW_4) {
            return 4;
        }else{
            return 5;
        }
    }

    public int checkAverageWind(int windAverage){
        if (windAverage <= WIND_SPEED_AVERAGE_1) {
            return 1;
        }else if (windAverage <= WIND_SPEED_AVERAGE_2) {
            return 2;
        }else if (windAverage <= WIND_SPEED_AVERAGE_3) {
            return 3;
        }else if (windAverage <= WIND_SPEED_AVERAGE_4) {
            return 4;
        }else {
            return 5;
        }
    }

    public int checkMaxWind(int maxWind){
        if (maxWind <= WIND_SPEED_MAX_1) {
            return 1;
        }else if (maxWind <= WIND_SPEED_MAX_2) {
            return 2;
        }else if (maxWind <= WIND_SPEED_MAX_3) {
            return 3;
        }else if (maxWind <= WIND_SPEED_MAX_4) {
            return 4;
        }else {
            return 5;
        }
    }

    public int checkRain(float rainAmount){
        if (rainAmount == RAIN_AMOUNT_1) {
            return 1;
        }else if (rainAmount < RAIN_AMOUNT_2) {
            return 2;
        }else if (rainAmount < RAIN_AMOUNT_3) {
            return 3;
        }else if (rainAmount < RAIN_AMOUNT_4) {
            return 4;
        }else{
            return 5;
        }
    }

    public int checkSnow(float snowAmountAmount){
        if (rainAmount == SNOW_AMOUNT_1) {
            return 1;
        }else if (rainAmount < SNOW_AMOUNT_2) {
            return 2;
        }else if (rainAmount < SNOW_AMOUNT_3) {
            return 3;
        }else if (rainAmount < SNOW_AMOUNT_4) {
            return 4;
        }else{
            return 5;
        }
    }
}
