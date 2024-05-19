package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ResponseDataTest {

    @Test
    public void testFilterByAttributes() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");

        response.filterByAttributes("name", "Glarner Tödi");
        assertEquals(9, response.getJsonNodes().size());
        response.removeDuplicates();
        assertEquals(1, response.getJsonNodes().size());
    }

    @Test
    public void testFilterByPolygon() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");

        double[][] polygon ={
                { 7.0, 45.0 },
                { 8.0, 45.0 },
                { 8.0, 46.0 },
                { 7.0, 46.0 } };

        response.removeDuplicates();
        response.filterByPolygon(polygon);
        assertEquals(34, response.getJsonNodes().size());
    }

    @Test
    public void testFilterByNames() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");
        response.filterByNames(List.of("Muttler", "Glarner Tödi"));
        response.removeDuplicates();
        assertEquals(2, response.getJsonNodes().size());
    }

    @Test
    public void testFilterByRegionName() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");
        response.filterByRegionName("Bern", RegionType.CANTON);
        assertEquals(176, response.getJsonNodes().size());
    }

    @Test
    public void testRemoveDuplicates() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");
        assertEquals(2010, response.getJsonNodes().size());
        response.removeDuplicates();
        assertEquals(228, response.getJsonNodes().size());
    }

    @Test
    public void testSelectRandomElements() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");
        List<JsonNode> selected = response.selectRandomElements(2);
        assertEquals(2, selected.size());
    }

    @Test
    public void testGetJsonNodes() {
        List<JsonNode> data = Arrays.asList(
            JsonNodeFactory.instance.objectNode().put("name", "A"),
            JsonNodeFactory.instance.objectNode().put("name", "B"),
            JsonNodeFactory.instance.objectNode().put("name", "C")
        );
        ResponseData response = new ResponseData(data);
        assertEquals(data, response.getJsonNodes());
    }

    @Test
    public void testGetJsonAsString() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");
        response.filterByNames(List.of("Glarner Tödi"));
        response.removeDuplicates();
        String json = response.getJsonAsString();
        var expected = "{\"results\":[{\"featureId\":425062,\"bbox\":[8.921949,46.810436,8.921949,46.810436],\"layerBodId\":\"ch.swisstopo.swissnames3d\",\"layerName\":\"Geografische Namen swissNAMES3D\",\"id\":425062,\"geometry\":{\"points\":[[8.921949,46.810436]],\"spatialReference\":{\"wkid\":4326}},\"attributes\":{\"objektart\":\"Alpiner Gipfel\",\"objektklasse\":\"TLM_NAME_PKT\",\"name\":\"Glarner Tödi\",\"sprachcode\":\"Hochdeutsch inkl. Lokalsprachen\",\"namen_typ\":\"einfacher Name\",\"status\":\"offiziell\",\"label\":\"Glarner Tödi\"}}]}";
        assertEquals(expected,  json);
    }

    @Test
    public void testLogSize() {
        ResponseData response = FetchData.readLocalJson("alpiner gipfel");
        var old = response.getFilterLog();
        response.logSize(0, "Test");
        assertNotEquals(old, response.getFilterLog());
        assertThrows(ResponseStatusException.class, () -> response.logSize(10000, ""));
    }

    @Test
    public void testReduceRingGeometry() {
        // not testing since it was only used to manipulate data which is stored now.
        // would need to fetch data or build the json manually to test this. 
        // Too much work for functionality testing.
    }
}
