package waykichain.wallet.base.params;

import com.google.common.base.Charsets;

import lombok.Data;
import org.waykichainj.core.*;
import javax.annotation.Nullable;



@Data
public class WaykiVerifyMsgSignParams {
    private String address;
    private boolean isValid;
    
    private final String signature;
    
    private final String publicKey;
    
    private final String msg;
    
    private final NetworkParameters netParams;

    public WaykiVerifyMsgSignParams( String signature,  String publicKey,  String msg,  NetworkParameters netParams) {
        this.signature = signature;
        this.publicKey = publicKey;
        this.msg = msg;
        this.netParams = netParams;
    }

    public final boolean checkParams() {
        if (this.publicKey.length() == 66 && this.signature.length() % 2 == 0) {
            return true;
        } else {
            System.out.println("The length of publicKey or signature error");
            return false;
        }
    }

    public final boolean checkPublicKey() {
        return this.checkParams() && ECKey.isPubKeyCanonical(Utils.HEX.decode(this.publicKey));
    }

    @Nullable
    public final String getAddressFromPublicKey() {
        if (this.checkPublicKey()) {
            ECKey ecKey = ECKey.fromPublicOnly(Utils.HEX.decode(this.publicKey));
            this.address = LegacyAddress.fromKey(this.netParams, ecKey).toBase58();
        }

        return this.address;
    }

    
    public final VerifyMsgSignatureResult verifyMsgSignature() {
        String addrFromPubKey = this.getAddressFromPublicKey();
        VerifyMsgSignatureResult verifyMsgSignatureResult = new VerifyMsgSignatureResult(false, "");
        if (addrFromPubKey != null) {
            byte[] msgBytes = this.msg.getBytes(Charsets.UTF_8);
            byte[] signatureBytes = Utils.HEX.decode(this.signature);
            byte[] data = Sha256Hash.hash(Utils.sha256hash160(msgBytes));
            this.isValid = ECKey.verify(data, signatureBytes, Utils.HEX.decode((CharSequence)this.publicKey));
            if (this.isValid) {
                verifyMsgSignatureResult = new VerifyMsgSignatureResult(this.isValid, addrFromPubKey);
                return verifyMsgSignatureResult;
            }
        }

        return verifyMsgSignatureResult;
    }


    @Data
    public static final class VerifyMsgSignatureResult {
        private boolean isValid;
        @Nullable
        private String address;

        public VerifyMsgSignatureResult(boolean isValid, @Nullable String address) {
            this.isValid = isValid;
            this.address = address;
        }
    }
}
