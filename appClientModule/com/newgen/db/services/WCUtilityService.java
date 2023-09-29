package com.newgen.db.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.newgen.db.commons.WCUtilityHelper;
import com.newgen.db.util.*;
import com.newgen.srvr.FunctionsWI;
import com.newgen.srvr.Log;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class  WCUtilityService  {
    boolean keepRunning=true;

	//private static Log log = null;
    //private static Logger   = null;
    public static Logger serviceLogger=Logger.getLogger("file");
	private static String SleepTime;
	private static String logFlag;
	FunctionsWI objWF = new FunctionsWI();

	WCUtilityService(int i) {
	
	}

	WCUtilityService() {
	}
		
	static
	{
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "Properties" + File.separator
				+ "WC_Server_Config_log4j.properties");
		
	
			
	}
	public static void main(String[] args) {
		
		//serviceLogger=Logger.getLogger(WCUtilityService.class.getName());
		//serviceLogger.debug("+++++++++++++++++++++akash");
		
		WCUtilityService utilityService = new  WCUtilityService();
		utilityService.startService();

	}
	private void startService() {
			readINI();
		    
			//log = new Log(logPath,"service_log","DbUtilityService.class");
			
			connectToWF();
			WCDSTAutoUpload Initialize = new WCDSTAutoUpload("",objWF);
			//System.out.println("Testing");
			Initialize.execute(objWF);
			
			/*WCRFaxAutoUpload FaxObj = new WCRFaxAutoUpload("",objWF);
			//System.out.println("Testing");
			FaxObj.execute(objWF);
			*/
			
			/*WCRFaxConciliationUpload ConcilObj = new WCRFaxConciliationUpload("",objWF);
			///System.out.println("Testing");
			ConcilObj.execute(objWF);
		*/
		}

	private void connectToWF() {
		System.out.print("connect");
		
		
		try {
            if (objWF.connectToServer()) {
            
                String str = objWF.connectToWorkFlow("N");
               
                String temp[] = str.split("~");
                if (!temp[0].equals("0")) {
                	Thread.sleep(Long.parseLong(SleepTime));
                  if (keepRunning) {
                       connectToWF();
                   }
                }
            }
        } catch (Exception ex) {
        	System.out.println(ex);
        	writeORnotLog(ex.toString());
        }
	}
	public void readINI() {
		try {
			
			Properties ini = new Properties();
			//ini.load(new FileInputStream(System.getProperty("user.dir") +System.getProperty("file.separator") + "WC_Server_Config.ini"));
			//ini.load(new FileInputStream("O:\\ravi jain\\ME Process\\NG_ME_UploadToWF\\config INI\\WC_Server_Config.ini"));
			//ini.load(new FileInputStream("I:\\Akash\\NG_ME_UploadToWF_DocuSign\\WC_Server_Config.ini"));
			 ini.load(new FileInputStream(System.getProperty("user.dir") +
		              System.getProperty("file.separator")
		              +"WC_Server_Config.ini"));

    		// **********Read the attributes from log4j.properties****************
			WCUtilityHelper.cabinetName=ini.getProperty("CabinetName");
			WCUtilityHelper.jtsIP=ini.getProperty( "JTSIP" );
			WCUtilityHelper.jtsPort=Integer.parseInt(ini.getProperty( "JTSPort" ));
			WCUtilityHelper.serverIP=ini.getProperty( "serverIP" );
			WCUtilityHelper.serverType=ini.getProperty( "JTSType" );
			WCUtilityHelper.serverPort=ini.getProperty( "serverPort" );
			WCUtilityHelper.processDefID=ini.getProperty( "processDefID" );
			WCUtilityHelper.constDataClassName=ini.getProperty( "constDataClassName" );
			WCUtilityHelper.constAOFDoc=ini.getProperty( "constAOFDoc" );
			WCUtilityHelper.constIntroMedAUTO=ini.getProperty( "constIntroMedAUTO" );
			WCUtilityHelper.constIntroMedOC=ini.getProperty( "constIntroMedOC" );
			WCUtilityHelper.siteID=ini.getProperty( "siteID" );
			WCUtilityHelper.VolumeID=ini.getProperty( "volid" );
			WCUtilityHelper.constantDataClassAOF=ini.getProperty( "constantDataClassAOF" );
			WCUtilityHelper.constantDataClassCIF=ini.getProperty( "constantDataClassCIF" );
			WCUtilityHelper.processDefID1=ini.getProperty( "processDefID1" );
			WCUtilityHelper.processDefIDSIG=ini.getProperty( "processDefIDSIG" );
			WCUtilityHelper.service_LOG=ini.getProperty( "service_LOG" );
			WCUtilityHelper.userPassword=ini.getProperty( "userPassword" );
			WCUtilityHelper.userName=ini.getProperty( "userName" );
			
			SleepTime=ini.getProperty( "SleepTime" );
			//logPath=ini.getProperty( "logPath" );
			logFlag=ini.getProperty( "logFlag" );
			
			serviceLogger.debug("username---------"+WCUtilityHelper.userName);
			serviceLogger.debug("password---------"+WCUtilityHelper.userPassword);
			/* try 
				{
					File desFile = new File("np.dat");
					System.out.println("file.exist"+desFile.exists());
					FileInputStream fis;
					FileOutputStream fos;
					CipherInputStream cis;

					// Read the cipher settings
					File KEY_FILE=new File("key.data");
					FileInputStream eksis = new FileInputStream( KEY_FILE );
					byte[] temp = new byte[ (int)KEY_FILE.length()];
					int bytesRead = eksis.read(temp);
								   
					byte[] encodedKeySpec = new byte[bytesRead];
					System.arraycopy(temp, 0, encodedKeySpec, 0, bytesRead);

					// Recreate the secret/symmetric key
					SecretKeySpec secretKey = new SecretKeySpec( encodedKeySpec, "DES");

					// Creation of Cipher objects
					Cipher decrypt =
					Cipher.getInstance("DES/ECB/PKCS5Padding");
					decrypt.init(Cipher.DECRYPT_MODE, secretKey);

					// Open the Encrypted file
					fis = new FileInputStream(desFile);
					cis = new CipherInputStream(fis, decrypt);

					byte[] b = new byte[8];
					int i = cis.read(b);
					String sdata=null;
					while (i != -1) {
					sdata=sdata+(new String(b));
					i = cis.read(b);
					}
					cis.close();
					fis.close();

					int m=sdata.indexOf("<Password>")+10;
					int n=sdata.indexOf("</Password>");
					WCUtilityHelper.userPassword=sdata.substring(m,n);
					
					

					int o=sdata.indexOf("<UserName>")+10;
					int p=sdata.indexOf("</UserName>");
					WCUtilityHelper.userName=sdata.substring(o,p);
					
					
					serviceLogger.debug("username---------"+WCUtilityHelper.userName);
					serviceLogger.debug("password---------"+WCUtilityHelper.userPassword);

					
				}
				catch(Exception e)
				{
					writeORnotLog("Error in Decryption.");
					e.printStackTrace();
					
				}*/
			
		} catch (Exception e) 
			{
				 e.printStackTrace();
				 System.exit(0);
			}

	}
	
	public void writeORnotLog(String logstring){
		System.out.println("logstring>>>>>>>>>>>"+logstring);
    	
    	if(logFlag.equalsIgnoreCase("YES")){
    		
    		
    		
    		serviceLogger.debug(logstring);
    	}
    	
    	
    }
}
