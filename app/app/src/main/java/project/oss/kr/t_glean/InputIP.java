package project.oss.kr.t_glean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputIP extends AppCompatActivity {
    EditText insertip;
    Button submitip;
    static String SERVER_ADDRESS = "";
    Button btn_dev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_ip);
        insertip = (EditText)findViewById(R.id.insertip);
        submitip = (Button)findViewById(R.id.submitip);
        btn_dev = (Button)findViewById(R.id.bt_dev);
        Intent it2 = getIntent();


        submitip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(insertip.getText().equals(""))
                    Toast.makeText(InputIP.this, "Input IP!", Toast.LENGTH_SHORT).show();
                else {
                    SERVER_ADDRESS = "http://" + insertip.getText().toString() + ":8080/T_glean_server";
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/csver").get();
                        if(result.equals("1")) {
                            Toast.makeText(InputIP.this, "Server is ON", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(InputIP.this, LoginActivity.class);
                            startActivity(it);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it2 = new Intent(InputIP.this, ChatUI.class);
                startActivity(it2);
            }
        });
    }
}
