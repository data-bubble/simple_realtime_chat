package com.fire.chaty.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.chaty.R;
import com.fire.chaty.model.User;
import com.fire.chaty.utilities.RecyclerUsersListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private ChildEventListener listener;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerUsersListAdapter adapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        users=new ArrayList<>();

        auth=FirebaseAuth.getInstance();
        buildRecyclerView();
        attachUserDatabaseReference();
    }

    private void buildRecyclerView(){
        recyclerView=findViewById(R.id.users_list_recycler);
        adapter=new RecyclerUsersListAdapter(users);
        manager= new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }
    private void attachUserDatabaseReference(){                             //загрузка юзеров в лист из базы
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        if(listener==null) {
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User user = dataSnapshot.getValue(User.class);
                   // user.setAvatarMockUpResourse(R.drawable.person_shape);    //установка авы
                    if(!auth.getCurrentUser().getUid().equals(user.getId()))
                    users.add(user);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.addChildEventListener(listener);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.log_out_menu:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UsersListActivity.this, LogInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
