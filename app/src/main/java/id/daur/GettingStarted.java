package id.daur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class GettingStarted extends AppCompatActivity {
    private FirebaseAuth mAuth;

    int RC_SIGN_IN = 101;
    SignInButton signIn;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getting_started2);


//        if (restorePrefData()) {
//            Intent i = new Intent(this, Input2Activity.class);
//            startActivity(i);
//            finish();
//        }

    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;

    }

    public void next(View view) {

        Intent i = new Intent(this, loginActivity.class);
        startActivity(i);
        finish();
    }



}