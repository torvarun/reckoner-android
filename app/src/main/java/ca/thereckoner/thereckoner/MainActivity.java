package ca.thereckoner.thereckoner;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
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
import ca.thereckoner.thereckoner.rating.AppRating;

/**
 * Created by Varun Venkataramanan.
 *
 * MainActivity for the app. Houses the nav drawer and displays the newest articles regardless of category.
 */
public class MainActivity extends AppCompatActivity {

  private final String TAG = "MainActivity"; //Tag for logging
  private ActionBarDrawerToggle drawerToggle; //Hamburger icon

  private final int NAV_CLOSE_TIME = 250; //250 fragment lauch time delay

  private Handler mHandler;

  private String oldTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(ca.thereckoner.thereckoner.R.style.AppTheme); //Remove the splash
    setContentView(ca.thereckoner.thereckoner.R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    NavigationView nvDrawer = (NavigationView) findViewById(R.id.nav_view);
    setupDrawerContent(nvDrawer); //Setup nav drawer

    DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, ca.thereckoner.thereckoner.R.string.drawer_open,  ca.thereckoner.thereckoner.R.string.drawer_close); //Setup hamburger
    mDrawer.addDrawerListener(drawerToggle); //Link hamburger to app state

    mHandler = new Handler();

    oldTitle = getString(R.string.app_name);

    //Start the default view to show all articles
    if(savedInstanceState == null) {
      FragmentManager manager = getSupportFragmentManager();
      FragmentTransaction transaction = manager.beginTransaction();
      transaction.replace(ca.thereckoner.thereckoner.R.id.flContent, ArticleListFragment.newInstance(""));
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
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
  public void selectDrawerItem(final MenuItem menuItem) {
    if(menuItem.getTitle() != oldTitle) {
      mHandler.postDelayed(new Runnable() {
        @Override public void run() {
          startNavDrawerItem(menuItem);
        }
      }, NAV_CLOSE_TIME);
    }

    // Close the navigation drawer
    DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawer.closeDrawer(GravityCompat.START);
  }

  private void startNavDrawerItem(MenuItem menuItem){
    int itemId = menuItem.getItemId();

    boolean toLaunchFrag = true;
    Fragment fragment = null;
    String fragTag = null;
    switch(itemId) {
      case ca.thereckoner.thereckoner.R.id.nav_news:
        Log.v(TAG, "news");
        fragTag = getString(R.string.news);
        fragment = ArticleListFragment.newInstance(getString(ca.thereckoner.thereckoner.R.string.news));
        break;

      case ca.thereckoner.thereckoner.R.id.nav_editorial:
        Log.v(TAG, "editorial");
        fragTag = getString(R.string.editorial);
        fragment = ArticleListFragment.newInstance(getString(ca.thereckoner.thereckoner.R.string.editorial));
        break;

      case ca.thereckoner.thereckoner.R.id.nav_life:
        Log.v(TAG, "life");
        fragTag = getString(R.string.life);
        fragment = ArticleListFragment.newInstance(getString(ca.thereckoner.thereckoner.R.string.life));
        break;

      case R.id.nav_rate:
        Log.v(TAG, "rating");
        toLaunchFrag = false;
        AppRating app = new AppRating();
        app.rateNow(MainActivity.this);
        break;

      case R.id.nav_about_us:
        Log.v(TAG, "about us");
        toLaunchFrag = false;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://thereckoner.ca/who-we-are-6th-guard/")); //Launch who we are page
        startActivity(browserIntent);
        break;

      default:
        Log.v(TAG, "default");
        fragment = ArticleListFragment.newInstance("");
    }

    if(toLaunchFrag && fragment != null) {
      // Set action bar title
      oldTitle = getTitle().toString();
      setTitle(menuItem.getTitle());

      // Insert the fragment by replacing any existing fragment
      final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
      ft.replace(R.id.flContent, fragment);
      //ft.addToBackStack(fragTag);
      ft.commit();
    } else{
      menuItem.setChecked(false);
    }
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

  @Override public void onBackPressed() {
    DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if(mDrawer.isDrawerOpen(GravityCompat.START)) { //Close the drawer if it is open
      mDrawer.closeDrawer(GravityCompat.START);
      return;
    } else if (oldTitle != getString(R.string.app_name)) {
      //getSupportFragmentManager().popBackStackImmediate();
    }

    super.onBackPressed();
  }

  @Override protected void onResume() {
    super.onResume();

    if (getTitle() == getString(R.string.nav_rate)) {
      setTitle(oldTitle);
    }
  }
}
