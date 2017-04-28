package application;

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
import modules.Configuration;
import modules.InfoOperation;

/**
 * 系统托盘控制类
 * 
 * @author susan
 * 
 */

public class Systray {

	StageController stageController;
	private TrayIcon trayIcon;
	private Stage stage;
	// icon 托盘图标状态，true时为托盘启用，默认未启用
	boolean icon = false;
	// FormerMouselistener 上一个界面的鼠标监听器，用于移除上一界面的托盘控制
	private MouseListener formermouselistener;

	public void setcontroller(StageController stageController) {
		this.stageController = stageController;
	}

	/**
	 * 启用系统托盘图标
	 * 
	 * @param name
	 *            需要与图标关联的界面名称
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
	 * 配置系统托盘图标
	 * 
	 * @param stage
	 */
	private void enableTray(Stage stage) {
		PopupMenu popupMenu = new PopupMenu();
		java.awt.MenuItem openItem = new java.awt.MenuItem("显示");
		java.awt.MenuItem hideItem = new java.awt.MenuItem("最小化");
		java.awt.MenuItem quitItem = new java.awt.MenuItem("退出");

		ActionListener acl = new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				java.awt.MenuItem item = (java.awt.MenuItem) e.getSource();

				if (item.getLabel().equals("退出")) {
					SystemTray.getSystemTray().remove(trayIcon);
					Platform.exit();
					icon = false;
					return;
				}
				if (item.getLabel().equals("显示")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.show();
						}
					});
				}
				if (item.getLabel().equals("最小化")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.hide();
						}
					});
				}

			}

		};

		// 鼠标双击事件方法
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
				Platform.setImplicitExit(false); // 多次使用显示和隐藏设置false
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
		// 首次打开界面是，创建系统托盘对象
		if (icon == false) {
			try {

				SystemTray tray = SystemTray.getSystemTray();

				BufferedImage image = ImageIO.read(Main.class.getResourceAsStream("u2.png"));

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
			// 托盘对象已存在，更改托盘关联界面
			trayIcon.setPopupMenu(popupMenu);
			trayIcon.removeMouseListener(formermouselistener);
			formermouselistener = sj;
			trayIcon.addMouseListener(sj);
		}

	}

}
