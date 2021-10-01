package view;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.Query;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.User;
import util.Util;

public class Login {
	@FXML 
	AnchorPane root;
	
	@FXML
	TextField id;
	@FXML
	TextField password;
	
	public void join() {
		Util util = new Util();
		if(id.getText().length() == 0 || password.getText().length() == 0) {
			util.alert("경고", "가입 실패", "모든 필드에 입력해주세요.");
			return;
		}
		
		if(id.getText().length() > 50 || password.getText().length() > 50) {
			util.alert("경고", "가입 실패", "아이디와 비밀번호는 50자를 넘을 수 없습니다.");
			return;
		}
		
		System.out.println("가입시도");
		if(new Query().new UserInfoQuery().join(id.getText(), password.getText())) {
			User.userId = id.getText();
			User.currentPage = 0;
			System.out.println(User.userId);
			newStage();
		} else {
			util.alert("경고", "가입 실패", "이미 존재하는 아이디 같습니다");
		}
	}
	
	public void login() {
		System.out.println("로그인");
		ResultSet rs = new Query().new UserInfoQuery().login(id.getText(), password.getText());
		int count = 0;
		
		try {
			while(rs.next()) {
				count++;
				User.userId =  rs.getString("id");
				User.currentPage = 0;
				System.out.println(User.userId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(count == 0) {
			new Util().alert("경고", "로그인 실패", "존재하는 아이디가 맞습니까?");
		} else {
			newStage();
		}
	}
	
	public void newStage() {
		new Util().newStage("/view/Main.fxml", root);
	}
}
