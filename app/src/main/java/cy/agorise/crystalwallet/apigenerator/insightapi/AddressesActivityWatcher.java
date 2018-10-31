package cy.agorise.crystalwallet.apigenerator.insightapi;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.GeneralCoinAccount;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Handles all the calls for the Socket.IO of the insight api
 *
 * Only gets new transaction in real time for each address of an Account
 *
 */

public class AddressesActivityWatcher {

    private final CryptoCoin cryptoCoin;
    /**
     * The list of address to monitor
     */
    private List<String> mWatchAddress = new ArrayList<>();
    /**
     * the Socket.IO
     */
    private Socket mSocket;

    private final String mServerUrl;
    private final String mPath;

    /**
     * Handles the address/transaction notification.
     * Then calls the GetTransactionData to get the info of the new transaction
     */
    private final Emitter.Listener onAddressTransaction = new Emitter.Listener() {
        @Override
        public void call(Object... os) {
            try {
                System.out.println("Receive accountActivtyWatcher " + os[0].toString() );
                String txid = ((JSONObject) os[0]).getString(InsightApiConstants.sTxTag);
                new GetTransactionData(txid, mServerUrl, mPath, cryptoCoin).start();
            } catch (JSONException ex) {
                Logger.getLogger(AddressesActivityWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    /**
     * Handles the connect of the Socket.IO
     */
    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... os) {
            System.out.println("Connected to accountActivityWatcher");
            JSONArray array = new JSONArray();
            for(String addr : mWatchAddress) {
                array.put(addr);
            }
            mSocket.emit(InsightApiConstants.sSubscribeEmmit, InsightApiConstants.sChangeAddressRoom, array);
        }
    };

    /**
     * Handles the disconnect of the Socket.Io
     * Reconcects the mSocket
     */
    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... os) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ignore) {}
            mSocket.connect();
        }
    };

    /**
     * Error handler, doesn't need reconnect, the mSocket.io do that by default
     */
    private final Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... os) {
            System.out.println("Error to accountActivityWatcher ");
            for(Object ob : os) {
                System.out.println("accountActivityWatcher " + ob.toString());
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ignore) {}
            mSocket.connect();
        }
    };

    /**
     * Basic constructor
     *
     */
    public AddressesActivityWatcher(String serverUrl, String path, CryptoCoin cryptoCoin) {
        this.mPath = path;
        this.mServerUrl = serverUrl;
        this.cryptoCoin = cryptoCoin;
        try {
            this.mSocket = IO.socket(serverUrl);
            this.mSocket.on(Socket.EVENT_CONNECT, onConnect);
            this.mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            this.mSocket.on(Socket.EVENT_ERROR, onError);
            this.mSocket.on(Socket.EVENT_CONNECT_ERROR, onError);
            this.mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onError);
            this.mSocket.on(InsightApiConstants.sChangeAddressRoom, onAddressTransaction);
        } catch (URISyntaxException e) {
            //TODO change exception handler
            e.printStackTrace();
        }
    }

    /**
     * Add an address to be monitored, it can be used after the connect
     * @param address The String address to monitor
     */
    public void addAddress(String address) {
        mWatchAddress.add(address);
        if (this.mSocket.connected()) {
            mSocket.emit(InsightApiConstants.sSubscribeEmmit, InsightApiConstants.sChangeAddressRoom, new String[]{address});
        }
    }

    /**
     * Connects the Socket
     */
    public void connect() {
        try{
            if(this.mSocket == null || !this.mSocket.connected()) {
                this.mSocket.connect();
            }
        }catch(Exception ignore){
        }
    }

    /**
     * Disconnects the Socket
     */
    public void disconnect() {this.mSocket.disconnect();}
}
