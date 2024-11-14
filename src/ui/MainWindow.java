package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import csv.Reader;
import csv.WeatherRecord;
import static csv.WeatherRecord.WeatherField.*;

import algorithm.WeatherRecordSorter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
	private int width = 1376;
	private int height = 768;
	private JTable table;
	private ArrayList<WeatherRecord> weatherRecords;
	private ArrayList<WeatherRecord> originalWeatherRecords; // Store original order
	private WeatherRecordSorter recordSorter;
	private WeatherTableModel tableModel;
	private JButton sortButton;
	private JButton randomButton; // Added randomButton
	private JButton resetButton;  // Added resetButton
	private JLabel timeLabel; // Added timeLabel

	public MainWindow() {
		setTitle("Weather Dataset");
		setPreferredSize(new Dimension(width, height));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Reader reader = new Reader("res/csv/weatherHistory.csv");
		weatherRecords = reader.readWeatherRecord();
		originalWeatherRecords = new ArrayList<>(weatherRecords); // Initialize originalWeatherRecords

		recordSorter = new WeatherRecordSorter();

		initializeTable();

		// Create a split pane to hold the table and the chart
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(createMainTablePanel());
		splitPane.setResizeWeight(0.7); // Allocate 70% to the table and 30% to the chart

		add(splitPane);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Creates the main panel containing the table and sorting controls.
	 *
	 * @return A JPanel containing the table and sorting controls.
	 */
	private JPanel createMainTablePanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel titleLabel = new JLabel("Weather Dataset", JLabel.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createTitledBorder("weatherHistory.csv (15.5 MB)"));
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Add sorting and control buttons
		JPanel sortPanel = new JPanel();
		sortPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel sortByLabel = new JLabel("Sort By:");
		JComboBox<String> sortByCombo = new JComboBox<>(new String[]{
			"Temperature", "Apparent Temperature", "Humidity", "Wind Speed",
			"Wind Bearing", "Visibility", "Cloud Cover", "Pressure", 
			"Formatted Date", "Summary", "Precip Type", "Daily Summary"
		});

		JLabel algorithmLabel = new JLabel("Algorithm:");
		JComboBox<String> algorithmCombo = new JComboBox<>(new String[]{
			"Quick", "Merge", "Shell", "Selection", "Sort", "Parallel Sort"
		});

		// New order selection dropdown
		JLabel orderLabel = new JLabel("Order:");
		JComboBox<String> orderCombo = new JComboBox<>(new String[]{
			"Ascending", "Descending"
		});

		sortButton = new JButton("Sort");
		randomButton = new JButton("Random"); // Initialize randomButton
		resetButton = new JButton("Reset");   // Initialize resetButton
		timeLabel = new JLabel("Time: "); // Initialize timeLabel

		sortPanel.add(sortByLabel);
		sortPanel.add(sortByCombo);
		sortPanel.add(Box.createHorizontalStrut(15)); // Spacer
		sortPanel.add(algorithmLabel);
		sortPanel.add(algorithmCombo);
		sortPanel.add(Box.createHorizontalStrut(15)); // Spacer
		sortPanel.add(orderLabel);  // Add order label
		sortPanel.add(orderCombo);  // Add order dropdown
		sortPanel.add(Box.createHorizontalStrut(15)); // Spacer
		sortPanel.add(sortButton);
		sortPanel.add(Box.createHorizontalStrut(15)); // Spacer
		sortPanel.add(randomButton); // Add randomButton
		sortPanel.add(Box.createHorizontalStrut(15)); // Spacer
		sortPanel.add(resetButton);  // Add resetButton
		sortPanel.add(Box.createHorizontalStrut(15)); // Spacer
		sortPanel.add(timeLabel); // Add timeLabel next to buttons

		mainPanel.add(sortPanel, BorderLayout.SOUTH);

		// Action listener for sort button
		sortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String sortBy = (String) sortByCombo.getSelectedItem();
				String algorithm = (String) algorithmCombo.getSelectedItem();
				String order = (String) orderCombo.getSelectedItem();  // Get selected order
				sortData(sortBy, algorithm, order);
			}
		});

		// Action listener for random button
		randomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomizeData();
			}
		});

		// Action listener for reset button
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetData();
			}
		});

		return mainPanel;
	}

	private void initializeTable() {
		tableModel = new WeatherTableModel(weatherRecords);
		table = new JTable(tableModel);
		table.setFillsViewportHeight(true);

		table.setRowHeight(0, 100);

		// Disable automatic row sorting to handle it manually
		table.setAutoCreateRowSorter(false);

		// Create chart renderers for numeric columns
		applyRenderers();
	}

	private void applyRenderers() {
		Map<Integer, WeatherRecord.WeatherField> columnToField = Map.of(
			0, FORMATTED_DATE,
			3, TEMPERATURE,
			4, APPARENT_TEMPERATURE,
			5, HUMIDITY,
			6, WIND_SPEED,
			7, WIND_BEARING,
			8, VISIBILITY,
			9, CLOUD_COVER,
			10, PRESSURE
		);

		for (Map.Entry<Integer, WeatherRecord.WeatherField> entry : columnToField.entrySet()) {
			table.getColumnModel().getColumn(entry.getKey())
				.setCellRenderer(new ChartRenderer(tableModel.getRecords(), entry.getValue()));
		}
	}

	private void sortData(String sortBy, String algorithm, String order) {
		final Comparator<WeatherRecord> comparator;

		switch (sortBy) {
			case "Temperature":
				comparator = Comparator.comparingDouble(WeatherRecord::getTemperature);
				break;
			case "Apparent Temperature":
				comparator = Comparator.comparingDouble(WeatherRecord::getApparentTemperature);
				break;
			case "Humidity":
				comparator = Comparator.comparingDouble(WeatherRecord::getHumidity);
				break;
			case "Wind Speed":
				comparator = Comparator.comparingDouble(WeatherRecord::getWindSpeed);
				break;
			case "Wind Bearing":
				comparator = Comparator.comparingDouble(WeatherRecord::getWindBearing);
				break;
			case "Visibility":
				comparator = Comparator.comparingDouble(WeatherRecord::getVisibility);
				break;
			case "Cloud Cover":
				comparator = Comparator.comparingDouble(WeatherRecord::getLoudCover); // Kept original method name
				break;
			case "Pressure":
				comparator = Comparator.comparingDouble(WeatherRecord::getPressure);
				break;
			case "Formatted Date":
				comparator = Comparator.comparing(WeatherRecord::getFormattedDate);
				break;
			case "Summary":
				comparator = Comparator.comparing(WeatherRecord::getSummary, Comparator.nullsLast(String::compareTo));
				break;
			case "Precip Type":
				comparator = Comparator.comparing(WeatherRecord::getPrecipType, Comparator.nullsLast(String::compareTo));
				break;
			case "Daily Summary":
				comparator = Comparator.comparing(WeatherRecord::getDailySummary, Comparator.nullsLast(String::compareTo));
				break;
			default:
				JOptionPane.showMessageDialog(this, "Unknown sort field: " + sortBy);
				return;
		}

		final Comparator<WeatherRecord> finalComparator = "Descending".equals(order) ? comparator.reversed() : comparator;

		sortButton.setEnabled(false);
		randomButton.setEnabled(false);
		resetButton.setEnabled(false);
		timeLabel.setText("Time: "); // Reset timeLabel

		final String finalAlgorithm = algorithm;

		SwingWorker<Long, Void> sorter = new SwingWorker<>() {
			private long elapsedTime;

			@Override
			protected Long doInBackground() throws Exception {
				final WeatherRecord[] recordsArray = weatherRecords.toArray(new WeatherRecord[0]);

				long startTime = System.nanoTime(); // Start timing

				try {
					switch (finalAlgorithm) {
						case "Quick":
						case "Merge":
						case "Shell":
						case "Selection":
							recordSorter.sortRecords(recordsArray, finalComparator, finalAlgorithm);
							break;
						case "Sort":
							Arrays.sort(recordsArray, finalComparator);
							break;
						case "Parallel Sort":
							Arrays.parallelSort(recordsArray, finalComparator);
							break;
						default:
							throw new IllegalArgumentException("Unknown sorting algorithm: " + finalAlgorithm);
					}
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage());
				}

				long endTime = System.nanoTime(); // End timing
				elapsedTime = endTime - startTime;

				weatherRecords = new ArrayList<>(Arrays.asList(recordsArray));
				return elapsedTime;
			}

			@Override
			protected void done() {
				try {
					long sortTime = get();
					SwingUtilities.invokeLater(() -> {
						tableModel.setRecords(weatherRecords);
						applyRenderers();
						table.setRowHeight(0, 100);
						table.repaint();
						sortButton.setEnabled(true);
						randomButton.setEnabled(true);
						resetButton.setEnabled(true);

						// Update the time label with elapsed time
						timeLabel.setText("Time: " + sortTime + " ns");

						// Update the chart
						JFreeChart updatedChart = createYearlyCountsChart();
						// Assuming you have a reference to the chart panel, you might need to update it here
					});
				} catch (Exception e) {
					e.printStackTrace();
					sortButton.setEnabled(true);
					randomButton.setEnabled(true);
					resetButton.setEnabled(true);
				}
			}
		};

		sorter.execute();
	}

	/**
	 * Shuffles the weatherRecords list into a random order.
	 */
	private void randomizeData() {
		sortButton.setEnabled(false);
		randomButton.setEnabled(false);
		resetButton.setEnabled(false);
		timeLabel.setText("Time: "); // Reset timeLabel

		SwingWorker<Void, Void> randomizer = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				// Shuffle the weatherRecords list
				java.util.Collections.shuffle(weatherRecords);
				return null;
			}

			@Override
			protected void done() {
				SwingUtilities.invokeLater(() -> {
					tableModel.setRecords(weatherRecords);
					applyRenderers();
					table.setRowHeight(0, 100);
					table.repaint();
					sortButton.setEnabled(true);
					randomButton.setEnabled(true);
					resetButton.setEnabled(true);

					// Reset the time label as no sorting was performed
					timeLabel.setText("Time: ");
				});
			}
		};

		randomizer.execute();
	}

	/**
	 * Resets the weatherRecords list to its original order as loaded from the CSV.
	 */
	private void resetData() {
		sortButton.setEnabled(false);
		randomButton.setEnabled(false);
		resetButton.setEnabled(false);
		timeLabel.setText("Time: "); // Reset timeLabel

		SwingWorker<Void, Void> resetter = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				// Reset the weatherRecords to the original order
				weatherRecords = new ArrayList<>(originalWeatherRecords);
				return null;
			}

			@Override
			protected void done() {
				SwingUtilities.invokeLater(() -> {
					tableModel.setRecords(weatherRecords);
					applyRenderers();
					table.setRowHeight(0, 100);
					table.repaint();
					sortButton.setEnabled(true);
					randomButton.setEnabled(true);
					resetButton.setEnabled(true);

					// Reset the time label as no sorting was performed
					timeLabel.setText("Time: ");
				});
			}
		};

		resetter.execute();
	}

	/**
	 * Aggregates the number of weather records per year.
	 *
	 * @return A Map where the key is the year and the value is the count of records.
	 */
	private Map<Integer, Long> getWeatherCountsPerYear() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");

		return weatherRecords.stream()
			.map(record -> {
				try {
					// Parse the formattedDate to extract the year
					LocalDateTime dateTime = LocalDateTime.parse(record.getFormattedDate(), formatter);
					return dateTime.getYear();
				} catch (DateTimeParseException e) {
					// Handle parsing exceptions if necessary
					return null;
				}
			})
			.filter(year -> year != null)
			.collect(Collectors.groupingBy(year -> year, Collectors.counting()));
	}

	/**
	 * Creates a bar chart showing the count of weather records per year.
	 *
	 * @return A JFreeChart object representing the bar chart.
	 */
	private JFreeChart createYearlyCountsChart() {
		Map<Integer, Long> countsPerYear = getWeatherCountsPerYear();

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		countsPerYear.forEach((year, count) -> {
			dataset.addValue(count, "Weather Records", year.toString());
		});

		JFreeChart barChart = ChartFactory.createBarChart(
			"", "", "",
			dataset, PlotOrientation.VERTICAL,
			false, true, false
		);

		barChart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
		barChart.getCategoryPlot().getRangeAxis().setVisible(false);
		barChart.setBorderVisible(false);
		barChart.setBackgroundPaint(null);
		barChart.getPlot().setBackgroundPaint(Color.WHITE);
		barChart.getCategoryPlot().getRangeAxis().setVisible(false);


		return barChart;
	}

	private class ChartRenderer extends DefaultTableCellRenderer {
		private final Map<WeatherRecord.WeatherField, JFreeChart> chartCache = new HashMap<>();
		private final WeatherRecord.WeatherField field;

		public ChartRenderer(ArrayList<WeatherRecord> records, WeatherRecord.WeatherField field) {
			this.field = field;
			if (!chartCache.containsKey(field)) {
				chartCache.put(field, createHistogram(records, field));
			}
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (row == 0) {
				JFreeChart chart = chartCache.get(field);
				ChartPanel chartPanel = new ChartPanel(chart);
				chartPanel.setPreferredSize(new Dimension(150, 150));
				chartPanel.setBorder(BorderFactory.createEmptyBorder());
				chartPanel.setBackground(null);
				return chartPanel;
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

	private JFreeChart createHistogram(ArrayList<WeatherRecord> weatherRecords, WeatherRecord.WeatherField attribute) {
		if (attribute == FORMATTED_DATE) return createYearlyCountsChart(); 

		Map<WeatherRecord.WeatherField, java.util.function.Function<WeatherRecord, Double>> attributeMap = Map.of(
			TEMPERATURE, WeatherRecord::getTemperature,
			APPARENT_TEMPERATURE, WeatherRecord::getApparentTemperature,
			HUMIDITY, WeatherRecord::getHumidity,
			WIND_SPEED, WeatherRecord::getWindSpeed,
			WIND_BEARING, WeatherRecord::getWindBearing,
			VISIBILITY, WeatherRecord::getVisibility,
			CLOUD_COVER, WeatherRecord::getLoudCover, // Kept original method name
			PRESSURE, WeatherRecord::getPressure
		);

		java.util.function.Function<WeatherRecord, Double> attributeFunction = attributeMap.get(attribute);
		double[] data = weatherRecords.stream().mapToDouble(attributeFunction::apply).toArray();

		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.FREQUENCY);
		dataset.addSeries(attribute.toString(), data, 20);

		JFreeChart histogram = ChartFactory.createHistogram(
			null, "", "", dataset,
			PlotOrientation.VERTICAL,
			false, true, false
		);

		histogram.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
		histogram.getXYPlot().getRangeAxis().setVisible(false);
		histogram.setBorderVisible(false);
		histogram.setBackgroundPaint(null);
		histogram.getPlot().setBackgroundPaint(Color.WHITE);

		histogram.getXYPlot().getRangeAxis().setVisible(false);
		if (histogram.getLegend() != null) histogram.getLegend().setVisible(false);

		return histogram;
	}
}
