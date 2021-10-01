package view;

import java.awt.FileDialog;
import java.awt.TextArea;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.omg.CORBA.Current;

import db.Query;
import db.Query.GetImageQuery;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.User;
import util.Util;

public class Board implements Initializable {
	@FXML 
	AnchorPane root;
	
	@FXML
	Canvas borad;
	@FXML
	ColorPicker colorPicker;
	@FXML
	TextField lineSize;
	@FXML
	TextField fontSize;
	
	@FXML
	TextField ImagePointX;
	@FXML
	TextField ImagePointY;
	@FXML
	TextField ImageSizeX;
	@FXML
	TextField ImageSizeY;
	
	
	@FXML
	Button addTextBtn;
	
	@FXML
	ComboBox<String> linetype;

	private boolean isMouseDown = false;
	private int[] beforeMousePoint = { 0, 0 };
	private GraphicsContext gc;
	private ObservableList<String> toolList = FXCollections.observableArrayList("실선", "점선");
	private TextField userInputText;
	
	private Util util = new Util();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		linetype.setItems(toolList);
		
		gc = borad.getGraphicsContext2D();

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, 101230, 102340);

		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

		colorPicker.setValue(Color.BLACK);
		
//		db에서 이미지 불러오기
		ResultSet rs = new Query().new GetImageQuery().getImage(User.currentImageId);

		try {
			while(rs.next()) {
				InputStream is = rs.getBlob("image").getBinaryStream();
				BufferedImage bufimg;
				bufimg = ImageIO.read(is);
				Image img = SwingFXUtils.toFXImage(bufimg, null);
				gc.drawImage(img, 0, 0, 858, 534);
			}
		} catch (SQLException e) {
			util.alert("경고", "이미지를 불러올 수 없습니다.", "");
			e.printStackTrace();
		} catch (IOException e) {
			util.alert("경고", "이미지를 불러올 수 없습니다.", "");
		}
		
	}
	
	public void setNumericField() {
		ArrayList<TextField> textFieldList = new ArrayList<TextField>();
		
		textFieldList.add(fontSize);
		textFieldList.add(lineSize);
		textFieldList.add(ImagePointX);
		textFieldList.add(ImagePointY);
		textFieldList.add(ImageSizeX);
		textFieldList.add(ImageSizeY);
		
		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).textProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			        if (!newValue.matches("\\d*")) {
			        	fontSize.setText(newValue.replaceAll("[^\\d]", ""));
			        }
			    }
			});
		}
	}

	public void canvasBeginpath() {
		gc.beginPath();
	}

	public void canvasDrawLine(Event e) {
		gc.lineTo(((MouseEvent) e).getSceneX() - 20, ((MouseEvent) e).getSceneY() - 10);
		gc.stroke();
	}

	public void changeColor() {
		gc.setStroke(colorPicker.getValue());
	}

	public void lineSizeChange() {
		try {
			gc.setLineWidth(Integer.parseInt(lineSize.getText()));
		} catch (Exception e) {
			util.alert("경고", "변환할 수 없는 입력입니다.", "다시 입력해주세요");
		}
	}

	public void back() {
		newStage("/view/Main.fxml");
	}

	public void save() {
		util.saveImage(borad);
	}
	
	public void addImage() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png");
		
		fileChooser.getExtensionFilters().add(imageFilter);
		File file = fileChooser.showOpenDialog(borad.getScene().getWindow());
		
		Image image = new Image(file.toURI().toString());
		try {
			gc.drawImage(image, Integer.parseInt(ImagePointX.getText()), Integer.parseInt(ImagePointY.getText()),
					Integer.parseInt(ImageSizeX.getText()), Integer.parseInt(ImageSizeY.getText()));
		} catch (Exception e) {
			util.alert("경고", "무언가 잘못되었습니다.", "입력값을 확인해주세요");
		}
	}
	
	public void dbSave() {
		new Query().new ImageControlQuery().updateImage(User.currentImageId, borad);
	}

	public void newStage(String src) {
		util.newStage(src, root);
	}
	
	public void delete() {
		new Query().new ImageControlQuery().deleteImage(User.currentImageId);
		util.alert("안내", "삭제 되었습니다.", "");
		User.currentPage = 0;
		newStage("/view/Main.fxml");
	}
	
	public void chagneLineType() {
		if(linetype.getValue() == null) {
			return;
		}
		
		if(linetype.getValue().equals("점선")) {
			gc.setLineDashes(10);
		} else if(linetype.getValue().equals("실선")) {
			gc.setLineDashes(0);
		}
	}
	
	public void createTextField() {
		userInputText = new TextField();
		userInputText.setLayoutX(10);
		userInputText.setLayoutY(10);
		
		userInputText.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event)->{
			userInputText.setLayoutX(event.getSceneX());
			userInputText.setLayoutY(event.getSceneY());
		});
				
		root.getChildren().add(userInputText);
	}
	
	public void filltext() {
		try {
	        gc.setFill(colorPicker.getValue());
	        gc.setFont(new Font("sans-serif", Double.parseDouble(fontSize.getText())));
			gc.fillText(userInputText.getText(), userInputText.getLayoutX(), userInputText.getLayoutY());
			root.getChildren().remove(userInputText);
		} catch (Exception e) {
			util.alert("경고", "잘못 된 폰크 크기 입니다.", "폰트 크기는 실수만 입력 가능합니다.");
		}
	}
	
	public void addText() {
		if(addTextBtn.getText().equals("텍스트 입력하기")) {
			addTextBtn.setText("텍스트 입력 완료");
			createTextField();
		} else {
			addTextBtn.setText("텍스트 입력하기");
			filltext();
		}
	}
	
	public void fontsizeField() {
		
	}
}
