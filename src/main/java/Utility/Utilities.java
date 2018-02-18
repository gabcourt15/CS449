package Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class Utilities {
    public static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String ICON_API_URL = "http://openweathermap.org/img/w/";

    public static JSONObject getObj (String tagName, JSONObject jsonObject) throws JSONException{
        JSONObject jObject = jsonObject.getJSONObject(tagName);
        return jObject;
    }

    public static String getString (String tagName, JSONObject jsonObject) throws JSONException{
        return jsonObject.getString(tagName);
    }

    public static float getFloat (String tagName, JSONObject jsonObject) throws JSONException{
        return (float) jsonObject.getDouble(tagName);
    }

    public static double getDouble (String tagName, JSONObject jsonObject) throws JSONException{
        return (float) jsonObject.getDouble(tagName);
    }

    public static int getInt (String tagName, JSONObject jsonObject) throws JSONException{
        return jsonObject.getInt(tagName);
    }
}
