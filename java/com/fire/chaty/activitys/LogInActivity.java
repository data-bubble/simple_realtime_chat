package com.fire.chaty.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fire.chaty.R;
import com.fire.chaty.constants.MyConstants;
import com.fire.chaty.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPassEditText;
    private EditText loginEditText;
    private TextView toogleLogin;
    private Button signButton;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference reference;
    private String login;

 
    private boolean loginMode=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        auth=FirebaseAuth.getInstance();

        emailEditText=findViewById(R.id.email_etext);
        passwordEditText=findViewById(R.id.pass_etext);
        loginEditText=findViewById(R.id.login_etext);
        toogleLogin=findViewById(R.id.toogle_sign_up_text_view);
        signButton=findViewById(R.id.sign_in_button);
        repeatPassEditText=findViewById(R.id.pass_repeat_etext);

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginSignUp(emailEditText.getText().toString().trim()
                ,passwordEditText.getText().toString().trim());

            }
        });



        db=FirebaseDatabase.getInstance();
        reference=db.getReference().child("users");
        login="default";
        if(auth.getCurrentUser()!=null){
            Intent intent=new Intent(this,UsersListActivity.class);
            getLoginFromReference(auth.getCurrentUser().getUid());
            intent.putExtra(MyConstants.USER_LOGIN,login);
            startActivity(intent); }     //проверка аутентификации


    }

    private void loginSignUp(String email,String password) {
        if(loginMode){
            if(checkInputParameters(email,password,null)) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(MyConstants.AUTH_TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
//                                    Intent intent=new Intent(LogInActivity.this,MainActivity.class);
//                                    intent.putExtra(MyConstants.USER_LOGIN,);
//                                    startActivity(intent);

                                       getLoginFromReference(user.getUid());
                                        Intent intent=new Intent(LogInActivity.this,UsersListActivity.class);
                                        intent.putExtra(MyConstants.USER_LOGIN,login);

                                       startActivity(intent);

                                    //    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(MyConstants.AUTH_TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LogInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //   updateUI(null);
                                }


                            }
                        });
            }
        } else {

           if(checkInputParameters(email,password,repeatPassEditText.getText().toString().trim())) {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(MyConstants.AUTH_TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    addUserInDb(user);
                                    Intent intent=new Intent(LogInActivity.this,UsersListActivity.class);
                                    intent.putExtra(MyConstants.USER_LOGIN,loginEditText.getText().toString().trim());
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(MyConstants.AUTH_TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(LogInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });
            }
        }

    }

    private void addUserInDb(FirebaseUser firebaseUser) {
        User user=new User();
        user.setEmail(firebaseUser.getEmail());
        user.setLogin(loginEditText.getText().toString().trim());
        user.setId(firebaseUser.getUid());
        reference.push().setValue(user);
    }
    boolean checkInputParameters(String email,String password,String repeatPassword){

        boolean isValidate=true;
        repeatPassword=(repeatPassword==null)?password:repeatPassword;

        if(email.equals("")) {
            Toast.makeText(this,"email mustn't be empty",Toast.LENGTH_SHORT).show();
            isValidate = false;
        }
        if(password.length()<7||repeatPassword.length()<7) {
            Toast.makeText(this, "password must be at least 7 characters", Toast.LENGTH_SHORT).show();
            isValidate = false;
        }
        if(!password.equals(repeatPassword)) {
            Toast.makeText(this, "passwords don't match", Toast.LENGTH_SHORT).show();
            isValidate =false;
        }

        return isValidate;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
     //   updateUI(currentUser);
    }

    public void toogleSignMode(View view) {
        if(loginMode){
            loginMode=false;
            signButton.setText("Sign up");
            toogleLogin.setText("tap to log in");
            repeatPassEditText.setVisibility(View.VISIBLE);
            loginEditText.setVisibility(View.VISIBLE);
        }
            else{
                loginMode = true;
                signButton.setText("Login");
                toogleLogin.setText("tap to sign up");
                repeatPassEditText.setVisibility(View.GONE);
                loginEditText.setVisibility(View.GONE);
            }


    }
    private void getLoginFromReference(final String userId) {
       // reference.orderByChild(MyConstants.DB_ID).equalTo(userId);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getId().equals(userId))
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
        });



    }

}
