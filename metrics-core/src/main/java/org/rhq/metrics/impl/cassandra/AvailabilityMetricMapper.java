package org.rhq.metrics.impl.cassandra;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.google.common.base.Function;

import org.rhq.metrics.core.Availability;
import org.rhq.metrics.core.AvailabilityMetric;
import org.rhq.metrics.core.Interval;
import org.rhq.metrics.core.MetricId;
import org.rhq.metrics.core.Tag;

/**
 * @author John Sanda
 */
public class AvailabilityMetricMapper implements Function<ResultSet, AvailabilityMetric> {

    private enum ColumnIndex {
        TENANT_ID,
        METRIC_NAME,
        INTERVAL,
        DPART,
        TIME,
        META_DATA,
        DATA_RETENTION,
        AVAILABILITY,
        TAGS
    }

    @Override
    public AvailabilityMetric apply(ResultSet resultSet) {
        if (resultSet.isExhausted()) {
            return null;
        }
        Row firstRow = resultSet.one();
        AvailabilityMetric metric = getMetric(firstRow);
        metric.addData(new Availability(metric, firstRow.getUUID(ColumnIndex.TIME.ordinal()), firstRow.getBytes(
            ColumnIndex.AVAILABILITY.ordinal()), getTags(firstRow)));

        for (Row row : resultSet) {
            metric.addData(new Availability(metric, row.getUUID(ColumnIndex.TIME.ordinal()), row.getBytes(
                ColumnIndex.AVAILABILITY.ordinal()), getTags(row)));
        }

        return metric;
    }

    private AvailabilityMetric getMetric(Row row) {
        AvailabilityMetric metric = new AvailabilityMetric(row.getString(ColumnIndex.TENANT_ID.ordinal()), getId(row),
            row.getMap(ColumnIndex.META_DATA.ordinal(), String.class, String.class), row.getInt(
            ColumnIndex.DATA_RETENTION.ordinal()));
        metric.setDpart(row.getLong(ColumnIndex.DPART.ordinal()));

        return metric;
    }

    private MetricId getId(Row row) {
        return new MetricId(row.getString(ColumnIndex.METRIC_NAME.ordinal()), Interval.parse(row.getString(
            ColumnIndex.INTERVAL.ordinal())));
    }

    private Set<Tag> getTags(Row row) {
        Map<String, String> map = row.getMap(ColumnIndex.TAGS.ordinal(), String.class, String.class);
        Set<Tag> tags;
        if (map.isEmpty()) {
            tags = Collections.emptySet();
        } else {
            tags = new HashSet<>();
            for (String tag : map.keySet()) {
                tags.add(new Tag(tag, map.get(tag)));
            }
        }
        return tags;
    }
}
