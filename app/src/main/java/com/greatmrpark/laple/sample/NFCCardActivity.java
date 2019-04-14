package com.greatmrpark.laple.sample;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.greatmrpark.laple.R;
import com.greatmrpark.laple.nfc.Counter;
import com.greatmrpark.laple.nfc.service.CardService;

public class NFCCardActivity extends AppCompatActivity {

    TextView text;
    Button buttonUpdate;
    Intent cardService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfccard);

        text = findViewById(R.id.text);
        buttonUpdate = findViewById( R.id.buttonUpdate );
        cardService = new Intent(this, CardService.class);
        startService(cardService);
    }

    @Override protected void onDestroy() {
        stopService(cardService);
    }

    public void onClickUpdate(View v) {
        text.setText("Count : " + Counter.GetCurrentCout());
    }
}
