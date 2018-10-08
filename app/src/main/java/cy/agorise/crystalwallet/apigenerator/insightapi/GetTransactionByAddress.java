package cy.agorise.crystalwallet.apigenerator.insightapi;

import android.content.Context;
import android.util.Log;

import com.idescout.sql.SqlScoutServer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cy.agorise.crystalwallet.apigenerator.InsightApiGenerator;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.AddressTxi;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Txi;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vin;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vout;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.manager.GeneralAccountManager;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.GTxIO;
import cy.agorise.crystalwallet.models.GeneralCoinAccount;
import cy.agorise.crystalwallet.models.GeneralCoinAddress;
import cy.agorise.crystalwallet.models.GeneralTransaction;
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

    private String serverUrl;
    private CryptoCoin cryptoNet;

    private boolean inProcess = false;

    /**
     * Basic consturcotr
     */
    public GetTransactionByAddress(CryptoCoin cryptoNet, String serverUrl) {
        this.cryptoNet = cryptoNet;
        this.serverUrl = serverUrl;
        this.mServiceGenerator = new InsightApiServiceGenerator(serverUrl);
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
            boolean changed = false;
            AddressTxi addressTxi = response.body();

            for (Txi txi : addressTxi.items) {
                GeneralAccountManager.getAccountManager(this.cryptoNet).processTxi(txi);
            }

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
            Call<AddressTxi> addressTxiCall = service.getTransactionByAddress(this.serverUrl,addressToQuery.toString());
            addressTxiCall.enqueue(this);
        }
    }
}
