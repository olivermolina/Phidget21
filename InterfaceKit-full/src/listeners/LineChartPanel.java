/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Tooltip;
import javax.swing.JPanel;
import static listeners.global_vars.scale;

public class LineChartPanel extends JPanel {

    private JFXPanel fxPanel;

    private XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private ExecutorService executor;

    private int xSeriesData = 0;
    private ConcurrentLinkedQueue<Number> ySeriesData = new ConcurrentLinkedQueue<>();

    private NumberAxis xAxis;

    private int Line_number = 0;
    private int Y = 0;
    private int lastInValue = 0;
    private int Yoffset = 0;
    private int InValue = 0;
    private int Yscale = 0;
    private int diff = 0;
    private int Y1 = 0;
    private int Total_diff = 0;
    private int maxValu;
    int Resolution_ms = 500;

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
                final NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("ms");

                // Create a LineChart
                final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis) {
                    // Override to remove symbols on each data point
                    @Override
                    protected void dataItemAdded(XYChart.Series<Number, Number> series, int itemIndex, XYChart.Data<Number, Number> item) {
                    }
                };

                lineChart.setAnimated(false);
                lineChart.setTitle("Trace");
                lineChart.setHorizontalGridLinesVisible(true);

                // Set Name for Series
                series1.setName("Series 1");

                // Add Chart Series
                lineChart.getData().addAll(series1);
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
        Yscale = global_vars.scale;
        Yoffset = global_vars.Yoffset;
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
                Line_number++;
                Y = lastInValue + Yoffset;

                InValue = global_vars.value_Read / Yscale;

                InValue *= -1;

                if (InValue < 0) {
                    InValue = Y1;
                }

                if (lastInValue > InValue) {
                    diff = lastInValue - InValue;
                    Y1 = lastInValue - diff + Yoffset;
                    Total_diff = Total_diff + diff;
                } else if (lastInValue < InValue) {
                    diff = InValue - lastInValue;
                    Y1 = lastInValue + diff + Yoffset;
                } else if (lastInValue == InValue) {
                    diff = 0;
                    Y1 = lastInValue + Yoffset;
                    Total_diff = Total_diff + diff;
                }
                lastInValue = InValue;
                System.out.println("Y value :" + Y1 + " lastInValue :" + lastInValue + " InValue :" + InValue);

                ySeriesData.add(Y1);

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

        for (int i = 0; i < global_vars.MaxX; i++) { //-- add 20 numbers to the plot+
            if (ySeriesData.isEmpty()) {
                break;
            }
            Data data = new XYChart.Data<>(xSeriesData++, ySeriesData.remove());
            series1.getData().add(data);
            Tooltip.install(data.getNode(), new Tooltip(
                    data.getXValue().toString() + "\n"
                    + "Number Of Events : " + data.getYValue()));

        }
    }

    private void StartTrace() {
        JPanel Graphe_Panel = new JPanel();

        int w, h;

        Insets insets = Graphe_Panel.getInsets();

        w = Graphe_Panel.getWidth() - insets.left - insets.right;
        h = Graphe_Panel.getHeight() - insets.top - insets.bottom;
        System.out.println("width=" + w + "hight =" + h);
        global_vars.MaxX = w;
        // Moh code starts here
        int Yoffset;
        int X = 0;
        int Y = 0;
        int X1 = 0;
        int Y1 = 0;
        int Resolution_ms = 20;
        //

        int XStep = 5;
        int diff;
        int Line_number = 0;
        int a;
        //

        int InValue = 0;
        int Yscale = 1;
        int Total_diff = 0;
        double average;
        int maxValu = 0;

        int lastInValue = 0;
        if (global_vars.scale == 0) {
            global_vars.scale = +3;
        }
        if (global_vars.Yoffset == 0) {
            global_vars.Yoffset = 300;

        }

        Yscale = global_vars.scale;
        Yoffset = global_vars.Yoffset;

        Graphics g;
        g = Graphe_Panel.getGraphics();
        g.clearRect(0, 0, Graphe_Panel.getWidth(), Graphe_Panel.getHeight());

        Yscale = scale;

        while (X < global_vars.MaxX) {

            g.drawLine(0, 75, 1500, 75);
            g.drawLine(0, 150, 1500, 150);
            g.drawLine(0, 225, 1500, 225);
            g.drawLine(0, 300, 1500, 300);
            System.out.println("value_Read :" + global_vars.value_Read + "  " + X + "," + Y + "," + X1 + "," + Y1 + " scale = " + Yscale + " maxValu=" + maxValu);

            Line_number++;
            a = Line_number % 2;

            if (a == 1) {
                X = X1;
                Y = lastInValue + Yoffset;

                X1 = X1 + XStep;
                InValue = global_vars.value_Read / Yscale;

                InValue *= -1;

                if (lastInValue > InValue) {
                    diff = lastInValue - InValue;
                    Y1 = lastInValue - diff + Yoffset;
                    Total_diff = Total_diff + diff;
                } else if (lastInValue < InValue) {
                    diff = InValue - lastInValue;
                    Y1 = lastInValue + diff + Yoffset;
                } else if (lastInValue == InValue) {
                    diff = 0;
                    Y1 = lastInValue + Yoffset;
                    Total_diff = Total_diff + diff;
                }
                lastInValue = InValue;

                System.out.println("Index :" + global_vars.value_Index);

            }

            if (a == 0) {
                Color customColor = new Color(2, 10, 255);
                g.setColor(customColor);

                g.drawLine(X, Y, X1, Y1);
                g.setColor(Color.RED);
                // g.drawLine(A, B, A1, B1);

            }

            try {
                Thread.sleep(global_vars.Resolution_ms);

            } catch (InterruptedException ex) {
                Logger.getLogger(InterfaceKit.class.getName()).log(Level.SEVERE, null, ex);

            }
            //find maxvalue
            if (global_vars.value_Read > maxValu) {
                maxValu = global_vars.value_Read;
            }

        }

        average = Total_diff / Line_number;
        System.out.printf("average was= %.03f", average);
        System.out.println(" Total_diff=" + Total_diff + " lines" + Line_number + "maxValu=" + maxValu);

    }//EO StartTrace
}
