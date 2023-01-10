package com.liviz.v2.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.liviz.v2.model.DataSourceSlot;
import com.liviz.v2.service.ApiFetcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ApiFetcherImpl implements ApiFetcher {

    public JsonNode fetchData(String url, List<DataSourceSlot> dataSourceSlot, Map<String, String> requestedParams) {
        // TODO: implement this method

        // TODO: services as interfaces

        return null;
    }

}
