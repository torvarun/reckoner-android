package ca.thereckoner.reckoner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * Created by Varun Venkataramanan.
 *
 * Activity to display an article for reading. Article is displayed in a WebVew.
 * This activity does not display the navigation drawer so that is is distraction free.
 *
 * TODO: Change the viewing from a WebView
 */
public class ReadingActivity extends AppCompatActivity {

  private Article mArticle; //Article being displayed

  private Toolbar mToolbar; //App toolbar

  private WebView mWebView; //WebView to display the article in

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(ca.thereckoner.reckoner.R.layout.activity_reading);

    Intent intent = getIntent();
    mArticle = (Article) intent.getSerializableExtra(
        getString(ca.thereckoner.reckoner.R.string.articleParam));

    mToolbar = (Toolbar) findViewById(ca.thereckoner.reckoner.R.id.toolbar); //Setup toolbar
    setSupportActionBar(mToolbar);

    createView();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //Add the share icon
    getMenuInflater().inflate(ca.thereckoner.reckoner.R.menu.share_menu, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case ca.thereckoner.reckoner.R.id.share:

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String subject = "The Reckoner: " + mArticle.getTitle(); //Subject of message

        //Body of message
        String body = mArticle.getTitle()
            + "\nBy "
            + mArticle.getAuthor()
            + "\n"
            + mArticle.getDescription()
            + "\n"
            + mArticle.getContentURL();

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
    mWebView = (WebView) findViewById(ca.thereckoner.reckoner.R.id.webView);
    mWebView.loadDataWithBaseURL(null, mArticle.getContent(), "text/html", "utf-8",
        null); //Ensure the utf-8 setting

    setTitle(mArticle.getTitle()); //Set the toolbar to the title of the article
  }

  @Override public void onBackPressed() {
    super.onBackPressed();

    finish();
  }
}
