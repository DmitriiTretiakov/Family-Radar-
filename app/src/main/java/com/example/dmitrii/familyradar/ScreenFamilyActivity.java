package com.example.dmitrii.familyradar;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.dmitrii.familyradar.adapter.TextAdapter;
import com.example.dmitrii.familyradar.databinding.ActivityScreenFamilyBinding;
import java.util.ArrayList;
import java.util.List;

public class ScreenFamilyActivity extends AppCompatActivity {

    private ActivityScreenFamilyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_family);

        //Getting an instance
        binding = DataBindingUtil.setContentView(this, R.layout.activity_screen_family);

        //Call the toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getResources().getString(R.string.contacts));

        // Button back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        // List contacts
        RecyclerView recyclerView = (RecyclerView) binding.rvContacts;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextAdapter textAdapter = new TextAdapter();
        recyclerView.setAdapter(textAdapter);

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            stringList.add(getResources().getString(R.string.listContact));
        }
        textAdapter.setItems(stringList);

        // Scroll animation fab
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy < -5 && !binding.fab.isShown())
                    binding.fab.show();
                else if (dy > 5 && binding.fab.isShown())
                    binding.fab.hide();
            } @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    // Method for processing the transition to another activity
    public void clickOnScreenAddContacts(View view) {
        Intent intent = new Intent(this, SearchUsersActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu mMenu) {
        Menu menu = binding.toolbar.getMenu();
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.delete);
        menu.findItem(R.id.checkSymbol).setVisible(false);
        menu.findItem(R.id.social).setVisible(false);
        return true;
    }

    //Method of replacing buttons when deleting from the list
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                binding.toolbar.getMenu().findItem(R.id.checkSymbol).setVisible(true);
                binding.toolbar.getMenu().findItem(R.id.delete).setVisible(false);
                binding.fab.setVisibility(View.GONE);
                return true;
            case R.id.checkSymbol:
                binding.toolbar.getMenu().findItem(R.id.delete).setVisible(true);
                binding.toolbar.getMenu().findItem(R.id.checkSymbol).setVisible(false);
                binding.fab.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
