package waykichain.wallet.base;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WaykiWallet {
    
    private final String privateKey;
    private final String pubKey;
    private final String address;

    
    public final WaykiWallet copy( String privateKey,  String pubKey,  String address) {
        return new WaykiWallet(privateKey, pubKey, address);
    }
    
    public String toString() {
        return "WaykiWallet(privateKey=" + this.privateKey + ", pubKey=" + this.pubKey + ", address=" + this.address + ")";
    }

    public int hashCode() {
        int keyHashCode = (this.privateKey != null ? this.privateKey.hashCode() : 0) * 31;
        keyHashCode = (keyHashCode + (this.pubKey != null ? this.pubKey.hashCode() : 0)) * 31;
        return (int)(keyHashCode + (this.address != null ? this.address.hashCode() : 0));
    }
}
