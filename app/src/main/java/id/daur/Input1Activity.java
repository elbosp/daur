package id.daur;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;

public class Input1Activity extends AppCompatActivity {
    private EditText edtName;
    private EditText edtBirthday;
    private EditText edtRbValue;

    private RadioGroup rgGender;
    private RadioButton rbGender;
    private RadioButton rbPria;
    private RadioButton rbWanita;

    private Button nextButton;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String [] readableMonth = { "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November",
            "Desember"};

    private DatabaseReference database;

    public String birthdate;
    public String gender;

    private int birthdateDay;
    private int birthdateMonth;
    private int birthdateYear;

    String parentRuleBmi = "BMI_L";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input1);
    }

    @Override
    protected void onStart(){
        super.onStart();

        edtName = (EditText)findViewById(R.id.et_nama);
        edtBirthday = (EditText)findViewById(R.id.et_ttl);

        rgGender = (RadioGroup)findViewById(R.id.rg_gender);
        rbPria = (RadioButton) findViewById(R.id.rb_pria);
        rbWanita = (RadioButton) findViewById(R.id.rb_wanita);

        nextButton = (Button)findViewById(R.id.bt_next);

        edtName.addTextChangedListener(registerTextWatcher);
        edtBirthday.addTextChangedListener(registerTextWatcher);

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

        edtBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
            String birthDateInput = edtBirthday.getText().toString().trim();

            nextButton.setEnabled(!nameInput.isEmpty() && !birthDateInput.isEmpty() );

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void next(View view) {

        registerUser();
//        savePrefsData();

        Intent i = new Intent(this, Input2Activity.class);
        startActivity(i);

//        finish();
    }

//    private void savePrefsData() {
//        edtName = (EditText)findViewById(R.id.et_nama);
//        String name = edtName.getText().toString();
//
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();

//        editor.putBoolean("isIntroOpnend",true);
//        editor.putString("userId", name);
//        editor.commit();
//    }

    public void setBirthday(View v) {
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Input1Activity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year,month,day
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                birthdateDay = day;
                birthdateMonth = month+1;
                birthdateYear = year;

                String displayedMonth = readableMonth[month];
                String date = day + "/" + displayedMonth + "/" + year;

                edtBirthday.setText(date);

            }
        };
    }

    public void registerUser(){
        String name = edtName.getText().toString();

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        String uid = sharedPref.getString("USER_ID", "null");

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("USER_NAME", name);
        editor.putInt("USER_BIRTHDATE_DAY", birthdateDay);
        editor.putInt("USER_BIRTHDATE_MONTH", birthdateMonth);
        editor.putInt("USER_BIRTHDATE_YEAR", birthdateYear);
        editor.putString("USER_GENDER", gender);
        editor.putString("USER_PARENT_BMI_RULE", parentRuleBmi);
        editor.commit();
    }

    public void rbGenderOnClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.rb_pria:
                if (checked) {
                    rbPria.setTextColor(this.getResources().getColor(R.color.colorBlack80));
                    rbWanita.setTextColor(this.getResources().getColor(R.color.colorBlack60));

                    gender="L";
                    parentRuleBmi = "BMI_L";
                }
                break;
            case R.id.rb_wanita:
                if (checked) {
                    rbWanita.setTextColor(this.getResources().getColor(R.color.colorBlack80));
                    rbPria.setTextColor(this.getResources().getColor(R.color.colorBlack60));

                    gender="W";
                    parentRuleBmi = "BMI_W";
                }
                break;
        }
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

    public void backToHome(View view) {
    }
}

