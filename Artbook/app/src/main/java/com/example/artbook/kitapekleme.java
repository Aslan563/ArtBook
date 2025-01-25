package com.example.artbook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class kitapekleme extends Fragment {
    ImageView selectimage,selectimagecamera;
    EditText bookname;
    Button addbuton,delete;
    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<String> permissionLauncher2;
    ActivityResultLauncher<Intent> cameraResultLauncher;
    Bitmap bitmap;
    SQLiteDatabase database;
    String geleninfo="";
    int gelenid=0;
    String gelenname="";
    String gelenimage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher();





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_kitapekleme, container, false);
        selectimage = viewGroup.findViewById(R.id.selectbook);
        bookname = viewGroup.findViewById(R.id.bookname);
        addbuton = viewGroup.findViewById(R.id.addbutun);
        delete=viewGroup.findViewById(R.id.delete);
        selectimagecamera=viewGroup.findViewById(R.id.selectimagecamera);

        if(getArguments()!=null){
            geleninfo=kitapeklemeArgs.fromBundle(getArguments()).getInfo();
            gelenid=kitapeklemeArgs.fromBundle(getArguments()).getId();
            gelenname=kitapeklemeArgs.fromBundle(getArguments()).getName();
            gelenimage=kitapeklemeArgs.fromBundle(getArguments()).getImage();
        }
        if(geleninfo.equals("new")){{
          delete.setVisibility(View.GONE);

        }}
        if(geleninfo.equals("old")){{

            bookname.setText(gelenname);
            selectimage.setImageBitmap(convertStringToBitmap(gelenimage));
            addbuton.setVisibility(View.GONE);
            selectimage.setEnabled(false);
            selectimagecamera.setVisibility(View.GONE);
            bookname.setEnabled(false);
        }}



        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimage(viewGroup);
            }
        });

        selectimagecamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(viewGroup);
            }
        });

        addbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              addbook(viewGroup.getRootView());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(viewGroup);
            }
        });

        return viewGroup;
    }
    public Bitmap convertStringToBitmap(String encodedString) {
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT); // Base64 ile decode et
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // Bitmap olarak decode et
    }
    public void delete(View view){
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(getContext().getDatabasePath("kutuphane"), null);
        database.execSQL("DELETE FROM kitaplar Where id="+gelenid+" ");
        getsecondfragment(getView());
    }

    public void addbook(View view) {



            String name = String.valueOf(bookname.getText()).trim(); // kitap adını alıyoruz
            if(bitmap==null){
                Toast.makeText(getContext(), "resim seçiniz", Toast.LENGTH_SHORT).show();
                return;
            }if(TextUtils.isEmpty(name)){
                bookname.setError("isim boş bırakılamaz");
                return;
            }



            // Bitmap'in doğru şekilde tanımlandığından ve bir resim verisiyle doldurulduğundan emin olun
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream); // Bitmap'i PNG formatında sıkıştırıyoruz
            byte[] kayitedilecekresim = outputStream.toByteArray(); // byte dizisine dönüştürüyoruz

            try {

                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(getContext().getDatabasePath("kutuphane"), null);
                database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY AUTOINCREMENT, resim BLOB, name VARCHAR,time DATE DEFAULT CURRENT_TIMESTAMP)");


                String sql = "INSERT INTO kitaplar(resim, name) VALUES(?, ?)";
                SQLiteStatement preparedStatement = database.compileStatement(sql);


                preparedStatement.bindBlob(1, kayitedilecekresim);
                preparedStatement.bindString(2, name);
                preparedStatement.executeInsert();
                bookname.setText("");
                selectimage.setImageResource(R.drawable.selectimage);

                getsecondfragment(getView());



            } catch (Exception e) {
                e.printStackTrace();

            }


    }

    private void openCamera(View view) {
        // 1. Kamera izni kontrolü ve talebi
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                Snackbar.make(view, "Kamera izni gerekli", Snackbar.LENGTH_SHORT)
                        .setAction("İzin ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                permissionLauncher2.launch(Manifest.permission.CAMERA);  // Kamera iznini talep et
                            }
                        }).show();
            } else {
                permissionLauncher2.launch(Manifest.permission.CAMERA);  // Kamera iznini talep et
            }
        } else {
            // Kamera izni verilmişse fotoğraf çekme işlemi başlatılabilir
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraResultLauncher.launch(cameraIntent);  // Fotoğraf çekme işlemine başla
        }








    }


    public void selectimage(View view) {
        Context context = getContext();
        if (context == null) {
            return;
        }

       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
           if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
               if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                   Snackbar.make(view, "İzin gerekli", Snackbar.LENGTH_SHORT)
                           .setAction("İzin ver", new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);  // İzin talep et
                               }
                           }).show();
               } else {
                   permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);  // İzin talep et
               }
           } else {

               Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               resultLauncher.launch(intent);
           }
        }
        else{
           if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
               if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                   Snackbar.make(view, "İzin gerekli", Snackbar.LENGTH_SHORT)
                           .setAction("İzin ver", new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);  // İzin talep et
                               }
                           }).show();
               } else {
                   permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);  // İzin talep et
               }
           } else {

               Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               resultLauncher.launch(intent);
           }
        }

    }

    public void launcher() {
        // Resim seçimi için launcher
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();  // Seçilen resmin URI'sini al

                    if (selectedImageUri != null) {
                        try {
                            // URI'yi Bitmap'e dönüştür
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                            selectimage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // İzin talebi için launcher
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    // İzin verildiyse, galeriyi başlat
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    resultLauncher.launch(intentGallery);
                } else {
                    // İzin verilmediyse, toast ile bilgilendir
                    Toast.makeText(getContext(), "İzin gerekli", Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionLauncher2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    // İzin verildiyse, kamera işlemi başlat
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraResultLauncher.launch(cameraIntent);
                } else {
                    // İzin verilmediyse, toast ile bilgilendir
                    Toast.makeText(getContext(), "Kamera izni gerekli", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                if (extras != null) {
                    bitmap = (Bitmap) extras.get("data");  // Çekilen fotoğraf
                    selectimage.setImageBitmap(bitmap);  // Fotoğrafı ekranda göster
                }
            }
        });

    }
    public  void getsecondfragment(View view){
        NavDirections action=kitapeklemeDirections.actionKitapeklemeToListfragment();
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               getsecondfragment(getView());
            }
        });
    }
}
