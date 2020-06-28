package com.mahc.custombottomsheet.Parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mahc.custombottomsheet.Interface.TaskLoadedCallback;
import com.mahc.custombottomsheet.R;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mahc.custombottomsheet.Activity.MainActivity.resources;


public class PointsParser extends AsyncTask<String, Integer, JSONObject> {
    TaskLoadedCallback taskCallback;
    String directionMode = "";
    //drawable에 있는 이미지를 담아놓은 class
    Class<R.drawable> drawable=R.drawable.class;

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected JSONObject doInBackground(String... jsonData) {

        JSONObject jObject = null;
        // List<List<HashMap<String, String>>> routes = null;

        Log.i("data show",jsonData.length+"");
        if(jsonData.length!=0){
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.i("data show 1", jsonData[0].toString());



            } catch (Exception e) {
                Log.d("mylog", e.toString());
                e.printStackTrace();
            }
        }



        return jObject;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(JSONObject jObject) {

        //point들이 .. document...
        ArrayList<LatLng> points;
        List<List<HashMap<String, String>>> result =null;
        ArrayList<ArrayList<LatLng>> pointsall = new ArrayList<ArrayList<LatLng>>();

        //각 point의 최종 list의 수
        ArrayList<Integer> su=new ArrayList<>();


        PolylineOptions lineOptions = null;
        // Traversing through all the routes

        //markeroption
        MarkerOptions markerOptions = null;
        //markeroption list
        ArrayList<MarkerOptions> markerOptionsall=new ArrayList<MarkerOptions>();

        //html_instruction
        ArrayList<String> instructionall=new ArrayList<String>();
        //duration
        ArrayList<String> duration=new ArrayList<String>();
        //distance
        ArrayList<String> distance=new ArrayList<String>();
        //transit_mode
        ArrayList<String> mode=new ArrayList<String>();

        //alldistance
        String all_distance="";
        //allduration
        String all_duration="";

        DataParser parser = new DataParser();


        // Starts parsing data
        result = parser.parse(jObject);
        Log.i("mylog", "Executing routes");
        Log.i("mylog", result.size()+"/"+result.toString());

        if(result!=null){
            for (int i = 0; i < result.size(); i++) {

                Log.i("result",i+"");

                Log.i("result2",result.get(i).toString());


                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                su.add(path.size());


                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {

                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    Log.i("path"+j,path.get(j).get("html_instructions"));
                    Log.i("duration"+j,path.get(j).get("duration"));
                    Log.i("distance"+j,path.get(j).get("distance"));
                    if(j==path.size()-1){
                        instructionall.add(path.get(j).get("html_instructions"));
                        distance.add(path.get(j).get("distance"));
                        duration.add(path.get(j).get("duration"));
                        mode.add(path.get(j).get("travel_mode"));
                        all_distance=path.get(j).get("all_distance");
                        all_duration=path.get(j).get("all_duration");
                    }


                    LatLng position = new LatLng(lat, lng);
                    points.add(position);


                }
                // Adding all the points in the route to LineOptions

                pointsall.add(points);


                //point 들이 다 담겼을 때
                if(i==result.size()-1){
                    for (int tt = 0; tt <pointsall.size() ; tt++) {

                        markerOptions= new MarkerOptions();


                        Log.i("travel_mode",mode.get(i));
                        if (mode.get(i).equalsIgnoreCase("walking")) {
                            Log.i("walking",mode.get(i));
                            lineOptions.width(20);
                            lineOptions.color(Color.parseColor("#28FF34"));
                        } else if (mode.get(i).equalsIgnoreCase("driving")){
                            Log.i("driving",mode.get(i));
                            lineOptions.width(20);
                            lineOptions.color(Color.parseColor("#FF6071"));
                           // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        }




                        for (int ttt = 0;  ttt<su.get(tt) ; ttt++) {

                            if(ttt==0){

                                if(tt!=0){ //처음은 출발지점이므로, 출발지점이 아니고 첫시작인애들만
                                    //Log.i("new start","new start");


                                 //   markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                                    int marker_num = 0;
                                    try {
                                        //drawable에 있는 marker는 번호가 매겨져 있으므로 그 이름 값을 가지고 넣기 위한 작업
                                        Field field=drawable.getField("marker"+(tt));
                                        marker_num=field.getInt(null);
                                    } catch (NoSuchFieldException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }

                                    //마커를 순서대로 넣어볼까요?
                                    markerOptions.position(pointsall.get(tt).get(ttt)).alpha(1f)
                                            .icon(BitmapDescriptorFactory.fromBitmap
                                                    (Bitmap.createScaledBitmap(((BitmapDrawable)resources.getDrawable(marker_num))
                                                            .getBitmap(),80,80,false)));

                                    markerOptionsall.add(markerOptions);
                                }else{

                                }


                            }else{

                                lineOptions.add(pointsall.get(tt).get(ttt));

                            }



                        }

                    }

                }

                Log.d("mylog", "onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                //mMap.addPolyline(lineOptions);
                taskCallback.onTaskDone(lineOptions,markerOptionsall,distance,duration,instructionall,mode,all_distance,all_duration);

            } else {
                taskCallback.onTaskDone("ZERO_RESULTS");

                Log.d("mylog", "without Polylines drawn");
            }
        }


    }
}