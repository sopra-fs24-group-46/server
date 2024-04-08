package ch.uzh.ifi.hase.soprafs24.geo_admin_api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import ch.uzh.ifi.hase.soprafs24.game.entity.GeoLocation;
import ch.uzh.ifi.hase.soprafs24.game.entity.Question;

public class APIService {
    // todo add options to select questions
    // for now returning alpine Gipfel in german names.

    public static List<Question> getQuestions(int amount) {
        ResponseData data = FetchData.readLocalJson("alpiner gipfel.json");

        // filtering the language check the function definition for more details
        data.filterByAttributes("sprachcode", "Hochdeutsch");

        // roughly area around Matterhorn
        // https://s.geo.admin.ch/qskqmg54fqm6
        data.filterByPolygon(new double[][] {
                { 588530, 129173 },
                { 589939, 83020 },
                { 636525, 83887 },
                { 645626, 128956 }
        });

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