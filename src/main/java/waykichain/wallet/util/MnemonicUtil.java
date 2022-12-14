package waykichain.wallet.util;

import org.waykichainj.crypto.MnemonicCode;
import org.waykichainj.crypto.MnemonicException;

import java.util.List;

public class MnemonicUtil {
    public void validateMnemonics(List<String> mnemonicCodes){
        try {
            MnemonicCode.INSTANCE.check(mnemonicCodes);
        } catch (MnemonicException.MnemonicLengthException e) {
            throw new TokenException(Messages.MNEMONIC_INVALID_LENGTH);
        } catch (MnemonicException.MnemonicWordException e) {
            throw new TokenException(Messages.MNEMONIC_BAD_WORD);
        } catch (Exception e) {
            throw new TokenException(Messages.MNEMONIC_INVALID_CHECKSUM);
        }
    }

    public List<String> randomMnemonicCodes(){
        return this.toMnemonicCodes(NumericUtil.generateRandomBytes(16));
    }

    private List<String> toMnemonicCodes(byte[] entropy){
        try{
            return MnemonicCode.INSTANCE.toMnemonic(entropy);
        }catch (MnemonicException.MnemonicLengthException e){
            throw new TokenException(Messages.MNEMONIC_INVALID_LENGTH);
        }catch (Exception e){
            throw new TokenException(Messages.MNEMONIC_INVALID_CHECKSUM);
        }
    }
}
