package com.fire.chaty.activityes;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.chaty.R;
import com.fire.chaty.model.MessageItem;
import com.fire.chaty.utilities.RecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private EditText editText;
    private ImageButton imageButton;
    private Button sendButton;
    private List<MessageItem> list;
    private String name;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference fireBaseReference;
    ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerView);
        editText=findViewById(R.id.edit_text);
        imageButton=findViewById(R.id.load_photo_button);
        sendButton=findViewById(R.id.send_button);

        firebaseDatabase=FirebaseDatabase.getInstance();
        fireBaseReference=firebaseDatabase.getReference().child("users");



        name="Default user";
        list=new ArrayList<>();

        adapter=new RecyclerAdapter(this,list,MainActivity.this);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        editText.addTextChangedListener(new TextWatcher() {                                     //слушатель на изменение текста для доступа к кнопке "отправить"
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().trim().length()>0)
                            sendButton.setEnabled(true);
                        else
                            sendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});    // фильтер на количество символов для ввода
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageItem it=new MessageItem();
                it.setName(name);
                it.setText(editText.getText().toString());
                it.setImageUrl(null);
                fireBaseReference.push().setValue(it);
            }
        });

        listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {                      //слушатель базы на добавление элемента
                MessageItem message= dataSnapshot.getValue(MessageItem.class);
                list.add(message);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1);

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

        fireBaseReference.addChildEventListener(listener);                      //добавить слушателя для ссылки на позицию в базе











    }
}
