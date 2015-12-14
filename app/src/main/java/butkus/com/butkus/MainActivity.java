package butkus.com.butkus;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void butkusClicked(View view) {
        String quote = getButkus();

        TextView quoteTextView = (TextView) findViewById(R.id.quoteTextView);

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

}

