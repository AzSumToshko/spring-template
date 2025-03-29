package com.example.spring_template.service.chart.generator;

import com.example.spring_template.service.chart.base.ChartGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;

import java.awt.geom.Ellipse2D;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.awt.*;
import java.util.List;
import java.util.Random;

@Service
public class BubbleChartGenerator extends ChartGenerator {

    //Example class
    @Setter
    @Getter
    @AllArgsConstructor
    private class ScatterGraphItem {
        private double averageCost;
        private double averagePricing;
        private int hour;
        private double kwh;
    }

    private List<ScatterGraphItem> generateDummyScatterData() {
        List<ScatterGraphItem> collection = new ArrayList<>();

        double[] kwhValues = {
                6.1408, 4.2909, 3.3013, 2.5541, 2.527, 2.5813, 3.967,
                6.9152, 12.9744, 19.2239, 25.5548, 36.9941, 54.2344,
                60.5926, 55.7153, 54.6284, 54.4925, 55.1447, 49.4794,
                39.2629, 28.8018, 21.4905, 14.333, 9.8497
        };

        Random random = new Random();

        for (int hour = 0; hour < kwhValues.length; hour++) {
            double baseCost = 0.15 + (random.nextDouble() * 0.10);      // 0.15 - 0.25
            double basePrice = baseCost + (random.nextDouble() * 0.35); // Cost + 0.0 - 0.35

            collection.add(new ScatterGraphItem(
                    Math.round(baseCost * 10000.0) / 10000.0,
                    Math.round(basePrice * 10000.0) / 10000.0,
                    hour,
                    kwhValues[hour]
            ));
        }

        return collection;
    }


    public String generateScatterChart() {
        List<ScatterGraphItem> collection = generateDummyScatterData();

        // Create dataset
        XYSeriesCollection dataset = createDataset(collection);

        // Create scatter chart
        JFreeChart scatterChart = createScatterChart(dataset);

        // Customize chart legend
        customizeLegend(scatterChart);

        // Customize chart plot
        XYPlot plot = scatterChart.getXYPlot();
        customizePlot(plot);

        // Customize domain axis (X-axis)
        customizeDomainAxis(plot);

        // Customize range axis (Primary Y-axis)
        customizePrimaryRangeAxis(plot);

        // Add secondary axis (Right Y-axis)
        addSecondaryRangeAxis(plot);

        // Add custom gridlines for secondary axis
        addSecondaryGridlines(plot);

        // Return the chart as a Base64-encoded image
        return createBase64Image(scatterChart);
    }

    private XYSeriesCollection createDataset(List<ScatterGraphItem> scatterGraphItems) {
        XYSeries kwhSeries = new XYSeries("kWh per hour (kWh)");
        XYSeries costSeries = new XYSeries("Average cost per kWh (cent)");
        XYSeries pricingSeries = new XYSeries("Average pricing per kWh (cents)");

        for (ScatterGraphItem item : scatterGraphItems) {
            kwhSeries.add(item.getHour(), item.getKwh());
            costSeries.add(item.getHour(), ((item.getAverageCost() * 100) / 90) * 500); // Convert to cents
            pricingSeries.add(item.getHour(), ((item.getAveragePricing() * 100) / 90) * 500); // Convert to cents
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(kwhSeries);
        dataset.addSeries(costSeries);
        dataset.addSeries(pricingSeries);
        return dataset;
    }

    private JFreeChart createScatterChart(XYSeriesCollection dataset) {
        return ChartFactory.createScatterPlot(
                null,
                "Hour of Day",
                "kWh / Cents",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private void customizeLegend(JFreeChart chart) {
        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.TOP);
        legend.setHorizontalAlignment(HorizontalAlignment.CENTER);
        legend.setVerticalAlignment(VerticalAlignment.CENTER);
        legend.setPadding(new RectangleInsets(10, 10, 10, 10));
    }

    private void customizePlot(XYPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlineStroke(new BasicStroke(1.0f));
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlineStroke(new BasicStroke(1.0f));
        plot.setInsets(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

        // Customize renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        customizeRenderer(renderer);
        plot.setRenderer(renderer);
    }

    private void customizeRenderer(XYLineAndShapeRenderer renderer) {
        // Enable and customize item labels
        renderer.setDefaultItemLabelGenerator((data, series, item) -> {
            double yValue = Math.round(data.getYValue(series, item));
            return String.valueOf(series == 0 ? yValue : Math.round((yValue / 500) * 90)).split("\\.")[0];
        });
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
        renderer.setDefaultLinesVisible(false);

        // Customize series visibility and colors
        customizeSeries(renderer, 0, Color.decode("#3b8745")); // Green
        customizeSeries(renderer, 1, Color.decode("#d91c5c")); // Red
        customizeSeries(renderer, 2, Color.decode("#2071c5")); // Blue

        // Customize shapes
        renderer.setSeriesShape(0, new Ellipse2D.Double(-7, -7, 14, 14));
        renderer.setSeriesShape(1, new Ellipse2D.Double(-7, -7, 14, 14));
        renderer.setSeriesShape(2, new Ellipse2D.Double(-7, -7, 14, 14));

        // Position labels for each series
        ItemLabelPosition labelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER
        );
        renderer.setSeriesPositiveItemLabelPosition(0, labelPosition);
        renderer.setSeriesPositiveItemLabelPosition(1, labelPosition);
        renderer.setSeriesPositiveItemLabelPosition(2, labelPosition);
    }

    private void customizeSeries(XYLineAndShapeRenderer renderer, int seriesIndex, Color color) {
        renderer.setSeriesShapesVisible(seriesIndex, true);
        renderer.setSeriesPaint(seriesIndex, color);
        renderer.setSeriesItemLabelPaint(seriesIndex, color);
    }

    private void customizeDomainAxis(XYPlot plot) {
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setTickUnit(new NumberTickUnit(1));
        domainAxis.setLabel("");
        domainAxis.setRange(-0.5, 23.5);

        domainAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                int hour = (int) number % 24;
                boolean isPM = hour >= 12;
                int displayHour = hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour);
                String period = isPM ? "PM" : "AM";
                return toAppendTo.append(displayHour % 2 == 0 ? String.format("%d:00 %s", displayHour, period) : "");
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                return null;
            }
        });
    }

    private void customizePrimaryRangeAxis(XYPlot plot) {
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabel("kWh per hour (kWh)");
        rangeAxis.setRange(0, 500);
        rangeAxis.setTickUnit(new NumberTickUnit(100));
        rangeAxis.setLabelPaint(Color.decode("#3b8745"));
        rangeAxis.setTickLabelPaint(Color.decode("#3b8745"));

        rangeAxis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                return toAppendTo.append((int) number).append(" kWh");
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return format((double) number, toAppendTo, pos);
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                return null;
            }
        });
    }

    private void addSecondaryRangeAxis(XYPlot plot) {
        NumberAxis centsAxis = new NumberAxis("Cents");
        centsAxis.setRange(0, 90);
        centsAxis.setTickUnit(new NumberTickUnit(5));
        centsAxis.setLabelPaint(Color.RED);
        centsAxis.setTickLabelPaint(Color.RED);

        plot.setRangeAxis(1, centsAxis);
        plot.mapDatasetToRangeAxis(1, 1);
    }

    private void addSecondaryGridlines(XYPlot plot) {
        for (double value = 0; value <= 500; value += 27.8) {
            ValueMarker gridline = new ValueMarker(value);
            gridline.setPaint(Color.LIGHT_GRAY);
            gridline.setStroke(new BasicStroke(1.0f));
            plot.addRangeMarker(gridline, Layer.BACKGROUND);
        }
    }
}
