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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cy.agorise.crystalwallet.apigenerator.ApiRequest;
import cy.agorise.crystalwallet.apigenerator.ApiRequestListener;
import cy.agorise.crystalwallet.apigenerator.InsightApiGenerator;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Txi;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vin;
import cy.agorise.crystalwallet.apigenerator.insightapi.models.Vout;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.BitcoinTransaction;
import cy.agorise.crystalwallet.models.BitcoinTransactionGTxIO;
import cy.agorise.crystalwallet.models.CryptoCoinTransaction;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GTxIO;
import cy.agorise.crystalwallet.models.GeneralCoinAddress;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestsListener;
import cy.agorise.crystalwallet.requestmanagers.GeneralAccountSendRequest;
import cy.agorise.graphenej.Util;

public class GeneralAccountManager implements CryptoAccountManager, CryptoNetInfoRequestsListener {

    static HashMap<CryptoCoin, GeneralAccountManager> generalAccountManagers = new HashMap();

    private static CryptoCoin[] SUPPORTED_COINS = new CryptoCoin[]{
            CryptoCoin.BITCOIN,
            CryptoCoin.BITCOIN_TEST,
            CryptoCoin.DASH,
            CryptoCoin.LITECOIN
    } ;

    final CryptoCoin cryptoCoin;
    final Context context;

    public static GeneralAccountManager getAccountManager(CryptoCoin coin){
        return generalAccountManagers.get(coin);
    }

    public GeneralAccountManager(CryptoCoin cryptoCoin, Context context) {
        this.cryptoCoin = cryptoCoin;
        this.context = context;
        generalAccountManagers.put(cryptoCoin,this);
    }

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
        if(Arrays.asList(SUPPORTED_COINS).contains(request.getCoin())){
            if(request instanceof GeneralAccountSendRequest){
                this.send((GeneralAccountSendRequest)request);
            }else{
                System.out.println("Invalid " +this.cryptoCoin.getLabel() + " request ");
            }

        }

    }

    /**
     * Class that process each transaction fetched by the insight api
     * @param txi
     */
    public void processTxi(Txi txi){
        CrystalDatabase db = CrystalDatabase.getAppDatabase(this.context);
        List<BitcoinTransaction> btTransactions = db.bitcoinTransactionDao().getTransactionsByTxid(txi.txid);
        if(!btTransactions.isEmpty()){
            for(BitcoinTransaction btTransaction : btTransactions) {
                btTransaction.setConfirmations(txi.confirmations);
                CryptoCoinTransaction ccTransaction = db.transactionDao().getById(btTransaction.getCryptoCoinTransactionId());
                if (!ccTransaction.isConfirmed() && btTransaction.getConfirmations() >= cryptoCoin.getCryptoNet().getConfirmationsNeeded()) {
                    ccTransaction.setConfirmed(true);
                    db.transactionDao().insertTransaction(ccTransaction);
                }

                db.bitcoinTransactionDao().insertBitcoinTransaction(btTransaction);
            }
        }else {
            /*List<CryptoCoinTransaction> ccTransactions = new ArrayList();
            btTransactions = new ArrayList();*/ //TODO transactions involving multiples accounts
            CryptoCoinTransaction ccTransaction = new CryptoCoinTransaction();
            BitcoinTransaction btTransaction = new BitcoinTransaction();
            btTransaction.setTxId(txi.txid);
            btTransaction.setBlock(txi.blockheight);
            btTransaction.setFee((long) (txi.fee * Math.pow(10, cryptoCoin.getPrecision())));
            btTransaction.setConfirmations(txi.confirmations);
            ccTransaction.setDate(new Date(txi.time * 1000));
            if(txi.txlock || txi.confirmations >= cryptoCoin.getCryptoNet().getConfirmationsNeeded()) {
                ccTransaction.setConfirmed(true);
            }else{
                ccTransaction.setConfirmed(false);
            }

            ccTransaction.setInput(false);

            long amount = 0;


            //transaction.setAccount(this.mAccount);
            //transaction.setType(cryptoCoin);
            List<BitcoinTransactionGTxIO> gtxios = new ArrayList();
            for (Vin vin : txi.vin) {
                BitcoinTransactionGTxIO input = new BitcoinTransactionGTxIO();
                String addr = vin.addr;
                input.setAddress(addr);
                input.setIndex(vin.n);
                input.setOutput(true);
                input.setAmount((long) (vin.value * Math.pow(10, cryptoCoin.getPrecision())));
                input.setOriginalTxId(vin.txid);
                input.setScriptHex(vin.scriptSig.hex);

                gtxios.add(input);

            }

            for (Vout vout : txi.vout) {
                if (vout.scriptPubKey.addresses == null || vout.scriptPubKey.addresses.length <= 0) {

                } else {
                    BitcoinTransactionGTxIO output = new BitcoinTransactionGTxIO();
                    String addr = vout.scriptPubKey.addresses[0];
                    output.setAddress(addr);
                    output.setIndex(vout.n);
                    output.setOutput(false);
                    output.setAmount((long) (vout.value * Math.pow(10, cryptoCoin.getPrecision())));
                    output.setScriptHex(vout.scriptPubKey.hex);

                    gtxios.add(output);
                /*for (GeneralCoinAddress address : this.mAddresses) {
                    if (address.getAddressString(this.mAccount.getNetworkParam()).equals(addr)) {
                        output.setAddress(address);
                        tempAccount = address.getAccount();

                        if (!address.hasTransactionInput(output, this.mAccount.getNetworkParam())) {
                            address.getTransactionInput().add(output);
                        }
                        changed = true;
                    }
                }*/
                }
            }

            long ccId = db.transactionDao().insertTransaction(ccTransaction)[0];
            btTransaction.setCryptoCoinTransactionId(ccId);
            long btId = db.bitcoinTransactionDao().insertBitcoinTransaction(btTransaction)[0];
            for(BitcoinTransactionGTxIO gtxio : gtxios){
                gtxio.setBitcoinTransactionId(btId);
            }
            //TODO database
                /*SCWallDatabase db = new SCWallDatabase(this.mContext);
                long idTransaction = db.getGeneralTransactionId(transaction);
                if (idTransaction == -1) {
                    db.putGeneralTransaction(transaction);
                } else {
                    transaction.setId(idTransaction);
                    db.updateGeneralTransaction(transaction);
                }*/

        /*if (tempAccount != null && transaction.getConfirm() < this.mAccount.getCryptoNet().getConfirmationsNeeded()) {
            InsightApiGenerator.followTransaction();
            new GetTransactionData(transaction.getTxid(), tempAccount, this.serverUrl, this.mContext, true).start();
        }
        for (GeneralCoinAddress address : this.mAddresses) {
            if (address.updateTransaction(transaction)) {
                break;
            }
        }*/
        }
    }

    public void send(final GeneralAccountSendRequest request){
        //TODO check server connection
        //TODO validate to address

        InsightApiGenerator.getEstimateFee(request.getAccount().getCryptoCoin(),new ApiRequest(1, new ApiRequestListener() {
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

                InsightApiGenerator.broadcastTransaction(request.getAccount().getCryptoCoin(),Util.bytesToHex(tx.bitcoinSerialize()),new ApiRequest(1, new ApiRequestListener() {
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
