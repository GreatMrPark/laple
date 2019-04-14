package com.greatmrpark.laple.nfc.parser;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.greatmrpark.laple.nfc.record.ParsedNdefRecord;
import com.greatmrpark.laple.nfc.record.SmartPoster;
import com.greatmrpark.laple.nfc.record.TextRecord;
import com.greatmrpark.laple.nfc.record.UriRecord;

import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {

    private NdefMessageParser() {
    }

    public static List<ParsedNdefRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();

        for (final NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            } else {
                elements.add(new ParsedNdefRecord() {
                    public String str() {
                        return new String(record.getPayload());
                    }
                });
            }
        }

        return elements;
    }
}