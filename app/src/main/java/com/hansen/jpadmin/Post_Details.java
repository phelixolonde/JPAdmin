package com.hansen.jpadmin;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;

public class Post_Details extends AppCompatActivity {
    DatabaseReference mRef;

    EditText tvTitle, tvBody;

    ImageView imgBody;
    ProgressDialog pd;

    String selection,postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setContentView(R.layout.activity_post_detailed);

        postKey = getIntent().getExtras().getString("postKey");
        selection=getIntent().getExtras().getString("selection");

        tvBody = (EditText) findViewById(R.id.tvBody);
        tvTitle = (EditText) findViewById(R.id.tvTitle);
        imgBody = (ImageView) findViewById(R.id.imgBody);
        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();


        if (postKey != null) {

            mRef = FirebaseDatabase.getInstance().getReference().child("jackpot").child(selection).child(postKey);
        }
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String title = dataSnapshot.child("title").getValue().toString();
                    String body = dataSnapshot.child("body").getValue().toString();
                    Long time = (Long) dataSnapshot.child("time").getValue();
                    if (title != null) {
                        tvTitle.setText(title.toUpperCase());
                        pd.dismiss();
                    } else {
                        Toast.makeText(Post_Details.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    if (body != null) {
                        tvBody.setText(body);

                    }
                    if (time != null) {
                        setTime(time);
                    }
                    if (dataSnapshot.hasChild("image")){
                        String image= (String) dataSnapshot.child("image").getValue();

                    }

                }catch (Exception e){

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        int edit=0;
        if (id == android.R.id.home) {
            finish();
        }
        if(id==R.id.delete){
          mRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Toast.makeText(Post_Details.this, "deleted successfully", Toast.LENGTH_SHORT).show();
                  finish();
              }
          });
        }
        if(id==R.id.edit){

            item.setVisible(false);

            final FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab_done);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("title", tvTitle.getText().toString());
                    map.put("body", tvBody.getText().toString());
                    mRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            item.setVisible(true);
                            fab.setVisibility(View.GONE);
                        }
                    });
                }
            });



        }

        return super.onOptionsItemSelected(item);
    }

    public void setTime(Long time) {
        TextView txtTime = (TextView) findViewById(R.id.post_time);
        long elapsedTime;
        long currentTime = System.currentTimeMillis();
        int elapsed = (int) ((currentTime - time) / 1000);
        if (elapsed < 60) {
            if (elapsed < 2) {
                txtTime.setText("Just Now");
            } else {
                txtTime.setText(elapsed + " sec ago");
            }
        } else if (elapsed > 604799) {
            elapsedTime = elapsed / 604800;
            if (elapsedTime == 1) {
                txtTime.setText(elapsedTime + " week ago");
            } else {

                txtTime.setText(elapsedTime + " weeks ago");
            }
        } else if (elapsed > 86399) {
            elapsedTime = elapsed / 86400;
            if (elapsedTime == 1) {
                txtTime.setText(elapsedTime + " day ago");
            } else {
                txtTime.setText(elapsedTime + " days ago");
            }
        } else if (elapsed > 3599) {
            elapsedTime = elapsed / 3600;
            if (elapsedTime == 1) {
                txtTime.setText(elapsedTime + " hour ago");
            } else {
                txtTime.setText(elapsedTime + " hours ago");
            }
        } else if (elapsed > 59) {
            elapsedTime = elapsed / 60;
            txtTime.setText(elapsedTime + " min ago");


        }

    }
}
