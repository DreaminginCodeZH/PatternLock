/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

/*
 * Adapted from com.android.internal.widget.LockPatternUtils at
 * 2cb687e7b9d0cbb1af5ba753453a9a05350a100e.
 */

package me.zhanghai.android.patternlock;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class PatternUtils {

    private PatternUtils() {}

    private static String bytesToString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private static byte[] stringToBytes(String string) {
        return Base64.decode(string, Base64.DEFAULT);
    }

    public static byte[] patternToBytes(List<PatternView.Cell> pattern) {
        int patternSize = pattern.size();
        byte[] bytes = new byte[patternSize];
        for (int i = 0; i < patternSize; ++i) {
            PatternView.Cell cell = pattern.get(i);
            bytes[i] = (byte)(cell.getRow() * 3 + cell.getColumn());
        }
        return bytes;
    }

    public static List<PatternView.Cell> bytesToPattern(byte[] bytes) {
        List<PatternView.Cell> pattern = new ArrayList<>();
        for (byte b : bytes) {
            pattern.add(PatternView.Cell.of(b / 3, b % 3));
        }
        return pattern;
    }

    /**
     * @deprecated Use {@link #patternToSha1String(List)} instead for better security.
     */
    public static String patternToString(List<PatternView.Cell> pattern) {
        return bytesToString(patternToBytes(pattern));
    }

    public static List<PatternView.Cell> stringToPattern(String string) {
        return bytesToPattern(stringToBytes(string));
    }

    private static byte[] sha1(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-1").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] patternToSha1(List<PatternView.Cell> pattern) {
        return sha1(patternToBytes(pattern));
    }

    public static String patternToSha1String(List<PatternView.Cell> pattern) {
        return bytesToString(patternToSha1(pattern));
    }
}
