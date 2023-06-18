package com.example.gine_tube.Activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gine_tube.Dashboard.Dash_Channel;
import com.example.gine_tube.R;
import com.example.gine_tube.fragments.ExploreFragment;
import com.example.gine_tube.fragments.HomeFragment;
import com.example.gine_tube.fragments.LibraryFragment;
import com.example.gine_tube.fragments.SubscriptionFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView userprofile_image;
    Fragment fragment;
    FrameLayout frameLayout;
    AppBarLayout appBarLayout;

    BottomNavigationView bottomNavigationView;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN=100;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        toolbar=findViewById(R.id.tllbr);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");



        bottomNavigationView = findViewById(R.id.bttn_nvgtn);
        frameLayout=findViewById(R.id.frm_bttn_lyt);
        appBarLayout=findViewById(R.id.appbr);
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
         userprofile_image = findViewById(R.id.profile_img);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient=GoogleSignIn.getClient(this, gso);

        getProfileImg();
        userprofile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user !=null){
                    startActivity(new Intent(MainActivity.this,AccountActivity.class));
                    getProfileImg();
                }
                else {
                    userprofile_image.setImageResource(R.drawable.accn);
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(true);
                    ViewGroup viewGroup=findViewById(android.R.id.content);
                    View view=LayoutInflater.from(getApplicationContext()).inflate(R.layout.signin_dialogue,viewGroup,false);
                    builder.setView(view);
                    TextView txt_google_signIn=view.findViewById(R.id.txt_google_sigIn);
                    txt_google_signIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=googleSignInClient.getSignInIntent();
                            startActivityForResult(intent,RC_SIGN_IN);


                        }
                    });
                    builder.create().show();
                }

            }
        });
        showFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment(new HomeFragment());
                        break;
                    case R.id.explr:
                        selectedFragment(new ExploreFragment());
                        break;
                    case R.id.pblsh:
                        Toast.makeText(MainActivity.this,"Add A Video", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sbscptn:
                        selectedFragment(new SubscriptionFragment());
                        break;
                    case R.id.lbry:
                        selectedFragment(new LibraryFragment());
                        break;

                }
                return true;
            }
            });

        bottomNavigationView.setSelectedItemId(R.id.major);


        }
        //Add user in firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("username",account.getDisplayName());
                                    map.put("email",account.getEmail());
                                    map.put("profile",String.valueOf(account.getPhotoUrl()));
                                    map.put("uid",firebaseUser.getUid());
                                    map.put("Search",account.getDisplayName().toLowerCase());

                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Google Users");
                                    reference.child(firebaseUser.getUid()).setValue(map);


                                }
                                else {
                                    Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();

            }
        }
    }
    private void selectedFragment(Fragment fragment) {
        setStatusBarColor("#FFFFFF");
        appBarLayout.setVisibility(View.VISIBLE);
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frm_bttn_lyt,fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.notify:
                Toast.makeText(this,"Notification", Toast.LENGTH_SHORT).show();
                break;
            case R.id.srch:
                Toast.makeText(this,"Search", Toast.LENGTH_SHORT).show();
                break;


            default:
                return super.onOptionsItemSelected(item);

        }
        return false;
    }
    //fetch pic from firebase
    private void  getProfileImg(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Google Users");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String pic=snapshot.child("profile").getValue().toString();
                    Picasso.get().load(pic).placeholder(R.drawable.accn).into(userprofile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
    private  void showFragment(){
        String type=getIntent().getStringExtra("type");
        if (type!=null){
            switch (type){
                case "channel name":
                    setStatusBarColor("#99FF0000");
                    appBarLayout.setVisibility(View.GONE);
                    fragment= Dash_Channel.newInstance();
                    break;
            }
            if (fragment!=null){
                Toast.makeText(this, "Till here fine", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager=getSupportFragmentManager();
               fragmentManager.beginTransaction().replace(R.id.frm_bttn_lyt,fragment).commit();
            }
            else {
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void setStatusBarColor(String color ){
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }
}