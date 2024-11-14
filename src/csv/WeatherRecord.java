package csv;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class WeatherRecord {
	private String formattedDate;
	private String summary;
	private String precipType;
	private double temperature;
	private double apparentTemperature;
	private double humidity;
	private double windSpeed;
	private double windBearing;
	private double visibility;
	private double loudCover;
	private double pressure;
	private String dailySummary;

	public static enum WeatherField {
		FORMATTED_DATE,
		SUMMARY,
		PRECIP_TYPE,
		TEMPERATURE,
		APPARENT_TEMPERATURE,
		HUMIDITY,
		WIND_SPEED,
		WIND_BEARING,
		VISIBILITY,
		CLOUD_COVER,
		PRESSURE,
		DAILY_SUMMARY
	}

	public WeatherRecord() { }

	public WeatherRecord(String formattedDate, String summary, String precipType, double temperature,
						double apparentTemperature, double humidity, double windSpeed, double windBearing,
						double visibility, double loudCover, double pressure, String dailySummary) {
		this.formattedDate			= formattedDate;
		this.summary				= summary;
		this.precipType				= precipType;
		this.temperature			= temperature;
		this.apparentTemperature	= apparentTemperature;
		this.humidity				= humidity;
		this.windSpeed				= windSpeed;
		this.windBearing			= windBearing;
		this.visibility				= visibility;
		this.loudCover				= loudCover;
		this.pressure				= pressure;
		this.dailySummary			= dailySummary;
	}

	public String getFormattedDate() { return formattedDate; }
	public String getSummary() { return summary; }
	public String getPrecipType() { return precipType; }
	public double getTemperature() { return temperature; }
	public double getApparentTemperature() { return apparentTemperature; };
	public double getHumidity() { return humidity; }
	public double getWindSpeed() { return windSpeed; }
	public double getWindBearing() { return windBearing; }
	public double getVisibility() {	return visibility; }
	public double getLoudCover() { return loudCover; }
	public double getPressure() { return pressure; }
	public String getDailySummary() { return dailySummary; }

    /**
     * Extracts the year from the formatted date string.
     * 
     * @return The year as an integer, or -1 if the date parsing fails.
     */
    public int getYear() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(this.formattedDate, formatter);
            return dateTime.getYear();
        } catch (DateTimeParseException e) {
            // Handle parsing exceptions if necessary
            return -1; // Return -1 if the date format is invalid or cannot be parsed
        }
    }

	@Override
	public String toString() {
		return formattedDate + ", " +
				summary + ", " +
				precipType + ", " +
				temperature + ", " +
				apparentTemperature + ", " +
				humidity + ", " +
				windSpeed + ", " +
				windBearing + ", " +
				visibility + ", " +
				loudCover + ", " +
				pressure + ", " +
				dailySummary;
	}
}

