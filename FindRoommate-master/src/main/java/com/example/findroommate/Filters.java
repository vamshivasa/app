package com.example.findroommate;

public class Filters {
    private boolean furnished;
    private boolean noSmoking;
    private boolean noDrinking;
    private boolean noPets;
    private boolean male;
    private boolean female;
    private long postId;
    private long filterId;

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getFilterId() {
        return filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }

    public boolean isFurnished() {
        return furnished;
    }

    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
    }

    public boolean isNoSmoking() {
        return noSmoking;
    }

    public void setNoSmoking(boolean noSmoking) {
        this.noSmoking = noSmoking;
    }

    public boolean isNoDrinking() {
        return noDrinking;
    }

    public void setNoDrinking(boolean noDrinking) {
        this.noDrinking = noDrinking;
    }

    public boolean isNoPets() {
        return noPets;
    }

    public void setNoPets(boolean noPets) {
        this.noPets = noPets;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }
}
