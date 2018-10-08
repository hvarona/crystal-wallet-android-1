package cy.agorise.crystalwallet.apigenerator.insightapi;

import android.content.Context;

import cy.agorise.crystalwallet.apigenerator.insightapi.models.Txi;
import cy.agorise.crystalwallet.models.GeneralCoinAccount;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Broadcast a transaction, using the InsightApi
 *
 */

public class BroadcastTransaction extends Thread implements Callback<Txi> {
    /**
     * The rawTX as Hex String
     */
    private String mRawTx;
    /**
     * The serviceGenerator to call
     */
    private InsightApiServiceGenerator mServiceGenerator;

    private String mPath;

    private BroadCastTransactionListener listener;

    /**
     * Basic Consturctor
     * @param RawTx The RawTX in Hex String
     *
     */
    public BroadcastTransaction(String RawTx, String serverUrl, String path, BroadCastTransactionListener listener){
        this.mServiceGenerator = new InsightApiServiceGenerator(serverUrl);
        this.mRawTx = RawTx;
        this.listener = listener;
        this.mPath = path;
    }

    /**
     * Handles the response of the call
     *
     */
    @Override
    public void onResponse(Call<Txi> call, Response<Txi> response) {
        if (response.isSuccessful()) {
            listener.onSuccess();
        } else {
            listener.onFailure(response.message());
        }
    }

    /**
     * Handles the failures of the call
     */
    @Override
    public void onFailure(Call<Txi> call, Throwable t) {
        listener.onConnecitonFailure();
    }

    /**
     * Starts the call of the service
     */
    @Override
    public void run() {
        InsightApiService service = this.mServiceGenerator.getService(InsightApiService.class);
        Call<Txi> broadcastTransaction = service.broadcastTransaction(this.mPath,this.mRawTx);
        broadcastTransaction.enqueue(this);
    }

    public interface BroadCastTransactionListener{
        void onSuccess();
        void onFailure(String msg);
        void onConnecitonFailure();
    }
}
