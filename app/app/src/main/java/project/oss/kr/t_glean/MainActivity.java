package project.oss.kr.t_glean;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static project.oss.kr.t_glean.InputIP.SERVER_ADDRESS;

public class MainActivity extends AppCompatActivity {
    TextView txt_message, txt_time_unow;
    Button btn_show_wtimes, btn_reload_wtime;
    static String LoginWorker;
    ToggleButton workbtn, lunchbtn, restbtn;
    ListView m_ListView;
    CustomAdapter m_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_message = (TextView) findViewById(R.id.txt_message);
        txt_time_unow = (TextView) findViewById(R.id.txt_time_unow);
        btn_show_wtimes = (Button) findViewById(R.id.btn_show_wtimes);
        btn_reload_wtime = (Button) findViewById(R.id.btn_reload_wtime);
        workbtn = (ToggleButton)findViewById(R.id.workbtn);
        lunchbtn = (ToggleButton)findViewById(R.id.lunchbtn);
        restbtn = (ToggleButton)findViewById(R.id.restbtn);



        Intent it = getIntent();
        LoginWorker = it.getStringExtra("LoginWorker");
        txt_message.setText(LoginWorker + ", Welcome!");
        lunchbtn.setEnabled(false);
        restbtn.setEnabled(false);

        try {
            int result1 = Integer.parseInt(new AccessServer().execute(SERVER_ADDRESS +
                    "/maquery?wname=" + URLEncoder.encode(LoginWorker, "UTF-8")).get());
            if(result1 >= 101) {
                //Toast.makeText(MainActivity.this, "오늘 출근 이력 없음", Toast.LENGTH_SHORT).show();
                if (result1 >= 102) {
                    //Toast.makeText(MainActivity.this, "퇴근 안찍었네", Toast.LENGTH_SHORT).show();
                    lunchbtn.setEnabled(true);
                    lunchbtn.setChecked(false);

                    restbtn.setEnabled(true);
                    restbtn.setChecked(false);

                    workbtn.setChecked(true);

                    if (result1 == 104) {
                        //Toast.makeText(MainActivity.this, "점심 끝 안찍고 퇴근 안찍음", Toast.LENGTH_SHORT).show();
                        lunchbtn.setChecked(true);

                        restbtn.setEnabled(false);
                    } else if (result1 == 105) {
                        //Toast.makeText(MainActivity.this, "휴식 끝 안찍고 퇴근 안찍음", Toast.LENGTH_SHORT).show();
                        restbtn.setChecked(true);

                        lunchbtn.setEnabled(false);
                    }
                }
            }
            setWtime();

        }catch (Exception e) {
            e.printStackTrace();
        }

