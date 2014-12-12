package org.poc.model.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It defines a threshold and type of operator.
 */
public class ThresholdCondition extends Condition {
    private static final Logger LOG = LoggerFactory.getLogger(ThresholdCondition.class);

    public enum Operator {
        LT, GT, LTE, GTE
    };

    private String metricId;
    private Operator operator;
    private Double threshold;

    public ThresholdCondition(String triggerId, String metricId, int conditionSetSize, int conditionSetIndex, Operator operator, Double threshold) {
        super(triggerId, conditionSetSize, conditionSetIndex);
        this.metricId = metricId;
        this.operator = operator;
        this.threshold = threshold;
    }

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getLog(double value) {
        final StringBuilder sb = new StringBuilder();
        sb.append(triggerId).append(": ").append(" ");
        sb.append(getOperator().name()).append(" ");
        sb.append(threshold);
        return sb.toString();
    }

    static public boolean match(Operator operator, double threshold, double value) {
        switch (operator) {
            case LT:
                return value < threshold;
            case GT:
                return value > threshold;
            case LTE:
                return value <= threshold;
            case GTE:
                return value >= threshold;
            default:
                LOG.error("Unknown operator: " + operator.name());
                return false;
        }
    }
}
