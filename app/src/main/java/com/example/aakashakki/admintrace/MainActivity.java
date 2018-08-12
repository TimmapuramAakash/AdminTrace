package com.example.aakashakki.admintrace;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {
    public CountryCodePicker ccp;
    private FirebaseAuth mAuth;
    private String uid;
    private long exitTime = 0;
    private TextView mDetailText;
    private Button mTracebutton;
    private DatabaseReference mDatabase;
    public EditText mPhoneNumberField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ccp = findViewById(R.id.ccp);
        mPhoneNumberField = findViewById(R.id.field_phone_number);
        mDetailText = findViewById(R.id.detail);
        mTracebutton = findViewById(R.id.trackbtn);
        mAuth = FirebaseAuth.getInstance();

        mTracebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePhoneNumber()) {
                    Snackbar.make(findViewById(android.R.id.content), "Invalid phone number.",
                            Snackbar.LENGTH_SHORT).show();

                }
                else{
//                   Query query =   FirebaseDatabase.getInstance().getReference("users").orderByChild("phoneNo")
//                            .equalTo(ccp.getSelectedCountryCodeWithPlus()+mPhoneNumberField.getText().toString());

                    mDatabase = FirebaseDatabase.getInstance().getReference("users/");

                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get Post object and use the values to update the UI
                                String number=ccp.getSelectedCountryCodeWithPlus()+mPhoneNumberField.getText().toString();
                                int flag=0;
                                 for (DataSnapshot d : dataSnapshot.getChildren()) {
                                     //Toast.makeText(getApplicationContext(),"id "+d.child("phoneNo").getValue()+" "+number,Toast.LENGTH_LONG).show();

                                     try {
                                         if (d.child("phoneNo").getValue().equals(number)) {
                                            // Toast.makeText(getApplicationContext(), "id " + d.getKey(), Toast.LENGTH_LONG).show();
                                        if(d.getKey()!=null){
                                            flag=1;
                                            Intent i = new Intent(MainActivity.this,TraceMap.class);
                                            Bundle Userid = new Bundle();
                                            Userid.putString("uid",d.getKey());
                                            i.putExtras(Userid);
                                            startActivity(i);
                                            finish();
                                            break;
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Bad Network  try again", Toast.LENGTH_LONG).show();

                                        }

                                         }


                                     }catch (Exception e){
                                         Log.d("unaccounted e",e+"");
                                     }
                                 }
                                 if(flag!=1){
                                     Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();

                                 }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Getting Post failed, log a message
                               // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        };
                        mDatabase.addListenerForSingleValueEvent(postListener);
                        //mDatabase.addValueEventListener(postListener);


                }
            }
        });




    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    private boolean validatePhoneNumber() {
            if (TextUtils.isEmpty(mPhoneNumberField.getText().toString())){    // validate number length (mPhoneNumberField.getText().toString().length()<10)
                mPhoneNumberField.setError("Invalid phone number.");
                return false;
            }


        return true;
    }

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
