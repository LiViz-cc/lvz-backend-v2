package com.liviz.v2.ApiFetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.liviz.v2.DataSource.DataSourceSlot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface ApiFetcher {
    JsonNode fetchData(String url, List<DataSourceSlot> dataSourceSlot, Map<String, String> requestedParams) throws IOException, URISyntaxException;
}
