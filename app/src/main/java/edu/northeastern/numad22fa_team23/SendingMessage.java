package edu.northeastern.numad22fa_team23;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import edu.northeastern.numad22fa_team23.databinding.ActivityMainBinding;
import edu.northeastern.numad22fa_team23.databinding.LayoutStickItToEmBinding;

public class SendingMessage extends AppCompatActivity {

//    private ImageView image1;
//    private Button sendMessageButton;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    private ImageView selectedImage;
    private String selectedUsername;

    LayoutStickItToEmBinding binding;

    private static String SERVER_KEY = "key=AAAAYVMPBrg:APA91bFcn3zDzceEIocqvzaKlPRBN1dKIdThGYeYK443c1A96HrITFGU8J3-VIj1u5ymAHbau-AsH3rpEsrUcN6E7FpCpz9XJjPGFuXDBx33-N_o-I2JLgepGt3qfMudTuCKGnWLKVy3";
    private static String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutStickItToEmBinding.inflate(getLayoutInflater());
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        setSpinner();

//        binding.selectImagebtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUsername = binding.spinner.getSelectedItem().toString();
                uploadPicture();
                sendMessageToDevice(v);
            }
        });


        binding.image01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = binding.image01;
                imageUri = Uri.parse("android.resource://edu.northeastern.numad22fa_team23/drawable/image01");
//                chooseImage();
            }
        });

        binding.image02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = binding.image02;
                imageUri = Uri.parse("android.resource://edu.northeastern.numad22fa_team23/drawable/image02");
               // chooseImage();
            }
        });

        binding.image03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = binding.image03;
                imageUri = Uri.parse("android.resource://edu.northeastern.numad22fa_team23/drawable/image03");
                //chooseImage();
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        if (token == null) {
                            // Get new FCM registration token
                            token = task.getResult();
                        }

                        System.out.println(token);
                        Toast.makeText(SendingMessage.this, "token: " + token, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void sendMessageToDevice(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(token);
            }
        }).start();
    }

    private void sendMessageToDevice(String targetToken) {

        // Prepare data
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Message Title from 'SEND MESSAGE TO CLIENT BUTTON'");
            jNotification.put("body", "Message body from 'SEND MESSAGE TO CLIENT BUTTON'");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            /*
            // We can add more details into the notification if we want.
            // We happen to be ignoring them for this demo.
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            */
            jdata.put("title", "data title from 'SEND MESSAGE TO CLIENT BUTTON'");
            jdata.put("content", "data content from 'SEND MESSAGE TO CLIENT BUTTON'");

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            /*
            // If sending to multiple clients (must be more than 1 and less than 1000)
            JSONArray ja = new JSONArray();
            ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            ja.put(FirebaseInstanceId.getInstance().getToken());
            jPayload.put("registration_ids", ja);
            */

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String resp = fcmHttpConnection(SERVER_KEY, jPayload);
        postToastMessage("Status from Server: " + resp, getApplicationContext());

    }

    public static void postToastMessage(final String message, final Context context){
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String fcmHttpConnection(String serverToken, JSONObject jsonObject) {
        try {

            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", serverToken);
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            return convertStreamToString(inputStream);
        } catch (IOException e) {
            return "NULL";
        }

    }

    public static String convertStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String len;
            while ((len = bufferedReader.readLine()) != null) {
                stringBuilder.append(len);
            }
            bufferedReader.close();
            return stringBuilder.toString().replace(",", ",\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            //image1.setImageURI(imageUri);
            //uploadPicture();
        }
    }

    private void uploadPicture() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending message...");
        progressDialog.show();

        Date now = new Date();
        String filename = now.toString();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/" + filename);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //binding.image01
                        Toast.makeText(SendingMessage.this, "Successfully send message", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(SendingMessage.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setSpinner() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap<String, HashMap<String, User>> map = (HashMap<String, HashMap<String, User>>) dataSnapshot.getValue();
//                User user = dataSnapshot.getValue(User.class);
//                System.out.println(user.get("users"));
                HashMap<String, User> user = map.get("users");
                List<String> userNames = new ArrayList<>();

                for (String u : user.keySet()) {
                    userNames.add(u);
                }
                ArrayAdapter<String> adapter
                        = new ArrayAdapter<>(getApplicationContext(),
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        userNames);
                binding.spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                databaseError.toException();
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

    public void onSendButtonPressend(View v) {



    }

//    public static Properties getProperties(Context context)  {
//        Properties properties = new Properties();
//        AssetManager assetManager = context.getAssets();
//        InputStream inputStream = null;
//        try {
//            inputStream = assetManager.open("firebase.properties");
//            properties.load(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return properties;
//    }

    public void createNotificationChannel() {
        // This must be called early because it must be called before a notification is sent.
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Team_23_Stick_It_To_Em";
            String description = "Team_23_Stick_It_To_Em";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Team_23_Stick_It_To_Em_Notification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
