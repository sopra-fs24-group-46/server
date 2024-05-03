package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

public class APIServiceTest {

    APIService apiService = new APIService();

    @Test
    public void testNoDuplicates() {
        var settings = new Settings();
        settings.setLocationTypes(List.of(LocationTypes.ALPINE_MOUNTAIN, LocationTypes.MOUNTAIN, LocationTypes.HILL,
                LocationTypes.MAIN_HILL));
        var questions = apiService.loadResponseData(settings).getJsonNodes();
        var names = questions.stream().map((node) -> {
            return node.get("attributes").get("name").asText();
        }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        assert (names.size() > 100);
        // find duplicates in names
        var duplicates = names.stream().filter(name -> Collections.frequency(names, name) > 1).distinct()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        assertEquals(0, duplicates.size());
    }

}
