package com.example.findroommate;

public interface CustomClickListener {
    void cardClicked(int pos, int open);

    void cardClick(int pos, int open, User userModel);

    void cardClick(int pos, int open, Post postModel);

    void clicks(int pos);
}