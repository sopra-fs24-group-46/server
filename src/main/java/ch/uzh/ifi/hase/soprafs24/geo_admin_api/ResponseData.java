package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

//we can work like in Rust e.g. functional style with
//filteredList = list1.strem().filter(obj -> obj.get("name").asText().contains("Bern")).collect()
//list1.addAll(list2)
//

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ResponseData {

    private List<JsonNode> data = new ArrayList<JsonNode>();

    public ResponseData(List<JsonNode> data) {
        this.data = data;
    }

    public ResponseData(ArrayNode json) {
        this.data = arrayNodeToList(json);
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
    public List<JsonNode> filterByAttributes(String field, String value) {
        return data.stream().filter(obj -> obj.get("attributes").get(field).asText().contains(value))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<JsonNode> filterByPolygon(List<JsonNode> json, double[][] polygon) {
        return json.stream().filter(obj -> isPointInsidePolygon(
                obj.get("poitns").get(0).get(0).asDouble(),
                obj.get("poitns").get(0).get(1).asDouble(),
                polygon))
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

    private static List<JsonNode> arrayNodeToList(ArrayNode json) {
        Stream<JsonNode> stream = StreamSupport.stream(json.spliterator(), false);
        List<JsonNode> list = stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return list;
    }

    public static void main(String[] args) {
        ResponseData data = FetchData.readLocalJson("gipfel.json");
        var data2 = FetchData.readLocalJson("huegel.json");
        data.addAll(data2);
        data.filterByAttributes("sprachcode", "Hochdeutsch");
        double[][] polygon = new double[][] {
                { 8.63201156032141, 47.55722864491296 },
                { 8.641542724980663, 47.47757020920054 },
                { 8.819943076188808, 47.46548121783862 },
                { 8.877324542319437, 47.51708447356059 },
                { 8.823688349653274, 47.563577608388435 },
                { 8.63201156032141, 47.55722864491296 }
        };
        // data.filterByPolygon(data.data, polygon); //doesn't work todo: fix
        var randomElements = data.selectRandomElements(10);

        // this is how the json is accessed

        System.out.println(randomElements.get(0).get("attributes").get("name").asText());
    }
}