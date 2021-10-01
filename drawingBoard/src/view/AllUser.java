package view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import db.Query;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.User;
import util.Util;

public class AllUser implements Initializable {
	@FXML 
	AnchorPane root;

	@FXML
	ImageView image1;
	@FXML
	ImageView image2;
	@FXML
	ImageView image3;
	@FXML
	ImageView image4;
	@FXML
	ImageView image5;
	@FXML
	ImageView image6;
	@FXML
	ImageView image7;
	@FXML
	ImageView image8;
	@FXML
	ImageView image9;
	@FXML
	ImageView image10;

	@FXML
	Button btn1;

	@FXML
	ComboBox<String> sortCombobox;
	@FXML
	ComboBox<String> filterCombobox;

	private ObservableList<String> sortTypeList = FXCollections.observableArrayList("추천 갯수", "비추천 갯수", "순추천 수");
	private ObservableList<String> filterTypelist = FXCollections.observableArrayList("내가 추천한 작품만", "내가 비추천한 작품만", "내가 그린 작품만");

	ArrayList<Integer> imageIdList = new ArrayList<Integer>();
	ArrayList<Image> imageList = new ArrayList<Image>();
	ArrayList<ImageView> ImageViewList = new ArrayList<ImageView>();
	ArrayList<Integer> idList = new ArrayList<Integer>();

	int limit = 0;
	
	private Query query = new Query();
	private Util util = new Util();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sortCombobox.setItems(sortTypeList);
		filterCombobox.setItems(filterTypelist);
		
		ImageViewList.add(image1);
		ImageViewList.add(image2);
		ImageViewList.add(image3);
		ImageViewList.add(image4);
		ImageViewList.add(image5);
		ImageViewList.add(image6);
		ImageViewList.add(image7);
		ImageViewList.add(image8);
		ImageViewList.add(image9);
		ImageViewList.add(image10);
		
		ResultSet rs = query.new GetImageQuery().getImageList(User.currentViewType, User.currentSortType);

		try {
			while (rs.next()) {
				InputStream is = rs.getBlob("image").getBinaryStream();
				BufferedImage bufimg;
				bufimg = ImageIO.read(is);
				imageIdList.add(rs.getInt("id"));
				imageList.add(SwingFXUtils.toFXImage(bufimg, null));
				limit++;
			}
			System.out.println(limit + "개 불러오기 완료");
			System.out.println("현재 페이지 : " + User.currentPage);
			for (int i = User.currentPage * 10; i < User.currentPage * 10 + 10; i++) {
				if (i >= limit) {
					break;
				}
				ImageViewList.get(i - User.currentPage * 10).setImage(imageList.get(i));
				idList.add(imageIdList.get(i));
			}
		} catch (SQLException e) {
			util.alert("경고", "이미지를 불러올 수 없습니다.", "");
		} catch (Exception e) {
			util.alert("경고", "이미지를 불러올 수 없습니다.", "");
		}
	}

	public void open1() {
		User.currentImageId = idList.get(0);
		newStage("/view/OtherBoard.fxml");
	}

	public void open2() {
		User.currentImageId = idList.get(1);
		newStage("/view/OtherBoard.fxml");
	}

	public void open3() {
		User.currentImageId = idList.get(2);
		newStage("/view/OtherBoard.fxml");
	}

	public void open4() {
		User.currentImageId = idList.get(3);
		newStage("/view/OtherBoard.fxml");
	}

	public void open5() {
		User.currentImageId = idList.get(4);
		newStage("/view/OtherBoard.fxml");
	}

	public void open6() {
		User.currentImageId = idList.get(5);
		newStage("/view/OtherBoard.fxml");
	}

	public void open7() {
		User.currentImageId = idList.get(6);
		newStage("/view/OtherBoard.fxml");
	}

	public void open8() {
		User.currentImageId = idList.get(7);
		newStage("/view/OtherBoard.fxml");
	}

	public void open9() {
		User.currentImageId = idList.get(8);
		newStage("/view/OtherBoard.fxml");
	}

	public void open10() {
		User.currentImageId = idList.get(9);
		newStage("/view/OtherBoard.fxml");
	}

	public void newStage(String src) {
		util.newStage(src, root);
	}

	public void goMain() {
		User.currentPage = 0;
		newStage("/view/Main.fxml");
	}

	public void back() {
		if (User.currentPage <= 0) {
			util.alert("경고", "페이지를 이동 할 수 없습니다.", "더 이상 뒤로 갈 수 있는 페이지가 없습니다.");
			return;
		}
		User.currentPage -= 1;
		newStage("/view/AllUsers.fxml");
	}

	public void next() {
		System.out.println(User.currentPage + " " + limit);
		if ((User.currentPage + 1) * 10 > limit) {
			util.alert("경고", "페이지를 이동 할 수 없습니다.", "더 이상 앞으로 갈 수 있는 페이지가 없습니다.");
		} else {
			User.currentPage += 1;
			newStage("/view/AllUsers.fxml");
		}
	}

	public void resort() {
		util.changeSortType(sortCombobox.getValue() != null ? sortCombobox.getValue() : "");
		util.changeGridView(filterCombobox.getValue() != null ? filterCombobox.getValue() : "");
		newStage("/view/AllUsers.fxml");
	}
}
