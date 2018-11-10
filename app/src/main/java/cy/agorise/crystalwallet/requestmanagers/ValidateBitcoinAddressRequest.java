package cy.agorise.crystalwallet.requestmanagers;

import cy.agorise.crystalwallet.enums.CryptoCoin;

/**
 * This class validates that an account name exist, this can be used to verified the existing accounts
 * or to verified if the name is available to create an Account
 *
 * Created by henry on 8/10/2017.
 */

public class ValidateBitcoinAddressRequest extends CryptoNetInfoRequest {
    // The account name to validate
    private String address;
    // The result of the validation, or null if there isn't a response
    private Boolean addressValid;

    public ValidateBitcoinAddressRequest(CryptoCoin cryptoCoin, String address) {
        super(cryptoCoin);
        this.address = address;
    }

    public boolean getAddressValid(){
        return this.addressValid;
    }

    public void setAddressValid(boolean value){
        this.addressValid = value;
        this.validate();
    }

    public void validate(){
        if ((this.addressValid != null)){
            this._fireOnCarryOutEvent();
        }
    }

    public String getAddress() {
        return address;
    }

}
