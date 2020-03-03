package com.fire.chaty.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.fire.chaty.constants.MyConstants;
import com.fire.chaty.model.MessageItem;
import com.fire.chaty.model.User;
import com.fire.chaty.utilities.RecyclerAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private EditText editText;
    private ImageButton imageButton;
    private Button sendButton;
    private List<MessageItem> list;
    private String login;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference fireBaseReference;
    private ChildEventListener listener;
    private DatabaseReference usersReference;
    private ChildEventListener usersListener;
    private FirebaseStorage fireStorage;
    private StorageReference fireStorageReference;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerView);
        editText=findViewById(R.id.edit_text);
        imageButton=findViewById(R.id.load_photo_button);
        sendButton=findViewById(R.id.send_button);

        firebaseDatabase=FirebaseDatabase.getInstance();
        fireBaseReference=firebaseDatabase.getReference().child("messages");
        usersReference=firebaseDatabase.getReference().child("users");
        fireStorage=FirebaseStorage.getInstance();
        fireStorageReference=fireStorage.getReference().child("images");




                Intent intent=getIntent();
                if(intent!=null)
                    login=intent.getStringExtra(MyConstants.USER_LOGIN);
                else
                    login="Default user";


                list=new ArrayList<>();

                adapter=new RecyclerAdapter(this,list,MainActivity.this);                           //добавил адаптер к recyclerview
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

                imageButton.setOnClickListener(new View.OnClickListener() {                         //отправка изображения в intent
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/jpeg");
                            intent.putExtra(intent.EXTRA_LOCAL_ONLY,true);
                            startActivityForResult(Intent.createChooser(intent,"Choose an image"),MyConstants.IMAGE_PICKER_CODE);   //выбор изображения Chooser


                        }
                });

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MessageItem it=new MessageItem();
                        it.setName(login);
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
                            recyclerView.scrollToPosition(list.size()-1);
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

                fireBaseReference.addChildEventListener(listener);                      //добавить слушателя для ссылки на позицию в базе ссылке сообщений

                usersListener=new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User user=dataSnapshot.getValue(User.class);
                        if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            login=user.getLogin();
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

                usersReference.addChildEventListener(usersListener);                    //слушатель аналогично сообщениям








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
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {               //получение uri от активити
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri=null;
        if(requestCode==MyConstants.IMAGE_PICKER_CODE&&resultCode==RESULT_OK){
            imageUri=data.getData();
            final StorageReference storageReference=fireStorageReference.child(imageUri.getLastPathSegment());
            uploadTask=storageReference.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {    //  получение downloadUrl
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        MessageItem messageItem=new MessageItem();
                        messageItem.setName(login);
                        messageItem.setImageUrl(downloadUri.toString());

                        fireBaseReference.push().setValue(messageItem);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

    }
}
