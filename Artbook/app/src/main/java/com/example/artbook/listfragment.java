package com.example.artbook;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class listfragment extends Fragment {
  RecyclerView mylistem;
  adaptor adaptor1;
  ArrayList<kitaplar> arrayListkitaplar;
  SQLiteDatabase database;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup=(ViewGroup) inflater.inflate(R.layout.fragment_listfragment, container, false);
        mylistem=viewGroup.findViewById(R.id.mylistem);

        try {

            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(getContext().getDatabasePath("kutuphane"), null);

            Cursor cursor= database.rawQuery("SELECT * FROM kitaplar ORDER BY time desc ",null);
            int resimindex=cursor.getColumnIndex("resim");
            int isiminsex=cursor.getColumnIndex("name");
            int idindex=cursor.getColumnIndex("id");
            int dateindex=cursor.getColumnIndex("time");
            arrayListkitaplar=new ArrayList<>();
            while (cursor.moveToNext()){
                 byte[] resim=cursor.getBlob(resimindex);
                String isim=cursor.getString(isiminsex);
                String datestr=cursor.getString(dateindex);
                int id=cursor.getInt(idindex);
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); // Veritabanından gelen format
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()); // İstediğiniz format: "dd.MM.yyyy"

                Bitmap gelenresim= BitmapFactory.decodeByteArray(resim,0,resim.length);
                Date date;
                try {
                    date = inputFormat.parse(datestr); // String'i Date'e dönüştürme
                    String formattedDate = outputFormat.format(date); // Tarihi istenilen formata dönüştür
                    arrayListkitaplar.add(new kitaplar(isim, gelenresim,formattedDate,id));
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }

            mylistem.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            adaptor1=new adaptor(arrayListkitaplar,getContext());
            mylistem.setAdapter(adaptor1);


        } catch (Exception e) {
            e.printStackTrace();

        }

        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        });
    }
}