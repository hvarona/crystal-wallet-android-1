package cy.agorise.crystalwallet.requestmanagers;

import android.content.Context;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * This class validates that an account name exist, this can be used to verified the existing accounts
 * or to verified if the name is available to create an Account
 *
 * Created by henry on 8/10/2017.
 */

public class CalculateBitcoinUriRequest extends CryptoNetInfoRequest {
    private CryptoNetAccount account;
    private CryptoCurrency currency;
    private double amount;
    private Context context;

    private String uri;

    public CalculateBitcoinUriRequest(CryptoCoin coin, CryptoNetAccount account, Context context) {
        super(coin);
        this.account = account;
        this.context = context;
    }

    public CalculateBitcoinUriRequest(CryptoCoin coin, CryptoNetAccount account, Context context, double amount) {
        super(coin);
        this.account = account;
        this.context = context;
        this.amount = amount;
    }

    public CalculateBitcoinUriRequest(CryptoCoin coin, CryptoNetAccount account, CryptoCurrency currency, double amount, Context context) {
        super(coin);
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.context = context;
    }

    public CryptoNetAccount getAccount() {
        return account;
    }

    public CryptoCurrency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public Context getContext() {
        return context;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
        this.validate();
    }

    public void validate(){
        if ((this.uri != null)){
            this._fireOnCarryOutEvent();
        }
    }

}
