package com.example.gine_tube.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.gine_tube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView circleImageView;
    TextView your_chnnl,settings,helpdesk,username,email;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    String profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        init();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference();
        fetchData();
        your_chnnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TO check already existing channel
                check_HaveChannel();
            }
        });




    }

    private void check_HaveChannel() {
        reference.child("Channel").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Intent intent=new Intent(AccountActivity.this,MainActivity.class);
                    intent.putExtra("type","channel name");
                    startActivity(intent);
                }
                else {
                    Dialog dialog=new Dialog(AccountActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.channel_formation);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    EditText channel_name=dialog.findViewById(R.id.Channel_Name);
                    EditText channel_desp=dialog.findViewById(R.id.Channel_Name_Descp);
                    TextView channel_create=dialog.findViewById(R.id.create_channel);
                    channel_create.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name=channel_name.getText().toString();
                            String description=channel_desp.getText().toString();
                            if (name.isEmpty()|| description.isEmpty()){
                                Toast.makeText(AccountActivity.this,"Enter Required Fields",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                create_NewChannel(name,description,dialog);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
    //TO create channel and add to database
    private  void create_NewChannel(String name, String description, Dialog dialog){
        ProgressDialog progressDialog = new ProgressDialog(AccountActivity.this);
        progressDialog.setTitle("New Channel");
        progressDialog.setMessage("Creating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //store the date
        String date= DateFormat.getDateInstance().format(new Date());
        HashMap<String ,Object> map= new HashMap<>();
        map.put("channel name",name);
        map.put("Description",description);
        map.put("Joined Date",date);
        map.put("uid",user.getUid());
        map.put("Channel logo",profile);
        reference.child("Channel").child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this,name+"Channel has been created",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(AccountActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    //Fetch User Data from Database
    private void fetchData(){
        reference.child("Google Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name=snapshot.child("username").getValue().toString();
                    String emai=snapshot.child("email").getValue().toString();
                    profile=snapshot.child("profile").getValue().toString();
                    username.setText(name);
                    email.setText(emai);
                    try {
                        Picasso.get().load(profile).placeholder(R.drawable.accn).into(circleImageView);

                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }


    private  void init() {
        toolbar=findViewById(R.id.Account_tllbr);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        circleImageView=findViewById(R.id.user_prof_img);
        username=findViewById(R.id.user_chnnlname);
        email=findViewById(R.id.email);
        your_chnnl=findViewById(R.id.channel);
        settings=findViewById(R.id.settings);
        helpdesk=findViewById(R.id.helpdesk);

    }
}