package com.waykichain.wallet;

import org.waykichainj.core.DumpedPrivateKey;
import org.waykichainj.core.ECKey;
import org.waykichainj.core.LegacyAddress;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waykichain.wallet.base.CoinType;
import waykichain.wallet.base.UCoinDest;
import waykichain.wallet.base.WaykiNetworkType;
import waykichain.wallet.base.WaykiWallet;
import waykichain.wallet.base.params.WaykiCommonTxParams;
import waykichain.wallet.base.params.WaykiTestNetParams;
import waykichain.wallet.base.params.WaykiUCoinContractTxParams;
import waykichain.wallet.base.params.WaykiUCoinTxParams;
import waykichain.wallet.impl.LegacyWallet;
import waykichain.wallet.util.BIP44Util;
import waykichain.wallet.util.ContractUtil;
import waykichain.wallet.util.MnemonicUtil;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestTx {
    private Logger logger = LoggerFactory.getLogger(TestTx.class);

    /*
    * 生成助记词
    * generate Mnemonic
    * */
    @Test
    public void generateMnemonic(){
      List<String> words= new MnemonicUtil().randomMnemonicCodes();
      logger.info(words.toString());
    }

    /*
    * 生成钱包
    * generate wicc Wallet
    * */
    @Test
    public void generateWalletFromMnemonic(){
        String words = "vote despair mind rescue crumble choice garden elite venture cattle oxygen voyage";
        WaykiTestNetParams networkParameters = new WaykiTestNetParams(); //generate Testnet Address From Mnemonic
       // WaykiMainNetParams networkParameters = WaykiMainNetParams.Companion.getInstance(); //generate Mainnet Address From Mnemonic
        WaykiWallet wallet= new BIP44Util().generateWaykiWallet(words,networkParameters);
        logger.info("PrivateKey:"+wallet.getPrivateKey()+"\n"+"Address:"+wallet.getAddress()+"\n"+"PublicKeyKey:"+wallet.getPubKey()+"\n");
    }

    /*
    * WICC转账
    * 小费最少0.0001WICC   fee Minimum 0.0001 wicc
    * */
    @Test
    public void commonTransaction() throws IOException {
        LegacyWallet wallet =new LegacyWallet();
        WaykiTestNetParams netParams = new WaykiTestNetParams(); // Test net params
        String srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13";// private key
        ECKey srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).getKey();
        String pubKey = srcKey.getPublicKeyAsHex(); //wallet public key
        String destAddr = "wWTStcDL4gma6kPziyHhFGAP6xUzKpA5if";//dest address
        String memo="test transfer";//transfer memo
        WaykiCommonTxParams txParams =new WaykiCommonTxParams(WaykiNetworkType.TEST_NET, 34550, pubKey,10000,
                1100000000000L, "0-1", destAddr,memo);
        txParams.signTx(srcKey);
        String tx = wallet.createCommonTransactionRaw(txParams);
        logger.info("生成交易Hex:"+tx);
    }

    /*
     * 多币种转账交易 ,支持多种币种转账
     * Test nUniversal Coin Transfer Tx
     * fee Minimum 0.0001 wicc
     * */
    @Test
    public void  testGenerateUCoinTransferTx() throws IOException {
        LegacyWallet wallet = new LegacyWallet();
        WaykiTestNetParams netParams = new WaykiTestNetParams();

        String srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13";
         ECKey  srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).getKey();
        String pubKey = srcKey.getPublicKeyAsHex();  //user publickey hex string
        Long nValidHeight = 838L;
        String coinSymbol = CoinType.WICC.getType();  //coind symbol
        Long coinAmount = 1000000000L ;   //transfer amount
        String feeSymbol = CoinType.WICC.getType();
        Long fees = 100000L;
        String regid = "0-1";
        String destAddr = "wiEEFm8v9UqCucynQWw1sdk57iBf57jjYF";
        String memo = "";

        UCoinDest dest1=new UCoinDest(LegacyAddress.fromBase58(netParams,destAddr),coinSymbol,coinAmount);
        List<UCoinDest> dests= Arrays.asList(dest1);

        WaykiUCoinTxParams txParams = new WaykiUCoinTxParams(nValidHeight, regid, pubKey, dests, feeSymbol, fees, memo);
        txParams.signTx(srcKey);
        String tx = wallet.createUCoinTransactionRaw(txParams);
        logger.info("生成交易Hex:"+tx);
    }

    /*
     * 多币种合约调用交易
     * Contract transaction sample
     * fee Minimum 0.0001 wicc
     * */
    @Test
    public void testGenerateUCoinContractTx() throws IOException {
        LegacyWallet wallet = new LegacyWallet();
        WaykiTestNetParams netParams = new WaykiTestNetParams();

        String srcPrivKeyWiF = "Y6J4aK6Wcs4A3Ex4HXdfjJ6ZsHpNZfjaS4B9w7xqEnmFEYMqQd13";
        ECKey srcKey = DumpedPrivateKey.fromBase58(netParams, srcPrivKeyWiF).getKey();
        logger.info(LegacyAddress.fromPubKeyHash(netParams, srcKey.getPubKeyHash()).toString());

        Long value = 100000000L;
        String appid = "450687-1";
        byte[] contractByte = new ContractUtil().hexString2binaryString("f001");
        WaykiUCoinContractTxParams txParams = new WaykiUCoinContractTxParams(srcKey.getPrivateKeyAsHex(), 727702,
                100000, value, "0-1",
                appid, contractByte, CoinType.WICC.getType(),CoinType.WUSD.getType());
        txParams.signTx(srcKey);
        String tx = wallet.createUCoinContractInvokeRaw(txParams);
        logger.info("生成交易Hex:"+tx);
    }

}
