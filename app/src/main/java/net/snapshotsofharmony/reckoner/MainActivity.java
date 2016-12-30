package net.snapshotsofharmony.reckoner;

import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer; //Custom toolbar for the nav drawer
    private final String TAG = "MainActivity"; //Tag for logging
    private ActionBarDrawerToggle drawerToggle; //Hamburger icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //Remove the splash
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar); //Setup toolbar
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.navView);
        setupDrawerContent(nvDrawer); //Setup nav drawer

        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close); //Setup hamburger
        mDrawer.addDrawerListener(drawerToggle); //Link hamburger to app state

        //Start the default view to show all articles
        if(savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.flContent, ArticleListFragment.newInstance(""));
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    /**
     * Execute the drawer click
     * @param menuItem Menu item in nav drawer that was clicked
     */
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_news:
                fragment = ArticleListFragment.newInstance(getString(R.string.news));
                Log.v(TAG, "news");
                break;
            case R.id.nav_editorial:
                Log.v(TAG, "editorial");
                fragment = ArticleListFragment.newInstance(getString(R.string.editorial));
                break;
            case R.id.nav_life:
                Log.v(TAG, "life");
                fragment = ArticleListFragment.newInstance(getString(R.string.life));
                break;
            case R.id.nav_humansOfGarneau:
                Log.v(TAG, "humans of garneau");
                //fragment = ArticleListFragment.newInstance(getString(R.string.humansOfGarneau));
                fragment = HOGFragment.newInstance(null, null);
                break;
            default:
                Log.v(TAG, "default");
                fragment = ArticleListFragment.newInstance("");
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


}
