package cy.agorise.crystalwallet.requestmanagers;

import android.content.Context;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GrapheneAccount;

/**
 * Class used to make a send amount request.
 *
 * Created by henry on 8/10/2017.
 */

public class BitcoinSendRequest extends CryptoNetInfoRequest {
    /**
     * The status code of this request
     */
    public enum StatusCode{
        NOT_STARTED,
        SUCCEEDED,
        NO_INTERNET,
        NO_SERVER_CONNECTION,
        NO_BALANCE,
        NO_FEE,
        PETITION_FAILED
    }

    // The app context
    private Context mContext;
    // The source account used to transfer fund from
    private CryptoNetAccount mSourceAccount;
    // The destination account id
    private String mToAccount;
    // The amount of the transaction
    private long mAmount;
    // The asset id of the transaction
    private CryptoCoin mCryptoCoin;
    // The memo, can be null
    private String mMemo;
    // The state of this request
    private StatusCode status = StatusCode.NOT_STARTED;

    public BitcoinSendRequest(Context context, CryptoNetAccount sourceAccount,
                              String toAccount, long amount, CryptoCoin cryptoCoin, String memo) {
        super(cryptoCoin);
        this.mContext = context;
        this.mSourceAccount = sourceAccount;
        this.mToAccount = toAccount;
        this.mAmount = amount;
        this.mCryptoCoin = cryptoCoin;
        this.mMemo = memo;
    }

    public BitcoinSendRequest(Context context, GrapheneAccount sourceAccount,
                              String toAccount, long amount, CryptoCoin cryptoCoin) {
        this(context, sourceAccount,toAccount,amount,cryptoCoin,null);
    }

    public Context getContext() {
        return mContext;
    }

    public CryptoNetAccount getSourceAccount() {
        return mSourceAccount;
    }

    public String getToAccount() {
        return mToAccount;
    }

    public long getAmount() {
        return mAmount;
    }

    public CryptoCoin getCryptoCoin() {
        return mCryptoCoin;
    }

    public String getMemo() {
        return mMemo;
    }

    public void validate(){
        if ((this.status != StatusCode.NOT_STARTED)){
            this._fireOnCarryOutEvent();
        }
    }

    public void setStatus(StatusCode code){
        this.status = code;
        this.validate();
    }

    public StatusCode getStatus() {
        return status;
    }
}
