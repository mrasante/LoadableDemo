/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esri.support.devsummitdemo;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import java.awt.BorderLayout;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author kwasi asante
 */
public class DevSummitDemo extends Application {

    private MapView mapView;
    private SceneView sceneView;
    
    private static final String DEMO_APP_TITLE = "Dev Summit Demo App";
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
	mapView = createArcGISMap();
	sceneView = createArcGISSceneView();
	VBox infoBox = DemoControlsCreator.createVerticalBox(mapView);
	infoBox.setTranslateY(1120);
	StackPane stackPane = new StackPane(mapView, DemoControlsCreator.createMapViewToolbar(mapView),
	infoBox);
	stackPane.setAlignment(Pos.TOP_LEFT);
	
	StackPane globeStackPane = new StackPane(sceneView, DemoControlsCreator.createGlobeViewToolbar(sceneView));
	globeStackPane.setAlignment(Pos.TOP_LEFT);
	TabPane tabPane = new TabPane();
	Tab mapViewTab = new Tab("Map View Demo", stackPane);
	Tab globeViewTab = new Tab("Globe View Demo", globeStackPane);
	globeViewTab.setClosable(false);
	mapViewTab.setClosable(false);
	tabPane.getTabs().addAll(mapViewTab, globeViewTab);
	mapViewTab.setContent(stackPane);
	mainScene = new Scene(tabPane);
	primaryStage.setScene(mainScene);
	primaryStage.setTitle(DEMO_APP_TITLE);
	primaryStage.setMaximized(true);
	primaryStage.show();
    }

    /**
     * 
     * @param args 
     */
    public static void main(String... args) {
	System.out.println("ArcGIS Runtime Version: "+ ArcGISRuntimeEnvironment.getAPIVersion());
	launch(args);
    }

    private MapView createArcGISMap() {
	MapView mapView = new MapView();
	ArcGISMap arcgisMap = new ArcGISMap(Basemap.createImageryWithLabelsVector());
	arcgisMap.loadAsync();
	mapView.setMap(arcgisMap);
	mapView.setViewpointGeometryAsync(new Envelope(-29471.5, 623810.76, -18385.24, 616541.55, SpatialReferences.getWebMercator()), 10);
	mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED, (eventHandler)->{
		if(eventHandler.getButton() == MouseButton.PRIMARY){
		    mainScene.setCursor(javafx.scene.Cursor.CLOSED_HAND);
		}else 
		    mainScene.setCursor(javafx.scene.Cursor.DEFAULT);
	});
	return mapView;
    }

    @Override
    public void stop() {
	if (mapView != null) {
	    mapView.dispose();
	}
    }

    private SceneView createArcGISSceneView() {
	SceneView localSceneView = new SceneView();
	ArcGISScene arcgisScene = new ArcGISScene(Basemap.Type.TOPOGRAPHIC);
	localSceneView.setArcGISScene(arcgisScene);
	return localSceneView;
    }

}
