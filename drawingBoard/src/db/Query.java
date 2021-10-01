package db;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import util.User;
import util.Util;

public class Query {
	
	public class UserInfoQuery {
		public ResultSet login(String id, String pw) {
			String sql = "SELECT `id` FROM `users` WHERE id = ? AND password = ?";
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setString(1, id);
				pstmt.setString(2, pw);
				rs = pstmt.executeQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rs;
		}

		public boolean join(String id, String pw) {
			String sql = "SELECT `id` FROM `users` WHERE id = ?";
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}

			int count = 0;

			try {
				while (rs.next()) {
					count++;
					User.userId = rs.getString("id");
					System.out.println(User.userId);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (count != 0) {
				return false;
			}

			sql = "INSERT INTO `users`(`id`, `password`) VALUES (?, ?);";
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setString(1, id);
				pstmt.setString(2, pw);
				pstmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}
	}

	public class ImageControlQuery {
		public void updateImage(int currentImageId, Canvas borad) {
			Util util = new Util();
			String sql = "UPDATE images SET image=? WHERE id=?";
			
			try {
				WritableImage image = borad.snapshot(new SnapshotParameters(), null);
				BufferedImage img = new BufferedImage((int)image.getWidth(), (int)image.getHeight(), BufferedImage.TYPE_INT_ARGB);
				SwingFXUtils.fromFXImage(image, img);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(img,"png", os); 
				InputStream is = new ByteArrayInputStream(os.toByteArray());

				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setBinaryStream(1, is);
				pstmt.setInt(2, currentImageId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				util.alert("경고", "저장실패", "");
			} catch (IOException e) {
				e.printStackTrace();
				util.alert("경고", "저장실패", "");
			} catch (Exception e) {
				e.printStackTrace();
				util.alert("경고", "저장실패", "");
			}
		}

		public void addImage(String user_id) {
			String sql = "INSERT INTO `images`(`image`, `user_id`) VALUES (?, ?)";
			try {
				Image image = new Util().createWhiteImage(100,100);
				BufferedImage img = new BufferedImage((int)image.getWidth(), (int)image.getHeight(), BufferedImage.TYPE_INT_ARGB);
				SwingFXUtils.fromFXImage(image, img);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(img,"png", os); 
				InputStream is = new ByteArrayInputStream(os.toByteArray());
			    
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				
				pstmt.setBinaryStream(1, is);
				pstmt.setString(2, user_id);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void deleteImage(int imageId) {
			String sql = "DELETE FROM `images` WHERE id = ?";
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setInt(1, imageId);
				pstmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class GalleryControlQuery{
		public void like(String userId, int imageId) {
			String sql = "SELECT COUNT(id) as count FROM `likes` WHERE user_id = ? AND image_id = ?";
			
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setString(1, userId);
				pstmt.setInt(2, imageId);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					System.out.println(rs.getInt("count"));
					if(rs.getInt("count") > 0) {
						new Util().alert("안내", "추천은 한 번만 누를 수 있습니다", "tip) 비추천도 한 번만 누를 수 있습니다.");
						return;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			sql = "INSERT INTO `likes`(`image_id`, `user_id`) VALUES (?,?)";
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setInt(1, imageId);
				pstmt.setString(2, userId);
				pstmt.executeUpdate();
				
				new Util().alert("안내", "추천 되었습니다.", "tip) 당신이 추천한 작품만 몰아볼 수 있는 기능도 있습니다.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void unlike(String userId, int imageId) {
			String sql = "SELECT COUNT(id) as count FROM `unlikes` WHERE user_id = ? AND image_id = ?";
			
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setString(1, userId);
				pstmt.setInt(2, imageId);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					if(rs.getInt("count") > 0) {
						new Util().alert("안내", "비추천은 한 번만 누를 수 있습니다", "tip) 추천도 한 번만 누를 수 있습니다.");
						return;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			sql = "INSERT INTO `unlikes`(`image_id`, `user_id`) VALUES (?,?)";
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setInt(1, imageId);
				pstmt.setString(2, userId);
				pstmt.executeUpdate();
				
				new Util().alert("안내", "비추천 되었습니다.", "tip) 당신이 추천한 작품만 몰아볼 수 있는 기능도 있습니다.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public int getLikeConut(int imageId) {
			String sql = "SELECT COUNT(id) as count FROM `likes` WHERE image_id = ?";
			
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setInt(1, imageId);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					return rs.getInt("count");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return 0;
		}
		
		public int getUnLikeConut(int imageId) {
			String sql = "SELECT COUNT(id) as count FROM `unlikes` WHERE image_id = ?";
			
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setInt(1, imageId);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					return rs.getInt("count");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return 0;
		}

	}
	
	public class UtilQuery {

		
		
		public int getMaxImageId() {
			String sql = "SELECT MAX(id) FROM `images` WHERE 1;";
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					return rs.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return -1;
		}
		
	}

	public class GetImageQuery {
		public ResultSet getUserImageList(String id, String orderBy) {
			String sql = "SELECT id, image FROM images WHERE user_id=? " + orderBy;
			ResultSet rs = null;

			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rs;
		}

		public ResultSet getImageList(String where, String orderBy) {
			String sql = "SELECT id, image FROM images WHERE 1 " + where + " " + orderBy;
			ResultSet rs = null;

			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				rs = pstmt.executeQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rs;
		}

		public ResultSet getImage(int id) {
			String sql = "SELECT image FROM images WHERE id=?";
			ResultSet rs = null;

			try {
				PreparedStatement pstmt = JDBC.con.prepareStatement(sql);
				pstmt.setInt(1, id);
				rs = pstmt.executeQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return rs;
		}

	}

}
