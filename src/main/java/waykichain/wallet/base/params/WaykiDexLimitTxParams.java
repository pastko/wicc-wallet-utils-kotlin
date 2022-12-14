package waykichain.wallet.base.params;

import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Sha256Hash;
import org.waykichainj.core.Utils;
import org.waykichainj.core.VarInt;
import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;

import java.io.IOException;

@Data
public class WaykiDexLimitTxParams extends BaseSignTxParams{
    private final String userId;
    private final String coinSymbol;
    private final String assetSymbol;
    private final long assetAmount;
    private final long price;

    public WaykiDexLimitTxParams(long nValidHeight, long fees,  String userId, String userPubKey,  String feeSymbol,  String coinSymbol,  String assetSymbol, long assetAmount, long price,  WaykiTxType txType) {
        super(feeSymbol, userPubKey, (byte[])null, nValidHeight, fees, txType, 1L);
        this.userId = userId;
        this.coinSymbol = coinSymbol;
        this.assetSymbol = assetSymbol;
        this.assetAmount = assetAmount;
        this.price = price;
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
                .add(this.coinSymbol)
                .add(this.assetSymbol)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.assetAmount)))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.price)));

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
                .add(this.coinSymbol)
                .add(this.assetSymbol)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.assetAmount)))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.price)))
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
