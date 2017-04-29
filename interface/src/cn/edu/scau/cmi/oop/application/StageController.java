package cn.edu.scau.cmi.oop.application;

/**
 * ����fxml�ļ�����Ӧ��view������������stage���󼰶�����й���
 *
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
    //����һ��ר�Ŵ洢Stage��Map��ȫ�����ڴ��Stage����

    private HashMap<String, Stage> stages = new HashMap<String, Stage>();
    Systray systray = new Systray();

    /**
     * ��stage����map��
     *
     * @param name
     * @param stage stage����
     */
    public void addStage(String name, Stage stage) {
        stages.put(name, stage);
    }

    /**
     * ��ȡstage����
     *
     * @param name stage����
     * @return
     */
    public Stage getStage(String name) {
        return stages.get(name);
    }

    /**
     * ������stage����
     *
     * @param primaryStageName
     * @param primaryStage
     */
    public void setPrimaryStage(String primaryStageName, Stage primaryStage) {
        this.addStage(primaryStageName, primaryStage);
    }

    /**
     * ���ش��ڵ�ַ����Ҫfxml��Դ�ļ����ڶ����Ĵ��ڲ���Pane������������̳�
     *
     * @param name ע��õ�fxml���ڵ��ļ�
     * @param resources fxml��ַ
     * @param styles �ɱ������initʹ�õĳ�ʼ����ʽ��Դ����
     * @return �Ƿ���سɹ�
     */
    public boolean loadStage(String name, String resources, StageStyle... styles) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resources));
            Pane tempPane = (Pane) loader.load();

            ControlledStage controlledStage = (ControlledStage) loader.getController();
            controlledStage.setStageController(this);

            Scene tempScene = new Scene(tempPane);
            Stage tempStage = new Stage();
            tempStage.setScene(tempScene);

            //tempScene.getStylesheets().add(getClass().getResource("application.css") .toExternalForm());
            for (StageStyle style : styles) {
                tempStage.initStyle(style);
            }

            this.addStage(name, tempStage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ��ʾstage������������stage
     *
     * @param name
     * @return
     */
    public boolean setStage(String name) {
        systray.setcontroller(this);
        systray.setTray(name);
        this.getStage(name).show();

        return true;
    }

    /**
     * ��ʾ������stage
     *
     * @param show ��ʾ��stage
     * @param close ���ص�stage
     * @return
     */
    public boolean setStage(String show, String close) {

        //systray.remove();
        systray.setTray(show);
        getStage(close).close();
        setStage(show);
        return true;
    }
}
