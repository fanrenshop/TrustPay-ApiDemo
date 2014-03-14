package biz.trustpay.ApiDemo;


import net.rim.device.api.ui.UiApplication;

public class ApiDemo extends UiApplication {
	   
	   public static void main(String[] args) {
		   ApiDemo theApp = new ApiDemo();
	      theApp.enterEventDispatcher(); 
	    }
	    
	    
	   public  ApiDemo() {
	       pushScreen (new Pay() );
	      }
	} 
