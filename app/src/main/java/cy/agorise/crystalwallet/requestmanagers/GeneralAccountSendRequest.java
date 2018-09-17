package cy.agorise.crystalwallet.requestmanagers;

import android.content.Context;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.GeneralCoinAccount;

public class GeneralAccountSendRequest extends CryptoNetInfoRequest {
    /**
     * The status code of this request
     */
    public enum StatusCode{
        NOT_STARTED,
        SUCCEEDED,
        NO_INTERNET,
        NO_SERVER_CONNECTION,
        BAD_TO_ADDRESS,
        NO_FEE,
        NO_BALANCE,
        PETITION_FAILED
    }

    // The app context
    private Context mContext;
    //The soruce Account
    private GeneralCoinAccount mAccount;
    // The destination account address
    private String mToAccount;
    // The amount of the transaction
    private long mAmount;
    // The memo, can be null
    private String mMemo;
    // The state of this request
    private StatusCode status = StatusCode.NOT_STARTED;

    public GeneralAccountSendRequest(CryptoCoin coin, Context context, GeneralCoinAccount account, String toAccount, long amount, String memo) {
        super(coin);
        this.mContext = context;
        this.mAccount = account;
        this.mToAccount = toAccount;
        this.mAmount = amount;
        this.mMemo = memo;
    }

    public GeneralAccountSendRequest(CryptoCoin coin, Context context, GeneralCoinAccount account, String toAccount, long amount) {
        this(coin,context,account,toAccount,amount,null);

    }

    public Context getContext() {
        return mContext;
    }

    public GeneralCoinAccount getAccount() {
        return mAccount;
    }

    public String getToAccount() {
        return mToAccount;
    }

    public long getAmount() {
        return mAmount;
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
        this._fireOnCarryOutEvent();
    }

    public StatusCode getStatus() {
        return status;
    }

}
