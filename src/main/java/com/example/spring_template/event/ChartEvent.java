package com.example.spring_template.event;

import com.example.spring_template.domain.enums.event.ChartEventType;
import com.example.spring_template.event.holder.ChartResultHolder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ChartEvent extends ApplicationEvent {
    private final ChartEventType chartType;

    @Getter
    private final ChartResultHolder resultHolder;

    public ChartEvent(ChartEventType type, ChartResultHolder resultHolder) {
        super("");
        this.chartType = type;
        this.resultHolder = resultHolder;
    }

    public String getSource() {
        return (String) super.getSource();
    }

    public boolean isBubbleChart() {
        return this.chartType == ChartEventType.BUBBLE;
    }

    public boolean isBarChart() {
        return this.chartType == ChartEventType.BAR;
    }

}
