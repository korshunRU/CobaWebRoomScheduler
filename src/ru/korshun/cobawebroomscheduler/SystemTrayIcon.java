package ru.korshun.cobawebroomscheduler;

import javafx.application.Platform;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by user on 28.12.2014.
 */
public final class SystemTrayIcon {

    private static TrayIcon trayIcon;


    public static void createIcon() {
        createTrayIcon();
    }


    private static void createTrayIcon() {
        if (SystemTray.isSupported()) {

            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage("res/tray.png");

            final ActionListener closeListener = e -> {

                Platform.exit();
                System.exit(0);
            };


            // create a popup menu
            PopupMenu popup = new PopupMenu();

            MenuItem closeItem = new MenuItem("Выход");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);

            trayIcon = new TrayIcon(image, "CobaWebRoomScheduler", popup);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

}
