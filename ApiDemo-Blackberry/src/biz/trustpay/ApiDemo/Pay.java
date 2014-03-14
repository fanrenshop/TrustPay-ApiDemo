package biz.trustpay.ApiDemo;

import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.container.MainScreen;
import biz.trustpay.api.BillingResult;
import biz.trustpay.api.PricePoints;
import biz.trustpay.api.TrustPayApi;
import biz.trustpay.api.TrustPayApiActivity;
import biz.trustpay.helpers.BindException;
import biz.trustpay.helpers.Logger;
import biz.trustpay.helpers.Statics;
import java.util.Date;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * 
 */
public class Pay extends MainScreen implements TrustPayApiActivity {

    //private static final String CONST_APP_ID = "ap.2e32dab8-a091-46cb-a5dc-2b756058ca47";
    private static final String CONST_APP_ID = "ap.317cf227-c4e2-44c1-a333-bd431d62b371";
    private BasicEditField amount;
    private BasicEditField response;
    private CheckboxField istest;
    private TrustPayApi fTrustPayApi;
    private String appuser = "AppUser";
    private String txDescription = "Api Demo points";

    public Pay() {
        super();

        setTitle("TrustPay payment demo");
        amount = new BasicEditField("Amount to bill:", "5", 100,
                BasicEditField.EDITABLE);
        add(amount);

        istest = new CheckboxField("Test Mode", false);
        add(istest);



        ButtonField pay = new ButtonField("Pay", ButtonField.CONSUME_CLICK
                | ButtonField.FIELD_RIGHT);
        add(pay);
        pay.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field arg0, int arg1) {

                Bill();
            }
        });

        ButtonField pricepoints = new ButtonField("Get Price Points",
                ButtonField.CONSUME_CLICK | ButtonField.FIELD_RIGHT);
        add(pricepoints);
        pricepoints.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field arg0, int arg1) {

                PricePoints();
            }
        });

        ButtonField update = new ButtonField("Update Billing Methods",
                ButtonField.CONSUME_CLICK | ButtonField.FIELD_RIGHT);
        add(update);
        update.setChangeListener(new FieldChangeListener() {

            public void fieldChanged(Field arg0, int arg1) {

                Update();
            }
        });



        response = new BasicEditField("", "", 400, BasicEditField.READONLY);

        add(response);
        startApi();
        //fTrustPayApi.getForeground();
    }

    protected boolean onSavePrompt() {
        return true;
    }

    private void Bill() {

        fTrustPayApi.setTest(istest.getChecked());
        //api.preparePayIntent(amount.getText(), "ZAR");
        Date now = new Date();
        
        if (!fTrustPayApi.requestBill(amount.getText(), "ZAR", "[*]", appuser, txDescription,"DEM"+now.getTime())) {
            response.setText("Cannot find TrustPay Application");

        }
    }

    private void startApi() {
        try {
            fTrustPayApi = new TrustPayApi(this, CONST_APP_ID, true);

        } catch (BindException ex) {
            ex.printStackTrace();
            response.setText(Statics.getInstance().getDownload());
            TrustPayApi.DownloadTrustPay();
           

        }

    }

    private void PricePoints() {

        fTrustPayApi.setTest(istest.getChecked());
        if (!fTrustPayApi.requestPricePoints()) {
            response.setText("Cannot find TrustPay Application");

        }

    }

    private void Update() {

        fTrustPayApi.setTest(istest.getChecked());
        if (!fTrustPayApi.UpdateBillingMethods()) {
            response.setText("Cannot find TrustPay Application");

        }

    }

    public void onTrustPayPricePointsResult(PricePoints pps) {

        String res = "onTrustPayPricePointsResult:";
        if (pps != null) {
            res = res + "Price Points:" + pps.toString();
        }
        response.setText("got result:" + res);

    }

    public void onBillingResult(BillingResult br) {

        String res = "onBillingResult:";
        if (br != null) {
            res = res + "Billing Result:" + br.toString();
        }
        response.setText("got result:" + res);

    }

    public void onUpdateMethods(Boolean b, int reason) {
        String res = "onUpdateResult:";
        if (b != null) {
            res = res + "Update Result:" + b.toString();
        }
        if (!b.booleanValue()) {
            if (reason == TrustPayApi.NETWORK_ERROR) {
                res = res + " Network error";
            } else if (reason == TrustPayApi.SERVERERROR) {
                res = res + " Network error";
            } else if (reason == TrustPayApi.NOSIMOPERATOR) {
                res = res + " No Sim Operator";
               fTrustPayApi.startMccMnc();
            }
        }
        response.setText("got result:" + res + "\n" + response.getText());
    }

    public void returnCountryNetwork() {
        
        String res = "Got Country";
        
        response.setText("got result:" + res);

    }
    
}
