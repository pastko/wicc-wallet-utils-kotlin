package waykichain.wallet.base.params;

import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Sha256Hash;
import org.waykichainj.core.Utils;
import org.waykichainj.core.VarInt;
import waykichain.wallet.base.HashReader;
import waykichain.wallet.base.HashWriter;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.types.VarIntExt;

import java.io.IOException;


@Data
public class WaykiUCoinContractTxParams extends BaseSignTxParams{
    private final long value;
    
    private final String srcRegId;
    
    private final String destRegId;
    private final byte[] vContract;
    
    private final String coinSymbol;

    public WaykiUCoinContractTxParams(String userPubKey, long nValidHeight, long fees, long value, String srcRegId, String destRegId, byte[] vContract, String feeSymbol, String coinSymbol) {
        super(feeSymbol, userPubKey, (byte[])null, nValidHeight, fees, WaykiTxType.UCONTRACT_INVOKE_TX, 1L);
        this.value = value;
        this.srcRegId = srcRegId;
        this.destRegId = destRegId;
        this.vContract = vContract;
        this.coinSymbol = coinSymbol;
    }

    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .writeUserId(this.srcRegId, pubkey)
                .writeRegId(this.destRegId)
                .writeCompactSize(this.vContract.length)
                .add(vContract)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(this.getFeeSymbol())
                .add(this.coinSymbol)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.value)));
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
                .writeRegId(this.destRegId)
                .writeCompactSize(this.vContract.length)
                .add(vContract)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
                .add(this.getFeeSymbol())
                .add(this.coinSymbol)
                .add(VarIntExt.encodeInOldWay(new VarInt(this.value)))
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
                .append("[pubKey]=").append(this.getUserPubKey()).append("\n")
                .append("[fees]=").append(this.getFees()).append("\n")
                .append("[signature]=").append(Utils.HEX.encode(this.getSignature())).append("\n");

        return builder.toString();
    }

    public static class Companion{
        public static BaseSignTxParams unSerializeTx(HashReader ss){
            long nVersion = ss.readVarInt().value;
            long nValidHeight = ss.readVarInt().value;
            String[] userId = ss.readUserId();
            String srcRegId = userId[0];
            String publicKey = userId[1];
            String destRegId = ss.readRegId();
            byte[] vContract = ss.readByteArray();
            long fees = ss.readVarInt().value;
            String feeSymbol = ss.readString();
            String coinSymbol = ss.readString();
            long value = ss.readVarInt().value;
            byte[] signature = ss.readByteArray();

            WaykiUCoinContractTxParams params = new WaykiUCoinContractTxParams(publicKey, nValidHeight, fees, value, srcRegId, destRegId, vContract, feeSymbol, coinSymbol);
            params.setNVersion(nVersion);
            params.setSignature(signature);
            return params;
        }
    }
}
