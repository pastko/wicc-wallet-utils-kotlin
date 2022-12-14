package waykichain.wallet.base.params;

import lombok.Data;
import org.waykichainj.params.MainNetParams;

@Data
public class WaykiMainNetParams extends MainNetParams {
    public WaykiMainNetParams(){
        this.packetMagic = 0x1a1d42ff;
        this.addressHeader = 73;
        this.dumpedPrivateKeyHeader = 153;
    }
}
