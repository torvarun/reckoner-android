package net.snapshotsofharmony.reckoner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class ReadingActivity extends AppCompatActivity {

    private Article mArticle;

    private WebView mWebView;
    private TextView titleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Intent intent = getIntent();

        mArticle = (Article) intent.getSerializableExtra(getString(R.string.articleParam));

        createView();
    }

    private void createView(){
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadDataWithBaseURL(null, mArticle.getContent(), "text/html", "utf-8", null); //Ensure the utf-8 setting

        titleTV = (TextView) findViewById(R.id.title);
        titleTV.setText(mArticle.getTitle());
    }
}
