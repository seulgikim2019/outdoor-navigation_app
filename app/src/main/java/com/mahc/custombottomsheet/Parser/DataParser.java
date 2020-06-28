package com.mahc.custombottomsheet.Parser;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataParser {
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        //거리 가지고 오기
        JSONObject distance;
        //이동소요시간 가지고 오기
        JSONObject duration;
        JSONArray jLegs;
        JSONArray jSteps;
        JSONArray jStepDetails;
        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {

                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {

                    //전체 이동 시간, 전체 이동 거리
                    distance=((JSONObject) jLegs.get(0)).getJSONObject("distance");
                    String all_distance_result=distance.getString("text");
                    duration=((JSONObject) jLegs.get(0)).getJSONObject("duration");
                    String all_duration_result=duration.getString("text");
                    Log.i("all_duration",all_duration_result);


                    jSteps = ((JSONObject) jLegs.get(j)).optJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        path = new ArrayList<>();
                        if(((JSONObject) jSteps.get(k)).optJSONArray("steps")!=null){   //step 안에 또다른 step이 있을 때
                            jStepDetails=((JSONObject) jSteps.get(k)).getJSONArray("steps");
                            Log.i("stepDetail",jStepDetails.length()+"");
                            Log.i("step",jSteps.length()+"");



                        }else{ //step 안에 또 다른 step이 없을 때
                            Log.i("stepDetail","no");

                            Log.i("step",jSteps.length()+"");


                            String distance_result=(String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("distance")).get("text");

                            String duration_result=(String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("duration")).get("text");


                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            Log.i("polyline"+k,polyline+"//");
                            //step이 어떻게 되어 있는지 본다.
                            Log.i("step"+k,jSteps.getString(k)+"//");
                            String travel_mode=String.valueOf((((JSONObject) jSteps.get(k)).get("travel_mode")));
                            Log.i("travel_mode"+k, String.valueOf((((JSONObject) jSteps.get(k)).get("travel_mode"))));
                            Log.i("html_instructions"+k, String.valueOf((((JSONObject) jSteps.get(k)).get("html_instructions"))));

                            String html_instructions=String.valueOf((((JSONObject) jSteps.get(k)).get("html_instructions")));

                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                // Log.i("how many points",l+"");

                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude));
                                hm.put("lng", Double.toString((list.get(l)).longitude));
                                hm.put("travel_mode", travel_mode);
                                hm.put("html_instructions", html_instructions);
                                hm.put("distance",distance_result);
                                hm.put("duration",duration_result);
                                hm.put("all_distance",all_distance_result);
                                hm.put("all_duration",all_duration_result);
                                path.add(hm);
                                if(l==list.size()-1){
                                    Log.i("polyline"+l,routes.size()+"");

                                }
                            }  routes.add(path);
                        }


//                        if(String.valueOf((((JSONObject) jSteps.get(k)).get("travel_mode"))).equals("TRANSIT")){
////                            Log.i("travel_mode_TRANSIT"+k,
////                                    String.valueOf((((JSONObject) jSteps.get(k)).get("transit_details")).get("line")));
//
//                            Log.i("travel_mode_TRANSIT"+k,
//                                    String.valueOf(((JSONObject)((JSONObject)((JSONObject)((JSONObject) jSteps.get(k)).get("transit_details")).get("line")).get("vehicle")).get("type")));
//                            travel_mode=String.valueOf(((JSONObject)((JSONObject)((JSONObject)((JSONObject) jSteps.get(k)).get("transit_details")).get("line")).get("vehicle")).get("type"));
//
//                        }


                    }
//                   routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }


    /**
     * polyline decode method
     *
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}