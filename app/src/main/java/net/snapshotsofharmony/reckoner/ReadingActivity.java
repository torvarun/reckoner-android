package net.snapshotsofharmony.reckoner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class ReadingActivity extends AppCompatActivity {

    private Article mArticle;

    private Toolbar mToolbar;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Intent intent = getIntent();
        mArticle = (Article) intent.getSerializableExtra(getString(R.string.articleParam));

        mToolbar = (Toolbar) findViewById(R.id.toolbar); //Setup toolbar
        setSupportActionBar(mToolbar);

        createView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Add the share icon
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String subject = "The Reckoner: " + mArticle.getTitle(); //Subject of message

                //Body of message
                String body = mArticle.getTitle()
                        + "\nBy " + mArticle.getAuthor()
                        + "\n" + mArticle.getDescription()
                        + "\n" + mArticle.getContentURL();

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(sharingIntent, "Share article with.... ")); //Text displayed on share dialog
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createView(){
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadDataWithBaseURL(null, mArticle.getContent(), "text/html", "utf-8", null); //Ensure the utf-8 setting

        setTitle(mArticle.getTitle()); //Set the toolbar to the title of the article
    }
}
