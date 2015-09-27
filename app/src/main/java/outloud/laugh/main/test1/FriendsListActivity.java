package outloud.laugh.main.test1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Hima on 9/25/2015.
 */
public class FriendsListActivity extends ListActivity {
    String[] listItems = {"Alice", "*random2", "vGo laugh"};

    ListView friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
        Log.v("friends list", "started");
        getFriendsMatchesList();
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id)
    {
        super.onListItemClick(lv, v, position, id);

        int itemPosition = position;
        String itemValue = (String) lv.getItemAtPosition(position);

        if(itemValue.startsWith("*"))//Have a new request
        {
            Intent challengeIntent = new Intent(this, DoChallengeActivity.class);
            startActivity(challengeIntent);
        }
        else if(itemValue.startsWith("v"))
        {
            Intent verifyIntent = new Intent(this, VerifyChallengeActivity.class);
            startActivity(verifyIntent);
        }
        else
        {
            Intent sentenceIntent = new Intent(this, CreateSentenceActivity.class);
            startActivity(sentenceIntent);
        }
    }


    public void getFriendsMatchesList() {
        invokeWS();
    }

    public void invokeWS() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getApplicationContext(), "http://mudgea3.cloudapp.net:5000/api/friends/2", null, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.v("current friends mat req", response.toString());
                //TODO analyze response match type
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("REST CALL ERROR", "tried to challenge " + statusCode);
            }
        });
    }



}
