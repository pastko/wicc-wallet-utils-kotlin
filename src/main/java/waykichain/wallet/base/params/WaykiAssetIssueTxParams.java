package waykichain.wallet.base.params;

import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Sha256Hash;
import org.waykichainj.core.Utils;
import org.waykichainj.core.VarInt;
import waykichain.wallet.base.CAsset;
import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;
import waykichain.wallet.util.Messages;
import waykichain.wallet.util.TokenException;

import java.io.IOException;

@Data
public class WaykiAssetIssueTxParams extends BaseSignTxParams {
    private final String SYMBOL_MATCH="[A-Z]{6,7}$";
    private final String srcRegId;
    private final CAsset asset;

    public WaykiAssetIssueTxParams(long nValidHeight, long fees, String srcRegId, String feeSymbol, CAsset asset) {
        super(feeSymbol, (String)null, (byte[])null, nValidHeight, fees, WaykiTxType.ASSET_ISSUE_TX, 1L);
        this.srcRegId = srcRegId;
        this.asset = asset;
    }
    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeRegId(this.srcRegId)
                .add(this.getFeeSymbol())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .addAsset(this.asset);
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
        Boolean symbolMatch = this.asset.getSymbol().matches(SYMBOL_MATCH);

        if (!symbolMatch) {
            throw new TokenException(Messages.SYMBOLNOTMATCH);
        } else {

            HashWriter ss = new HashWriter();
            ss.add(VarIntExt.encodeInOldWay(new VarInt((long)this.getNTxType().getType())))
                    .add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                    .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                    .writeRegId(this.srcRegId)
                    .add(this.getFeeSymbol())
                    .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                    .addAsset(this.asset)
                    .writeCompactSize((long)this.getSignature().length)
                    .add(this.getSignature());
            return Utils.HEX.encode(ss.toByteArray());
        }
    }

}
