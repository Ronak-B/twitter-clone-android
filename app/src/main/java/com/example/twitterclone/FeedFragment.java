package com.example.twitterclone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class FeedFragment extends Fragment {


    private static final String TAG = FeedAdapter.class.getSimpleName();

    private User currUser;

    ImageView imageView;

    GestureDetector mDetector;
    Uri filePath;
    private RecyclerView recyclerView;
    private List<Tweet> tweets;
    private FeedAdapter feedAdapter;

    //private OnFragmentInteractionListener mListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance(User currUser) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putSerializable("currUser",currUser);
        fragment.setArguments(args);
        return fragment;
    }

    public void pickImage(Context context) {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null) {
           try {
               filePath=data.getData();
               //filePath=getPath(selectedImage);
               Bitmap bitmap=MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
               imageView.setImageBitmap(bitmap);
               imageView.setVisibility(View.VISIBLE);
               //System.out.println(ConvertBitmapToString(bitmap).length());
               System.out.println();
           } catch (Exception e) {
               e.printStackTrace();
           }
        }
    }

    public void uploadMultipart(String user_id,String text) {
        String path = getPath(filePath);
        try {
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(getContext(), uploadId,"https://twitter-clone-test.herokuapp.com/tweet")
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("user_id", user_id)
                    .addParameter("text",text)//Adding text parameter to the request
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void addNewTweet(final Context context, final String s, final User currUser, final EditText editText) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://twitter-clone-test.herokuapp.com/tweet?text="+s+"&user_id="+currUser.getId();
        System.out.println();
        System.out.println(currUser.getId()+s);
        System.out.println();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.get("response").equals("success")) {
                                Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();
                                editText.setText("");
                            } else {
                                Toast.makeText(context,"error",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_feed,container,false);
        recyclerView=view.findViewById(R.id.feedRecyclerView);
        mDetector=new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Toast.makeText(getContext(),"hrllo",Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e2.getY()-e1.getY()>150) {
                    refreshFeed(getContext(),currUser);
                    return true;
                }
                return false;
            }
        });
        tweets=new ArrayList<>();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });
        requestStoragePermission();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        feedAdapter=new FeedAdapter(getActivity(),tweets);
        recyclerView.setAdapter(feedAdapter);

        currUser=(User)getArguments().getSerializable("currUser");

        TextView textView=view.findViewById(R.id.textView);
        imageView=view.findViewById(R.id.tweetImageId);
        Button addImageBtn=view.findViewById(R.id.addImageBtn);
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(getContext());
            }
        });

        Button newTweetBtn=view.findViewById(R.id.newTweetBtn);
        newTweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newTweetText=view.findViewById(R.id.newTweetText);
                String s=newTweetText.getText().toString();
                if(s.length()>0) {
                    uploadMultipart(currUser.getId(),s);
                    newTweetText.setText("");
                    imageView.setImageBitmap(null);
                    imageView.setVisibility(View.GONE);
                    refreshFeed(getContext(),currUser);
                } else {
                    Toast.makeText(getContext(), "Empty Tweet Not Allowed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        String s=currUser.getUsername()+"'s feed";
        textView.setText(s);
        refreshFeed(getContext(),currUser);
        return view;


    }


    class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {
        private Context context;
        private List<Tweet> tweets;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView username,handle,date,text;
            public ImageView image,proImage;

            public MyViewHolder(View view) {
                super(view);
                username = view.findViewById(R.id.tweetusername);
                handle = view.findViewById(R.id.tweethandle);
                date = view.findViewById(R.id.tweetdate);
                text=view.findViewById(R.id.tweettext);
                image=view.findViewById(R.id.tweetImageFeed);
                proImage=view.findViewById(R.id.tweetProImg);
            }
        }


        public FeedAdapter(Context context, List<Tweet> tweets) {
            this.context = context;
            this.tweets = tweets;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tweet_layout, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Tweet tweet = tweets.get(position);
            holder.username.setText(tweet.getUsername());
            holder.handle.setText("@"+tweet.getHandle());
            holder.date.setText(tweet.getDate().substring(0,16));
            holder.text.setText(tweet.getTweettext());
            Picasso.get().load("https://twitter-clone-test.herokuapp.com/getImage?fname="+tweet.getFname()).into(holder.image);
            Picasso.get().load("https://twitter-clone-test.herokuapp.com/getProImg?fname="+tweet.getUser_fname()).into(holder.proImage);

        }

        @Override
        public int getItemCount() {
            return tweets.size();
        }
    }
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    private static final int STORAGE_PERMISSION_CODE = 123;//This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void refreshFeed(Context context,final User currUser) {
        RequestQueue requestQueue=Volley.newRequestQueue(context);
        String url = "https://twitter-clone-test.herokuapp.com/getTweets";
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        tweets.clear();
                        for(int i=0;i<response.length();i++) {
                           try{
                               tweets.add(new Tweet(response.getJSONObject(i).get("username").toString(),
                                                    response.getJSONObject(i).get("handle").toString(),
                                                    response.getJSONObject(i).get("text").toString(),
                                                    response.getJSONObject(i).get("date_created").toString(),
                                                    response.getJSONObject(i).get("fname").toString(),
                                                    response.getJSONObject(i).get("user_fname").toString()));
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                        }
                        recyclerView.setAdapter(feedAdapter);
                        feedAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }
}
