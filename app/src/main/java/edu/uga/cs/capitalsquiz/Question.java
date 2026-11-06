package edu.uga.cs.capitalsquiz;

public class Question {
    private long id;
    private String state;
    private String capitalCity;
    private String secondCity;
    private String thirdCity;
    private int statehood;
    private int capitalSince;

    public Question() {
        this.id = -1;
        this.state = null;
        this.capitalCity = null;
        this.secondCity = null;
        this.thirdCity = null;
        this.statehood = 0;
        this.capitalSince = 0;
    } // Question

    public Question (String state, String capitalCity, String secondCity, String thirdCity,
                     int statehood, int capitalSince) {
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
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCapitalCity() {
        return capitalCity;
    }

    public void setCapitalCity(String capitalCity) {
        this.capitalCity = capitalCity;
    } // setCapitalCity

    public String getSecondCity() {
        return secondCity;
    }

    public void setSecondCity(String secondCity) {
        this.secondCity = secondCity;
    }

    public String getThirdCity() {
        return thirdCity;
    }

    public void setThirdCity(String thirdCity) {
        this.thirdCity = thirdCity;
    }

    public int getStatehood() { return statehood; }

    public void setStatehood(int statehood) { this.statehood = statehood; }

    public int getCapitalSince() { return capitalSince; }

    public void setCapitalSince(int capitalSince) { this.capitalSince = capitalSince; }

    /*public String toString()
    {
        return id + ": " +  + " " + phone + " " + url + " " + comments;
    }
     */
}
