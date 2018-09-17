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
     * @param serverUrl The url of the insight server
     * @param listener the listener to this answer
     */
    public static void getEstimateFee(String serverUrl, final estimateFeeListener listener) {
        try {
            InsightApiServiceGenerator serviceGenerator = new InsightApiServiceGenerator(serverUrl);
            InsightApiService service = serviceGenerator.getService(InsightApiService.class);
            Call<JsonObject> call = service.estimateFee(serverUrl);
            final JsonObject answer = new JsonObject();
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    listener.estimateFee((long) (answer.get("answer").getAsDouble()));

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    listener.fail();
                    listener.estimateFee(-1);
                }
            });
        }catch(Exception e){
            listener.fail();
        }
    }

    public static interface estimateFeeListener{
        public void estimateFee(long value);
        public void fail();
    }

}