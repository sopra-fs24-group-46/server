package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;

import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.game.entity.Question;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

public class APIService {
    // todo add options to select questions
    // for now returning alpine Gipfel in german names.

    public static List<Question> getQuestions(Settings settings) {
        var amount = settings.getRounds();
        // converts json nodes to questions
        var data = loadResponseData(settings);

        var jsonNodes = data.selectRandomElements(amount);

        return jsonNodes.stream()
                .map(APIService::convertJsonToQuestion)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static ResponseData loadResponseData(Settings settings) {
        var locationTypes = settings.getLocationTypes();
        var roundNumber = settings.getRounds();

        if (locationTypes == null || locationTypes.isEmpty()) {// defaults to alpines
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select at least one location type.");
        }

        ResponseData data = new ResponseData();

        for (LocationTypes type : locationTypes) {
            data.addAll(FetchData.readLocalJson(type.getValue()));
        }
        data.logSize(roundNumber, "loading: " + locationTypes);

        // filtering the language check the function definition for more details

        if (settings.getRegion() != null && settings.getRegionType() != null) {
            data.filterByRegionName(settings.getRegion(), settings.getRegionType());
            data.logSize(roundNumber, "filtering region " + settings.getRegionType() + " " + settings.getRegion());
        }

        if (settings.getLocationNames() != null && !settings.getLocationNames().isEmpty()) {
            data.filterByNames(settings.getLocationNames());
            data.logSize(roundNumber, "filtering names " + settings.getLocationNames());
        }

        if (settings.getRegionAsPolygon() != null && settings.getRegionAsPolygon().length > 2
                && settings.getRegionAsPolygon()[0].length == 2) {// only filter for region if region is provided
            data.filterByPolygon(settings.getRegionAsPolygon());
            data.logSize(roundNumber, "filtering by polygon " + settings.getRegionAsPolygon());
        }
        // Example
        // data.filterByPolygon(new double[][] {
        //         { 7.0, 45.0 },
        //         { 8.0, 45.0 },
        //         { 8.0, 46.0 },
        //         { 7.0, 46.0 }
        // });

        data.filterByAttributes("sprachcode", "Hochdeutsch");
        data.logSize(roundNumber, "filtering language: Hochdeutsch");

        data.removeDuplicates(); // for now removing duplicates to get rid of ambiguous questions
        data.logSize(roundNumber, "removing duplicates");

        return data;
    }

    public static void filterByRegion(ResponseData data, String region, RegionType type) {
        if (region != null && type != null) {
            data.filterByPolygon(FetchData.fetchRegionBoundaries(region, type));
        }
    }

    public static Question convertJsonToQuestion(JsonNode json) {
        Double x = json.get("geometry").get("points").get(0).get(0).asDouble();
        Double y = json.get("geometry").get("points").get(0).get(1).asDouble();

        String name = json.get("attributes").get("name").asText();

        return new Question(name, new GeoLocation(x, y));
    }
}