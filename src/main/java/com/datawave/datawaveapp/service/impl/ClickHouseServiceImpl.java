package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.dto.PriceMetricRecord;
import com.datawave.datawaveapp.service.ClickHouseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClickHouseServiceImpl implements ClickHouseService {

    private final JdbcTemplate jdbcTemplate;

    public ClickHouseServiceImpl(@Qualifier("clickTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PriceMetricRecord> getMetricByJdbc() {

        return this.jdbcTemplate.query("SELECT * FROM metric_price",
                (rs, rowNum) -> new PriceMetricRecord(
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getFloat("value"),
                        rs.getString("asset"),
                        rs.getString("exchange")
                ));
    }

    @Override
    public List<PriceMetricRecord> getMetric(String metricName) {

        return this.jdbcTemplate.query("SELECT * FROM ?",
                ps -> ps.setString(1, metricName),
                (rs, rowNum) -> new PriceMetricRecord(
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getFloat("value"),
                        rs.getString("asset"),
                        rs.getString("exchange")
                ));
    }

    @Override
    public List<String> getColumns(String metricName) {

        List<List<String>> result = this.jdbcTemplate.query("SELECT * FROM ? LIMIT 1",
                ps -> ps.setString(1, metricName),
                (rs, rowNum) -> {
                    List<String> list = new ArrayList<>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        list.add(rs.getMetaData().getColumnName(i));
                    }
                    return list;
                }
        );
        return !result.isEmpty() ? result.get(0) : null;
    }

    //FIXME SQL Injection vulnerability
    @Override
    public List<String> getFilteredValues(String metricName, String column, Map<String, Object> filter) {

        StringBuilder whereClause = new StringBuilder();

        if (!filter.isEmpty()) {

            String collected = filter.entrySet()
                    .stream()
                    .map(kv -> kv.getKey() + " = '" + kv.getValue() + "'")
                    .collect(Collectors.joining(" and "));

            whereClause.append("SELECT DISTINCT " + column + " FROM " + metricName + " WHERE ").append(collected);
        }

        return this.jdbcTemplate.query(whereClause.toString(),
                (rs, rowNum) -> rs.getString(column)
        );

//        return this.jdbcTemplate.query("SELECT DISTINCT default.\\'?'.? FROM default.? ?",
//                ps -> {
//                    ps.setString(0, metricName);
//                    ps.setString(1, column);
//                    ps.setString(2, metricName);
//                    ps.setString(3, whereClause.toString());
//                },
//                (rs, rowNum) -> rs.getString(column)
//        );
    }
}
