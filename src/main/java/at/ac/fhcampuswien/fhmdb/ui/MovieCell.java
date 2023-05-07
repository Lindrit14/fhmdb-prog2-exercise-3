package at.ac.fhcampuswien.fhmdb.ui;

import at.ac.fhcampuswien.fhmdb.HomeController;
import at.ac.fhcampuswien.fhmdb.WatchListViewController;
import at.ac.fhcampuswien.fhmdb.data.Database;
import at.ac.fhcampuswien.fhmdb.data.WatchlistRepository;
import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class MovieCell extends ListCell<Movie> {
    private final Label title = new Label();
    private final Label detail = new Label();
    private final Label genre = new Label();
    private final JFXButton detailBtn = new JFXButton("Show Details");
    private final JFXButton watchlistBtn = new JFXButton("");
    private final VBox layout = new VBox(title, detail, genre, watchlistBtn, detailBtn);
    private boolean collapsedDetails = true;
    WatchlistRepository repository = new WatchlistRepository();

    public MovieCell(String buttonText) {
        super();
        // color scheme
        detailBtn.setStyle("-fx-background-color: #f5c518;");
        watchlistBtn.setStyle("-fx-background-color: #f5c518;");
        watchlistBtn.setText(buttonText);
        title.getStyleClass().add("text-yellow");
        detail.getStyleClass().add("text-white");
        genre.getStyleClass().add("text-white");
        genre.setStyle("-fx-font-style: italic");
        layout.setBackground(new Background(new BackgroundFill(Color.web("#454545"), null, null)));

        // layout
        title.fontProperty().set(title.getFont().font(20));
        detail.setWrapText(true);
        layout.setPadding(new Insets(10));
        layout.spacingProperty().set(10);
        layout.alignmentProperty().set(javafx.geometry.Pos.CENTER_LEFT);

        detailBtn.setOnMouseClicked(mouseEvent -> {
            if (collapsedDetails) {
                layout.getChildren().add(getDetails());
                collapsedDetails = false;
                detailBtn.setText("Hide Details");
            } else {
                layout.getChildren().remove(5);
                collapsedDetails = true;
                detailBtn.setText("Show Details");
            }
            setGraphic(layout);
        });

        if (watchlistBtn.getText().equals("Watchlist")){
            watchlistBtn.setOnMouseClicked(mouseEvent -> {
                try {
                    repository.addToWatchlist(getItem());
                } catch (SQLException e) {
                    String title = "Error";
                    String headerText = "Error while adding item to watchlist";
                    String contentText = "The following error occurred while adding the item to watchlist:";
                    showExceptionAlert(title, headerText, contentText, new DatabaseException(headerText, e));
                }
            });
        } else if (watchlistBtn.getText().equals("Remove")) {
            watchlistBtn.setOnMouseClicked(mouseEvent -> {
                try {
                    repository.removeFromWatchlist(getItem());

                } catch (SQLException e) {
                    String title = "Error";
                    String headerText = "Error while removing item from watchlist";
                    String contentText = "The following error occurred while removing the item from watchlist:";
                    showExceptionAlert(title, headerText, contentText, new DatabaseException(headerText, e));
                }
            });
        }
    }

    private VBox getDetails() {
        VBox details = new VBox();
        Label releaseYear = new Label("Release Year: " + getItem().getReleaseYear());
        Label length = new Label("Length: " + getItem().getLengthInMinutes() + " minutes");
        Label rating = new Label("Rating: " + getItem().getRating() + "/10");

        Label directors = new Label("Directors: " + String.join(", ", getItem().getDirectors()));
        Label writers = new Label("Writers: " + String.join(", ", getItem().getWriters()));
        Label mainCast = new Label("Main Cast: " + String.join(", ", getItem().getMainCast()));

        releaseYear.getStyleClass().add("text-white");
        length.getStyleClass().add("text-white");
        rating.getStyleClass().add("text-white");
        directors.getStyleClass().add("text-white");
        writers.getStyleClass().add("text-white");
        mainCast.getStyleClass().add("text-white");

        details.getChildren().add(releaseYear);
        details.getChildren().add(rating);
        details.getChildren().add(length);
        details.getChildren().add(directors);
        details.getChildren().add(writers);
        details.getChildren().add(mainCast);
        return details;
    }
    @Override
    protected void updateItem(Movie movie, boolean empty) {
        super.updateItem(movie, empty);

        if (empty || movie == null) {
            setGraphic(null);
            setText(null);
        } else {
            this.getStyleClass().add("movie-cell");
            title.setText(movie.getTitle());
            detail.setText(
                    movie.getDescription() != null
                            ? movie.getDescription()
                            : "No description available"
            );

            String genres = movie.getGenres()
                    .stream()
                    .map(Enum::toString)
                    .collect(Collectors.joining(", "));
            genre.setText(genres);

            detail.setMaxWidth(this.getScene().getWidth() - 30);

            setGraphic(layout);
        }
    }

    //https://www.youtube.com/watch?v=rhlchwZstcw&t=396s
    //UI for displaying Exceptions to the User during Runtime in the Application
    public static void showExceptionAlert(String title, String headerText, String contentText, Exception ex) {
        // Create a new alert  with the given error
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // create extra ui component so that the whole stack trace gets put into there and does not fill up the whole window
        // stack trace need to be converted into a string? Is it not already a string?
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        //label and textarea for the stack tace
        Label label = new Label("Exception stacktrace:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        //styling options

        //fill the dead space
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        //layout stuff, like grid in css, so
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // set the additional info to the Alert dialog
        alert.getDialogPane().setExpandableContent(expContent);

        //stays open until client closes it
        alert.showAndWait();
    }




}

