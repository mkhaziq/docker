package com.lst.vrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.github.filosganga.geogson.model.FeatureCollection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lst.vrp.utils.GeoJsonCoverter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JsonTest {

    final String warehouseJson = "warehouses.geojson";
    final String customersJson = "customers.geojson";

    @Test
    void loadTest() throws IOException {
        // Gson gson = new GsonBuilder()
        // .registerTypeAdapterFactory(new GeometryAdapterFactory())
        // .create();

        assertTrue(true);
        System.out.println("loadTest");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(warehouseJson).getFile());
        // String absolutePath = file.getAbsolutePath();
        // Path filePath = Path.of(absolutePath);
        String geoJsonContent = Files.readString(file.toPath());

        FeatureCollection fc = GeoJsonCoverter.getGSON().fromJson(geoJsonContent, FeatureCollection.class);
        fc.toString();
        assertNotNull(fc);
        String json = GeoJsonCoverter.getGSON().toJson(fc);
        System.out.println(json);

    }

}
