package waykichain.wallet.util;

import org.waykichainj.core.ECKey;
import org.waykichainj.core.LegacyAddress;
import org.waykichainj.core.NetworkParameters;
import org.waykichainj.crypto.ChildNumber;
import org.waykichainj.crypto.DeterministicKey;
import org.waykichainj.wallet.DeterministicKeyChain;
import org.waykichainj.wallet.DeterministicSeed;
import waykichain.wallet.base.WaykiWallet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BIP44Util {
    final static String WAYKICHAIN_WALLET_PATH= "m/44'/99999'/0'";

    public List<ChildNumber> generatePath(String path){
        List<ChildNumber> list = new ArrayList<>();
        for (String p : Arrays.stream(path.split("/")).collect(Collectors.toList())) {
            if ("m".equals(p) || "" == p.trim()) {
                continue;
            } else if (p.contains("\'")) {
                list.add(new ChildNumber(Integer.parseInt(p.substring(0, p.length() - 1)),true));
            } else {
                list.add(new ChildNumber(Integer.parseInt(p), false));
            }
        }
        final List<ChildNumber> builder = new ArrayList<ChildNumber>();
        builder.addAll(list);

        return builder;
    }

    public WaykiWallet generateWaykiWallet(String wordStr, NetworkParameters networkParameters){
        List<String> words = Arrays.asList(wordStr.split(" "));
        new MnemonicUtil().validateMnemonics(words);

        DeterministicSeed seed = new DeterministicSeed(words,(byte[])null,"",0L);
        DeterministicKeyChain.builder().seed(seed).build();
        DeterministicKeyChain keyChain = DeterministicKeyChain.builder().seed(seed).build();
        DeterministicKey mainKey = keyChain.getKeyByPath(this.generatePath(this.WAYKICHAIN_WALLET_PATH + "/0/0"), true);
        String address = LegacyAddress.fromPubKeyHash(networkParameters, mainKey.getPubKeyHash()).toString();
        ECKey ecKey = ECKey.fromPrivate(mainKey.getPrivKey());
        String privateKey = ecKey.getPrivateKeyAsWiF(networkParameters);
        String pubKey = ecKey.getPublicKeyAsHex();

        WaykiWallet waykiWallet = new WaykiWallet(privateKey, pubKey, address);
        return waykiWallet;
    }
}
