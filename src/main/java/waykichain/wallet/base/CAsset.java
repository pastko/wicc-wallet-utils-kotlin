package waykichain.wallet.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CAsset {
    private String symbol;
    private String ownerRegid;
    private String name;
    private long totalSupply;
    private boolean minTable;
}
