package sg.com.colormemory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import sg.com.colormemory.entity.HighScore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anky on 08-03-2016.
 */
public class HighScoreDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_SCORE };

    public HighScoreDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public HighScore createHighScore(HighScore highScore) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, highScore.getName());
        values.put(MySQLiteHelper.COLUMN_SCORE, highScore.getScore());
        long insertId = database.insert(MySQLiteHelper.TABLE_HIGHSCORE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_HIGHSCORE,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        HighScore highscore = cursorToHighScore(cursor);
        cursor.close();
        return highscore;
    }

    public void deleteComment(int idn) {
        long id = idn;
        System.out.println("highscore deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_HIGHSCORE, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<HighScore> getAllComments() {
        List<HighScore> scores = new ArrayList<HighScore>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_HIGHSCORE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HighScore highScore = cursorToHighScore(cursor);
            scores.add(highScore);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return scores;
    }

    private HighScore cursorToHighScore(Cursor cursor) {
        HighScore highScore = new HighScore();
        highScore.setId(cursor.getLong(0));
        highScore.setName(cursor.getString(1));
        highScore.setScore(cursor.getString(2));
        System.out.print("HighScore: " + highScore.getName());
        return highScore;
    }

}
