package cy.agorise.crystalwallet.apigenerator.insightapi;

import com.google.gson.JsonObject;

import java.io.IOException;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Get the estimete fee amount from an insight api server.
 * This class gets the rate of the fee for a giving coin in about to block for a transaction to be
 * confirmated.
 *
 * This ammount is giving as amount of currency / kbytes,  as example btc / kbytes
 *
  */

public abstract class GetEstimateFee {

    /**
     * The funciton to get the rate for the transaction be included in the next 2 blocks
     * @param coin The coin to get the rate
     */
    public static void getEstimateFee(final CryptoCoin coin, String serverUrl, final estimateFeeListener listener) {
        InsightApiServiceGenerator serviceGenerator = new InsightApiServiceGenerator(serverUrl);
        InsightApiService service = serviceGenerator.getService(InsightApiService.class);
        Call<JsonObject> call = service.estimateFee(InsightApiConstants.getPath(coin));
        final JsonObject answer = new JsonObject();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.estimateFee((long) (answer.get("answer").getAsDouble()));

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.estimateFee(-1);
            }
        });
    }

    public static interface estimateFeeListener{
        public void estimateFee(long value);
    }

}