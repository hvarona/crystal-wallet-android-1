package cy.agorise.crystalwallet.apigenerator.insightapi;

import android.content.Context;

import java.util.Date;

import cy.agorise.crystalwallet.apigenerator.insightapi.models.Txi;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vin;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vout;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.manager.GeneralAccountManager;
import cy.agorise.crystalwallet.models.GTxIO;
import cy.agorise.crystalwallet.models.GeneralCoinAccount;
import cy.agorise.crystalwallet.models.GeneralCoinAddress;
import cy.agorise.crystalwallet.models.GeneralTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CThis class retrieve the data of a single transaction
 */

public class GetTransactionData extends Thread implements Callback<Txi> {
    /**
     * The transaction txid to be query
     */
    private String mTxId;
    /**
     * The serviceGenerator to call
     */
    private InsightApiServiceGenerator mServiceGenerator;

    private String mServerUrl;

    private String mPath;
    /**
     * If has to wait for another confirmation
     */
    private boolean mMustWait = false;

    private CryptoCoin cryptoCoin;

    /**
     * Constructor used to query for a transaction with unknown confirmations
     * @param txid The txid of the transaciton to be query
     */
    public GetTransactionData(String txid, String serverUrl, String path, CryptoCoin cryptoCoin) {
        this(txid, serverUrl, path, cryptoCoin, false);

    }

    /**
     * Consturctor to be used qhen the confirmations of the transaction are known
     * @param txid The txid of the transaciton to be query
     * @param mustWait If there is less confirmation that needed
     */
    public GetTransactionData(String txid, String serverUrl, String path, CryptoCoin cryptoCoin, boolean mustWait) {
        this.mPath = path;
        this.mServerUrl = serverUrl;
        this.mTxId= txid;
        this.mServiceGenerator = new InsightApiServiceGenerator(serverUrl);
        this.mMustWait = mustWait;
        this.cryptoCoin = cryptoCoin;
    }

    /**
     * Function to start the insight api call
     */
    @Override
    public void run() {
        if (this.mMustWait) {
            //We are waiting for confirmation
            try {
                Thread.sleep(InsightApiConstants.sWaitTime);
            } catch (InterruptedException ignored) {
                //TODO this exception never rises
            }
        }

        InsightApiService service = this.mServiceGenerator.getService(InsightApiService.class);
        Call<Txi> txiCall = service.getTransaction(this.mPath,this.mTxId);
        txiCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<Txi> call, Response<Txi> response) {
        if (response.isSuccessful()) {

            Txi txi = response.body();
            GeneralAccountManager.getAccountManager(this.cryptoCoin).processTxi(txi);
            if (txi.confirmations < this.cryptoCoin.getCryptoNet().getConfirmationsNeeded()) {
                //If transaction weren't confirmed, add the transaction to watch for change on the confirmations
                new GetTransactionData(this.mTxId, this.mServerUrl, this.mPath, this.cryptoCoin, true).start();
            }
        }
    }

    /**
     * TODO handle the failure response
     * @param call the Call object
     * @param t the reason of the failure
     */
    @Override
    public void onFailure(Call<Txi> call, Throwable t) {

    }
}
