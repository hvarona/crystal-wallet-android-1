package cy.agorise.crystalwallet.requestmanagers;

import android.content.Context;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GrapheneAccount;

/**
 * Class used to make a bitcoin uri parse request.
 *
 * Created by henry on 11/13/2018.
 */

public class BitcoinUriParseRequest extends CryptoNetInfoRequest {
    /**
     * The status code of this request
     */
    public enum StatusCode{
        NOT_STARTED,
        VALID,
        NOT_VALID
    }

    private String uri;

    private String address;
    private Double amount;
    private String memo;



    private StatusCode status = StatusCode.NOT_STARTED;

    public BitcoinUriParseRequest(String uri, CryptoCoin cryptoCoin) {
        super(cryptoCoin);
        this.address = "";
        this.amount = -1.0;
        this.memo = "";
        this.uri = uri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void validate(){
        if ((this.status != StatusCode.NOT_STARTED)){
            this._fireOnCarryOutEvent();
        }
    }

    public void setStatus(StatusCode code){
        this.status = code;
        this._fireOnCarryOutEvent();
    }

    public StatusCode getStatus() {
        return status;
    }

    public String getUri() {
        return uri;
    }
}
