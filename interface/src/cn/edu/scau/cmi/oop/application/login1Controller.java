package cn.edu.scau.cmi.oop.application;
/**
 * login1��ͨ��½���������
 * @author susan
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.Initializable;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.text.html.HTMLEditorKit.LinkController;

import cn.edu.scau.cmi.oop.modules.Configuration;
import cn.edu.scau.cmi.oop.modules.InfoOperation;
import cn.edu.scau.cmi.oop.application.*;

import javafx.stage.*;

import java.awt.MenuItem;
import java.io.File;

public class login1Controller implements ControlledStage, Initializable {

	@FXML
	private ImageView icon_server, icon_cloud, icon_file;
	@FXML
	private TextField text_ip, text_port, text_fileadd;
	@FXML
	private Button button_files, button_save;
	@FXML
	private Polygon button_spread;
	@FXML
	private HBox pane;
	
	private String serverType = "";
	String label= new String();
	String infopath = System.getProperty("user.dir");
	
	public ArrayList<Configuration> userinfo = new ArrayList<>();
	// login2Controller login2;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
         setServerType();
	}

	StageController myController;

	@Override
	public void setStageController(StageController stageController) {
		this.myController = stageController;
	}

	/**
	 * ��½���л����� ��Ϣ������֤�д�����
	 * 
	 * @throws IOException
	 */
	public boolean saveButtonClicked() throws IOException {
    //������ȡ��ע��----------------------------------------------------------------------
		if(getServerType().equals("")){
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty ServerType!!");
            alert.showAndWait();
			return false;
        }
		if(getIp().equals("")){
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty IP!!");
            alert.showAndWait();
			return false;
		}
		if(getWatchingDic().equals("")){
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty watching dictionary!!");
            alert.showAndWait();
            return false;
		}
		newuser();
			myController.setStage(Main.linkInterface,Main.login1ID );
			
			return true;
	}

	/**
	 * չ����½����
	 * 
	 * @throws IOException
	 */
	public void spread() throws IOException {
		myController.setStage(Main.login2ID, Main.login1ID);

	}

	/**
	 * ��ȡ����ͬ��Ŀ¼
	 * 
	 * @throws IOException
	 */
	public void filechose() throws IOException {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("chose file");
		File file = chooser.showDialog(myController.getStage("login1"));
		String add = file.getAbsolutePath();
		text_fileadd.setText(add);
	}

	public String getIp() {
		//System.out.println("ip inputing...");
		String ip = text_ip.getText();
		return ip;
	}
	
	public int getPort() {
		//System.out.println("port inputing...");
		String port = text_port.getText();
		if(port.equals("")) port = "0";
		System.out.println(port);
		return Integer.parseInt(port);
	}

	public String getWatchingDic() {
		System.out.println("fileaddress chosing...");//***********
		String add = new String();
		add = text_fileadd.getText();
		return add;
	}

	public void setServerType() {
		final String[] greetings = new String[] { "SFTP                                                    ","FTP","local" };
		ChoiceBox<String> choicebox = new ChoiceBox<>(
				FXCollections.observableArrayList("SFTP                                                    ", "FTP","local"));
		choicebox.setPrefWidth(250);
		pane.getChildren().add(choicebox);		
		choicebox.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue ov, Number value, Number new_value) {
            label=greetings[new_value.intValue()];
            serverType = label;
          }
        });
	}
	
	public String getServerType(){
		return serverType;
	}
	
	public boolean newuser(){
		File file = new File(infopath+"\\info.obj");
		Configuration new1 = new Configuration(getServerType().replaceAll(" ",""),getIp(),getPort());
		new1.watchingpath = getWatchingDic();
		//System.out.println(file.exists());
		if(file.exists()){
			userinfo = InfoOperation.load(infopath);	
			System.out.println(userinfo.contains(new1));
			if(userinfo.contains(new1)){
				userinfo.remove(new1);
				userinfo.add(new1);
				InfoOperation.dump(infopath,userinfo);
				return false;
			}
		}
		userinfo.add(new1);
		InfoOperation.dump(infopath,userinfo);
        ArrayList<Configuration> result = InfoOperation.load(infopath);
        for (Configuration c:result)//**************
        System.out.println(c);//**************
	    return true; 
	}
	
}
