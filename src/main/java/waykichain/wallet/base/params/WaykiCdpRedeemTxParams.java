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
import java.util.HashMap;
import java.util.Map;

@Data
public class WaykiCdpRedeemTxParams extends BaseSignTxParams{
    private final String userId;
    private final String cdpTxid;
    private final long sCoinsToRepay;
    private final HashMap<String, Long> assetMap;

    public WaykiCdpRedeemTxParams(long nValidHeight, long fees, String userId, String userPubKey, String cdpTxid, String feeSymbol, long sCoinsToRepay, HashMap<String, Long> assetMap) {
        super(feeSymbol, userPubKey, (byte[])null, nValidHeight, ((fees == 0L) ? 1000L : fees), WaykiTxType.TX_CDPREDEEM, 1L);
        this.userId = userId;
        this.cdpTxid = (cdpTxid == null || cdpTxid.isEmpty()) ? "0000000000000000000000000000000000000000000000000000000000000000" : cdpTxid;
        this.sCoinsToRepay = sCoinsToRepay;
        this.assetMap = assetMap;
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
                .add(VarIntExt.encodeInOldWay(new VarInt(this.sCoinsToRepay)))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.assetMap.size())))
                .addCdpAssets(assetMap);

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

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(userId, pubkey)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(cdpTxHex)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.sCoinsToRepay)))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.assetMap.size())))
                .addCdpAssets(assetMap)
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
