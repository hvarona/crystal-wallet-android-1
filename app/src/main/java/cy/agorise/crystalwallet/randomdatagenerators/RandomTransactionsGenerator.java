package cy.agorise.crystalwallet.randomdatagenerators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import cy.agorise.crystalwallet.models.CryptoCoinTransaction;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 20/9/2017.
 */

public class RandomTransactionsGenerator {

    static public List<CryptoCoinTransaction> generateTransactions(List<CryptoNetAccount> accounts, int numberOfTransactions, int minTimestamp, int maxTimestamp, int minAmount, int maxAmount){
        ArrayList<CryptoCoinTransaction> result = new ArrayList<CryptoCoinTransaction>();
        Random randomGenerator = new Random();
        Calendar cal = Calendar.getInstance();
        int randomTimeStamp;
        int randomAmount;
        CryptoCoinTransaction randomTransaction;

        for (int i=0;i<numberOfTransactions;i++){
            int randomAccountIndex = randomGenerator.nextInt(accounts.size());
            CryptoNetAccount randomSelectedAccount = accounts.get(randomAccountIndex);
            randomAmount = randomGenerator.nextInt((maxAmount - minAmount) + 1) + minAmount;
            randomTimeStamp = randomGenerator.nextInt((maxTimestamp - minTimestamp) + 1) + minTimestamp;
            cal.setTimeInMillis(randomTimeStamp*1000);

            randomTransaction = new CryptoCoinTransaction();
            randomTransaction.setAccountId(randomSelectedAccount.getId());
            randomTransaction.setAmount(randomAmount);
            randomTransaction.setFrom("friend"+i);
            randomTransaction.setTo("me"+i);
            randomTransaction.setDate(cal.getTime());
            result.add(randomTransaction);
        }

        return result;
    }
}
