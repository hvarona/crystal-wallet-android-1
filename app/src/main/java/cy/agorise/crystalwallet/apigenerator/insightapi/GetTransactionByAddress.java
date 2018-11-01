package cy.agorise.crystalwallet.apigenerator.insightapi;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cy.agorise.crystalwallet.apigenerator.InsightApiGenerator;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.AddressTxi;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Txi;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.manager.GeneralAccountManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Get all the transaction data of the addresses of an account
 *
 */

public class GetTransactionByAddress extends Thread implements Callback<AddressTxi> {
    /**
     * The list of address to query
     */
    private List<String> mAddresses = new ArrayList<>();
    /**
     * The serviceGenerator to call
     */
    private InsightApiServiceGenerator mServiceGenerator;

    private String mServerUrl;

    private String mPath;
    private CryptoCoin cryptoNet;

    private boolean inProcess = false;

    private InsightApiGenerator.HasTransactionListener listener;

    /**
     * Basic consturcotr
     */
    public GetTransactionByAddress(CryptoCoin cryptoNet, String serverUrl, String path, InsightApiGenerator.HasTransactionListener listener) {
        this.mPath = path;
        this.cryptoNet = cryptoNet;
        this.mServerUrl = serverUrl;
        this.mServiceGenerator = new InsightApiServiceGenerator(serverUrl);
        this.listener = listener;
    }

    /**
     * add an address to be query
     * @param address the address to be query
     */
    public void addAddress(String address) {
        this.mAddresses.add(address);
    }


    /**
     * Handle the response
     * @param call The call with the addresTxi object
     * @param response the response status object
     */
    @Override
    public void onResponse(Call<AddressTxi> call, Response<AddressTxi> response) {
        inProcess = false;
        if (response.isSuccessful()) {
            AddressTxi addressTxi = response.body();
            if(listener != null) {
                if (addressTxi.items.length > 0 ) {
                    listener.hasTransaction(true);
                }else{
                    listener.hasTransaction(false);
                }
            }

            for (Txi txi : addressTxi.items) {
                GeneralAccountManager.getAccountManager(this.cryptoNet).processTxi(txi);
            }

        }else{
            listener.hasTransaction(false);
        }
    }

    /**
     * Failure of the call
     * @param call The call object
     * @param t The reason for the failure
     */
    @Override
    public void onFailure(Call<AddressTxi> call, Throwable t) {
        inProcess = false;
        Log.e("GetTransactionByAddress", "Error in json format");
    }

    /**
     * Function to start the insight api call
     */
    @Override
    public void run() {
        if (this.mAddresses.size() > 0 && !inProcess) {
            inProcess = true;
            StringBuilder addressToQuery = new StringBuilder();
            for (String address : this.mAddresses) {
                addressToQuery.append(address).append(",");
            }
            addressToQuery.deleteCharAt(addressToQuery.length() - 1);
            InsightApiService service = this.mServiceGenerator.getService(InsightApiService.class);
            Call<AddressTxi> addressTxiCall = service.getTransactionByAddress(this.mPath,addressToQuery.toString());
            addressTxiCall.enqueue(this);
        }
    }
}
