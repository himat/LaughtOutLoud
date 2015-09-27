package outloud.laugh.main.test1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends ActionBarActivity {

    public static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("info", "hi");
        login();
        Intent intent = new Intent(this, FriendsListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("resumed", "on resume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("username", "bob");
            jsonParams.put("password", "youcandoit");
            StringEntity entity = new StringEntity(jsonParams.toString());

            invokeWS(entity);
        } catch(Exception e) { e.printStackTrace(); }
    }

    public void invokeWS(StringEntity entity) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), "http://mudgea3.cloudapp.net:5000/api/login", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.v("userid is", response.getString("user_id"));
                    userID = response.getString("user_id");

                } catch (Exception e) {
                    Log.e("JSON error", "errror");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("REST CALL ERROR", "tried to login " +statusCode);
            }
        });
    }
}
