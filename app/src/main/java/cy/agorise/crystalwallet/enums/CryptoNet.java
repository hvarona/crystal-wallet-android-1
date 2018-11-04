package cy.agorise.crystalwallet.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cy.agorise.crystalwallet.R;

/**
 * CryptoNet Enumeration, a Crypto Net is define as the net where a CryptoCoin works, iniside the
 * CrypotNet is where the transaction and balance works, using the CryptoCoin Assets
 *
 * Created by Henry Varona on 12/9/2017.
 */
public enum CryptoNet implements Serializable {
    UNKNOWN("UNKNOWN",6,-1,android.R.drawable .ic_menu_help),
    BITCOIN("BITCOIN",6,1,R.drawable.coin_icon_bitcoin),
    BITCOIN_TEST("BITCOIN(TEST)",6,2,R.drawable.coin_icon_bitcoin),
    LITECOIN("LITECOIN",6,3,R.drawable.coin_icon_litecoin),
    DASH("DASH",6,5,R.drawable.coin_icon_dash),
    DOGECOIN("DOGECOIN",6,4,R.drawable.coin_icon_doge),
    BITSHARES("BITSHARES",1,6,R.drawable.bts),
    STEEM("STEEM",1,7,R.drawable.coin_icon_steem);

    protected String label;

    protected int confirmationsNeeded;

    protected int bip44Index;

    protected int iconImageResource;

    private static Map<Integer, CryptoNet> bip44Map = new HashMap<Integer, CryptoNet>();
    static {
        for (CryptoNet cryptoNetEnum : CryptoNet.values()) {
            bip44Map.put(cryptoNetEnum.bip44Index, cryptoNetEnum);
        }
    }

    CryptoNet(String label,int confirmationsNeeded, int bip44Index, int iconImageResource){
        this.label = label;
        this.confirmationsNeeded = confirmationsNeeded;
        this.bip44Index = bip44Index;
        this.iconImageResource = iconImageResource;
    }

    public String getLabel(){
        return this.label;
    }

    public int getConfirmationsNeeded(){
        return this.confirmationsNeeded;
    }

    public int getBip44Index(){
        return this.bip44Index;
    }

    public int getIconImageResource() {
        return this.iconImageResource;
    }

    public static CryptoNet fromBip44Index(int index){
        if (bip44Map.containsKey(index)) {
            return bip44Map.get(index);
        } else {
            return CryptoNet.UNKNOWN;
        }
    }
}
