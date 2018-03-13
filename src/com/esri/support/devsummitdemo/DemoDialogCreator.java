/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esri.support.devsummitdemo;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author kwasi asante
 */
public class DemoDialogCreator {

    private static Dialog<ButtonType> dialog;
    private static Stage stage;
    private static Label statusLabel;
    private static Label statusMessage;

    public static Dialog<ButtonType> createExceptionDialog(Throwable th) {
	Dialog<ButtonType> dialog = new Dialog<ButtonType>();

	dialog.setTitle("ArcGISRuntime Exception");

	final DialogPane dialogPane = dialog.getDialogPane();
	dialogPane.setContentText("Details of the problem:");
	dialogPane.getButtonTypes().addAll(ButtonType.OK);
	dialogPane.setContentText(th.getMessage());
	dialog.initModality(Modality.APPLICATION_MODAL);

	Label label = new Label("Exception stacktrace:");
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	th.printStackTrace(pw);
	pw.close();

	TextArea textArea = new TextArea(sw.toString());
	textArea.setEditable(false);
	textArea.setWrapText(true);

	textArea.setMaxWidth(Double.MAX_VALUE);
	textArea.setMaxHeight(Double.MAX_VALUE);
	GridPane.setVgrow(textArea, Priority.ALWAYS);
	GridPane.setHgrow(textArea, Priority.ALWAYS);

	GridPane root = new GridPane();
	root.setVisible(false);
	root.setMaxWidth(Double.MAX_VALUE);
	root.add(label, 0, 0);
	root.add(textArea, 0, 1);
	dialogPane.setExpandableContent(root);
	dialog.showAndWait()
		.filter(response -> response == ButtonType.OK)
		.ifPresent(response -> System.out.println("The exception was approved"));
	return dialog;
    }

    public static ProgressBar createProgressBar() {
	StackPane group = new StackPane();
//	group.setLayoutX(150);
//	group.setLayoutY(10);
	ProgressBar progressIndicator = new ProgressBar();
//	progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
	progressIndicator.setPrefWidth(300);
	statusLabel = new Label("Job Status: ");
	statusMessage = new Label("");
	VBox layoutBox = new VBox();
	layoutBox.setAlignment(Pos.CENTER);
	layoutBox.getChildren().addAll(progressIndicator, statusLabel, statusMessage);
	group.getChildren().add(layoutBox);
	stage = new Stage();
	stage.setResizable(false);
	stage.setTitle("Processing....");
	stage.setScene(new Scene(group));
	stage.show();
	return progressIndicator;
    }

    public static void close() {
	stage.close();
    }

    public static Label getProgressLabel(){
//    if(statusLabel != null){
//	return  statusLabel;
//    }
    return statusLabel;
    }
    
    public static Label getStatusLabel(){
    return statusMessage;
    }

  static ProgressBar createRoutingProgressBar() {
	StackPane group = new StackPane();
	ProgressBar progressIndicator = new ProgressBar();
	progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
	progressIndicator.setPrefWidth(300);
	statusLabel = new Label("Calculating Routes.... Please wait ");
	
	VBox layoutBox = new VBox();
	layoutBox.setAlignment(Pos.CENTER);
	layoutBox.getChildren().addAll(progressIndicator, statusLabel);
	group.getChildren().add(layoutBox);
	stage = new Stage();
	stage.setResizable(false);
	stage.setTitle("Processing....");
	stage.setScene(new Scene(group));
	stage.show();
	return progressIndicator;
    }
}
