package com.example.spring_template.service.chart;

import com.example.spring_template.event.ChartEvent;
import com.example.spring_template.service.chart.generator.BarChartGenerator;
import com.example.spring_template.service.chart.generator.BubbleChartGenerator;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ChartService {
    private final BubbleChartGenerator bubbleChartGenerator;
    private final BarChartGenerator barChartGenerator;

    public ChartService(BubbleChartGenerator bubbleChartGenerator, BarChartGenerator barChartGenerator) {
        this.bubbleChartGenerator = bubbleChartGenerator;
        this.barChartGenerator = barChartGenerator;
    }

    @EventListener(condition = "#event.bubbleChart")
    public String generateBubbleChart(ChartEvent event) {
        String chart = this.bubbleChartGenerator.generateScatterChart();
        event.getResultHolder().setBase64Chart(chart);
        return chart;
    }

    @EventListener(condition = "#event.barChart")
    public String generateBarChart(ChartEvent event) {
        String chart = this.barChartGenerator.generateBarChart();
        event.getResultHolder().setBase64Chart(chart);
        return chart;
    }
}
