package biz.trustpay.ApiDemo;

import biz.trustpay.helpers.GetSimInfo;
import biz.trustpay.helpers.Logger;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class ApiDemo extends MIDlet {

    private Display display;
    private Pay pay;
    public boolean isPaused = false;

    protected void destroyApp(boolean bln) throws MIDletStateChangeException {
        Logger.Log(Logger.DEBUG, "!!!!!!!!!!!TrustPayApiDemo Destroyed!!!!!!!!!!!!!");
    }

    protected void pauseApp() {
        Logger.Log(Logger.DEBUG, "!!!!!!!!!!TrustPayApiDemo Pausing!!!!!!!!!!");
        isPaused = true;
    }

    protected void startApp() throws MIDletStateChangeException {
        isPaused = false;
        Logger.Log(Logger.DEBUG, "!!!!!!!!!!!TrustPayApiDemo Starting!!!!!!!!!!!!");
        if (pay == null) {
            display = Display.getDisplay(this);
            pay = new Pay(this);
            display.setCurrent(pay);
        } else {
            display.setCurrent(pay);
        }

        Logger.Log(Logger.DEBUG, "Is this shown : " + pay.isShown());

    }
}
