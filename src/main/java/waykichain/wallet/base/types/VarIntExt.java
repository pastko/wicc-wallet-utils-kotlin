package waykichain.wallet.base.types;

import org.waykichainj.core.VarInt;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;


public class VarIntExt {
    public static final byte[] encodeInOldWay( VarInt receiver) {
        int size = size(receiver);
        byte[] tmp = new byte[(size * 8 + 6) / 7];
        ArrayList<Byte> ret = new ArrayList<Byte>();
        int len = 0;
        long n = receiver.value;

        while(true) {
            long h = (len == 0) ? 0L : 128L;
            tmp[len] = (byte) ((int) (n & 127L | h));
            if (n <= (long) 127) {
                break;
            }
            n = (n >> 7) - 1L;
            len++;
        }

        do {
            ret.add(tmp[len]);
        } while(len-- > 0);

        return toByteArray(ret);
    }

    private static byte[] toByteArray(Collection<Byte> bytes){
        byte[] result = new byte[bytes.size()];
        int index = 0;
        for (byte element : bytes)
            result[index++] = element;
        return result;
    }

    public static final int size( VarInt receiver) {
        int ret = 0;
        long n = receiver.value;

        while(true) {
            ret++;
            if (n <= (long)127)
                break;;

            n = (n >> 7) - 1L;
        }
        return ret;
    }
    
    public static final VarInt decode(VarInt receiver, ByteArrayInputStream array) {
        long n = 0L;

        while(true) {
            long c = (long)array.read();
            n = n << 7 | c & 127L;
            if ((c & 128L) == 0L) {
                n++;
            }else return new VarInt(n);
        }
    }
}
