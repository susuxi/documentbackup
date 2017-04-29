package cn.edu.scau.cmi.oop.application;
	


import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import cn.edu.scau.cmi.oop.modules.FtpUtil;
import cn.edu.scau.cmi.oop.modules.SFtpUtil;


public class Main extends Application {

	public static String login1ID = "login1";
	public static String login1Res = "/login1.fxml";
	
	public static String login2ID = "login2";
	public static String login2Res = "/login2.fxml";
	

	public static String linkInterface = "linkInterface";
	public static String linkInterfaceRes = "/linkInterface.fxml";
	public static String operateInterface = "operateInterface";
	public static String operateInterfaceRes = "/operateInterface.fxml";
	public static String synchroInterface = "synchroInterface";
	public static String synchroInterfaceRes = "/synchroInterface.fxml";
	
	private StageController stageController;

	
	@Override	
	public void start(Stage primaryStage) {
		try {
			stageController = new StageController();
			
			stageController.setPrimaryStage("primaryStage", primaryStage);
			
			stageController.loadStage(login1ID, login1Res);
			stageController.loadStage(login2ID, login2Res);
			stageController.loadStage(linkInterface, linkInterfaceRes);
			stageController.loadStage(operateInterface, operateInterfaceRes);
			stageController.loadStage(synchroInterface, synchroInterfaceRes);
			
			stageController.setStage(Main.login1ID);
			Platform.setImplicitExit(false); //���ʹ����ʾ����������false
 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkUpdate(){
		
	}
	
	public static void main(String[] args) throws IOException {
		launch(args);
	
		
		
	}
}
