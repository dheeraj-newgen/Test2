/**
 * <p>Title: Auto Create Server</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Newgen Software Technologies Ltd.</p>
 * @author Shahnawaj Akhtar
 * @version 1.0
 */

package com.newgen.srvr;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.newgen.db.commons.*;
import com.newgen.db.services.WCUtilityService;
import com.newgen.omni.wf.util.app.*;
import com.newgen.omni.wf.util.excp.*;
import com.newgen.srvr.XML.*;
import com.newgen.wfdesktop.xmlapi.*;

public class FunctionsWI {
  public Hashtable docType = new Hashtable();
  XMLParser xmlParser = new XMLParser();
  public String userName = "";
  public String userID = "";
  public String password = "";
  public String serverIP = "127.0.0.1";
  public int serverPort = 3333;
  public String cabinetName = "";
  public String processName = "";
  public String processDefID = "";
  public String siteName = "";
  public int siteID = 0;
  public String volName = "";
  public String volID = "";
  public String sessionID = "";
  public String ProcessFolderID = "";
  public String activityName = "";
  public String queueName = "";
  public String activityID = "";
  public String queueID = "";
  public String activityName1 = "";
  public String queueName1 = "";
  public String activityID1 = "";
  public String queueID1 = "";
  public String introActivityName = "";
  public String introQueueName = "";
  public String introActivityID = "";
  public String introQueueID = "";
  public String batchSize = "250";
  public String parentFolderIndex = "";
  public String parentFolderIndex1 = "";
  public String dataDefIndex = "";
  public String dataDefName = "AcOpening";
  public String dataDefProp[][];
  public int dataDefPropCount = 0;
  public static NGEjbClient ngEjbClient = null;
 // public Log log = new Log("Log", "service_log", "FunctionWI.class");
  public Logger wiLog=WCUtilityService.serviceLogger;
  public static boolean sessionFlag = true;
  //private static Log log = new Log(DbUtilityHelper.service_LOG,"service_log","FunctionWI.class");
  String sMainCode = "";
  Socket s = null;

  public FunctionsWI() {
    //boolean connect = connectToServer();
	
		 

		// **********Read the attributes from log4j.properties****************

  }
 
  public boolean connectToServer() {
    try {
      System.out.println("In connectToServer");
      ngEjbClient = NGEjbClient.getSharedInstance();
      System.out.println("1111");
     ngEjbClient.initialize(WCUtilityHelper.serverIP,
                             String.valueOf(WCUtilityHelper.serverPort),
                             WCUtilityHelper.serverType);
       /* ngEjbClient.initialize("oflowxad01",
                                     "8080",
                                     "JTS");*/

      System.out.println("2222888888888");
      //ngEjbClient.initialize(this.serverIP ,GlobalVar.WebLogicPort,"WebLogic");
      return true;
    }
    catch (NGException ngE) {
      wiLog.info(ngE.toString());
      return false;
    }
  }

  public void disconnectFromServer() {
    try {
      ngEjbClient = null;
    }
    catch (Exception e) {
    	wiLog.info(e.toString());
    }
  }

  private void mReconnectToWorkflow() {
    System.out.print("Reconnect");
    try {
      this.disconnectFromWorkFlow();
      this.disconnectFromServer();
    }
    catch (Exception ex) {}
    try {
      if (this.connectToServer()) {
        String str = this.connectToWorkFlow("N");
        System.out.println(str);
        // Thread.sleep(1000*1);
        sessionFlag = true;
      }
    }
    catch (Exception ex) {
      //writeORnotLog(ex.toString());
    }
  }

  public synchronized String execute(String inXml) {
    try {
      String outXml = ngEjbClient.makeCall(inXml);
      xmlParser.setInputXML(outXml);
      sMainCode = xmlParser.getValueOf("MainCode");
      if (sMainCode.equals("-50146")||sMainCode.equals("11")) {
        String str = this.connectToWorkFlow("Y");
        String[] sArr = str.split("~");
        if (sArr[0].equals("0")) {
          sessionFlag = true;
        }
        else {
          mReconnectToWorkflow();
        }
        // outXml=this.execute(inXml);
      }
      return outXml;
    }
    catch (NGException ngE) {
    	wiLog.info(ngE.toString());
      disconnectFromServer();
      if (connectToServer()) {
        try {
          String outXml = ngEjbClient.makeCall(inXml);
          xmlParser.setInputXML(outXml);
          sMainCode = xmlParser.getValueOf("MainCode");
          if (sMainCode.equals("0")) {
            return outXml;
          }
          else if (sMainCode.equals("-50146")) {

            mReconnectToWorkflow();
          }
        }
        catch (NGException ngE1) {
        	wiLog.info(ngE1.toString());
        }
        catch (Exception ex) {
        	wiLog.info(ex.toString());
        }
      }
      return "";
    }
  }

