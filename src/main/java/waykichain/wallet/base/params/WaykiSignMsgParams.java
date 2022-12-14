package waykichain.wallet.base.params;

import com.google.common.base.Charsets;
import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Sha256Hash;
import org.waykichainj.core.Utils;


@Data
public class WaykiSignMsgParams {
    private byte[] signature;
    
    private String publicKey;
    
    private final String msg;

    public WaykiSignMsgParams( String msg) {
        this.msg = msg;
        this.publicKey = "";
    }

    public final byte[] getSignatureHash() {
        byte[] msgBytes = this.msg.getBytes(Charsets.UTF_8);
        byte[] sha256_sha160 = Sha256Hash.hash(Utils.sha256hash160(msgBytes));

        return sha256_sha160;
    }

    
    public final byte[] signatureMsg(ECKey key) {
        byte[] sigHash = this.getSignatureHash();
        ECKey.ECDSASignature ecSig = key.sign(Sha256Hash.wrap(sigHash));
        this.publicKey = key.getPublicKeyAsHex();
        this.signature = ecSig.encodeToDER();

        return this.signature;
    }

    
    public final SignResult serializeSignature() {
        String signatureStr = Utils.HEX.encode(this.signature);
        SignResult signResult = new SignResult(signatureStr, this.publicKey);
        return signResult;
    }


    @Data
    public static final class SignResult {
        
        private String signature;
        
        private String publicKey;

        public SignResult( String signature,  String publicKey) {
            this.signature = signature;
            this.publicKey = publicKey;
        }
    }
}
