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
public class WaykiRegisterAccountTxParams extends BaseSignTxParams{

    public WaykiRegisterAccountTxParams(String userPubKey, byte[] minerPubKey, long nValidHeight, long fees, String feeSymbol) {
        super(feeSymbol, userPubKey, minerPubKey, nValidHeight, fees, WaykiTxType.TX_REGISTERACCOUNT, 1L);
    }

    @Override
    public byte[] getSignatureHash() throws IOException {
        HashWriter ss = new HashWriter();
        byte[] pubkey = Utils.HEX.decode(this.getUserPubKey());

        ss.add(VarIntExt.encodeInOldWay(new VarInt(this.getNVersion())))
                .add(this.getNTxType().getType())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getNValidHeight())))
                .add(VarIntExt.encodeInOldWay(new VarInt(33)))
                .add(pubkey)
                .add(VarIntExt.encodeInOldWay(new VarInt(0)))
                .add(this.getMinerPubKey())
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
                .add(VarIntExt.encodeInOldWay(new VarInt(33)))
                .add(pubkey)
                .add(VarIntExt.encodeInOldWay(new VarInt(0)))
                .add(this.getMinerPubKey())
                .add(VarIntExt.encodeInOldWay(new VarInt(this.getFees())))
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
            String userPubkey = ss.readPubKey();
            ss.read();
            long fees = ss.readVarInt().value;
            byte[] signature = ss.readByteArray();

            WaykiRegisterAccountTxParams params = new WaykiRegisterAccountTxParams(userPubkey, null, nValidHeight, fees,"" );
            params.setNVersion(nVersion);
            params.setSignature(signature);
            return params;
        }
    }
}
