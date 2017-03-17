package ca.thereckoner.reckoner;

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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Varun Venkataramanan.
 *
 * MainActivity for the app. Houses the nav drawer and displays the newest articles regardless of category.
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer; //Nav Drawer Layout
    @BindView(R.id.toolbar) Toolbar toolbar; //App toolbar
    @BindView(R.id.navView) NavigationView nvDrawer; //Custom toolbar for the nav drawer

    private final String TAG = "MainActivity"; //Tag for logging
    private ActionBarDrawerToggle drawerToggle; //Hamburger icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ca.thereckoner.reckoner.R.style.AppTheme); //Remove the splash
        setContentView(ca.thereckoner.reckoner.R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawer); //Setup nav drawer

        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, ca.thereckoner.reckoner.R.string.drawer_open,  ca.thereckoner.reckoner.R.string.drawer_close); //Setup hamburger
        mDrawer.addDrawerListener(drawerToggle); //Link hamburger to app state

        //Start the default view to show all articles
        if(savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(ca.thereckoner.reckoner.R.id.flContent, ArticleListFragment.newInstance(""));
            transaction.commit();
        }
    }

    /**
     * Handle item click for the nav drawer
     * @param item
     * @return
     */
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

    /**
     * Setip the nav drawer on click interface
     * @param navigationView
     */
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
        Fragment fragment;
        switch(menuItem.getItemId()) {
            case ca.thereckoner.reckoner.R.id.nav_news:
                Log.v(TAG, "news");
                fragment = ArticleListFragment.newInstance(getString(ca.thereckoner.reckoner.R.string.news));
                break;
            case ca.thereckoner.reckoner.R.id.nav_editorial:
                Log.v(TAG, "editorial");
                fragment = ArticleListFragment.newInstance(getString(ca.thereckoner.reckoner.R.string.editorial));
                break;
            case ca.thereckoner.reckoner.R.id.nav_life:
                Log.v(TAG, "life");
                fragment = ArticleListFragment.newInstance(getString(ca.thereckoner.reckoner.R.string.life));
                break;
            default:
                Log.v(TAG, "default");
                fragment = ArticleListFragment.newInstance("");
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(ca.thereckoner.reckoner.R.id.flContent, fragment).commit();

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
