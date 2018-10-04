package cy.agorise.crystalwallet.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import cy.agorise.crystalwallet.models.BitcoinTransactionExtended;

/**
 * Created by Henry Varona on 10/02/2018.
 */
@Dao
public interface BitcoinTransactionDao {

    @Query("SELECT * FROM crypto_coin_transaction cct, bitcoin_transaction bt WHERE bt.crypto_coin_transaction_id = cct.id")
    LiveData<BitcoinTransactionExtended> getAll();
}
