package id.daur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import id.daur.model.BmiRule;
import id.daur.model.UserData;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Input2Activity extends AppCompatActivity {

    private String activityLevel;
    private Spinner spinnerActivity;

    private EditText etBeratBadan;
    private EditText etTinggiBadan;

    private DatabaseReference rootRef;

    private String uid;
    private String name;
    private int day;
    private int month;
    private int year;
    private String parentRuleBmi;
    private String gender;
    private int beratBadan;
    private int tinggiBadan;

    private Button nextButton;
    private UserData userData;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input2);

        userData = new UserData();

        etBeratBadan = (EditText)findViewById(R.id.et_bb);
        etTinggiBadan = (EditText)findViewById(R.id.et_tb);

        loadingBar = new ProgressDialog(this);

        spinnerActivity = (Spinner)findViewById(R.id.spinnerAktivitas);

        spinnerActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activityLevel = parent.getItemAtPosition(position).toString();
                activityLevel = ""+activityLevel.charAt(0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);

        uid = sharedPref.getString("USER_ID", "null");
        name = sharedPref.getString("USER_NAME", "null");
        day = sharedPref.getInt("USER_BIRTHDATE_DAY", 0);
        month = sharedPref.getInt("USER_BIRTHDATE_MONTH", 0);
        year = sharedPref.getInt("USER_BIRTHDATE_YEAR", 0);
        gender = sharedPref.getString("USER_GENDER", "null");
        parentRuleBmi = sharedPref.getString("USER_PARENT_BMI_RULE", "null");
//
        Log.d("tes", " "+parentRuleBmi);

    }

    @Override
    protected void onStart(){
        super.onStart();

        Spinner spinner = findViewById(R.id.spinnerAktivitas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.listAktivitas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        nextButton = (Button)findViewById(R.id.bt_next);

        etTinggiBadan.addTextChangedListener(registerTextWatcher);
        etBeratBadan.addTextChangedListener(registerTextWatcher);

        etTinggiBadan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        etBeratBadan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    public void next(View view) {
        loadingBar.setTitle("Sedang Masuk...");
        loadingBar.setMessage("Tolong tunggu sebentar");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        beratBadan = Integer.parseInt(etBeratBadan.getText().toString());
        tinggiBadan = Integer.parseInt(etTinggiBadan.getText().toString());

        int userBMI = userData.calculateBmi(beratBadan, tinggiBadan);
        Log.d("bmi", ""+userBMI);
        classificationBMI(userBMI);
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String nameInput = etTinggiBadan.getText().toString().trim();
            String birthDateInput = etBeratBadan.getText().toString().trim();

            nextButton.setEnabled(!nameInput.isEmpty() && !birthDateInput.isEmpty() );

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void classificationBMI(final int userBMI) {
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef = rootRef.child(parentRuleBmi);
        Log.d("tes", ""+rootRef);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String userBodyStatus;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot rule: dataSnapshot.getChildren()) {
                    BmiRule bmiRule = rule.getValue(BmiRule.class);

                    int topLimit = bmiRule.getBatas_atas();
                    int bottomLimit = bmiRule.getBatas_bawah();

                    if (userBMI >= bottomLimit && userBMI <= topLimit) {
                        userBodyStatus = bmiRule.getStatus();
                        break;
                    }
                }
                setUserStatus(userBodyStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Input2Activity.this,"gakebaca"+uid, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserStatus(String myBodyStatus) {
//        userName = edtName.getText().toString();

        int umur = userData.calculateAge(day, month, year);
        int cal = userData.calculateCalorieIntake(gender, activityLevel, beratBadan, tinggiBadan, umur);
        int calDiet = userData.calculateCalorieDiet(cal, myBodyStatus);
        int bigPortion = userData.calculateBigPortion(calDiet);
        int smallPortion = userData.calculateSmallPortion(calDiet);
        int ideal = userData.calculateIdealWeight(tinggiBadan, gender);
        int dayLeft = userData.calculateDayLeft(ideal, beratBadan);
        int bmiBody = userData.calculateBmi(beratBadan,tinggiBadan);
//
        userData.setName(name);
        userData.setWeight(beratBadan);
        userData.setHeight(tinggiBadan);
        userData.setGender(gender);
        userData.setAge(umur);
        userData.setBig_portion(bigPortion);
        userData.setSmall_portion(smallPortion);
        userData.setDays_to_ideal(dayLeft);
        userData.setIdeal_weight(ideal);
        userData.setBmi(bmiBody);
        userData.setBody_status(myBodyStatus);
        userData.setCalorie_diet(calDiet);
        userData.setCalorie_intake(cal);

        storeDataToFirebase(userData);
//

//        Toast.makeText(MainActivity.this,"Calorie Diet: "+ calDiet+ " big portion: "+ bigPortion + " small portion: "+ smallPortion, Toast.LENGTH_LONG).show();
//        Toast.makeText(MainActivity.this,"Nama: "+userName+" BB: "+ userWeight + " TB: "+userHeight+" Usia: "+ age+" Status: "+ myBodyStatus + " Gender: "+gender+" Activity: "+activityLevel, Toast.LENGTH_LONG).show();
    }

    private void storeDataToFirebase(UserData userData) {

        final DatabaseReference daurRoot;
        daurRoot = FirebaseDatabase.getInstance().getReference();

        daurRoot.child("Users").child(uid).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
//                    loaditngBar.dismiss();

                    loadingBar.dismiss();
                    Toast.makeText(Input2Activity.this,"Selamat Anda berhasil terdaftar", Toast.LENGTH_LONG).show();
                    updateUI();


                } else {
//                    loadingBar.dismiss();

                    Toast.makeText(Input2Activity.this,"Error: Unsuccessfull Adding to Firebase", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void screenOnclick(View view) {
        hideKeyboard(view);
    }

    public void hideKeyboard(View view) {
        // Get the input method manager
        InputMethodManager inputMethodManager = (InputMethodManager)
                view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // Hide the soft keyboard
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void updateUI(){
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

}
