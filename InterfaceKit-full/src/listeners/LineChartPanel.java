/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.chart.XYChart.Data;
import javax.swing.JPanel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

public class LineChartPanel extends JPanel {

    private JFXPanel fxPanel;
    private XYChart.Series<Number, Number> series;
    private ExecutorService executor;
    private int xSeriesData;
    private ConcurrentLinkedQueue<Number> ySeriesData;
    private int maxValu;
    int Resolution_ms = 30;
    LineChart<Number, Number> lineChart;
    Scene scene;
    NumberAxis xAxis;
    NumberAxis yAxis;
    InterfaceKit parent;

    private static final Object MOUSE_TRIGGER_LOCATION = "tooltip-last-location";

    public LineChartPanel(InterfaceKit parent) {
        this.parent = parent;
        initComponents();
    }

    //Initiliaze required components
    private void initComponents() {
        fxPanel = new JFXPanel();
        fxPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        series = new XYChart.Series<>();
        ySeriesData = new ConcurrentLinkedQueue<>();
        xSeriesData = 0;
        this.add(fxPanel);

        //Required to implement the JavaFx panel to run with SwingWorkers
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                //The tooltip message when you hover the line
                Tooltip t = new Tooltip();
                t.setOnShowing(e -> {
                    Point2D screen = (Point2D) t.getProperties().get(MOUSE_TRIGGER_LOCATION);
                    if (screen == null) {
                        return;
                    }
                    XYChart chart = series.getChart();
                    double localX = chart.getXAxis().screenToLocal(screen).getX();
                    double localY = chart.getYAxis().screenToLocal(screen).getY();
                    Object xValue = chart.getXAxis().getValueForDisplay(localX);
                    Object yValue = chart.getYAxis().getValueForDisplay(localY);
                    String s = String.valueOf(yValue); // the Y value as String data type
                    double d = Double.parseDouble(s); // the Y value as Double data type
                    int i = (int) d; // the Y value as Integer data type

                    //Initialize DateTimestamp
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
                    t.textProperty().set("Vertical Y value: " + i + "\n"
                            + "Timestamp: " + currentTimestamp.toString());
                });

                //Listener when you hover the linechart to show the tooltip
                series.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override
                    public void changed(ObservableValue<? extends Node> arg0, Node arg1,
                            Node node) {
                        Tooltip.install(node, t);
                        node.setOnMouseMoved(e -> {
                            Point2D screen = new Point2D(e.getScreenX(), e.getScreenY());
                            t.getProperties().put(MOUSE_TRIGGER_LOCATION, screen);
                        });
                        series.nodeProperty().removeListener(this);
                    }
                });

                //Construct xAxis 
                xAxis = new NumberAxis();
                xAxis.setLabel("Trace Count");

                //Construct yAxis 
                yAxis = new NumberAxis();
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
                StackPane root = new StackPane();
                root.getChildren().add(lineChart);
                scene = new Scene(root, 1300, 400);
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

    //Method to start/restart drawing the linechart
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

        //provides methods to manage termination and methods that can produce a Future for tracking progress of one or more asynchronous task
        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });

        //Populate Y values in linechart
        //Equivalent to while loop
        AddToQueue addToQueue = new AddToQueue();
        executor.execute(addToQueue);

        //-- Prepare Timeline
        prepareTimeline();

    }

    //Stop the linechart to draw lines
    public void stop() {
        executor.shutdownNow();
    }

    //Reset the linechart
    public void reset() {
        removeAll();
        initComponents();
        revalidate();
        repaint();
    }

    //Method to populate the Y value
    private class AddToQueue implements Runnable {

        public void run() {
            try {
                Random random = new Random();
                int yValue = global_vars.value_Read;
                if (parent.jTextField1.getText().isEmpty()) {
                    yValue = global_vars.value_Read == 0 ? random.nextInt(300) : global_vars.value_Read;

                }

                ySeriesData.add(yValue);

                //find maxvalue
                if (global_vars.value_Read > maxValu) {
                    maxValu = global_vars.value_Read;
                }

                Thread.sleep(global_vars.Resolution_ms);

                executor.execute(this);
            } catch (InterruptedException ex) {

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

    //Initialize series(X and Y) and plot node in the linechart
    private void addDataToSeries() {

        for (int i = 0; i < global_vars.MaxX; i++) {
            if (ySeriesData.isEmpty()) {
                break;
            }
            Number yData = ySeriesData.remove();
            Data data = new XYChart.Data<>(xSeriesData++, yData);
            series.getData().add(data);
        }
    }
}
