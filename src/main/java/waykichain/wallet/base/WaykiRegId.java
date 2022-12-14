package waykichain.wallet.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WaykiRegId {
    private long regHeight;
    private long regIndex;
}
