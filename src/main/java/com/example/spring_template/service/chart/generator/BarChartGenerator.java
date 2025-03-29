package com.example.spring_template.service.chart.generator;

import com.example.spring_template.service.chart.base.ChartGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.RectangularShape;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

@Service
public class BarChartGenerator extends ChartGenerator {
    @Getter
    @Setter
    @AllArgsConstructor
    private class IncomeAndReturn {

        private double accumulatedReturnPercentage;
        private double accumulatedReturnValue;
        private double netIncomePeriod;
        private String periodId;
    }

    private ArrayList<IncomeAndReturn> generateDummyData() {
        ArrayList<IncomeAndReturn> data = new ArrayList<>();

        double accumulatedReturn = 0;
        double accumulatedPercentage = 0;

        for (int i = 1; i <= 6; i++) {
            double netIncome = 500 + (Math.random() * 1500);
            accumulatedReturn += netIncome;
            accumulatedPercentage = Math.min(1.0, accumulatedPercentage + 0.2); // hit 1.0 on last step

            data.add(new IncomeAndReturn(
                    accumulatedPercentage,
                    accumulatedReturn,
                    netIncome,
                    "Q" + i
            ));
        }

        return data;
    }


    public String generateBarChart() {
        ArrayList<IncomeAndReturn> collection = generateDummyData();

        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

        double totalAccumulatedReturn = 0;
        double totalReturn = 0;
        boolean returnCaptured = false;

        for (var element : collection) {
            if (element.getAccumulatedReturnPercentage() == 1 && !returnCaptured) {
                totalReturn = element.getAccumulatedReturnValue();
                returnCaptured = true;
            }

            totalAccumulatedReturn = element.getAccumulatedReturnValue();

            lineDataset.addValue(element.getAccumulatedReturnValue(), "Accumulated return", element.getPeriodId());
            barDataset.addValue(element.getNetIncomePeriod(), "Net income per period", element.getPeriodId());
        }

        // Setup the chart plot
        CategoryPlot plot = new CategoryPlot();
        plot.setDataset(0, lineDataset);
        plot.setRenderer(0, createLineRenderer(totalAccumulatedReturn));
        plot.setDataset(1, barDataset);
        plot.setRenderer(1, createBarRenderer());

        plot.setRangeAxis(0, createValueAxis(totalAccumulatedReturn));
        plot.setRangeAxis(1, createPercentageAxis());
        plot.setDomainAxis(new CategoryAxis(""));
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.addRangeMarker(renderVirtualMarker(totalReturn));

        JFreeChart barChart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        LegendTitle legend = barChart.getLegend();
        legend.setPosition(RectangleEdge.TOP);
        legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
        legend.setFrame(new BlockBorder(Color.WHITE));
        legend.setPadding(new RectangleInsets(0, 0, 16, 0));

        barChart.setBackgroundPaint(Color.WHITE);

        return createBase64Image(barChart);
    }

    private LineAndShapeRenderer createLineRenderer(double totalAccumulatedReturn) {
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.decode("#d91c5c"));
        renderer.setSeriesStroke(0, new BasicStroke(5.0f));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 18));
        renderer.setDefaultItemLabelPaint(Color.decode("#d91c5c"));

        renderer.setDefaultItemLabelGenerator(new CategoryItemLabelGenerator() {
            @Override
            public String generateRowLabel(CategoryDataset dataset, int row) {
                return "";
            }

            @Override
            public String generateColumnLabel(CategoryDataset dataset, int column) {
                return "";
            }

            @Override
            public String generateLabel(CategoryDataset dataset, int row, int column) {
                Number value = dataset.getValue(row, column);
                if (value != null) {
                    double percentage = (value.doubleValue() / totalAccumulatedReturn) * 100;
                    return String.format("%.1f%%", percentage);
                }
                return "";
            }
        });

        return renderer;
    }

    private BarRenderer createBarRenderer() {
        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, Color.decode("#2071c5"));

        renderer.setBarPainter(new BarPainter() {
            @Override
            public void paintBar(Graphics2D g2, BarRenderer r, int row, int column,
                                 RectangularShape bar, RectangleEdge base) {
                g2.setPaint(r.getItemPaint(row, column));
                g2.fill(bar);
            }

            @Override
            public void paintBarShadow(Graphics2D g2, BarRenderer r, int row, int column,
                                       RectangularShape bar, RectangleEdge base, boolean pegShadow) {
                // No shadow rendering for clean flat bars
            }
        });

        return renderer;
    }


    private NumberAxis createPercentageAxis() {
        NumberAxis axis = new NumberAxis("%");
        axis.setRange(0.0, 1.0);
        axis.setTickUnit(new NumberTickUnit(0.1));
        axis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        axis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        return axis;
    }

    private NumberAxis createValueAxis(double totalValue) {
        NumberAxis axis = new NumberAxis("$");

        axis.setNumberFormatOverride(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                return toAppendTo.append(String.format("$%.0fK", number / 1000));
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

        axis.setTickUnit(new NumberTickUnit(Math.ceil(totalValue / 10)));
        axis.setMinorTickMarksVisible(false);
        axis.setTickMarksVisible(true);
        return axis;
    }

    private ValueMarker renderVirtualMarker(double totalReturn) {
        ValueMarker marker = new ValueMarker(totalReturn);
        marker.setPaint(Color.RED);
        marker.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f, 6.0f}, 0.0f));
        marker.setLabelFont(new Font("SansSerif", Font.BOLD, 16));
        marker.setLabelPaint(Color.GRAY);
        marker.setLabelAnchor(RectangleAnchor.TOP);
        return marker;
    }
}
