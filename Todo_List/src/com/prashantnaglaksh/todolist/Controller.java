package com.prashantnaglaksh.todolist;

import datamodel.TodoData;
import datamodel.Todoitem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    private List<Todoitem> todoitems;
    @FXML
    private ListView<Todoitem> todolistview;
    @FXML
    private TextArea textareaid;
    @FXML
    private Label deadlinelabelid;
    @FXML
    private BorderPane mainborderpane;
    @FXML
    private ContextMenu listcontextmenu;
    @FXML
    private ToggleButton filtertoggelbutton;

    private FilteredList<Todoitem> filteredList;
    private Predicate<Todoitem> wanttodaysitem;
    private Predicate<Todoitem> wantallitems;

    public void initialize(){
      /*  Todoitem item1 = new Todoitem("send birthday gift", "birthday gift for Ms Arpita",
                LocalDate.of(2020, Month.MARCH,6));
        Todoitem item2 = new Todoitem("doc appointment", "meeat doc will",
                LocalDate.of(2020, Month.APRIL,12));
        Todoitem item3 = new Todoitem("fast", "not forget to fast as it is Shivratri",
                LocalDate.of(2020, Month.FEBRUARY,14));
        Todoitem item4 = new Todoitem("project", "complete the project whic you started",
                LocalDate.of(2020, Month.MARCH,1));

        todoitems = new ArrayList<Todoitem>();
        todoitems.add(item1);
        todoitems.add(item2);
        todoitems.add(item3);
        todoitems.add(item4);

        TodoData.getInstance().setTodoitems(todoitems);*/
      listcontextmenu = new ContextMenu();
      MenuItem deletemenuitem = new MenuItem("Delete");
      deletemenuitem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
              Todoitem item = todolistview.getSelectionModel().getSelectedItem();
              deleteitem(item);
          }
      });
      listcontextmenu.getItems().addAll(deletemenuitem);
        todolistview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Todoitem>() {
            @Override
            public void changed(ObservableValue<? extends Todoitem> observableValue, Todoitem todoitem, Todoitem t1) {
                if (t1!= null){
                    Todoitem item = todolistview.getSelectionModel().getSelectedItem();
                    textareaid.setText(item.getDetails());
                    deadlinelabelid.setText(item.getDeadline().toString());
                }
            }
        });
        wantallitems = new Predicate<Todoitem>() {
            @Override
            public boolean test(Todoitem todoitem) {
                return true;
            }
        };
        wanttodaysitem = new Predicate<Todoitem>() {
            @Override
            public boolean test(Todoitem todoitem) {
                return (todoitem.getDeadline().equals(LocalDate.now()));
            }
        };
        filteredList = new FilteredList<Todoitem>(TodoData.getInstance().getTodoitems(), wantallitems);
        SortedList<Todoitem> sortedList = new SortedList<Todoitem>(filteredList,
                new Comparator<Todoitem>() {
                    @Override
                    public int compare(Todoitem o1, Todoitem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });
//        todolistview.setItems(TodoData.getInstance().getTodoitems());
        todolistview.setItems(sortedList);
        todolistview.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todolistview.getSelectionModel().selectFirst();
        todolistview.setCellFactory(new Callback<ListView<Todoitem>, ListCell<Todoitem>>() {
            @Override
            public ListCell<Todoitem> call(ListView<Todoitem> param) {
                ListCell<Todoitem> cell = new ListCell<Todoitem>(){
                    @Override
                    protected void updateItem(Todoitem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty){
                            setText(null);
                        }else {
                            setText(item.getShortdescription());
                            if (item.getDeadline().equals(LocalDate.now())){
                                setTextFill(Color.RED);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) ->{
                                if(isNowEmpty) {
                                    cell.setContextMenu(null);
                                }else {
                                    cell.setContextMenu(listcontextmenu);
                                }
            });
                return cell;
            }
        });
    }
    @FXML
    public void shownewitemdialoge(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainborderpane.getScene().getWindow());
        dialog.setTitle("Add New Todo Item ");
        dialog.setHeaderText("use this dialoge to add new todoitem ");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoitemdialoge.fxml"));
        try {
//            Parent root = FXMLLoader.load(getClass().getResource("todoitemdialoge.fxml"));

            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't get the dialoge");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            DialogController controller = fxmlLoader.getController();
            Todoitem newitem = controller.procesresults();
//            todolistview.getItems().setAll(TodoData.getInstance().getTodoitems());
            todolistview.getSelectionModel().select(newitem);
            System.out.println("OK Pressed ");
        }else {
            System.out.println("CANCEL Pressed ");
        }
    }
    @FXML
    public void handlekeypressed(KeyEvent keyevent){
        Todoitem selectedItem = todolistview.getSelectionModel().getSelectedItem();
        if (selectedItem != null){
            if (keyevent.getCode().equals(KeyCode.DELETE)){
                deleteitem(selectedItem);
            }
        }
    }
    @FXML
    public void handleclicklistview(){
        Todoitem item = todolistview.getSelectionModel().getSelectedItem();
        textareaid.setText(item.getDetails());
        deadlinelabelid.setText(item.getDeadline().toString());
//        System.out.println("The selection item is " + item);
//        StringBuilder sb =  new StringBuilder(item.getDetails());
//        sb.append("\n\n\n\n");
//        sb.append("Due:: ");
//        sb.append(item.getDeadline().toString());
//        textareaid.setText(sb.toString());
    }
    public void deleteitem(Todoitem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item ");
        alert.setHeaderText("Delete Todo Item " + item.getShortdescription());
        alert.setContentText("Are you sure ? ");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            TodoData.getInstance().deletetodoitem(item);
        }
    }
    public void handlefilterbutton(){
        Todoitem selecteditem = todolistview.getSelectionModel().getSelectedItem();
        if (filtertoggelbutton.isSelected()){
            filteredList.setPredicate(wanttodaysitem);
            if (filteredList.isEmpty()){
                textareaid.clear();
                deadlinelabelid.setText("");
            }else if (filteredList.contains(selecteditem)){
                todolistview.getSelectionModel().select(selecteditem);
            }else {
                todolistview.getSelectionModel().selectFirst();
            }
        }else{
            filteredList.setPredicate(wantallitems);
            todolistview.getSelectionModel().select(selecteditem);
        }
    }
    @FXML
    public void handleexit(){
        Platform.exit();
    }
}
