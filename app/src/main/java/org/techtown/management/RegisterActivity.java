package org.techtown.management;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    final static private String RegistURL = "http://lle21cen.cafe24.com/Register.php";
    final static private String dupCheckURL = "http://lle21cen.cafe24.com/DupCheck.php";

    EditText idText, passwordText, passwordConfirm;
    TextView dupCheck, passwordCheck, passwordConfirmCheck;
    String passwd1, passwd2;
    boolean isPasswordAvailable = false, isIdAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idText = (EditText) findViewById(R.id.editText3);
        passwordText = (EditText) findViewById(R.id.editText4);
        final EditText nameText = (EditText) findViewById(R.id.editText5);
        final EditText ageText = (EditText) findViewById(R.id.editText6);
        passwordConfirm = (EditText) findViewById(R.id.passwordConfirm);

        dupCheck = (TextView) findViewById(R.id.dupCheckButton);
        passwordCheck = (TextView) findViewById(R.id.passwordCheck);
        passwordConfirmCheck = (TextView) findViewById(R.id.passwordConfirmCheck);

        Button registerButton = (Button) findViewById(R.id.button);

        formAvailabilityTest();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isIdAvailable && isPasswordAvailable) {
                    String userId = idText.getText().toString();
                    String userPassword = passwordText.getText().toString();
                    String userPasswordConfirm = passwordConfirm.getText().toString();
                    String userName = nameText.getText().toString();
                    int userAge = Integer.parseInt(ageText.getText().toString());

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("회원 등록에 성공했습니다.")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    RegisterActivity.this.startActivity(intent);
                                                }
                                            })
                                            .create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("회원 등록에 실패했습니다.\n문제가 계속되면 관리자에게 문의바람.")
                                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    idText.setText("");
                                                    passwordText.setText("");
                                                    passwordConfirm.setText("");
                                                    nameText.setText("");
                                                    ageText.setText("");
                                                    idText.requestFocus();
                                                }
                                            })
                                            .create().show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    RegisterRequest registerRequest = new RegisterRequest(Request.Method.POST, RegistURL, responseListener, null);
                    registerRequest.doRegist(userId, userPassword, userName, userAge);
                    RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                    requestQueue.add(registerRequest);
                } else if (!isIdAvailable) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("아이디를 입력하고 중복검사를 눌러주세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    idText.requestFocus();
                                }
                            })
                            .create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("비밀번호가 올바르지 않거나 일치하지 않습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    passwordText.requestFocus();
                                }
                            })
                            .create().show();
                }
            }
        });
        idText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isIdAvailable = false;
            }
        });
    }

    public void dupCheck(View v) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean exist = jsonResponse.getBoolean("exist");

                    if (exist) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("이 아이디는 이미 사용중입니다. ").setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        idText.setText("");
                                    }
                                }).create().show();

                    } else {
                        Toast.makeText(RegisterActivity.this, "아이디를 사용하실 수 있습니다.", 0).show();
                        passwordText.requestFocus();
                        isIdAvailable = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DupCheck dupCheck = new DupCheck(Request.Method.POST, dupCheckURL, responseListener, null);
        String userID = idText.getText().toString();
        dupCheck.setUserID(userID);
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(dupCheck);
    }

    public void formAvailabilityTest() {

        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passwd1 = passwordText.getText().toString();
                passwd2 = passwordConfirm.getText().toString();
                if (!hasFocus) {
                    if (passwd1.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요.", 0).show();
                        isPasswordAvailable = false;
                    } else {
//                        if (비밀번호가 적합할 때) {
//                            ...
//                            passwordCheck.setVisibility(View.VISIBLE);
//                            passwordCheck.setText("사용불가");
//                            passwordCheck.setTextColor(Color.RED);
//                        }
                    }
                }
            }
        });
        passwordText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwd1 = passwordText.getText().toString();
                if (passwd1.equals(passwd2)) {
                    passwordConfirmCheck.setTextColor(Color.parseColor("#32cd32"));
                    isPasswordAvailable = true;
                    passwordConfirmCheck.setText("일치");
                } else {
                    passwordConfirmCheck.setTextColor(Color.RED);
                    isPasswordAvailable = false;
                    passwordConfirmCheck.setText("불일치");
                }
            }
        });


        passwd1 = passwordText.getText().toString();
        passwordConfirm.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwd2 = passwordConfirm.getText().toString();
                if (passwd1.equals(passwd2)) {
                    passwordConfirmCheck.setTextColor(Color.parseColor("#32cd32"));
                    isPasswordAvailable = true;
                    passwordConfirmCheck.setText("일치");
                } else {
                    passwordConfirmCheck.setTextColor(Color.RED);
                    isPasswordAvailable = false;
                    passwordConfirmCheck.setText("불일치");
                }
            }
        });
    }
}
