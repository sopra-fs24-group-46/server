package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class FetchDataTest {
    
    // @BeforeEach
    // public void setup() {
        
    //     when(FetchData.callGeoAdminAPI(Mockito.any(), Mockito.any())).thenReturn("{\"results\":[]}");
    // }
    @Mock
    private static FetchData fetchDataMock;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReadLocalJson() throws IOException {
        String file_name = "alpiner gipfel";
        var jsonContent = new String(Files.readAllBytes(
                Paths.get(
                    "src/main/resources/GeoAdminAPI/" + file_name + ".json")));
        ResponseData response = FetchData.readLocalJson(file_name);
        assertEquals(new ResponseData((ArrayNode) parseJson(jsonContent).get("results")).size(), response.size());
    }

    //don't know how to mock static method
    //don't want to call the API for tests

    // @Test
    // public void testCallGeoAdmin2() {
    //     String method = "find";
    //     HashMap<String, String> params = new HashMap<>();
    //     params.put("layer", "ch.swisstopo.swissboundaries3d-kanton-flaeche.fill");
    //     params.put("searchText", "Bern");
    //     params.put("searchField", "name");
    //     params.put("sr", "4326");
    //     Mockito.when(FetchData.callGeoAdminAPI(Mockito.any(), params)).thenReturn("{\"results\":[]}");
        
    //     // Call the method under test
    //     ResponseData response = fetchDataMock.callGeoAdmin(method, params);
        
    // }
    // @Test
    // public void testFetchRegionBoundaries() {
    //     final String region = "Bern";
    //     RegionType type = RegionType.CANTON;

    //     String jsonContent = "{\"results\": [{\"geometry\":{\"rings\": [[[8.0,47.0],[8.0,48.0],[9.0,48.0],[9.0,47.0],[8.0,47.0]]]}]}";
    //     when(FetchData.callGeoAdminAPI(Mockito.any(), Mockito.any())).thenReturn(jsonContent);

    //     double[][] boundaries = FetchData.fetchRegionBoundaries(region, type);
    //     double[][] expectedBoundaries = new double[][] {
    //         {8.0,47.0},
    //         {8.0,48.0},
    //         {9.0,48.0},
    //         {9.0,47.0},
    //         {8.0,47.0}
    //     };

    //     assertArrayEquals(expectedBoundaries, boundaries);

    // }

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

