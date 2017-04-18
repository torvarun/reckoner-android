package ca.thereckoner.thereckoner.rating;

import android.content.Context;
import android.net.Uri;

/**
 * Created by Varun on 2017-04-09.
 */

public class GoogleMarket implements Market {

  private static String marketLink = "market://details?id=";

  @Override public Uri getMarketURI(Context context) {
    return Uri.parse(marketLink + context.getPackageName());
  }
}
