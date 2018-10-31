package cy.agorise.crystalwallet.network;

import cy.agorise.crystalwallet.apigenerator.insightapi.GetGenesisBlock;
import cy.agorise.crystalwallet.enums.CryptoCoin;

public class BitcoinCryptoNetVerifier extends CryptoNetVerifier{

    final CryptoCoin cryptoCoin;

    public BitcoinCryptoNetVerifier(CryptoCoin cryptoCoin) {
        this.cryptoCoin = cryptoCoin;
    }

    @Override
    public void checkURL(final String url) {

        final long startTime = System.currentTimeMillis();
        GetGenesisBlock genesisBloc = new GetGenesisBlock(url, new GetGenesisBlock.genesisBlockListener() {
            @Override
            public void genesisBlock(String value) {
                if(cryptoCoin.getParameters()!= null){
                    if(value.equals(cryptoCoin.getParameters().getGenesisBlock().getHashAsString())){

                        CryptoNetManager.verifiedCryptoNetURL(cryptoCoin.getCryptoNet(), url, System.currentTimeMillis() - startTime);
                    }
                    //TODO bad genesis block
                }else{
                    CryptoNetManager.verifiedCryptoNetURL(cryptoCoin.getCryptoNet(), url, System.currentTimeMillis() - startTime);
                }
            }

            @Override
            public void fail() {
                //TODO failed
            }
        });
    }

    @Override
    public String getChainId() {
        if(cryptoCoin == null || cryptoCoin.getParameters()== null) {
            return null;
        }
        return cryptoCoin.getParameters().getGenesisBlock().getHashAsString();
    }
}
