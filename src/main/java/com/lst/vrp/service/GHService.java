package com.lst.vrp.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.isochrone.algorithm.ContourBuilder;
import com.graphhopper.isochrone.algorithm.JTSTriangulator;
import com.graphhopper.isochrone.algorithm.ShortestPathTree;
import com.graphhopper.isochrone.algorithm.Triangulator;
import com.graphhopper.routing.ev.Subnetwork;
import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.util.DefaultSnapFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.storage.index.Snap;
import com.graphhopper.util.DistanceCalcEarth;
import com.graphhopper.util.shapes.GHPoint;

@Service
public class GHService {


    static final String pdfFile = "pakistan-latest.osm.pbf";

    Triangulator triangulator;

    private GraphHopper graphHopper;

    public GraphHopper getGHopper() {

        if (graphHopper == null) {
            graphHopper = createGraphHopperInstance(pdfFile);
            triangulator = new JTSTriangulator(graphHopper.getRouterConfig());

        }
        return graphHopper;
    }

    private GraphHopper createGraphHopperInstance(String pdfFile) {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(pdfFile).getFile());
        String ghLoc = file.getAbsolutePath();
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(ghLoc);
        // specify where to store graphhopper files
        hopper.setGraphHopperLocation("target/routing-graph-cache");

        // see docs/core/profiles.md to learn more about profiles
        hopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));

        // this enables speed mode for the profile we called car
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));

        // now this can take minutes if it imports or a few seconds for loading of
        // course this is dependent on the area you import
        hopper.importOrLoad();
        return hopper;
    }

    public ResponsePath routing(GHRequest req) {
        // simple configuration of the request object
        // GHRequest req = new GHRequest(42.508552, 1.532936, 42.507508, 1.528773).
        // note that we have to specify which profile we are using even when there is
        // only one like here
        // setProfile("car").
        // define the language for the turn instructions
        // setLocale(Locale.US);

        GHResponse rsp = this.getGHopper().route(req);

        // handle errors
        if (rsp.hasErrors())
            throw new RuntimeException(rsp.getErrors().toString());

        // use the best path, see the GHResponse class for more possibilities.
        ResponsePath path = rsp.getBest();

        // points, distance in meters and time in millis of the full path
        // PointList pointList = path.getPoints();
        // double distance = path.getDistance();
        // long timeInMs = path.getTime();

        // Translation tr =
        // this.graphHopper.getTranslationMap().getWithFallBack(Locale.UK);
        // InstructionList il = path.getInstructions();
        // // iterate over all turn instructions
        // for (Instruction instruction : il) {
        // System.out.println("distance " + instruction.getDistance() + " for
        // instruction: "
        // + instruction.getTurnDescription(tr));
        // }

        return path;
    }

    public List<Geometry> doIsoChrone(double lat, double lon) {

        GHPoint point = new GHPoint(lat, lon);

        GraphHopper hopper = getGHopper();
        // get encoder from GraphHopper instance
        EncodingManager encodingManager = hopper.getEncodingManager();
        FlagEncoder encoder = encodingManager.getEncoder("car");

        // snap some GPS coordinates to the routing graph and build a query graph
        FastestWeighting weighting = new FastestWeighting(encoder);
        Snap snap = hopper.getLocationIndex().findClosest(lat, lon, // 42.508679, 1.532078,
                new DefaultSnapFilter(weighting, encodingManager.getBooleanEncodedValue(Subnetwork.key("car"))));
        QueryGraph queryGraph = QueryGraph.create(hopper.getGraphHopperStorage(), snap);

        // run the isochrone calculation
        ShortestPathTree tree = new ShortestPathTree(queryGraph, weighting, false, TraversalMode.NODE_BASED);

        // find all nodes that are within a radius of 120s
        tree.setTimeLimit(120_000);

        /*
         * AtomicInteger counter = new AtomicInteger(0);
         * // you need to specify a callback to define what should be done
         * tree.search(snap.getClosestNode(), label -> {
         * // see IsoLabel.java for more properties
         * System.out.println("node: " + label.node + ", time: " + label.time +
         * ",distance: " + label.distance);
         * 
         * counter.incrementAndGet();
         * });
         */
        ToDoubleFunction<ShortestPathTree.IsoLabel> fz = l -> l.time;
        // DO QUERY
        Triangulator.Result result = triangulator.triangulate(snap, queryGraph, tree, fz, degreesFromMeters(20));
        // convert result to geometry
        double limit = 120_000;
        int nBuckets = 1;
        ArrayList<Double> zs = new ArrayList<>();
        double delta = limit / nBuckets;
        for (int i = 0; i < nBuckets; i++) {
            zs.add((i + 1) * delta);
        }

        ContourBuilder contourBuilder = new ContourBuilder(result.triangulation);
        ArrayList<Geometry> isochrones = new ArrayList<Geometry>();

        for (Double z : zs) {
            System.out.println("Building contour z=" + z);
            MultiPolygon isochrone = contourBuilder.computeIsoline(z, result.seedEdges);
            // if (fullGeometry) {
            // isochrones.add(isochrone);
            // } else {

            Polygon maxPolygon = heuristicallyFindMainConnectedComponent(isochrone,
                    isochrone.getFactory().createPoint(new Coordinate(point.getLon(),
                            point.getLat())));
            isochrones.add(isochrone.getFactory().createPolygon(((LinearRing) maxPolygon.getExteriorRing())));

            // }
        }

        return isochrones;
    }

    private Polygon heuristicallyFindMainConnectedComponent(MultiPolygon multiPolygon, Point point) {
        int maxPoints = 0;
        Polygon maxPolygon = null;
        for (int j = 0; j < multiPolygon.getNumGeometries(); j++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(j);
            if (polygon.contains(point)) {
                return polygon;
            }
            if (polygon.getNumPoints() > maxPoints) {
                maxPoints = polygon.getNumPoints();
                maxPolygon = polygon;
            }
        }
        return maxPolygon;
    }

    static double degreesFromMeters(double distanceInMeters) {
        return distanceInMeters / DistanceCalcEarth.METERS_PER_DEGREE;
    }

}
