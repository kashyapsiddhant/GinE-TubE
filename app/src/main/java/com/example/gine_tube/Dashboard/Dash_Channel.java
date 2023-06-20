package com.example.gine_tube.Dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gine_tube.Adapters.ViewPagerAdapter;
import com.example.gine_tube.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Dash_Channel extends Fragment {
    TextView user_channel_name;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    public Dash_Channel() {
        // Required empty public constructor
    }


    public static Dash_Channel newInstance() {
        Dash_Channel fragment = new Dash_Channel();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dash__channel, container, false);
        user_channel_name=view.findViewById(R.id.channel_UserName);
        tabLayout=view.findViewById(R.id.tab);
        viewPager=view.findViewById(R.id.Channel_ViewPager);
        initAdapter();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Channel");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name=snapshot.child("channel name").getValue().toString();
                    user_channel_name.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }

    private void initAdapter() {
        viewPagerAdapter= new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.add(new HomeDashFragment(),"HOME");
        viewPagerAdapter.add(new VideoDashFragment(),"VIDEO");
        viewPagerAdapter.add(new PlaylistDashFragment(),"PLAYLIST");
        viewPagerAdapter.add(new SubscriptionDashFragment(),"SUBSCRIPTION");
        viewPagerAdapter.add(new AboutDashFragment(),"ABOUT");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}