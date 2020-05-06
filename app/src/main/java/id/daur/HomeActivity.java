package id.daur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.daur.model.FeedRule;
import id.daur.model.FoodComposition;
import id.daur.model.Taskz;
import id.daur.model.UserData;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerView2, recyclerView3, recyclerViewSarapan;
    private ContentAdapter contentAdapter, contentAdapter2, contentAdapter3, contentAdapterSarapan;
    private ArrayList<FoodComposition> contentArrayList, contentArrayList2, contentArrayList3, contentArrayListSarapan;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private UserData userData;
    private String mainMenuId, uid, currentDate, snackMenuId;
    private Taskz taskz;
    private SwipeController swipeController, swipeController2, swipeController3, swipeControllerSarapan;
    private int sarapanStatus, makanSiangStatus, makanMalamStatus;

    private static String keluar = "false";

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadingBar = new ProgressDialog(this);

        loadingBar.setTitle("Menyiapkan menu makanan Anda...");
        loadingBar.setMessage("Tetap Ideal bersama Daur");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);

        uid = sharedPref.getString("USER_ID", "null");
//        uid = "61TDlv3cL6No1Xsr1ZzFe098Czs2";

        mDatabaseReference = mFirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userData = dataSnapshot.getValue(UserData.class);
                userMenuClassification(userData);
                setGreetingCard(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!(snapshot.hasChild("Users/" + uid + "/task/" + currentDate))) {
                    Taskz t = new Taskz();
                    try {
                    mDatabaseReference.child("Users").child(uid).child("task").child(currentDate)
                            .setValue(t);
                    } finally {
                        getTask();
                    }
                } else {
                    getTask();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTask() {
        mDatabaseReference.child("Users").child(uid).child("task").child(currentDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        taskz = dataSnapshot.getValue(Taskz.class);
                        userGenerateTask(taskz);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void userGenerateTask(Taskz taskz) {
        sarapanStatus = taskz.getSarapan();
        makanSiangStatus = taskz.getMakan_siang();
        makanMalamStatus = taskz.getMakan_malam();
    }

    private void setGreetingCard(UserData userData) {
        String name = userData.getName();
        String bodyStatus = userData.getBody_status();
        int bmi = userData.getBmi();
        int bigPortion = userData.getBig_portion();
        int smallPortion = userData.getSmall_portion();
        int calorieDiet = userData.getCalorie_diet();
        int calorieIntake = userData.getCalorie_intake();


        TextView tvName = findViewById(R.id.greeting);
        TextView tvBodyStatus = findViewById(R.id.body_status);
//        TextView tvBmi = findViewById(R.id.bmi);
//        TextView tvBigPortion = findViewById(R.id.big_portion);
//        TextView tvSmallPortion = findViewById(R.id.small_portion);
//        TextView tvCalorieDiet = findViewById(R.id.calorie_diet);
//        TextView tvCalorieIntake = findViewById(R.id.calorie_intake);

        tvName.setText("Hai, " + name);
        tvBodyStatus.setText("Kamu terdeteksi " + bodyStatus + ", disarankan untuk mengkonsumsi maksimal " + calorieDiet + " Kcal perhari!");
//        tvBmi.setText("Index Masa Tubuh: " + bmi);
//        tvCalorieDiet.setText("disarankan untuk mengkonsumsi " + calorieDiet + " Kcal perhari");
//        tvBigPortion.setText("Porsi Makan Besar: " + bigPortion);
//        tvSmallPortion.setText("Porsi Makan Kecil: " + smallPortion);
//        tvCalorieIntake.setText("Kebutuhan Kalori: " + calorieIntake);
    }

    private void userMenuClassification(UserData userData) {
        final int bigPortion = userData.getBig_portion();
        final int smallPortion = userData.getSmall_portion();

        mDatabaseReference.child("Aturan_Makan").child("Makan_Besar")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot rule: dataSnapshot.getChildren()) {
                            FeedRule feedRule = rule.getValue(FeedRule.class);

                            int topLimit = feedRule.getBatas_atas();
                            int bottomLimit = feedRule.getBatas_bawah();

                            if(bigPortion >= bottomLimit && bigPortion <= topLimit) {
                                mainMenuId = feedRule.getId_menu();
                                Log.i("menu", mainMenuId);
                                searchMainMenu(mainMenuId);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        });

        mDatabaseReference.child("Aturan_Makan").child("Makan_Kecil").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot rule: dataSnapshot.getChildren()) {
                    FeedRule feedRule = rule.getValue(FeedRule.class);

                    int topLimit = feedRule.getBatas_atas();
                    int bottomLimit = feedRule.getBatas_bawah();

                    if(smallPortion >= bottomLimit && smallPortion <= topLimit) {
                        snackMenuId = feedRule.getId_menu();
//                        Log.i("menu makan kecil", smallPortionMenu);
                        searchSnackMenu(snackMenuId);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchSnackMenu(String snackMenuId) {
        mDatabaseReference.child("Makanan").child("Makan_Kecil").child(snackMenuId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contentArrayListSarapan = new ArrayList<>();
                        for(DataSnapshot food: dataSnapshot.getChildren()) {
                            FoodComposition myFood = food.getValue(FoodComposition.class);
                            contentArrayListSarapan.add(myFood);
                        }
                        setSnackMenu(contentArrayListSarapan);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setSnackMenu(final ArrayList<FoodComposition> contentArrayListSarapan) {
        loadingBar.dismiss();
        contentAdapterSarapan = new ContentAdapter(contentArrayListSarapan, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerViewSarapan = findViewById(R.id.content_sarapan);
        recyclerViewSarapan.setLayoutManager(layoutManager);
        recyclerViewSarapan.setAdapter(contentAdapterSarapan);

        swipeControllerSarapan = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                contentArrayListSarapan.clear();
                setMainMenu(contentArrayListSarapan);
            }

            @Override
            public void onLeftClicked(int position) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(id.daur.HomeActivity.this,
                        Uri.parse(contentArrayListSarapan.get(position).getDetail_link()));
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeControllerSarapan);
        itemTouchhelper.attachToRecyclerView(recyclerViewSarapan);

        recyclerViewSarapan.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeControllerSarapan.onDraw(c);
            }
        });
    }

    private void searchMainMenu(String mainMenuId) {
        mDatabaseReference.child("Makanan").child("Makan_Besar").child(mainMenuId).child("Sarapan")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contentArrayList = new ArrayList<>();
                        for(DataSnapshot food: dataSnapshot.getChildren()) {
                            FoodComposition myFood = food.getValue(FoodComposition.class);
                            contentArrayList.add(myFood);
                        }
                        setMainMenu(contentArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        });

        mDatabaseReference.child("Makanan").child("Makan_Besar").child(mainMenuId).child("Makan_Siang")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contentArrayList2 = new ArrayList<>();
                        for(DataSnapshot food: dataSnapshot.getChildren()) {
                            FoodComposition myFood = food.getValue(FoodComposition.class);
                            contentArrayList2.add(myFood);
                        }
                        setMainMenu2(contentArrayList2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        });

        mDatabaseReference.child("Makanan").child("Makan_Besar").child(mainMenuId).child("Makan_Malam")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contentArrayList3 = new ArrayList<>();
                        for(DataSnapshot food: dataSnapshot.getChildren()) {
                            FoodComposition myFood = food.getValue(FoodComposition.class);
                            contentArrayList3.add(myFood);
                        }
                        setMainMenu3(contentArrayList3);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        });
    }

    private void setMainMenu(final ArrayList<FoodComposition> contentArrayList) {
        contentAdapter = new ContentAdapter(contentArrayList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.content);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(contentAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                contentArrayList.clear();
                setMainMenu(contentArrayList);
            }

            @Override
            public void onLeftClicked(int position) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(id.daur.HomeActivity.this,
                        Uri.parse(contentArrayList.get(position).getDetail_link()));
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    private void setMainMenu2(final ArrayList<FoodComposition> contentArrayList2) {
        contentAdapter2 = new ContentAdapter(contentArrayList2, this);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);

        recyclerView2 = findViewById(R.id.content_siang);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(contentAdapter2);

        swipeController2 = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                contentArrayList2.clear();
                setMainMenu2(contentArrayList2);
            }

            @Override
            public void onLeftClicked(int position) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(id.daur.HomeActivity.this,
                        Uri.parse(contentArrayList2.get(position).getDetail_link()));
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController2);
        itemTouchhelper.attachToRecyclerView(recyclerView2);

        recyclerView2.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController2.onDraw(c);
            }
        });
    }

    private void setMainMenu3(final ArrayList<FoodComposition> contentArrayList3) {
        contentAdapter3 = new ContentAdapter(contentArrayList3, this);

        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(this);

        recyclerView3 = findViewById(R.id.content_malam);
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setAdapter(contentAdapter3);

        swipeController3 = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                contentArrayList3.clear();
                setMainMenu3(contentArrayList3);
            }

            @Override
            public void onLeftClicked(int position) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(id.daur.HomeActivity.this,
                        Uri.parse(contentArrayList3.get(position).getDetail_link()));
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController3);
        itemTouchhelper.attachToRecyclerView(recyclerView3);

        recyclerView3.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController3.onDraw(c);
            }
        });
    }

    public void goToProfile(View view) {
        Intent i = new Intent(this, profileActivity.class);
        startActivity(i);
    }

    public void signOut(View view) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kamu yakin mau keluar?")
                .setPositiveButton(Html.fromHtml("<font color='#CF212A'> Keluar </font"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateUI();

                    }
                })
                .setNegativeButton(Html.fromHtml("<font color='#52575C'> Urungkan </font"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog alertDialog =  builder.create();
        alertDialog.show();
    }

    private void updateUI() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        pref.edit().clear().commit();

        Intent i = new Intent(this, loginActivity.class);
        startActivity(i);
        finish();
    }

}
