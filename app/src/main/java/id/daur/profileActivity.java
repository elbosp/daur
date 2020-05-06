package id.daur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import id.daur.model.BmiRule;
import id.daur.model.UserData;

import android.app.DatePickerDialog;
import android.content.Context;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class profileActivity extends AppCompatActivity {
    Button signOutButton;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String activityLevel;
    private Spinner spinnerActivity;

    private RadioGroup rgGender;
    private RadioButton rbPria;
    private RadioButton rbWanita;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String [] readableMonth = { "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November",
            "Desember"};

    private DatabaseReference database;

    private EditText edtName;
    private EditText edtBirthday;
    public String birthdate;
    public String gender;
    private String name;

    private EditText etBeratBadan;
    private EditText etTinggiBadan;

    private int birthdateDay;
    private int birthdateMonth;
    private int birthdateYear;
    private int beratBadan;
    private int tinggiBadan;

    private UserData userData;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private int birtdateDay;
    private int birtdateMonth;
    private int birtdateYear;

    private Button buttonNext;

    private String uid;


    String parentRuleBmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        uid = sharedPref.getString("USER_ID", "null");

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

        mDatabaseReference = mFirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userData = dataSnapshot.getValue(UserData.class);
                getAllUserData(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected  void getAllUserData(UserData userData){
        String name = userData.getName();
        int age = userData.getAge();
        int tb = userData.getHeight();
        int bb = userData.getWeight();
        gender = userData.getGender();

        if(gender.equals("L")) {
            parentRuleBmi = "BMI_L";
        } else if (gender.equals("W")){
            parentRuleBmi = "BMI_W";
        }

        edtName.setText(name);
//        edtBirthday.setText(birtdateDay + "/" + birtdateMonth + "/" + birtdateYear);
        etTinggiBadan.setText(Integer.toString(tb));
        etBeratBadan.setText(Integer.toString(bb));
    }

    @Override
    protected void onStart(){
        super.onStart();

        edtName = (EditText)findViewById(R.id.et_nama);
        edtBirthday = (EditText)findViewById(R.id.et_ttl);

        rgGender = (RadioGroup)findViewById(R.id.rg_gender);
        rbPria = (RadioButton) findViewById(R.id.rb_pria);
        rbWanita = (RadioButton) findViewById(R.id.rb_wanita);

        etBeratBadan = (EditText)findViewById(R.id.et_bb);
        etTinggiBadan = (EditText)findViewById(R.id.et_tb);

        buttonNext = (Button)findViewById(R.id.bt_next);

        Spinner spinner = findViewById(R.id.spinnerAktivitas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.listAktivitas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        edtName.addTextChangedListener(registerTextWatcher);
        etTinggiBadan.addTextChangedListener(registerTextWatcher);
        etBeratBadan.addTextChangedListener(registerTextWatcher);

        edtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String nameInput = edtName.getText().toString().trim();
            String tinggiBadanInput = etTinggiBadan.getText().toString().trim();
            String beratBadanInput = etBeratBadan.getText().toString().trim();

            buttonNext.setEnabled(!nameInput.isEmpty() && !tinggiBadanInput.isEmpty() && !beratBadanInput.isEmpty() );

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void backToHome(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
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

    public void next(View view) {
        name = edtName.getText().toString();
        beratBadan = Integer.parseInt(etBeratBadan.getText().toString());
        tinggiBadan = Integer.parseInt(etTinggiBadan.getText().toString());

        int bmi = userData.calculateBmi(beratBadan, tinggiBadan);

        classificationBMI(bmi);
    }

    private void storeDataToFirebase(UserData userData) {

        final DatabaseReference daurRoot;
        daurRoot = FirebaseDatabase.getInstance().getReference();

        daurRoot.child("Users").child(uid).child("big_portion").setValue(userData.getBig_portion());
        daurRoot.child("Users").child(uid).child("bmi").setValue(userData.getBmi());
        daurRoot.child("Users").child(uid).child("body_status").setValue(userData.getBody_status());
        daurRoot.child("Users").child(uid).child("calorie_diet").setValue(userData.getCalorie_diet());
        daurRoot.child("Users").child(uid).child("calorie_intake").setValue(userData.getCalorie_intake());
        daurRoot.child("Users").child(uid).child("days_to_ideal").setValue(userData.getDays_to_ideal());
        daurRoot.child("Users").child(uid).child("height").setValue(userData.getHeight());
        daurRoot.child("Users").child(uid).child("ideal_weight").setValue(userData.getIdeal_weight());
        daurRoot.child("Users").child(uid).child("name").setValue(userData.getName());
        daurRoot.child("Users").child(uid).child("small_portion").setValue(userData.getSmall_portion());
        daurRoot.child("Users").child(uid).child("weight").setValue(userData.getWeight()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(profileActivity.this,"Data Anda berhasil tersimpan", Toast.LENGTH_LONG).show();

                } else {
//                    loadingBar.dismiss();

                    Toast.makeText(profileActivity.this,"Error: Terjadi kesalahan teknis", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

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
                Toast.makeText(profileActivity.this,"gakebaca"+uid, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserStatus(String myBodyStatus) {
//        userName = edtName.getText().toString();
        int umur = userData.getAge();
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
}
