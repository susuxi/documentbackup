package cn.edu.scau.cmi.oop.application;

import java.awt.Cursor;
import java.io.IOException;
/**
 * @author susan
 */
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jcraft.jsch.UserInfo;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import cn.edu.scau.cmi.oop.modules.Configuration;
import cn.edu.scau.cmi.oop.modules.InfoOperation;
import cn.edu.scau.cmi.oop.modules.ServerUtil;
import cn.edu.scau.cmi.oop.strategy.RealTimeSync;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;

public class synchroController implements ControlledStage, Initializable {
	@FXML
	private AnchorPane synchroScene, partset;
	@FXML
	private ImageView back;
	@FXML
	private Text grobalConf, change, totalset;
	@FXML
	private Hyperlink totalsetSwitch;
	@FXML
	private Pane timingchoice,savechoice,serverpane;
	@FXML
	private RadioButton realTime, manual, timing, persave, reserve;

	final ToggleGroup group = new ToggleGroup();
    String infopath = System.getProperty("user.dir");    
    ArrayList<Configuration> userinfo = InfoOperation.load(infopath);
    
	private String timinginterval;
	private String savinginterval;
	private int synchroserverindex;
	private Path localPath;
	String label= new String();
	
	
	
	
	public void initialize(URL location, ResourceBundle resources) {
        backup();
        save();
        settings();

	}

	StageController myController;

	@Override
	public void setStageController(StageController stageController) {
		this.myController = stageController;
	}

	public void back() {
		myController.setStage(Main.linkInterface, Main.synchroInterface);
	}

	public void toPartset() {
		totalset.setVisible(false);
		totalsetSwitch.setVisible(false);
		partset.setVisible(true);
                partSet();
                
	}

	public void toTotalset() {
		partset.setVisible(false);
		totalset.setVisible(true);
		totalsetSwitch.setVisible(true);
	}

	public void getPartIp() {
		//String ip = ipaddress.getText();
	}
	
	public void setBackupStrategy(){
		if(partset.isVisible()){
			userinfo.get(synchroserverindex).backupstrategy = timinginterval;
			
		}else{
			for(Configuration c:userinfo){
				c.backupstrategy = timinginterval;
			}
		}
		InfoOperation.dump(infopath, userinfo);
	}
	
	public void setSavingStrategy(){
		if(partset.isVisible()){
			userinfo.get(synchroserverindex).savestrategy = savinginterval;
		}else{
			for(Configuration c:userinfo){
				c.savestrategy = savinginterval;
			}
		}
		InfoOperation.dump(infopath, userinfo);
	}
	


	public void settings() {

		final ToggleGroup group1 = new ToggleGroup();
		final ToggleGroup group2 = new ToggleGroup();
		realTime.setToggleGroup(group1);
		manual.setToggleGroup(group1);
		timing.setToggleGroup(group1);
		persave.setToggleGroup(group2);
		reserve.setToggleGroup(group2);
		
		
	}
	
	public void backup(){
		final String[] greetings = new String[] {
				"一天一次","每12小时","每6小时","每2小时",
				"每1小时","一周一次","三天一次"
		};
		ObservableList<String> timings = FXCollections.observableArrayList(
				"一天一次","每12小时","每6小时","每2小时",
				"每1小时","一周一次","三天一次"); 
		ChoiceBox<String> choicebox = new ChoiceBox<>(timings);
		timingchoice.getChildren().add(choicebox);		
		choicebox.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue ov, Number value, Number new_value) {
            label=greetings[new_value.intValue()];
            timinginterval = label;
            setBackupStrategy();
          }
        });
		
	}
	public void save(){
		final String[] greetings = new String[] {
				"一年","半年","三个月","一个月","一周"};
		ObservableList<String> saving = FXCollections.observableArrayList(
				"一年","半年","三个月","一个月","一周");
		ChoiceBox<String> choicebox2 = new ChoiceBox<>(saving);
		savechoice.getChildren().add(choicebox2);
		choicebox2.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue ov, Number value, Number new_value) {
            label=greetings[new_value.intValue()];
            savinginterval = label;
            setSavingStrategy();
          }
        });
		
	}
	
	public void partSet(){
                userinfo = InfoOperation.load(infopath);
		final String[] greetings = new String[userinfo.size()];
		ObservableList<String> servers = FXCollections.observableArrayList();
		int i=0;
		for(Configuration c: userinfo){
			servers.add(c.ip);
		    greetings[i] = c.ip;
		}
		ChoiceBox<String> choicebox3 = new ChoiceBox<>(servers);
		serverpane.getChildren().add(choicebox3);
		choicebox3.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue ov, Number value, Number new_value) {
            label=greetings[new_value.intValue()];
            //System.out.println(label);
            synchroserverindex = new_value.intValue();
           // System.out.println(synchroserverindex);
          }
        });
	}
	
	public String getTimingInterval(){
		System.out.println(timinginterval);
		return timinginterval;
	}
	
	public String getSavingInterval(){
		//System.out.println(savinginterval);
		return savinginterval;
	}
 
	
}

