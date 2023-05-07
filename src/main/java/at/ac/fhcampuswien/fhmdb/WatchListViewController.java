package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.data.WatchlistMovieEntity;
import at.ac.fhcampuswien.fhmdb.data.WatchlistRepository;
import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static at.ac.fhcampuswien.fhmdb.ui.MovieCell.showExceptionAlert;

public class WatchListViewController {

    @FXML
    public Button homeBtn;
    @FXML
    public Button watchlistBtn;
    @FXML
    public Button aboutBtn;

    @FXML
    public VBox mainBox;

    WatchlistRepository repository;

    @FXML
    public JFXListView movieListView;


    public void initialize(){
        System.out.println("WatchList Controller initialized");
        repository = new WatchlistRepository();
        List<WatchlistMovieEntity> movieEntities = new ArrayList<>();

        try {
            movieEntities = repository.readAllMovies();
        } catch (SQLException e) {
            String title = "Error";
            String headerText = "Error for database";
            String contentText = "The following error occurred: " + e.getMessage();
            showExceptionAlert(title, headerText, contentText + e.getMessage(), new DatabaseException(e));
        }

        for (WatchlistMovieEntity element : movieEntities){
            System.out.println(element);
        }

        ObservableList<Movie> observableMovies = FXCollections.observableArrayList(
                movieEntities.stream()
                        .map(WatchlistMovieEntity::createMovie)
                        .collect(Collectors.toList()));


        movieListView.setItems(observableMovies);   // set the items of the listview to the observable list
        movieListView.setCellFactory(movieListView -> new MovieCell("Remove")); // apply custom cells to the listview
    }


}