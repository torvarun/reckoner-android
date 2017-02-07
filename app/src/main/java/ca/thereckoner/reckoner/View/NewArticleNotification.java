package ca.thereckoner.reckoner.View;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.Glide;

import ca.thereckoner.reckoner.Article;
import ca.thereckoner.reckoner.R;
import ca.thereckoner.reckoner.ReadingActivity;

import java.util.concurrent.ExecutionException;

/**
 * Created by Varun Venkataramanan.
 *
 * Helper class for showing and canceling new article
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class NewArticleNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "NewArticle";

    /**
     * Accepts and article and displays it as a notification. Clicking the notification will open the reading view for that article
     * @param context Context of Notification
     * @param article Article to be displayed in the notification
     * @see #cancel(Context)
     */
    public static void notify(final Context context, Article article) {
        final Resources res = context.getResources();

        Bitmap thumbnail; // This image is used as the notification's large icon (thumbnail).
        try{
            thumbnail = Glide.with(context)
                    .load(article.getImageURL())
                    .asBitmap()
                    .into(-1,-1) //Full size image
                    .get();
        }catch (InterruptedException | ExecutionException e){
            thumbnail = BitmapFactory.decodeResource(res, R.drawable.logo_full);
        }

        String title = res.getString(R.string.new_article_notification_title_template, article.getTitle());

        //Set up the intent to open when notification is clicked
        Intent intent = new Intent(context, ReadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(res.getString(R.string.articleParam), article);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(title)
                .setContentText(article.getDescription())

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(thumbnail)

                // Set ticker text (preview) information for this notification.
                .setTicker(title)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                intent, //The intent to open reading view
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show expanded text content on devices running Android 4.1 or
                // later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(article.getDescription())
                        .setBigContentTitle(title)
                        .setSummaryText(article.getDescription()))

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
