package algorithm;

import csv.WeatherRecord;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.util.stream.IntStream;

public class WeatherRecordSorter {
    private final Sort<WeatherRecord> sorter = new Sort<>();

    /**
     * Sorts weather records based on the specified attribute and sort method.
     * Supports both comparator-based and radix sorting.
     */
    public void sortWeatherRecords(ArrayList<WeatherRecord> weatherRecords, String sortBy, String sortMethod) {
        if ("radix".equalsIgnoreCase(sortMethod)) {
            sortWeatherRecordsUsingRadix(weatherRecords, sortBy);
        } else {
            sortWeatherRecordsUsingComparator(weatherRecords, sortBy, sortMethod);
        }
    }

    /**
     * Sorts the weather records using Comparator-based sorting algorithms.
     */
    private void sortWeatherRecordsUsingComparator(ArrayList<WeatherRecord> weatherRecords, String sortBy, String sortMethod) {
        Comparator<WeatherRecord> comparator = getComparatorForAttribute(sortBy);

        if (comparator == null) {
            System.out.println("Invalid sort attribute. Sorting aborted.");
            return;
        }

        WeatherRecord[] recordsArray = weatherRecords.toArray(new WeatherRecord[0]);

        switch (sortMethod.toLowerCase()) {
            case "quick":
                sorter.quick(recordsArray, 0, recordsArray.length - 1, comparator);
                break;
            case "merge":
                sorter.merge(recordsArray, 0, recordsArray.length - 1, comparator);
                break;
            case "shell":
                sorter.shell(recordsArray, comparator);
                break;
            case "selection":
                sorter.selection(recordsArray, comparator);
                break;
            default:
                Arrays.sort(recordsArray, comparator);
        }

        weatherRecords.clear();
        weatherRecords.addAll(Arrays.asList(recordsArray));
    }

    /**
     * Sorts the weather records based on an integer attribute using Radix Sort.
     */
    private void sortWeatherRecordsUsingRadix(ArrayList<WeatherRecord> weatherRecords, String sortBy) {
        int[] attributeArray = extractAttributeArray(weatherRecords, sortBy);

        new Sort().radix(attributeArray);
        rebuildWeatherRecords(weatherRecords, attributeArray, sortBy);
    }

    /**
     * Extracts an array of integer values from the specified attribute of WeatherRecords for Radix Sort.
     */
    private int[] extractAttributeArray(ArrayList<WeatherRecord> weatherRecords, String sortBy) {
        return weatherRecords.stream()
            .mapToInt(record -> {
                switch (sortBy.toLowerCase()) {
                    case "temperature": return (int) record.getTemperature();
                    case "pressure": return (int) record.getPressure();
                    case "humidity": return (int) (record.getHumidity() * 100);
                    case "windspeed": return (int) record.getWindSpeed();
                    case "windbearing": return (int) record.getWindBearing();
                    case "visibility": return (int) record.getVisibility();
                    default: throw new IllegalArgumentException("Invalid attribute for radix sort");
                }
            }).toArray();
    }

    /**
     * Rebuilds the weatherRecords list to match the sorted order of the attribute array.
     */
    private void rebuildWeatherRecords(ArrayList<WeatherRecord> weatherRecords, int[] sortedAttributes, String sortBy) {
        ArrayList<WeatherRecord> sortedRecords = new ArrayList<>(weatherRecords.size());

        IntStream.range(0, sortedAttributes.length)
            .mapToObj(i -> findAndRemoveMatchingRecord(weatherRecords, sortedAttributes[i], sortBy))
            .forEach(sortedRecords::add);

        weatherRecords.clear();
        weatherRecords.addAll(sortedRecords);
    }

    /**
     * Finds and removes the first WeatherRecord matching a given attribute value.
     */
    private WeatherRecord findAndRemoveMatchingRecord(ArrayList<WeatherRecord> records, int attributeValue, String sortBy) {
        for (int i = 0; i < records.size(); i++) {
            WeatherRecord record = records.get(i);
            int valueToCompare = switch (sortBy.toLowerCase()) {
                case "temperature" -> (int) record.getTemperature();
                case "pressure" -> (int) record.getPressure();
                case "humidity" -> (int) (record.getHumidity() * 100);
                case "windspeed" -> (int) record.getWindSpeed();
                case "windbearing" -> (int) record.getWindBearing();
                case "visibility" -> (int) record.getVisibility();
                default -> throw new IllegalArgumentException("Invalid attribute for radix sort");
            };

            if (valueToCompare == attributeValue) {
                return records.remove(i);
            }
        }
        return null;
    }

    /**
     * Returns a Comparator for sorting WeatherRecords based on the specified attribute.
     * Includes comparators for both numeric and categorical fields.
     */
	public Comparator<WeatherRecord> getComparatorForAttribute(String sortBy) {
		return switch (sortBy.toLowerCase()) {
			case "temperature" -> Comparator.comparingDouble(WeatherRecord::getTemperature);
			case "apparenttemperature" -> Comparator.comparingDouble(WeatherRecord::getApparentTemperature);
			case "humidity" -> Comparator.comparingDouble(WeatherRecord::getHumidity);
			case "windspeed" -> Comparator.comparingDouble(WeatherRecord::getWindSpeed);
			case "windbearing" -> Comparator.comparingDouble(WeatherRecord::getWindBearing);
			case "visibility" -> Comparator.comparingDouble(WeatherRecord::getVisibility);
			case "loudcover" -> Comparator.comparingDouble(WeatherRecord::getLoudCover);
			case "pressure" -> Comparator.comparingDouble(WeatherRecord::getPressure);
	
			// Date field, sorts by date in chronological order
			case "formatted date" -> Comparator.comparing(WeatherRecord::getFormattedDate);
	
			// Sorts by alphabetic order, handling nulls if necessary
			case "summary" -> Comparator.comparing(WeatherRecord::getSummary, Comparator.nullsLast(String::compareTo));
			case "preciptype" -> Comparator.comparing(WeatherRecord::getPrecipType, Comparator.nullsLast(String::compareTo));
			case "dailysummary" -> Comparator.comparing(WeatherRecord::getDailySummary, Comparator.nullsLast(String::compareTo));
	
			default -> null;
		};
	}

    /**
     * Additional method to provide a direct way to sort using arrays, for `MainWindow`.
     */
    public void sortRecords(WeatherRecord[] records, Comparator<WeatherRecord> comparator, String algorithm) {
        switch (algorithm.toLowerCase()) {
            case "quick":
                sorter.quick(records, 0, records.length - 1, comparator);
                break;
            case "merge":
                sorter.merge(records, 0, records.length - 1, comparator);
                break;
            case "shell":
                sorter.shell(records, comparator);
                break;
            case "selection":
                sorter.selection(records, comparator);
                break;
            default:
                Arrays.sort(records, comparator);
        }
    }
}
