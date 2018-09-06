package com.example.dmitrii.familyradar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import com.example.dmitrii.familyradar.databinding.ActivitySearchUsersBinding;

public class SearchUsersActivity extends AppCompatActivity {

    private ActivitySearchUsersBinding binding;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        //Getting an instance
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_users);


        binding.imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseUsersSearch();

            }
        });
    }

    private  void firebaseUsersSearch(){

    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        View view;

        public UsersViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }


    }
}
