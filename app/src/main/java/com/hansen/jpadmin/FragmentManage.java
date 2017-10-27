package com.hansen.jpadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentManage extends Fragment {


    View view;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    DatabaseReference mDatabaseReference;
    FirebaseRecyclerAdapter<Model, ItemViewHolder> firebaseRecyclerAdapter;
    Spinner spinner;
    Button btnGo;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        spinner = view.findViewById(R.id.spinner);
        btnGo=view.findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayRecycler(spinner.getSelectedItem().toString().toLowerCase());
            }
        });




        mLayoutManager = new LinearLayoutManager(getContext());
        //mLayoutManager.setReverseLayout(true);
        //mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);


        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //  int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    public void displayRecycler(final String select) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("jackpot").child(select);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ItemViewHolder>(
                Model.class,
                R.layout.post_row,
                ItemViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, final Model model, int position) {
                final String item_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setPrice(model.getBody());
                viewHolder.setTime(model.getTime());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent adDetails = new Intent(v.getContext(), Post_Details.class);
                        adDetails.putExtra("selection",select);
                        adDetails.putExtra("postKey", item_key);
                        startActivity(adDetails);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public ItemViewHolder(View v) {
            super(v);
            mView = v;

        }

        public void setTitle(String title) {
            TextView tvTitle =  mView.findViewById(R.id.postTitle);
            tvTitle.setText(title);
        }

        public void setPrice(String price) {

            TextView txtPrice = mView.findViewById(R.id.post);
            txtPrice.setText("Ksh. " + price);

        }

        public void setTime(Long time) {

            TextView txtTime = mView.findViewById(R.id.postTime);
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


}



