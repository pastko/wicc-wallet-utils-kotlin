package waykichain.wallet.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.waykichainj.core.LegacyAddress;

@Data
@AllArgsConstructor
public class UCoinDest {
    private LegacyAddress destAddress;
    private String coinSymbol;
    private long transferAmount;
}
