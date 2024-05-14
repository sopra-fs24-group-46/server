package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

//we can work like in Rust e.g. functional style with
//filteredList = list1.strem().filter(obj -> obj.get("name").asText().contains("Bern")).collect()
//list1.addAll(list2)
//

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ResponseData {
    // todo handle rings location data

    public List<JsonNode> data = new ArrayList<JsonNode>();

    public ResponseData(List<JsonNode> data) {
        this.data = data;
    }

    public ResponseData(ArrayNode json) {
        this.data = arrayNodeToList(json);
    }

    public ResponseData() {
        this.data = new ArrayList<JsonNode>();
    }

    public ResponseData addAll(ResponseData other) {// is this legal code?
        this.data.addAll(other.data); // calling the data of other object
        return this;
    }

    // Example attributes
    // "attributes": {
    // "objektart": "Alpiner Gipfel",
    // "objektklasse": "TLM_NAME_PKT",
    // "name": "Punta Rosa",
    // "sprachcode": "Italienisch inkl. Lokalsprachen",
    // "namen_typ": "Endonym",
    // "status": "ueblich",
    // "label": "Punta Rosa"}
    // }
    public void filterByAttributes(String field, String value) {
        data = data.stream().filter(obj -> obj.get("attributes").get(field).asText().contains(value))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void filterByPolygon(double[][] polygon) {
        data = data.stream().filter(
                obj -> {
                    if (obj.get("geometry").get("points") == null) {// filtering for polygons
                        return false;
                    }
                    return isPointInsidePolygon(
                            obj.get("geometry").get("points").get(0).get(0).asDouble(),
                            obj.get("geometry").get("points").get(0).get(1).asDouble(),
                            polygon);
                }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void filterByNames(List<String> names) {
        if (names == null || names.size() == 0) {
            throw new IllegalArgumentException("names should contain at least one name: " + names);
        }
        data = data.stream().filter(obj -> names.contains(obj.get("attributes").get("name").asText()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void filterByRegionName(String region, RegionType type) {
        if (region == null || type == null) {
            throw new IllegalArgumentException("region and type must not be null: " + region + " " + type);
        }
        filterByPolygon(FetchData.fetchRegionBoundaries(region, type));
    }

    public void removeDuplicates() {
        // reducing to unique featureIds
        List<Integer> featureIds = new ArrayList<>();
        List<JsonNode> uniqueFeatureIdData = new ArrayList<>();
        for (JsonNode node : this.data) {
            int featureId = node.get("featureId").asInt();
            if (!featureIds.contains(featureId)) {
                featureIds.add(featureId);
                uniqueFeatureIdData.add(node);
            }
        }

        // removing duplicate names
        var names = uniqueFeatureIdData.stream().map(obj -> obj.get("attributes").get("name").asText()).collect(
                ArrayList::new,
                ArrayList::add, ArrayList::addAll);
        this.data = uniqueFeatureIdData.stream()
                .filter(obj -> Collections.frequency(names, obj.get("attributes").get("name").asText()) == 1)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    }

    public List<JsonNode> selectRandomElements(int numElements) {
        List<JsonNode> randomElements = new ArrayList<>();
        Random random = new Random();
        Set<Integer> selectedIndices = new HashSet<>();

        while (selectedIndices.size() < numElements && selectedIndices.size() < data.size()) {
            int randomIndex = random.nextInt(data.size());
            selectedIndices.add(randomIndex);
        }

        for (Integer index : selectedIndices) {
            randomElements.add(data.get(index));
        }

        return randomElements;
    }

    // this is a raytracing algorithm to check if a point is inside the polygon
    // Copied from https://codeium.io (Copilote Alternative)
    // detailed explanation here:
    // https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon
    // I have checked the alogrithem and it should work. - Serafin
    private boolean isPointInsidePolygon(double x, double y, double[][] polygon) {
        int crossings = 0;
        for (int i = 0; i < polygon.length; i++) {
            double[] p1 = polygon[i];
            double[] p2 = polygon[(i + 1) % polygon.length];
            if (p1[1] <= y && p2[1] > y || p1[1] > y && p2[1] <= y) {
                double vt = (y - p1[1]) / (p2[1] - p1[1]);
                if (x < p1[0] + vt * (p2[0] - p1[0])) {
                    crossings++;
                }
            }
        }
        return crossings % 2 == 1;
    }

    public void reduceRingGeometry() {
        data = data.stream().map(ResponseData::reduceRingGeometryToPoint).collect(ArrayList::new, ArrayList::add,
                ArrayList::addAll);
    }

    public List<JsonNode> getJsonNodes() {
        return data;
    }

    private static List<JsonNode> arrayNodeToList(ArrayNode json) {
        Stream<JsonNode> stream = StreamSupport.stream(json.spliterator(), false);
        List<JsonNode> list = stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return list;
    }

    private static JsonNode reduceRingGeometryToPoint(JsonNode json) {
        if (json.get("geometry").has("points")) {
            return json;
        }

        ArrayNode ringArrayNode = (ArrayNode) json.get("geometry").get("rings");
        List<JsonNode> ring = arrayNodeToList(ringArrayNode);
        // find the middle of the ring
        var mx = ring.stream().mapToDouble(node -> node.get(0).asDouble()).average().getAsDouble();
        var my = ring.stream().mapToDouble(node -> node.get(1).asDouble()).average().getAsDouble();

        // add a field to the json node
        ObjectNode mutable = (ObjectNode) json.get("geometry");

        ArrayNode points = JsonNodeFactory.instance.arrayNode();
        points.add(mx);
        points.add(my);
        mutable.set("points", points);
        mutable.remove("rings");

        return json;
    }

    public static void main(String[] args) {
        ResponseData data = FetchData.readLocalJson("gipfel");
        var data2 = FetchData.readLocalJson("huegel");
        data.addAll(data2);
        double[][] polygon = new double[][] {
                { 7.0, 45.0 },
                { 8.0, 45.0 },
                { 8.0, 46.0 },
                { 7.0, 46.0 }
        };
        data.data.get(0).get("geometry").get("points").get(0).get(0).asDouble();

        // data.filterByPolygon(polygon);
        data.filterByAttributes("sprachcode", "Hochdeutsch");
        var randomElements = data.selectRandomElements(10);

        // this is how the json is accessed

        System.out.println(randomElements.get(0).get("attributes").get("name").asText());
    }
}