package waykichain.wallet.base.params;


import lombok.Data;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.Utils;

import waykichain.wallet.base.CoinType;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.util.ByteUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;


@Data
public abstract class BaseSignTxParams {

    private byte[] signature;

    private String feeSymbol;

    private String userPubKey;

    private byte[] minerPubKey;
    private long nValidHeight;
    private long fees;

    private WaykiTxType nTxType;
    private long nVersion;

    public BaseSignTxParams(String feeSymbol, String userPubKey, byte[] minerPubKey, long nValidHeight, long fees, WaykiTxType nTxType, long nVersion) {
        this.feeSymbol = (feeSymbol == null) ? CoinType.WICC.getType() : feeSymbol;
        this.userPubKey = userPubKey;
        this.minerPubKey = minerPubKey;
        this.nValidHeight = nValidHeight;
        this.fees = ( fees == 0L ) ? 10000L : fees;
        this.nTxType = (nTxType == null) ? WaykiTxType.TX_NONE : nTxType;
        this.nVersion = nVersion;
    }

    public BaseSignTxParams(){
        this.feeSymbol = CoinType.WICC.getType();
        this.nValidHeight = 0L;
        this.fees = 10000L;
        this.nTxType = WaykiTxType.TX_NONE;
        this.nVersion = 1L;
    }

    public abstract byte[] getSignatureHash() throws IOException;
    public abstract byte[] signTx(ECKey key) throws IOException;
    public abstract String serializeTx() throws IOException;

    String getTxid() throws IOException {
        byte[] signatureHash = this.getSignatureHash();
        ByteUtil.reverse(signatureHash);
        return Utils.HEX.encode(signatureHash);
    }
}
