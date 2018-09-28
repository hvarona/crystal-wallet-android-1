package cy.agorise.crystalwallet.apigenerator;

import android.content.Context;

import java.util.HashMap;

import cy.agorise.crystalwallet.apigenerator.insightapi.BroadcastTransaction;
import cy.agorise.crystalwallet.apigenerator.insightapi.GetEstimateFee;
import cy.agorise.crystalwallet.apigenerator.insightapi.GetTransactionByAddress;
import cy.agorise.crystalwallet.apigenerator.insightapi.GetTransactionData;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.network.CryptoNetManager;

public class InsightApiGenerator {

    private static HashMap<CryptoNet,GetTransactionByAddress> transactionGetters = new HashMap();
    private static HashMap<CryptoNet,GetTransactionData> transacitonFollowers = new HashMap();

    /**
     * Fecth all the transaciton for a giving address
     * @param cryptoNet the crypto net of the address
     * @param address The address String
     * @param request the request api to response
     * @param subscribe If needs to follow the address (Real time)
     */
    public static void getTransactionFromAddress(CryptoNet cryptoNet, String address,
                                                 ApiRequest request, boolean subscribe){
        if(!transactionGetters.containsKey(cryptoNet)){
            transactionGetters.put(cryptoNet,new GetTransactionByAddress(cryptoNet,CryptoNetManager.getURL(cryptoNet)));
        }
        transactionGetters.get(cryptoNet).addAddress(address);
        //TODO process request
    }

    /**
     * Funciton used for unconfirmed transactions
     * @param cryptoNet
     * @param txid
     * @param context
     */
    public static void followTransaction(CryptoNet cryptoNet, String txid, Context context){

    }

    /**
     * Broadcast an insight api transaction
     * @param cryptoNet The cryptoNet of the transaction
     * @param rawtx the transaction to be broadcasted
     */
    public static void broadcastTransaction(CryptoNet cryptoNet, String rawtx, final ApiRequest request){
        BroadcastTransaction bTransaction = new BroadcastTransaction(rawtx, CryptoNetManager.getURL(cryptoNet), "api", new BroadcastTransaction.BroadCastTransactionListener() {
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
    }

    /**
     * Fetch the estimated fee for a transaction
     */
    public static void getEstimateFee(CryptoNet cryptoNet, final ApiRequest request){
        GetEstimateFee.getEstimateFee(CryptoNetManager.getURL(cryptoNet), new GetEstimateFee.estimateFeeListener() {
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
}
