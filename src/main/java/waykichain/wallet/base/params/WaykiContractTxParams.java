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
public class WaykiContractTxParams extends BaseSignTxParams{
    private final long value;
    private final String srcRegId;
    private final String destRegId;
    private final byte[] vContract;

    public WaykiContractTxParams(String userPubKey, long nValidHeight, long fees, long value, String srcRegId, String destRegId,  byte[] vContract, String feeSymbol) {
        super(feeSymbol, userPubKey, (byte[])null, nValidHeight, fees, WaykiTxType.TX_CONTRACT, 1L);
        this.value = value;
        this.srcRegId = srcRegId;
        this.destRegId = destRegId;
        this.vContract = vContract;
    }
    
    
    
    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.srcRegId, pubkey)
                .add(this.destRegId)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.value)))
                .writeCompactSize(vContract.length)
                .add(vContract);

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
                .writeUserId(this.srcRegId, pubkey)
                .add(this.destRegId)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(VarIntExt.encodeInOldWay(new VarInt(this.value)))
                .writeCompactSize(vContract.length)
                .add(vContract)
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
