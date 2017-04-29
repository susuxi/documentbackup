package cn.edu.scau.cmi.oop.application;

import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import cn.edu.scau.cmi.oop.modules.Configuration;
import cn.edu.scau.cmi.oop.modules.InfoOperation;

/**
 * ϵͳ���̿�����
 * 
 * @author susan
 * 
 */

public class Systray {

	StageController stageController;
	private TrayIcon trayIcon;
	private Stage stage;
	// icon ����ͼ��״̬��trueʱΪ�������ã�Ĭ��δ����
	boolean icon = false;
	// FormerMouselistener ��һ����������������������Ƴ���һ��������̿���
	private MouseListener formermouselistener;

	public void setcontroller(StageController stageController) {
		this.stageController = stageController;
	}

	/**
	 * ����ϵͳ����ͼ��
	 * 
	 * @param name
	 *            ��Ҫ��ͼ������Ľ�������
	 */
	public void setTray(String name) {
		stage = stageController.getStage(name);
		enableTray(stage);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				stage.hide();

				ArrayList<Configuration> userinfo = new ArrayList<>();
				String infopath = System.getProperty("user.dir");
				userinfo = InfoOperation.load(infopath);
				for (Configuration c : userinfo) {
					if (c.connected == true)
						c.connected = false;
				}
				InfoOperation.dump(infopath,userinfo);
			}
		});
	}

	/**
	 * ����ϵͳ����ͼ��
	 * 
	 * @param stage
	 */
	private void enableTray(Stage stage) {
		PopupMenu popupMenu = new PopupMenu();
		java.awt.MenuItem openItem = new java.awt.MenuItem("show");
		java.awt.MenuItem hideItem = new java.awt.MenuItem("hide");
		java.awt.MenuItem quitItem = new java.awt.MenuItem("exit");

		ActionListener acl = new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();

				if (item.getLabel().equals("exit")) {
					SystemTray.getSystemTray().remove(trayIcon);
					Platform.exit();
					icon = false;
					return;
				}
				if (item.getLabel().equals("show")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.show();
						}
					});
				}
				if (item.getLabel().equals("hide")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.hide();
						}
					});
				}

			}

		};

		// ���˫���¼�����
		MouseListener sj = new MouseListener() {
			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				Platform.setImplicitExit(false); // ���ʹ����ʾ����������false
				if (e.getClickCount() == 2) {
					if (stage.isShowing()) {
						Platform.runLater(new Runnable() {
							@Override

							public void run() {
								stage.hide();

							}
						});
					} else {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								stage.show();
							}
						});
					}
				}
			}
		};

		openItem.addActionListener(acl);
		quitItem.addActionListener(acl);
		hideItem.addActionListener(acl);

		popupMenu.add(openItem);
		popupMenu.add(hideItem);
		popupMenu.add(quitItem);
		// �״δ򿪽����ǣ�����ϵͳ���̶���
		if (icon == false) {
			try {

				SystemTray tray = SystemTray.getSystemTray();

				BufferedImage image = ImageIO.read(Main.class.getResourceAsStream("/cn/edu/scau/cmi/oop/image/u2.png"));

				trayIcon = new TrayIcon(image, "TimeMachine", popupMenu);
				trayIcon.setImageAutoSize(true);
				trayIcon.setToolTip("TimeMachine");
				tray.add(trayIcon);
				trayIcon.addMouseListener(sj);
				formermouselistener = sj;
				icon = true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// ���̶����Ѵ��ڣ��������̹�������
			trayIcon.setPopupMenu(popupMenu);
			trayIcon.removeMouseListener(formermouselistener);
			formermouselistener = sj;
			trayIcon.addMouseListener(sj);
		}

	}

}
