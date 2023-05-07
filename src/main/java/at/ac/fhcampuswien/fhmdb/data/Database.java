package at.ac.fhcampuswien.fhmdb.data;

import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

//import static at.ac.fhcampuswien.fhmdb.ui.MovieCell.showExceptionAlert;

public class Database {
    public static final String DB_URL = "jdbc:h2:file: ./db/watchlistDB";
    public static final String user = "JamesPonce";
    public static final String password = "MITP?";

    private static ConnectionSource connectionSource;

    private Dao<WatchlistMovieEntity, Long> dao;

    private static Database instance;

    private Database() {
        try {
            createConnectionSource();
            dao = DaoManager.createDao(connectionSource,WatchlistMovieEntity.class);
            createTables();
        } catch (SQLException e){
            String title = "Error";
            String headerText = "Error while initializing database";
            String contentText = "The following error occurred while initializing the database:";
            //showExceptionAlert(title, headerText, contentText, new DatabaseException(e));
        }
    }

    public static Database getDatabase(){
        if (instance == null){
            instance = new Database();
        }

        return instance;
    }

    private static void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, WatchlistMovieEntity.class);
    }

    private static void createConnectionSource() throws SQLException {
        connectionSource = new JdbcConnectionSource(DB_URL,user,password);
    }

    public Dao<WatchlistMovieEntity, Long> getDao() {
        return this.dao;
    }

}
