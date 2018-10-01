package project.oss.kr.t_glean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccessServer extends AsyncTask<String, Void, String> {
    /*
    Context ct;
    short requestCode;
    Button btn1, btn2;

    public AccessServer() {}


    public AccessServer(Context ct) {
        this.ct = ct;
    }

    public AccessServer(Context ct, short requestCode) {
        this.ct = ct;
        this.requestCode = requestCode;
    }

    public AccessServer(Context ct, short requestCode, Button btn1, Button btn2) {
        this.ct = ct;
        this.requestCode = requestCode;
        this.btn1 = btn1;
        this.btn2 = btn2;
    }
    */

    /*
        Request Code
            (short)101 : Check SERVER IP validation
            (short)159 : Worker Login
            (short)811 : Record Working Time
    */

    @Override
    protected String doInBackground(String... urls) {
        int data;
        StringBuffer sb = new StringBuffer();

        try {
            InputStream in = accessUrl(urls[0]);
            while((data = in.read()) != -1)
                sb.append((char) data);

        }catch (Exception e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
            sb.append("Failed to Access Server!");
        }

        return sb.toString();
    }

    /*
    @Override
    protected void onPostExecute(String results) {

        if(requestCode == (short)101) {     //Check SERVER IP validation
            Toast.makeText(ct, "Check SERVER IP validation : " + results, Toast.LENGTH_SHORT).show();
            if(results.equals("1")) {
                //server is on
                Intent it = new Intent((Activity)ct, LoginActivity.class);
                ct.startActivity(it);
            }
        }
        else if(requestCode == (short)159) {    //Worker Login
            Toast.makeText(ct, "Login Response Code : " + results, Toast.LENGTH_SHORT).show();
            //200#wname : Success / 400 : Failed / 444 : Server Error
            if(results.substring(0, 3).equals("200")) {
                Toast.makeText(ct, "Login Successed!", Toast.LENGTH_SHORT).show();
                Intent it = new Intent((Activity)ct, MainActivity.class);
                it.putExtra("LoginWorker", results.substring(4));
                ct.startActivity(it);
            } else if(results.equals("400")) {
                Toast.makeText(ct, "Wrong ID or Password!", Toast.LENGTH_SHORT).show();
            } else if(results.equals("444")) {
                Toast.makeText(ct, "Ask Administrator!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == (short)811) {
            //
            if(results.equals("200")) {
                Toast.makeText(ct, "Success to input to log or db!", Toast.LENGTH_SHORT).show();
                btn1.setEnabled(false);
                btn2.setEnabled(true);
            }
            else {
                Toast.makeText(ct, "Ask Administrator!", Toast.LENGTH_SHORT).show();
            }
        }

    }*/

    public InputStream accessUrl(String getUrl) throws IOException {
        URL url = new URL(getUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        conn.setReadTimeout(5000 /* milliseconds */);
        conn.setConnectTimeout(5000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Starts the query
        conn.connect();
        InputStream in = conn.getInputStream();
        return in;
    }
}
