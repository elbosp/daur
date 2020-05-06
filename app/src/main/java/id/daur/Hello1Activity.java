package id.daur;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Hello1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello1);
    }

    public void next(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
}
