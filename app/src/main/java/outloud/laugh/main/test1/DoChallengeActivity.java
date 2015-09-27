package outloud.laugh.main.test1;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Hima on 9/26/2015.
 */
public class DoChallengeActivity extends Activity {
    TextView challengeDisplay;
    ImageButton recordBtn;
    Button playBtn;

    private boolean isRecording = false;
    private String fileName = null;
    private MediaRecorder mediaRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_challenge);

        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/LOL_audio_test.mp3";

        challengeDisplay = (TextView) findViewById(R.id.sentence_challenge_display);
        recordBtn = (ImageButton) findViewById(R.id.record_btn);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRecording = !isRecording;
                if(isRecording)
                    startRecording();
                else {
                    stopRecording();

                    //TODO send data to api
                    Log.v("recording", "stopped");
                    sendRecordingToServer();

                    Intent mainIntent = new Intent(getApplicationContext(), FriendsListActivity.class);
                    startActivity(mainIntent);
                }
            }
        });


    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(fileName);

        try {
            mediaRecorder.prepare();
        } catch(IOException e) {
            Log.e("AudioRecordTest", "prepare() failed");
        }

        mediaRecorder.start();
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void sendRecordingToServer() {
        JSONObject jsonParams = new JSONObject();
        try {
            File myRecording = new File(fileName);
            byte[] arr = convertFileToByteArray(myRecording);
            String outputString = Base64.encodeToString(arr, Base64.DEFAULT);
            jsonParams.put("audio", outputString);
            Log.v("audio out", outputString);
            StringEntity entity = new StringEntity(jsonParams.toString());

            invokeWS(entity);
        } catch(Exception e) { e.printStackTrace(); }
    }



    public void invokeWS(StringEntity entity) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), "http://mudgea3.cloudapp.net:5000/api/audio/1", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.v("status is", response.toString());

                } catch (Exception e) {
                    Log.e("JSON error", "errror");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("REST CALL ERROR", "tried to send audio " +statusCode);
                Log.e("audio error", errorResponse.toString());
            }
        });
    }

    public static byte[] convertFileToByteArray(File f)
    {
        byte[] byteArray = null;
        try
        {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024*8];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArray;
    }
}
