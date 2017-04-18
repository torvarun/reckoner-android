package ca.thereckoner.thereckoner.rating;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Varun on 2017-04-09.
 */

public class AppRating {

  private static Market market = new GoogleMarket();

  public static void rateNow(final Context context) {
    try {
      context.startActivity(new Intent(Intent.ACTION_VIEW, market.getMarketURI(context)));
    } catch (ActivityNotFoundException activityNotFoundException1) {
      Log.e(AppRating.class.getSimpleName(), "Market Intent not found");
    }
  }
}
