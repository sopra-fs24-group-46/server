package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.LocationTypes;
import ch.uzh.ifi.hase.soprafs24.game.entity.Question;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

public class APIService {
    // todo add options to select questions
    // for now returning alpine Gipfel in german names.

    public static List<Question> getQuestions(Settings settings) {
        var locationTypes = settings.getLocationTypes();
        var amount = settings.getRounds();

        boolean defaultLocationFilter = false;
        if (locationTypes == null || locationTypes.isEmpty()) {// defaults to alpines
            defaultLocationFilter = true;

            locationTypes = new ArrayList<>();
            locationTypes.add(LocationTypes.ALPINE_MOUNTAIN);
        }

        ResponseData data = new ResponseData();

        for (LocationTypes type : locationTypes) {
            data.addAll(FetchData.readLocalJson(type.getValue()));
        }

        // filtering the language check the function definition for more details
        data.filterByAttributes("sprachcode", "Hochdeutsch");

        if (defaultLocationFilter) {
            // roughly area around Matterhorn
            // https://s.geo.admin.ch/qskqmg54fqm6
            data.filterByPolygon(new double[][] {
                    { 7.0, 45.0 },
                    { 8.0, 45.0 },
                    { 8.0, 46.0 },
                    { 7.0, 46.0 }
            });
        }

        data.removeDuplicates(); // for now removing duplicates to get rid of ambigous questions

        var jsonNodes = data.selectRandomElements(amount);

        // converts json nodes to questions
        return jsonNodes.stream()
                .map(APIService::convertJsonToQuestion)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private static Question convertJsonToQuestion(JsonNode json) {
        Double x = json.get("geometry").get("points").get(0).get(0).asDouble();
        Double y = json.get("geometry").get("points").get(0).get(1).asDouble();

        String name = json.get("attributes").get("name").asText();

        return new Question(name, new GeoLocation(x, y));
    }
}