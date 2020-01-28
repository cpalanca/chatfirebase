package org.izv.pgc.firebaserealtimedatabase;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.izv.pgc.firebaserealtimedatabase.receiver.ConnectivityStateReceiver;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "xyz";
    private TextInputLayout etLUser, etLPassword;
    TextInputEditText etUser, etPassword;
    CardView btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initEvents();

        //init();
        //initUser();
        //initLogin("example4@example.com", "example4");
        //initUid();
    }

    private void initComponents() {
        etLUser = findViewById(R.id.etLUser);
        etLPassword = findViewById(R.id.etLPassword);
        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.boton_aceptar);
    }

    private void initEvents() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
        etUser.addTextChangedListener(new ValidationTextWatcher(etUser));
        etPassword.addTextChangedListener(new ValidationTextWatcher(etPassword));

    }

    private void validarDatos() {
        String user = etLUser.getEditText().getText().toString();
        String password = etLPassword.getEditText().getText().toString();

        boolean a = esUserValido(user);
        boolean b = esPasswordValido(password);

        if (a && b) {
            //closeKeyboard();
            // OK, se pasa a la siguiente acción
            initLogin(user,password);
        }else{
            Toast.makeText(this, "Existen errores", Toast.LENGTH_LONG).show();

        }


    }

    private boolean esUserValido(String user){
        //Pattern patron = Pattern.compile("^[a-zA-Z0-9]+$");
        /*if (!patron.matcher(user).matches()) {
            etLUser.setError("Nombre de Usuario inválido");
            return false;
        } else {
            etLUser.setError(null);
        }*/
        if(user == null){
            etLUser.setError("Nombre de Usuario inválido");
            return false;
        }else{
            etLUser.setError(null);
        }
        return true;
    }
    private boolean esPasswordValido(String pass){
        Pattern patron = Pattern.compile("^[a-zA-Z0-9]+$");
        if (!patron.matcher(pass).matches() || pass.length() > 16) {
            etLPassword.setError("Contraseña de Usuario inválido");
            return false;
        } else {
            etLPassword.setError(null);
        }

        return true;
    }

    private class ValidationTextWatcher implements TextWatcher {

        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.etPassword:
                    esPasswordValido(""+charSequence);
                    break;
                case R.id.etUser:
                    esUserValido(""+charSequence);
                    break;
            }
        }

        public void afterTextChanged(Editable editable) {

        }
    }

    private void initLogin(String email, String password) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.v(TAG, user.getEmail());
                if (task.isSuccessful()) {
                    //Log.v(TAG, "task succesfull");
                    Toast.makeText(MainActivity.this, "Logueado como: " + user.getEmail(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.v(TAG, task.getException().toString());
                    Toast.makeText(MainActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initUser() {
        //REGISTER AND LOGIN
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //firebaseAuth.createUserWithEmailAndPassword("example@example.com", "example");
        firebaseAuth.createUserWithEmailAndPassword("example4@example.com", "example4").
                addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.v(TAG, user.getEmail());
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    private void init() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        DatabaseReference referenciaItem = database.getReference("user");


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

        //referenciaItem.setValue("valor item");
        //referenciaItem.child("uno").setValue("hola");

        String key = referenciaItem.push().getKey();
        //referenciaItem.child(key).setValue("hola1");
        //key = referenciaItem.push().getKey();
        //referenciaItem.child(key).setValue("hola2");

        //Date myDate = new Date();
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String myDate = simpleDateFormat.format(new Date());

        ChatSentence item = new ChatSentence("how are you?", "¿como estas?", "bot", "9:53:01");
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

    private ConnectivityStateReceiver connectivityStateReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        connectivityStateReceiver = new ConnectivityStateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityStateReceiver, intentFilter );
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityStateReceiver);
    }





}
