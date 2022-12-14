package waykichain.wallet.base;

import com.google.common.base.Charsets;
import org.waykichainj.core.LegacyAddress;
import org.waykichainj.core.NetworkParameters;
import org.waykichainj.core.Utils;
import org.waykichainj.core.VarInt;
import waykichain.wallet.base.types.VarIntExt;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class HashReader extends ByteArrayInputStream {

    public HashReader(byte[] bytes) {
        super(bytes);
    }

    public HashReader(byte[] bytes, int i, int i1) {
        super(bytes, i, i1);
    }

    public final String readString() {
        VarInt len = VarIntExt.decode(new VarInt(0L), (ByteArrayInputStream)this);
        byte[] array = new byte[(int)len.value];
        this.read(array, 0, (int)len.value);
        return new String(array, Charsets.UTF_8);
    }

    public final VarInt readVarInt() {
        return VarIntExt.decode(new VarInt(0L), (ByteArrayInputStream)this);
    }

    public final long readCompactSize() throws Exception {
        long cSize = 0L;
        int chSize = this.read();
        if (chSize < 253) {
            cSize = (long)chSize;
        } else {
            if (chSize == 253) {
                Short xSize = (short)this.read();
                cSize = (long)xSize;
                if (cSize < (long)253) {
                    throw new Exception("non-canonical ReadCompactSize()");
                }
            } else if (chSize == 254) {
                int xSize = this.read();
                cSize = (long)xSize;
                if (cSize < (long)65536) {
                    throw new Exception("non-canonical ReadCompactSize()");
                }
            } else {
                Long xSize = (long)this.read();
                cSize = xSize;
                if (xSize < 4294967296L) {
                    throw new Exception("non-canonical ReadCompactSize()");
                }
            }
        }

        if (cSize > Long.MAX_VALUE) {
            throw new Exception("ReadCompactSize() : size too large");
        } else {
            return cSize;
        }
    }

    public final String readRegId() {
        VarInt regIdLen = this.readVarInt();
        long height = this.readVarInt().value;
        long index = this.readVarInt().value;
//        WaykiRegId regId = WaykiRegId(height, index);
        return height + "-" + index;
    }

    
    public final String readPubKey() {
        this.mark(this.pos);
        int keySize = this.read();
        if (keySize != 33) {
            this.reset();
            System.out.print(this.pos);
            return "";
        } else {
            byte[] array = new byte[keySize];
            this.read(array, 0, keySize);
            return Utils.HEX.encode(array);
        }
    }

    
    public final String[] readUserId() {
        String regId = "";
        String pubKey = this.readPubKey();
        if (pubKey.isEmpty()) {
            regId = this.readRegId();
        }

        return new String[]{regId, pubKey};
    }

    public final void readUCoinDestAddr( ArrayList dests,  NetworkParameters params) {
        int size = (int)this.readVarInt().value;
        dests.clear();
        IntStream.rangeClosed(1,size).forEach((i)-> {
                    VarInt len = this.readVarInt();
                    byte[] array = new byte[(int) len.value];
                    this.read(array, 0, (int) len.value);
                    LegacyAddress addr = LegacyAddress.fromPubKeyHash(params, array);
                    String coinSymbol = this.readString();
                    long transferAmount = this.readVarInt().value;
                    dests.add(new UCoinDest(addr, coinSymbol, transferAmount));
                }
        );
    }

    
    public final byte[] readByteArray() {
        VarInt len = this.readVarInt();
        byte[] array = new byte[(int)len.value];
        this.read(array, 0, (int)len.value);
        return array;
    }
}
