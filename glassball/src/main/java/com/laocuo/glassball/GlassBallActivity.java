package com.laocuo.glassball;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.laocuo.glassball.fragment.GlassGameFragment;
import com.laocuo.glassball.fragment.GlassMenuFragment;
import com.laocuo.glassball.utils.L;


public class GlassBallActivity extends AppCompatActivity
        implements GlassMenuFragment.GlassMenuInterface, GlassGameFragment.GlassGameInterface{
    private Toolbar mToolbar;
    private FragmentManager fm;
    private GlassMenuFragment mMenuFragment;
    private GlassGameFragment mGameFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_ball);
        initToolBar();
        initFragment();
        L.d("onCreate");
    }

    private void initFragment() {
        mMenuFragment = new GlassMenuFragment();
        mGameFragment = new GlassGameFragment();
        fm = getFragmentManager();
        switchFragment(mMenuFragment);
    }

    private void switchFragment(Fragment mFragment) {
        if (mFragment != null) {
            fm.beginTransaction().replace(R.id.content, mFragment).commit();
        }
    }

    private void initToolBar() {
//        mToolbar = (Toolbar) findViewById(R.id.toobar);
//        setSupportActionBar(mToolbar);
//        ActionBar ab = getSupportActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_glass_ball, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fm.findFragmentById(R.id.content) instanceof GlassGameFragment) {
            onBackPress();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void startGame() {
        switchFragment(mGameFragment);
    }

    @Override
    public void onBackPress() {
        switchFragment(mMenuFragment);
    }
}
