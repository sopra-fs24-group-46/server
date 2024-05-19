package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

public class APIServiceTest {

    @Test
    public void testNoDuplicates() {
        var settings = Settings.defaultSettings();
        settings.setLocationTypes(List.of(LocationTypes.ALPINE_MOUNTAIN, LocationTypes.MOUNTAIN, LocationTypes.HILL,
                LocationTypes.MAIN_HILL, LocationTypes.LAKE));
        var questions = APIService.loadResponseData(settings).getJsonNodes();
        var names = questions.stream().map((node) -> {
            return node.get("attributes").get("name").asText();
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        assert (names.size() > 100);
        // find duplicates in names
        var duplicates = names.stream().filter(name -> Collections.frequency(names, name) > 1).distinct()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        assertEquals(0, duplicates.size());
    }

    @Test
    public void hasContentAlpine() {
        var settings = Settings.defaultSettings();
        settings.setLocationTypes(List.of(LocationTypes.ALPINE_MOUNTAIN));
        var questions = APIService.loadResponseData(settings).getJsonNodes();
        var names = questions.stream().map((node) -> {
            return node.get("attributes").get("name").asText();
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        assert (names.size() > 0);
        // find duplicates in names
        var duplicates = names.stream().filter(name -> Collections.frequency(names, name) > 1).distinct()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        assertEquals(0, duplicates.size());
    }

    @Test
    public void testNoRingGeometry() {
        var settings = Settings.defaultSettings();
        settings.setLocationTypes(List.of(LocationTypes.LAKE));
        var jsonNodes = APIService.loadResponseData(settings).getJsonNodes();
        List<JsonNode> points = jsonNodes.stream().filter((node) -> {
            return node.get("geometry").has("points");
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        var rings = jsonNodes.stream().filter((node) -> {
            return node.get("geometry").has("rings");
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        assert (points.size() > 100);
        assertEquals(points.get(0).get("geometry").get("points").get(0).size(), 2);
        assertEquals(0, rings.size());
    }
    
    @Test
    public void loadLakes() {
        var settings = Settings.defaultSettings();
        settings.setRounds(10);
        settings.setLocationTypes(List.of(LocationTypes.LAKE));
        var questions = APIService.getQuestions(settings);
        assertEquals(10, questions.size());
    }
    
    @Test
    public void correctEncodingLakes() {
        var settings = Settings.defaultSettings();
        settings.setRounds(1);
        settings.setLocationTypes(List.of(LocationTypes.LAKE));
        settings.setLocationNames(List.of("Heurütiweiher"));
        var questions = APIService.getQuestions(settings);
        assertEquals(1, questions.size());
    }

    @Test
    public void correctEncodingMountainsEscaped() {
        var settings = Settings.defaultSettings();
        settings.setRounds(1);
        settings.setLocationTypes(List.of(LocationTypes.ALPINE_MOUNTAIN));
        settings.setLocationNames(List.of("Glarner T\u00F6di"));
        var questions = APIService.getQuestions(settings);
        assertEquals(1, questions.size());
    }
    @Test
    public void correctEncodingMountains() {
        var settings = Settings.defaultSettings();
        settings.setRounds(1);
        settings.setLocationTypes(List.of(LocationTypes.ALPINE_MOUNTAIN));
        settings.setLocationNames(List.of("Glarner Tödi"));
        var questions = APIService.getQuestions(settings);
        assertEquals(1, questions.size());
    }
}
