package butkus.com.butkus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    String quote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    case R.id.menu_item_share:
        shareIntent(quote);
        return true;
    default:
        return super.onOptionsItemSelected(item);
    }
    }

    public void butkusClicked(View view) {
        quote = getButkus();

        TextView quoteTextView = (TextView) findViewById(R.id.quoteTextView);

        quoteTextView.setTextColor(Color.parseColor(randomColor()));
        quoteTextView.setText(quote);
    }

    public String getButkus() {
        String url = "https://butkus.xyz/api/quote";
        String result;
        String quote = "";
        String noQuote = "Nothing";

        DownloadButkusTask butkusTask = new DownloadButkusTask();

        try {
            result = butkusTask.execute(url).get();

            Pattern pattern = Pattern.compile("<butkus> (.+)");
            Matcher matcher = pattern.matcher(result);

            if (matcher.find()) {
                quote = matcher.group(1);
            } else {
                quote = noQuote;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return quote;
    }

    public void shareIntent(String quote) {

        if (quote == null) {
            Context context = getApplicationContext();
            CharSequence text = "Inget att dela!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, quote);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }

    public class DownloadButkusTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String randomColor() {
        String[] colors = new String[]{"#af8700", "#d75f00", "#d70000", "#af005f", "#5f5faf",
                                        "#0087ff", "#00afaf", "#5f8700"};
        int random = new Random().nextInt(colors.length);
        return colors[random];
    }

}

