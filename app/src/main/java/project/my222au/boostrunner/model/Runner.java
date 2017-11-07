package project.my222au.boostrunner.model;


public class Runner {
    private String mName;
    private double mWeight;
    private double mHeight;
    private boolean isMale;
    private int mAge;
    private  Gender mGender;


    private String mCaloris;
    private long ID;



    public Runner(String name, double weight, double height, int age) {
        mName = name;
        mWeight = weight;
        mHeight = height;

        mAge = age;
    }

    public Runner() {
    }

    public String getName() {
        return mName;
    }

    public String getCaloris() {
        return mCaloris;
    }
    public void setName(String name) {
        mName = name;
    }
    public void setCaloris(String caloris) {
        mCaloris = caloris;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
    public double getWeight() {
        return mWeight;
    }

    public void setWeight(double weight) {
        mWeight = weight;
    }

    public double getHeight() {
        return mHeight;
    }

    public void setHeight(double height) {
        mHeight = height;
    }


    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender gender) {
        mGender = gender;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }


    public enum Gender {
        MALE,
        FEMALE
    }

    }




