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

    private static HashMap<CryptoNet,BroadcastTransaction> broadcaster = new HashMap();
    private static HashMap<CryptoNet,GetTransactionByAddress> transactionGetters = new HashMap();
    private static HashMap<CryptoNet,GetTransactionData> transacitonFollowers = new HashMap();

    public static void getTransactionFromAddress(CryptoNet cryptoNet, String address,
                                                 ApiRequest request, Context context,
                                                 boolean subscribe){
        if(!transactionGetters.containsKey(cryptoNet)){
            //TODO change this line
            transactionGetters.put(cryptoNet,new GetTransactionByAddress(null,CryptoNetManager.getURL(cryptoNet),context));
        }

    }

    public static void followTransaction(CryptoNet cryptoNet, String txid, Context context){

    }

    public static void broadcastTransaction(CryptoNet cryptoNet, String rawtx, ApiRequest request){
        if(!broadcaster.containsKey(cryptoNet)){
            //TODO change to multiple broadcast
            broadcaster.put(cryptoNet,new BroadcastTransaction(rawtx,null,
                    CryptoNetManager.getURL(cryptoNet),null));
            broadcaster.get(cryptoNet).start();
        }
    }

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
