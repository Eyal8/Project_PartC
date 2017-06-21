package View;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.security.PublicKey;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Shani on 18/06/2017.
 */
public class View implements Observer, IView {

    private MyViewModel viewModel;
    private MazeDisplayer mazeDisplayer;
    @FXML
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Button solve_button;
    public  javafx.scene.control.Label Char_row;
    public  javafx.scene.control.Label Char_column;
    public  javafx.scene.control.MenuItem newFile;

    //public void setViewModel
    public void generateMaze() {
        int rows = Integer.valueOf(txtfld_rowsNum.getText());
        int columns = Integer.valueOf(txtfld_columnsNum.getText());
        newFile.setDisable(true);
        solve_button.setDisable(false);
        if(rows<10 || columns<10)
        {
            showAlert("Too small dimensions - generate default 10X10 maze...");
            viewModel.generateMaze(10, 10);
        }
        else
            viewModel.generateMaze(rows, columns);
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }
    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.KeyPressed(keyEvent.getCode());
        keyEvent.consume();
    }

    public void solveMaze(ActionEvent actionEvent) {

        showAlert("Solving maze..");
        mazeDisplayer.setSolve(true);
        mazeDisplayer.redraw();
        mazeDisplayer.setSolve(false);
        actionEvent.consume();

    }
    @Override
    public void update(Observable o, Object arg) {
        if(o == viewModel)
        {
            mazeDisplayer.setMaze(viewModel.getBoard());
            displayMaze(viewModel.getBoard());
            newFile.setDisable(false);
            solve_button.setDisable(false);
            mazeDisplayer.requestFocus();
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
       // mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
       // CharacterRow.set(characterPositionRow + "");
      //  CharacterColumn.set(characterPositionColumn + "");
    }

    public void setRows(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            if (rows < 10) {
                showAlert("The maze is too small! what are you child?");
                viewModel.generateMaze(10, 10);
            }
            else {
                txtfld_columnsNum.requestFocus();
            }
            keyEvent.consume();
        }
    }

    public void setCols(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
            if (columns < 10) {
                showAlert("The maze is too small! what are you child?");
                viewModel.generateMaze(10, 10);
            }
            else
                generateMaze();
            keyEvent.consume();
        }
    }
    public void zooming(ScrollEvent se)
    {
        if(se.isControlDown() && se.getDeltaY() < 0)
        {
            mazeDisplayer.setHeight(mazeDisplayer.getHeight() + se.getDeltaY());
            mazeDisplayer.setWidth(mazeDisplayer.getWidth() + se.getDeltaY());
            mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
        }
        else if(se.isControlDown() && se.getDeltaY() > 0)
        {
            mazeDisplayer.setHeight(mazeDisplayer.getHeight() + se.getDeltaY());
            mazeDisplayer.setWidth(mazeDisplayer.getWidth() + se.getDeltaY());
            mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
        }
        se.consume();
    }

    public void load()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("load maze");
        fc.setInitialDirectory(new File("./savedMazes"));
        File chosen = fc.showOpenDialog(null);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("./savedMazes/" + chosen.getName());
            ObjectInputStream is = new ObjectInputStream(fis);
            mazeDisplayer = (MazeDisplayer) is.readObject();
            generateMaze();
            //mazeDisplayer.setCharacterPosition(mazeDisplayer.getCharacterPositionRow(), mazeDisplayer.getCharacterPositionColumn());
            is.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            showAlert("FileNotFoundException");

        }
        catch (IOException e)
        {
            System.out.println("IOException");
            showAlert("IOException");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("ClassNotFoundException");
            showAlert("ClassNotFoundException");

        }
    }

    public void save()
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Saving the maze");
        fc.setInitialDirectory(new File("./savedMazes"));
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        File chosen = fc.showSaveDialog(null);
        try {
            File file = new File("./savedMazes/" + chosen.getName());
            FileOutputStream fo = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fo);
            os.writeObject(mazeDisplayer);
            fo.close();
            os.close();
        } catch (IOException ex) {
            System.out.println("IOException");
        }
    }

    public void exit(){
        Platform.exit();
    }

    public void aboutTheProgrammers(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Who are we?");
        alert.setHeaderText("information about us");
        alert.setContentText("Our names are Eyal Arviv and Shani Houri and we are totaly awesome!");

        alert.showAndWait();
    }
    public void aboutTheAlgorithms(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Algorithms");
        alert.setHeaderText("Information about the algorithms used in this game");
        alert.setContentText("In this game we used a few algorithms:\n" +
                "first, the algorithm to generate the maze the DFS (Depth First Search) algorithm.\n" +
                "second, the algorithm to solve the maze the Best first search algorithm.");
        // alert.setContentText("first, the algorithm to generate the maze the DFS (Depth First Search) algorithm.");
        // alert.setContentText("second, the algorithm to solve the maze the Best first search algorithm");

        alert.showAndWait();
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        Char_column.textProperty().bind(viewModel.characterColumnProperty());
        Char_row.textProperty().bind(viewModel.characterRowProperty());
    }
}