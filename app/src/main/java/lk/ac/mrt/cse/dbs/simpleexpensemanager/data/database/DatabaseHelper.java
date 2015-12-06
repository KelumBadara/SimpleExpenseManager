package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

/**
 * Created by Kelum on 06/12/2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{


    // Logcat tag
    public static final String LOG = DatabaseHelper.class.getName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "130059P";

    // table names
    public static final String ACCOUNT = "accounts";
    public static final String TRANSACTION = "transactions";

    // account Table Columns names
    public static final String ACC_NUM = "id";
    public static final String BANK = "bank";
    public static final String ACCOUNT_HOLDER = "account_holder";
    public static final String BALANCE = "balance";

    //transaction table column names
    public static final String DATE = "date";
    public static final String ACCOUNT_NO = "account_number";
    public static final String TYPE = "type";
    public static final String AMOUNT = "amount";



    String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + ACCOUNT + "("
            + ACC_NUM + " TEXT PRIMARY KEY," + BANK + " TEXT,"
            + ACCOUNT_HOLDER + " TEXT," +BALANCE + " DOUBLE" + ")";

    String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TRANSACTION + "("
            + DATE + " DATE," + ACCOUNT_NO + " TEXT,"
            + TYPE + " TEXT," +AMOUNT + " DOUBLE" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION);

        onCreate(db);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
