package com.example.twitterclone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {


    boolean pickImage=false;
    EditText username,handle,email,password;
    Uri filePath;
    Bitmap bitmap;
    CircleImageView proImageView;

//    public String getPath(Uri uri) {
//        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = this.getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//
//        return path;
//    }

    public void sendRequest() {
        String url="https://twitter-clone-test.herokuapp.com/signup";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    if(result.get("response").equals("200")) {
                        Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (result.get("response").equals("100")){
                        Toast.makeText(getApplicationContext(),"Username not Unique",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Server error",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("email",email.getText().toString());
                params.put("handle",handle.getText().toString());
                params.put("password",password.getText().toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(username.getText().toString()+"Img",getFileDataFromDrawable(bitmap), "image/jpeg"));
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

//    public void sendRequest(Uri newPath) {
//        String path = getPath(newPath);
//        try {
//            String uploadId = UUID.randomUUID().toString();
//            new MultipartUploadRequest(getApplicationContext(), uploadId,
//                    "https://twitter-clone-test.herokuapp.com/signup")
//                    .addFileToUpload(path, "image") //Adding file
//                    .addParameter("username", username.getText().toString())
//                    .addParameter("email",email.getText().toString())
//                    .addParameter("handle",handle.getText().toString())
//                    .addParameter("password",password.getText().toString())//Adding text parameter to the request
//                    .setMaxRetries(2)
//                    .startUpload();
//            //Toast.makeText(getApplicationContext(),"success upload", Toast.LENGTH_SHORT).show();
//        } catch (Exception exc) {
//            //Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

    public void regUser(View view) {
        username=((EditText)findViewById(R.id.regUsername));
        email=((EditText)findViewById(R.id.regEmail));
        handle=((EditText)findViewById(R.id.regHandle));
        password=((EditText)findViewById(R.id.regPass));
        if(username.getText().toString().equals("")||email.getText().toString().equals("")
                ||password.getText().toString().equals("")||handle.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
        } else {
            if(!pickImage) {
                Toast.makeText(getApplicationContext(),"Choose a Profile Image",Toast.LENGTH_SHORT).show();
            } else {
                sendRequest();
            }
        }
    }

//    public class RegisterTask extends AsyncTask<Uri,Void,String> {
//        @Override
//        protected String doInBackground(Uri... uris) {
//            Uri uri=uris[0];
//            sendRequest(uri);
//            return "Done";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            username.setText("");
//            email.setText("");
//            handle.setText("");
//            password.setText("");
//            proImageView.setImageBitmap(null);
//            Toast.makeText(getApplicationContext(),"Successfully Registered",Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        proImageView=(CircleImageView) findViewById(R.id.profile_image);
        ImageView addImgView=findViewById(R.id.addProImgBtn);
        addImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null) {
            try {
                filePath=data.getData();
                //filePath=getPath(selectedImage);
                bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),filePath);
                proImageView.setImageBitmap(bitmap);
                proImageView.setVisibility(View.VISIBLE);
                pickImage=true;
                //System.out.println(ConvertBitmapToString(bitmap).length());
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
