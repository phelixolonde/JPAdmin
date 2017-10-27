package com.hansen.jpadmin;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by POlonde on 9/28/2017.
 */

public class Fragment_New extends Fragment {
    View v;
    AutoCompleteTextView txtTitle, txtBody;
    Button submit;
    Spinner spinner;

    DatabaseReference mRef;
    DatabaseReference nref;
    String option;
    String time;
    ProgressDialog pd;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String LEGACY_SERVER_KEY="AIzaSyBDWCIZ23nOtCXNXHjm7PbnK-7T3nJIquk";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_new, container, false);
        txtBody = v.findViewById(R.id.txtBody);
        txtTitle = v.findViewById(R.id.txtTitle);
        submit = v.findViewById(R.id.button);
        spinner = v.findViewById(R.id.spinner);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Please wait");


        mRef = FirebaseDatabase.getInstance().getReference().child("jackpot");

        final long millis = System.currentTimeMillis() / 1000;
        time = String.valueOf(1 - millis);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();



                    nref = mRef.child(spinner.getSelectedItem().toString().toLowerCase()).child(time);
                    nref.child("title").setValue(txtTitle.getText().toString());
                    nref.child("body").setValue(txtBody.getText().toString());
                    nref.child("time").setValue(System.currentTimeMillis()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            Toast.makeText(getContext(), "Posted Succesfully", Toast.LENGTH_SHORT).show();
                            sendNotification(txtTitle.getText().toString());
                        }
                    });



            }
        });

        return v;

    }

    private void sendNotification(final String sBody) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body",sBody);
                    dataJson.put("title","1 New Post");
                    json.put("notification",dataJson);
                    json.put("to","/topics/jackpot");
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+ LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();

                }catch (Exception e){
                }
                return null;
            }
        }.execute();

    }
}
