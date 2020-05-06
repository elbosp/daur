package id.daur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class registerActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etKonfirmPassword;
    private Button regButton;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail = (EditText)findViewById(R.id.et_email);
        etPassword = (EditText)findViewById(R.id.et_password);
        etKonfirmPassword = (EditText)findViewById(R.id.et_konfirm_password);

        regButton = (Button)findViewById(R.id.bt_next);

        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onStart(){
        super.onStart();

        etEmail.addTextChangedListener(registerTextWatcher);
        etPassword.addTextChangedListener(registerTextWatcher);
        etKonfirmPassword.addTextChangedListener(registerTextWatcher);

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        etKonfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    public void register(View view) {
        hideKeyboard(view);

        loadingBar.setTitle("Sedang Mendaftar...");
        loadingBar.setMessage("Sebentar lagi aku akan menjadi asistenmu");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

//        Toast.makeText(registerActivity.this, email+pass,Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingBar.dismiss();
                            Log.d("firebase", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingBar.dismiss();

                            registerActivity.alertDialog A = new registerActivity.alertDialog();
                            A.show(getSupportFragmentManager(), "selesai");

                            Log.w("firebase", "createUserWithEmail:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        String uid = user.getUid();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("USER_ID", uid);
        editor.commit();

        Intent i = new Intent(this, Input1Activity.class);
        startActivity(i);
    }

    public void goToLogin(View view) {
        Intent i = new Intent(this, loginActivity.class);
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

            String emailInput = etEmail.getText().toString().trim();
            String passInput = etPassword.getText().toString().trim();
            String konfirmPassInput = etKonfirmPassword.getText().toString().trim();

            regButton.setEnabled(!emailInput.isEmpty() && !passInput.isEmpty() && !konfirmPassInput.isEmpty());;
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
            builder.setTitle("Tidak dapat mendaftar").setMessage(Html.fromHtml("<font color='#A0A4A8'> Email sudah terdaftar! \nsilahkan ke halaman login </font>"))
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
