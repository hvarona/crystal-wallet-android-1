package cy.agorise.crystalwallet.enums;

import org.bitcoinj.core.NetworkParameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents each supported Crypto Coin
 *
 * Created by Henry Varona on 12/9/2017.
 */

public enum CryptoCoin implements Serializable {
    BITCOIN(CryptoNet.BITCOIN,"BTC",8,0,NetworkParameters.fromID(NetworkParameters.ID_TESTNET)),
    BITCOIN_TEST(CryptoNet.BITCOIN_TEST,"BTC",8,1,NetworkParameters.fromID(NetworkParameters.ID_TESTNET)),
    LITECOIN(CryptoNet.LITECOIN,"LTC",8,2,null),
    DASH(CryptoNet.DASH,"DASH",8,5,null),
    DOGECOIN(CryptoNet.DOGECOIN,"DOGE",8,3,null),
    BITSHARES(CryptoNet.BITSHARES,"BTS",5,0,null),
    STEEM(CryptoNet.STEEM,"BTS",5,0,null);

    protected CryptoNet cryptoNet;
    protected String label;
    protected int precision;
    protected int coinNumber;
    protected NetworkParameters parameters;

    CryptoCoin(CryptoNet cryptoNet, String label, int precision, int coinNumber, NetworkParameters parameters){
        this.cryptoNet = cryptoNet;
        this.label = label;
        this.precision = precision;
        this.coinNumber = coinNumber;
        this.parameters = parameters;

    }

    public CryptoNet getCryptoNet(){
        return this.cryptoNet;
    }
    public String getLabel(){
        return this.label;
    }
    public int getPrecision(){
        return this.precision;
    }
    public NetworkParameters getParameters() {
        return parameters;
    }

    public int getCoinNumber() {
        return coinNumber;
    }

    public static List<CryptoCoin> getByCryptoNet(CryptoNet cryptoNet){
        List<CryptoCoin> result = new ArrayList<CryptoCoin>();

        for (CryptoCoin nextCryptoCoin : CryptoCoin.values()){
            if (nextCryptoCoin.getCryptoNet().equals(cryptoNet)) {
                result.add(nextCryptoCoin);
            }
        }

        return result;
    }

}
