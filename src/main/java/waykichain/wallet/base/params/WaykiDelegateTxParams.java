package waykichain.wallet.base.params;

import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Sha256Hash;
import org.waykichainj.core.Utils;
import org.waykichainj.core.VarInt;
import waykichain.wallet.base.CoinType;
import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.OperVoteFund;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;

import java.io.IOException;

@Data
public class WaykiDelegateTxParams extends BaseSignTxParams{
    
    private final String srcRegId;
    
    private OperVoteFund[] voteLists;

    public WaykiDelegateTxParams( String srcRegId,  String pubKey, OperVoteFund[] voteLists, long fees, long nValidHeight) {
        super(CoinType.WICC.getType(), pubKey, (byte[])null, nValidHeight, fees, WaykiTxType.TX_DELEGATE, 1L);
        this.srcRegId = srcRegId;
        this.voteLists = voteLists;
    }
    
    
    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.srcRegId, pubkey)
                .add(this.voteLists)
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
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.srcRegId, pubkey)
                .add(this.voteLists)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .writeCompactSize((long)this.getSignature().length)
                .add(this.getSignature());

        return Utils.HEX.encode(ss.toByteArray());
    }
}
