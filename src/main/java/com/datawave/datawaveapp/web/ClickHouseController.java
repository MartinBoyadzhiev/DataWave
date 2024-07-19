package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.PriceMetricRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ClickHouseController {

    private final JdbcTemplate jdbcTemplate;

    public ClickHouseController(@Qualifier("clickTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //TODO: retrieving data by path variable
    @GetMapping("/metric")
    public List<PriceMetricRecord> getMetricByJDBC() {
        return this.jdbcTemplate.query("SELECT * FROM default.`metric.price`",
                (rs, rowNum) -> new PriceMetricRecord(
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getFloat("value"),
                        rs.getString("asset"),
                        rs.getString("exchange")
                ));
    }
//    @GetMapping("/metric/{metricName}/data")
//    public List<PriceMetricRecord> getMetric(@PathVariable("metricName") String metricName) throws ClickHouseException {
//        return this.clickHouseService.getMetric(metricName);
//    }
//
//    @GetMapping("/metric/{metricName}/columns")
//    public List<String> getColumnNames(@PathVariable("metricName") String metricName) throws ClickHouseException {
//        return this.clickHouseService.getColumns(metricName);
//    }
//
//    @GetMapping("/metric/{metricName}/values/{column}")
//    public List<String> getFilteredValues(@PathVariable("metricName") String metricName,
//                                          @PathVariable("column") String column,
//                                          @RequestParam Map<String, Object> filter) throws ClickHouseException {
//        return this.clickHouseService.getDistinctValues(metricName, column, filter);
//    }
}