  public String executeWithoutInLog(String inXml) {
    try {
      String outXml = ngEjbClient.makeCall(inXml);
      return outXml;
    }
    catch (NGException ngE) {
    	wiLog.info(ngE.toString());
      disconnectFromServer();
      if (connectToServer()) {
        try {
          String outXml = ngEjbClient.makeCall(inXml);
          return outXml;
        }
        catch (NGException ngE1) {
        	wiLog.info(ngE1.toString());
        }
        catch (Exception ex) {
        	wiLog.info(ex.toString());
        }
      }
      return "";
    }
  }

  public String connectToWorkFlow(String forceful) {
    int i = -9;
    String desc = null;
    String xmlInput = null;
    String xmlOutput = null;
    try {
      System.out.println("333786");
      System.out.println("Thread: " + Thread.currentThread().getName());
      //System.out.println("Cabinet Name"+WCUtilityHelper.cabinetName);
      //System.out.println("User Name"+WCUtilityHelper.userName);
      //System.out.println("Password "+WCUtilityHelper.userPassword);
      xmlInput = XMLGen.get_WMConnect_Input(WCUtilityHelper.
                                            cabinetName,
                                            WCUtilityHelper.userName,
                                            WCUtilityHelper.userPassword,
                                            forceful);
      wiLog.debug("In XML input akash-- " + xmlInput);
      xmlOutput = this.executeWithoutInLog(xmlInput);
      System.out.println("444");
      //System.out.println("xmlOutput"+xmlOutput);
      xmlParser.setInputXML(xmlOutput);
      System.out.println("In xmlOutput " + xmlOutput);
      String s9 = xmlParser.getValueOf("Option");
      if (!s9.equalsIgnoreCase("WMConnect")) {
        return "-9~Invalid Workflow Server IP and Port are registered.";
      }
      String s6 = xmlParser.getValueOf("MainCode");
      i = Integer.parseInt(s6);
      if (i == 0) {
        System.out.println("555");
        WCUtilityHelper.sessionID = xmlParser.getValueOf("SessionID");
        WCUtilityHelper.userID = xmlParser.getValueOf("ID");
        System.out.println("666");
      }
      else {
        String s7 = xmlParser.getValueOf("SubErrorCode");
        desc = xmlParser.getValueOf("Description");
        i = Integer.parseInt(s7);
      }
    }
    catch (Exception e) {
    	wiLog.info(e.toString());
      e.printStackTrace();
    }
    return i + "~" + desc;
  }

  public String connectToOmnidocs(String forceful) {
    int i = -9;
    String desc = null;
    String xmlInput = null;
    String xmlOutput = null;
    try {
      xmlInput = XMLGen.get_NGOConnectCabinet_Input(this.
          cabinetName, this.userName,
          this.password, forceful);
      xmlOutput = this.execute(xmlInput);
      xmlParser.setInputXML(xmlOutput);
      String s9 = xmlParser.getValueOf("Option");
      if (!s9.equalsIgnoreCase("NGOConnectCabinet")) {
        return "-9~Invalid Workflow Server IP and Port are registered.";
      }
      String s7 = xmlParser.getValueOf("Status");
      i = Integer.parseInt(s7);
      if (i == 0) {
        this.sessionID = xmlParser.getValueOf("UserDBId");
        this.userID = xmlParser.getValueOf("LoginUserIndex");
      }
      desc = xmlParser.getValueOf("Error");
    }
    catch (Exception e) {
    	wiLog.info(e.toString());
      e.printStackTrace();
    }
    return i + "~" + desc;
  }

  public void disconnectFromWorkFlow() {
    String str_inxml = XMLGen.get_WMDisConnect_Input(WCUtilityHelper.
        cabinetName,
        WCUtilityHelper.sessionID);
    String str_outxml = execute(str_inxml);
  }
}
