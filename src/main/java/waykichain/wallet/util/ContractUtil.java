package waykichain.wallet.util;


import java.nio.ByteBuffer;
import java.util.stream.IntStream;

public class ContractUtil {

    public final String to2HexString4byte(Long value) {
        String s = Long.toHexString(value);
        String first = "";

        if (s.length() % 2 == 1) {
            first = "0" + s.substring(0, 1);
            s = s.substring(1);
        }
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = s.length(); i > 1; i -= 2) {
            stringBuilder.append(s.substring(i-2, i));
        }

        stringBuilder.append(first);
        return this.fillZero(stringBuilder.toString(), 8);
    }

    
    public final String fillZero( String str, int maxlength) {
        int fillLenth = maxlength - str.length();
        StringBuilder sb = new StringBuilder(str);
        IntStream.range(0,fillLenth).forEach((r)->{
            sb.append("0");
        });

        return sb.toString();
    }

    public final byte[] hexString2binaryString(String hexStringIn) {
         if (hexStringIn.isEmpty())
            return null;

        String hexString = hexStringIn.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];

        IntStream.range(0,length).forEach((i)->{
            int pos = i * 2;
            d[i] = (byte)(charToByte(hexChars[pos]) << 4 | (this.charToByte(hexChars[pos + 1])));
        });

        return d;
    }

    public byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    
    public String to2HexString( Long valHex) {
        String s = Long.toHexString(valHex);
        String first = "";

        if (s.length() % 2 == 1) {
            first = "0" + s.substring(0, 1);
            s = s.substring(1);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = s.length(); i > 1; i -= 2) {
            stringBuilder.append(s.substring(i-2, i));
        }

        stringBuilder.append(first);
        return this.fillZero(stringBuilder.toString(), 8);
    }

    
    public final byte[] transferWRC20Contract(long amount,  String destAddress) {
        String hexWrc20Amount = this.to2HexString(amount * 100000000L);
        String header = "f0160000";
        String destAddr = this.toHexString(destAddress);
        String contract = header + destAddr + hexWrc20Amount;
        byte[] contractByte = this.hexString2binaryString(contract);
        return contractByte;
    }

    public static final String toHexString(String receiver) {
        StringBuilder sb = new StringBuilder();

        IntStream.range(0,receiver.length()).forEach((i)->{
            int ch = receiver.charAt(i);
            String hexString = Integer.toHexString(ch);
            sb.append(hexString);
        });

        return sb.toString();
    }

    public static final byte[] longToBytes(long receiver) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, receiver);
        return buffer.array();
    }

    public static final byte[] unLongToShortByteArray(long receiver, boolean littleEndian) {
        byte[] bytes = new byte[2];

        IntStream.range(0,2).forEach((i)->{
            int i1 = (littleEndian ? i : 1 - i) << 3;
            bytes[i] = (byte)((int)(receiver >> i1 & 255L));
        });

        return bytes;
    }

    public static final byte[] unLongToIntByteArray(long $receiver, boolean littleEndian) {
        byte[] bytes = new byte[4];

        IntStream.range(0,4).forEach((i)-> {
            int i1 = (littleEndian ? i : 3 - i) << 3;
            bytes[i] = (byte) ((int) ($receiver >> i1 & 255L));
        });

        return bytes;
    }
}
