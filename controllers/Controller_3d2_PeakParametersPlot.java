package controllers;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CalculationTaskSave;
import model.ContinueData;
import model.Group;
import model.IntervalPeak;
import model.IntervalPeaks;
import model.PackageData;
import model.Peak;
import model.TimeSpeed;
import model.XYCircleAnnotation;

public class Controller_3d2_PeakParametersPlot implements Initializable {
	private PackageData main_package;
	private double lowerBoundDomain;
    private double upperBoundDomain;
	private double fps_val;
	private double pixel_val;
	private double average_value;
	private double upper_limit;
	private static Group currentGroup;
	private JFreeChart currentChart;
	private Peak currentPeak;
	private List<IntervalMarker> intervalsList;
	private List<Integer> maximum_list;
	private List<Integer> minimum_list;
	private List<Integer> first_points;
	private List<Integer> fifth_points;
	private IntervalPeaks intervalPeaks;
	private boolean ask_saved;
	//	private ToggleGroup toggleGroup = new ToggleGroup();
	private int start;
	private int stop;
	private int step = 1;
	private int global_min;
	private List<TimeSpeed> timespeedlist;
	@FXML
    private Button cmdBack;

    @FXML
    private Button cmdNext;
    
	@FXML
	private CheckBox checkSeconds;
	
    @FXML
    private TableView<Peak> timeTableView;

    @FXML
    private TableColumn<Peak, String> posCol;

    @FXML
    private TableColumn<Peak, Double> tcrCol;

    @FXML
    private TableColumn<Peak, Double> tcCol;
    
    @FXML
    private TableColumn<Peak, Double> trCol;

    @FXML
    private TableColumn<Peak, Double> tc_vmcCol;

    @FXML
    private TableColumn<Peak, Double> tc_vmc_minCol;

    @FXML
    private TableColumn<Peak, Double> tr_vmrCol;

    @FXML
    private TableColumn<Peak, Double> tr_vmr_bCol;

    @FXML
    private TableColumn<Peak, Double> t_vmc_vmrCol;
    
    @FXML
    private TableView<Peak> speedTableView;

    @FXML
    private TableColumn<Peak, String> speedPosCol;

    @FXML
    private TableColumn<Peak, Double> speedVMCCol;

    @FXML
    private TableColumn<Peak, Double> speedVMRCol;

    @FXML
    private TableColumn<Peak, Double> speed_DVMCVMRCol;

//    @FXML
//    private TableColumn<Peak, Double> speedVMINCol;
    
    @FXML
    private TableView<Peak> areaTableView;
    
    @FXML
    private TableColumn<Peak, String> areaPosCol;

    @FXML
    private TableColumn<Peak, Double> speedAREATCol;

    @FXML
    private TableColumn<Peak, Double> speedAREACCol;

//    @FXML
//    private TableColumn<Peak, Double> speedAREARCol;
    
    @FXML
    private SwingNode swgNode;
    
    @FXML
    private RadioButton timeRadio, speedRadio, areaRadio;

    private ToggleGroup radioGroup = new ToggleGroup();
    
    @FXML
    void handleMenuNewImage(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
		FileReader.chooseSourceDirectory(primaryStage, url, main_package);
    }
    
    @FXML
    void handleMenuNewVideo(ActionEvent event) throws IOException{
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
		URL url = getClass().getResource("FXML_2b_ImagesNew.fxml");
    	FileReader.chooseVideoFiles(primaryStage, url, main_package);
    }

    @FXML
    void handleClose(ActionEvent event){
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
    	primaryStage.close();
    }
    
    @FXML
    void handleReinitialize(ActionEvent event) throws IOException, ClassNotFoundException{
    	Stage primaryStage = (Stage) cmdBack.getScene().getWindow();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_1_InitialScreen.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
		((Controller_1_InitialScreen)fxmlloader.getController()).setContext(new PackageData(main_package.isLoad_preferences()));
		primaryStage.setTitle("ContractionWave");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }
    
    @FXML
    void handleAbout(ActionEvent event) throws IOException{
    	Stage stage = new Stage();
    	Parent root = FXMLLoader.load(getClass().getResource("FXML_About.fxml"));
    	stage.setScene(new Scene(root));
    	stage.setTitle("ContractionWave");
		stage.initModality(Modality.APPLICATION_MODAL);
		//stage.initOwner(((Node)event.getSource()).getScene().getWindow());
    	stage.show();
    }

