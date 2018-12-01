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

    private static String PATH = "api";

    /**
     * The funciton to get the rate for the transaction be included in the next 2 blocks
     * @param serverUrl The url of the insight server
     * @param listener the listener to this answer
     */
    public static void getEstimateFee(String serverUrl, final estimateFeeListener listener) {
        try {
            InsightApiServiceGenerator serviceGenerator = new InsightApiServiceGenerator(serverUrl);
            InsightApiService service = serviceGenerator.getService(InsightApiService.class);
            Call<JsonObject> call = service.estimateFee(PATH);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        listener.estimateFee((double) (response.body().get("2").getAsDouble()));
                    }catch (Exception e){
                        e.printStackTrace();
                        listener.fail();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    listener.fail();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            listener.fail();
        }
    }

    public static interface estimateFeeListener{
        public void estimateFee(double value);
        public void fail();
    }

}