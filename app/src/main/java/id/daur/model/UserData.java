package id.daur.model;


import java.time.LocalDate;
import java.time.Period;

public class UserData {

    private String name, body_status, gender;
    private int weight, height, age, bmi;
    private int calorie_intake, calorie_diet, days_to_ideal, ideal_weight, big_portion, small_portion;

    public UserData() {

    }

    public UserData(String name, String body_status, String gender, int weight, int height, int age, int bmi, int calorie_intake, int calorie_diet, int days_to_ideal, int ideal_weight, int big_portion, int small_portion) {
        this.name = name;
        this.body_status = body_status;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.bmi = bmi;
        this.calorie_intake = calorie_intake;
        this.calorie_diet = calorie_diet;
        this.days_to_ideal = days_to_ideal;
        this.ideal_weight = ideal_weight;
        this.big_portion = big_portion;
        this.small_portion = small_portion;
    }

    public int getBig_portion() {
        return big_portion;
    }

    public void setBig_portion(int big_portion) {
        this.big_portion = big_portion;
    }

    public int getSmall_portion() {
        return small_portion;
    }

    public void setSmall_portion(int small_portion) {
        this.small_portion = small_portion;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody_status() {
        return body_status;
    }

    public void setBody_status(String body_status) {
        this.body_status = body_status;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getBmi() {
        return bmi;
    }

    public void setBmi(int bmi) {
        this.bmi = bmi;
    }

    public int getCalorie_intake() {
        return calorie_intake;
    }

    public void setCalorie_intake(int calorie_intake) {
        this.calorie_intake = calorie_intake;
    }

    public int getCalorie_diet() {
        return calorie_diet;
    }

    public void setCalorie_diet(int calorie_diet) {
        this.calorie_diet = calorie_diet;
    }

    public int getDays_to_ideal() {
        return days_to_ideal;
    }

    public void setDays_to_ideal(int days_to_ideal) {
        this.days_to_ideal = days_to_ideal;
    }

    public int getIdeal_weight() {
        return ideal_weight;
    }

    public void setIdeal_weight(int ideal_weight) {
        this.ideal_weight = ideal_weight;
    }


    public int calculateBmi(int userWeight, int userHeight) {

        double usHeight = userHeight/100.0;
        double usBmi = userWeight/(Math.pow(usHeight,2));

        int calResult = (int) Math.round(usBmi);
        return calResult;
    }

    public int calculateAge(int day, int month, int year) {
        LocalDate dateBirth = LocalDate.of(year, month, day);
        LocalDate dateNow = LocalDate.now();

        Period diff = Period.between(dateBirth, dateNow);
        int age = diff.getYears();
        return age;
    }

    public double activityLevelClassification(String activity) {
        double activityLevel = 0.0;
        if(activity.equals("I"))  {
            activityLevel = 1.2;
        } else if(activity.equals("R"))  {
            activityLevel = 1.375;
        } else if(activity.equals("S"))  {
            activityLevel = 1.550;
        } else if(activity.equals("B"))  {
            activityLevel = 1.725;
        }
        return activityLevel;
    }

    public int calculateCalorieIntake(String gender, String activity, int weight, int height, int age) {
        double calorieIntake;
        double activityLevel = activityLevelClassification(activity);
        if (gender.equals("L")) {
            calorieIntake = 66.4730+(13.7516*weight)+(5.0033*height)-(6.7550*age);
            calorieIntake = calorieIntake*activityLevel;
        } else {
            calorieIntake = 655.0955+(9.5634*weight)+(1.8496*height)-(4.6756*age);
            calorieIntake = calorieIntake*activityLevel;
        }
        int calorieResult = (int) Math.round(calorieIntake);
        return calorieResult;
    }

    public int calculateCalorieDiet(int calorieIntake, String bodyStatus) {
        int calorieDiet = 0;
        if(bodyStatus.equals("Kurus")) {
            calorieDiet = calorieIntake + 500;
        } else if (bodyStatus.equals("Kegemukan") || bodyStatus.equals("Obesitas")) {
            calorieDiet = calorieIntake - 500;
        } else if(bodyStatus.equals("Normal")) {
            calorieDiet = calorieIntake;
        }
        return calorieDiet;
    }

    public int calculateIdealWeight (int height, String gender) {
        double topLimitIdeal, bottomLimitIdeal;
        double genderFactor = 0.0;

        if(gender.equals("L")) {
            genderFactor = 10/100.0;
        } else {
            genderFactor = 15/100.0;
        }

        topLimitIdeal = (height-100)+(genderFactor*(height-100));
        bottomLimitIdeal = (height-100)-(genderFactor*(height-100));

        int resultIdeal = (int) Math.round(((topLimitIdeal+bottomLimitIdeal)/2));
        return resultIdeal;
    }

    public int calculateDayLeft(int idealWeight, int weight) {
        int mustBurn = Math.abs(weight-idealWeight);
        int daysLeft = (int) Math.round((mustBurn/0.07));
        return daysLeft;
    }

    public int calculateBigPortion(int calorieDiet) {
        double factorPortion = 25/100.0;
        int myPortion = (int) Math.round((calorieDiet*factorPortion));
        return myPortion;
    }

    public int calculateSmallPortion(int calorieDiet) {
        double factorPortion = 12.5/100.0;
        int myPortion = (int) Math.round((calorieDiet*factorPortion));
        return myPortion;
    }
}