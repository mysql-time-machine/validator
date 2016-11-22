package com.booking.validator.utils;

/**
 * Created by psalimov on 11/22/16.
 */
public class HexEncoder {

    private static final char[] LOOKUP = "0123456789ABCDEF".toCharArray();

    public static String encode(byte[] data){

        if (data == null) return null;

        int length = data.length;

        StringBuilder sb = new StringBuilder(length * 2);

        for (int i = 0; i< length; i++){

            int b = data[i] & 0xFF;

            sb.append( LOOKUP[b >> 4] );

            sb.append( LOOKUP[b & 0xF] );

        }

        return sb.toString();

    }

}
