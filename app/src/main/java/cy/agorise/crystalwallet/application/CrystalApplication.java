package cy.agorise.crystalwallet.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.idescout.sql.SqlScoutServer;

import java.util.Locale;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.apigenerator.GrapheneApiGenerator;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.BitsharesAsset;
import cy.agorise.crystalwallet.models.BitsharesAssetInfo;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoCurrencyEquivalence;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.network.CryptoNetManager;
import cy.agorise.crystalwallet.notifiers.CrystalWalletNotifier;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetEvents;
import cy.agorise.crystalwallet.service.CrystalWalletService;

/**
 * The main application
 *
 * Created by Henry Varona on 6/9/2017.
 */

public class CrystalApplication extends Application {
    private Locale locale = null;

    public static String BITSHARES_URL[] =
            {
                    "wss://de.palmpay.io/ws",
                    "wss://nl.palmpay.io/ws",
                    "wss://mx.palmpay.io/ws",
                    "wss://us.nodes.bitshares.ws/ws",
                    "wss://eu.nodes.bitshares.ws/ws",
                    "wss://sg.nodes.bitshares.ws/ws",
                    "wss://dallas.bitshares.apasia.tech/ws"
            };

    //This is for testing the equivalent values on the testnet TODO remove
    public static BitsharesAsset bitUSDAsset = new BitsharesAsset("USD",4,"1.3.121",BitsharesAsset.Type.SMART_COIN);
    //This is for testing the equivalent values on the testnet TODO remove
    public static BitsharesAsset bitEURAsset = new BitsharesAsset("EUR",4,"1.3.120",BitsharesAsset.Type.SMART_COIN);


    public static final String BITCOIN_SERVER_URLS[] ={
            "https://test-insight.bitpay.com",
            "https://testnet.blockexplorer.com/",
            //"https://insight.bitpay.com/"

    };

    public static final CryptoCurrency BITCOIN_CURRENCY = new CryptoCurrency("BTC",CryptoNet.BITCOIN,8);

    public static String STEEM_URL[] =
            {
                    "https://api.steemit.com",
                    "https://api.steemitdev.com",
                    "https://api.steemitstage.com",
                    "https://api.steem.house",
                    "https://appbasetest.timcliff.com",


            };

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize the database
        CrystalDatabase db = CrystalDatabase.getAppDatabase(this.getApplicationContext());
        SqlScoutServer.create(this, getPackageName());

        //Using Bitshares Agorise Testnet
        //CryptoNetManager.addCryptoNetURL(CryptoNet.BITSHARES,BITSHARES_TESTNET_URL);

        //This is for testing the equivalent values on the testnet TODO remove
        if(db.bitsharesAssetDao().getBitsharesAssetInfoById(bitEURAsset.getBitsharesId())== null){
            if(db.cryptoCurrencyDao().getByName(bitEURAsset.getName(),bitEURAsset.getCryptoNet().name())== null){
                db.cryptoCurrencyDao().insertCryptoCurrency(bitEURAsset);
            }
            long idCurrency = db.cryptoCurrencyDao().getByName(bitEURAsset.getName(),bitEURAsset.getCryptoNet().name()).getId();
            BitsharesAssetInfo info = new BitsharesAssetInfo(bitEURAsset);
            info.setCryptoCurrencyId(idCurrency);
            db.bitsharesAssetDao().insertBitsharesAssetInfo(info);

        }

        //This is for testing the equivalent values on the testnet TODO remove
        if(db.bitsharesAssetDao().getBitsharesAssetInfoById(bitUSDAsset.getBitsharesId())== null){
            if(db.cryptoCurrencyDao().getByName(bitUSDAsset.getName(),bitUSDAsset.getCryptoNet().name())== null){
                db.cryptoCurrencyDao().insertCryptoCurrency(bitUSDAsset);
            }
            long idCurrency = db.cryptoCurrencyDao().getByName(bitUSDAsset.getName(),bitUSDAsset.getCryptoNet().name()).getId();
            BitsharesAssetInfo info = new BitsharesAssetInfo(bitUSDAsset);
            info.setCryptoCurrencyId(idCurrency);
            db.bitsharesAssetDao().insertBitsharesAssetInfo(info);

        }

        //The crystal notifier is initialized
        CrystalWalletNotifier crystalWalletNotifier = new CrystalWalletNotifier(this);
        CryptoNetEvents.getInstance().addListener(crystalWalletNotifier);

        //Next line is for use the bitshares main net
        // TODO fix, the following line accepts one string not an array it needs to accept an arrey
        // TODO and hoop over the urls if no connection can be established
        CryptoNetManager.addCryptoNetURL(CryptoNet.BITSHARES,BITSHARES_URL);

        //Adding Bitcoin info
        CryptoNetManager.addCryptoNetURL(CryptoNet.BITCOIN,BITCOIN_SERVER_URLS);

        if(db.cryptoCurrencyDao().getByName(BITCOIN_CURRENCY.getName(),BITCOIN_CURRENCY.getCryptoNet().name())== null){
            db.cryptoCurrencyDao().insertCryptoCurrency(BITCOIN_CURRENCY);
        }

        CryptoNetManager.addCryptoNetURL(CryptoNet.STEEM,STEEM_URL);


        GeneralSetting generalSettingPreferredLanguage = db.generalSettingDao().getSettingByName(GeneralSetting.SETTING_NAME_PREFERRED_LANGUAGE);

        if (generalSettingPreferredLanguage != null) {
            Resources resources = getBaseContext().getResources();
            Locale locale = new Locale(generalSettingPreferredLanguage.getValue());
            Locale.setDefault(locale);
            DisplayMetrics dm = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, dm);
        }

        Intent intent = new Intent(getApplicationContext(), CrystalWalletService.class);
        startService(intent);
    }
}
