package waykichain.wallet.base.params;

import org.waykichainj.params.TestNet3Params;

public class WaykiTestNetParams extends TestNet3Params {
    public WaykiTestNetParams(){
        this.packetMagic = 0xd75c7dfd;
        this.addressHeader = 135;
        this.dumpedPrivateKeyHeader = 210;
    }
}
