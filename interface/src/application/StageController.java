package application;
/**
 * 加载fxml文件及对应的view控制器，生成stage对象及对其进行管理
 * @author susan
 *
 */

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;

public class StageController {
	//建立一个专门存储Stage的Map，全部用于存放Stage对象
	private HashMap<String, Stage> stages = new HashMap<String, Stage>();
    Systray systray=new Systray();
    
    
/**
 * 将stage放入map中
 * @param name
 * @param stage  stage对象
 */
	public void addStage(String name,Stage stage){
		stages.put(name, stage);
	}
	
/**
 * 获取stage对象
 * @param name stage名称
 * @return
 */
	public Stage getStage(String name){
		return stages.get(name);
	}
	
	/**
	 * 保存主stage对象
	 * @param primaryStageName
	 * @param primaryStage
	 */
	public void setPrimaryStage(String primaryStageName,Stage primaryStage){
		this.addStage(primaryStageName, primaryStage);
	}
	
	/**
	 * 加载窗口地址，需要fxml资源文件属于独立的窗口并用Pane容器或其子类继承
	 * @param name         注册好的fxml窗口的文件
	 * @param resources    fxml地址
	 * @param styles       可变参数，init使用的初始化样式资源设置  
	 * @return             是否加载成功
	 */
	public boolean loadStage(String name,String resources,StageStyle... styles){
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource(resources));
			Pane tempPane = (Pane) loader.load();
			
			ControlledStage controlledStage = (ControlledStage)loader.getController();
			controlledStage.setStageController(this);
			
			Scene tempScene = new Scene(tempPane);
			Stage tempStage = new Stage();
			tempStage.setScene(tempScene);
			
			//tempScene.getStylesheets().add(getClass().getResource("application.css") .toExternalForm());
			for(StageStyle style : styles){
				tempStage.initStyle(style);
			}
			
			this.addStage(name, tempStage);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 显示stage而不隐藏其他stage
	 * @param name
	 * @return
	 */
	public boolean setStage(String name){
		systray.setcontroller(this);
		systray.setTray(name);
		this.getStage(name).show();

		return true;
	}
	
	/**
	 * 显示并隐藏stage
	 * @param show 显示的stage
	 * @param close 隐藏的stage
	 * @return
	 */
	public boolean setStage(String show,String close){

		//systray.remove();
		
		systray.setTray(show);
		getStage(close).close();
		setStage(show);
		return true;
	}
}
