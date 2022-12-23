package com.liviz.v2;

import com.liviz.v2.model.DataSourceSlot;
import com.liviz.v2.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class V2ApplicationTests {

    @Test
    void test() {
        DataSourceSlot dataSourceSlot1 = new DataSourceSlot("a", "b", true, "c", "d");
        DataSourceSlot dataSourceSlot2 = new DataSourceSlot("a", "b", true, "c", "d");
        List<DataSourceSlot> dataSourceSlots = List.of(new DataSourceSlot[]{dataSourceSlot1, dataSourceSlot2});

        List<DataSourceSlot> copiedDataSourceSlots = dataSourceSlots.stream()
                .map(DataSourceSlot::new)
                .collect(Collectors.toList());
        ;
        copiedDataSourceSlots.get(0).setName("bbb");
        System.out.println(dataSourceSlots.get(0).getName());
        System.out.println(copiedDataSourceSlots);
    }

}
