package ca.thereckoner.thereckoner;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import ca.thereckoner.thereckoner.firebase.AnalyticsEvent;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Varun Venkataramanan.
 *
 * Activity to display an article for reading. Article is displayed in a WebVew.
 * This activity does not display the navigation drawer so that is is distraction free.
 *
 * TODO: Change the viewing from a WebView
 */
public class ReadingActivity extends AppCompatActivity {

  private Article article; //Article being displayed

  private Toolbar toolbar; //App toolbar

  private WebView webView; //WebView to display the article in

  private FirebaseAnalytics firebaseAnalytics;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(ca.thereckoner.thereckoner.R.layout.activity_reading);

    firebaseAnalytics = FirebaseAnalytics.getInstance(this);

    Intent intent = getIntent();
    article = (Article) intent.getSerializableExtra(
        getString(ca.thereckoner.thereckoner.R.string.articleParam));

    toolbar = (Toolbar) findViewById(ca.thereckoner.thereckoner.R.id.toolbar); //Setup toolbar
    setSupportActionBar(toolbar);

    createView();
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
        analyticsInfo.putString(AnalyticsEvent.PARAM_ARTICLE_NAME, article.getTitle());
        firebaseAnalytics.logEvent(AnalyticsEvent.EVENT_ARTICLE_SHARED, analyticsInfo);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String subject = "The Reckoner: " + article.getTitle(); //Subject of message

        //Body of message
        String body = article.getTitle()
            + "\nBy "
            + article.getAuthor()
            + "\n"
            + article.getDescription()
            + "\n"
            + article.getContentURL();

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
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
  private void createView() {
    webView = (WebView) findViewById(ca.thereckoner.thereckoner.R.id.webView);
    webView.setBackgroundColor(Color.TRANSPARENT);
    webView.loadDataWithBaseURL(null, article.getContent(), "text/html", "utf-8",
        null); //Ensure the utf-8 setting

    setTitle(article.getTitle()); //Set the toolbar to the title of the article
  }

  @Override public void onBackPressed() {
    super.onBackPressed();

    finish();
  }
}
