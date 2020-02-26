package com.fire.chaty.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fire.chaty.R;
import com.fire.chaty.activityes.MainActivity;
import com.fire.chaty.model.MessageItem;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
      private   Context context;
      private   List<MessageItem> items;
      private   MainActivity activity;


    public class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView name;
           private ImageView imageView;
           private TextView text;

            public MyViewHolder(View view){
                super(view);
                this.name=view.findViewById(R.id.text_name);
                this.imageView=view.findViewById(R.id.image);
                this.text=view.findViewById(R.id.text);
            }

    }

    public RecyclerAdapter(Context context, List<MessageItem> items, MainActivity mainActivity) {

        this.context = context;
        this.items = items;
        this.activity = mainActivity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MessageItem messageItem=items.get(position);
            String name=messageItem.getName();
            String text=messageItem.getText();
            String imageUrl=messageItem.getImageUrl();
            ImageView photoImageView=holder.imageView;
            if(name!=null)
                 holder.name.setText(messageItem.getName());
        if(imageUrl==null)
            photoImageView.setVisibility(View.GONE);
        else {
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext()).load(imageUrl).into(photoImageView);
        }

        if(text==null)
            holder.text.setVisibility(View.GONE);
        else {
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText(text);
        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
