package waykichain.wallet.util;

public class TokenException extends RuntimeException {
    static final Long serialVersionUID = 4300404932829403534L;

    public TokenException(String msg){
        super(msg);
    }

    TokenException(String msg, Throwable cause){
        super(msg, cause);
    }

}
