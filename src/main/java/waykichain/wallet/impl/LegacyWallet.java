package waykichain.wallet.impl;

import org.waykichainj.core.ECKey;
import org.waykichainj.core.LegacyAddress;
import org.waykichainj.params.AbstractBitcoinNetParams;
import waykichain.wallet.LegacyWalletInterface;
import waykichain.wallet.base.WalletAddress;
import waykichain.wallet.base.WaykiNetworkType;
import waykichain.wallet.base.params.*;

import java.io.IOException;

public class LegacyWallet implements LegacyWalletInterface {
    @Override
    public WalletAddress generateWalletAddress(WaykiNetworkType networkType) {
        AbstractBitcoinNetParams params = (networkType.equals(WaykiNetworkType.MAIN_NET)) ? new WaykiMainNetParams() : new WaykiTestNetParams();
        ECKey ecKey = new ECKey();
        String WIFprivateKey = ecKey.getPrivateKeyAsWiF(params);
        byte[] pubKeyHash = ecKey.getPubKeyHash();
        String address = LegacyAddress.fromPubKeyHash(params, pubKeyHash).toString();

        return new WalletAddress(ecKey, WIFprivateKey, address);
    }

    @Override
    public String createRegisterTransactionRaw(WaykiRegisterAccountTxParams params) throws IOException {
        return params.serializeTx();
    }

    @Override
    public String createCommonTransactionRaw(WaykiCommonTxParams params) throws IOException {
        return params.serializeTx();
    }

    @Override
    public String createContractTransactionRaw(WaykiContractTxParams params) throws IOException {
        return params.serializeTx();
    }

    @Override
    public String createDelegateTransactionRaw(WaykiDelegateTxParams params) throws IOException {
        return params.serializeTx();
    }

    @Override
    public String createCdpStakeTransactionRaw(WaykiCdpStakeTxParams params) throws IOException {
        return params.serializeTx();
    }

    @Override
    public String createCdpRedeemTransactionRaw(WaykiCdpRedeemTxParams params) throws IOException {
        return params.serializeTx();
    }
}
