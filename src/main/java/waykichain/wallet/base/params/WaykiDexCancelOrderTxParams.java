package waykichain.wallet.base.params;

import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Sha256Hash;
import org.waykichainj.core.Utils;
import org.waykichainj.core.VarInt;

import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;
import waykichain.wallet.util.ByteUtil;

import java.io.IOException;

@Data
public class WaykiDexCancelOrderTxParams extends BaseSignTxParams{
    private final String userId;
    private final String orderId;

    public WaykiDexCancelOrderTxParams(long nValidHeight, long fees,  String userId, String userPubKey,  String feeSymbol,  String orderId) {
        super(feeSymbol, userPubKey, (byte[])null, nValidHeight, fees, WaykiTxType.DEX_CANCEL_ORDER_TX, 1L);
        this.userId = userId;
        this.orderId = orderId;
    }
    
    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());
        byte[] orderIdByte = Utils.HEX.decode(this.orderId);
        ByteUtil.reverse(orderIdByte);

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.userId, pubkey)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(orderIdByte);

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
        byte[] orderIdByte = Utils.HEX.decode(this.orderId);
        ByteUtil.reverse(orderIdByte);

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.userId, pubkey)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(orderIdByte)
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
