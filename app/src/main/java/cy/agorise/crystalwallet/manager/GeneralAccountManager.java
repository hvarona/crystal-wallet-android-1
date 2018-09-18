package cy.agorise.crystalwallet.manager;

import android.content.Context;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.script.Script;

import java.util.ArrayList;
import java.util.List;

import cy.agorise.crystalwallet.apigenerator.ApiRequest;
import cy.agorise.crystalwallet.apigenerator.ApiRequestListener;
import cy.agorise.crystalwallet.apigenerator.InsightApiGenerator;
import cy.agorise.crystalwallet.apigenerator.insightapi.BroadcastTransaction;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GTxIO;
import cy.agorise.crystalwallet.models.GeneralCoinAddress;
import cy.agorise.crystalwallet.models.GeneralTransaction;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestsListener;
import cy.agorise.crystalwallet.requestmanagers.GeneralAccountSendRequest;
import cy.agorise.graphenej.Util;

public class GeneralAccountManager implements CryptoAccountManager, CryptoNetInfoRequestsListener {

    @Override
    public void createAccountFromSeed(CryptoNetAccount account, ManagerRequest request, Context context) {

    }

    @Override
    public void importAccountFromSeed(CryptoNetAccount account, Context context) {

    }

    @Override
    public void loadAccountFromDB(CryptoNetAccount account, Context context) {

    }

    @Override
    public void onNewRequest(CryptoNetInfoRequest request) {

    }

    public void send(final GeneralAccountSendRequest request){
        //TODO check server connection
        //TODO validate to address

        InsightApiGenerator.getEstimateFee(request.getAccount().getCryptoNet(),new ApiRequest(1, new ApiRequestListener() {
            @Override
            public void success(Object answer, int idPetition) {
                Transaction tx = new Transaction(request.getAccount().getNetworkParam());
                long currentAmount = 0;
                long fee = -1;
                long feeRate = (Long) answer;
                fee = 226 * feeRate;

                List<GeneralCoinAddress> addresses = request.getAccount().getAddresses();
                List<GTxIO> utxos = new ArrayList();
                for(GeneralCoinAddress address : addresses){
                    List<GTxIO> addrUtxos = address.getUTXos();
                    for(GTxIO addrUtxo : addrUtxos){
                        utxos.add(addrUtxo);
                        currentAmount += addrUtxo.getAmount();
                        if(currentAmount >= request.getAmount()+ fee){
                            break;
                        }
                    }
                    if(currentAmount >= request.getAmount() + fee){
                        break;
                    }
                }


                if(currentAmount< request.getAmount() + fee){
                    request.setStatus(GeneralAccountSendRequest.StatusCode.NO_BALANCE);
                    return;
                }

                //String to an address
                Address toAddr = Address.fromBase58(request.getAccount().getNetworkParam(), request.getToAccount());
                tx.addOutput(Coin.valueOf(request.getAmount()), toAddr);

                if(request.getMemo()!= null && !request.getMemo().isEmpty()){
                    String memo = request.getMemo();
                    if(request.getMemo().length()>40){
                        memo = memo.substring(0,40);
                    }
                    byte[]scriptByte = new byte[memo.length()+2];
                    scriptByte[0] = 0x6a;
                    scriptByte[1] = (byte) memo.length();
                    System.arraycopy(memo.getBytes(),0,scriptByte,2,memo.length());
                    Script memoScript = new Script(scriptByte);
                    tx.addOutput(Coin.valueOf(0),memoScript);
                }

                //Change address
                long remain = currentAmount - request.getAmount() - fee;
                if( remain > 0 ) {
                    Address changeAddr = Address.fromBase58(request.getAccount().getNetworkParam(), request.getAccount().getNextChangeAddress());
                    tx.addOutput(Coin.valueOf(remain), changeAddr);
                }

                for(GTxIO utxo: utxos) {
                    Sha256Hash txHash = Sha256Hash.wrap(utxo.getTransaction().getTxid());
                    Script script = new Script(Util.hexToBytes(utxo.getScriptHex()));
                    TransactionOutPoint outPoint = new TransactionOutPoint(request.getAccount().getNetworkParam(), utxo.getIndex(), txHash);
                    if(utxo.getAddress().getKey().isPubKeyOnly()){
                        if(utxo.getAddress().isIsChange()){
                            utxo.getAddress().setKey(HDKeyDerivation.deriveChildKey(request.getAccount().getChangeKey(), new ChildNumber(utxo.getAddress().getIndex(), false)));
                        }else{
                            utxo.getAddress().setKey(HDKeyDerivation.deriveChildKey(request.getAccount().getExternalKey(), new ChildNumber(utxo.getAddress().getIndex(), false)));
                        }
                    }
                    tx.addSignedInput(outPoint, script, utxo.getAddress().getKey(), Transaction.SigHash.ALL, true);
                }

                InsightApiGenerator.broadcastTransaction(request.getAccount().getCryptoNet(),Util.bytesToHex(tx.bitcoinSerialize()),new ApiRequest(1, new ApiRequestListener() {
                    @Override
                    public void success(Object answer, int idPetition) {
                        request.setStatus(GeneralAccountSendRequest.StatusCode.SUCCEEDED);
                    }

                    @Override
                    public void fail(int idPetition) {
                        request.setStatus(GeneralAccountSendRequest.StatusCode.PETITION_FAILED);
                    }
                }));
            }

            @Override
            public void fail(int idPetition) {
                request.setStatus(GeneralAccountSendRequest.StatusCode.NO_FEE);

            }
        }));
    }
}
