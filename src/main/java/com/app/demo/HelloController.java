package com.app.demo;


import com.dlsc.formsfx.model.structure.Field;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.IOException;
import java.util.function.ObjDoubleConsumer;

public class HelloController {

    public Tooltip helpToolTip;
    public PieChart chart;
    public Label placeHolderLabel;
    private SceneSwitcher switcher;


    @FXML
    private Button helpButton;

    public void setSceneSwitcher(SceneSwitcher switcher) {
        this.switcher = switcher;
    }
    @FXML
    public void initialize() {

        // Create a fade transition for the help tooltip
        helpToolTip.setShowDelay(Duration.seconds(0.5));

        setChartData();
    }
    private void setChartData(){
        double categoryTotal = 0;
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        for (Transaction t : TransactionDAO.getTransactions()) {
            if (t.getIncomeExpense().equals("Expense")) {
                if(chartData.stream().anyMatch(data -> data.getName().equals(t.getCategory().getName()))){
                    for (PieChart.Data data : chartData) {
                        data.getNode();
                        if(data.getName().equals(t.getCategory().getName())){
                            data.setPieValue(data.getPieValue() + t.getAmount());
                            categoryTotal += t.getAmount();
                        }
                    }
                } else {
                    chartData.add(new PieChart.Data(t.getCategory().getName(), t.getAmount()));
                    categoryTotal += t.getAmount();
                }
            }


        }
        chart.setData(chartData);
        placeHolderLabel.setVisible(chartData.isEmpty());
        chartData.forEach(data -> {

            // Add mouse entered event handler
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                // Calculate the midpoint angle of the slice
                double startAngle = getStartAngle(data);
                double angle = startAngle + (data.getPieValue() / getTotalValue(chartData)) * 180;

                // Convert angle to radians
                double radians = Math.toRadians(angle);

                // Define the translation distance
                double translateDistance = 35;

                // Calculate the x and y offsets
                double offsetX = translateDistance * Math.cos(radians);
                double offsetY = translateDistance * Math.sin(radians);

                // Create and play the translate transition
                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), data.getNode());
                tt.setByX(offsetX);
                tt.setByY(offsetY);
                tt.setAutoReverse(true);
                tt.setCycleCount(2);
                tt.play();
            });

            // Add mouse exited event handler to reset position
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                // Create and play the translate transition to move back
                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), data.getNode());
                tt.setToX(0);
                tt.setToY(0);
                tt.play();
            });
        });



    }
    private double getStartAngle(PieChart.Data data){
        double total = getTotalValue(chart.getData());
        double startAngle = chart.getStartAngle();
        for(PieChart.Data d : chart.getData()){
            if(d == data){
                break;
            }
            startAngle += (d.getPieValue() / total) * 360;
        }
        return startAngle;
    }
    private double getTotalValue(ObservableList<PieChart.Data> data){
        return data.stream().mapToDouble(PieChart.Data::getPieValue).sum();

    }



    public void onTransactionButtonClick(ActionEvent actionEvent) {
        try{
            switcher.switchToPreloaded("transactions");
        } catch (Exception e) {
            SceneSwitcher s = new SceneSwitcher(HelloApplication.primaryStage);
            s.preloadScene("transactions", "/com/app/demo/transactions-page.fxml");
            s.switchToPreloaded("transactions");
        }
    }
    public void onReportButtonClick(ActionEvent actionEvent) {
        try{
            switcher.switchToPreloaded("reports");
        } catch (Exception e) {
            SceneSwitcher s = new SceneSwitcher(HelloApplication.primaryStage);
            s.preloadScene("reports", "/com/app/demo/reports-page.fxml");
            s.switchToPreloaded("reports");
        }
    }
    @FXML
    private void onHelpButtonClick(ActionEvent actionEvent) {
        try{
            switcher.switchToPreloaded("help");
        } catch (Exception e) {
            SceneSwitcher s = new SceneSwitcher(HelloApplication.primaryStage);
            s.preloadScene("help", "/com/app/demo/help-view.fxml");
            s.switchToPreloaded("help");
        }
    }
}