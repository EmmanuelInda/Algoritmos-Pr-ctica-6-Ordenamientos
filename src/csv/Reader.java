package csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {
	private String filePath;
	private BufferedReader br;

	public Reader() {}

	public Reader(String filePath) {
		this.filePath = filePath;
	}

	public ArrayList<WeatherRecord> readWeatherRecord() {
		return readWeatherRecord(new File(filePath));
	}

	public ArrayList<WeatherRecord> readWeatherRecord(File file) {
		ArrayList<WeatherRecord> weatherRecords = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			this.br = br;
			br.readLine();

			String line;

			while ((line = br.readLine()) != null) {
				WeatherRecord record = parseLine(line);

				if (record != null)
					weatherRecords.add(record);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return weatherRecords;
	}

	private WeatherRecord parseLine(String line) {
		try {
			String[] datos = line.split(",");
			return new WeatherRecord(
				datos[0],
				datos[1],
				datos[2],
				Double.parseDouble(datos[3]),
				Double.parseDouble(datos[4]),
				Double.parseDouble(datos[5]),
				Double.parseDouble(datos[6]),
				Double.parseDouble(datos[7]),
				Double.parseDouble(datos[8]),
				Double.parseDouble(datos[9]),
				Double.parseDouble(datos[10]),
				datos[11]
			);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			System.out.println("Error parsing line: " + line);
			e.printStackTrace();
			return null;
		}
	}

	public void displayRecords(ArrayList<WeatherRecord> weatherRecords) {
		System.out.println("Date, Summary, PrecipType, Temperature, ApparentTemperature, Humidity, WindSpeed, " +
						   "WindBearing, Visibility, LoudCover, Pressure, DailySummary");

		for (WeatherRecord record : weatherRecords)
			System.out.println(record);
	}

	public void close() {
		if (br != null) {
			try {
				br.close();
				br = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
