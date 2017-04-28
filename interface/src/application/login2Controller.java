package application;
/**
 * login2加长版登陆界面控制器
 * @author susan
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import javafx.scene.control.SplitMenuButton;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import modules.Configuration;
import modules.InfoOperation;


public class login2Controller implements ControlledStage,Initializable{

	@FXML
	private ImageView icon_mark,icon_server,icon_cloud,icon_file,icon_key;
	@FXML
	private TextField text_ip,text_port,text_user,text_key,text_mark,text_file;
	@FXML
	private Button button_files,button_save;
	@FXML
	private ImageView icon_user;
	@FXML
	private Polygon button_retract;
	@FXML
	private HBox pane;
	
	public ArrayList<Configuration> userinfo = new ArrayList<>();
	StageController myController;
	login1Controller login1;
	String label= new String();
	private String serverType;
	String infopath = System.getProperty("user.dir");
	
	
	
    @Override
    public void setStageController(StageController stageController) {
    	this.myController = stageController;
    }
    
    public void initialize(URL location, ResourceBundle resources) {
    	setServerType();
	}
    
    /*public void init(){
    	text_ip.setText(getIp());
    	text_port.setText(getIp());
    	text_file.setText(getfileadd());
    }*/
    
    /**
     * save按钮点击事件，切换到初次登陆界面
     * @throws IOException
     */
    public void saveButtonClicked()throws IOException{
            newuser();
			myController.setStage(Main.linkInterface,Main.login2ID );
    }
    /**
     * 收回登陆界面
     * @throws IOException
     */
    public void retract()throws IOException{
    	myController.setStage(Main.login1ID,Main.login2ID );

    }
    /**
     * 获取本地同步目录并载入目录路径
     * @throws IOException
     */
    public void filechose()throws IOException{
    	DirectoryChooser chooser = new DirectoryChooser();
    	chooser.setTitle("选择文件夹");
    	File file =chooser.showDialog(myController.getStage("login1"));
    	System.out.println(file);
        String add = file.getAbsolutePath();
        text_file.setText(add);
    }
    /**
     * 获取服务器ip地址
     * @return
     */
    public String getIp(){
    	String ip = text_ip.getText();     	    	
    	return ip;
    }
    
	public int getPort() {
		System.out.println("port inputing...");
		String port = text_port.getText();
		if(port.equals("")) port = "0";
		return Integer.parseInt(port);
	}
    /**
     * 获取手动输入的文件夹路径
     * @return
     */
    public String getWatchingDic(){
    	System.out.println("fileaddress chosing...");
    	String add = text_file.getText();
    	return add; 
    }
    /**
     * 获取输入的用户名
     * @return
     */
    public String getUser(){
    	String user = text_user.getText();
    	return user;
    }
    /**
     * 获取用户输入的密码
     * @return
     */
    public String getPassword(){
    	String passw = text_key.getText();
    	return passw;
    }
    /**
     * 获取输入的备注
     * @return
     */
    public String getmark(){
    	String mark = text_mark.getText();
    	return mark;
    }
    
    public void setServerType() {
		final String[] greetings = new String[] { "SFTP                                                    ","FTP " };
		ChoiceBox<String> choicebox = new ChoiceBox<>(
				FXCollections.observableArrayList("SFTP                                                    ", "FTP "));
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
		Configuration new1 = new Configuration(getServerType().replaceAll(" ",""),getIp(),getUser(),getPassword(),getPort());
		//System.out.println(file.exists());
		new1.watchingpath = getWatchingDic();
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
        for (Configuration c:result)
        System.out.println(c);
	    return true; 		
	}
}
