package in.wptrafficanalyzer.euecologico2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JObjectParser {

    public static CategoryPositions[] CategoryPosition(JSONObject jObj, int length) throws JSONException {
        // Initializes the Array.
        CategoryPositions[] categoryPosition = new CategoryPositions[length];

        if (jObj.getBoolean("success")) {
            for (int i = 0; i < length; i++) {
                categoryPosition[i] = new CategoryPositions();
            }
            // Populates the Array.
            JSONArray jArray = jObj.getJSONArray("category_position");
            for (int i = 0; i < jArray.length(); i++) {
                categoryPosition[jArray.getJSONObject(i).getInt("category")].add(new Position(jArray.getJSONObject(i).getString("lat"), jArray.getJSONObject(i).getString("lng")));
            }
            return categoryPosition;
        }
        else {
            for (int i = 0; i < length; i++) {
                categoryPosition[i] = null;
            }
        }

        return categoryPosition;
    }
}
