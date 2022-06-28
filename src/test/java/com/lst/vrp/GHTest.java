package com.lst.vrp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.filosganga.geogson.model.FeatureCollection;
import com.graphhopper.GHRequest;
import com.graphhopper.ResponsePath;
import com.lst.vrp.service.GHService;
import com.lst.vrp.utils.GeoJsonCoverter;

@SpringBootTest
class GHTest {

    @Autowired
    GHService ghService;

    @Test
    void serviceTest() {
        assertNotNull(ghService);
        assertNotNull(ghService.getGHopper());

        GHRequest req = new GHRequest(33.8938, 73.4944, 33.4927, 72.6361).
        // note that we have to specify which profile we are using even when there is
        // only one like here
                setProfile("car").
                // define the language for the turn instructions
                setLocale(Locale.US);
        ResponsePath path = ghService.routing(req);
        assertNotNull(path);
        FeatureCollection fc = GeoJsonCoverter.toGeoJSON(path);
        assertNotNull(fc);
        System.out.println(GeoJsonCoverter.getGSON().toJson(req));
    }

    @Test
    void isoChroneTest() throws JsonProcessingException {
        assertNotNull(ghService);
        assertNotNull(ghService.getGHopper());

        List<Geometry> isochrone = ghService.doIsoChrone(42.508679, 1.532078);// lat lon
        assertNotNull(isochrone);

        String isoJson = GeoJsonCoverter.polygonToGeoJSON(isochrone);
        assertNotNull(isoJson);
        System.out.println(isoJson);

    }

}
