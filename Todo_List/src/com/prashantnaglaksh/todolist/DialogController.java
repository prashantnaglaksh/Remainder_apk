package com.prashantnaglaksh.todolist;

import datamodel.TodoData;
import datamodel.Todoitem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortdescriptionfield;
    @FXML
    private TextArea detailsarea;
    @FXML
    private DatePicker deadlinepicker;
    public Todoitem procesresults(){
        String shortdescription = shortdescriptionfield.getText().trim();
        String details = detailsarea.getText().trim();
        LocalDate deadlinevalue = deadlinepicker.getValue();
        Todoitem newitem = new Todoitem(shortdescription, details, deadlinevalue);
        TodoData.getInstance().addtodoitem(newitem);
        return newitem;
    }
}
