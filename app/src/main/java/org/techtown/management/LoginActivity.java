package org.techtown.management;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    final static private String LoginURL = "http://lle21cen.cafe24.com/Login.php";
    EditText id, password;
    Button loginButton;
    String userId, userPassword, userName;
    int userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText) findViewById(R.id.login_id);
        password = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                userId = jsonResponse.getString("userID");
                                userPassword = jsonResponse.getString("userPassword");
                                userName = jsonResponse.getString("userName");
                                userAge = jsonResponse.getInt("userAge");

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("로그인에 성공했습니다 ").setCancelable(false)
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("userID", userId);
                                                intent.putExtra("userPassword", userPassword);
                                                intent.putExtra("userName", userName);
                                                intent.putExtra("userAge", userAge);

                                                finish();
                                                startActivity(intent);
                                            }
                                        }).create().show();


                            } else {
                                Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", 1).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(Request.Method.POST, LoginURL, responseListener, null);
                loginRequest.doLogin(id.getText().toString(), password.getText().toString());
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                requestQueue.add(loginRequest);
            }
        });
    }

    public void startRegisterActivity(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
