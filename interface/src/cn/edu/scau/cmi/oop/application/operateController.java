package cn.edu.scau.cmi.oop.application;
/**
 * @author susan
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.event.ChangeListener;
import org.apache.commons.net.ftp.FTP;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import cn.edu.scau.cmi.oop.modules.Configuration;
import cn.edu.scau.cmi.oop.modules.FtpUtil;
import cn.edu.scau.cmi.oop.modules.InfoOperation;
import cn.edu.scau.cmi.oop.modules.LocalUtil;
import cn.edu.scau.cmi.oop.modules.SFtpUtil;
import cn.edu.scau.cmi.oop.modules.ServerFile;
import cn.edu.scau.cmi.oop.strategy.IndependentWatch;
import cn.edu.scau.cmi.oop.strategy.RealTimeSync;
import cn.edu.scau.cmi.oop.watch.ChangeItem;
import cn.edu.scau.cmi.oop.modules.LocalUtil;
import cn.edu.scau.cmi.oop.modules.SFtpUtil;
import cn.edu.scau.cmi.oop.modules.FtpUtil;
public class operateController<T, S> implements ControlledStage, Initializable {
	@FXML
	private ImageView back, search, up, down, update;
	@FXML
	private ImageView to1, to2, synchro;
	@FXML
	private Text locDirectory, remDirectory;
	@FXML
	private TextField searchDocName, dir;
	@FXML
	AnchorPane operateScene, locFolder, remFolder;
	@FXML
	private TreeView<String> local,remote;
	@FXML
    private Pane addbutton,delbutton; 
	@FXML
	private ScrollPane watchingpane;
	@FXML
	private HBox tree;
	@FXML
	private VBox vb;
	
	
	String remotePath;
	String infopath = System.getProperty("user.dir");
	ArrayList<Configuration> userinfo = InfoOperation.load(infopath);
	ArrayList<ServerFile> l;
	Configuration cur = new Configuration("0", "0");
	ArrayList<String> diclist = new ArrayList<>();
	ArrayList<Pane> panelist = new ArrayList<>();
	
	public void initialize(URL location, ResourceBundle resources) {	
		try {
			synStrategy();
			//buildTree();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	StageController myController;
	LocalUtil localutil = new LocalUtil("D:\\STUDY\\");
	IndependentWatch independentwatch;
	SFtpUtil sftputil;

	public void setremotePath() {

	}

	@Override
	public void setStageController(StageController stageController) {
		this.myController = stageController;
	}

	public void back() {
		myController.setStage(Main.linkInterface, Main.operateInterface);
	}

	public void filechose() throws IOException {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("file chose");
		File file = chooser.showDialog(myController.getStage("operateInterface"));
		String add = file.getAbsolutePath();
		dir.setText(add);
	}
	
	
	
	public void synStrategy() throws IOException{
		getCurServer();
		switch(cur.backupstrategy){
		case "Timing":     RealSynch();break;
		case "一天一次":   timingSyn(86400000);break;
		case "12小时":    timingSyn(43200000);break;
		case "6小时": timingSyn(21600000);break;
		case "2小时": timingSyn(7200000);break;
		case "1小时": timingSyn(3600000);break;
		case "一周一次": timingSyn(604800000);break;
		case "三天一次": timingSyn(259200000);break;
		default :return;
		}
	}
	
	public void setDicItem(int i){
       
		Pane pane = new Pane();
		if(i%2==0)
		pane.setStyle("-fx-background-color:white;");
		pane.setPrefSize(300,20);
		Label label = new Label(diclist.get(i));
		label.setLayoutX(55);
		
		CheckBox cb = new CheckBox();
		cb.setLayoutX(30);cb.setLayoutY(1);
		Image icondel = new Image("cn/edu/scau/cmi/oop/image/del.png");
		ImageView icondel1 = new ImageView(icondel);
		icondel1.setFitWidth(15); icondel1.setFitHeight(15);
		Pane delpane = new Pane();
		delpane.setPrefSize(20, 20);delpane.getChildren().add(icondel1);
		delpane.setLayoutX(8);delpane.setLayoutY(1);
		pane.getChildren().addAll(delpane,cb,label);
		delpane.setOnMouseClicked(e->{   
			diclist.remove(i);	
			setDic();
		});
		
		cb.selectedProperty().addListener((ov, old_val, new_val) -> {  
            getCurServer();
            cur.watchingpath = diclist.get(i);
            System.out.println(cur.watchingpath);
        });  
		
		
		vb.getChildren().add(pane);
		panelist.add(pane);
	}

	public void setDic(){
		System.out.println("set");
		clear();
		int i=0;
		for(String item: diclist){
			setDicItem(i++);
		}
		watchingpane.setContent(vb);

	}
	@FXML
	public void add(){
		if(diclist.isEmpty()){
			userinfo = InfoOperation.load(infopath);
			getCurServer();
			diclist.add(cur.watchingpath);
			setDic();
		}else{
		diclist.add(getPath());
		setDic();}
	}
	
	public void clear(){
		vb.getChildren().clear();
	}

	public String getPath() {
		return dir.getText();
	}

	public boolean getCurServer(){
		
		int f = 0;
		for (Configuration c : userinfo) {
			if (c.connected == true)
				cur = c;
			else{
				System.out.println("Connected Server Not Found,Can Not Synch");
				f++;
			}
		}
		if(f == userinfo.size()) return false;
		else return true;
	}
	
	public boolean timingSyn(long sleepMillis) throws IOException{
		userinfo = InfoOperation.load(infopath);
		if(!getCurServer())     
			return false;
		if(cur.type.equals("SFTP")){
			RealTimeSync watch = new RealTimeSync(cur.watchingpath,
					true, new SFtpUtil(cur.ip, cur.username, cur.password, cur.port), sleepMillis);
			new Thread(watch).start();
			System.out.println("SFTP");//**************
			return true;
		}else if(cur.type.equals("FTP")){
			RealTimeSync watch = new RealTimeSync(cur.watchingpath,
					true, new FtpUtil(cur.ip, cur.username, cur.password, cur.port),sleepMillis);
			new Thread(watch).start();
			System.out.println("ftp");//***************
			return true;
		}else if(cur.type.equals("local")){
			
			RealTimeSync watch = new RealTimeSync(cur.watchingpath,
					true, new LocalUtil(cur.ip),sleepMillis);
			new Thread(watch).start();
			System.out.println("local");//***************
			return true;
		}else{
			System.out.println("Sever Type Error");
			return false;
		}
		
	}
	public boolean RealSynch() throws IOException {
		
		userinfo = InfoOperation.load(infopath);
		if(!getCurServer())     
			return false;
		System.out.println(cur.toString());//******************************
		
		if(cur.watchingpath.equals("")){
			System.out.println("no watching path");
			return false;
		}
		
		if(cur.type.equals("SFTP")){
			RealTimeSync watch = new RealTimeSync(cur.watchingpath,
					true, new SFtpUtil(cur.ip, cur.username, cur.password, cur.port));
			new Thread(watch).start();
			System.out.println("SFTP");
			return true;
		}else if(cur.type.equals("FTP")){
			
			RealTimeSync watch = new RealTimeSync(cur.watchingpath,
					true, new FtpUtil(cur.ip, cur.username, cur.password, cur.port));
			new Thread(watch).start();
			System.out.println("ftp");
			return true;
		}else if(cur.type.equals("local")){
			
			RealTimeSync watch = new RealTimeSync(cur.watchingpath,
					true, new LocalUtil(cur.ip));
			new Thread(watch).start();
			System.out.println("local");
			return true;
		}else{
			System.out.println("Sever Type Error");
			return false;
		}
		

	}

/*	public void getRemoteDic() throws IOException {
		Configuration cur = new Configuration("0", "0");

		userinfo = InfoOperation.load(infopath);
		for (Configuration c : userinfo) {
			if (c.connected == true)
				cur = c;
			else
				System.out.println("Connected Server Not Found");
		}
		switch (cur.type) {
		case "SFTP":
			SFtpUtil sftp = new SFtpUtil(cur.ip, cur.username, cur.password, cur.port);
			sftp.connect();
			l = sftp.ls("");
			sftp.disconnect();
		case "FTP":
			FtpUtil ftp = new FtpUtil(cur.ip, cur.username, cur.password, cur.port);
			ftp.connect();
			l = ftp.ls("");
			ftp.disconnect();
		case "local":
			LocalUtil local = new LocalUtil(cur.ip);
			local.connect();
			l = local.ls("");
			local.disconnect();
		}
	}*/

	/**
	 * ��bug������
	 * @throws IOException 
	 */
