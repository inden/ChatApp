package com.in.den.android.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MainTabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private MyBroadCastReceiver broadcastReceiver;
    private int PROFILE_REQUEST = 1;
    private SharedPreferenceOp sharedPreferenceOp;
    private TextView textViewUserName;
    private ImageView imageViewUserIcon;
    private FireBaseOperation fireBaseOperation;
    private String myUserKey ="";
    private String USERNAME = "";
    private String TAG = MainTabActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(R.string.maintitle);


        //--------------------------
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            else {
                Log.d(TAG, "write external storage granted");
            }
        }

        //----------------------------------------
        sharedPreferenceOp = new SharedPreferenceOp(this);
        //----------------------------------------
        fireBaseOperation = new FireBaseOperation();
        fireBaseOperation.setSharedPreferenceOp(sharedPreferenceOp);
        requestUsername();
        //---------------------------

        textViewUserName = (TextView) findViewById(R.id.textViewUsername);
        imageViewUserIcon = (ImageView) findViewById(R.id.imageViewUserIcon);
        setUser();

        imageViewUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputProfil();
            }
        });
        broadcastReceiver = new MyBroadCastReceiver();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(0);

        tabLayout.addOnTabSelectedListener(getOnTabSelectedListener());

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("INVITED_USERS_ACTION");
        intentFilter.addAction("SELECT_USERS_ACTION");
        intentFilter.addAction("REGISTER_TOPIC_ACTION");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                intentFilter
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void setUser() {
        textViewUserName.setText(sharedPreferenceOp.getUserName());

        imageViewUserIcon.invalidate();
        if(sharedPreferenceOp.isIconFile() && !sharedPreferenceOp.getRealpath().isEmpty()
                ) {
            Picasso.with(this).load(Uri.parse(sharedPreferenceOp.getRealpath())).resize(30,30).
                    centerCrop().into(imageViewUserIcon);
        }
        else {
            Picasso.with(this).load(R.drawable.neko_shiro).resize(30,30).
                    centerCrop().into(imageViewUserIcon);
        }
    }

    private void requestUsername() {
        USERNAME = sharedPreferenceOp.getUserName();
        myUserKey = sharedPreferenceOp.getUserkey();
        if (!USERNAME.isEmpty() && !myUserKey.isEmpty()) {
           return;
        }

        inputProfil();
    }

    private void inputProfil() {
        Intent intent = new Intent(this, SettingProfileActivity.class);
        startActivityForResult(intent,  PROFILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_REQUEST && resultCode == RESULT_OK && data != null ) {
            String name = data.getStringExtra("name");
            String email = data.getStringExtra("email");
            String password = data.getStringExtra("password");
            boolean localicon = data.getBooleanExtra("localicon", false);
            Uri iconuri = data.getData();
            String realpath = sharedPreferenceOp.getRealpath();
            boolean biconchanged = false;

            if(localicon && iconuri != null) {

                realpath = iconuri.toString();

                String oldrealpath = sharedPreferenceOp.getRealpath();

                if(!realpath.equals(oldrealpath)) {
                    biconchanged = true;
                }
            }


            sharedPreferenceOp.setInfo(name,email,password,localicon, realpath);
            setUser();
            //---------------------------------------------------------------
            // create the user on firebase
            //---------------------------------------------------------------
            fireBaseOperation.findUserByEmail(email, name, biconchanged, iconuri);


        }
    }

    private TabLayout.OnTabSelectedListener getOnTabSelectedListener() {

        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==2) {
                    ListUserFragment listUserFragment =(ListUserFragment) mSectionsPagerAdapter
                            .getRegisteredFragment(2);
                    if(listUserFragment != null) {
                        listUserFragment.changeCheckBoxVisible(false);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    /******************************************************************************
        Inner class SectionsPagerAdapter
     *******************************************************************************/

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SparseArray<Fragment> myPagerFragments= new SparseArray<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            myPagerFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            myPagerFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return myPagerFragments.get(position);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
           Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new TopicsFragment();
                    break;
                case 1:
                    fragment = new NewTopicFragment();
                    break;
                case 2:
                    ListUserFragment listUserFragment = new ListUserFragment();
                    fragment = listUserFragment;
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_topics);
                case 1:
                    return getString(R.string.tab_chat);
                case 2:
                    return getString(R.string.tab_user);
            }
            return null;
        }
    }

    /******************************************************************************
        Inner class
     *******************************************************************************/
    public class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
            intentFilter.addAction("INVITED_USERS_ACTION");
        intentFilter.addAction("SELECT_USERS_ACTION");
             */
            if(intent.getAction().equals("INVITED_USERS_ACTION")) {
                ArrayList<User> invites = (ArrayList<User>) intent.getSerializableExtra("invited");

                mViewPager.setCurrentItem(1);

                NewTopicFragment fragment = (NewTopicFragment) mSectionsPagerAdapter
                        .getRegisteredFragment(1);
                if (fragment != null) fragment.resetUserList(invites);
            }
            else if(intent.getAction().equals("SELECT_USERS_ACTION")) {

                boolean checkboxvisible = intent.getBooleanExtra("checkboxvisible", true);

                mViewPager.setCurrentItem(2);

                ListUserFragment listUserFragment =(ListUserFragment) mSectionsPagerAdapter
                        .getRegisteredFragment(2);
                listUserFragment.changeCheckBoxVisible(checkboxvisible);
            }
            else if(intent.getAction().equals("REGISTER_TOPIC_ACTION")) {

                HashMap<String, Object> map =
                        (HashMap<String, Object>) intent.getSerializableExtra("map");

                fireBaseOperation.createNewTopic(map);

                mViewPager.setCurrentItem(0);
            }
        }
    }
}