        btn_show_wtimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, WtimesActivity.class);
                startActivity(it);
            }
        });

        btn_reload_wtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWtime();
            }
        });

        workbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(workbtn.isChecked()) {
                    System.out.println("startwork");
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/rwlog?wname=" +
                                URLEncoder.encode(LoginWorker, "UTF-8") + "&btnName=startwork").get();
                        if(result.equals("200")) {
                            //Toast.makeText(MainActivity.this, "Success to input to log or db!", Toast.LENGTH_SHORT).show();
                            lunchbtn.setEnabled(true);
                            lunchbtn.setChecked(false);

                            restbtn.setEnabled(true);
                            restbtn.setChecked(false);
                            Date nd = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            refresh("startwork - " + sdf.format(nd),0);
                            altshow();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Ask Administrator!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(MainActivity.this, LoginWorker + ", startwork!", Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println("finishwork");
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/rwlog?wname=" +
                                URLEncoder.encode(LoginWorker, "UTF-8") + "&btnName=finishwork").get();
                        if(result.equals("200")) {
                            //Toast.makeText(MainActivity.this, "Success to input to log or db!", Toast.LENGTH_SHORT).show();
                            lunchbtn.setChecked(false);
                            lunchbtn.setEnabled(false);
                            restbtn.setChecked(false);
                            restbtn.setEnabled(false);
                            Date nd = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            refresh("finishwork - " + sdf.format(nd),0);

                            setWtime();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Ask Administrator!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(MainActivity.this, LoginWorker + ", finishwork!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lunchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lunchbtn.isChecked()) {
                    System.out.println("startlunch");
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/rwlog?wname=" +
                                URLEncoder.encode(LoginWorker, "UTF-8") + "&btnName=startlunch").get();
                        if(result.equals("200")) {
                            //Toast.makeText(MainActivity.this, "Success to input to log or db!", Toast.LENGTH_SHORT).show();
                            restbtn.setEnabled(false);
                            setWtime();
                            Date nd = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            refresh("startlunch - " + sdf.format(nd),0);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Ask Administrator!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(MainActivity.this, LoginWorker + ", startlunch!", Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println("finishlunch");
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/rwlog?wname=" +
                                URLEncoder.encode(LoginWorker, "UTF-8") + "&btnName=finishlunch").get();
                        if(result.equals("200")) {
                            //Toast.makeText(MainActivity.this, "Success to input to log or db!", Toast.LENGTH_SHORT).show();
                            restbtn.setEnabled(true);
                            setWtime();
                            Date nd = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            refresh("finishlunch - " + sdf.format(nd),0);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Ask Administrator!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(MainActivity.this, LoginWorker + ", finishlunch!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        restbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restbtn.isChecked()) {
                    System.out.println("startrest");
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/rwlog?wname=" +
                                URLEncoder.encode(LoginWorker, "UTF-8") + "&btnName=startrest").get();
                        if(result.equals("200")) {
                            //Toast.makeText(MainActivity.this, "Success to input to log or db!", Toast.LENGTH_SHORT).show();
                            lunchbtn.setEnabled(false);
                            setWtime();
                            Date nd = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            refresh("startrest - " + sdf.format(nd),0);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Ask Administrator!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(MainActivity.this, LoginWorker + ", startrest!", Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println("finishrest");
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/rwlog?wname=" +
                                URLEncoder.encode(LoginWorker, "UTF-8") + "&btnName=finishrest").get();
                        if(result.equals("200")) {
                            //Toast.makeText(MainActivity.this, "Success to input to log or db!", Toast.LENGTH_SHORT).show();
                            lunchbtn.setEnabled(true);
                            setWtime();
                            Date nd = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                            refresh("finishrest - " + sdf.format(nd),0);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Ask Administrator!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(MainActivity.this, LoginWorker + ", finishrest!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        m_Adapter = new CustomAdapter();

        m_ListView = (ListView) findViewById(R.id.listView1);
        m_ListView.setAdapter(m_Adapter);



//        findViewById(R.id.button1).setOnClickListener(new Button.OnClickListener() {
//              @Override
//              public void onClick(View v) {
//                  EditText editText = (EditText) findViewById(R.id.editText1) ;
//                  String inputValue = editText.getText().toString() ;
//                  editText.setText("");
//                  refresh(inputValue,0);
//              }
//          }
//        );


        findViewById(R.id.button2).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) findViewById(R.id.editText1) ;
                        String inputValue = editText.getText().toString() ;
                        editText.setText("");
                        refresh(inputValue,1);
                        }
                }
        );

    }

    private void refresh (String inputValue, int _str) {
        m_Adapter.add(inputValue,_str) ;
        m_Adapter.notifyDataSetChanged();
    }

    public void setWtime() {
        long result = 0;
        try {
            result = Long.parseLong(new AccessServer().execute(SERVER_ADDRESS +
                    "/gwtms?wname=" + URLEncoder.encode(LoginWorker, "UTF-8")).get());
        }catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        txt_time_unow.setText(sdf.format(new Date(result)));
    }

    public void altshow()
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("매우 좋음");
        ListItems.add("좋음");
        ListItems.add("보통");
        ListItems.add("짜증");
        ListItems.add("매우 짜증");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final List SelectedItems  = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg="";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                        }
                        Toast.makeText(getApplicationContext(),
                                "Items Selected.\n"+ msg , Toast.LENGTH_LONG)
                                .show();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}
