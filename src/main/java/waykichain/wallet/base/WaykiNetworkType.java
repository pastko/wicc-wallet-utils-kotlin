package waykichain.wallet.base;

public enum WaykiNetworkType {
    MAIN_NET    (1, "mainnet"),
    TEST_NET    (2, "testnet"),
    REGTEST_NET (3, "regtest");

    private final int netNumber;
    private final String netName;

    WaykiNetworkType(int numb, String name ){
        this.netNumber = numb;
        this.netName = name;
    }

    public int getNetNumber(){
        return this.netNumber;
    }

    public String getNetName(){
        return this.netName;
    }
}
