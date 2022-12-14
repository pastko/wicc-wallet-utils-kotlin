package waykichain.wallet.base;

public enum VoteOperType {
    NULL_OPER(0),            //
    ADD_FUND(1),        //投票
    MINUS_FUND(2);    //撤销投票

    private int value;

    VoteOperType(int value){
        this.value = value;
    }
}
