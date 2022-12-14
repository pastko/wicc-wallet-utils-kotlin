package waykichain.wallet.base.params;

import org.waykichainj.core.*;
import waykichain.wallet.base.HashReader;
import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.UCoinDest;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WaykiUCoinTxParams extends BaseSignTxParams{
    private final String userId;
    private final List<UCoinDest> dests;
    private final String memo;

    public WaykiUCoinTxParams(long nValidHeight, String userId, String userPubKey, List<UCoinDest> dests, String feeSymbol, long fees, String memo) {
        super(feeSymbol, userPubKey, (byte[])null, nValidHeight, fees, WaykiTxType.TX_UCOIN_TRANSFER, 1L);
        this.userId = userId;
        this.dests = dests;
        this.memo = memo;
    }

    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.userId, pubkey)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .addUCoinDestAddr(this.dests)
                .add(this.memo);

        byte[] hash = Sha256Hash.hashTwice(ss.toByteArray());
        String hashStr = Utils.HEX.encode(hash);
        System.out.println("hash: " + hashStr);

        return hash;
    }

    @Override
    public byte[] signTx(ECKey key) throws IOException {
        byte[] sigHash = this.getSignatureHash();
        ECKey.ECDSASignature ecSig = key.sign(Sha256Hash.wrap(sigHash));
        this.setSignature(ecSig.encodeToDER());

        return this.getSignature();
    }

    @Override
    public String serializeTx() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.userId, pubkey)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .addUCoinDestAddr(this.dests)
                .add(this.memo)
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }



    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("[nTxType]=").append(this.getNTxType()).append("\n")
                .append("[nVersion]=").append(this.getNVersion()).append("\n")
                .append("[nValidHeight]=").append(this.getNValidHeight()).append("\n")
                .append("[feeSymbol]=").append(this.getFees()).append("\n")
                .append("[fees]=").append(this.getFees()).append("\n")
                .append("[memo]=").append(this.memo).append("\n")
                .append("[signature]=").append(Utils.HEX.encode(this.getSignature())).append("\n");
        for(UCoinDest dest : this.dests) {
            builder.append("[destAddress]=").append(dest.getDestAddress()).append("\n")
                    .append("[coinSymbol]=").append(dest.getCoinSymbol()).append("\n")
                    .append("[transferAmount]=").append(dest.getTransferAmount()).append("\n");
        }

        return builder.toString();
    }

    public static class Companion{
        public static BaseSignTxParams unSerializeTx(HashReader ss, NetworkParameters params){
            long nVersion = ss.readVarInt().value;
            long nValidHeight = ss.readVarInt().value;
            String[] array = ss.readUserId();
            String userId = array[0];
            String pubKey = array[1];
            String feeSymbol = ss.readString();
            long fees = ss.readVarInt().value;
            ArrayList<UCoinDest> dests = new ArrayList<>();
            ss.readUCoinDestAddr(dests, params);
            String memo = ss.readString();
            byte[] signature = ss.readByteArray();

            WaykiUCoinTxParams rets = new WaykiUCoinTxParams(nValidHeight, userId, pubKey, dests, feeSymbol, fees, memo);
            rets.setNVersion(nVersion);
            rets.setSignature(signature);
            return rets;
        }
    }
}
