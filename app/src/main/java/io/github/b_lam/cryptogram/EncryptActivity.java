package io.github.b_lam.cryptogram;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;

public class EncryptActivity extends AppCompatActivity {

    private RSA rsa;
    private String unencryptedText;
    private String encryptedText;
    TextView cipherMessage;
    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText nVal = (EditText) findViewById(R.id.editTextN);
        final EditText eVal = (EditText) findViewById(R.id.editTextE);
        final Button btnEncrypt = (Button) findViewById(R.id.btnEncrypt);
        message = (EditText) findViewById(R.id.editTextMessage);
        cipherMessage = (TextView) findViewById(R.id.textViewCipher);


        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nVal.getText().length() != 0 && eVal.getText().length() != 0){
                    rsa = new RSA(new BigInteger(nVal.getText().toString()), new BigInteger(eVal.getText().toString()));
                    encryptText();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter a key", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Generating new keys...", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                rsa = new RSA(1024);
//                rsa.generateKeys();
//                nVal.setText(rsa.getN().toString());
//                eVal.setText(rsa.getE().toString());
//            }
//        });


    }

    public void encryptText(){
        if(message.getText().toString().trim().length() != 0) {
            unencryptedText = message.getText().toString();
            encryptedText = rsa.encrypt(unencryptedText);
            cipherMessage.setText(encryptedText);
        }else{
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.encrypt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.decrypt:
                startActivity(new Intent(this, DecryptActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
