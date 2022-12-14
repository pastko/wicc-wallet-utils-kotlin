package waykichain.wallet.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.waykichainj.core.ECKey;

@Data
@AllArgsConstructor
public class WalletAddress {
    
    private final ECKey key;
    
    private final String privKey;
    
    private final String address;

}
