package cn.edu.scau.cmi.oop.application;

/**
 * login2�ӳ����½���������
 *
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import javafx.scene.control.SplitMenuButton;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import cn.edu.scau.cmi.oop.modules.Configuration;
import cn.edu.scau.cmi.oop.modules.InfoOperation;

public class login2Controller implements ControlledStage, Initializable {

    @FXML
    private ImageView icon_mark, icon_server, icon_cloud, icon_file, icon_key;
    @FXML
    private TextField text_ip, text_port, text_user, text_key, text_mark, text_file;
    @FXML
    private Button button_files, button_save;
    @FXML
    private ImageView icon_user;
    @FXML
    private Polygon button_retract;
    @FXML
    private HBox pane;

    public ArrayList<Configuration> userinfo = new ArrayList<>();
    StageController myController;
    login1Controller login1;
    String label = new String();
    private String serverType="";
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
     * save��ť����¼����л������ε�½����
     *
     * @throws IOException
     */
    public boolean saveButtonClicked() throws IOException {
        if (getServerType().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty ServerType!!");
            alert.showAndWait();
            return false;
        }
        if (getIp().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty IP!!");
            alert.showAndWait();
            return false;
        }
        if (getWatchingDic().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Empty watching dictionary!!");
            alert.showAndWait();
            return false;
        }
        newuser();
        myController.setStage(Main.linkInterface, Main.login2ID);
        return true;
    }

    /**
     * �ջص�½����
     *
     * @throws IOException
     */
    public void retract() throws IOException {
        myController.setStage(Main.login1ID, Main.login2ID);

    }

    /**
     * ��ȡ����ͬ��Ŀ¼������Ŀ¼·��
     *
     * @throws IOException
     */
    public void filechose() throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("chose file");
        File file = chooser.showDialog(myController.getStage("login1"));
        System.out.println(file);
        String add = file.getAbsolutePath();
        text_file.setText(add);
    }

    /**
     * ��ȡ������ip��ַ
     *
     * @return
     */
    public String getIp() {
        String ip = text_ip.getText();
        return ip;
    }

    public int getPort() {
        //System.out.println("port inputing...");
        String port = text_port.getText();
        if (port.equals("")) {
            port = "0";
        }
        return Integer.parseInt(port);
    }

    /**
     * ��ȡ�ֶ�������ļ���·��
     *
     * @return
     */
    public String getWatchingDic() {
        //System.out.println("fileaddress chosing...");
        String add = text_file.getText();
        return add;
    }

    /**
     * ��ȡ������û���
     *
     * @return
     */
    public String getUser() {
        String user = text_user.getText();
        return user;
    }

    /**
     * ��ȡ�û����������
     *
     * @return
     */
    public String getPassword() {
        String passw = text_key.getText();
        return passw;
    }

    /**
     * ��ȡ����ı�ע
     *
     * @return
     */
    public String getmark() {
        String mark = text_mark.getText();
        return mark;
    }

    public void setServerType() {
        final String[] greetings = new String[]{"SFTP                                                    ", "FTP "};
        ChoiceBox<String> choicebox = new ChoiceBox<>(
                FXCollections.observableArrayList("SFTP                                                    ", "FTP "));
        choicebox.setPrefWidth(250);
        pane.getChildren().add(choicebox);
        choicebox.getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue ov, Number value, Number new_value) {
                        label = greetings[new_value.intValue()];
                        serverType = label;
                    }
                });
    }

    public String getServerType() {
        return serverType;
    }

    public boolean newuser() {
        File file = new File(infopath + "\\info.obj");
        Configuration new1 = new Configuration(getServerType().replaceAll(" ", ""), getIp(), getUser(), getPassword(), getPort());
        //System.out.println(file.exists());
        new1.watchingpath = getWatchingDic();
        if (file.exists()) {
            userinfo = InfoOperation.load(infopath);
            System.out.println(userinfo.contains(new1));
            if (userinfo.contains(new1)) {
                userinfo.remove(new1);
                userinfo.add(new1);
                InfoOperation.dump(infopath, userinfo);
                return false;
            }
        }
        userinfo.add(new1);
        InfoOperation.dump(infopath, userinfo);
        ArrayList<Configuration> result = InfoOperation.load(infopath);
        for (Configuration c : result) {
            System.out.println(c);
        }
        return true;
    }
}
