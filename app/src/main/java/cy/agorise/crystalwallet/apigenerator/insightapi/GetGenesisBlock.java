package cy.agorise.crystalwallet.apigenerator.insightapi;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetGenesisBlock {

    private static String PATH = "api";

    public GetGenesisBlock(String serverUrl, final genesisBlockListener listener) {
        try {
            InsightApiServiceGenerator serviceGenerator = new InsightApiServiceGenerator(serverUrl);
            InsightApiService service = serviceGenerator.getService(InsightApiService.class);
            Call<JsonObject> call = service.genesisBlock(PATH);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        listener.genesisBlock(response.body().get("blockHash").getAsString());
                    }catch(Exception e){
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
            listener.fail();
        }
    }

    public interface genesisBlockListener{
        void genesisBlock(String value);
        void fail();
    }
}
