package cy.agorise.crystalwallet.network;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cy.agorise.graphenej.RPC;
import cy.agorise.graphenej.api.BaseGrapheneHandler;
import cy.agorise.graphenej.interfaces.WitnessResponseListener;
import cy.agorise.graphenej.models.ApiCall;
import cy.agorise.graphenej.models.WitnessResponse;

/**
 * Created by henry on 28/2/2018.
 */

public class GetDatabaseVersion extends BaseGrapheneHandler {

    private final WitnessResponseListener mListener;

    public GetDatabaseVersion(WitnessResponseListener listener) {
        super(listener);
        this.mListener = listener;
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        ApiCall getAccountByName = new ApiCall(0, "database_api.get_version", new ArrayList<Serializable>(), RPC.VERSION, 1);
        websocket.sendText(getAccountByName.toJsonString());
    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        String response = frame.getPayloadText();

        Type GetChainIdResponse = new TypeToken<WitnessResponse<String>>(){}.getType();
        GsonBuilder builder = new GsonBuilder();
        WitnessResponse<VersionResponse> witnessResponse = builder.create().fromJson(response, GetChainIdResponse);
        if(witnessResponse.error != null){
            this.mListener.onError(witnessResponse.error);
        }else{
            this.mListener.onSuccess(witnessResponse);
        }

            websocket.disconnect();
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    public class VersionResponse{
        public String blockchain_version;
        public String steem_revision;
        public String fc_revision;
        public String chain_id;
    }
}
