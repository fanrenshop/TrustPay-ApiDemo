package biz.trustpay.ApiDemo;

import biz.trustpay.api.BillingResult;
import biz.trustpay.api.TrustPayApi;
import biz.trustpay.api.TrustPayApiTrueApi;
import biz.trustpay.api.TrustPayApiActivity;
import biz.trustpay.api.TrustPayCallback;
import biz.trustpay.api.BillingMethods;
import biz.trustpay.api.PricePoints;
import biz.trustpay.helpers.Logger;
import biz.trustpay.helpers.Statics;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

/**
 * 
 */
public class Pay extends Form implements CommandListener, TrustPayCallback {

    private static final String CONST_APP_ID = "ap.317cf227-c4e2-44c1-a333-bd431d62b371";
    //private static final String CONST_APP_ID = "ap.6b30258e-92ac-49d6-b022-fabc650856b5";
    private TextField amount;
    private TextField response;
    private ChoiceGroup istest;
    private TrustPayApi api;
    private String appuser = "AppUser";
    private String txDescription = "AppTxDescription";
    private Command payCommand = new Command("Pay", Command.ITEM, 1);
    private Command updateCommand = new Command("Update", Command.ITEM, 1);
    private Command priceCommand = new Command("Get Price Points", Command.ITEM, 2);
    private ApiDemo midlet;
    
    public Pay(ApiDemo _midlet) {
        super("Pay!");
        midlet = _midlet;

        setTitle("TrustPay payment demo");
        amount = new TextField("Amount to bill:", "", 100, TextField.ANY);
        append(amount);
        istest = new ChoiceGroup("Mode", ChoiceGroup.MULTIPLE);
        istest.append("Is Test", null);
        append(istest);
        response = new TextField("", "", 300, TextField.UNEDITABLE);

        append(response);
        addCommand(payCommand);
        addCommand(updateCommand);
        addCommand(priceCommand);
       
        setCommandListener(this);
        // api = new TrustPayApi(this, midlet, istest.isSelected(0), CONST_APP_ID);
    }

    public void commandAction(Command cmnd, Displayable dsplbl) {
        if (cmnd == payCommand) {
            Bill();
        }
        if (cmnd == priceCommand) {
            PricePoints();
        }
        if (cmnd == updateCommand) {
            Update();
        }
        


    }

    protected boolean onSavePrompt() {
        return true;
    }

    private void Bill() {
        if (api == null) {
            api = new TrustPayApi(this, midlet, istest.isSelected(0), CONST_APP_ID);
            
        }
        Date now = new Date();
        api.requestBilling(amount.getString(), "USD","[*]",appuser,txDescription,"DMO"+now.getTime());
    }

    private void PricePoints() {
        if (api == null) {
            api = new TrustPayApi(this, midlet, istest.isSelected(0), CONST_APP_ID);
        }
        api.getPricePoints();
    }
 private void Update() {
        if (api == null) {
            api = new TrustPayApi(this, midlet, istest.isSelected(0), CONST_APP_ID);
        }
        api.updateBillingMethods();
    }


    public void returnPricePoints(PricePoints pps) {
         String res = "onTrustPayPricePointsResult:";
        if (pps != null) {
            if (pps.status == PricePoints.COUNTRY_NETWORK_UNAVAILABLE) {

                api.displayCountryNetworkSelectionScreen();

            } else {
                res = res + "Price Points:" + pps.toString();
            }
        } 
        append("got result:" + res);
        
    }

    public void returnBillingResult(BillingResult br) {
        String res = "onBillingResult:";
        if (br != null) {
            res = res + "Billing Result:" + br.toString();
        }
        append("got result:" + res);
    }

    public void donePayment() {
        Display.getDisplay(midlet).setCurrent(this);
    }

    public void returnCountryNetwork() {
        
        api.updateBillingMethods();
    }

    public void onUpdateMethods(Boolean b) {
        String res = "onUpdateMethods:";
        if (b != null) {
            res = res + "Update Methods:" + b.toString();
        }
        append("got result:" + res);
    }
}
