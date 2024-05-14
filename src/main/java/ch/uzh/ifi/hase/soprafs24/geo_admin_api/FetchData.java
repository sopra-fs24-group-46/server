//This calls the GeoAdmin API and parses the String into Jackson JSON
//FetchData::callGeoAdmin(method, params)
//interface:
//static ResponseData callGeoAdmin(String method, HashMap<String, String> params)
//static ResponseData readLocalJson(String file_name) //files are located in recources
//static main(String[] args) //this calls GeoAdmin and stores string into a json file

package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class FetchData {

    public static void main(String[] args) {// this calls GeoAdmin and stores string into a json file
        // these are helper code to write json files.
        // Stored under src main resources
        String searchText = ""; // e.g see

        searchText = "See";
        // searchText = "Alpiner Gipfel";
        // searchText = "Gipfel";
        // searchText = "Haupthuegel";
        // searchText = "Huegel";

        HashMap<String, String> params = new HashMap<>();
        params.put("layer", "ch.swisstopo.swissnames3d");
        params.put("searchText", searchText);
        params.put("searchField", "objektart");
        params.put("contains", "false");
        params.put("sr", "4326");
        String json = callGeoAdminAPI("find", params);
        ResponseData data = new ResponseData((ArrayNode) parseJson(json).get("results"));
        data.reduceRingGeometry();
        json = data.getJsonAsString();
        try (FileWriter fileWriter = new FileWriter(
                "src/main/resources/GeoAdminAPI/" + searchText + ".json")) { // use relative path
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //---------------------------------------------------------------------------
        // double[][] ring = fetchRegionBoundaries("Winterthur", RegionType.DISTRICT);
        // System.out.println(ring[0][0]);
        
        //---------------------------------------------------------------------------
        // HashMap<String, String> params = new HashMap<>();
        
        // // String region = "kanton";
        // // String searchField = "name";

        // String region = "gemeinde";
        // String searchField = "gemname";
        // String offset = "6";
        // params.put("offset", offset);

        // // String region = "bezirk";
        // // String searchField = "name";

        // String layerId = "ch.swisstopo.swissboundaries3d-"+region+"-flaeche.fill";


        // params.put("layer", layerId);
        // params.put("searchText", "");
        // params.put("searchField", searchField);
        // params.put("sr", "4326");
        
        // String json = callGeoAdminAPI("find", params);
        // ArrayNode results = (ArrayNode) parseJson(json).get("results");
        // Stream<JsonNode> stream = StreamSupport.stream(results.spliterator(), false);
        // var names = stream.map(x -> "'"+x.get("attributes").get(searchField).asText() +" ("+ x.get("attributes").get("kanton").asText()+")'\n").toList();
        
        // try (FileWriter fileWriter = new FileWriter(
        //         "src/main/resources/GeoAdminAPI/" + region + offset +".json")) { // use relative path
        //     fileWriter.write(names.toString());
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    public static ResponseData readLocalJson(String file_name) {
        String jsonContent = "";
        try {
            // Read JSON file content as a String
            jsonContent = new String(Files.readAllBytes(
                    Paths.get(
                            "src/main/resources/GeoAdminAPI/" + file_name + ".json")));
            // Use the JSON content String as needed
            // System.out.println("JSON content read from file as a String: " +
            // jsonContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseData((ArrayNode) parseJson(jsonContent).get("results"));
    }

    public static ResponseData callGeoAdmin(String method, HashMap<String, String> params) {
        String jsonContent = callGeoAdminAPI(method, params);
        return new ResponseData((ArrayNode) parseJson(jsonContent).get("results"));
    }
    
    public static double[][] fetchRegionBoundaries(String region, RegionType type) {
        HashMap<String, String> params = new HashMap<>();
        String layerId = "";
        String searchField = "name";

        switch (type) {
            case CANTON:
                layerId = "ch.swisstopo.swissboundaries3d-kanton-flaeche.fill";
                break;
            case MUNICIPALITY:
                layerId = "ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill";
                searchField = "gemname";
                break;
            case DISTRICT:
                layerId = "ch.swisstopo.swissboundaries3d-bezirk-flaeche.fill";
                break;
            default:
                break;
        }
        params.put("layer", layerId);
        params.put("searchText", region);
        params.put("searchField", searchField);
        params.put("sr", "4326");
        
        String json = callGeoAdminAPI("find", params);
        ArrayNode results = (ArrayNode) parseJson(json).get("results");
        if (results.size() > 0) {
            JsonNode geometry = results.get(0).get("geometry");
            ArrayNode rings = (ArrayNode) geometry.get("rings").get(0);
            Stream<JsonNode> stream = StreamSupport.stream(rings.spliterator(), false);
            double[][] boundaries = stream.map(node -> {
                return new double[] { node.get(0).asDouble(), node.get(1).asDouble() };
            }).toArray(double[][]::new);
            if (boundaries.length < 10) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found: " + region);
            }
            return boundaries;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found: " + region);
        }
    }

    // function to convert hasmap into a paramter string for the GeoAdmin API
    private static String paramsToApiString(HashMap<String, String> params) {

        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        // this does url encode the string
        return "?" + sb.toString().replace(" ", "%20");
    }

    // this calls GeoAdmin API and retunrs JSON as a string
    private static String callGeoAdminAPI(String method, HashMap<String, String> params) {
        String apiUrl = "https://api3.geo.admin.ch/rest/services/api/MapServer/"; // Replace with the actual API URL

        String APICall = apiUrl + method + paramsToApiString(params);
        // e.g. url/ find
        // ?layer=ch.swisstopo.swissboundaries3d-kanton-flaeche.fill&searchText=Bern&searchField=name

        System.out.println("API Call: " + APICall);
        try {
            // Create a URL object with the API endpoint
            URL url = new URL(APICall);

            // Open a connection to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method (GET, POST, etc.)
            connection.setRequestMethod("GET");

            // Get the response from the API
            int responseCode = connection.getResponseCode();

            // Check if the response is successful
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Process the response
                String apiResponse = response.toString();
                // System.out.println("API Response: " + apiResponse);
                connection.disconnect();
                return apiResponse;
            } else {
                // System.out.println("API request failed with response code: " + responseCode);
                connection.disconnect();
                throw new RuntimeException("API request failed with response code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("API request failed: " + e.getMessage());
        }
    }

    private static JsonNode parseJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage());
        }
    }
}
