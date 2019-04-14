package com.greatmrpark.laple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.greatmrpark.laple.common.log.Dlog;
import com.greatmrpark.laple.sample.NFCActivity;
import com.greatmrpark.laple.sample.NFCCardActivity;
import com.greatmrpark.laple.sample.NFCReaderActivity;
import com.greatmrpark.laple.sample.NFCWriterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dlog.d("LAPLE Start");

        /**
         * NFC
         */
        Button buttonNfc = findViewById(R.id.buttonNfc); /*페이지 전환버튼*/
        buttonNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NFCActivity.class);
                startActivity(intent);
            }
        });

        /**
         * NFCCard
         */
        Button buttonNFCCard = findViewById(R.id.buttonNFCCard); /*페이지 전환버튼*/
        buttonNFCCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NFCCardActivity.class);
                startActivity(intent);
            }
        });

        Dlog.d("LAPLE Start");
    }
}
