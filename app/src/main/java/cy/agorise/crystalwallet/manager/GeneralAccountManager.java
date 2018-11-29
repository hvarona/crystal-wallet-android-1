package cy.agorise.crystalwallet.manager;

import android.content.Context;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
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
import cy.agorise.crystalwallet.dao.BitcoinAddressDao;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.BitcoinAddress;
import cy.agorise.crystalwallet.models.BitcoinTransaction;
import cy.agorise.crystalwallet.models.BitcoinTransactionGTxIO;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoCoinTransaction;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.requestmanagers.BitcoinSendRequest;
import cy.agorise.crystalwallet.requestmanagers.BitcoinUriParseRequest;
import cy.agorise.crystalwallet.requestmanagers.CalculateBitcoinUriRequest;
import cy.agorise.crystalwallet.requestmanagers.CreateBitcoinAccountRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestsListener;
import cy.agorise.crystalwallet.requestmanagers.NextBitcoinAccountAddressRequest;
import cy.agorise.crystalwallet.requestmanagers.ValidateBitcoinAddressRequest;
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
    public void loadAccountFromDB(final CryptoNetAccount account, Context context) {
        final CrystalDatabase db = CrystalDatabase.getAppDatabase(context);

        AccountSeed seed = db.accountSeedDao().findById(account.getSeedId());
        DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey((DeterministicKey) seed.getPrivateKey(),
                new ChildNumber(44, true));
        DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey,
                new ChildNumber(cryptoCoin.getCoinNumber(), true));
        DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey,
                new ChildNumber(account.getAccountIndex(), true));
        final DeterministicKey externalKey = HDKeyDerivation.deriveChildKey(accountKey,
                new ChildNumber(0, false));
        final DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey,
                new ChildNumber(1, false));

        CryptoCoinBalance balance = new CryptoCoinBalance();
        long amount = 0;
        List<CryptoCoinTransaction> trransactions = db.transactionDao().getByIdAccount(account.getId());
        for(CryptoCoinTransaction transaction : trransactions){
            if(transaction.isConfirmed()){
                if(transaction.getInput()){
                    amount += transaction.getAmount();
                }else{
                    amount -= transaction.getAmount();
                }

            }
        }
        balance.setBalance(amount);
        balance.setCryptoCurrencyId(db.cryptoCurrencyDao().getByName(cryptoCoin.getLabel(),cryptoCoin.name()).getId());
        balance.setAccountId(account.getId());
        db.cryptoCoinBalanceDao().insertCryptoCoinBalance(balance);

        long indexExternal = db.bitcoinAddressDao().getLastExternalAddress(account.getId());
        if(indexExternal > 0){
            for(int i = 0; i < indexExternal;i++){
                BitcoinAddress address = db.bitcoinAddressDao().getExternalByIndex(i);
                InsightApiGenerator.getTransactionFromAddress(cryptoCoin,address.getAddress(),true,null);
            }
        }else {
            ECKey externalAddrKey = HDKeyDerivation.deriveChildKey(externalKey, new ChildNumber((int) 0, true));
            BitcoinAddress address = new BitcoinAddress();
            address.setChange(false);
            address.setAccountId(account.getId());
            address.setIndex(0);
            String addressString = externalAddrKey.toAddress(this.cryptoCoin.getParameters()).toString();
            address.setAddress(addressString);
            db.bitcoinAddressDao().insertBitcoinAddresses(address);
            InsightApiGenerator.getTransactionFromAddress(cryptoCoin,addressString,true,
                    new CheckAddressForTransaction(db.bitcoinAddressDao(),account.getId(),externalKey,false,0));
        }

        long indexChange = db.bitcoinAddressDao().getLastChangeAddress(account.getId());
        if(indexChange > 0){
            for(int i = 0; i < indexChange;i++){
                BitcoinAddress address = db.bitcoinAddressDao().getChangeByIndex(i);
                InsightApiGenerator.getTransactionFromAddress(cryptoCoin,address.getAddress(),true,null);
            }
        }else {
            ECKey changeAddrKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) 0, true));
            BitcoinAddress address = new BitcoinAddress();
            address.setChange(true);
            address.setAccountId(account.getId());
            address.setIndex(0);
            String addressString =changeAddrKey.toAddress(this.cryptoCoin.getParameters()).toString();
            address.setAddress(addressString);
            db.bitcoinAddressDao().insertBitcoinAddresses(address);
            InsightApiGenerator.getTransactionFromAddress(cryptoCoin,addressString,true,
                    new CheckAddressForTransaction(db.bitcoinAddressDao(),account.getId(),externalKey,true,0));
        }
    }



    @Override
    public void onNewRequest(CryptoNetInfoRequest request) {
        //if(Arrays.asList(SUPPORTED_COINS).contains(request.getCoin())){
        if(request.getCoin().equals(this.cryptoCoin)){
            if(request instanceof BitcoinSendRequest) {
                this.send((BitcoinSendRequest) request);
            }else if(request instanceof CreateBitcoinAccountRequest){
                this.createGeneralAccount((CreateBitcoinAccountRequest) request);
            }else if(request instanceof NextBitcoinAccountAddressRequest){
                this.getNextAddress((NextBitcoinAccountAddressRequest) request);
            }else if(request instanceof ValidateBitcoinAddressRequest){
                this.validateAddress((ValidateBitcoinAddressRequest) request);
            }else if(request instanceof CalculateBitcoinUriRequest){
                this.calculateUri((CalculateBitcoinUriRequest) request);
            }else if(request instanceof BitcoinUriParseRequest){
                this.parseUri((BitcoinUriParseRequest) request);
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
        try {
            System.out.println("GeneralAccountManager processingTxi " + txi.txid);
            CrystalDatabase db = CrystalDatabase.getAppDatabase(this.context);
            List<BitcoinTransaction> btTransactions = db.bitcoinTransactionDao().getTransactionsByTxid(txi.txid);
            if (!btTransactions.isEmpty()) {
                System.out.println("GeneralAccountManager Transaction not null " + txi.txid);
                for (BitcoinTransaction btTransaction : btTransactions) {
                    btTransaction.setConfirmations(txi.confirmations);
                    CryptoCoinTransaction ccTransaction = db.transactionDao().getById(btTransaction.getCryptoCoinTransactionId());
                    if (!ccTransaction.isConfirmed() && btTransaction.getConfirmations() >= cryptoCoin.getCryptoNet().getConfirmationsNeeded()) {
                        ccTransaction.setConfirmed(true);
                        db.transactionDao().insertTransaction(ccTransaction);
                        updateBalance(ccTransaction, (ccTransaction.getInput() ? 1 : -1) * ccTransaction.getAmount(), db);
                    }

                    db.bitcoinTransactionDao().insertBitcoinTransaction(btTransaction);
                }
            } else {
            /*List<CryptoCoinTransaction> ccTransactions = new ArrayList();
            btTransactions = new ArrayList();*/ //TODO transactions involving multiples accounts
                CryptoCoinTransaction ccTransaction = new CryptoCoinTransaction();
                BitcoinTransaction btTransaction = new BitcoinTransaction();
                btTransaction.setTxId(txi.txid);
                btTransaction.setBlock(txi.blockheight);
                btTransaction.setFee((long) (txi.fee * Math.pow(10, cryptoCoin.getPrecision())));
                btTransaction.setConfirmations(txi.confirmations);
                ccTransaction.setDate(new Date(txi.time * 1000));
                if (txi.txlock || txi.confirmations >= cryptoCoin.getCryptoNet().getConfirmationsNeeded()) {
                    ccTransaction.setConfirmed(true);
                } else {
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

                    BitcoinAddress address = db.bitcoinAddressDao().getdadress(addr);
                    if (address != null) {
                        if (ccTransaction.getAccountId() < 0) {
                            ccTransaction.setAccountId(address.getAccountId());
                            ccTransaction.setFrom(addr);
                            ccTransaction.setInput(false);
                        }

                        if (ccTransaction.getAccountId() == address.getAccountId()) {
                            amount -= (long) (vin.value * Math.pow(10, cryptoCoin.getPrecision()));
                        }
                    }

                    if (ccTransaction.getFrom() == null || ccTransaction.getFrom().isEmpty()) {
                        ccTransaction.setFrom(addr);
                    }

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
                        output.setOriginalTxId(txi.txid);

                        gtxios.add(output);

                        BitcoinAddress address = db.bitcoinAddressDao().getdadress(addr);
                        if (address != null) {
                            if (ccTransaction.getAccountId() < 0) {
                                ccTransaction.setAccountId(address.getAccountId());
                                ccTransaction.setInput(true);
                                ccTransaction.setTo(addr);
                            }

                            if (ccTransaction.getAccountId() == address.getAccountId()) {
                                amount += (long) (vout.value * Math.pow(10, cryptoCoin.getPrecision()));
                            }
                        } else {
                            //TOOD multiple send address
                            if (ccTransaction.getTo() == null || ccTransaction.getTo().isEmpty()) {
                                ccTransaction.setTo(addr);
                            }
                        }
                    }
                }

                ccTransaction.setAmount(amount);
                CryptoCurrency currency = db.cryptoCurrencyDao().getByNameAndCryptoNet(this.cryptoCoin.getLabel(), this.cryptoCoin.getCryptoNet().name());
                if (currency == null) {
                    currency = new CryptoCurrency();
                    currency.setCryptoNet(this.cryptoCoin.getCryptoNet());
                    currency.setName(this.cryptoCoin.getLabel());
                    currency.setPrecision(this.cryptoCoin.getPrecision());
                    long idCurrency = db.cryptoCurrencyDao().insertCryptoCurrency(currency)[0];
                    currency.setId(idCurrency);
                }

                ccTransaction.setIdCurrency((int) currency.getId());

                long ccId = db.transactionDao().insertTransaction(ccTransaction)[0];
                btTransaction.setCryptoCoinTransactionId(ccId);
                long btId = db.bitcoinTransactionDao().insertBitcoinTransaction(btTransaction)[0];
                for (BitcoinTransactionGTxIO gtxio : gtxios) {
                    gtxio.setBitcoinTransactionId(ccId);
                    db.bitcoinTransactionDao().insertBitcoinTransactionGTxIO(gtxio);
                }

                if (ccTransaction.isConfirmed()) {
                    updateBalance(ccTransaction, amount, db);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createGeneralAccount(CreateBitcoinAccountRequest request){
        CrystalDatabase db = CrystalDatabase.getAppDatabase(this.context);
        CryptoNetAccount account = new CryptoNetAccount();
        account.setAccountIndex(0);
        account.setCryptoNet(this.cryptoCoin.getCryptoNet());
        account.setName(request.getAccountSeed().getName());
        account.setSeedId(request.getAccountSeed().getId());
        long idAccount = db.cryptoNetAccountDao().insertCryptoNetAccount(account)[0];
        account.setId(idAccount);

        loadAccountFromDB(account,request.getContext());
        request.setStatus(CreateBitcoinAccountRequest.StatusCode.SUCCEEDED);

    }

    private void updateBalance(CryptoCoinTransaction ccTransaction, long amount, CrystalDatabase db){
        CryptoCurrency currency = db.cryptoCurrencyDao().getByNameAndCryptoNet(this.cryptoCoin.getLabel(), this.cryptoCoin.getCryptoNet().name());
        if (currency == null) {
            currency = new CryptoCurrency();
            currency.setCryptoNet(this.cryptoCoin.getCryptoNet());
            currency.setName(this.cryptoCoin.getLabel());
            currency.setPrecision(this.cryptoCoin.getPrecision());
            long idCurrency = db.cryptoCurrencyDao().insertCryptoCurrency(currency)[0];
            currency.setId(idCurrency);
        }

        CryptoCoinBalance balance = db.cryptoCoinBalanceDao().getBalanceFromAccount(ccTransaction.getAccountId(), currency.getId());
        if (balance == null) {
            balance = new CryptoCoinBalance();
            balance.setAccountId(ccTransaction.getAccountId());
            balance.setCryptoCurrencyId(currency.getId());
            long idBalance = db.cryptoCoinBalanceDao().insertCryptoCoinBalance(balance)[0];
            balance.setId(idBalance);
        }
        balance.setBalance(balance.getBalance()+amount);
        db.cryptoCoinBalanceDao().insertCryptoCoinBalance(balance);
    }

    private void validateAddress(ValidateBitcoinAddressRequest request){
        try{
            Address address = Address.fromBase58(this.cryptoCoin.getParameters(), request.getAddress());
            request.setAddressValid(true);
        }catch(AddressFormatException ex){
            request.setAddressValid(false);
        }
        request.validate();
    }

    public void send(final BitcoinSendRequest request){
        //TODO check server connection
        //TODO validate to address

        InsightApiGenerator.getEstimateFee(this.cryptoCoin,new ApiRequest(1, new ApiRequestListener() {
            @Override
            public void success(Object answer, int idPetition) {
                Transaction tx = new Transaction(cryptoCoin.getParameters());
                long currentAmount = 0;
                long fee = -1;
                long feeRate =  (long)(((double)answer) * Math.pow(10,cryptoCoin.getPrecision()));
                fee = 226 * feeRate;

                CrystalDatabase db = CrystalDatabase.getAppDatabase(request.getContext());
                db.bitcoinTransactionDao();

                List<BitcoinTransactionGTxIO> utxos = getUtxos(request.getSourceAccount().getId(),db);

                if(currentAmount< request.getAmount() + fee){
                    request.setStatus(BitcoinSendRequest.StatusCode.NO_BALANCE);
                    return;
                }
                AccountSeed seed = db.accountSeedDao().findById(request.getSourceAccount().getSeedId());
                DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey((DeterministicKey) seed.getPrivateKey(),
                        new ChildNumber(44, true));
                DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey,
                        new ChildNumber(cryptoCoin.getCoinNumber(), true));
                DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey,
                        new ChildNumber(request.getSourceAccount().getAccountIndex(), true));
                DeterministicKey externalKey = HDKeyDerivation.deriveChildKey(accountKey,
                        new ChildNumber(0, false));
                DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey,
                        new ChildNumber(1, false));

                //String to an address
                Address toAddr = Address.fromBase58(cryptoCoin.getParameters(), request.getToAccount());
                tx.addOutput(Coin.valueOf(request.getAmount()), toAddr);

                /*if(request.getMemo()!= null && !request.getMemo().isEmpty()){
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
                }*/

                //Change address
                long remain = currentAmount - request.getAmount() - fee;
                if( remain > 0 ) {
                    long index = db.bitcoinAddressDao().getLastChangeAddress(request.getSourceAccount().getId());
                    BitcoinAddress btAddress = db.bitcoinAddressDao().getChangeByIndex(index);
                    Address changeAddr;
                    if(btAddress != null && db.bitcoinTransactionDao().getGtxIOByAddress(btAddress.getAddress()).size()<=0){
                            changeAddr = Address.fromBase58(cryptoCoin.getParameters(), btAddress.getAddress());

                    }else{
                        if(btAddress == null){
                            index = 0;
                        }else{
                            index++;
                        }
                        btAddress = new BitcoinAddress();
                        btAddress.setIndex(index);
                        btAddress.setAccountId(request.getSourceAccount().getId());
                        btAddress.setChange(true);
                        btAddress.setAddress(HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) btAddress.getIndex(), false)).toAddress(cryptoCoin.getParameters()).toString());
                        db.bitcoinAddressDao().insertBitcoinAddresses(btAddress);
                        changeAddr = Address.fromBase58(cryptoCoin.getParameters(), btAddress.getAddress());
                    }
                    tx.addOutput(Coin.valueOf(remain), changeAddr);
                }

                for(BitcoinTransactionGTxIO utxo: utxos) {
                    Sha256Hash txHash = Sha256Hash.wrap(utxo.getOriginalTxId());
                    Script script = new Script(Util.hexToBytes(utxo.getScriptHex()));
                    TransactionOutPoint outPoint = new TransactionOutPoint(cryptoCoin.getParameters(), utxo.getIndex(), txHash);
                    BitcoinAddress btAddress = db.bitcoinAddressDao().getdadress(utxo.getAddress());
                    ECKey addrKey;

                    if(btAddress.isChange()){
                        addrKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber((int) btAddress.getIndex(), false));
                    }else{
                        addrKey = HDKeyDerivation.deriveChildKey(externalKey, new ChildNumber((int) btAddress.getIndex(), true));
                    }
                    tx.addSignedInput(outPoint, script, addrKey, Transaction.SigHash.ALL, true);
                }

                InsightApiGenerator.broadcastTransaction(cryptoCoin,Util.bytesToHex(tx.bitcoinSerialize()),new ApiRequest(1, new ApiRequestListener() {
                    @Override
                    public void success(Object answer, int idPetition) {
                        request.setStatus(BitcoinSendRequest.StatusCode.SUCCEEDED);
                    }

                    @Override
                    public void fail(int idPetition) {
                        request.setStatus(BitcoinSendRequest.StatusCode.PETITION_FAILED);
                    }
                }));
            }

            @Override
            public void fail(int idPetition) {
                request.setStatus(BitcoinSendRequest.StatusCode.NO_FEE);

            }
        }));
    }

    private void getNextAddress(NextBitcoinAccountAddressRequest request){
        CrystalDatabase db = CrystalDatabase.getAppDatabase(request.getContext());
        long index = db.bitcoinAddressDao().getLastExternalAddress(request.getAccount().getId());
        BitcoinAddress address = db.bitcoinAddressDao().getExternalByIndex(index);
        if(address != null && db.bitcoinTransactionDao().getGtxIOByAddress(address.getAddress()).size()<=0){
            request.setAddress(address.getAddress());
            request.setStatus(NextBitcoinAccountAddressRequest.StatusCode.SUCCEEDED);
        }else {
            index++;
            AccountSeed seed = db.accountSeedDao().findById(request.getAccount().getSeedId());
            DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey((DeterministicKey) seed.getPrivateKey(),
                    new ChildNumber(44, true));
            DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey,
                    new ChildNumber(cryptoCoin.getCoinNumber(), true));
            DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey,
                    new ChildNumber(request.getAccount().getAccountIndex(), true));
            DeterministicKey externalKey = HDKeyDerivation.deriveChildKey(accountKey,
                    new ChildNumber(0, false));
            ECKey addrKey = HDKeyDerivation.deriveChildKey(externalKey, new ChildNumber((int) index, true));
            address = new BitcoinAddress();
            address.setChange(false);
            address.setAccountId(request.getAccount().getId());
            address.setIndex(index);
            String addressString = addrKey.toAddress(this.cryptoCoin.getParameters()).toString();
            address.setAddress(addressString);
            db.bitcoinAddressDao().insertBitcoinAddresses(address);
            InsightApiGenerator.getTransactionFromAddress(this.cryptoCoin, addressString, true, null);

            request.setAddress(addressString);
            request.setStatus(NextBitcoinAccountAddressRequest.StatusCode.SUCCEEDED);
        }
    }

    private void calculateUri(CalculateBitcoinUriRequest request) {
        StringBuilder uri = new StringBuilder(this.cryptoCoin.name().toLowerCase()+":");

        CrystalDatabase db = CrystalDatabase.getAppDatabase(request.getContext());
        long index = db.bitcoinAddressDao().getLastExternalAddress(request.getAccount().getId());
        BitcoinAddress address = db.bitcoinAddressDao().getExternalByIndex(index);
        if(address != null && db.bitcoinTransactionDao().getGtxIOByAddress(address.getAddress()).size()<=0){
            uri.append(address.getAddress());
        }else {
            index++;
            AccountSeed seed = db.accountSeedDao().findById(request.getAccount().getSeedId());
            DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey((DeterministicKey) seed.getPrivateKey(),
                    new ChildNumber(44, true));
            DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey,
                    new ChildNumber(cryptoCoin.getCoinNumber(), true));
            DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey,
                    new ChildNumber(request.getAccount().getAccountIndex(), true));
            DeterministicKey externalKey = HDKeyDerivation.deriveChildKey(accountKey,
                    new ChildNumber(0, false));
            ECKey addrKey = HDKeyDerivation.deriveChildKey(externalKey, new ChildNumber((int) index, true));
            address = new BitcoinAddress();
            address.setChange(false);
            address.setAccountId(request.getAccount().getId());
            address.setIndex(index);
            String addressString = addrKey.toAddress(this.cryptoCoin.getParameters()).toString();
            address.setAddress(addressString);
            db.bitcoinAddressDao().insertBitcoinAddresses(address);
            InsightApiGenerator.getTransactionFromAddress(this.cryptoCoin, addressString, true, null);

            uri.append(address.getAddress());
        }
        if(request.getAmount() > 0 ){
            uri.append("?amount=");
            uri.append(Double.toString(request.getAmount()));
        }

        System.out.println("GeneralAccountMAnager uri calculated : " + uri.toString());

        request.setUri(uri.toString());
        //request.validate();
    }

    private void parseUri(BitcoinUriParseRequest request){
        String uri = request.getUri();
        if(uri.indexOf(":")>0){
            String cryptoNet = uri.substring(0,uri.indexOf(":"));
            if(cryptoNet.equalsIgnoreCase(this.cryptoCoin.name().toLowerCase())){
                try{
                    int parameterIndex =uri.indexOf("?");
                    Address address = Address.fromBase58(this.cryptoCoin.getParameters(), uri.substring(uri.indexOf(":")+1,parameterIndex>0?parameterIndex:uri.length()));
                    request.setAddress(address.toString());
                    request.setStatus(BitcoinUriParseRequest.StatusCode.VALID);
                    if(parameterIndex>0){
                        try {
                            String[] parameters = uri.substring(parameterIndex + 1).split("&");
                            for (String parameter : parameters) {
                                int idx = parameter.indexOf("=");
                                if (idx > 0 && parameter.substring(0, idx).equalsIgnoreCase("amount")) {
                                    request.setAmount(Double.parseDouble(parameter.substring(idx + 1)));
                                }
                            }
                        }catch(Exception ignored){}
                    }
                }catch(AddressFormatException ex){
                    request.setStatus(BitcoinUriParseRequest.StatusCode.NOT_VALID);
                }

            }else{
                request.setStatus(BitcoinUriParseRequest.StatusCode.NOT_VALID);
            }
        }else{
            int parameterIndex =uri.indexOf("?");
            if(parameterIndex>0){
                try{
                    Address address = Address.fromBase58(this.cryptoCoin.getParameters(), uri.substring(uri.indexOf(":")+1,parameterIndex>0?parameterIndex:uri.length()));
                    request.setAddress(address.toString());
                    request.setStatus(BitcoinUriParseRequest.StatusCode.VALID);
                    try{
                    String[] parameters = uri.substring(parameterIndex+1).split("&");
                    for(String parameter : parameters){
                        int idx = parameter.indexOf("=");
                        if(idx > 0 && parameter.substring(0,idx).equalsIgnoreCase("amount")){
                            request.setAmount(Double.parseDouble(parameter.substring(idx+1)));
                        }
                    }
                    }catch(Exception ignored){}

                }catch(AddressFormatException ex){
                    request.setStatus(BitcoinUriParseRequest.StatusCode.NOT_VALID);
                }
            }else{
                try{
                    Address address = Address.fromBase58(this.cryptoCoin.getParameters(), uri);
                    request.setAddress(address.toString());
                    request.setStatus(BitcoinUriParseRequest.StatusCode.VALID);

                }catch(AddressFormatException ex){
                    request.setStatus(BitcoinUriParseRequest.StatusCode.NOT_VALID);
                }
            }
        }
        request.validate();
    }

    private List<BitcoinTransactionGTxIO> getUtxos(long accountId, CrystalDatabase db){
        List<BitcoinTransactionGTxIO> answer = new ArrayList<>();
        List<BitcoinTransactionGTxIO> bTGTxI = new ArrayList<>();
        List<BitcoinTransactionGTxIO> bTGTxO = new ArrayList<>();
        List<CryptoCoinTransaction> ccTransactions = db.transactionDao().getByIdAccount(accountId);
        for(CryptoCoinTransaction ccTransaction : ccTransactions) {
            List<BitcoinTransactionGTxIO> gtxios = db.bitcoinTransactionDao().getGtxIOByTransaction(ccTransaction.getId());
            for(BitcoinTransactionGTxIO gtxio : gtxios){
                if(db.bitcoinAddressDao().addressExists(gtxio.getAddress())){
                    if(gtxio.isOutput()){
                        bTGTxO.add(gtxio);
                    }else{
                        bTGTxI.add(gtxio);
                    }
                }
            }
        }
        for(BitcoinTransactionGTxIO gtxi : bTGTxI){
            boolean find = false;
            for(BitcoinTransactionGTxIO gtxo : bTGTxO){
                if(gtxo.getOriginalTxId().equals(gtxi.getOriginalTxId())){
                    find = true;
                    break;
                }
            }
            if(!find){
                answer.add(gtxi);
            }
        }

        return answer;
    }

    class CheckAddressForTransaction implements InsightApiGenerator.HasTransactionListener{
        BitcoinAddressDao bitcoinAddressDao;
        long idAccount;
        DeterministicKey addressKey;
        boolean isChange;
        int lastIndex;

        public CheckAddressForTransaction(BitcoinAddressDao bitcoinAddressDao, long idAccount, DeterministicKey addressKey, boolean isChange, int lastIndex) {
            this.bitcoinAddressDao = bitcoinAddressDao;
            this.idAccount = idAccount;
            this.addressKey = addressKey;
            this.isChange = isChange;
            this.lastIndex = lastIndex;
        }

        @Override
        public void hasTransaction(boolean value) {
            if(value){

                ECKey externalAddrKey = HDKeyDerivation.deriveChildKey(addressKey, new ChildNumber(lastIndex+1, true));
                BitcoinAddress address = new BitcoinAddress();
                address.setChange(isChange);
                address.setAccountId(idAccount);
                address.setIndex(lastIndex+1);
                String addressString =externalAddrKey.toAddress(cryptoCoin.getParameters()).toString();
                address.setAddress(addressString);
                bitcoinAddressDao.insertBitcoinAddresses(address);
                InsightApiGenerator.getTransactionFromAddress(cryptoCoin,addressString,true,
                        new CheckAddressForTransaction(bitcoinAddressDao,idAccount,addressKey,isChange,lastIndex+1));
            }
        }
    }
}
