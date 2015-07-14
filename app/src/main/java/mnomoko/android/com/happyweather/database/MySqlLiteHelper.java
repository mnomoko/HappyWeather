package mnomoko.android.com.happyweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mnomoko on 05/07/15.
 */
public class MySqlLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "CityDB";

    public MySqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// SQL statement to create city table
        String CREATE_CITY_TABLE = "CREATE TABLE IF NOT EXISTS cities ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, "+
                "code TEXT )";

        // create cities table
        db.execSQL(CREATE_CITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older cities table if existed
        db.execSQL("DROP TABLE IF EXISTS cities");

        // create fresh cities table
        this.onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Drop older cities table if existed
        db.execSQL("DROP TABLE IF EXISTS cities");
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_CITY_TABLE = "CREATE TABLE cities ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, "+
                "code TEXT )";

        // create cities table
        db.execSQL(CREATE_CITY_TABLE);
    }


    /**
     * CRUD operations (create "add", read "get", update, delete) city + get all cities + delete all cities
     */

    // Cities table name
    private static final String TABLE_CITIES = "cities";

    // Cities Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE = "code";

    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_CODE};

    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CITIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void addCities(List<String> cities) {

        SQLiteDatabase db = this.getWritableDatabase();
//        onCreate(db);

        db.beginTransaction();
        String sql = "INSERT INTO " + TABLE_CITIES + " ("+ KEY_NAME + ", " + KEY_CODE + ") values(?,?)";
        SQLiteStatement insert = db.compileStatement(sql);

        for(String s : cities) {

            String[] array = s.split("\t");

            insert.bindString(1, array[1]);
            insert.bindString(2, array[4]);
            insert.execute();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        // 4. close
        db.close();

    }

    public void addCitiesNative(List<String> cities) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        int i = 0;
        for(String s : cities) {

            String[] array = s.split("\t");


            // 2. create ContentValues to add key "column"/value
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, array[1]); // get name
            values.put(KEY_CODE, array[4]); // get code


            // 3. insert
            db.insert(TABLE_CITIES, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values

            if(i == 1000) {
                Log.e("MySqlLiteHealper", "" + i);
                i = 0;
            }
            i++;
        }


        db.setTransactionSuccessful();
        db.endTransaction();
        // 4. close
        db.close();
    }

    public void addCity(City city){
//        Log.d("addCity", city.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, city.getName()); // get name
        values.put(KEY_CODE, city.getCode()); // get code

        // 3. insert
        db.insert(TABLE_CITIES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public City getCity(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_CITIES, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build city object
        City city = new City();
        city.setId(Integer.parseInt(cursor.getString(0)));
        city.setName(cursor.getString(1));
        city.setCode(cursor.getString(2));

//        Log.d("getCity(" + id + ")", city.toString());

        // 5. return city
        return city;
    }

    // Read records related to the search term
    public City[] read(String searchTerm) {

        // select query
        String sql = "";
        sql += "SELECT * FROM " + TABLE_CITIES;
//        sql += " WHERE " + KEY_NAME + " LIKE '%" + searchTerm + "%'";
        sql += " WHERE " + KEY_NAME + " = '" + searchTerm + "'";
        sql += " ORDER BY " + KEY_ID + " DESC";
        sql += " LIMIT 0,8";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.getCount() == 0) {

            // select query
            sql = "";
            sql += "SELECT * FROM " + TABLE_CITIES;
    //        sql += " WHERE " + KEY_NAME + " LIKE '%" + searchTerm + "%'";
            sql += " WHERE " + KEY_NAME + " LIKE '" + searchTerm + "%'";
            sql += " ORDER BY " + KEY_ID + " DESC";
            sql += " LIMIT 0,8";

            // execute the query
            cursor = db.rawQuery(sql, null);
        }

        int recCount = cursor.getCount();

        City[] ObjectItemData = new City[recCount];
        int x = 0;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String objectName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String objectCode = cursor.getString(cursor.getColumnIndex(KEY_CODE));
//                Log.e("Unknown", "objectName: " + objectName);

                City myObject = new City(objectName, objectCode);

                ObjectItemData[x] = myObject;

                x++;

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ObjectItemData;

    }

    // Get All Cities
    public List<City> getAllCities() {
        List<City> cities = new LinkedList<City>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_CITIES;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build city and add it to list
        City city = null;
        if (cursor.moveToFirst()) {
            do {
                city = new City();
                city.setId(Integer.parseInt(cursor.getString(0)));
                city.setName(cursor.getString(1));
                city.setCode(cursor.getString(2));

                // Add city to cities
                cities.add(city);
            } while (cursor.moveToNext());
        }

//        Log.d("getAllCities()", cities.toString());

        // return cities
        return cities;
    }

    // Updating single city
    public int updateCity(City city) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("name", city.getName()); // get name
        values.put("code", city.getCode()); // get code

        // 3. updating row
        int i = db.update(TABLE_CITIES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(city.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single city
    public void deleteCity(City city) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_CITIES,
                KEY_ID+" = ?",
                new String[] { String.valueOf(city.getId()) });

        // 3. close
        db.close();

//        Log.d("deleteCity", city.toString());

    }
}
