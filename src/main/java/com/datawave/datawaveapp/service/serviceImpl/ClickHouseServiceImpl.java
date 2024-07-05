package com.datawave.datawaveapp.service.serviceImpl;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseException;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseColumn;
import com.clickhouse.data.ClickHouseFormat;
import com.datawave.datawaveapp.model.dto.PriceMetricRecord;
import com.datawave.datawaveapp.service.ClickHouseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClickHouseServiceImpl implements ClickHouseService {

    private final ClickHouseClient client;
    private final ClickHouseNode node;

    public ClickHouseServiceImpl(ClickHouseClient client, ClickHouseNode node) {
        this.client = client;
        this.node = node;
    }

    @Override
    public List<PriceMetricRecord> getMetric(String metricName) throws ClickHouseException {

        ClickHouseResponse clickHouseResponse = this.client.read(this.node)
                .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
                .query("select timestamp,value,asset,exchange from default.\"" + metricName + "\";")
                .executeAndWait();

        return clickHouseResponse.stream()
                .map(row ->
                        new PriceMetricRecord(
                                row.getValue("timestamp").asInstant(),
                                row.getValue("value").asFloat(),
                                row.getValue("asset").asString(),
                                row.getValue("exchange").asString()
                        )
                ).collect(Collectors.toList());
    }

    @Override
    public List<String> getColumns(String metricName) throws ClickHouseException {

        ClickHouseResponse clickHouseResponse = this.client.read(this.node)
                .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
                .query("select * from default.\"" + metricName + "\";")
                .executeAndWait();


        return clickHouseResponse.getColumns()
                .stream()
                .map(ClickHouseColumn::getColumnName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctValues(String metricName, String column, Map<String, Object> filter) throws ClickHouseException {

        StringBuilder query = new StringBuilder("select distinct " + column + " from default.\"" + metricName + "\"");

        String whereClause = filter.entrySet()
                .stream()
                .map(kv -> kv.getKey() + " = '" + kv.getValue() + "'")
                .collect(Collectors.joining(" and "));

        if (!filter.isEmpty()) {
            query.append("where " + whereClause);
        }
        query.append(";");

        ClickHouseResponse clickHouseResponse = this.client.read(node)
                .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
                .query(query.toString())
                .executeAndWait();

        return clickHouseResponse.stream()
                .map(row -> row.getValue(column).asString())
                .collect(Collectors.toList());
    }
}
