package datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "Todolistitem.txt";
    private ObservableList<Todoitem> todoitems;
    private DateTimeFormatter formatter;
    public static TodoData getInstance(){
        return instance;
    }
    private TodoData(){
        formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
    }

    public ObservableList<Todoitem> getTodoitems() {
        return todoitems;
    }
    public void addtodoitem(Todoitem item){
        todoitems.add(item);
    }

//    public void setTodoitems(List<Todoitem> todoitems) {
//        this.todoitems = todoitems;
//    }
    public void loadtodoitems() throws IOException{
        todoitems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);
        String input;

        try {
            while ((input = br.readLine()) != null){
                String[] itempieces = input.split("\t");
                String shortdescription = itempieces[0];
                String datestring = itempieces[1];
                String details = itempieces[2];
                LocalDate date = LocalDate.parse(datestring, formatter);
                Todoitem todoitem = new Todoitem(shortdescription, details,date);
                todoitems.add(todoitem);
            }
        }finally {
            if (br!=null){
                br.close();
            }
        }
    }
    public void storetodoitems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try{
            Iterator<Todoitem> iter = todoitems.iterator();
            while (iter.hasNext()){
                Todoitem item = iter.next();
                bw.write(String.format("%s\t%S\t%s",
                        item.getShortdescription(),
                        item.getDeadline().format(formatter),
                        item.getDetails()));
                bw.newLine();
            }
        }finally {
            if (bw!= null){
                bw.close();
            }
        }
    }
    public void deletetodoitem(Todoitem item){
        todoitems.remove(item);
    }
}
