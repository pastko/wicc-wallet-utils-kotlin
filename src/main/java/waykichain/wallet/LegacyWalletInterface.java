// LegacyWalletInterface$WhenMappings.java
package waykichain.wallet;

import org.waykichainj.core.NetworkParameters;
import org.waykichainj.core.Utils;
import waykichain.wallet.base.HashReader;
import waykichain.wallet.base.WalletAddress;
import waykichain.wallet.base.WaykiNetworkType;
import waykichain.wallet.base.WaykiTxType;
import waykichain.wallet.base.params.*;

import java.io.IOException;

import static waykichain.wallet.base.WaykiTxType.*;

public interface LegacyWalletInterface {
    /**
     * creation of wallet priv/pub key pair and corresponding address
     */
    WalletAddress generateWalletAddress(WaykiNetworkType networkType);

    String createRegisterTransactionRaw(WaykiRegisterAccountTxParams params) throws IOException;

    
    String createCommonTransactionRaw( WaykiCommonTxParams params) throws IOException;

    
    String createContractTransactionRaw( WaykiContractTxParams params) throws IOException;

    
    String createDelegateTransactionRaw( WaykiDelegateTxParams params) throws IOException;

    
    String createCdpStakeTransactionRaw( WaykiCdpStakeTxParams params) throws IOException;

    
    String createCdpRedeemTransactionRaw( WaykiCdpRedeemTxParams params) throws IOException;

    
    default String createCdpLiquidateTransactionRaw(WaykiCdpLiquidateTxParams params) throws IOException {
        return params.serializeTx();
    };

    
    default String createUCoinTransactionRaw(WaykiUCoinTxParams params) throws IOException {
        return params.serializeTx();
    };

    /**
     * offline creation of Dex limit Transaction raw data
     */
    default String createDexLimitTransactionRaw(WaykiDexLimitTxParams params) throws IOException {
        return params.serializeTx();
    };

    /**
     * offline creation of Dex market Transaction raw data
     */
    default String createDexMarketTransactionRaw(WaykiDexMarketTxParams params) throws IOException {
        return params.serializeTx();
    };

    /**
     * offline creation of Dex cancel order Transaction raw data
     */
    default String createDexCancelOrderTransactionRaw(WaykiDexCancelOrderTxParams params) throws IOException {
        return params.serializeTx();
    };

    /**
     * offline creation of UCoin contract Invoke
     */
    default String createUCoinContractInvokeRaw(WaykiUCoinContractTxParams params) throws IOException {
        return params.serializeTx();
    };

    
    default String createAssetIssueRaw(WaykiAssetIssueTxParams params) throws IOException {
        return params.serializeTx();
    };

    
    default String createAssetUpdateRaw(WaykiAssetUpdateTxParams params) throws IOException {
        return params.serializeTx();
    };

    
    default String createDeployContractRaw(WaykiDeployContractTxParams params) throws IOException {
        return params.serializeTx();
    };

    
    default WaykiSignMsgParams.SignResult createSignMessage(WaykiSignMsgParams params){
        return params.serializeSignature();
    };

    
    default WaykiVerifyMsgSignParams.VerifyMsgSignatureResult verifyMsgSignature(WaykiVerifyMsgSignParams params){
        return params.verifyMsgSignature();
    };
    
    default BaseSignTxParams parseTransactionRaw(String rawtx, NetworkParameters params){
        HashReader hash = new HashReader(Utils.HEX.decode(rawtx));
        BaseSignTxParams ret;
        WaykiTxType nTxType = of((int)hash.readVarInt().value);
        switch (nTxType){
            case TX_UCOIN_TRANSFER:
            {
                ret = WaykiUCoinTxParams.Companion.unSerializeTx(hash, params);
                ret.setNTxType(nTxType);
            }
            break;
            case TX_REGISTERACCOUNT:
            {
                ret = WaykiRegisterAccountTxParams.Companion.unSerializeTx(hash);
                ret.setNTxType(nTxType);
            }
            break;
            case UCONTRACT_INVOKE_TX:
            {
                ret = WaykiUCoinContractTxParams.Companion.unSerializeTx(hash);
            }
            break;
            default: {
                ret = WaykiRegisterAccountTxParams.Companion.unSerializeTx(hash);
            }
        }
        return ret;
    };
}
