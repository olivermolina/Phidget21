/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javax.swing.JPanel;

public class LineChartPanel extends JPanel {

    private JFXPanel fxPanel;
    private XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private ExecutorService executor;
    private int xSeriesData = 0;
    private ConcurrentLinkedQueue<Number> ySeriesData = new ConcurrentLinkedQueue<>();
    private int maxValu;
    int Resolution_ms = 500;
    LineChart<Number, Number> lineChart;

    public LineChartPanel() {
	initComponents();
    }

    private void initComponents() {
	fxPanel = new JFXPanel();
	fxPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
	this.add(fxPanel);
	Platform.runLater(new Runnable() {

	    @Override
	    public void run() {

		final NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Trace Count");
		final NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Analog In Values");

		// Create a LineChart
		lineChart = new LineChart<Number, Number>(xAxis, yAxis) {
		    // Override to remove symbols on each data point
		    @Override
		    protected void dataItemAdded(XYChart.Series<Number, Number> series, int itemIndex, XYChart.Data<Number, Number> item) {
		    }
		};

		lineChart.setAnimated(false);
		lineChart.setTitle("Trace");
		lineChart.setHorizontalGridLinesVisible(true);

		// Set Name for Series
		series.setName("Analog In");

		// Add Chart Series
		lineChart.getData().addAll(series);		
		populateTooltips(lineChart);
		Scene scene = new Scene(lineChart, 1300, 400);
		scene.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());
		fxPanel.setScene(scene);
		LineChartPanel.this.repaint();
		LineChartPanel.this.validate();

	    }
	});
	Platform.setImplicitExit(false);
    }

    public void setScale(int scale) {
	global_vars.scale = scale;
    }

    public void setOffset(int offset) {
	global_vars.Yoffset = offset;
    }

    public void start() {
	if (global_vars.MaxX == 0) {
	    global_vars.MaxX = 1000;
	}

	if (global_vars.scale == 0) {
	    global_vars.scale = +3;
	}
	if (global_vars.Yoffset == 0) {
	    global_vars.Yoffset = 300;

	}

	if (global_vars.Resolution_ms == 0) {
	    global_vars.Resolution_ms = Resolution_ms;
	}
	executor = Executors.newCachedThreadPool(new ThreadFactory() {
	    @Override
	    public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		return thread;
	    }
	});

	AddToQueue addToQueue = new AddToQueue();
	executor.execute(addToQueue);
	//-- Prepare Timeline
	prepareTimeline();
    }

    public void stop() {
	executor.shutdown();
    }

    private class AddToQueue implements Runnable {

	public void run() {
	    try {
		Random random = new Random();
		final int yValue = global_vars.value_Read == 0 ? random.nextInt(300) : global_vars.value_Read;
		ySeriesData.add(yValue);

		//find maxvalue
		if (global_vars.value_Read > maxValu) {
		    maxValu = global_vars.value_Read;
		}

		Thread.sleep(global_vars.Resolution_ms);
		executor.execute(this);
	    } catch (InterruptedException ex) {
		ex.printStackTrace();
	    }
	}
    }

//-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
	// Every frame to take any data from queue and add to chart
	new AnimationTimer() {
	    @Override
	    public void handle(long now) {
		addDataToSeries();
	    }
	}.start();
    }

    private void addDataToSeries() {

	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	Date date = new Date();
	for (int i = 0; i < global_vars.MaxX; i++) { //-- add 20 numbers to the plot+
	    if (ySeriesData.isEmpty()) {
		break;
	    }
	    date.setTime(date.getTime() + i * 11111);
	    Data data = new XYChart.Data<>(xSeriesData++, ySeriesData.remove());
	    series.getData().add(data);
	}
    }

    private void populateTooltip(final Series<Number, Number> series, final Data<Number, Number> data) {

	if (data == null) {
	    return;
	}
	final Tooltip tooltip = new Tooltip(
		series.getName()
		+ System.lineSeparator()
		+ data.getYValue()
		+ System.lineSeparator());

	Tooltip.install(data.getNode(), tooltip);
    }

    private void populateTooltips(final LineChart<Number, Number> lineChart) {
	
	for (final Series<Number, Number> series : lineChart.getData()) {
	    for (final Data<Number, Number> data : series.getData()) {
		populateTooltip(series, data);
	    }
	}
    }
}
