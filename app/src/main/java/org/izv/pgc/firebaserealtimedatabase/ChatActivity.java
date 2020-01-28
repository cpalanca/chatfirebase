package org.izv.pgc.firebaserealtimedatabase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.izv.pgc.firebaserealtimedatabase.apibot.ChatterBot;
import org.izv.pgc.firebaserealtimedatabase.apibot.ChatterBotFactory;
import org.izv.pgc.firebaserealtimedatabase.apibot.ChatterBotSession;
import org.izv.pgc.firebaserealtimedatabase.apibot.ChatterBotType;
import org.izv.pgc.firebaserealtimedatabase.apibot.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = "xyz";
    private LinearLayout clMain;
    private TextToSpeech mTts;
    private ImageView mic, btSend;
    private String textoUser;
    private EditText etText, etMessagaList;
    private TextView tvMessagaList;
    ArrayList<String> result = null;
    private ScrollView scroll;
    private String sentenceEn, sentenceEs, talker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_chat);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        initComponents();
        initEvents();


    }

    public static String getShortTime() {
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        Date date = new Date();
        return dateFormat.format(date);
    }


    private void initEvents() {

        // TextToSpeech.OnInitListener
        mTts = new TextToSpeech(this, this);

        etText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    final Handler handler;
                    handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            scroll.smoothScrollTo(0, 500);

                        }
                    };
                    handler.postDelayed(r, 200);
                }
            }
        });


        btSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                textoUser = etText.getText().toString();
                sentenceEs = textoUser;
                tvMessagaList.setText(tvMessagaList.getText() + "\n" +"user> " + textoUser + "\n");
                System.out.println("user> " + textoUser);
                talker = "user";
                new TranslateToEng(textoUser).execute();
                //new Chat(textoUser).execute();
                scroll.fullScroll(View.FOCUS_DOWN);

                etText.setText("");
            }
        });

        mic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Toast.makeText(v.getContext(), "Say Something...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 5);
                } else {
                    Toast.makeText(v.getContext(), "Your Device Doesn't Support Speech Intent", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initComponents() {
        clMain = findViewById(R.id.activity_main);
        mic = findViewById(R.id.mic);
        tvMessagaList = findViewById(R.id.tvMessagaList);
        btSend = findViewById(R.id.btSend);
        etText = findViewById(R.id.etText);
        scroll = findViewById(R.id.scrollView);

    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.v("UTF ENCODING EXCEPTION", "UTF-8 should always be supported", e);
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==5) {
            if(resultCode==RESULT_OK && data!=null) {
                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tvMessagaList.setText(tvMessagaList.getText() + "\n" + "user> " + result.get(0) + "\n");
                new TranslateToEng(result.get(0)).execute();
                scroll.fullScroll(View.FOCUS_DOWN);
                etText.setText("");
            }
        }
    }



    private void talkMe(String text) {
        mTts.speak(text,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);

    }

    private void chat(String userText){
        try{
            ChatterBotFactory factory = new ChatterBotFactory();

            ChatterBot bot1 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            ChatterBotSession bot1session = bot1.createSession();

            String s;

                s = bot1session.think(userText);
                sentenceEn = s;
                talker="bot";
                new TranslateToEsp(s).execute();
                //s = decomposeJson(s);
                //talkMe(s);
                //System.out.println("bot1> " + s);

                //tvLog.setText(tvLog.getText().toString() + s);

        }catch (Exception e){

        }
    }

    @Override
    public void onInit(int status) {

        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.getDefault());
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.
                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                btSend.setEnabled(true);
                // Greet the user.
                talkMe("¿En que puedo ayudarle?");
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    public String decomposeJson(String json){
        String translationResult = "Could not get";
        try {
            JSONArray arr = new JSONArray(json);
            JSONObject jObj = arr.getJSONObject(0);
            translationResult = jObj.getString("translations");
            JSONArray arr2 = new JSONArray(translationResult);
            JSONObject jObj2 = arr2.getJSONObject(0);
            translationResult = jObj2.getString("text");
        } catch (JSONException e) {
            translationResult = e.getLocalizedMessage();
        }
        return translationResult;
    }

    private class Chat extends AsyncTask <String,Void,Void> {
        String v;

        public Chat(String s){
            this.v = s;
        }

        @Override
        protected Void doInBackground(String... strings) {
            chat(v);
            return null;
        }
    }

    private class TranslateToEng extends AsyncTask<Void, Void, Void>{
        HashMap<String, String> httpBodyParams;
        private String parameters, s;

        public TranslateToEng(String message) {
            httpBodyParams = new HashMap<>();
            httpBodyParams.put("fromLang", "es");
            httpBodyParams.put("to", "en");
            httpBodyParams.put("text", message);

            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : httpBodyParams.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");
                try {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result.append("=");
                try {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            parameters = result.toString();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = Utils.postHttp("https://www.bing.com/ttranslatev3?", parameters);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("xyz", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            s = decomposeJson(s);
            sentenceEn = s;
            initUid(sentenceEn, sentenceEs, talker);
            new Chat(s).execute();
        }
    }

    private class TranslateToEsp extends AsyncTask<Void, Void, Void>{
        HashMap<String, String> httpBodyParams;
        private String parameters, s;

        public TranslateToEsp(String message) {

            httpBodyParams = new HashMap<>();
            httpBodyParams.put("fromLang", "en");
            httpBodyParams.put("to", "es");
            httpBodyParams.put("text", message);
            Log.v("HOLA","1");
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : httpBodyParams.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");
                try {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result.append("=");
                try {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            parameters = result.toString();
            System.out.println(parameters);
            Log.v("HOLA","2");

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.v("HOLA","asdfasfasd");
                s = Utils.postHttp("https://www.bing.com/ttranslatev3?", parameters);
                System.out.println(s);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("xyz", "Error: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            s = decomposeJson(s);
            System.out.println("TRADUCCIONESP: "+s);
            sentenceEs = s;
            initUid(sentenceEn,sentenceEs,talker);
            tvMessagaList.setText(tvMessagaList.getText()+"\n"+"bot> " + s +"\n");
            talkMe(s);
        }
    }

    private void initUid(String sentenceEn, String sentenceEs, String talker) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference referenciaItem = database.getReference("user/" + uid);


        // Se pueden agregar 'listeners':
        referenciaItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "data changed: " + dataSnapshot.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "error: " + databaseError.toException());
            }
        });

        String key = referenciaItem.push().getKey();
        //Date myDate = new Date();
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String myDate = simpleDateFormat.format(new Date());
        Calendar calendario = Calendar.getInstance();
        int hora, minutos, segundos;
        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);

        ChatSentence item = new ChatSentence(sentenceEn, sentenceEs, talker, hora + ":" + minutos + ":" + segundos);
        Map<String, Object> map = new HashMap<>();
        key = referenciaItem.child(""+myDate).push().getKey();
        map.put(myDate+"/" + key, item.toMap());
        referenciaItem.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });;

    }


    // https://www.bing.com/ttranslatev3?isVertical=1&&IG=C7C2278972724E04852E45BF3FA519D1&IID=translator.5026.2
    // POST
    // fromLang=es
    // text=soy programador
    // to=en



}
