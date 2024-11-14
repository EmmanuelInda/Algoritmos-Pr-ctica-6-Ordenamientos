package ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

import csv.Reader;
import csv.WeatherRecord;
import static csv.WeatherRecord.WeatherField.*;

public class WeatherTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "Formatted Date", "Summary", "Precip Type", "Temperature (C)", "Apparent Temperature (C)",
        "Humidity", "Wind Speed", "Wind Bearing", "Visibility", "Cloud Cover", "Pressure", "Daily Summary"
    };
    private ArrayList<WeatherRecord> records;

    public WeatherTableModel(ArrayList<WeatherRecord> records) {
        this.records = records;
    }

    @Override
    public int getRowCount() {
        return records.size() + 1; // +1 for the fixed chart row
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex == 0) {
            return null; // Fixed chart row
        }
        WeatherRecord record = records.get(rowIndex - 1);
        switch (columnIndex) {
            case 0: return record.getFormattedDate();
            case 1: return record.getSummary();
            case 2: return record.getPrecipType();
            case 3: return record.getTemperature();
            case 4: return record.getApparentTemperature();
            case 5: return record.getHumidity();
            case 6: return record.getWindSpeed();
            case 7: return record.getWindBearing();
            case 8: return record.getVisibility();
            case 9: return record.getLoudCover();
            case 10: return record.getPressure();
            case 11: return record.getDailySummary();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Make all cells non-editable
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    // Method to update the records and notify the table
    public void setRecords(ArrayList<WeatherRecord> newRecords) {
        this.records = newRecords;
        fireTableDataChanged();
    }

    // Getter for records
    public ArrayList<WeatherRecord> getRecords() {
        return records;
    }
}

