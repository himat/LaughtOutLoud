package outloud.laugh.main.test1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by Hima on 9/26/2015.
 */
public class CreateSentenceActivity extends Activity{
    Button v1, v2, v3, n1, n2, n3, a1, a2,a3, p1, p2, p3;
    Button[] words;
    Button submitSentenceBtn;
    TextView sentence;

    String sentenceTemplate;
    String[] V, N, A, P;
    //Verbs = green, Nouns = blue, Adjectives = red, Proper = purple
    String properChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sentence);
        sentence = (TextView) findViewById(R.id.text_sentence);
        submitSentenceBtn = (Button) findViewById(R.id.submit_sentence_btn);

        //TODO call api to get sentence template and random words
        //testing code
        sentenceTemplate = "I wish my _N would _V, so that I could pick up _P in my _A car.";
        V = new String[] {"run", "swim", "strum"};
        N = new String[] {"cat", "balls", "dog"};
        A = new String[] {"blue", "fuzzy", "cute"};
        P = new String[] {"Heinz", "Stanford", "CMU"};



        submitSentenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("hi", "button pressed");
                sendChallenge();
                Intent mainIntent = new Intent(getApplicationContext(), FriendsListActivity.class);
                startActivity(mainIntent);
            }
        });
        initButtons();

        //Verbs = green, Nouns = blue, Adjectives = red, Proper = purple

        String coloredSentence = sentenceTemplate.replaceAll("_V", "<font color='0x05ED05'>"+"_____"+"</font>");
        coloredSentence = coloredSentence.replaceAll("_N", "<font color='blue'>"+"_____"+"</font>");
        coloredSentence = coloredSentence.replaceAll("_A", "<font color='red'>"+"_____"+"</font>");
        coloredSentence = coloredSentence.replaceAll("_P", "<font color='purple'>"+"_____"+"</font>");
        sentence.setText(Html.fromHtml(coloredSentence));

        Log.v("sentence just made", Html.toHtml((Spanned) sentence.getText()));

    }

    //type = V, N, A, F
    void updateWithWord(String type, String word) {
        String currentText = Html.toHtml((Spanned) sentence.getText());
        Log.v("new type", type);
        Log.v("current sentence", currentText);
        String color = "";
        switch (type)
        {
            case "V":
                color = "05ed05";
                break;
            case "N":
                color = "0000ff";
                break;
            case "A":
                color = "ff0000";
                break;
            case "P":
                color = "800080";
                break;
            default:
                Log.e("INVALID TYPE", type);
        }
        Log.v("color is", color);
        String prevText = currentText;
        currentText = currentText.replaceAll(color+"\">_____", color+"\">"+word);
        if(type.equals("P") && !prevText.equals(currentText))
            properChosen = word;
        Log.v("updated sentence", currentText);
        sentence.setText(Html.fromHtml(currentText));
    }

    void initButtons()
    {
        v1 = (Button) findViewById(R.id.v1);
        v1.setText(V[0]);
        v1.setTextColor(Color.rgb(5, 237, 5));
        v2 = (Button) findViewById(R.id.v2);
        v2.setText(V[1]);
        v2.setTextColor(Color.rgb(5, 237, 5));
        v3 = (Button) findViewById(R.id.v3);
        v3.setText(V[2]);
        v3.setTextColor(Color.rgb(5, 237, 5));

        n1 = (Button) findViewById(R.id.n1);
        n1.setText(N[0]);
        n1.setTextColor(Color.BLUE);
        n2 = (Button) findViewById(R.id.n2);
        n2.setText(N[1]);
        n2.setTextColor(Color.BLUE);
        n3 = (Button) findViewById(R.id.n3);
        n3.setText(N[2]);
        n3.setTextColor(Color.BLUE);

        a1 = (Button) findViewById(R.id.a1);
        a1.setText(A[0]);
        a1.setTextColor(Color.RED);
        a2 = (Button) findViewById(R.id.a2);
        a2.setText(A[1]);
        a2.setTextColor(Color.RED);
        a3 = (Button) findViewById(R.id.a3);
        a3.setText(A[2]);
        a3.setTextColor(Color.RED);

        p1 = (Button) findViewById(R.id.p1);
        p1.setText(P[0]);
        p1.setTextColor(Color.rgb(128, 0, 128));
        p2 = (Button) findViewById(R.id.p2);
        p2.setText(P[1]);
        p2.setTextColor(Color.rgb(128, 0, 128));
        p3 = (Button) findViewById(R.id.p3);
        p3.setText(P[2]);
        p3.setTextColor(Color.rgb(128, 0, 128));

        words = new Button[] {v1, v2, v3, n1, n2, n3, a1, a2, a3, p1, p2, p3};
        for(int i=0; i<words.length; i++)
        {
            String type;
            if(i <= 2) type = "V";
            else if (i <= 5) type = "N";
            else if (i <= 8) type = "A";
            else type = "P";
            Log.i("type before", type);
            words[i].setTag(type);
            words[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateWithWord(((Button) view).getTag().toString(), ((Button) view).getText().toString());
                }
            });
        }
    }

    public void sendChallenge() {

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("text", sentence.getText().toString());
            jsonParams.put("word", properChosen);
            jsonParams.put("challenger", MainActivity.userID);
            jsonParams.put("challenged", "1");
            StringEntity entity = new StringEntity(jsonParams.toString());

            invokeWS(entity);
        } catch(Exception e) { e.printStackTrace(); }
    }

    public void invokeWS(StringEntity entity) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), "http://mudgea3.cloudapp.net:5000/api/challenge", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("challenge sent response", response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("REST CALL ERROR", "tried to challenge " +statusCode);
            }
        });
    }


}
