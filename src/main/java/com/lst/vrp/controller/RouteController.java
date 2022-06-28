package com.lst.vrp.controller;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.filosganga.geogson.model.FeatureCollection;
import com.graphhopper.GHRequest;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.shapes.GHPoint;
import com.lst.vrp.service.GHService;
import com.lst.vrp.utils.GeoJsonCoverter;

@RestController
@CrossOrigin
@RequestMapping("/routing")
public class RouteController {

    @Autowired
    GHService service;

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.OK)
    public String routing(@RequestBody GHRequest request) {

        ResponsePath path = service.routing(request);
        FeatureCollection fc = GeoJsonCoverter.toGeoJSON(path);

        System.out.println(GeoJsonCoverter.getGSON().toJson(fc));
        return GeoJsonCoverter.getGSON().toJson(fc);
    }

    /**
     * curl -X POST "http://localhost:8081/routing/generate/" -H "Content-Type:
     * application/json" -d
     * "{\"points\":[{\"lat\":42.508552,\"lon\":1.532936},{\"lat\":42.507508,\"lon\":1.528773}],\"profile\":\"car\",\"hints\":{\"map\":{}},\"headings\":[],\"pointHints\":[],\"curbsides\":[],\"snapPreventions\":[],\"pathDetails\":[],\"algo\":\"\",\"locale\":\"en_US\"}"
     * 
     * @throws JsonProcessingException
     */

    @PostMapping("/isochrone")
    @ResponseStatus(HttpStatus.OK)
    public String isochrone(@RequestBody GHRequest request) throws JsonProcessingException {

        GHPoint point = request.getPoints().get(0);
        List<Geometry> isochrones = service.doIsoChrone(point.lat, point.lon);

        return GeoJsonCoverter.polygonToGeoJSON(isochrones);

    }

    /*
     * curl -X POST "http://localhost:8081/routing/isochrone/" -H
     * "Content-Type: application/json" -d
     * "{\"points\":[{\"lat\":42.508552,\"lon\":1.532936},{\"lat\":42.507508,\"lon\":1.528773}],\"profile\":\"car\",\"hints\":{\"map\":{}},\"headings\":[],\"pointHints\":[],\"curbsides\":[],\"snapPreventions\":[],\"pathDetails\":[],\"algo\":\"\",\"locale\":\"en_US\"}
     */

}
