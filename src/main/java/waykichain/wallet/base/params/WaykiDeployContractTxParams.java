package waykichain.wallet.base.params;

import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Sha256Hash;
import org.waykichainj.core.Utils;
import org.waykichainj.core.VarInt;
import waykichain.wallet.base.CoinType;
import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;

import java.io.IOException;

@Data
public class WaykiDeployContractTxParams extends BaseSignTxParams{

    private final String srcRegId;
    private final byte[] vContract;
    private final String description;

    public WaykiDeployContractTxParams(long nValidHeight, long fees, String srcRegId, byte[] vContract, String description) {
        super(CoinType.WICC.getType(), (String)null, (byte[])null, nValidHeight, fees, WaykiTxType.LCONTRACT_DEPLOY_TX, 1L);
        this.srcRegId = srcRegId;
        this.vContract = vContract;
        this.description = description;
    }
    
    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeRegId(this.srcRegId)
                .writeScript(this.vContract, this.description)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())));

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

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeRegId(this.srcRegId)
                .writeScript(this.vContract, this.description)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
