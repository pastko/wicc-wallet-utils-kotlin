package waykichain.wallet.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperVoteFund {
    private int voteType;
    private byte[] pubKey;
    private long voteValue;
}
