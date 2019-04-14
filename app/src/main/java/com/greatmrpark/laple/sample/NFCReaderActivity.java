package com.greatmrpark.laple.sample;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.greatmrpark.laple.R;
import com.greatmrpark.laple.common.log.Dlog;

import static android.nfc.NdefRecord.createMime;

public class NFCReaderActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    NfcAdapter nfcAdapter;
    TextView textNFCInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);

        /**
         * 버튼이벤트
         */
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Dlog.d("LAPLE NFC Reader Start");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // Check for available NFC Adapter
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC is available", Toast.LENGTH_LONG).show();
        }
        // Register callback
        nfcAdapter.setNdefPushMessageCallback(this, this);

        Dlog.d("LAPLE NFC Reader End");
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\nBeam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(new NdefRecord[] {
                createMime("application/vnd.com.greatmrpark.laple.sample", text.getBytes())
        });
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        textNFCInfo = (TextView) findViewById(R.id.textNFCInfo);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];

        Dlog.d("LAPLE NFC Reader End");

        Toast.makeText(this, new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_LONG).show();
        // record 0 contains the MIME type, record 1 is the AAR, if present
        textNFCInfo.setText(new String(msg.getRecords()[0].getPayload()));
    }
}
