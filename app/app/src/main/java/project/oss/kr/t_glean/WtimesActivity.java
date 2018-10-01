package project.oss.kr.t_glean;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static project.oss.kr.t_glean.InputIP.SERVER_ADDRESS;
import static project.oss.kr.t_glean.MainActivity.LoginWorker;

public class WtimesActivity extends AppCompatActivity {
    TextView txt_date, txt_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wtimes);
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_time = (TextView) findViewById(R.id.txt_time);

        ArrayList<Info> al = new ArrayList<>();
        StringBuffer dateSb = new StringBuffer(), timeSb = new StringBuffer();

        try {
            String result = new AccessServer().execute(SERVER_ADDRESS + "/getdt?wname=" +
                    URLEncoder.encode(LoginWorker, "UTF-8")).get();

            System.out.println(result);

            long ltime = 0;
            JSONObject jo = new JSONObject(result);     //JSON from server
            JSONArray jarray = (JSONArray)jo.get("data");   // data의 JSONArray
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            for(int i=0; i < jarray.length() - 1; i++) {
                JSONObject jObject = jarray.getJSONObject(i);
                String nextD = jarray.getJSONObject(i + 1).getString("date");
                String nextT = jarray.getJSONObject(i + 1).getString("time");
                String date = jObject.getString("date");
                String time = jObject.getString("time");
                ltime += sdf.parse(time).getTime();
                if(!nextD.equals(date)) {        //현재 date와 다음 date가 다르다면
                    Info f1 = new Info();
                    f1.date = date;
                    f1.time = sdf.format(new Date(ltime));
                    al.add(f1);
                    ltime = 0;
                    if(i == jarray.length() - 2) {      //last index
                        ltime += sdf.parse(nextT).getTime();
                        Info f2 = new Info();
                        f2.date = nextD;
                        f2.time = sdf.format(new Date(ltime));
                        al.add(f2);
                    }
                }
                else {
                    if(i == jarray.length() - 2) {      //last index
                        ltime += sdf.parse(nextT).getTime();
                        Info f = new Info();
                        f.date = date;
                        f.time = sdf.format(new Date(ltime));
                        al.add(f);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < al.size(); i++) {
            //System.out.println(al.get(i).date + " : " + al.get(i).time);
            dateSb.append(al.get(i).date + "\n");
            timeSb.append(al.get(i).time + "\n");
        }

        txt_date.setText(dateSb.toString());
        txt_time.setText(timeSb.toString());
    }
}

class Info {
    public String date, time;
}