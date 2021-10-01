package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import db.Query;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.User;
import util.Util;

public class OtherBoard implements Initializable{
	@FXML 
	AnchorPane root;

	@FXML
	Canvas borad;
	@FXML
	Label recommendLable;
	@FXML
	Label unRecommendLable;
	@FXML
	Label scoreLabel;
	
	private GraphicsContext gc;
	
	private Query query = new Query();
	private Query.GalleryControlQuery galleryControlQuery = query.new GalleryControlQuery();
	private Util util = new Util();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = borad.getGraphicsContext2D();
		
//		db에서 이미지 불러오기
		ResultSet rs = query.new GetImageQuery().getImage(User.currentImageId);

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
		int countRecommend = galleryControlQuery.getLikeConut(User.currentImageId);
		int countUnRecommend = galleryControlQuery.getUnLikeConut(User.currentImageId);
		recommendLable.setText("추천 : " + countRecommend);
		unRecommendLable.setText("비추천 : " + countUnRecommend);
		scoreLabel.setText("순추천(추천-비추천) 수 : " + (countRecommend - countUnRecommend));
	}
	
	public void recommendClick() {
		galleryControlQuery.like(User.userId, User.currentImageId);
		newStage("/view/OtherBoard.fxml");
	}
	
	public void unrecommendClick() {
		galleryControlQuery.unlike(User.userId, User.currentImageId);
		newStage("/view/OtherBoard.fxml");
	}

	public void save() {
		util.saveImage(borad);
	}

	public void back() {
		newStage("/view/AllUsers.fxml");
	}
	
	public void newStage(String src) {
		util.newStage(src, root);
	}
}
