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

    public String getStatehood() { return statehood; }

    public void setStatehood(String statehood) { this.statehood = statehood; }

    public String getCapitalSince(String capitalSince) { return capitalSince; }

    public void setCapitalSince(String capitalSince) { this.capitalSince = capitalSince; }

    /*public String toString()
    {
        return id + ": " +  + " " + phone + " " + url + " " + comments;
    }
     */
}
