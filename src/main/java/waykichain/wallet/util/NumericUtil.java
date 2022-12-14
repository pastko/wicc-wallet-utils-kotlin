package waykichain.wallet.util;

import java.security.SecureRandom;

public class NumericUtil {

    public static byte[] generateRandomBytes(Integer size){
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}
