package at.ac.fhcampuswien.fhmdb.data;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.dao.Dao;
import javafx.scene.chart.PieChart;

import java.sql.SQLException;
import java.util.List;

public class WatchlistRepository {

    Dao<WatchlistMovieEntity, Long> dao;

    public WatchlistRepository(){
        this.dao = Database.getDatabase().getDao();
    }

    public void addToWatchlist(Movie movie) throws SQLException {
        if (dao.queryForMatching(movieToWatchlist(movie)).isEmpty()){
            dao.create(movieToWatchlist(movie));
        }else {
            System.out.println("Object already exists");
        }
    }

    public void removeFromWatchlist(Movie movie) throws SQLException {
        dao.delete(dao.queryForEq("apiId",movie.getId()));
    }

    public List<WatchlistMovieEntity> readAllMovies() throws SQLException{
        return dao.queryForAll();
    }

    private WatchlistMovieEntity movieToWatchlist(Movie movie){
        return new WatchlistMovieEntity(movie.getId(),movie.getTitle(), movie.getDescription(), WatchlistMovieEntity.genresToString(movie.getGenres()),movie.getReleaseYear(),movie.getImgUrl(),movie.getLengthInMinutes(),movie.getRating());
    }
}