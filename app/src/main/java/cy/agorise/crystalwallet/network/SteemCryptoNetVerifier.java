package cy.agorise.crystalwallet.network;

import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.graphenej.interfaces.WitnessResponseListener;
import cy.agorise.graphenej.models.BaseResponse;
import cy.agorise.graphenej.models.WitnessResponse;

/**
 *
 * Created by henry on 28/2/2018.
 */

public class SteemCryptoNetVerifier extends CryptoNetVerifier {
    private final CryptoNet cryptoNet = CryptoNet.STEEM;
    private final String CHAIN_ID = "0000000000000000000000000000000000000000000000000000000000000000";//mainnet

    @Override
    public void checkURL(final String url) {
        final long startTime = System.currentTimeMillis();
        WebSocketThread thread = new WebSocketThread(new GetChainId(new WitnessResponseListener() {
            @Override
            public void onSuccess(WitnessResponse response) {
                if(response.result instanceof GetDatabaseVersion.VersionResponse) {
                    GetDatabaseVersion.VersionResponse result = (GetDatabaseVersion.VersionResponse) response.result;
                    if(result.chain_id.equals(CHAIN_ID)) {
                        CryptoNetManager.verifiedCryptoNetURL(cryptoNet, url, System.currentTimeMillis() - startTime);
                    }else{
                        System.out.println(" BitsharesCryptoNetVerifier Error we are not in the net current chain id " + result.chain_id + " excepted " + CHAIN_ID);
                        //TODO handle error bad chain
                    }
                }else{
                    //TODO handle error bad answer
                }
            }

            @Override
            public void onError(BaseResponse.Error error) {
                //TODO handle error
                System.out.println("Bad server response " + url);
            }
        }),url);
        thread.start();
    }

    @Override
    public String getChainId() {
        return CHAIN_ID;
    }
}