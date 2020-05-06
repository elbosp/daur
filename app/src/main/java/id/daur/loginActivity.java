package id.daur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPass;
    private RelativeLayout layout;
    private Button login;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText)findViewById(R.id.et_email);
        edtPass = (EditText)findViewById(R.id.et_password);
        layout = (RelativeLayout)findViewById(R.id.layout);
        login = (Button)findViewById(R.id.bt_next);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            updateUI(currentUser);
        }
        else {
        }

        edtEmail.addTextChangedListener(registerTextWatcher);
        edtPass.addTextChangedListener(registerTextWatcher);

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackgroundResource( R.drawable.et_fill_text);
                }
                else {
                    v.setBackgroundResource( R.drawable.et_fill_text_disable);

                }
            }
        });

        edtPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackgroundResource( R.drawable.et_fill_text);
                }
                else {
                    v.setBackgroundResource( R.drawable.et_fill_text_disable);
                }
            }
        });
    }


    public void login(View view) {
        hideKeyboard(view);

        loadingBar.setTitle("Sedang Masuk...");
        loadingBar.setMessage("Sebentar lagi aku akan menjadi asisten makan kamu");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingBar.dismiss();
                            Log.d("firebase", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingBar.dismiss();

                            alertDialog A = new alertDialog();
                            A.show(getSupportFragmentManager(), "selsai");

                            Log.w("firebase", "signInWithEmail:failure", task.getException());

//                            updateUI(null);
                        }


                    }
                });

    }

    private void updateUI(FirebaseUser currentUser) {
        String uid = currentUser.getUid();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("USER_ID", uid);
        editor.commit();

        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    public void goToRegister(View view) {
        Intent i = new Intent(this, registerActivity.class);
        startActivity(i);
    }

    public void hideKeyboard(View view) {
        // Get the input method manager
        InputMethodManager inputMethodManager = (InputMethodManager)
                view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // Hide the soft keyboard
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String nameInput = edtEmail.getText().toString().trim();
            String birthDateInput = edtPass.getText().toString().trim();

            login.setEnabled(!nameInput.isEmpty() && !birthDateInput.isEmpty());;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void screenOnclick(View view) {
        hideKeyboard(view);
    }

    public static class alertDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Tidak dapat masuk").setMessage(Html.fromHtml("<font color='#A0A4A8'> Email dan password Anda salah ! \nSilahkan masukkan kembali </font>"))
                    .setPositiveButton(Html.fromHtml("<font color='#26BEB4'> Tutup </font"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
