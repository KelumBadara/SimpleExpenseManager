package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Subhash on 12/4/2015.
 */
public class InDatabaseTransactionDAO implements TransactionDAO {

    private DatabaseHelper dbHelper;

    public InDatabaseTransactionDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ACCOUNT_NO, accountNo);
        values.put(DatabaseHelper.DATE, getDateTime(date));
        values.put(DatabaseHelper.TYPE, expenseType.toString());
        values.put(DatabaseHelper.AMOUNT, amount);

        db.insert(DatabaseHelper.TRANSACTION, null, values);

        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT  * FROM " + DatabaseHelper.TRANSACTION;

        Log.e(DatabaseHelper.LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(null,null,null,0);

                Date date = null;
                try {
                    date = getDateTime(c.getString(c.getColumnIndex(DatabaseHelper.DATE)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transaction.setAccountNo(c.getString((c.getColumnIndex(DatabaseHelper.ACCOUNT_NO))));
                transaction.setDate(date);
                transaction.setExpenseType(ExpenseType.valueOf(c.getString(c.getColumnIndex(DatabaseHelper.TYPE))));
                transaction.setAmount(c.getDouble(c.getColumnIndex(DatabaseHelper.AMOUNT)));


                transactions.add(transaction);
            } while (c.moveToNext());
        }

        db.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = getAllTransactionLogs();
        int size = transactions.size();

        if (size <= limit) {
            return transactions;
        }

        return transactions.subList(size - limit, size);
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date getDateTime(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


        Date date1 = format.parse(date);
        return date1;

    }

}