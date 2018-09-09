package tagliaferro.adriano.projetoposto.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import tagliaferro.adriano.projetoposto.R;
import tagliaferro.adriano.projetoposto.view.AbastecimentoActivity;
import tagliaferro.adriano.projetoposto.view.MainActivity;

/**
 * Created by Adriano2 on 06/01/2018.
 */

public class PostoWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_IDPROVIDER_KEYS = "controle_posto_widget_id_keys";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName thisWidget = new ComponentName(context, PostoWidgetProvider.class);

        int[] allWidgetsId = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int id : allWidgetsId) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent abastActivity = new Intent(context.getApplicationContext(), AbastecimentoActivity.class);
            abastActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, abastActivity, 0);
            views.setOnClickPendingIntent(R.id.widget_btn_add, pi);

            Intent intent = new Intent(context.getApplicationContext(), PostoRemoteViewService.class);
            views.setRemoteAdapter(R.id.widget_list_veiculos, intent);

            appWidgetManager.updateAppWidget(id, views);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.hasExtra(WIDGET_IDPROVIDER_KEYS)){
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDPROVIDER_KEYS);
            this.onUpdate(context.getApplicationContext(), AppWidgetManager.getInstance(context.getApplicationContext()), ids);
        }else {
            super.onReceive(context, intent);
        }


    }
}
