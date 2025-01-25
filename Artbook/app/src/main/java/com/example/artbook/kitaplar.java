package com.example.artbook;

import android.graphics.Bitmap;

import java.util.Date;

public class kitaplar {

    String name;
    Bitmap image;
    String tarih;
    int id;


    public kitaplar(String name, Bitmap image, String tarih, int id) {
        this.name = name;
        this.image = image;
        this.tarih = tarih;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
