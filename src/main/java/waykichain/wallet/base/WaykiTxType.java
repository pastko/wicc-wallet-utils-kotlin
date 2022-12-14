package waykichain.wallet.base;



import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum WaykiTxType {
    TX_NONE(0),
    TX_REGISTERACCOUNT(2),
    TX_COMMON(3),
    TX_CONTRACT(4),
    LCONTRACT_DEPLOY_TX(5),
    TX_DELEGATE(6),

    ASSET_ISSUE_TX(9),    //!< a user issues onchain asset
    ASSET_UPDATE_TX(10),   //!< a user update onchain asset

    TX_UCOIN_TRANSFER(11),
    UCONTRACT_DEPLOY_TX(14),   //!< universal VM contract deployment
    UCONTRACT_INVOKE_TX(15),   //!< universal VM contract invocation

    TX_CDPSTAKE(21),
    TX_CDPREDEEM(22),
    TX_CDPLIQUIDATE(23),

    DEX_CANCEL_ORDER_TX(88),//!< dex cancel order Tx
    DEX_BUY_LIMIT_ORDER_TX(84), //!< dex buy limit price order Tx
    DEX_SELL_LIMIT_ORDER_TX(85),//!< dex sell limit price order Tx
    DEX_BUY_MARKET_ORDER_TX(86),//!< dex buy market price order Tx
    DEX_SELL_MARKET_ORDER_TX(87);//!< dex sell market price order Tx

    private int value;
    WaykiTxType(int value){
        this.value = value;
    }

    public int getType(){
        return this.value;
    }

    public static WaykiTxType of(final Integer code){
        return WaykiTxType.valueOf(CODE_MAP.get(code));
    }

    private static final Map<Integer, String> CODE_MAP= Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(WaykiTxType::getType, WaykiTxType::name))
    );
}


