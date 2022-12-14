package edu.northeastern.numad22fa_team23;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.northeastern.numad22fa_team23.model.Chat;

public class ChatActivity extends AppCompatActivity {
    List<Chat> chatList;
    EditText content;
    Button send;
    Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String groupName;
    String myname;
    RecyclerView chatRecycler;
    ChatListAdapter chatListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        content = findViewById(R.id.edittext_chatbox);
        send = findViewById(R.id.button_chatbox_send);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        Intent i=getIntent();
        Bundle data=i.getExtras();
        groupName = data.getString("groupname");
        myname = data.getString("username");
//        groupName = "groupName";
//        myname = "qwe";

        FirebaseUser user = mAuth.getCurrentUser();
//        String uid = "OfUlIQ1M42YjTg8iFplDJefLALX2";
//        String text = "h r u";
//        String time = "2022-11-21 21:12:13506";
//        Chat ch = new Chat(text, myname, time, uid);
//        List<Chat> l = new ArrayList<>();
//        l.add(ch);
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Groups").child(groupName)
                .child("Chats");
//        mDatabase.setValue(l);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = content.getText().toString();
                if(msg.isEmpty()){
                    Toast.makeText(context, "Input cannot be empty!", Toast.LENGTH_SHORT).show();
                }else{
                    mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                                chatList = (List<Chat>) task.getResult().getValue();
                                if (chatList == null) {
                                    chatList = new ArrayList<>();
                                }
                                Date date = new Date();
                                TimeZone.setDefault(TimeZone.getTimeZone("GMT-7"));
                                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                                date = cal.getTime();
                                long t = date.getTime();
                                final String timeStamp = new Timestamp(t).toString().
                                        replace(".", "");

                                Log.d("timeStamp", timeStamp);

                                Chat newChat = new Chat(msg, myname, timeStamp, mAuth.getCurrentUser().getUid());
                                chatList.add(newChat);
                                mDatabase.setValue(chatList);
                                content.setText("");

                            }
                        }
                    });

                }
            }
        });

        List<Chat> list = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    list.clear();
                    for(DataSnapshot sn: snapshot.getChildren()){
                        //System.out.println(sn.getValue().getClass() );
                        Chat msg = (Chat) sn.getValue(Chat.class);
                        list.add(msg);
                    }
                    List<Chat> chats = (List<Chat>) snapshot.getValue();
                    //System.out.println(snapshot.getValue().getClass().getClass());
                    chatRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
                    chatListAdapter = new ChatListAdapter(context, list);
                    chatRecycler.setLayoutManager(new LinearLayoutManager(context));
                    chatRecycler.setAdapter(chatListAdapter);
                    // chatRecycler.scrollToPosition(chatList.size()-1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
