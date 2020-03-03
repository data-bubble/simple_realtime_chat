package com.fire.chaty.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.chaty.R;
import com.fire.chaty.model.User;

import java.util.List;

public class RecyclerUsersListAdapter extends RecyclerView.Adapter<RecyclerUsersListAdapter.UsersViewHolder> {

    private List<User> cards;
    private OnUserClickListener listener;

     public interface OnUserClickListener {
         void onUserClick(int position);
     }

     public void setOnUserClickListener(OnUserClickListener listener){
         this.listener=listener;
     }
     public class UsersViewHolder extends RecyclerView.ViewHolder {
         private ImageView ava;
         private TextView name;

        public UsersViewHolder(View view, final OnUserClickListener listener) {
            super(view);
            ava = view.findViewById(R.id.ava_image_view);
            name = view.findViewById(R.id.user_data_text_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                            listener.onUserClick(position);
                    }

                }
            });
        }

    }

        public RecyclerUsersListAdapter(List<User> list){
            cards=list;

        }
    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_item,parent,false);
        return new UsersViewHolder(itemView,listener);
    }
    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
                User user=cards.get(position);
                ImageView avatar=holder.ava;
                TextView nameTextView=holder.name;
                String name=user.getLogin();
                avatar.setImageResource(user.getAvatarMockUpResourse());

                if(!name.equals("")&&name!=null)
                    nameTextView.setText(name);



    }



    @Override
    public int getItemCount() {
        return cards.size();
    }
}
