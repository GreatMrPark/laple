package com.greatmrpark.laple.nfc.record;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.greatmrpark.laple.nfc.parser.NdefMessageParser;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * A representation of an NFC Forum "Smart Poster".
 */
public class SmartPoster implements ParsedNdefRecord {

    /**
     * NFC 포럼 스마트 포스터 레코드 유형 정의 섹션 3.2.1.
     *
     * 서비스에 대한 타이틀 레코드
     * (여러 언어로 된 이들 중 많은 수가있을 수 있지만 반복해서는 안됩니다.)
     * 이 레코드는 선택 사항입니다.
     */
    private final TextRecord mTitleRecord;

    /**
     * NFC 포럼 스마트 포스터 레코드 유형 정의 섹션 3.2.1.
     *
     * "The URI record. This is the core of the Smart Poster, and all other
     * records are just metadata about this record. There MUST be one URI record
     * and there MUST NOT be more than one."
     */
    private final UriRecord mUriRecord;

    /**
     * NFC 포럼 스마트 포스터 레코드 유형 정의 섹션 3.2.1.
     *
     * 작업 레코드입니다. 이 기록은 서비스가 어떻게 취급되어야하는지 설명합니다.
     * 예를 들어, 작업은 장치가 URI를 책갈피로 저장하거나 브라우저를 열어야 함을 나타낼 수 있습니다.
     * 조치 레코드는 선택 사항입니다.
     * 존재하지 않으면 장치는 서비스를 어떻게할지 결정할 수 있습니다.
     * 행동 기록이 존재한다면, 그것은 강력한 제안으로 취급되어야한다;
     * UI 디자이너는이를 무시할 수 있지만 그렇게하면 기기마다 다른 사용자 경험을 유도하게됩니다.
     *
     */
    private final RecommendedAction mAction;

    /**
     * NFC 포럼 스마트 포스터 레코드 유형 정의 섹션 3.2.1.
     *
     * 유형 레코드.
     * URI가 (예를 들어 URL을 통해) 외부 엔티티를 참조하는 경우,
     * 유형 레코드는 엔티티의 MIME 유형을 선언하는 데 사용될 수 있습니다.
     * 이것은 연결을 열기 전에 어떤 종류의 객체를 기대할 수 있는지를 모바일 장치에 알리는 데 사용할 수 있습니다.
     * 유형 레코드는 선택 사항입니다.
     */
    private final String mType;

    public SmartPoster(UriRecord uri, TextRecord title, RecommendedAction action, String type) {
        mUriRecord = Preconditions.checkNotNull(uri);
        mTitleRecord = title;
        mAction = Preconditions.checkNotNull(action);
        mType = type;
    }

    public UriRecord getUriRecord() {
        return mUriRecord;
    }

    /**
     * 스마트 포스터의 제목을 반환합니다. 이것은 null 일 가능성이 있습니다.
     */
    public TextRecord getTitle() {
        return mTitleRecord;
    }

    public static SmartPoster parse(NdefRecord record) {
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_SMART_POSTER));
        try {
            NdefMessage subRecords = new NdefMessage(record.getPayload());
            return parse(subRecords.getRecords());
        } catch (FormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static SmartPoster parse(NdefRecord[] recordsRaw) {
        try {
            Iterable<ParsedNdefRecord> records = NdefMessageParser.getRecords(recordsRaw);
            UriRecord uri = Iterables.getOnlyElement(Iterables.filter(records, UriRecord.class));
            TextRecord title = getFirstIfExists(records, TextRecord.class);
            RecommendedAction action = parseRecommendedAction(recordsRaw);
            String type = parseType(recordsRaw);
            return new SmartPoster(uri, title, action, type);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isPoster(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String str() {
        if (mTitleRecord != null) {
            return mTitleRecord.str() + "\n" + mUriRecord.str();
        } else {
            return mUriRecord.str();
        }
    }

    /**
     * 요소가 존재하지 않는 경우는 type의 인스턴스 인 요소의 최초의 요소를 돌려줍니다.
     */
    private static <T> T getFirstIfExists(Iterable<?> elements, Class<T> type) {
        Iterable<T> filtered = Iterables.filter(elements, type);
        T instance = null;
        if (!Iterables.isEmpty(filtered)) {
            instance = Iterables.get(filtered, 0);
        }
        return instance;
    }

    public enum RecommendedAction {
        UNKNOWN((byte) -1), DO_ACTION((byte) 0), SAVE_FOR_LATER((byte) 1), OPEN_FOR_EDITING(
                (byte) 2);

        private static final ImmutableMap<Byte, RecommendedAction> LOOKUP;
        static {
            ImmutableMap.Builder<Byte, RecommendedAction> builder = ImmutableMap.builder();
            for (RecommendedAction action : RecommendedAction.values()) {
                builder.put(action.getByte(), action);
            }
            LOOKUP = builder.build();
        }

        private final byte mAction;

        private RecommendedAction(byte val) {
            this.mAction = val;
        }

        private byte getByte() {
            return mAction;
        }
    }

    private static NdefRecord getByType(byte[] type, NdefRecord[] records) {
        for (NdefRecord record : records) {
            if (Arrays.equals(type, record.getType())) {
                return record;
            }
        }
        return null;
    }

    private static final byte[] ACTION_RECORD_TYPE = new byte[] {'a', 'c', 't'};

    private static RecommendedAction parseRecommendedAction(NdefRecord[] records) {
        NdefRecord record = getByType(ACTION_RECORD_TYPE, records);
        if (record == null) {
            return RecommendedAction.UNKNOWN;
        }
        byte action = record.getPayload()[0];
        if (RecommendedAction.LOOKUP.containsKey(action)) {
            return RecommendedAction.LOOKUP.get(action);
        }
        return RecommendedAction.UNKNOWN;
    }

    private static final byte[] TYPE_TYPE = new byte[] {'t'};

    private static String parseType(NdefRecord[] records) {
        NdefRecord type = getByType(TYPE_TYPE, records);
        if (type == null) {
            return null;
        }
        return new String(type.getPayload(), Charsets.UTF_8);
    }
}