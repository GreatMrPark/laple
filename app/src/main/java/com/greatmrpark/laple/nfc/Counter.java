package com.greatmrpark.laple.nfc;

public class Counter {
    private static int cnt = 0;
    public static void AddOne() {cnt++;}
    public static int GetCurrentCout() {return cnt;}
}
