package waykichain.wallet.base.params;


import lombok.Data;
import org.waykichainj.core.*;
import org.waykichainj.params.AbstractBitcoinNetParams;
import waykichain.wallet.base.CoinType;
import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.WaykiNetworkType;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;

import java.io.IOException;

@Data
public class WaykiCommonTxParams extends BaseSignTxParams{
    private final AbstractBitcoinNetParams netParams;
    private final LegacyAddress legacyAddress;
    private final long value;
    private final String srcRegId;
    private final String memo;

    public WaykiCommonTxParams(WaykiNetworkType networkType, long nValidHeight, String pubKey, long fees, long value, String srcRegId, String destAddr, String memo) {
        super(CoinType.WICC.getType(), pubKey, (byte[])null, nValidHeight, fees, WaykiTxType.TX_COMMON, 1L);
        this.value = value;
        this.srcRegId = srcRegId;
        this.memo = memo;
        this.netParams = (networkType == WaykiNetworkType.MAIN_NET) ? new WaykiMainNetParams() : new WaykiTestNetParams();
        this.legacyAddress = LegacyAddress.fromBase58((NetworkParameters)this.netParams, destAddr);
    }
    
    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.srcRegId, pubkey)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.legacyAddress.getHash().length)))
                .add(this.legacyAddress.getHash())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.value)))
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

        ss.add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.srcRegId, pubkey)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.legacyAddress.getHash().length)))
                .add(this.legacyAddress.getHash())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.value)))
                .add(this.memo)
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
