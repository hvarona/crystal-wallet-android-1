package cy.agorise.crystalwallet.apigenerator;

import java.util.HashMap;

import cy.agorise.crystalwallet.apigenerator.insightapi.AddressesActivityWatcher;
import cy.agorise.crystalwallet.apigenerator.insightapi.BroadcastTransaction;
import cy.agorise.crystalwallet.apigenerator.insightapi.GetEstimateFee;
import cy.agorise.crystalwallet.apigenerator.insightapi.GetTransactionByAddress;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.network.CryptoNetManager;

public class InsightApiGenerator {

    private static HashMap<CryptoCoin,GetTransactionByAddress> transactionGetters = new HashMap();
    private static HashMap<CryptoCoin,AddressesActivityWatcher> transactionFollowers = new HashMap();

    private static final String PATH = "api";

    /**
     * Fecth all the transaciton for a giving address
     * @param cryptoCoin the crypto net of the address
     * @param address The address String
     * @param subscribe If needs to follow the address (Real time)
     */
    public static void getTransactionFromAddress(CryptoCoin cryptoCoin, String address, boolean subscribe, HasTransactionListener listener){
        /*if(!transactionGetters.containsKey(cryptoCoin)){
            transactionGetters.put(cryptoCoin,new GetTransactionByAddress(cryptoCoin,CryptoNetManager.getURL(cryptoCoin.getCryptoNet()),PATH));
        }
        transactionGetters.get(cryptoCoin).addAddress(address);
        transactionGetters.get(cryptoCoin).start();*/

        GetTransactionByAddress transByAddr = new GetTransactionByAddress(cryptoCoin,CryptoNetManager.getURL(cryptoCoin.getCryptoNet()),PATH,listener);
        transByAddr.addAddress(address);
        transByAddr.start();

        if(subscribe){
            if(!transactionFollowers.containsKey(cryptoCoin)){
                transactionFollowers.put(cryptoCoin,new AddressesActivityWatcher(CryptoNetManager.getURL(cryptoCoin.getCryptoNet()),PATH,cryptoCoin));
            }
            transactionFollowers.get(cryptoCoin).addAddress(address);
            transactionFollowers.get(cryptoCoin).connect();
        }
    }

    /**
     * Broadcast an insight api transaction
     * @param cryptoCoin The cryptoNet of the transaction
     * @param rawtx the transaction to be broadcasted
     */
    public static void broadcastTransaction(CryptoCoin cryptoCoin, String rawtx, final ApiRequest request){
        BroadcastTransaction bTransaction = new BroadcastTransaction(rawtx,
                CryptoNetManager.getURL(cryptoCoin.getCryptoNet()), PATH, new BroadcastTransaction.BroadCastTransactionListener() {
            @Override
            public void onSuccess() {
                request.getListener().success(true,request.getId());
            }

            @Override
            public void onFailure(String msg) {
                request.getListener().fail(request.getId());
            }

            @Override
            public void onConnecitonFailure() {
                request.getListener().fail(request.getId());
            }
        });
        bTransaction.start();
    }

    /**
     * Fetch the estimated fee for a transaction
     */
    public static void getEstimateFee(CryptoCoin cryptoCoin, final ApiRequest request){
        GetEstimateFee.getEstimateFee(CryptoNetManager.getURL(cryptoCoin.getCryptoNet()),
                new GetEstimateFee.estimateFeeListener() {
            @Override
            public void estimateFee(long value) {
                request.listener.success(value,request.getId());
            }

            @Override
            public void fail() {
                request.listener.fail(request.getId());
            }
        });
    }

    public interface HasTransactionListener{
        public void hasTransaction(boolean value);
    }
}
