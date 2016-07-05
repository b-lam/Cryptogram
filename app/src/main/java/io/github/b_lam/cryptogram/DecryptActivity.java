package io.github.b_lam.cryptogram;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText nVal = (EditText) findViewById(R.id.editTextN);
        final EditText eVal = (EditText) findViewById(R.id.editTextE);
        final EditText dVal = (EditText) findViewById(R.id.editTextD);
        Button btnDecrypt = (Button) findViewById(R.id.btnDecrypt);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnLoad = (Button) findViewById(R.id.btnLoad);
        message = (TextView) findViewById(R.id.textViewMessage);
        cipherMessage = (EditText) findViewById(R.id.editTextCipher);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("keys", Context.MODE_PRIVATE);
                nVal.setText(sharedPreferences.getString("nKey", ""));
                eVal.setText(sharedPreferences.getString("eKey", ""));
                dVal.setText(sharedPreferences.getString("dKey", ""));

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
                Snackbar.make(view, "Generating new keys...", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                rsa = new RSA(600);
                rsa.generateKeys();
                nVal.setText(rsa.getN().toString());
                eVal.setText(rsa.getE().toString());
                dVal.setText(rsa.getD().toString());
            }
        });
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

}
