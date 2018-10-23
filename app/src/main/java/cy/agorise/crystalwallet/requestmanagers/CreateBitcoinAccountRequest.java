package cy.agorise.crystalwallet.requestmanagers;

import android.content.Context;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.GrapheneAccount;

/**
 * Creates bitcoin accounts using a seed,
 *
 * Created by Henry Varona on 10/22/2018.
 */

public class CreateBitcoinAccountRequest extends CryptoNetInfoRequest {

    private AccountSeed accountSeed;
    private CryptoNet accountCryptoNet;

    private Context context;

    /**
     * The status code of this request
     */
    public enum StatusCode{
        NOT_STARTED,
        SUCCEEDED,
        ACCOUNT_EXIST
    }

    // The state of this request
    private StatusCode status = StatusCode.NOT_STARTED;

    public CreateBitcoinAccountRequest(AccountSeed accountSeed, Context context,  CryptoNet cryptoNet){
        super(CryptoCoin.BITSHARES);
        this.accountSeed = accountSeed;
        this.accountCryptoNet = cryptoNet;
        this.context = context;
    }

    public AccountSeed getAccountSeed() {
        return this.accountSeed;
    }

    public void validate(){
        if(!status.equals(StatusCode.NOT_STARTED))
            this._fireOnCarryOutEvent();
    }

    public CryptoNet getAccountCryptoNet() {
        return this.accountCryptoNet;
    }

    public Context getContext() {
        return context;
    }

    public void setStatus(StatusCode code){
        this.status = code;
        this.validate();
    }

    public StatusCode getStatus() {
        return status;
    }
}
