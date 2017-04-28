package application;
/**
 * @author susan
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.javafx.css.converters.FontConverter.FontWeightConverter;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import modules.Configuration;
import modules.InfoOperation;

public class linkController implements ControlledStage,Initializable{
	@FXML
    private Hyperlink ip ;
	@FXML
	private ImageView link,unlink;	
	@FXML
	private ImageView isLink,setting,append,synchroSetting;	
	@FXML
	private Separator separator; 
	@FXML
	private VBox remPage;
	@FXML
	private AnchorPane linkScene;
	@FXML
	private ScrollPane scroll;
	
	
    StageController myController;
    VBox vb = new VBox();    
    String infopath = System.getProperty("user.dir");    
    ArrayList<Configuration> userinfo = InfoOperation.load(infopath);
    //ArrayList<Node> serverList = new ArrayList<>();   //服务器结点 
    
    public void initialize(URL location, ResourceBundle resources) {
       // scroll.nodeOrientationProperty().bind(serverList);
        setServer();
	}
 
    @Override
    public void setStageController(StageController stageController) {
    	this.myController = stageController;
    } 
	
	public void EnterIp(){
		myController.setStage(Main.operateInterface,Main.linkInterface );
	}

	public void synSetting(){
		myController.setStage(Main.synchroInterface,Main.linkInterface);
	}
	
	public void ipSetting(){
		myController.setStage(Main.login1ID,Main.linkInterface);
	}
	
	public void ipAdd(){
		myController.setStage(Main.login1ID,Main.linkInterface);
	}
	
	public void refresh(){
		clear();
		setServer();
		System.out.println("refreshing...");
	}
	
	public void clear(){
		System.out.println("clearing..");
		vb.getChildren().clear();
	}
	
	public void EnterOperate(){
		myController.setStage(Main.operateInterface,Main.linkInterface );
	}
	
	public void setServerItem(int i){
		Configuration c = userinfo.get(i);
		Pane pane = new Pane();
		Image icontype;
		if(i%2==0)
		pane.setStyle("-fx-background-color:white;");
		pane.setPrefSize(395,45);
		if(c.type.equals("SFTP")){
			icontype = new Image("image/sftp.png");
		}else if(c.type.equals("FTP")){
			icontype = new Image("image/ftp.png");
		}else{
			icontype = new Image("image/local.png");
		}
		ImageView iconftp1 = new ImageView(icontype);
		iconftp1.setFitWidth(45); iconftp1.setFitHeight(40);
		Label label = new Label(c.ip);
		label.setFont(Font.font("Courier",FontWeight.BOLD,15));
		label.setGraphic(iconftp1);
		label.setLayoutX(5); label.setLayoutY(2);
		
		Image icondel = new Image("image/del.png");
		ImageView icondel1 = new ImageView(icondel);
		icondel1.setFitWidth(20); icondel1.setFitHeight(20);
		Pane delpane = new Pane();
		delpane.setPrefSize(20, 20);delpane.getChildren().add(icondel1);
		delpane.setLayoutX(330);delpane.setLayoutY(10);
		
		Image iconlink = new Image("image/link.png");
		ImageView iconlink1 = new ImageView(iconlink);
		iconlink1.setFitWidth(20); iconlink1.setFitHeight(20);
		iconlink1.setLayoutX(360);iconlink1.setLayoutY(10);
		pane.getChildren().addAll(label,delpane,iconlink1);
		vb.getChildren().add(pane);     
		
		iconlink1.setOnMouseClicked(e->{
			myController.setStage(Main.operateInterface,Main.linkInterface );
			c.connected = true;
			InfoOperation.dump(infopath, userinfo);
			
		});

		
		delpane.setOnMouseClicked(e->{
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("确定删除？");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
            	userinfo.remove(userinfo.get(i));
            	InfoOperation.dump(infopath, userinfo);
            	refresh();
            }				
		});
		
		
	}
	
	public void setServer(){
		int i = 0;
		System.out.println(infopath);//***********************************
		userinfo = InfoOperation.load(infopath);
	    for(Configuration c: userinfo){
	    	setServerItem(i);
	    	i++;
	    }
	    InfoOperation.dump(infopath, userinfo);
	    scroll.setContent(vb);
	}
	
	
	
}
