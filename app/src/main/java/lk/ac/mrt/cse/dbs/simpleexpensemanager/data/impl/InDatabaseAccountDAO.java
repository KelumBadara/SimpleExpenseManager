

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.sqlite.SQLiteDatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class InDatabaseAccountDAO implements AccountDAO {
    private DatabaseHelper dbhlp;

    public InDatabaseAccountDAO(DatabaseHelper data) {
        this.dbhlp = data;
    }

    @Override
    public List<String> getAccountNumbersList() {



        SQLiteDatabase db = dbhlp.getReadableDatabase();
        ArrayList <String> accNoList = new ArrayList<String>();
        String selectQuery = "SELECT " + DatabaseHelper.ACC_NUM + " FROM " + DatabaseHelper.ACCOUNT;

        Log.e(DatabaseHelper.LOG, selectQuery);




        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                accNoList.add(c.getString(c.getColumnIndex(DatabaseHelper.ACC_NUM)));
            } while (c.moveToNext());
        }

        db.close();

        return accNoList;


    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbhlp.getReadableDatabase();
        List<Account> accounts = new ArrayList<Account>();
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.ACCOUNT;

        Log.e(DatabaseHelper.LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Account account = new Account(null,null,null,0);
                account.setAccountNo(c.getString((c.getColumnIndex(DatabaseHelper.ACC_NUM))));
                account.setBankName((c.getString(c.getColumnIndex(DatabaseHelper.BANK))));
                account.setAccountHolderName(c.getString(c.getColumnIndex(DatabaseHelper.ACCOUNT_HOLDER)));
                account.setBalance(c.getDouble(c.getColumnIndex(DatabaseHelper.BALANCE)));

                accounts.add(account);
            } while (c.moveToNext());
        }

        db.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbhlp.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DatabaseHelper.ACCOUNT + " WHERE "
                + DatabaseHelper.ACC_NUM + " = (?)";

        //Log.e(DatabaseHelper.LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[]{accountNo});
        if (c != null) {
            c.moveToFirst();


            Account account = new Account(null,null,null,0);
            account.setAccountNo(c.getString((c.getColumnIndex(DatabaseHelper.ACC_NUM))));
            account.setBankName((c.getString(c.getColumnIndex(DatabaseHelper.BANK))));
            account.setAccountHolderName(c.getString(c.getColumnIndex(DatabaseHelper.ACCOUNT_HOLDER)));
            account.setBalance(c.getDouble(c.getColumnIndex(DatabaseHelper.BALANCE)));
            db.close();

            return account;
        }
        db.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {


        SQLiteDatabase db = dbhlp.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ACC_NUM, account.getAccountNo());
        values.put(DatabaseHelper.BANK, account.getBankName());
        values.put(DatabaseHelper.ACCOUNT_HOLDER, account.getAccountHolderName());
        values.put(DatabaseHelper.BALANCE, account.getBalance());

        db.insert(DatabaseHelper.ACCOUNT, null, values);
        db.close();





    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbhlp.getReadableDatabase();
        int success = db.delete(DatabaseHelper.ACCOUNT, DatabaseHelper.ACC_NUM + " = ?",
                new String[]{accountNo});
        db.close();
        if(success == 0){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = getAccount(accountNo);

        if (account == null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        SQLiteDatabase db = dbhlp.getWritableDatabase();

        switch (expenseType) {
            case EXPENSE:
                double balance1 = account.getBalance() - amount;
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(DatabaseHelper.BALANCE, balance1);

                db.update(DatabaseHelper.ACCOUNT, contentValues1, DatabaseHelper.ACC_NUM + " = ?", new String[]{accountNo});
                db.close();
                break;

            case INCOME:
                double balance2 = account.getBalance() + amount;
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(DatabaseHelper.BALANCE, balance2);

                db.update(DatabaseHelper.ACCOUNT, contentValues2, DatabaseHelper.ACC_NUM + " = ?", new String[]{accountNo});
                db.close();
                break;
        }
    }
}
