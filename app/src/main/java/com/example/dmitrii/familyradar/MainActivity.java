package com.example.dmitrii.familyradar;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.dmitrii.familyradar.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int SIGN_IN_CODE = 777;

    private GoogleApiClient googleApiClient;
    private ActivityMainBinding binding;
    private GoogleSignInOptions googleSignInOptions;
    private FirebaseAuth mAuth;
    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    private DatabaseReference dataBaseUsers;
    private FirebaseUser firebaseUser;
    private String email, name;
    private Users users = new Users();

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //verification method is user authorized
        authorizationCheck();

        binding.buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    // Check get data user
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            signIn();
        } else {
            checkUsers(firebaseUser.getUid());

        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent,SIGN_IN_CODE);

    }

    // Auth to firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWitchCredential:onComplite:" + task.isSuccessful());
                    }
                });
    }

    // Check get log user
    private void getLogUser(FirebaseUser user) {

        if (user.getUid() != null) {
            users.setUid(user.getUid());
        }
        if (user.getDisplayName() != null) {
            users.setName(user.getDisplayName());
        }
        if (user.getPhotoUrl() != null) {
            users.setImage(user.getPhotoUrl().toString());
        }

        if (user.getEmail() != null) {
            users.setEmail(user.getEmail());
        }

    }

    // Create database and write
    private void checkUsers(String uid){

        dataBaseUsers = dataBase.getReference("Users");
        DatabaseReference userRef = dataBaseUsers.child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    dataBaseUsers.child(firebaseUser.getUid()).setValue(firebaseUser.getUid());

                    getLogUser(firebaseUser);

                    dataBaseUsers.child(firebaseUser.getUid()).updateChildren(users.toMap());



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //verification of authorization on the google side
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }
    }

    //verification of authorization result
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            goMapScreen();
        } else {
            Toast.makeText(this, R.string.not_log_in, Toast.LENGTH_SHORT).show();
        }
    }

    //method of transition to another activity
    private void goMapScreen() {
        Intent intent = new Intent(this, ScreenMapActivity.class);
        startActivity(intent);
    }

    //verification method is user authorized
    public void authorizationCheck(){
        if(GoogleSignIn.getLastSignedInAccount(getApplicationContext()) != null){
            Intent intent = new Intent(this, ScreenMapActivity.class);
            startActivity(intent);
        }
    }
}


