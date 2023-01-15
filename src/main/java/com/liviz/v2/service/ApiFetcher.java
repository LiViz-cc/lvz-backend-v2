package com.liviz.v2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.liviz.v2.model.DataSourceSlot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface ApiFetcher {
    JsonNode fetchData(String url, List<DataSourceSlot> dataSourceSlot, Map<String, String> requestedParams) throws IOException, URISyntaxException;
}
