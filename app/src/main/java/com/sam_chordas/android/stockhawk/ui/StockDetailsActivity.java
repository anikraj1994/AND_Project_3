package com.sam_chordas.android.stockhawk.ui;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class StockDetailsActivity extends AppCompatActivity {
    LineChartView mChart,mChart2;
    TextView tv;
    String stockname="GOOGL";
    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();

    private Tooltip mTip;

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChart = (LineChartView) findViewById(R.id.linechart);
    tv=(TextView)findViewById(R.id.valuetext);
        stockname=getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(stockname.toUpperCase());

        new GetHistory().execute();

       // setupchart();



    }


    private class GetHistory extends AsyncTask<Void, Void, Void> {
        double max,min;
        @Override
        protected Void doInBackground(Void... params) {
            try{
                StringBuilder urlStringBuilder = new StringBuilder();
                // Base URL for the Yahoo query
                urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
                urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = \""+stockname+"\" and startDate = \""+getYesterdayDateString(-30)+"\" and endDate = \""+getYesterdayDateString(-1)+"\"", "UTF-8"));
                urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                        + "org%2Falltableswithkeys&callback=");
                String urlString = urlStringBuilder.toString();
                try{
                    String getResponse = fetchData(urlString);
                    List<String[]> valuesgot = Utils.historyJsonToVals(getResponse);
                    String[] label =new String[valuesgot.size()];
                    float[] values = new float[valuesgot.size()];
                   // float[] tipval = new float[valuesgot.size()];
                     max=Float.parseFloat(valuesgot.get(0)[1]);
                     min=Float.parseFloat(valuesgot.get(0)[1]);


                   for (int i = valuesgot.size()-1; i >=0 ; i--) { if(values[i]>max)max=values[i];
                       if(values[i]<min)min=values[i];}

                    for (int i = valuesgot.size()-1; i >=0 ; i--) {
                    Log.e("anik",valuesgot.get(i)[0]+"  "+valuesgot.get(i)[1]+"\n");
                    label[i]=valuesgot.get(i)[0].substring(8);
                    values[i]=scale(Float.parseFloat(valuesgot.get(i)[1]),min,max,0,15);
                  //  tipval[i]=Float.parseFloat(valuesgot.get(i)[1]);


                    }

                    setupchart(label,values,max,min);
                }catch (Exception e){
                    Log.e("anik","error fetching data "+e.toString());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            tv.setText("Max = "+max);
        }
    }

    public static float scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return (float)(((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin);
    }



    private String getYesterdayDateString(int x) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, x);
        return dateFormat.format(cal.getTime());
    }

public void setupchart(String[] label, final float[] values, double max, double min){
 //   TextView tv1 = (TextView) findViewById(R.id.valuetext);
  //  tv1.setText("Min = "+min+" Max = "+max);


  /*  mTip = new Tooltip(this, R.layout.linechart_three_tooltip, R.id.value);

    ((TextView) mTip.findViewById(R.id.value))
            .setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Semibold.ttf"));

    mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
    mTip.setDimensions((int) Tools.fromDpToPx(65), (int) Tools.fromDpToPx(25));

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

        mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

        mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

        mTip.setPivotX(Tools.fromDpToPx(65) / 2);
        mTip.setPivotY(Tools.fromDpToPx(25));

    }

    mChart.setTooltips(mTip);
    */

    // Data
    /*LineSet dataset = new LineSet(mLabels, mValues[0]);
    dataset.setColor(Color.parseColor("#758cbb"))
            .setFill(Color.parseColor("#2d374c"))
            .setDotsColor(Color.parseColor("#758cbb"))
            .setThickness(4)
            .setDashed(new float[]{10f,10f})
            .beginAt(5);
    mChart.addData(dataset);*/

   // mChart.setStep(100);
    LineSet dataset = new LineSet(label, values);
    dataset.setColor(Color.parseColor("#fed074"))
            .setFill(Color.parseColor("#5aaff2"))
            .setDotsColor(Color.parseColor("#ffc755"))
            .setThickness(4);
    mChart.addData(dataset);
    // Chart
    mChart.setBorderSpacing(Tools.fromDpToPx(15))
            .setAxisBorderValues(0, 20)
            .setYLabels(AxisController.LabelPosition.NONE)
            .setLabelsColor(Color.parseColor("#7c7c7c"))
            .setXAxis(false)
            .setYAxis(false);

    //  mBaseAction = action;
    Runnable chartAction = new Runnable() {
        @Override
        public void run() {
            //mBaseAction.run();
         //   mTip.prepare(mChart.getEntriesArea(0).get(3), values[3]);
        //    mChart.showTooltip(mTip, true);
        }
    };

    Animation anim = new Animation()
            .setEasing(new BounceEase())
            .setEndAction(chartAction);

    mChart.show(anim);
}

}
