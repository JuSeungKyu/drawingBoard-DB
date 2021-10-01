package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Util {
	public void alert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}
	
	public void changeSortType(String type) {
		User.currentSortType = "";
		System.out.println(type);
		if(type.equals("추천 갯수")) {
			User.currentSortType = "ORDER BY (SELECT COUNT(id) FROM likes WHERE likes.image_id = images.id) DESC";
		} else if(type.equals("비추천 갯수")) {
			User.currentSortType = "ORDER BY (SELECT COUNT(id) FROM unlikes WHERE unlikes.image_id = images.id) DESC";
		} else if(type.equals("순추천 수")) {
			User.currentSortType = "ORDER BY (SELECT COUNT(id) FROM likes WHERE likes.image_id = images.id) - (SELECT COUNT(id) FROM unlikes WHERE unlikes.image_id = images.id) DESC;";
		}
		
		User.currentPage = 0;
	}
	
	public void changeGridView(String type) {
		User.currentViewType = "";
		
		if(type.equals("내가 추천한 작품만")) {
			User.currentViewType = "AND images.id in (select likes.image_id from likes where likes.user_id = '"+ User.userId +"')";
		} else if(type.equals("내가 비추천한 작품만")) {
			User.currentViewType = "AND images.id in (select unlikes.image_id from unlikes where unlikes.user_id = '"+ User.userId +"')";
		} else if(type.equals("내가 그린 작품만")) {
			User.currentViewType = "AND images.user_id = " + User.userId;
		}
		
		User.currentPage = 0;
	}
	
	public Image createWhiteImage(int width, int height) {
	    WritableImage img = new WritableImage(width, height);
	    PixelWriter pw = img.getPixelWriter();

	    int pixel = (255 << 24) | (255 << 16) | (255 << 8) | 255 ;
	    int[] pixels = new int[width * height];
	    Arrays.fill(pixels, pixel);

	    pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
	    return img;
	}
	
	public void saveImage(Canvas board) {
		WritableImage image = board.snapshot(new SnapshotParameters(), null);
		
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png");
		
		fileChooser.getExtensionFilters().add(imageFilter);
		File file = fileChooser.showSaveDialog(board.getScene().getWindow());
		if (file != null) {
			try {
				FileWriter writer = null;
				writer = new FileWriter(file);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		try {
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			this.alert("알림", "저장되었습니다.", "- 친절한 프로그래머 -");
		} catch (Exception s) {
			this.alert("경고", "파일 저장 실패", "올바른 경로가 맞습니까?");
		}
	}
	
	public void newStage(String src, AnchorPane pane) {
		System.out.println(src);
		Stage newStage = new Stage();
		Stage stage = (Stage) pane.getScene().getWindow();
		try {
			Parent main = FXMLLoader.load(getClass().getResource(src));
			Scene sc = new Scene(main);
			newStage.setTitle("Drawing Board App");
			newStage.setScene(sc);
			newStage.show();
			stage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
