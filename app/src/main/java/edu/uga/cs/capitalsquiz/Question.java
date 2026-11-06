package edu.uga.cs.capitalsquiz;

public class Question {
    private long id;
    private String state;
    private String capitalCity;
    private String secondCity;
    private String thirdCity;
    private String statehood;
    private String capitalSince;

    public Question() {
        this.id = -1;
        this.state = null;
        this.capitalCity = null;
        this.secondCity = null;
        this.thirdCity = null;
        this.statehood = null;
        this.capitalSince = null;
    } // Question

    public Question (String state, String capitalCity, String secondCity, String thirdCity,
                     String statehood, String capitalSince) {
        this.id = -1;
        this.state = state;
        this.capitalCity = capitalCity;
        this.secondCity = secondCity;
        this.thirdCity = thirdCity;
        this.statehood = statehood;
        this.capitalSince = capitalSince;
    } // Question

    public long getId() {
        return id;
    } // getId

    public void setId(long id) {
        this.id = id;
    } // setId

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    } // setState

    public String getCapitalCity() {
        return capitalCity;
    } // getCapitalCity

    public void setCapitalCity(String capitalCity) {
        this.capitalCity = capitalCity;
    } // setCapitalCity

    public String getSecondCity() {
        return secondCity;
    } // getSecondCity

    public void setSecondCity(String secondCity) {
        this.secondCity = secondCity;
    } // setSecondCity

    public String getThirdCity() {
        return thirdCity;
    } // getThirdCity

    public void setThirdCity(String thirdCity) {
        this.thirdCity = thirdCity;
    } // setThirdCity
}
