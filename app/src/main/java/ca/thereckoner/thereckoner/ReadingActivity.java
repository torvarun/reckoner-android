package ca.thereckoner.thereckoner;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import butterknife.ButterKnife;
import ca.thereckoner.thereckoner.firebase.AnalyticsEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

/**
 * Created by Varun Venkataramanan.
 *
 * Activity to display an article for reading. Article is displayed in a WebVew.
 * This activity does not display the navigation drawer so that is is distraction free.
 *
 * TODO: Change the viewing from a WebView
 */
public class ReadingActivity extends AppCompatActivity {

  private static final String TAG = "ReadingActivity";

  private Article article = null; //Article being displayed
  private String deepLinkUrl = null;

  private Toolbar toolbar; //App toolbar
  private WebView webView; //WebView to display the article in

  private FirebaseAnalytics firebaseAnalytics;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(ca.thereckoner.thereckoner.R.layout.activity_reading);
    ButterKnife.bind(this);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    webView = (WebView) findViewById(R.id.webView);

    firebaseAnalytics = FirebaseAnalytics.getInstance(this);

    setSupportActionBar(toolbar);

    //Check to see if the activity was started by a deep link
    FirebaseDynamicLinks.getInstance()
        .getDynamicLink(getIntent())
        .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
          @Override
          public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
            // Get deep link from result (may be null if no link is found)
            if (pendingDynamicLinkData != null) {
              deepLinkUrl = pendingDynamicLinkData.getLink().toString();

              createView(deepLinkUrl);
            } else{
              Intent intent = getIntent();
              article = (Article) intent.getSerializableExtra(
                  getString(ca.thereckoner.thereckoner.R.string.articleParam));
              createView(article);
            }
          }
        })
        .addOnFailureListener(this, new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            //Activity not started from deep link
            Log.w(TAG, "getDynamicLink:onFailure", e);
          }
        });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //Add the share icon
    getMenuInflater().inflate(ca.thereckoner.thereckoner.R.menu.share_menu, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case ca.thereckoner.thereckoner.R.id.share:
        Bundle analyticsInfo = new Bundle();


        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String body;

        if (article != null) {
          body = article.getTitle()
              + "\nBy "
              + article.getAuthor()
              + "\n"
              + article.getDescription()
              + "\n"
              + article.getContentURL();

          analyticsInfo.putString(AnalyticsEvent.PARAM_ARTICLE, article.getTitle());
        } else {
          body = deepLinkUrl;

          analyticsInfo.putString(AnalyticsEvent.PARAM_ARTICLE, deepLinkUrl);
        }

        firebaseAnalytics.logEvent(AnalyticsEvent.EVENT_ARTICLE_SHARED, analyticsInfo);

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(sharingIntent,
            "Share article with.... ")); //Text displayed on share dialog
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Sets up the WebView with the article. Also displays the article title in the toolbar.
   */
  private void createView(Article a) {
    webView.setBackgroundColor(Color.TRANSPARENT);
    webView.loadDataWithBaseURL(null, a.getContent(), "text/html", "utf-8",
        null); //Ensure the utf-8 setting

    setTitle(a.getTitle()); //Set the toolbar to the title of the article
  }

  private void createView(String url){
    webView.setBackgroundColor(Color.TRANSPARENT);
    webView.loadUrl(url);

    setTitle("The Reckoner of MGCI");
  }

  @Override public void onBackPressed() {
    super.onBackPressed();

    finish();
  }
}
