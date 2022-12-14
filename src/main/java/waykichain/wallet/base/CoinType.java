package waykichain.wallet.base;

public enum CoinType {
    WICC("WICC"),
    WUSD("WUSD"),
    WGRT("WICC"),
    WCNY("WCNY"),
    WBTC("WBTC"),
    WETH("WETH"),
    WEOS("WEOS"),
    USD("USD"),
    CNY("CNY"),
    EUR("EUR"),
    BTC("BTC"),
    USDT("USDT"),
    GOLD("GOLD"),
    KWH("KWH");

    private String type;

    CoinType(String type){
        this.type=type;
    }

    public String getType(){
        return this.type;
    }
}
