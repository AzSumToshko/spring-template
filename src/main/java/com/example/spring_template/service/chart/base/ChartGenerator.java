package com.example.spring_template.service.chart.base;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ChartGenerator {
    protected String createBase64Image(JFreeChart chart) {
        try {
            BufferedImage image = chart.createBufferedImage(1000, 600);  // Create the chart image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(outputStream, image);    // Write image to output stream

            byte[] imageBytes = outputStream.toByteArray();
            return "data:image/png;base64," + DatatypeConverter.printBase64Binary(imageBytes);     // Convert image bytes to Base64
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
