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

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONObject;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {



    EditText username,handle,email,password;
    Uri filePath;
    CircleImageView proImageView;

    public String getPath(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = this.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void sendRequest(Uri newPath) {
        String path = getPath(newPath);
        try {
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(getApplicationContext(), uploadId,
                    "https://twitter-clone-test.herokuapp.com/signup")
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("username", username.getText().toString())
                    .addParameter("email",email.getText().toString())
                    .addParameter("handle",handle.getText().toString())
                    .addParameter("password",password.getText().toString())//Adding text parameter to the request
                    .setMaxRetries(2)
                    .startUpload();
            //Toast.makeText(getApplicationContext(),"success upload", Toast.LENGTH_SHORT).show();
        } catch (Exception exc) {
            //Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void regUser(View view) {
        username=((EditText)findViewById(R.id.regUsername));
        email=((EditText)findViewById(R.id.regEmail));
        handle=((EditText)findViewById(R.id.regHandle));
        password=((EditText)findViewById(R.id.regPass));
        if(username.getText().toString().equals("")||email.getText().toString().equals("")
                ||password.getText().toString().equals("")||handle.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
        } else {
            if(filePath==null) {
                Uri uri = Uri.parse("android.resource://com.example.twitterclone/drawable/default_profile_img.png");
                new RegisterTask().execute(uri);
            } else {
                new RegisterTask().execute(filePath);
            }
        }
    }

    public class RegisterTask extends AsyncTask<Uri,Void,String> {
        @Override
        protected String doInBackground(Uri... uris) {
            Uri uri=uris[0];
            sendRequest(uri);
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            username.setText("");
            email.setText("");
            handle.setText("");
            password.setText("");
            proImageView.setImageBitmap(null);
            Toast.makeText(getApplicationContext(),"Successfully Registered",Toast.LENGTH_SHORT).show();
            finish();
        }
    }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null) {
            try {
                filePath=data.getData();
                //filePath=getPath(selectedImage);
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),filePath);
                proImageView.setImageBitmap(bitmap);
                proImageView.setVisibility(View.VISIBLE);
                //System.out.println(ConvertBitmapToString(bitmap).length());
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
