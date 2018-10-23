package cy.agorise.crystalwallet.requestmanagers;

import android.content.Context;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Ask for the next address of a bitcoin alike account
 *
 * Created by Henry Varona on 10/22/2018.
 */

public class NextBitcoinAccountAddressRequest extends CryptoNetInfoRequest {

    private CryptoNetAccount account;
    private CryptoCoin cryptoCoin;
    private Context context;

    /**
     * The status code of this request
     */
    public enum StatusCode{
        NOT_STARTED,
        SUCCEEDED
    }

    // The state of this request
    private StatusCode status = StatusCode.NOT_STARTED;

    public NextBitcoinAccountAddressRequest(CryptoNetAccount account, CryptoCoin cryptoCoin, Context context){
        super(cryptoCoin);
        this.account = account;
        this.cryptoCoin = cryptoCoin;
        this.context = context;
    }

    public CryptoNetAccount getAccount() {
        return this.account;
    }

    public CryptoCoin getCryptoCoin() {
        return this.cryptoCoin;
    }

    public void validate(){
        if(!status.equals(StatusCode.NOT_STARTED))
            this._fireOnCarryOutEvent();
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
