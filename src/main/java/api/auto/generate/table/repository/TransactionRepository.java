package api.auto.generate.table.repository;

import api.auto.generate.table.entity.Transaction;
import api.auto.generate.table.utill.FileHandling;

public class TransactionRepository extends FileHandling<Transaction, Transaction[]> {

    public  TransactionRepository() {
        super("transactions.json", Transaction[].class);
    }

}
