package io.github.b_lam.cryptogram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigInteger;

public class DecryptActivity extends AppCompatActivity {

    private RSA rsa;
    private String unencryptedText;
    private String encryptedText;
    EditText cipherMessage;
    TextView message;
    EditText nVal;
    EditText eVal;
    EditText dVal;
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nVal = (EditText) findViewById(R.id.editTextN);
        eVal = (EditText) findViewById(R.id.editTextE);
        dVal = (EditText) findViewById(R.id.editTextD);
        Button btnDecrypt = (Button) findViewById(R.id.btnDecrypt);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnLoad = (Button) findViewById(R.id.btnLoad);
        message = (TextView) findViewById(R.id.textViewMessage);
        cipherMessage = (EditText) findViewById(R.id.editTextCipher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialog();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("keys", Context.MODE_PRIVATE);
                nVal.setText(sharedPreferences.getString("nKey", ""));
                eVal.setText(sharedPreferences.getString("eKey", ""));
                dVal.setText(sharedPreferences.getString("dKey", ""));
                Toast.makeText(getApplicationContext(), "Loaded previously saved keys", Toast.LENGTH_SHORT).show();
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nVal.getText().length() != 0 && dVal.getText().length() != 0){
                    rsa = new RSA(new BigInteger(nVal.getText().toString()), new BigInteger(eVal.getText().toString()), new BigInteger(dVal.getText().toString()));
                    decryptText();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter a key", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Snackbar snackbar = Snackbar.make(view, "Generating new keys...", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                new Thread(new Runnable() {
                    public void run() {
                        generateKeys();
                        nVal.post(new Runnable() {
                            public void run() {
                                nVal.setText(rsa.getN().toString());
                                eVal.setText(rsa.getE().toString());
                                dVal.setText(rsa.getD().toString());
                                snackbar.dismiss();

                            }
                        });
                    }
                }).start();
            }
        });
    }

    public void generateKeys(){
        rsa = new RSA(2048);
        rsa.generateKeys();
    }

    public void decryptText(){
        if(cipherMessage.getText().toString().trim().length() != 0) {
            encryptedText = cipherMessage.getText().toString();
            unencryptedText = rsa.decrypt(encryptedText);
            message.setText(unencryptedText);
        }else{
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.decrypt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.encrypt:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Are you sure you want to save these keys?");

        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "The keys were not saved", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = getSharedPreferences("keys", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nKey", nVal.getText().toString());
                editor.putString("eKey", eVal.getText().toString());
                editor.putString("dKey", dVal.getText().toString());
                editor.apply();
                String publicKey = "n = " + nVal.getText().toString() + "\ne = " + eVal.getText().toString() + "\nd = " + dVal.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("publicKey", publicKey);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied keys to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialogBox = dialogBuilder.create();
        dialogBox.show();
    }
}
