package waykichain.wallet.base;

import com.google.common.base.Charsets;
import org.waykichainj.core.VarInt;
import waykichain.wallet.base.types.VarIntExt;
import waykichain.wallet.util.ContractUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class HashWriter extends ByteArrayOutputStream {
    
    public final HashWriter add(String data) throws IOException {
        if (data != null) {
            byte[] arr = data.getBytes(Charsets.UTF_8);
            this.write(VarIntExt.encodeInOldWay(new VarInt(arr.length)));
            this.write(arr);
        }
        return this;
    }

    
    public final HashWriter add(byte[] data) throws IOException {
        if (data != null) {
            this.write(data);
        }
        return this;
    }

    
    public final HashWriter addCdpAssets(HashMap<String, Long> map) {
        map.forEach((key, value)->{
            try {
                this.add(key);
                this.add(VarIntExt.encodeInOldWay(new VarInt(value)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return this;
    }

    
    public final HashWriter add( OperVoteFund[] operVoteFund) throws IOException {
        this.writeCompactSize((long)operVoteFund.length);

        for(OperVoteFund oper : operVoteFund) {
            this.write(VarIntExt.encodeInOldWay(new VarInt((long)oper.getVoteType())));
            this.write(VarIntExt.encodeInOldWay(new VarInt(33L)));
            this.write(oper.getPubKey());
            this.write(VarIntExt.encodeInOldWay(new VarInt(oper.getVoteValue())));
        }
        return this;
    }

    
    public final HashWriter add(int data) {
        this.write(data);
        return this;
    }

    
    public final HashWriter writeRegId( String regIdStr) throws IOException {
        WaykiRegId regId = this.parseRegId(regIdStr);

        byte[] heightBytes = VarIntExt.encodeInOldWay(new VarInt(regId.getRegHeight()));
        byte[] indexBytes = VarIntExt.encodeInOldWay(new VarInt(regId.getRegIndex()));
        long regIdLen = (long)heightBytes.length + (long)indexBytes.length;
        this.write(VarIntExt.encodeInOldWay(new VarInt(regIdLen)));
        this.write(heightBytes);
        this.write(indexBytes);
        return this;
    }

    
    public final HashWriter writeUserId( String userIdStr, byte[] pubKey) throws IOException {
        WaykiRegId regId = this.parseRegId(userIdStr);
        if (regId != null) {
            this.writeRegId(userIdStr);
        } else if (pubKey != null) {
            this.write(pubKey.length);
            this.write(pubKey);
        }

        return this;
    }

    
    public final HashWriter addAsset( CAsset asset) throws IOException {
        HashWriter buff = new HashWriter();
        int mintable = asset.isMinTable() ? 1 : 0;
        buff.add(asset.getSymbol());
        buff.writeRegId(asset.getOwnerRegid());
        buff.add(asset.getName());
        buff.write(mintable);
        buff.write(VarIntExt.encodeInOldWay(new VarInt(asset.getTotalSupply())));
        this.write(buff.toByteArray());
        return this;
    }

    
    public final HashWriter updateAsset( AssetUpdateData data) throws IOException {
        switch (data.getEnumAsset().getType()) {
            case 1:
                this.write(1);
                this.writeRegId(data.getValue().toString());
                break;
            case 2:
                this.write(2);
                this.add(data.getValue().toString());
                break;
            case 3:
                this.write(3);
                byte[] amount = VarIntExt.encodeInOldWay(new VarInt((Long)data.getValue()));
                this.write(amount);
        }
        return this;
    }

    
    public final HashWriter writeScript( byte[] script,  String description) throws IOException {
        HashWriter buff = new HashWriter();
        buff.writeCompactSize((long)script.length);
        buff.write(script);
        buff.writeCompactSize((long)description.length());
        buff.write(description.getBytes(Charsets.UTF_8));
        this.writeCompactSize((long)buff.toByteArray().length);
        this.write(buff.toByteArray());
        return this;
    }

    
    public final HashWriter writeCompactSize(long len) throws IOException {
        byte[] arr1;
        if (len < (long)253) {
            arr1 = new byte[]{(byte)len};
            this.write(arr1);
        } else {
            byte[] arr2;
            if (len < (long)65536) {
                arr1 = new byte[]{(byte)253};
                this.write(arr1);
                arr2 = ContractUtil.unLongToShortByteArray(len, true);
                this.write(arr2);
            } else if (len < 4294967296L) {
                arr1 = new byte[]{(byte)254};
                this.write(arr1);
                arr2 = ContractUtil.unLongToIntByteArray(len, true);
                this.write(arr2);
            } else {
                arr1 = new byte[]{(byte)255};
                this.write(arr1);
                arr2 = ContractUtil.longToBytes(len);
                this.write(arr2);
            }
        }

        return this;
    }

    public final WaykiRegId parseRegId( String regId) {
        String[] arr = regId.split("-");
        if (arr.length > 1) {
            if (!this.intOrString((String)arr[0])) {
                return null;
            } else if (!this.intOrString((String)arr[1])) {
                return null;
            } else {
                long height = Long.parseLong((String)arr[0]);
                long index = Long.parseLong((String)arr[1]);
                return new WaykiRegId(height, index);
            }
        } else {
            return null;
        }
    }

    public final boolean intOrString( String str) {
        Integer v = this.toIntOrNull(str);
        return v != null;
    }

    private static Integer toIntOrNull(String string) {
        Integer i = null;
        try {
            i = Integer.valueOf(string);
        } catch (NumberFormatException e) {
            //ignore
        }
        return i;
    }
    
    public final HashWriter addUCoinDestAddr( List<UCoinDest> dests) throws IOException {
        this.write(VarIntExt.encodeInOldWay(new VarInt((long)dests.size())));

        for(UCoinDest dest : dests){
            byte[] aa = dest.getDestAddress().getHash();
            this.write(VarIntExt.encodeInOldWay(new VarInt((long)aa.length)));
            this.write(dest.getDestAddress().getHash());
            this.add(dest.getCoinSymbol());
            this.write(VarIntExt.encodeInOldWay(new VarInt(dest.getTransferAmount())));
        }
        return this;
    }
}
