/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esri.support.devsummitdemo;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.portal.PortalQueryParameters;
import com.esri.arcgisruntime.portal.PortalQueryResultSet;
import com.esri.arcgisruntime.security.UserCredential;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.TextFields;

/**
 *
 * @author kwasi asante
 */
public class DemoControlsCreator {

    private static final Image ZOOM_ICON = new Image(DemoControlsCreator.class.getResourceAsStream("Brush.jpeg"));
    private static final Image LOADER_ICON = new Image(DemoControlsCreator.class.getResourceAsStream("3DAnalyst.png"));
    private static final Image ADD_DATA_ICON = new Image(DemoControlsCreator.class.getResourceAsStream("DataAdd.png"));
    private static final Image SELECTION_ICON = new Image(DemoControlsCreator.class.getResourceAsStream("Sketch.png"));
    private static final Image ROUTE_ICON = new Image(DemoControlsCreator.class.getResourceAsStream("Route.png"));
    private static final Image GEOPROCESSING_ICON = new Image(DemoControlsCreator.class.getResourceAsStream("Geopro.png"));
    private static final String ARCGISONLINE_PORTAL = "https://www.arcgis.com";
    private static File selectedFile;
    private static Stage addDataStage;
    private static Portal portal;
    private static List<PortalItem> listOfFeaturedPortalItems;
    private static List<PortalItem> listOfItems;
    private static ArrayList<String> listofPortalItemNames;
    private static Label mapLabelInfo;
    private static FeatureLayer featureLayer;
    private final static String SLPK_PATH = "D:\\Developer\\Hackathons\\HackTheMap\\charlotte.slpk";
    private static final String ELEVATION_SOURCE_SERVICE = "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";
    private static final String LOCAL_ROUTING =     "C:\\Program Files (x86)\\ArcGIS SDKs\\java10.2.4\\sdk\\samples\\data\\disconnected\\route\\SanFrancisco\\RuntimeSanFrancisco.geodatabase";
    private static final String SCENE_SERVICE = "http://scene.arcgis.com/arcgis/rest/services/Hosted/Buildings_San_Francisco/SceneServer/layers/0";
    private static ArcGISSceneLayer arcgisSceneLayer;
    private static ArrayList<Point> listOfClickedPoints;
    private static Button routeButton;
    private static final String GPK_LOCATION = "D:\\TechLead\\TA\\Ken\\SDKRT\\geoprocessing\\Contour.gpk";

    private static RouteTask routeTask;
    private static Point originPoint;
    private static Stop destination;
    private static RouteParameters defaultRouteParams;
    private static ListenableFuture<RouteResult> routeResult;
    private static Route route;

    public static ToolBar createMapViewToolbar(MapView mapView) {
	ToolBar toolbar = new ToolBar();
	ImageView zoomInView = new ImageView(ZOOM_ICON);
	ImageView addDataView = new ImageView(ADD_DATA_ICON);
	ImageView geoprocessingImage = new ImageView(GEOPROCESSING_ICON);
	zoomInView.setFitHeight(32);
	zoomInView.setFitWidth(32);
	Button zoomButton = new Button("Clear Map", zoomInView);
	Button addDataButton = new Button("Add Data", addDataView);
	Button geoprocessingButton = new Button("Perform Geoprocessing", geoprocessingImage);
	geoprocessingButton.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler->{
	if(eventHandler.getButton() == MouseButton.PRIMARY){
	    new DemoTaskJob().performGeoprocessing(GPK_LOCATION, mapView);
	}
	});
	HBox toolbarBox = new HBox(zoomButton, addDataButton, geoprocessingButton);
	toolbarBox.setSpacing(10);
	toolbar.getItems().add(toolbarBox);

	zoomButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
	    if (event.getButton() == MouseButton.PRIMARY) {
		if (mapView.getMap().getOperationalLayers() != null) {
		    mapView.getMap().getOperationalLayers().clear();
		}
	    }
	});

	addDataButton.setOnAction((event) -> {
	    addDataStage = new Stage();
	    addDataStage.setResizable(false);
	    addDataStage.setTitle("Add Data from Local Device or Remote Resource");
	    addDataStage.setScene(createAddDataStage(mapView));
	    addDataStage.show();

	});
	return toolbar;
    }

    public static ToolBar createGlobeViewToolbar(SceneView sceneView) {
	ToolBar toolbar = new ToolBar();
	ImageView loadIconImage = new ImageView(LOADER_ICON);
	ImageView selectionImage = new ImageView(SELECTION_ICON);
	ImageView routeImage = new ImageView(ROUTE_ICON);
	
	
	loadIconImage.setFitHeight(32);
	loadIconImage.setFitWidth(32);
	Button sceneLoaderButton = new Button("Load Scene Layer", loadIconImage);
	Button selectionButton = new Button("Create Stops", selectionImage);
	
	routeButton = new Button("Perform Route Task", routeImage);
	routeButton.setDisable(true);

	routeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (eventHandler) -> {
	    startRouting(listOfClickedPoints, sceneView);
	});
	HBox toolbarBox = new HBox(sceneLoaderButton, selectionButton, routeButton);
	toolbarBox.setSpacing(10);
	toolbar.getItems().add(toolbarBox);

	sceneLoaderButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (eventHandler) -> {
	    if (eventHandler.getButton() == MouseButton.PRIMARY) {
		loadSceneLayer(sceneView);
	    }
	});

	selectionButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (eventHandler) -> {
	    activateStopsCreation(sceneView);
	});

	return toolbar;
    }

    private static Scene createAddDataStage(MapView mapview) {
	HBox horiBox = new HBox(5);
	horiBox.setSpacing(10);
	horiBox.setAlignment(Pos.CENTER);
	Button browseButton = new Button("Browse for data ...");
	Label browseLabel = new Label();
	browseLabel.setVisible(false);
	Button showDataButton = new Button("Show Data on Map");
	showDataButton.setVisible(false);
	horiBox.getChildren().addAll(browseButton, browseLabel);
	VBox verticalBox = new VBox(horiBox, showDataButton);
	verticalBox.setSpacing(10);
	verticalBox.setAlignment(Pos.CENTER);

	HBox loginBox = new HBox(10);
	loginBox.setAlignment(Pos.CENTER);
	HBox usernameHori = new HBox(10);
	TextField usernameField = new TextField();
	usernameField.setPromptText("Portal Username");
	PasswordField passwordField = new PasswordField();
	passwordField.setPromptText("Portal Password");
	Button loginButton = new Button("Authenticate");
	usernameHori.getChildren().addAll(usernameField, passwordField, loginButton);
	loginBox.getChildren().add(usernameHori);

	ListView<String> itemDisplayListView = new ListView<>();
//	  itemDisplayListView
	verticalBox.getChildren().add(loginBox);

	Scene localScene = new Scene(verticalBox, 550, 150);
	loginButton.setOnAction((loginAction) -> {
	    UserCredential userCredential = new UserCredential(
		    usernameField.getText().trim(),
		    passwordField.getText().trim());
	    portal = new Portal(ARCGISONLINE_PORTAL, true);
	    portal.setCredential(userCredential);
	    portal.loadAsync();
	    portal.addDoneLoadingListener(() -> {

		if (portal.getLoadStatus() == LoadStatus.LOADED) {
		    Notifications.create()
			    .title("Login Successfull")
			    .text("You have successfully logged in to " + portal.getPortalInfo().getUrl() + " as " + portal.getUser().getFullName())
			    .position(Pos.CENTER)
			    .hideAfter(Duration.seconds(5))
			    .showInformation();
		    loginBox.getChildren().clear();
		    TextField searchTextField = TextFields.createClearableTextField();
		    Label resultsLabel = new Label();
		    searchTextField.setPromptText("Keyword to search");
		    loginBox.getChildren().add(searchTextField);
		    searchTextField.setAlignment(Pos.CENTER);
		    searchTextField.setOnAction(eventListener -> {
			String searchText = searchTextField.getText();
			try {
			    itemDisplayListView.getItems().clear();
			    if (!searchText.isEmpty()) {
				PortalQueryParameters portalQueryParams = new PortalQueryParameters();
				portalQueryParams.setLimit(150);
				portalQueryParams.setQuery(PortalItem.Type.WEBMAP, null, searchText);
				PortalQueryResultSet<PortalItem> portalQueryResults = portal.findItemsAsync(portalQueryParams).get();
				listOfItems = portalQueryResults.getResults();
				resultsLabel.setText(listOfItems.size() + " web maps found for " + searchText + " in " + portal.getPortalInfo().getUrl());
				listofPortalItemNames = new ArrayList<>();
				listOfItems.stream().forEach(consumer -> {
				    listofPortalItemNames.add(consumer.getTitle());
				});
				itemDisplayListView.setItems(FXCollections.observableList(listofPortalItemNames));
				if (!verticalBox.getChildren().contains(itemDisplayListView)) {
				    verticalBox.getChildren().addAll(itemDisplayListView, resultsLabel);
				}
				verticalBox.setAlignment(Pos.CENTER);
				addDataStage.setHeight(600);
			    }

			} catch (ExecutionException | InterruptedException intExecu) {
				DemoDialogCreator.createExceptionDialog(intExecu);
			}
		    });

		} else {
		    DemoDialogCreator.createExceptionDialog(portal.getLoadError());
		}

	    });

	});

	itemDisplayListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	    PortalItem portalItem = listOfItems.get(listofPortalItemNames.indexOf(observable.getValue()));
	    mapview.setMap(new ArcGISMap(portalItem));
	    mapLabelInfo.setText(portalItem.getTitle());
	    mapview.setViewpointGeometryAsync(portalItem.getExtent(), 100);
	});

	browseButton.setOnAction((actionEvent) -> {
	    FileChooser filechooser = new FileChooser();
	    filechooser.setTitle("Choose Data...");
	    String dirLocation = "D:\\GIS\\ArcGIS\\Data";//DemoControlsCreator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	    System.out.println(dirLocation);
	    filechooser.setInitialDirectory(new File(dirLocation));
	    filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Supported file types", "*.shp", "*.geodatabase", "*.mmpk"));
	    selectedFile = filechooser.showOpenDialog(localScene.getWindow());
	    if (selectedFile != null) {
		browseLabel.setText("Selected: " + selectedFile.getName());
	    }
	    browseLabel.setVisible(true);
	    showDataButton.setVisible(true);
	});

	showDataButton.setOnAction((actionValue) -> {
	    if (selectedFile != null) {
		FeatureLayer layer = createLayer(selectedFile);
		if (layer.getLoadStatus() == LoadStatus.LOADED) {
		    mapview.getMap().getOperationalLayers().add(layer);
		    mapview.setViewpointGeometryAsync(layer.getFullExtent(), 100);
		} else {
		    DemoDialogCreator.createExceptionDialog(layer.getLoadError());
		}

	    }
	});

	return localScene;
    }

    private static FeatureLayer createLayer(File selectedFile) {
	String switchString = getFileExtension(selectedFile);
	switch (switchString) {
	    case ".shp":
		ShapefileFeatureTable shapefileTable = new ShapefileFeatureTable(selectedFile.getAbsolutePath());
//		shapefileTable.loadAsync();
		featureLayer = new FeatureLayer(shapefileTable);
		featureLayer.loadAsync();
		
		break;
	    case ".geodatabase":
		Geodatabase geodatabase = new Geodatabase(selectedFile.getAbsolutePath());
		geodatabase.loadAsync();

		geodatabase.addDoneLoadingListener(() -> {
		    if (geodatabase.getLoadStatus() == LoadStatus.LOADED) {
			List<GeodatabaseFeatureTable> listOfFeatureTables = geodatabase.getGeodatabaseFeatureTables();
			featureLayer = new FeatureLayer(listOfFeatureTables.get(0));
			featureLayer.loadAsync();
		    } else if (geodatabase.getLoadError() != null) {
			featureLayer.retryLoadAsync();
			System.out.println("Geodatabase failed to load because: " + geodatabase.getLoadError().getAdditionalMessage());
			DemoDialogCreator.createExceptionDialog(featureLayer.getLoadError());
		    }
		});
		break;
	}
	return featureLayer;
    }

    public static VBox createVerticalBox(MapView mapView) {
	VBox cornerInfoBox = new VBox();
	cornerInfoBox.setAlignment(Pos.TOP_CENTER);
	Background background = new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY));
	cornerInfoBox.setBackground(background);
	mapLabelInfo = new Label();
	mapLabelInfo.setFont(new Font(44));
	mapLabelInfo.setOpacity(1);
	cornerInfoBox.getChildren().add(mapLabelInfo);
	cornerInfoBox.setOpacity(0.9);

	return cornerInfoBox;
    }

    private static String getFileExtension(File selectedFile) {
	String fileName = selectedFile.getName();
	if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
	    return fileName.substring(fileName.lastIndexOf("."));
	} else {
	    return "";
	}

    }

    private static void loadSceneLayer(SceneView globeView) {
	ArcGISTiledElevationSource arcGISTiledElevationSource = new ArcGISTiledElevationSource(ELEVATION_SOURCE_SERVICE);
	arcGISTiledElevationSource.loadAsync();
	arcGISTiledElevationSource.loadAsync();
	arcGISTiledElevationSource.addDoneLoadingListener(() -> {
	    if(arcGISTiledElevationSource.getLoadStatus() == LoadStatus.LOADED){
	    globeView.getArcGISScene().getBaseSurface().getElevationSources().add(arcGISTiledElevationSource);
	    globeView.getArcGISScene().getBaseSurface().setElevationExaggeration(0.1f);
	    }
	});

	arcgisSceneLayer = new ArcGISSceneLayer(SLPK_PATH);
	arcgisSceneLayer.loadAsync();

	arcgisSceneLayer.addDoneLoadingListener(() -> {
	    globeView.getArcGISScene().getOperationalLayers().add(arcgisSceneLayer);
	    Point camLocation = new Point(-80.837890, 35.212124, 250, globeView.getSpatialReference());
	    Camera viewPointCam = new Camera(camLocation, 4, 70, 0);
	    globeView.setViewpointAsync(new Viewpoint(arcgisSceneLayer.getFullExtent(), viewPointCam), 3);
	});

	globeView.addViewpointChangedListener((listener) -> {
	    Camera currentCam = globeView.getCurrentViewpointCamera();
	    System.out.println("Pitch: " + currentCam.getPitch() + " Roll: " + currentCam.getRoll() + " Heading: " + currentCam.getHeading());
	});
    }

    private static void activateStopsCreation(SceneView sceneView) {
	//zoom to san francisco
	ArcGISSceneLayer sanFranSceneLayer = new ArcGISSceneLayer(SCENE_SERVICE);
	 sanFranSceneLayer.loadAsync();
	 sanFranSceneLayer.addDoneLoadingListener(()->{
	if(sanFranSceneLayer.getLoadStatus() == LoadStatus.LOADED){
	     sceneView.getArcGISScene().getOperationalLayers().add(sanFranSceneLayer);
	 Camera viewpointCam = new Camera(new Point(-122.39741217227888, 37.76703449153985), 400, 2, 70, 0);
	 sceneView.setViewpointCameraAsync(viewpointCam, 3);
	 
	}else
	    DemoDialogCreator.createExceptionDialog(sanFranSceneLayer.getLoadError());
	 });
	listOfClickedPoints = new ArrayList<>();
	sceneView.addEventHandler(MouseEvent.MOUSE_CLICKED, (eventHandler) -> {
	    Point2D point = new Point2D(eventHandler.getX(), eventHandler.getY());
	    ListenableFuture<Point> pointClickedFuture = sceneView.screenToLocationAsync(point);
	    
	    //logic to clear list; just for demo
	    if(listOfClickedPoints.size() > 1){
		listOfClickedPoints.clear();
	    }
	    
	    pointClickedFuture.addDoneListener(() -> {
		try {
		    Point pointClicked = pointClickedFuture.get();
		    listOfClickedPoints.add(pointClicked);
		    if (listOfClickedPoints.size() > 1) {
			routeButton.setDisable(false);
		    }
		} catch (ExecutionException | InterruptedException exInt) {
		    DemoDialogCreator.createExceptionDialog(exInt);
		}
	    });

	});

    }

    private static void startRouting(ArrayList<Point> listOfPoints, SceneView sceneView) {
	routeTask = new RouteTask(LOCAL_ROUTING, "Streets_ND");
	routeTask.loadAsync();

	DemoDialogCreator.createRoutingProgressBar();
	ArrayList<Stop> routeStops = new ArrayList<>();
	originPoint = (Point) GeometryEngine.project(listOfPoints.get(0), SpatialReferences.getWebMercator());//new Point(-80.837890, 35.212124, SpatialReferences.getWgs84());
	Stop origin = new Stop(originPoint);
	origin.setType(Stop.Type.STOP);
	routeStops.add(origin);

	destination = new Stop((Point) GeometryEngine.project(listOfPoints.get(1), SpatialReferences.getWebMercator()));
	destination.setType(Stop.Type.STOP);
	routeStops.add(destination);

	routeTask.addDoneLoadingListener(() -> {
	    try {
		if(routeTask.getLoadStatus() == LoadStatus.LOADED){
		defaultRouteParams = routeTask.createDefaultParametersAsync().get();
		defaultRouteParams.setOutputSpatialReference(SpatialReferences.getWgs84());
		defaultRouteParams.setReturnDirections(true);
		defaultRouteParams.setReturnRoutes(true);
		defaultRouteParams.setReturnStops(true);
		defaultRouteParams.setStops(routeStops);

		//long task 
		Platform.runLater(() -> {
		    performAsyncRoutingTask(sceneView);
		});
		}else 
		    DemoDialogCreator.createExceptionDialog(routeTask.getLoadError());
	    } catch (ExecutionException | InterruptedException inEx) {
		System.out.println(inEx.getCause());
	    }

	});

    }

    private static void performAsyncRoutingTask(SceneView sceneView) {
	routeResult = routeTask.solveRouteAsync(defaultRouteParams);
	routeResult.addDoneListener(() -> {
	    try {
		route = routeResult.get().getRoutes().get(0);
		final Polyline routeLines = route.getRouteGeometry();

		//construct route line and add to graphicsoverlay
		Graphic routeGraphic = new Graphic(routeLines);
		Symbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xEA5721FF, 4);//0xF0F8FA7F
		routeGraphic.setSymbol(routeSymbol);

		//construct origin and destination and add to layer
		PictureMarkerSymbol originMarkerSymbol = new PictureMarkerSymbol("http://static.arcgis.com/images/Symbols/Shapes/GreenPin2LargeB.png");
		originMarkerSymbol.setWidth(30);
		originMarkerSymbol.setHeight(35);
		originMarkerSymbol.loadAsync();
		Graphic originGraphic = new Graphic(originPoint, originMarkerSymbol);

		PictureMarkerSymbol destinationMarkerSym = new PictureMarkerSymbol("http://static.arcgis.com/images/Symbols/Basic/CheckeredFlag.png");
		destinationMarkerSym.setHeight(30);
		destinationMarkerSym.setWidth(20);
		destinationMarkerSym.loadAsync();
		Graphic destinationGraphic = new Graphic(destination.getGeometry(), destinationMarkerSym);

		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);

		//check for the presence of older graphics, clear them and add new ones
		if (graphicsOverlay.getGraphics() != null) {
		    graphicsOverlay.getGraphics().clear();
		}
		graphicsOverlay.getGraphics().add(routeGraphic);
		graphicsOverlay.getGraphics().add(destinationGraphic);
		graphicsOverlay.getGraphics().add(originGraphic);
		sceneView.getGraphicsOverlays().add(graphicsOverlay);
		DemoDialogCreator.close();
		sceneView.setViewpointAsync(new Viewpoint(routeLines.getExtent()), 5);
	    } catch (Exception e) {
		DemoDialogCreator.createExceptionDialog(e);
	    }
	});

    }
}
