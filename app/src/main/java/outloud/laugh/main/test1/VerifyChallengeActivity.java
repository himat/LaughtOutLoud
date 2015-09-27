package outloud.laugh.main.test1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Hima on 9/26/2015.
 */
public class VerifyChallengeActivity extends Activity{
    TextView sentenceDisplay;
    ImageButton playAudioBtn;
    Button goodBtn, badBtn;

    boolean isPlaying = false;
    MediaPlayer mediaPlayer;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_challenge);

        sentenceDisplay = (TextView) findViewById(R.id.sentence_verification_display);
        playAudioBtn = (ImageButton) findViewById(R.id.play_audio_button);
        goodBtn = (Button) findViewById(R.id.good_sound);
        badBtn = (Button) findViewById(R.id.bad_sound);

        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/LOL_audio_returned.mp3";

        playAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlaying = !isPlaying;
                if(isPlaying) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(fileName);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        Log.e("AudioTest", "prepare() failed");
                    }
                } else {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });

        final Intent mainIntent = new Intent(this, FriendsListActivity.class);

        goodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //TODO send data to server
                getAudioFromServer();
                //startActivity(mainIntent);
            }
        });

        badBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //TODO send data to server
                startActivity(mainIntent);
            }
        });
    }

    public void getAudioFromServer() {
        invokeWS();
    }

    public void invokeWS() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getApplicationContext(), "http://mudgea3.cloudapp.net:5000/api/audio/1", null, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                String bString = null;
                try {
                    bString = response.getString("audio");
                    response.getInt("audio");
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }
                Log.v("audio is ", ""+bString.getBytes());
                Log.v("audio is ", ""+bString.substring(2, bString.length() - 1));
                byte[] data = Base64.decode(bString.substring(2, bString.length() - 1), Base64.DEFAULT);
                //byte[] byteArray = Base64.decodeBase64(string.getBytes());



                try {
                    FileOutputStream fos = new FileOutputStream(fileName);
                    fos.write(data);
                    fos.close();
                } catch(Exception e) {
                    Log.e("file error", e.getMessage());
                }

                //writeByteArrayToFile(bString);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("REST audio ERROR", "tried to get audio " + statusCode);
                Log.e("error", errorResponse.toString());
            }
        });
    }


    public void writeByteArrayToFile(String content) {


        String strFilePath = fileName;
        try {
            FileOutputStream f1 = new FileOutputStream(strFilePath);

            //f1.write("content");
            f1.close();
        }
        catch(FileNotFoundException ex)   {
            System.out.println("FileNotFoundException : " + ex);
        }
        catch(IOException ioe)  {
            System.out.println("IOException : " + ioe);
        }
    }

}