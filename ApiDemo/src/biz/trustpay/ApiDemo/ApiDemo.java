package biz.trustpay.ApiDemo;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import biz.trustpay.api.BillingResult;
import biz.trustpay.api.PricePointListener;
import biz.trustpay.api.PricePoints;
import biz.trustpay.api.TrustPayApi;

public class ApiDemo extends Activity implements PricePointListener {

	private static final String CONST_APP_ID = "ap.317cf227-c4e2-44c1-a333-bd431d62b371";
	TrustPayApi fTrustPayApi;
	String appuser = "AppUser";
	String txDescription = "Demo Application credits";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_text)
				.setCancelable(false)
				.setTitle(R.string.dialog_title)
				.setPositiveButton("Done",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		setUpMainForm();
		fTrustPayApi = new TrustPayApi(this, CONST_APP_ID);
		fTrustPayApi.setPricePointListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		fTrustPayApi.doBindService(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		fTrustPayApi.doUnbindService();
	}

	public void setUpMainForm() {
		Button pay = (Button) findViewById(R.id.pay_button);
		pay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText et = (EditText) findViewById(R.id.editText1);
				et.setText("");

				CheckBox t = (CheckBox) findViewById(R.id.test_mode);
				EditText a = (EditText) findViewById(R.id.amount);
				EditText c = (EditText) findViewById(R.id.currency);
				String amountStr = a.getText().toString();
				String curencyStr = c.getText().toString();
				pay(amountStr, curencyStr, t.isChecked());

			}
		});

		Button prices = (Button) findViewById(R.id.price_points_button);
		prices.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				EditText et = (EditText) findViewById(R.id.editText1);
				et.setText("");
				if (!getPricePoints()) {
					DownloadTrustPay();

				}
			}
		});
		
		Button update = (Button) findViewById(R.id.update_button);
		update.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				EditText et = (EditText) findViewById(R.id.editText1);
				et.setText("");
				if (!updateBillingMethods()) {
					DownloadTrustPay();

				}
			}
		});

	}

	private void DownloadTrustPay() {
		TrustPayApi.downloadTrustPay(this);
	}

	private void startMccMnc() {
		//Select your region because API could not get it from the SIM card
		Intent intent = fTrustPayApi.prepareMccIntent();
		try {
			startActivityForResult(intent, 321);
		} catch (ActivityNotFoundException e) {
			DownloadTrustPay();

		}
	}

	public boolean getPricePoints() {
		return fTrustPayApi.requestPricePoints();
	}

	public boolean updateBillingMethods() {
		return fTrustPayApi.requestUpdate();
	}

	public void pay(String amount, String currency, boolean isTest) {
		fTrustPayApi.setTest(isTest);
		Date now = new Date();
		Intent intent = fTrustPayApi.preparePayIntent("" + amount, currency,
				"[*]", appuser, txDescription, "DMO" + now.getTime());
		try {
			startActivityForResult(intent, 123);
		} catch (ActivityNotFoundException e) {
			DownloadTrustPay();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		EditText t = (EditText) findViewById(R.id.editText1);
		if (requestCode == 123) {
			if (resultCode == Activity.RESULT_OK) {
				BillingResult res = (BillingResult) data
						.getSerializableExtra("result");
				t.setText("got result:" + res);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				if (data != null) {
					BillingResult res = (BillingResult) data
							.getSerializableExtra("result");
					t.setText("got result:" + res);
				}
			}
		} else if (requestCode == 321) {
			//Selected region
			if (resultCode == Activity.RESULT_OK) {
				String res = data.getStringExtra("country");
				t.setText("got result:" + res);
			}

		} else if (requestCode == 999) {
			//Playstore closed after install
			updateBillingMethods();
		} else {
			//Anything else is strange...
			String res = "got strange result: (resultCode,requestCode):("
					+ resultCode + "," + requestCode + ")";
			t.setText(res);
		}
	}

	@Override
	public void onTrustPayPricePointsResult(PricePoints pps) {
		EditText t = (EditText) findViewById(R.id.editText1);
		String res = "onTrustPayPricePointsResult:";
		if (pps != null) {
			res = res + "Price Points:" + pps.toString();
		}
		t.setText("got result:" + res);

	}
	
	@Override
	public void onUpdateComplete(Boolean arg0, int reason) {
		String res = "Got Update status as " + arg0;
		if (!arg0) {
			if (reason == TrustPayApi.NETWORK_ERROR) {
				res = res + " Network error";
			} else if (reason == TrustPayApi.SERVERERROR) {
				res = res + " Server error";
			} else if (reason == TrustPayApi.NOSIMOPERATOR) {
				res = res + " No Sim Operator";
				startMccMnc();
			}
		}
		EditText t = (EditText) findViewById(R.id.editText1);
		if (t != null) {
			t.setText(res + "\n" + t.getText());
		}
	}

}
