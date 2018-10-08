package cy.agorise.crystalwallet.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Represents a Bitcoin alike Transaction
 *
 * Created by Henry Varona on 10/2/2018.
 */
public class BitcoinTransactionExtended {

    @Embedded
    public CryptoCoinTransaction cryptoCoinTransaction;

    @Embedded
    public BitcoinTransaction bitcoinTransaction;

    @Relation(parentColumn = "id", entityColumn = "bitcoin_transaction_id", entity = BitcoinTransactionGTxIO.class)
    public List<BitcoinTransactionGTxIO> bitcoinTransactionGTxIOList;
}
