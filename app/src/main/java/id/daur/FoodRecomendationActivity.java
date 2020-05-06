package id.daur;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import id.daur.model.FeedRule;
import id.daur.model.FoodComposition;
import id.daur.model.UserData;

public class FoodRecomendationActivity extends AppCompatActivity {
    private DatabaseReference db;
    private UserData userData;
//    private FoodComposition breakfast, lunch, dinner;
//    private FeedRule feedRule;
    private ArrayList <FoodComposition> breakfast, lunch, dinner, snack;
    private String user, uid, parentRule;
    private String bigPortionMenu, smallPortionMenu;
    private TextView breakfastFood, lunchFood, dinnerFood, snackFood1, snackFood2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_recomendation);

//        user = getIntent().getExtras().get("userName").toString();
        uid = getIntent().getExtras().get("userId").toString();
//        uid = "-Lvgy3DRT1PHMqOOG9TP";

        breakfast = new ArrayList<>();
        lunch = new ArrayList<>();
        dinner = new ArrayList<>();
        snack = new ArrayList<>();

        breakfastFood = (TextView) findViewById(R.id.menu_sarapan);
        lunchFood = (TextView) findViewById(R.id.menu_makan_siang);
        dinnerFood = (TextView) findViewById(R.id.menu_makan_malam);
        snackFood1 = (TextView)findViewById(R.id.menu_cemilan_1);
        snackFood2 = (TextView)findViewById(R.id.menu_cemilan_2);

//        Toast.makeText(FoodRecomendationActivity.this, "id: "+uid, Toast.LENGTH_SHORT).show();
        db = FirebaseDatabase.getInstance().getReference();
//        Log.i("tes", ""+userData.getName());


    }

    public void processClicked(View view) {

        db.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userData = dataSnapshot.getValue(UserData.class);
                getPortion(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPortion(UserData userData) {
        final int bigPortion = userData.getBig_portion();
        final int smallPortion = userData.getSmall_portion();

        db.child("Aturan_Makan").child("Makan_Besar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot rule: dataSnapshot.getChildren()) {
                    FeedRule feedRule = rule.getValue(FeedRule.class);

                    int topLimit = feedRule.getBatas_atas();
                    int bottomLimit = feedRule.getBatas_bawah();

                    if(bigPortion >= bottomLimit && bigPortion <= topLimit) {
                        bigPortionMenu = feedRule.getId_menu();
                        Log.i("menu", bigPortionMenu);
                        searchBigPortionMenu(bigPortionMenu);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child("Aturan_Makan").child("Makan_Kecil").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot rule: dataSnapshot.getChildren()) {
                    FeedRule feedRule = rule.getValue(FeedRule.class);

                    int topLimit = feedRule.getBatas_atas();
                    int bottomLimit = feedRule.getBatas_bawah();

                    if(smallPortion >= bottomLimit && smallPortion <= topLimit) {
                        smallPortionMenu = feedRule.getId_menu();
//                        Log.i("menu makan kecil", smallPortionMenu);
                        searchSmallPortionMenu(smallPortionMenu);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchSmallPortionMenu(String smallPortionMenu) {
        Log.i("snack", smallPortionMenu);
        db.child("Makanan").child("Makan_Kecil").child(smallPortionMenu).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot food: dataSnapshot.getChildren()) {
                    FoodComposition myFood = food.getValue(FoodComposition.class);
                    snack.add(myFood);
                }
                showSnackMenu(snack, snackFood1, snackFood2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchBigPortionMenu(String bigPortionMenu) {
        db.child("Makanan").child("Makan_Besar").child(bigPortionMenu).child("Sarapan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot food: dataSnapshot.getChildren()) {
                    FoodComposition myFood = food.getValue(FoodComposition.class);
                    breakfast.add(myFood);
                }
                showMyMenu(breakfast, breakfastFood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child("Makanan").child("Makan_Besar").child(bigPortionMenu).child("Makan_Siang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot food: dataSnapshot.getChildren()) {
                    FoodComposition myFood = food.getValue(FoodComposition.class);
                    lunch.add(myFood);
                }
                showMyMenu(lunch, lunchFood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child("Makanan").child("Makan_Besar").child(bigPortionMenu).child("Makan_Malam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot food: dataSnapshot.getChildren()) {
                    FoodComposition myFood = food.getValue(FoodComposition.class);
                    dinner.add(myFood);
                }
                showMyMenu(dinner, dinnerFood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMyMenu(ArrayList<FoodComposition> menu, TextView textView) {
        Random rand = new Random();
        int index = rand.nextInt(3);

        String[] foodName = splitString(menu.get(index).getNama_makanan());
        String[] menuDetail = splitString(menu.get(index).getKet_makanan());
        double menuCal = menu.get(index).getTotal_kal();
        String menuName = menu.get(index).getNama_menu();

        String conInformation = processMenuInformation(foodName, menuDetail, menuName, menuCal);

        textView.setText(conInformation);

    }

    private void showSnackMenu(ArrayList<FoodComposition> snack, TextView snackFood1, TextView snackFood2) {
        Random rand = new Random();
        int snack1Index = rand.nextInt(3);
        int snack2Index = 0;

        if(snack1Index != 1) {
            snack2Index = 2-snack1Index;
        }

        String[] foodName = splitString(snack.get(snack1Index).getNama_makanan());
        String[] menuDetail = splitString(snack.get(snack1Index).getKet_makanan());
        double menuCal = snack.get(snack1Index).getTotal_kal();
        String menuName = snack.get(snack1Index).getNama_menu();

        String con1Information = processMenuInformation(foodName, menuDetail, menuName, menuCal);


        String[] foodName1 = splitString(snack.get(snack2Index).getNama_makanan());
        String[] menuDetail1 = splitString(snack.get(snack2Index).getKet_makanan());
        double menuCal1 = snack.get(snack2Index).getTotal_kal();
        String menuName1 = snack.get(snack2Index).getNama_menu();

        String con2Information = processMenuInformation(foodName1, menuDetail1, menuName1, menuCal1);

        snackFood1.setText(con1Information);
        snackFood2.setText(con2Information);
    }



    private String[] splitString(String rawStr) {
        String[] arrOfStr = rawStr.split("_", 5);

        return  arrOfStr;
    }

    private String processMenuInformation(String[] foodName, String[] menuDetail, String menuName, double menuCal) {
        String menuInformation = "";

        for(int i=0; i<foodName.length; i++) {
            menuInformation = menuInformation + foodName[i] + " setara dengan " + menuDetail[i]+"\n";
        }
        menuInformation = menuInformation + "\n" + "Total Kalori makanan: "+menuCal;

        String conInformation = menuName + "\n\n"+ menuInformation;
        return conInformation;
    }
}
