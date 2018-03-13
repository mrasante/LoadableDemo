/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esri.support.devsummitdemo;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.localserver.LocalGeoprocessingService;
import com.esri.arcgisruntime.localserver.LocalServer;
import com.esri.arcgisruntime.localserver.LocalServerStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingDouble;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingJob;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameter;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameters;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingParameters.ExecutionType;
import com.esri.arcgisruntime.tasks.geoprocessing.GeoprocessingTask;
import java.io.File;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author kwasi asante
 */
public class DemoTaskJob {

    private ListenableFuture<Void> listenableF;
    private LocalGeoprocessingService localGPService;
    private GeoprocessingTask geoprocessingTask;
    private ArcGISMapImageLayer imageLayer;
    private ProgressBar progressIndicator;
    private int counter;


    public void performGeoprocessing(String gpkPath, MapView mapView) {
	ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer("D:\\TechLead\\TA\\Ken\\SDKRT\\geoprocessing\\RasterHillshade.tpk");
	tiledLayer.loadAsync();
	mapView.getMap().getOperationalLayers().add(tiledLayer);
	LocalServer localServer = LocalServer.INSTANCE;
	if (localServer.checkInstallValid()) {
	    listenableF = localServer.startAsync();
	    localServer.addStatusChangedListener(listener -> {

		if (localServer.getStatus() == LocalServerStatus.STARTED) {
		    progressIndicator = DemoDialogCreator.createProgressBar();
		    try {
			String gpkUrl = new File(gpkPath).getAbsolutePath();
			localGPService = new LocalGeoprocessingService(gpkUrl, LocalGeoprocessingService.ServiceType.ASYNCHRONOUS_SUBMIT_WITH_MAP_SERVER_RESULT);
			localGPService.startAsync();
			localGPService.addStatusChangedListener(statusChangedListener -> {
			    if (statusChangedListener.getNewStatus() == LocalServerStatus.STARTED) {

				geoprocessingTask = new GeoprocessingTask(localGPService.getUrl() + "/Contour");
				geoprocessingTask.loadAsync();
				GeoprocessingParameters gpParams = new GeoprocessingParameters(ExecutionType.ASYNCHRONOUS_SUBMIT);
				gpParams.setOutputSpatialReference(mapView.getSpatialReference());
				Map<String, GeoprocessingParameter> geoprocessingInputs = gpParams.getInputs();
				geoprocessingInputs.put("Interval", new GeoprocessingDouble(200));
				GeoprocessingJob geoprocessingJob = geoprocessingTask.createJob(gpParams);
				showNotification("Job Started at " + geoprocessingTask.getUri());
				System.out.println("Job URL: "+geoprocessingTask.getUri());
				counter = 0;
				geoprocessingJob.addJobChangedListener(() -> {
				    DemoDialogCreator.getStatusLabel().setText(geoprocessingJob.getMessages().get(counter).getMessage());
				    counter++;
				});
				geoprocessingJob.addProgressChangedListener(() -> {
				    Platform.runLater(() -> {
					progressIndicator.setProgress(((double) geoprocessingJob.getProgress()) / 100);
					DemoDialogCreator.getProgressLabel().setText(geoprocessingJob.getStatus().name());
				    });
				});
				geoprocessingJob.start();
				geoprocessingJob.addJobDoneListener(() -> {
				    if (geoprocessingJob.getStatus() == Job.Status.SUCCEEDED) {
					imageLayer = new ArcGISMapImageLayer(
						localGPService.getUrl().replace("GPServer", "MapServer/jobs/" + geoprocessingJob.getServerJobId())
					);
					imageLayer.loadAsync();
					imageLayer.addDoneLoadingListener(() -> {
					    mapView.getMap().getOperationalLayers().add(imageLayer);
					    mapView.setViewpointAsync(new Viewpoint(imageLayer.getFullExtent()), 3);
					    System.out.println("Layer SR: "+ imageLayer.getSpatialReference().getWKText());
					    System.out.println("MapView SR: "+ mapView.getSpatialReference().getWKText());
					});
				    }
				});

			    }
			});
		    } catch (Exception fileiO) {
			DemoDialogCreator.createExceptionDialog(fileiO);
		    }
		} else {
		    System.out.println("Status: " + localServer.getStatus().name());
		}
	    });

//	    if (localGPService != null) {
//		localGPService.addStatusChangedListener(statusChangedListener -> {
//		    if (statusChangedListener.getNewStatus() == LocalServerStatus.STARTED) {
//			geoprocessingTask = new GeoprocessingTask(localGPService.getUrl() + "/Contour");
//			geoprocessingTask.loadAsync();
//			contourImageLayer = generateContours(geoprocessingTask);
//			sceneView.getArcGISScene().getOperationalLayers().add(contourImageLayer);
//			sceneView.setViewpointAsync(new Viewpoint(contourImageLayer.getFullExtent()), 3);
//		    }
//		});
//	    }
	}
    }

//    private ArcGISMapImageLayer generateContours(GeoprocessingTask geoprocessingTask) {
//	GeoprocessingParameters gpParams = new GeoprocessingParameters(ExecutionType.ASYNCHRONOUS_SUBMIT);
////	gpParams = geoprocessingTask.createDefaultParametersAsync().;
//	Map<String, GeoprocessingParameter> geoprocessingInputs = gpParams.getInputs();
//	geoprocessingInputs.put("Interval", new GeoprocessingDouble(2));
//	GeoprocessingJob geoprocessingJob = geoprocessingTask.createJob(gpParams);
//	geoprocessingJob.addProgressChangedListener(() -> {
//	    DemoDialogCreator.createProgressBar();
//	});
//
//	geoprocessingJob.start();
//	geoprocessingJob.addJobDoneListener(() -> {
//	    if (geoprocessingJob.getStatus() == Job.Status.SUCCEEDED) {
//		imageLayer = new ArcGISMapImageLayer(
//			localGPService.getUrl().replace("GPServer", "MapServer/jobs/" + geoprocessingJob.getServerJobId())
//		);
//		imageLayer.loadAsync();
//
//	    }
//	});
//	return imageLayer;
//    }
    private void showNotification(String string) {
	Notifications.create()
		.title("Geoprocessing Job Started Successfully....")
		.text(string)
		.position(Pos.BOTTOM_RIGHT)
		.hideAfter(Duration.seconds(15))
		.showInformation();
    }

}
