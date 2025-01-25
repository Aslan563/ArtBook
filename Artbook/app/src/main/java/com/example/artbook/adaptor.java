package com.example.artbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class adaptor extends  RecyclerView.Adapter<adaptor.viewholder>{
    @NonNull
    ArrayList<kitaplar> arrayListkitalar;
    Context context;

    public adaptor(@NonNull ArrayList<kitaplar> arrayListkitalar, Context context) {
        this.arrayListkitalar = arrayListkitalar;
        this.context = context;
    }

    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.rowlayou,parent,false);

        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        kitaplar kitaplar1=arrayListkitalar.get(position);
     holder.image.setImageBitmap(kitaplar1.getImage());
     holder.name.setText(kitaplar1.getName());
     holder.datetext.setText(String.valueOf(kitaplar1.getTarih()));

     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             NavDirections action=listfragmentDirections.actionListfragmentToKitapekleme(kitaplar1.getId(),"old",kitaplar1.getName(),convertBitmapToString(kitaplar1.getImage()));
             Navigation.findNavController(holder.itemView).navigate(action);
         }
     });

    }
    public String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // PNG formatında sıkıştır
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT); // Base64 ile encode et
    }

    @Override
    public int getItemCount() {
        return arrayListkitalar.size();
    }

    class  viewholder extends RecyclerView.ViewHolder {
        TextView name,datetext;
        ImageView image;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            image=itemView.findViewById(R.id.image);
            datetext= itemView.findViewById(R.id.date);
        }
    }
}
