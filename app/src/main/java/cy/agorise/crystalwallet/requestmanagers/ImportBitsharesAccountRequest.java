package cy.agorise.crystalwallet.requestmanagers;

import android.content.Context;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.SeedType;

/**
 * Imports a bitshares accounts,
 *
 * return true if the account exist, and the mnemonic (brainkey provide is for that account
 * Created by Henry Varona on 10/24/2018.
 */

public class ImportBitsharesAccountRequest extends CryptoNetInfoRequest {

    /**
     * The status code of this request
     */
    public enum StatusCode{
        NOT_STARTED,
        SUCCEEDED,
        NO_INTERNET,
        NO_SERVER_CONNECTION,
        ACCOUNT_DOESNT_EXIST,
        BAD_SEED,
        NO_ACCOUNT_DATA,
        PETITION_FAILED
    }

    /**
     * The name of the account
     */
    private String accountName;

    /**
     * The mnemonic words
     */
    private final String mnemonic;

    /**
     * True - the account must be added if the accountName and mnemonic are correct
     */
    private boolean addAccountIfValid = false;

    /**
     * If this seed is BIP39 or Brainkey
     */
    private SeedType seedType;

    /**
     * The status of this request
     */
    private StatusCode status = StatusCode.NOT_STARTED;

    private Context context;

    public ImportBitsharesAccountRequest(String mnemonic, Context context){
        super(CryptoCoin.BITSHARES);
        this.accountName = "";
        this.mnemonic = mnemonic;
        this.context = context;
    }

    public ImportBitsharesAccountRequest(String mnemonic, Context context, boolean addAccountIfValid){
        super(CryptoCoin.BITSHARES);
        this.accountName = "";
        this.mnemonic = mnemonic;
        this.addAccountIfValid = addAccountIfValid;
        this.context = context;
    }

    public void validate(){
        if (!(this.status.equals(StatusCode.NOT_STARTED))){
            this._fireOnCarryOutEvent();
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public SeedType getSeedType() {
        return seedType;
    }

    public Context getContext() {
        return context;
    }

    public boolean addAccountIfValid(){
        return this.addAccountIfValid;
    }

    public void setAccountName(String accountName){
        this.accountName = accountName;
    }

    public void setSeedType(SeedType seedType) {
        this.seedType = seedType;
    }

    public void setStatus(StatusCode status) {
        this.status = status;
        this._fireOnCarryOutEvent();
    }

    public StatusCode getStatus() {
        return status;
    }
}
