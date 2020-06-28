package com.mahc.custombottomsheet.Activity;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mahc.custombottomsheet.Parser.PointsParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mahc.custombottomsheet.Activity.MainActivity.bottomSheetTextView;

public class MoveURL extends AsyncTask<String, Void, String> {
    Context mContext;
    String directionMode = "";


    public MoveURL(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... strings) {

        String data = "";
        //directionmode 2번째 값에 따로 담은 것만 뽑아냄.
        directionMode = strings[1];

        try {
            //google api 통신하여 return 값 받기 .
            data = downloadUrl(strings[0]);

            Log.d("mylog", "Background task data " + data.toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        PointsParser parser=new PointsParser(mContext,directionMode);
        parser.execute(s);

//        PointsParser parserTask = new PointsParser(mContext, directionMode);
//        // Invokes the thread for parsing the JSON data
//        parserTask.execute(s);
    }

    //url 보내서 json 값을 받아옴.
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // url 통신- httpurlconnection
            urlConnection = (HttpURLConnection) url.openConnection();
            // connect
            urlConnection.connect();
            // url로 부터 data 읽어오기
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            String distance="";
            String duration="" ;
            String result="" ;
            int a=0;
            while ((line = br.readLine()) != null) {
                if(a==4&&line.contains("ZERO_RESULTS")){

                    String test[]=line.split(":");
                    result=test[1].substring(2,14);
                    Log.i("result",result);
                }
                a++;
                sb.append(line);
            }



            if(result.equals("ZERO_RESULTS")){
                bottomSheetTextView.setText(result);
            }



            data = sb.toString();

            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}


