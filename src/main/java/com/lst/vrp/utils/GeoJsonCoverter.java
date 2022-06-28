package com.lst.vrp.utils;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.github.filosganga.geogson.model.Feature;
import com.github.filosganga.geogson.model.FeatureCollection;
import com.github.filosganga.geogson.model.LineString;
import com.github.filosganga.geogson.model.Point;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.JsonFeature;

public class GeoJsonCoverter {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new GeometryAdapterFactory())
            .create();

    private static ObjectMapper mapper = null;

    public static ObjectMapper GetJTSObjectMapper() {

        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.registerModule(new JtsModule());
        }

        return mapper;

    }

    public static Gson getGSON() {
        return gson;
    }

    public static FeatureCollection toGeoJSON(ResponsePath rp) {

        // List<Feature> ptfeatures = new ArrayList<Feature>();

        // LineString line = new LineString(positions)
        List<Point> ptList = new ArrayList<Point>();

        /*
         * for (GHPoint3D pt : rp.getPoints()) {
         * ptList.add(Point.from(pt.lon, pt.lat));
         * }
         */
        rp.getPoints().forEach((t) -> ptList.add(Point.from(t.lon, t.lat)));
        LineString pathLine = LineString.of(ptList);

        FeatureCollection fc = new FeatureCollection(asList(Feature.of(pathLine)));
        // FeatureCollection fc = new
        // FeatureCollection(Collections.<Feature>emptyList());

        // LinearPositions lPositions = new LinearPositions(children)
        // LineString line = new LineString(
        System.out.println(gson.toJson(fc));
        return fc;
    }

    public static String polygonToGeoJSON(List<Geometry> rp) throws JsonProcessingException {

        ArrayList<JsonFeature> features = new ArrayList<>();
        for (Geometry isochrone : rp) {
            JsonFeature feature = new JsonFeature();
            HashMap<String, Object> properties = new HashMap<>();
            // properties.put("bucket", features.size());

            feature.setProperties(properties);
            feature.setGeometry(isochrone);
            // feature.setBBox(new BBox(coords));
            // isochrone.getEnvelope().getCoordinates()
            features.add(feature);
        }
        ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.put("type", "FeatureCollection");
        json.putPOJO("features", features);

        return GetJTSObjectMapper().writeValueAsString(json);
    }

}
