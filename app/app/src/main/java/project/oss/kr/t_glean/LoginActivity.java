package project.oss.kr.t_glean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static project.oss.kr.t_glean.InputIP.SERVER_ADDRESS;

public class LoginActivity extends AppCompatActivity {
    EditText edt_id, edt_pwd;
    Button btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_id = (EditText)findViewById(R.id.edt_id);
        edt_pwd = (EditText)findViewById(R.id.edt_pwd);
        btn_login = (Button)findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_id.getText().equals("") || edt_pwd.getText().equals(""))
                    Toast.makeText(LoginActivity.this, "Input ID or PWD", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        String result = new AccessServer().execute(SERVER_ADDRESS + "/mlogin?id=" + edt_id.getText().toString()
                                + "&pwd=" + edt_pwd.getText().toString()).get();
                        //Toast.makeText(LoginActivity.this, "Login Response Code : " + result, Toast.LENGTH_SHORT).show();
                        //200#wname : Success / 400 : Failed / 444 : Server Error
                        if(result.substring(0, 3).equals("200")) {
                            Toast.makeText(LoginActivity.this, "Login Successed!", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(LoginActivity.this, MainActivity.class);
                            it.putExtra("LoginWorker", result.substring(4));
                            startActivity(it);
                        } else if(result.equals("400")) {
                            Toast.makeText(LoginActivity.this, "Wrong ID or Password!", Toast.LENGTH_SHORT).show();
                        } else if(result.equals("444")) {
                            Toast.makeText(LoginActivity.this, "Ask Administrator!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
}