/*	public void buildTree() throws IOException {
		ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("folder.png")));
		iv.setFitHeight(15);
		iv.setFitWidth(15);
		getRemoteDic();
		//System.out.println(l);
		File file = new File("D:\\11111");
		TreeView<File> treeView = new TreeView<File>(new MyTreeItem(file));
		treeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
			@Override
			public TreeCell<File> call(TreeView<File> arg0) {
				return new TreeCell<File>() {
					@Override
					protected void updateItem(File f, boolean empty) {
						// TODO Auto-generated method stub
						super.updateItem(f, empty);
						if (empty) {
							setText(null);
							setGraphic(null);
						} else {
							setText(f.getName());
							if (f.isDirectory()) {
								setGraphic(iv);
							}
						}
					}
				};
			}
		});
		tree.getChildren().add(treeView);
		HBox.setHgrow(treeView, Priority.ALWAYS);

	}

	public static void main(String[] args) throws IOException {

	}
	*/
}

/*class MyTreeItem extends TreeItem<File> {
	private boolean notInitialized = true;

	public MyTreeItem(final File file) {
		super(file);
	}

	@Override

	public ObservableList<TreeItem<File>> getChildren() {
		if (notInitialized) {
			notInitialized = false;
			if (getValue().isDirectory()) {
				for (final File file : getValue().listFiles()) {
					super.getChildren().add(new MyTreeItem(file));
				}
			}
		}
		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		return !getValue().isDirectory();
	}
}
*/