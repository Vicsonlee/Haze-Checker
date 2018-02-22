package com.assignment.hazechecker;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.hazechecker.Fragments.HomeFragment;
import com.assignment.hazechecker.Fragments.LoadingDialogFragment;
import com.assignment.hazechecker.Fragments.Pm25Fragment;
import com.assignment.hazechecker.Fragments.PsiFragment;
import com.assignment.hazechecker.Fragments.TimeoutDialogFragment;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements LoadingDialogFragment.DialogListener, TimeoutDialogFragment.DialogListener {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private DialogFragment loadingDialog;
    private DialogFragment timeoutDialog;

    private String appTitle;
    private String psiTitle = "PSI";
    private String pm25Title = "PM25";
    private String[] listItems;

    private String timestamp;
    private Map<String,String> psiValues;
    private Map<String,String> pm25Values;

    private MainController controller = new MainController(this);

    public void setPsiValues(Map<String,String> psiValues){
        this.psiValues = psiValues;
    }

    public void setPm25Values(Map<String,String> pm25Values){
        this.pm25Values = pm25Values;
    }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingDialog = new LoadingDialogFragment();
        timeoutDialog = new TimeoutDialogFragment();

        appTitle = getTitle().toString();
        listItems = getResources().getStringArray(R.array.items_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);

        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment newFragment;
                switch(position){
                    case 1:
                        newFragment = PsiFragment.newInstance(psiValues);
                        appTitle = psiTitle;
                        break;
                    case 2:
                        newFragment = Pm25Fragment.newInstance(pm25Values);
                        appTitle = pm25Title;
                        break;
                    default:
                        newFragment = HomeFragment.newInstance(psiValues,pm25Values);
                        appTitle = getTitle().toString();
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
                drawerLayout.closeDrawer(drawerList);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawer();

        if (savedInstanceState != null) {
            return;
        }

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .commit();

        controller.loadData();
        refresh();
    }

    private void setupDrawer(){
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(appTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog){
        // a little hacky, but it works
        dialog.dismiss();
        returnButtonOnClick(findViewById(R.id.refresh_button));
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        dialog.dismiss();
        controller.cancelQuery();
    }

    public void returnButtonOnClick(View v){
        // honestly this should be in controller
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            loadingDialog.show(getSupportFragmentManager(), "loading");
            controller.startQuery();
        }
        else{
            timeoutDialog.show(getSupportFragmentManager(), "timeout");
        }
    }

    public void refresh(){
        Fragment dialog = getSupportFragmentManager().findFragmentByTag("loading");
        if (dialog != null) {
            loadingDialog.dismiss();
        }

        TextView time = findViewById(R.id.result_time_text);
        time.setText(getString(R.string.last_result, timestamp));

        Fragment loadedFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Fragment newFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (loadedFragment instanceof PsiFragment) {
            newFragment = PsiFragment.newInstance(psiValues);
        }
        else if (loadedFragment instanceof Pm25Fragment) {
            newFragment = Pm25Fragment.newInstance(pm25Values);
        }
        else{
            newFragment = HomeFragment.newInstance(psiValues,pm25Values);
        }
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }

    public void timeout(){
        Fragment dialog = getSupportFragmentManager().findFragmentByTag("loading");
        if (dialog != null) {
            loadingDialog.dismiss();
        }
        timeoutDialog.show(getSupportFragmentManager(), "timeout");
    }
}
