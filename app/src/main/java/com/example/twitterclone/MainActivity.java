package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    public void login(final View view) {
        TextView usernameView=findViewById(R.id.usernameInput);
        final String username=usernameView.getText().toString();
        TextView passView=findViewById(R.id.passwordInput);
        String password=passView.getText().toString();
        view.setVisibility(View.INVISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "https://twitter-clone-test.herokuapp.com/login?username="+username+"&password="+password;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.get("response").equals(100)) {
                                Toast.makeText(getApplicationContext(),"Username not found!!!",Toast.LENGTH_SHORT).show();
                                view.setVisibility(View.VISIBLE);
                            } else if(response.get("response").equals(200)) {
                                JSONObject jsonObject=response.getJSONObject("currUser");
                                User currUser=new User(jsonObject.get("id").toString(),jsonObject.get("username").toString(),
                                        jsonObject.get("handle").toString(),jsonObject.get("email").toString(),jsonObject.get("dateJoined").toString());
                                Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),NavActivity.class);
                                intent.putExtra("currUser",currUser);
                                startActivity(intent);
                                view.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(),"Password does not match",Toast.LENGTH_SHORT).show();
                                view.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),"Network error!!!",Toast.LENGTH_SHORT).show();
                            view.setVisibility(View.VISIBLE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView signUp=findViewById(R.id.signupBtn);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
