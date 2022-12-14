package waykichain.wallet.base;

public enum AssetUpdateType {
    UPDATE_NONE(0),
    OWNER_UID(1),
    NAME(2),
    MINT_AMOUNT(3);

    private int type;

    AssetUpdateType(int type) {
        this.type = type;
    }

    public int getType(){
        return this.type;
    }
}
