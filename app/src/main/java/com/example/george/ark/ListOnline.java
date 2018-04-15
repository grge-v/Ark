package com.example.george.ark;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.george.ark.activites.LoginActivity;
import com.example.george.ark.activites.TrackingActivity;
import com.example.george.ark.models.Tracking;
import com.example.george.ark.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListOnline extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int UPDATE_INTERVAL = 3000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISTANCE = 10;
    private RecyclerView mUsersList;


    private LinearLayoutManager mLayoutManager;
    private DatabaseReference onlineRef;
    private DatabaseReference counterRef;
    private DatabaseReference currentRef;
    private DatabaseReference locations;
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter;


    //Location
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApi;
    private Location mLastLocation;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);

        mAuth = FirebaseAuth.getInstance();

        mLayoutManager = new LinearLayoutManager(this);

        locations = FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("LastOnline");
        currentRef = FirebaseDatabase.getInstance().getReference("LastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {


            ActivityCompat.requestPermissions(this, new String[]{

                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGooglApiClient();
                createLocalRequest();
                displayLocation();

            }
        }


        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        setupRef();
        updateRef();


    }

    private void sendToStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.delete();

        if (currentUser == null) {

            Intent startIntent = new Intent(ListOnline.this, LoginActivity.class);
            startActivity(startIntent);
            finish();
        } else {
            Toast.makeText(ListOnline.this,"ldsnflsdfdsff",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //setupRef();
        updateRef();

    }

    private void displayLocation() {

        // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Добавление элементов в БД

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi);
        if (mLastLocation != null) {

            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail()
                            , FirebaseAuth.getInstance().getCurrentUser().getUid().toString()
                            , String.valueOf(mLastLocation.getLatitude())
                            , String.valueOf(mLastLocation.getLongitude())));
        } else {

        }
    }

    private void createLocalRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void buildGooglApiClient() {
        mGoogleApi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApi.connect();

    }


    // Проверка подключения к API
    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RES_REQUEST).show();
            }

            return false;

        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGooglApiClient();
                        createLocalRequest();
                        displayLocation();
                    }
                }
            }
        }
    }

    private void updateRef() {


        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(

                User.class,
                R.layout.card_fragment,
                UserViewHolder.class,
                counterRef

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder usersViewHolder, final User users, int position) {


                usersViewHolder.username.setText(users.getEmail());

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!users.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                           // Toast.makeText(ListOnline.this,mLastLocation.getLatitude()+ " " + mLastLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                            Intent profileIntent = new Intent(ListOnline.this, TrackingActivity.class);
                            profileIntent.putExtra("email", users.getEmail());
                            profileIntent.putExtra("lat", mLastLocation.getLatitude());
                            profileIntent.putExtra("lng", mLastLocation.getLongitude());
                            startActivity(profileIntent);
                        }

                    }
                });


            }
        };


        adapter.notifyDataSetChanged();
        mUsersList.setAdapter(adapter);

    }

    private void setupRef() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentRef.onDisconnect().removeValue(); // провверка соединения
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())                              // inflate model user
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "online"));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        } else if (mGoogleApi != null) {
                mGoogleApi.connect();
            }
        }






    @Override
    protected void onStop() {

        if (mGoogleApi != null)
            mGoogleApi.disconnect();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentRef.removeValue();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApi, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApi.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        setupRef();
        // updateRef();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        displayLocation();
    }
}
