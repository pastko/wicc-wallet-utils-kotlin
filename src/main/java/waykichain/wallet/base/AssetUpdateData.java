package waykichain.wallet.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetUpdateData {
    private AssetUpdateType enumAsset;
    private Object value;
}