    @FXML
    void handleExportTSV(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.tsv");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
		writeTSV(file);
		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportTXT(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.txt");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
		writeTSV(file);
		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    public void writeTSV(File file) throws Exception {
	    Writer writer = null;
	    try {
	        writer = new BufferedWriter(new FileWriter(file));
	        String time_str = "Time (s)";
			if (checkSeconds.isSelected() == false) {
				time_str = "Time (ms)";
			}
	        
	        String text2 = time_str+"\tSpeed (\u00B5/s)\n";
	        writer.write(text2);
	        
//	        for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
	        
//		    for (int i = start; i < stop; i++) {
		    for (int i = start; i < stop; i++) {

				double average = currentGroup.getMagnitudeListValue(i);
				writer.write(String.valueOf(i / fps_val));
				writer.write(",");
//				writer.write(String.valueOf(average * fps_val * pixel_val));
				writer.write(String.valueOf((average * fps_val * pixel_val) - average_value));
				writer.write("\n");
			}	        
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    finally {
	        writer.flush();
	        writer.close();
	    } 
	}
    
    @FXML
    void handleExportXLS(ActionEvent event) throws IOException{
    	Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("time_speed");
		Row row = spreadsheet.createRow(0);
		
        String time_str = "Time (s)";
		if (checkSeconds.isSelected() == false) {
			time_str = "Time (ms)";
		}
		row.createCell(0).setCellValue(time_str);
		row.createCell(1).setCellValue("Speed (\u00B5/s)");
		
//		for (int i = 0; i < currentGroup.getMagnitudeSize(); i++) {
		for (int i = start; i < stop; i++) {
			double average = currentGroup.getMagnitudeListValue(i);
			row = spreadsheet.createRow(i + 1);
			
			row.createCell(0).setCellValue(i / fps_val);
//			row.createCell(1).setCellValue(average * fps_val * pixel_val);
			row.createCell(1).setCellValue((average * fps_val * pixel_val) - average_value);
		}
		spreadsheet.autoSizeColumn(0);
		spreadsheet.autoSizeColumn(1);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("time-speed.xls");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
		FileOutputStream fileOut = new FileOutputStream(file);
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
		
		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportJPEG(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.jpg");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        ChartPanel panel = (ChartPanel) swgNode.getContent();
        OutputStream out = new FileOutputStream(file);
        ChartUtils.writeChartAsJPEG(out,
                currentChart,
                panel.getWidth(),
                panel.getHeight());
		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportPNG(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.png");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        ChartPanel panel = (ChartPanel) swgNode.getContent();
        OutputStream out = new FileOutputStream(file);
        ChartUtils.writeChartAsPNG(out,
                currentChart,
                panel.getWidth(),
                panel.getHeight());
		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportTIFF(ActionEvent event) throws Exception{
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setInitialFileName("time-speed.tiff");
        Stage primaryStage;
    	primaryStage = (Stage) cmdNext.getScene().getWindow();
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        ChartPanel panel = (ChartPanel) swgNode.getContent();
        JFreeChart chart = panel.getChart();
        BufferedImage bImage = chart.createBufferedImage(panel.getWidth(), panel.getHeight());
        
        Mat imgLayer = Java2DFrameUtils.toMat(bImage);
        MatVector channels = new MatVector();
        Mat imgLayerRGB = new Mat(bImage.getHeight(), bImage.getWidth(), org.bytedeco.javacpp.opencv_core.CV_8UC3);
        org.bytedeco.javacpp.opencv_core.split(imgLayer, channels);
        Mat blueCh = channels.get(1);
        Mat greenCh = channels.get(2);
        Mat redCh = channels.get(3);
        MatVector channels2 = new MatVector(3);
        channels2.put(0, redCh);
        channels2.put(1, greenCh);
        channels2.put(2, blueCh);
        org.bytedeco.javacpp.opencv_core.merge(channels2, imgLayerRGB);
		imwrite(file.getCanonicalPath(), imgLayerRGB);
		JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportTableXLS(ActionEvent event) throws Exception{
    	if (radioGroup.getSelectedToggle().getUserData().equals("time") == true) {
    		exportTable(timeTableView,0,"time-table.xls"); 
    	} else if (radioGroup.getSelectedToggle().getUserData().equals("speed") == true) {
    		exportTable(speedTableView,0,"speed-table.xls");
    	} else {
    		exportTable(areaTableView,0,"area-table.xls");
    	}
    	JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportAllTableXLS(ActionEvent event) throws Exception {
    	exportTables("all-table.xls");
    	JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportTableTSV(ActionEvent event) throws Exception{
    	if (radioGroup.getSelectedToggle().getUserData().equals("time") == true) {
    		exportTable(timeTableView,1,"time-table.tsv"); 
    	} else if (radioGroup.getSelectedToggle().getUserData().equals("speed") == true) {
    		exportTable(speedTableView,1,"speed-table.tsv");
    	} else {
    		exportTable(areaTableView,1,"area-table.tsv");
    	}
    	JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleExportTableTXT(ActionEvent event) throws Exception{
    	if (radioGroup.getSelectedToggle().getUserData().equals("time") == true) {
    		exportTable(timeTableView,1,"time-table.txt"); 
    	} else if (radioGroup.getSelectedToggle().getUserData().equals("speed") == true) {
    		exportTable(speedTableView,1,"speed-table.txt");
    	} else {
    		exportTable(areaTableView,1,"area-table.txt");
    	}
    	JOptionPane.showMessageDialog(null, "File was saved successfully.");
    }
    
    @FXML
    void handleShowAnnotations(ActionEvent event) {
       	XYPlot plot = currentChart.getXYPlot();
    	XYDataset dataset = plot.getDataset();
    	int input = JOptionPane.showConfirmDialog(null, "Hide Annotations?");
    	if (input == 1 && main_package.getPlot_preferences().isDrawAnnotations() == false) {
    		//show
    		main_package.getPlot_preferences().setDrawAnnotations(true);
    		if (maximum_list.size() + minimum_list.size() < 1500 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
    	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
    	        	double x = dataset.getXValue(0, x1);
    	        	double y = dataset.getYValue(0, x1);
    	        	if (maximum_list.contains(x1+global_min)) {
    	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
    	        	}
    	        	if (minimum_list.contains(x1+global_min)) {
    	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
    	        	}
    	        	if (first_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)) {
    	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
    	        	}
    	        	if (fifth_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)  && !first_points.contains(x1+global_min)) {
    	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
    	        	}
    	        }
    		}
    	} else if (input == 0 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
    		main_package.getPlot_preferences().setDrawAnnotations(false);
    		//hide
            plot.clearAnnotations();
    	}
    }
    
    
    @FXML
    void handleMinimumColor(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	XYDataset dataset = plot.getDataset();
    	java.awt.Color initialColor = main_package.getPlot_preferences().getSeriesColorRGB();
        java.awt.Color newColor = JColorChooser.showDialog(null, "Choose Peak minimum color", initialColor);
        main_package.getPlot_preferences().setMinimumDotColorRGB(newColor);
        plot.clearAnnotations();
		if (maximum_list.size() + minimum_list.size() < 1500 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        	double x = dataset.getXValue(0, x1);
	        	double y = dataset.getYValue(0, x1);
	        	if (maximum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)  && !first_points.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
    }
	
    @FXML
    void handleMaximumColor(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	XYDataset dataset = plot.getDataset();
    	java.awt.Color initialColor = main_package.getPlot_preferences().getSeriesColorRGB();
        java.awt.Color newColor = JColorChooser.showDialog(null, "Choose Peak minimum color", initialColor);
        main_package.getPlot_preferences().setMaximumDotColorRGB(newColor);
        plot.clearAnnotations();
		if (maximum_list.size() + minimum_list.size() < 1500 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        	double x = dataset.getXValue(0, x1);
	        	double y = dataset.getYValue(0, x1);
	        	if (maximum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)  && !first_points.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
    }
    
    @FXML
    void handleFirstColor(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	XYDataset dataset = plot.getDataset();
    	java.awt.Color initialColor = main_package.getPlot_preferences().getSeriesColorRGB();
        java.awt.Color newColor = JColorChooser.showDialog(null, "Choose Peak minimum color", initialColor);
        main_package.getPlot_preferences().setFirstDotColorRGB(newColor);
        plot.clearAnnotations();
		if (maximum_list.size() + minimum_list.size() < 1500 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        	double x = dataset.getXValue(0, x1);
	        	double y = dataset.getYValue(0, x1);
	        	if (maximum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)  && !first_points.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
    }
    
    @FXML
    void handleLastColor(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	XYDataset dataset = plot.getDataset();
    	java.awt.Color initialColor = main_package.getPlot_preferences().getSeriesColorRGB();
        java.awt.Color newColor = JColorChooser.showDialog(null, "Choose Peak minimum color", initialColor);
        main_package.getPlot_preferences().setLastDotColorRGB(newColor);
        plot.clearAnnotations();
		if (maximum_list.size() + minimum_list.size() < 1500 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        	double x = dataset.getXValue(0, x1);
	        	double y = dataset.getYValue(0, x1);
	        	if (maximum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1+global_min) && !minimum_list.contains(x1+global_min)  && !first_points.contains(x1+global_min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
    }
    
    
    void save() throws IOException {
    	System.out.println("Save status:");
    	System.out.println(ask_saved);
    	if (ask_saved == false) {

            Button buttonTypeOk = new Button("Save");
    		Button buttonTypeSkip = new Button("Skip");
    		Button buttonTypeCancel = new Button("Cancel");
    		
    		Stage dialogMicroscope= new Stage();    		
	    	dialogMicroscope.initModality(Modality.APPLICATION_MODAL);
	    	dialogMicroscope.initOwner(null);
	    	dialogMicroscope.setResizable(true);
	    	GridPane grid = new GridPane();
//	    	grid.setGridLinesVisible(true);
	    	grid.setPrefWidth(250);
	    	grid.setPrefHeight(80);
	    	Label askQuestion = new Label("Save Progress?");
	    	grid.add(askQuestion, 2, 0, 1, 1);
	    	GridPane.setHalignment(askQuestion, HPos.CENTER);
	    	GridPane.setHgrow(askQuestion, Priority.ALWAYS);
	    	GridPane.setVgrow(askQuestion, Priority.ALWAYS);
	    	grid.add(buttonTypeOk, 0, 1, 1, 1);
	    	GridPane.setHalignment(buttonTypeOk, HPos.CENTER);
	    	GridPane.setHgrow(buttonTypeOk, Priority.ALWAYS);
	    	GridPane.setVgrow(buttonTypeOk, Priority.ALWAYS);
	    	grid.add(buttonTypeSkip, 2, 1, 1, 1);
	    	GridPane.setHalignment(buttonTypeSkip, HPos.CENTER);
	    	GridPane.setHgrow(buttonTypeSkip, Priority.ALWAYS);
	    	GridPane.setVgrow(buttonTypeSkip, Priority.ALWAYS);
	    	grid.add(buttonTypeCancel, 4, 1, 1, 1);
	    	GridPane.setHalignment(buttonTypeCancel, HPos.CENTER);
	    	GridPane.setHgrow(buttonTypeCancel, Priority.ALWAYS);
	    	GridPane.setVgrow(buttonTypeCancel, Priority.ALWAYS);
	    	buttonTypeOk.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	ContinueData continue_obj = new ContinueData(main_package.getCurrent_groups(), main_package.getCores(), currentGroup, fps_val, pixel_val, average_value, upper_limit, intervalsList, maximum_list, minimum_list, first_points, fifth_points, timespeedlist, main_package.getDelta(), main_package.getInter(), main_package.getIntra());
					FileChooser fileChooser = new FileChooser();
			        File file = fileChooser.showSaveDialog(dialogMicroscope);
					FileOutputStream fout = null;
					try {
						fout = new FileOutputStream(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ObjectOutputStream oos = null;
					try {
						oos = new ObjectOutputStream(fout);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						oos.writeObject(continue_obj);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						oos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						fout.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
//					Stage buttonTypeOk = (Stage) cmdNext.getScene().getWindow();
	                dialogMicroscope.close();
	                navigation();
	            }
	        });
	    	buttonTypeSkip.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                dialogMicroscope.close();
	                navigation();
	            }
	        });
	    	buttonTypeCancel.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                dialogMicroscope.close();
	            }
	        });
        	dialogMicroscope.setScene(new Scene(grid));
	    	dialogMicroscope.show();
		} else {
            navigation();
		}
    }
        
    @FXML
    void handleSeriesColor(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	java.awt.Color initialColor = main_package.getPlot_preferences().getSeriesColorRGB();
        java.awt.Color newColor = JColorChooser.showDialog(null, "Choose Series color", initialColor);
        plot.getRenderer().setSeriesPaint(0, newColor);
        main_package.getPlot_preferences().setSeriesColorRGB(newColor);
    }
    
    @FXML
    void handleSeriesThickness(ActionEvent event) {
    	XYPlot plot = currentChart.getXYPlot();
    	//int thickness = main_package.getPlot_preferences().getLineThickness();
        String test1 = JOptionPane.showInputDialog(null, "Please input new line thickness (Default: 1)");
        float new_thickness = Float.parseFloat(test1);
        if (new_thickness > 0) {
        	plot.getRenderer().setSeriesStroke(0, new java.awt.BasicStroke(new_thickness));
        	main_package.getPlot_preferences().setLineThickness(new_thickness);
        }
    }
    
    private void commitColors() {
    	XYPlot plot = currentChart.getXYPlot();
    	main_package.getPlot_preferences().setSeriesColorRGB( (java.awt.Color) plot.getRenderer().getSeriesPaint(0));
    	main_package.getPlot_preferences().setRangeGridColorRGB( (java.awt.Color) plot.getRangeGridlinePaint());
    	main_package.getPlot_preferences().setDomainGridColorRGB( (java.awt.Color) plot.getDomainGridlinePaint());
    	main_package.getPlot_preferences().setBackgroundColorRGB( (java.awt.Color) plot.getBackgroundPaint());
    }
    
    @FXML
    void back(ActionEvent event) throws ClassNotFoundException, IOException {
    	//ask for saving object
		Stage primaryStage = (Stage) cmdNext.getScene().getWindow();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
		URL url = getClass().getResource("FXML_3d_MagnitudeFirstCharts.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	Group g1 = currentGroup;
    	commitColors();
		((Controller_3d_MagnitudeFirstCharts)fxmlloader.getController()).setContext(main_package, g1, fps_val, pixel_val, average_value, upper_limit, timespeedlist);	
		primaryStage.setTitle("ContractionWave - Subset Main Chart");
//		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }

    @FXML
    void nextPageNavigate(ActionEvent event) throws IOException {
    	int validation = validateRadio();
    	if(validation == -1) return;
    	save();
    }
    
    void navigation() {
       		double peak_size = (stop - start);
    		int option = alertRAM(peak_size);
    		if(option == -1) return;
    		CalculationTaskSave new_task = new CalculationTaskSave(currentGroup, main_package, stop, start, 1, 0);
    		main_package.getExec().submit(new_task);
    		new_task.setOnSucceeded(ignoredArg -> {
    			System.out.println("Done!");
    			try {
    				System.out.println("Next page!");
    				nextPageDo();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		});
    }
        	
	public void writeTableTSV(File file, TableView viewResultsTable) throws Exception {
	    Writer writer = null;
	    try {
	        writer = new BufferedWriter(new FileWriter(file));
	        String text2 = "";
	        
	        for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
				if (j == 0) {
					text2 = ((TableColumn<Peak, String>) viewResultsTable.getColumns().get(j)).getText();
					System.out.println(text2 + " 1");
				} else {
					TableColumn<Peak, Double> a = (TableColumn<Peak, Double>) viewResultsTable.getColumns().get(j);
					Label b = (Label) a.getGraphic();
					//text2 = ((TableColumn<Peak, Double>) viewResultsTable.getColumns().get(j)).getText();
					text2 = b.getText();
					System.out.println(text2 + " 2");
				}
				writer.write(text2 + "\t");
			}
	        writer.write("\n");
	        
	        for (int i = 0; i < viewResultsTable.getItems().size(); i++) {
	        	for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
	        		if (j == 0) {
						if(((TableColumnBase<Peak, String>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
							text2 = ((TableColumnBase<Peak, String>) viewResultsTable.getColumns().get(j)).getCellData(i).toString();
							System.out.println(text2 + " 3");
						}
						else {
							text2 = "";
							System.out.println(text2 + " 4");
						}
						
					} else {
						if(((TableColumnBase<Peak, Double>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
							text2 = ((TableColumnBase<Peak, Double>) viewResultsTable.getColumns().get(j)).getCellData(i).toString();
							System.out.println(text2 + " 5");
						}
						else {
							text2 = "";
							System.out.println(text2 + " 6");
						}
					}
	        		writer.write(text2 + "\t");
	        	}
	        	writer.write("\n");
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    finally {
	        writer.flush();
	        writer.close();
	    } 
	}
    
	
	void createNewSheet(Sheet spreadsheet, TableView viewResultsTable) {
		Row row = spreadsheet.createRow(0);
		for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
			if (j == 0) {
				row.createCell(j).setCellValue(((TableColumn<Peak, String>) viewResultsTable.getColumns().get(j)).getText());
			} else {
				TableColumn<Peak, Double> a = (TableColumn<Peak, Double>) viewResultsTable.getColumns().get(j);
				Label b = (Label) a.getGraphic();
				row.createCell(j).setCellValue(b.getText());
			}
		}
		for (int i = 0; i < viewResultsTable.getItems().size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {

				if (j == 0) {
					if(((TableColumnBase<Peak, String>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
						row.createCell(j).setCellValue(((TableColumnBase<Peak, String>) viewResultsTable.getColumns().get(j)).getCellData(i).toString()); 
					}
					else {
						row.createCell(j).setCellValue("");
					}
					
				} else {
					if(((TableColumnBase<Peak, Double>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
						row.createCell(j).setCellValue(((TableColumnBase<Peak, Double>) viewResultsTable.getColumns().get(j)).getCellData(i).toString()); 
					}
					else {
						row.createCell(j).setCellValue("");
					}
				}
			}
		}
		for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
			spreadsheet.autoSizeColumn(j);
		}
	}
	
	void createAverageSheet(Sheet spreadsheet, TableView thistable) {
		Row row = spreadsheet.createRow(0);
		row.createCell((int)thistable.getColumns().size()/2).setCellValue("Averages:");
		row = spreadsheet.createRow(1);
		for (int j = 0; j < thistable.getColumns().size(); j++) {
			if (j == 0) {
				row.createCell(j).setCellValue(((TableColumn<Peak, String>) thistable.getColumns().get(j)).getText());
			} else {
				TableColumn<Peak, Double> a = (TableColumn<Peak, Double>) thistable.getColumns().get(j);
				Label b = (Label) a.getGraphic();
				row.createCell(j).setCellValue(b.getText());
			}
		}
		row = spreadsheet.createRow(2);
		List<Double> avgs = new ArrayList<Double>();
		for (int j = 1; j < thistable.getColumns().size(); j++) {
			double avg = 0.0;
			double row_number = 0.0;
			for (int i = 0; i < thistable.getItems().size(); i++) {
				Double result = ((TableColumnBase<Peak, Double>) thistable.getColumns().get(j)).getCellData(i);
				if (result != null) {
					avg += result.doubleValue();
					row_number += 1.0;
				}
			}
			avg /= row_number;
			avgs.add(avg);
			row.createCell(j).setCellValue(avg);
		}
		row = spreadsheet.createRow(4);
		row.createCell((int)thistable.getColumns().size()/2).setCellValue("Standard Deviation:");
		row = spreadsheet.createRow(5);
		for (int j = 1; j < thistable.getColumns().size(); j++) {
			double dev = 0.0;
			int row_number = 0;
			for (int i = 0; i < thistable.getItems().size(); i++) {
				Double result = ((TableColumnBase<Peak, Double>) thistable.getColumns().get(j)).getCellData(i);
				if (result != null) {
					double value = result.doubleValue() - avgs.get(j-1);
					dev += Math.pow(value, 2);
					row_number += 1;
				}
			}
//			System.out.println(dev);
			dev /= row_number;
//			System.out.println(dev);
			dev = Math.sqrt(dev);
			System.out.println(dev);
			row.createCell(j).setCellValue(dev);
		}
		for (int j = 0; j < thistable.getColumns().size(); j++) {
			spreadsheet.autoSizeColumn(j);
		}
	}
	
	void exportTables(String filename) throws IOException {

		//include average and st deviation table
		Workbook workbook = new HSSFWorkbook();
		Sheet spreadsheet = workbook.createSheet("time");
		createNewSheet(spreadsheet, timeTableView);
		Sheet spreadsheet2 = workbook.createSheet("speed");
		createNewSheet(spreadsheet2, speedTableView);
		Sheet spreadsheet3 = workbook.createSheet("area");
		createNewSheet(spreadsheet3, areaTableView);
		Sheet spreadsheet4 = workbook.createSheet("time_stats");
		createAverageSheet(spreadsheet4, timeTableView);
		Sheet spreadsheet5 = workbook.createSheet("speed_stats");
		createAverageSheet(spreadsheet5, speedTableView);
		Sheet spreadsheet6 = workbook.createSheet("area_stats");
		createAverageSheet(spreadsheet6, areaTableView);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName(filename);
	    Stage primaryStage;
	    primaryStage = (Stage) cmdNext.getScene().getWindow();
	    //Show save file dialog
	    File file = fileChooser.showSaveDialog(primaryStage);
		FileOutputStream fileOut = new FileOutputStream(file);
		workbook.write(fileOut);
		workbook.close();
		fileOut.close();
	}
	
    void exportTable(TableView viewResultsTable, int type, String filename) throws Exception {
		if (type == 0){
			Workbook workbook = new HSSFWorkbook();
			String f_sheet_name = filename.split("\\-")[0];
			Sheet spreadsheet = workbook.createSheet(f_sheet_name);
			Row row = spreadsheet.createRow(0);
			
			for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
				if (j == 0) {
					row.createCell(j).setCellValue(((TableColumn<Peak, String>) viewResultsTable.getColumns().get(j)).getText());
				} else {
					TableColumn<Peak, Double> a = (TableColumn<Peak, Double>) viewResultsTable.getColumns().get(j);
					Label b = (Label) a.getGraphic();
					row.createCell(j).setCellValue(b.getText());
				}
			}
			for (int i = 0; i < viewResultsTable.getItems().size(); i++) {
				row = spreadsheet.createRow(i + 1);
				for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {

					if (j == 0) {
						if(((TableColumnBase<Peak, String>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
							row.createCell(j).setCellValue(((TableColumnBase<Peak, String>) viewResultsTable.getColumns().get(j)).getCellData(i).toString()); 
						}
						else {
							row.createCell(j).setCellValue("");
						}
						
					} else {
						if(((TableColumnBase<Peak, Double>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
							row.createCell(j).setCellValue(((TableColumnBase<Peak, Double>) viewResultsTable.getColumns().get(j)).getCellData(i).toString()); 
						}
						else {
							row.createCell(j).setCellValue("");
						}
					}
				}
			}
			for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
				spreadsheet.autoSizeColumn(j);
			}
			String type_tb = filename.split("\\-")[0] + "_stats";
			Sheet spreadsheet5 = workbook.createSheet(type_tb);
			createAverageSheet(spreadsheet5, viewResultsTable);
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(filename);
	        Stage primaryStage;
	    	primaryStage = (Stage) cmdNext.getScene().getWindow();
	        //Show save file dialog
	        File file = fileChooser.showSaveDialog(primaryStage);
			FileOutputStream fileOut = new FileOutputStream(file);
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();
		} else {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(filename);
	        Stage primaryStage;
	    	primaryStage = (Stage) cmdNext.getScene().getWindow();
	        //Show save file dialog
	        File file = fileChooser.showSaveDialog(primaryStage);
			writeTableTSV(file, viewResultsTable);
		}
    }
        
    private void nextPageDo() throws IOException {
		Stage primaryStage = (Stage) cmdNext.getScene().getWindow();
    	double prior_X = primaryStage.getX();
    	double prior_Y = primaryStage.getY();
//		URL url = getClass().getResource("FXML_3e_ViewJetQuiverMerge.fxml");
		URL url = getClass().getResource("FXML_3e_ViewJetQuiverMergeSingle.fxml");
    	FXMLLoader fxmlloader = new FXMLLoader();
    	fxmlloader.setLocation(url);
    	fxmlloader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
    	root = fxmlloader.load();
    	Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
//    	javafx.geometry.Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
//    	Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    	commitColors();
//    	((Controller_3e_ViewJetQuiverMerge)fxmlloader.getController()).setContext(main_package, currentGroup, fps_val, pixel_val, average_value, upper_limit, use_double, start, stop, step, timespeedlist);
    	((Controller_3e_ViewJetQuiverMergeSingle)fxmlloader.getController()).setContext(main_package, currentGroup, fps_val, pixel_val, average_value, upper_limit, start, stop, step, intervalsList, maximum_list, minimum_list, first_points, fifth_points, timespeedlist, ask_saved, checkSeconds.isSelected());
    	primaryStage.setTitle("ContractionWave - Peak Parameters Plot");
//    	primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setX(prior_X);
		primaryStage.setY(prior_Y);
    }
    
    
	private int alertRAM(double frames){
		System.out.println("querying available RAM");
		double height = currentGroup.getHeight();
		double width = currentGroup.getWidth();
		double consumption = (frames * height * width * Double.BYTES) / (1024 * 1024);
		double freeRAM = Runtime.getRuntime().freeMemory();
		System.out.println(consumption);
		System.out.println(freeRAM);
		
		if(consumption > freeRAM){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Generating Plots Alert");
			alert.setHeaderText("The approximate memory usage for this operation is: " + String.valueOf(consumption) + "MB");
			alert.setContentText("Choose your option:");
	
			ButtonType buttonTypeOne = new ButtonType("Run using Memory (faster)");
//			ButtonType buttonTypeTwo = new ButtonType("Save each result in files (slower)");
			ButtonType buttonTypeCancel = new ButtonType("Cancel operation", ButtonData.CANCEL_CLOSE);
	
//			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
	
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne){
			    // ... user chose "One"
				return 0;
//			} else if (result.get() == buttonTypeTwo) {
			    // ... user chose "Two"
//				return 1;
			} else {
			    // ... user chose CANCEL or closed the dialog
				return -1;
			}
		}
		return 0;
	}
	
	private int validateRadio() {
		if (timeTableView.getSelectionModel().getSelectedItem() == null) return -1;
		return 1;
	}
        
	public void setContext(PackageData main_package_data, Group g1, double fps_val1, double pixel_val1, double average_value2, double upper_limit2, List<IntervalMarker> intervalsList1, List<Integer> maximum_list1, List<Integer> minimum_list1, List<Integer> first_points1, List<Integer> fifth_points1, List<TimeSpeed>timespeedlist2, boolean saved) throws IOException, ClassNotFoundException {
		//read file, plot first graph, adapt second
		main_package = main_package_data;
		average_value = average_value2;
		upper_limit = upper_limit2;
		currentGroup = g1;
//		currentChart = currentChart2;
		intervalsList = intervalsList1;
		maximum_list = maximum_list1;
		minimum_list = minimum_list1;
		first_points = first_points1;
		fifth_points = fifth_points1;
		fps_val = fps_val1;
		pixel_val = pixel_val1;
		//peak parameters should be calculated here
		intervalPeaks = new IntervalPeaks();
		intervalPeaks.getListPeaks().clear();
		timespeedlist = timespeedlist2;
		ask_saved = saved;
		
    	System.out.println("intervalsList.size()");
    	System.out.println(intervalsList.size());
    	int zix = 0;
      	for (IntervalMarker e : intervalsList) {
      		System.out.println("IntervalMarker:" + zix);
    		IntervalPeak f = new IntervalPeak(currentGroup, average_value, fps_val, pixel_val, e, maximum_list, minimum_list, first_points, fifth_points);
    		intervalPeaks.addIntervalPeak(f);
    		zix+=1;
    	}
    	System.out.println("intervalPeaks.getListPeaks().size()");
    	System.out.println(intervalPeaks.getListPeaks().size());
    	//for each peak in intervalPeaks, a reference in table should be created
    	timeTableView.setItems(intervalPeaks.getObservableList());
    	speedTableView.setItems(intervalPeaks.getObservableList());
    	areaTableView.setItems(intervalPeaks.getObservableList());
    	currentPeak = intervalPeaks.getListPeaks().get(0);
		int to_val;
		int from_val;
		if (currentPeak.getF_point() - 1 >= 0) {
			from_val = currentPeak.getF_point() - 1;
		} else {
			from_val = 0;
		}
		start = from_val;
		if (currentPeak.getEnd_point() + 1 <= currentGroup.size() - 1) {
			to_val = currentPeak.getEnd_point() + 1;
		} else {
//			to_val = currentGroup.size() - 1;
			to_val = currentGroup.size();
		}
		stop = to_val;
		System.out.println("start , stop:");
		System.out.println(start + "," + stop);
    	writeLinePlot(start, stop);
    	timeTableView.getSelectionModel().selectFirst();
    	speedTableView.getSelectionModel().selectFirst();
    	areaTableView.getSelectionModel().selectFirst();
    	currentSelModel = timeTableView.getSelectionModel().getSelectedIndices().stream().collect(java.util.stream.Collectors.toList());
//		currentSelModel = timeTableView.getSelectionModel().getSelectedIndices();
    	resizeTable(timeTableView);
    	resizeTable(speedTableView);
    	resizeTable(areaTableView);
	}
	
	
	private List<Integer> currentSelModel = new ArrayList<Integer>();
	
	
	@SuppressWarnings("unchecked")
	public void resizeTable(TableView viewResultsTable) {
		for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
			if (j > 0) {
				TableColumn<Peak, Double> a = (TableColumn<Peak, Double>) viewResultsTable.getColumns().get(j);
				Label b = (Label) a.getGraphic();
				Text theText = new Text(b.getText());
//				double col_width = a.getWidth();
				double col_width = a.widthProperty().get();
				double text_width = theText.getBoundsInLocal().getWidth();
				double text_f_size = theText.getFont().getSize();
				String f_name = theText.getFont().getName();
				double diff = col_width - text_width;
				while (diff < 0 && text_f_size > 10) {
					text_f_size -= 1;
//					javafx.scene.text.Fontnew javafx.scene.text.Font(f_name, text_f_size);
					theText.setFont(javafx.scene.text.Font.font(theText.getFont().getFamily(), FontWeight.SEMI_BOLD, text_f_size));
					text_width = theText.getBoundsInLocal().getWidth();
					diff = col_width - text_width;
				}
				((Label) ((TableColumn<Peak, Double>) viewResultsTable.getColumns().get(j)).getGraphic()).setFont( javafx.scene.text.Font.font(theText.getFont().getFamily(), FontWeight.SEMI_BOLD, theText.getFont().getSize()));

			}
		}
//				TableColumn<Peak, Double> a = (TableColumn<Peak, Double>) viewResultsTable.getColumns().get(j);
//				Label b = (Label) a.getGraphic();
//				row.createCell(j).setCellValue(b.getText());
//			}
//		}
//		for (int i = 0; i < viewResultsTable.getItems().size(); i++) {
//			row = spreadsheet.createRow(i + 1);
//			for (int j = 0; j < viewResultsTable.getColumns().size(); j++) {
//		
//				if (j == 0) {
//					if(((TableColumnBase<Peak, String>) viewResultsTable.getColumns().get(j)).getCellData(i) != null) {
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		timeRadio.setToggleGroup(radioGroup);
		speedRadio.setToggleGroup(radioGroup);
		areaRadio.setToggleGroup(radioGroup);
		timeRadio.setUserData("time");
		speedRadio.setUserData("speed");
		areaRadio.setUserData("area");
		timeRadio.setSelected(true);
		radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			
			@Override
		    public void changed(ObservableValue<? extends Toggle> ov,
		        Toggle old_toggle, Toggle new_toggle) {
		            if (radioGroup.getSelectedToggle() != null) {
		            	if (radioGroup.getSelectedToggle().getUserData().equals("time") == true) {
		            		timeTableView.setVisible(true);
		            		speedTableView.setVisible(false);
		            		areaTableView.setVisible(false);
		            	} else if (radioGroup.getSelectedToggle().getUserData().equals("speed") == true) {
		            		timeTableView.setVisible(false);
		            		speedTableView.setVisible(true);
		            		areaTableView.setVisible(false);
		            	} else {
		            		timeTableView.setVisible(false);
		            		speedTableView.setVisible(false);
		            		areaTableView.setVisible(true);
		            	}
		            }                
		        }
		});
		
		
		Image imgBack = new Image(getClass().getResourceAsStream("/left-arrow-angle.png"));
		cmdBack.setGraphic(new ImageView(imgBack));
		Tooltip tooltip5 = new Tooltip();
		tooltip5.setText("Back to microscope parametrization");
		cmdBack.setTooltip(tooltip5);
		
		Image imgNext = new Image(getClass().getResourceAsStream("/right-arrow-angle.png"));
		cmdNext.setGraphic(new ImageView(imgNext));
		Tooltip tooltip6 = new Tooltip();
		tooltip6.setText("Display and Save Jet, Quiver and Merge plots");
		cmdNext.setTooltip(tooltip6);
		
		
		speedTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
//		speedTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		speedTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		speedTableView.getSelectionModel().getSelectedItems().addListener((Change<? extends Peak> c) -> {
//		speedTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			System.out.println("speed");
			System.out.println(currentSelModel.toString());
			System.out.println(speedTableView.getSelectionModel().getSelectedIndices().toString());
		    if ((speedTableView.getSelectionModel().getSelectedIndices().size() >= 1) && (!speedTableView.getSelectionModel().getSelectedIndices().equals(currentSelModel))) {
				//List<Integer> indexes = areaTableView.getSelectionModel().getSelectedIndices();
		    	System.out.println("ch ch changeee");
				int final_from = currentGroup.size();
				int final_to = 0;
				for (Peak z : speedTableView.getSelectionModel().getSelectedItems()) {
					currentPeak = z;
					int to_val;
					int from_val;
					if (currentPeak.getF_point() - 1 >= 0) {
						from_val = currentPeak.getF_point() - 1;
					} else {
						from_val = 0;
					}
					if (currentPeak.getEnd_point() + 1 <= currentGroup.size() - 1) {
						to_val = currentPeak.getEnd_point() + 1;
					} else {
						to_val = currentGroup.size() - 1;
					}
					if (final_from > from_val) {
						final_from = from_val;
					}
					if (final_to < to_val) {
						final_to = to_val;
					}
				}
				start = final_from;
				stop = final_to;
				System.out.println("start , stop:");
				System.out.println(start + "," + stop);
		    	writeLinePlot(start, stop);
//		    	currentSelModel = new ArrayList<Integer>(speedTableView.getSelectionModel().getSelectedIndices().size());
//		    	Collections.copy(currentSelModel, speedTableView.getSelectionModel().getSelectedIndices());
		    	currentSelModel = speedTableView.getSelectionModel().getSelectedIndices().stream().collect(java.util.stream.Collectors.toList());
		    	areaTableView.setSelectionModel(speedTableView.getSelectionModel());
		    	timeTableView.setSelectionModel(speedTableView.getSelectionModel());
			}
		    
		    
//		    if ((newSelection != null) && (currentPeak.getF_point() != timeTableView.getSelectionModel().getSelectedItem().getF_point()) && (currentPeak.getEnd_point() != timeTableView.getSelectionModel().getSelectedItem().getEnd_point())) {
//		    	currentPeak = timeTableView.getSelectionModel().getSelectedItem();
//		    	int index = timeTableView.getSelectionModel().getSelectedIndex();
//				int to_val;
//				int from_val;
//				if (currentPeak.getF_point() - 1 >= 0) {
//					System.out.println("case start");
//					from_val = currentPeak.getF_point() - 1;
//				} else {
//					from_val = 0;
//				}
//				start = from_val;
//				if (currentPeak.getEnd_point() + 1 <= currentGroup.size() - 1) {
//					System.out.println("case end");
//					to_val = currentPeak.getEnd_point() + 1;
//				} else {
//					to_val = currentGroup.size() - 1;
//				}
//				stop = to_val;
//				System.out.println("start , stop:");
//				System.out.println(start + "," + stop);
//		    	writeLinePlot(start, stop);
//		    	timeTableView.getSelectionModel().select(index);
//		    	areaTableView.getSelectionModel().select(index);
//		    }
		});
		
		
		timeTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
//		timeTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		timeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//		timeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		timeTableView.getSelectionModel().getSelectedItems().addListener((Change<? extends Peak> c) -> {
			System.out.println("time");
			System.out.println(currentSelModel.toString());
			System.out.println(timeTableView.getSelectionModel().getSelectedIndices().toString());
		    if ((timeTableView.getSelectionModel().getSelectedIndices().size() >= 1) && (!timeTableView.getSelectionModel().getSelectedIndices().equals(currentSelModel))) {
				//List<Integer> indexes = areaTableView.getSelectionModel().getSelectedIndices();
				System.out.println("time changeee");
				int final_from = currentGroup.size();
				int final_to = 0;
				for (Peak z : timeTableView.getSelectionModel().getSelectedItems()) {
					currentPeak = z;
					int to_val;
					int from_val;
					if (currentPeak.getF_point() - 1 >= 0) {
						from_val = currentPeak.getF_point() - 1;
					} else {
						from_val = 0;
					}
					if (currentPeak.getEnd_point() + 1 <= currentGroup.size() - 1) {
						to_val = currentPeak.getEnd_point() + 1;
					} else {
//						to_val = currentGroup.size() - 1;
						to_val = currentGroup.size();
					}
					if (final_from > from_val) {
						final_from = from_val;
					}
					if (final_to < to_val) {
						final_to = to_val;
					}
				}
				start = final_from;
				stop = final_to;
				System.out.println("start , stop:");
				System.out.println(start + "," + stop);
		    	writeLinePlot(start, stop);
//		    	currentSelModel = new ArrayList<Integer>(timeTableView.getSelectionModel().getSelectedIndices().size());
//		    	Collections.copy(currentSelModel, timeTableView.getSelectionModel().getSelectedIndices());
		    	currentSelModel = timeTableView.getSelectionModel().getSelectedIndices().stream().collect(java.util.stream.Collectors.toList());

		    	areaTableView.setSelectionModel(timeTableView.getSelectionModel());
		    	speedTableView.setSelectionModel(timeTableView.getSelectionModel());
			}
			
			
//		    if ((newSelection != null) && (currentPeak.getF_point() != timeTableView.getSelectionModel().getSelectedItem().getF_point()) && (currentPeak.getEnd_point() != timeTableView.getSelectionModel().getSelectedItem().getEnd_point())) {
//		    	currentPeak = timeTableView.getSelectionModel().getSelectedItem();
//		    	int index = timeTableView.getSelectionModel().getSelectedIndex();
//				int to_val;
//				int from_val;
//				if (currentPeak.getF_point() - 1 >= 0) {
//					from_val = currentPeak.getF_point() - 1;
//				} else {
//					from_val = 0;
//				}
//				start = from_val;
//				if (currentPeak.getEnd_point() + 1 <= currentGroup.size() - 1) {
//					to_val = currentPeak.getEnd_point() + 1;
//				} else {
//					to_val = currentGroup.size() - 1;
//				}
//				stop = to_val;
//				System.out.println("start , stop:");
//				System.out.println(start + "," + stop);
//		    	writeLinePlot(start, stop);
//		    	speedTableView.getSelectionModel().select(index);
//		    	areaTableView.getSelectionModel().select(index);
//		    }
		});
		
		areaTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
//		areaTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		areaTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		areaTableView.getSelectionModel().getSelectedItems().addListener((Change<? extends Peak> c) -> {
//		areaTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			System.out.println("area");
//		    if ((newSelection != null) && (!areaTableView.getSelectionModel().getSelectedIndices().equals(currentSelModel))) {
			if ((areaTableView.getSelectionModel().getSelectedIndices().size() >= 1) && (!areaTableView.getSelectionModel().getSelectedIndices().equals(currentSelModel))) {
				System.out.println("area changee");
				//List<Integer> indexes = areaTableView.getSelectionModel().getSelectedIndices();
				int final_from = currentGroup.size();
				int final_to = 0;
				for (Peak z : areaTableView.getSelectionModel().getSelectedItems()) {
					currentPeak = z;
					int to_val;
					int from_val;
					if (currentPeak.getF_point() - 1 >= 0) {
						from_val = currentPeak.getF_point() - 1;
					} else {
						from_val = 0;
					}
					if (currentPeak.getEnd_point() + 1 <= currentGroup.size() - 1) {
						to_val = currentPeak.getEnd_point() + 1;
					} else {
//						to_val = currentGroup.size() - 1;
						to_val = currentGroup.size();
					}
					if (final_from > from_val) {
						final_from = from_val;
					}
					if (final_to < to_val) {
						final_to = to_val;
					}
				}
				start = final_from;
				stop = final_to;
				System.out.println("start , stop:");
				System.out.println(start + "," + stop);
		    	writeLinePlot(start, stop);
//		    	currentSelModel = new ArrayList<Integer>(areaTableView.getSelectionModel().getSelectedIndices().size());
//		    	Collections.copy(currentSelModel, areaTableView.getSelectionModel().getSelectedIndices());
		    	currentSelModel = areaTableView.getSelectionModel().getSelectedIndices().stream().collect(java.util.stream.Collectors.toList());

		    	speedTableView.setSelectionModel(areaTableView.getSelectionModel());
		    	timeTableView.setSelectionModel(areaTableView.getSelectionModel());
			}
			
//		    if ((newSelection != null) && (currentPeak.getF_point() != areaTableView.getSelectionModel().getSelectedItem().getF_point()) && (currentPeak.getEnd_point() != areaTableView.getSelectionModel().getSelectedItem().getEnd_point())) {
//		    	currentPeak = areaTableView.getSelectionModel().getSelectedItem();
//		    	int index = areaTableView.getSelectionModel().getSelectedIndex();
//				int to_val;
//				int from_val;
//				if (currentPeak.getF_point() - 1 >= 0) {
//					from_val = currentPeak.getF_point() - 1;
//				} else {
//					from_val = 0;
//				}
//				start = from_val;
//				if (currentPeak.getEnd_point() + 1 <= currentGroup.size() - 1) {
//					to_val = currentPeak.getEnd_point() + 1;
//				} else {
//					to_val = currentGroup.size() - 1;
//				}
//				stop = to_val;
//				System.out.println("start , stop:");
//				System.out.println(start + "," + stop);
//		    	writeLinePlot(start, stop);
//		    	speedTableView.getSelectionModel().select(index);
//		    	timeTableView.getSelectionModel().select(index);
//		    }
		});

	    
	    
//		loadedColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 3 ); // 1% width
		posCol.setMaxWidth( 1f * Integer.MAX_VALUE * 12 ); // 11% width
		
		//Time Labels and Size
		
		Label tcrLabel = new Label("Contraction-Relaxation Time");
//		Label tcrLabel = new Label("CRT");
	    tcrLabel.setTooltip(new Tooltip("Contraction-Relaxation Time"));
	    tcrCol.setText("");
	    tcrCol.setGraphic(tcrLabel);
		tcrCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width
		
		Label ctLabel = new Label("Contraction Time");
//		Label ctLabel = new Label("CT");
	    ctLabel.setTooltip(new Tooltip("Contraction Time"));
	    tcCol.setText("");
	    tcCol.setGraphic(ctLabel);
		tcCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width
		
		Label rtLabel = new Label("Relaxation Time");
//		Label rtLabel = new Label("RT");
	    rtLabel.setTooltip(new Tooltip("Relaxation Time"));
	    trCol.setText("");
	    trCol.setGraphic(rtLabel);
		trCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width
		
		Label vmcLabel = new Label("Contraction Time up to VMC");
//		Label vmcLabel = new Label("CT-VMC");
	    vmcLabel.setTooltip(new Tooltip("Contraction Time up to VMC"));
	    tc_vmcCol.setText("");
	    tc_vmcCol.setGraphic(vmcLabel);
		tc_vmcCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width
		
		Label vmcMinLabel = new Label("Contraction Time up to Minimum Speed");
		
//		Label vmcMinLabel = new Label("CT-MS");
		vmcMinLabel.setTooltip(new Tooltip("Contraction Time up to Minimum Speed"));
	    tc_vmc_minCol.setText("");
	    tc_vmc_minCol.setGraphic(vmcMinLabel);
		tc_vmc_minCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width
		
//		System.out.println(tc_vmc_minCol.getMaxWidth() - theText.getBoundsInLocal().getWidth());
		
		Label tr_vmrLabel = new Label("Relaxation Time up to VMR");
//		Label tr_vmrLabel = new Label("RT-VMR");
		tr_vmrLabel.setTooltip(new Tooltip("Relaxation Time up to VMR"));
	    tr_vmrCol.setText("");
	    tr_vmrCol.setGraphic(tr_vmrLabel);
		tr_vmrCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width
		
		Label tr_vmr_bLabel = new Label("Relaxation Time up to Basal");
//		Label tr_vmr_bLabel = new Label("RT-B");
		tr_vmr_bLabel.setTooltip(new Tooltip("Relaxation Time up to Basal"));
	    tr_vmr_bCol.setText("");
	    tr_vmr_bCol.setGraphic(tr_vmr_bLabel);
		tr_vmr_bCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width
		
		Label t_vmc_vmrLabel = new Label("MCS/MRS Difference Time");
//		Label t_vmc_vmrLabel = new Label("MCS/MRS-DT");
		t_vmc_vmrLabel.setTooltip(new Tooltip("MCS/MRS Difference Time"));
	    t_vmc_vmrCol.setText("");
	    t_vmc_vmrCol.setGraphic(t_vmc_vmrLabel);
		t_vmc_vmrCol.setMaxWidth( 1f * Integer.MAX_VALUE * 11 ); // 11% width

		
		//Speed Labels and Size
		
		speedPosCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
		
//		Label speedVMCLabel = new Label("MCS");
		Label speedVMCLabel = new Label("Maximum Contraction Speed");
		speedVMCLabel.setTooltip(new Tooltip("Maximum Contraction Speed"));
	    speedVMCCol.setText("");
	    speedVMCCol.setGraphic(speedVMCLabel);
		speedVMCCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
		
//		Label speedVMRLabel = new Label("MRS");
		Label speedVMRLabel = new Label("Maximum Relaxation Speed");
		speedVMRLabel.setTooltip(new Tooltip("Maximum Relaxation Speed"));
	    speedVMRCol.setText("");
	    speedVMRCol.setGraphic(speedVMRLabel);
		speedVMRCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
		
		Label speed_DVMCVMRLabel = new Label("MCS/MRS Difference Speed");
//		Label speed_DVMCVMRLabel = new Label("MCS/MRS-DS");
		speed_DVMCVMRLabel.setTooltip(new Tooltip("MCS/MRS Difference Speed"));
	    speed_DVMCVMRCol.setText("");
	    speed_DVMCVMRCol.setGraphic(speed_DVMCVMRLabel);
		speed_DVMCVMRCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
		
//		Label speedVMINLabel = new Label("VMIN");
//		speedVMINLabel.setTooltip(new Tooltip(""));
////	    speedVMINCol.setText("");
//	    speedVMINCol.setGraphic(speedVMINLabel);
//		speedVMINCol.setMaxWidth( 1f * Integer.MAX_VALUE * 20 );
//		
		//Area Labels and Size
		
		areaPosCol.setMaxWidth( 1f * Integer.MAX_VALUE * 34 );
		
		Label speedAREATLabel = new Label("Contraction-Relaxation Area");
//		Label speedAREATLabel = new Label("CRA");
		speedAREATLabel.setTooltip(new Tooltip("Contraction-Relaxation Area"));
	    speedAREATCol.setText("");
	    speedAREATCol.setGraphic(speedAREATLabel);
		speedAREATCol.setMaxWidth( 1f * Integer.MAX_VALUE * 33 );
		
		Label speedAREACLabel = new Label("Shortening Fraction Area");
//		Label speedAREACLabel = new Label("SFA");
		speedAREACLabel.setTooltip(new Tooltip("Shortening Fraction Area"));
	    speedAREACCol.setText("");
	    speedAREACCol.setGraphic(speedAREACLabel);
		speedAREACCol.setMaxWidth( 1f * Integer.MAX_VALUE * 33 );
		
//		Label speedAREARLabel = new Label("");
//		speedAREARLabel.setTooltip(new Tooltip(""));
//	    speedAREARCol.setText("");
//	    speedAREARCol.setGraphic(speedAREARLabel);
//		speedAREARCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
		
		//Fit data to table
		posCol.setCellValueFactory(new PropertyValueFactory<Peak,String>("pos"));
		tcrCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("tcr"));
		tcCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("tc"));
		trCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("tr"));
		tc_vmcCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("tc_vmc"));
		tc_vmc_minCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("tc_vmc_min"));
		tr_vmrCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("tr_vmr"));
		tr_vmr_bCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("tr_vmr_b"));
		t_vmc_vmrCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("t_vmc_vmr"));
		
