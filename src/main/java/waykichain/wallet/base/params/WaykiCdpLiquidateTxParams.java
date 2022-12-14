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
public class WaykiCdpLiquidateTxParams extends BaseSignTxParams{
    private final String SYMBOL_MATCH="[A-Z]{6,7}$";
    private final String userId;
    private final String cdpTxid;
    private final long sCoinsToLiquidate;
    private final String liquidateAssetSymbol;

    public WaykiCdpLiquidateTxParams(long nValidHeight, long fees, String userId, String userPubKey, String cdpTxid, String feeSymbol, long sCoinsToLiquidate, String liquidateAssetSymbol) {
        super(feeSymbol, userPubKey, (byte[])null, nValidHeight, ((fees == 0L) ? 1000L : fees), WaykiTxType.TX_CDPLIQUIDATE, 1L);
        this.userId = userId;
        this.cdpTxid = (cdpTxid == null || cdpTxid.isEmpty()) ? "0000000000000000000000000000000000000000000000000000000000000000" : cdpTxid;
        this.sCoinsToLiquidate = sCoinsToLiquidate;
        this.liquidateAssetSymbol = liquidateAssetSymbol;
    }
    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] cdpTxHex = Utils.HEX.decode(this.cdpTxid);
        ByteUtil.reverse(cdpTxHex);
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());
        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(userId, pubkey)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(cdpTxHex)
                .add(this.liquidateAssetSymbol)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.sCoinsToLiquidate)));

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
        byte[] cdpTxHex = Utils.HEX.decode(this.cdpTxid);
        ByteUtil.reverse(cdpTxHex);
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt((long)this.getNTxType().getType())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(userId, pubkey)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(cdpTxHex)
                .add(this.liquidateAssetSymbol)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.sCoinsToLiquidate)))
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
