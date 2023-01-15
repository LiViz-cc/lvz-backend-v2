package com.liviz.v2.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.model.DataSourceSlot;
import com.liviz.v2.service.ApiFetcher;
import com.liviz.v2.utils.ParameterStringBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApiFetcherImpl implements ApiFetcher {
    @Autowired
    ParameterStringBuilder parameterStringBuilder;

    public JsonNode fetchData(String urlString, List<DataSourceSlot> dataSourceSlot, Map<String, String> query) throws IOException, URISyntaxException {
        // connect to the url and fetch the data

        Map<String, String> requestParams = buildRequestParamsMap(dataSourceSlot, query);

        URIBuilder builder = new URIBuilder();
        builder.setPath(urlString);
        requestParams.forEach(builder::addParameter);

        URL url = new URL(builder.build().toString());

        StringBuilder content = new StringBuilder();
        // connect to the url
        {
            var con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            // send request
            con.setDoOutput(true);

            int status = con.getResponseCode();

            // read response
            {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
            }

            con.disconnect();
        }

        // string to json
        return new ObjectMapper().readTree(content.toString());
    }

    @NotNull
    private static Map<String, String> buildRequestParamsMap(List<DataSourceSlot> dataSourceSlot, Map<String, String> query) {
        Map<String, String> requestParams = new HashMap<>();

        for (DataSourceSlot slot : dataSourceSlot) {
            String name = slot.getName();
            String type = slot.getSlotType();
            Boolean optional = slot.getIsOptional();
            String defaultValue = slot.getDefaultValue();
            String aliasValue = slot.getAliasValue();

            String paramName = (aliasValue != null && !aliasValue.isEmpty()) ? aliasValue : name;

            if (requestParams.containsKey(paramName)) {
                throw new BadRequestException("Duplicate parameter name: " + paramName);
            }

            if (optional) {
                if (query.containsKey(paramName)) {
                    requestParams.put(name, query.get(paramName));
                } else if (defaultValue != null && !defaultValue.isEmpty()) {
                    requestParams.put(name, defaultValue);
                }
            } else {
                if (query.containsKey(paramName)) {
                    requestParams.put(name, query.get(paramName));
                } else {
                    throw new BadRequestException("Required parameter missing: " + paramName);
                }
            }
        }
        return requestParams;
    }

}
