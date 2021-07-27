package com.acc.gluon.utilities;

import java.util.Base64;
import java.util.UUID;

// see: https://stackoverflow.com/questions/772802/storing-uuid-as-base64-string

public class UUIDUtils {

    public static String asBase64(UUID uuid) {
        return Base64.getUrlEncoder().encodeToString(asByteArray(uuid));
    }

    public static String asBase64Trimmed(UUID uuid) {
        return asBase64(uuid).split("=")[0];
    }

    public static byte[] asByteArray(UUID uuid) {

        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;

    }

    public static UUID toUUID(byte[] byteArray) {

        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (byteArray[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (byteArray[i] & 0xff);

        return new UUID(msb, lsb);
    }
}
