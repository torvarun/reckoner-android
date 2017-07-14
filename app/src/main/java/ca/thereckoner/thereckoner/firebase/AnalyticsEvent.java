package ca.thereckoner.thereckoner.firebase;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by torvarun on 2017-07-13.
 *
 * Class for Reckoner analytics event constants. Contains the tags used for event logging (defaults stored in superclass).
 */

public class AnalyticsEvent extends FirebaseAnalytics.Event{

  public static final String PARAM_TAG = "tag"; //TAG of class
  public static final String PARAM_CATEGORY = "category"; //Category selected
  public static final String PARAM_ARTICLE_NAME = "article_name"; //Name of article

  public static final String EVENT_CATEGORY_VIEWED = "category_viewed"; //Use for when user selects a section (ie. News, Editorial, Life)
  public static final String EVENT_ARTICLE_VIEWED = "article_viewed"; //Use for when user reads an article
  public static final String EVENT_ARTICLE_SHARED = "article_shared"; //Use for when user shares an article
  public static final String EVENT_RATING_LAUNCHED = "rating_app"; //Used for when user selects dialog to rate app
  public static final String EVENT_ABOUT_US = "about_us_viewed"; //Used for when user selects the about us section
}