		speedPosCol.setCellValueFactory(new PropertyValueFactory<Peak,String>("pos"));
		speedVMCCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("vmc"));
		speedVMRCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("vmr"));
		speed_DVMCVMRCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("d_vmc_vmr"));
//		speedVMINCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("vmin"));
		
		areaPosCol.setCellValueFactory(new PropertyValueFactory<Peak,String>("pos"));
		speedAREATCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("area_t"));
		speedAREACCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("area_c"));
//		speedAREARCol.setCellValueFactory(new PropertyValueFactory<Peak,Double>("area_r"));
		
		ChangeListener<Number> stageSizeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            	System.out.println("resizing all");
    		   	resizeTable(timeTableView);
    	    	resizeTable(speedTableView);
    	    	resizeTable(areaTableView);
            }
        };
		
        timeTableView.widthProperty().addListener(stageSizeListener);
        timeTableView.heightProperty().addListener(stageSizeListener);
        checkSeconds.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            	intervalPeaks.convertTime();
            	timeTableView.refresh();
            	writeLinePlot(start, stop);
            }
        });
	}
	
	
	//PLOT GENERATING FUNCTIONS
	private void writeLinePlot(int min, int max) {
		global_min = min;
		currentChart = createChart(createDataset(min, max), min);
		ChartPanel linepanel = new ChartPanel(currentChart);

		JCheckBoxMenuItem gridLinesmenuItem = new JCheckBoxMenuItem();
		gridLinesmenuItem.setSelected(true);
		gridLinesmenuItem.setText("Gridlines on/off");
		GridLinesSwitch gridLinesZoomAction = new GridLinesSwitch(linepanel); 
		gridLinesmenuItem.addActionListener(gridLinesZoomAction);
		linepanel.getPopupMenu().add(gridLinesmenuItem);
		
		JCheckBoxMenuItem showSpline = new JCheckBoxMenuItem();
		showSpline.setText("Render Splines on/off");
		SplineShow splineRendering = new SplineShow(linepanel);
		showSpline.addActionListener(splineRendering);
		linepanel.getPopupMenu().add(showSpline);
		
		linepanel.setRangeZoomable(false);
		linepanel.setDomainZoomable(false);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
        );
        linepanel.setBorder(border);
        linepanel.setMouseWheelEnabled(true);

        swgNode.setContent(linepanel);
	}
	
	private XYDataset createDataset(int min, int max) {
		XYSeries series1 = new XYSeries("Optical Flow");
		for (int i = min; i < max; i++) {
			double average = currentGroup.getMagnitudeListValue(i);
//			series1.add(i / fps_val, average * fps_val * pixel_val);
			double new_time = i / fps_val;
			if (checkSeconds.isSelected() == false) {
				new_time *= 1000;
			}
			series1.add(new_time, (average * fps_val * pixel_val) - average_value);
		}
		//peak detection algorithm receives a group
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        return dataset;
    }
	
	
	private JFreeChart createChart(XYDataset dataset, int min) {
		String time_str =  "Time (s)";
		if (checkSeconds.isSelected() == false) {
			time_str = "Time (ms)";
		}
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Main Plot",
            time_str,
            "Speed (\u00B5m/s)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        Font title_font = new Font("Dialog", Font.PLAIN, 17); 
        Font domain_range_axis_font = new Font("Dialog", Font.PLAIN, 14); 
        chart.getTitle().setFont(title_font);
        chart.removeLegend();
        plot.getDomainAxis().setLabelFont(domain_range_axis_font);
        plot.getRangeAxis().setLabelFont(domain_range_axis_font);
        plot.getRangeAxis().setUpperMargin(0.1);
        lowerBoundDomain = chart.getXYPlot().getDomainAxis().getLowerBound();
        upperBoundDomain = chart.getXYPlot().getDomainAxis().getUpperBound();

        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        //plot.setSeriesPaint(0, new Color(0x00, 0x00, 0xFF));
        plot.setBackgroundPaint(main_package.getPlot_preferences().getBackgroundColorRGB());
        plot.setDomainGridlinePaint(main_package.getPlot_preferences().getDomainGridColorRGB());
        plot.setRangeGridlinePaint(main_package.getPlot_preferences().getRangeGridColorRGB());
        plot.setDomainGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
        plot.setRangeGridlinesVisible(main_package.getPlot_preferences().isGridlineDefaultState());
        
        if (upperBoundDomain - lowerBoundDomain <= 200 && main_package.getPlot_preferences().isDrawAnnotations() == true) {
	        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
	        	double x = dataset.getXValue(0, x1);
	        	double y = dataset.getYValue(0, x1);
	        	if (maximum_list.contains(x1+min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
	        	}
	        	if (minimum_list.contains(x1+min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
	        	}
	        	if (first_points.contains(x1+min) && !minimum_list.contains(x1+min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
	        	}
	        	if (fifth_points.contains(x1+min) && !minimum_list.contains(x1+min)  && !first_points.contains(x1+min)) {
	        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
	        	}
	        }
		}
        plot.addChangeListener(new PlotChangeListener(){
        	@Override
        	public void plotChanged(PlotChangeEvent event) {
//        		System.out.println("I am called after a zoom event (and some other events too).");
        		if ( (plot.getDomainAxis().getLowerBound() != lowerBoundDomain || plot.getDomainAxis().getUpperBound() != upperBoundDomain) && main_package.getPlot_preferences().isDrawAnnotations() == true) {
        			lowerBoundDomain = plot.getDomainAxis().getLowerBound();
        			upperBoundDomain = plot.getDomainAxis().getUpperBound();
        			if (upperBoundDomain - lowerBoundDomain <= 200) {
        		        for(int x1 = 0; x1 < dataset.getItemCount(0); x1++){
        		        	double x = dataset.getXValue(0, x1);
        		        	double y = dataset.getYValue(0, x1);
        		        	if (maximum_list.contains(x1+min)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMaximumDotColorRGB()));
        		        	}
        		        	if (minimum_list.contains(x1+min)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getMinimumDotColorRGB()));
        		        	}
        		        	if (first_points.contains(x1+min) && !minimum_list.contains(x1+min)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getFirstDotColorRGB()));
        		        	}
        		        	if (fifth_points.contains(x1+min) && !minimum_list.contains(x1+min)  && !first_points.contains(x1+min)) {
        		        		plot.addAnnotation(new XYCircleAnnotation(x, y, 5.0, main_package.getPlot_preferences().getLastDotColorRGB()));
        		        	}
        		        }
        			} else {
        				plot.clearAnnotations();
        			}
        		}
        }});

        if (main_package.getPlot_preferences().isSplineDefaultState() == true) {
			XYSplineRenderer renderer = new XYSplineRenderer();
			renderer.setDefaultShapesVisible(false);
	        renderer.setDefaultShapesFilled(false);
	        renderer.setSeriesPaint(0, main_package.getPlot_preferences().getSeriesColorRGB());
        	renderer.setSeriesStroke(0, new java.awt.BasicStroke(main_package.getPlot_preferences().getLineThickness()));
        } else {
        	XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    		renderer.setDefaultShapesVisible(false);
            renderer.setDefaultShapesFilled(false);
            renderer.setSeriesPaint(0, main_package.getPlot_preferences().getSeriesColorRGB());
        	renderer.setSeriesStroke(0, new java.awt.BasicStroke(main_package.getPlot_preferences().getLineThickness()));
        }
        return chart;
    }
	
	// ACCESSORY PLOT CLASSES
	public class SplineShow implements ActionListener {
	    private JFreeChart chart;
	    private ChartPanel panel;
	    private XYPlot plot;
	    private XYDataset dataset;
	    private XYLineAndShapeRenderer original_render;
		private boolean is_curves_on;
		
		public SplineShow (ChartPanel panel1) {
	        this.panel = panel1;
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
	        this.original_render = (XYLineAndShapeRenderer) plot.getRenderer();
	        this.is_curves_on = false;
//			parent_controller.setPolynonState(!this.is_curves_on);
		}
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			boolean state = this.is_curves_on;
			this.is_curves_on = !state;
			if (this.is_curves_on) {
	  			XYSplineRenderer renderer = new XYSplineRenderer();
	  			renderer.setDefaultShapesVisible(false);
	  	        renderer.setDefaultShapesFilled(false);
		        for (int i = 0; i < this.plot.getDataset().getSeriesCount(); i++) {
			        renderer.setSeriesPaint(i, original_render.getSeriesPaint(i));		        	
		        }
		        this.chart.getXYPlot().setRenderer(renderer);
		        main_package.getPlot_preferences().setSplineDefaultState(true);
			} else {
	  			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	  			renderer.setDefaultShapesVisible(false);
	  	        renderer.setDefaultShapesFilled(false);
		        for (int i = 0; i < this.plot.getDataset().getSeriesCount(); i++) {
			        renderer.setSeriesPaint(i, original_render.getSeriesPaint(i));		        	
		        }
		        this.chart.getXYPlot().setRenderer(renderer);
			}
		}
	}
	
	
	public class GridLinesSwitch implements ActionListener {
	    private JFreeChart chart;
	    private ChartPanel panel;
	    private XYPlot plot;
	    
		public GridLinesSwitch(ChartPanel panel) {
	        this.setPanel(panel);
	        this.chart = panel.getChart();
	        this.plot = (XYPlot) chart.getPlot();
		}
		
		@Override
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (plot.isDomainGridlinesVisible() == true) {
				plot.setDomainGridlinesVisible(false);
				plot.setRangeGridlinesVisible(false);
		        main_package.getPlot_preferences().setGridlineDefaultState(false);
			} else {
				plot.setDomainGridlinesVisible(true);
				plot.setRangeGridlinesVisible(true);
				main_package.getPlot_preferences().setGridlineDefaultState(true);
			}
		}

		public ChartPanel getPanel() {
			return panel;
		}

		public void setPanel(ChartPanel panel) {
			this.panel = panel;
		}
		
	}
}