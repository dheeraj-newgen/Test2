/*---------------------------------------------------------------------------------------------------------

//			                   NEWGEN SOFTWARE TECHNOLOGIES LIMITED
//Group                      : Application �Projects
//Product / Project          : IBMWC 
//Module                     : RFax Upload           
//File Name                  : WCRFaxAutoUpload.java           
//Author                     : SHAHNAWAJ AKHTAR
//Date written (DD/MM/YYYY) :  
//Description                :This auto program will be responsible for creating workitems from tiff present in RFax Network Share location. This utility will be running all the time and will have access to this network share location.
							  The functionality of this utility is explained in detail in section 2.1. Other salient features of this utility are as follows:
							  Utility will read tiff file to create workitems. It will also read the windows property of the tiff file which is created date to capture �Date Received� field.
*/
package com.newgen.db.util;

import com.newgen.niplj.fileformat.*;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.newgen.db.commons.*;
import com.newgen.db.services.WCUtilityService;
import com.newgen.srvr.*;
import com.newgen.srvr.XML.*;
import com.newgen.wfdesktop.xmlapi.*;

public class WCRFaxAutoUpload
    implements Runnable {
  private static int threadCount;
  //private static Log logger = null;
  private Logger rfaxLogger=Logger.getLogger("rfaxlogger");

  private String sSleepTime = "";
  private String sAttributes = "";
  public String sProcessDefId;
  private String sPath = "";
  private String sDestFolderPath = "";
  private String sActivityID = "";
  private String sActivityName = "";
  private int iDirAliveNoOfDays = 5;
  private int idelay = 0;
  private String sDocType = "";
  //added by chhavi
  private String  DeleteFilesIn="";
  File[] fDirArr = null;
  // private String dateSuccessFolder="";
  //private String dateErrorFolder="";

  FunctionsWI objWF = null;
  XMLParser xmlParser = new XMLParser();
  DataInputStream dis = null;

  String arr2[] = new String[100];
  String sAttributeListArray[];
  long lMilliSecondsLeft = 0;
  long lMilliSecondsCurrent = 0;
  boolean isFristIntr = true;
  long currntTime;
  long tmpDiff;
  long startTime;
  Date startDate;
  String[] subfile = null;
  String[] aDSTLocation = null;
  String sDstLoc = null;
  WFXmlResponse InstrumentListXmlResponse = new WFXmlResponse("");
  WFXmlList instrumentList = new WFXmlList();

  int iNoOfPages = 0;
  private static String logFlag;

  String folderIndex = "";

  Common comm = new Common();

  public WCRFaxAutoUpload(FunctionsWI objWF) {
    this.objWF = objWF;
  }
  
  public WCRFaxAutoUpload(String threadName, FunctionsWI objWF) {
    try {
    	
      this.objWF = objWF;
      System.out.println(
          "**************Thread WCRFaxAutoUpload Running**************");
      readINI();
      //System.out.println("Log file created");
      //logger.debug("WCDSTAutoUpload");
      while (! ( (new File(sPath)).exists())) {

        try {
          rfaxLogger.debug("The main File Path Doesn't exist :" + sPath);

        }
        catch (Exception ex) {
          rfaxLogger.info("Exception : The thread cannot sleep... :" + ex);
        }
      }

    }
    catch (Exception e) {
      System.out.println("exception");
      rfaxLogger.info("Error in starting ECSUpload Thread ...." + e);
    }
  }

  public void run() {

    while (true) {
      try {
        //System.out.println("call api call"+sPath);
        System.out.println(
            "************** Thread WCRFaxAutoUpload Running **************");
        aDSTLocation = null;
        sDstLoc = "";
        mProcessRFaxLocationTxtFiles();
        Thread.sleep(Long.parseLong(sSleepTime) * 1000 * 60);
        //function added by chhavi for deleting files in output folder past i week
       DeleteFiles();
      }
      catch (Exception e) {
        rfaxLogger.debug("Exception in thread start");
        e.printStackTrace();
      }
    }
  }

  private void DeleteFiles() {
	// TODO Auto-generated method stub
	//  dstlogger.debug("Delete files from outputfolder function running" );
      readINI();
      int days=Integer.parseInt(DeleteFilesIn);
      String sDestpath=sDestFolderPath;
      File folder = new File(sDestpath);
      removeDirectory(folder,days);
}
  public void removeDirectory(File dir,long days) {
		
	//	System.out.println("chhavi-2-");
    long purgeTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000);

    if(dir.isDirectory() == false && dir.lastModified() < purgeTime)
    	{
    //	System.out.println("chhavi-3-deleting files");
    	dir.delete();
    	}
    else if (dir.isDirectory() == true && dir.lastModified() < purgeTime) 
    	recursiveDelete(dir);
    else if(dir.isDirectory()){
         File[] files = dir.listFiles();
         for (File aFile : files)
             removeDirectory(aFile,days);
    }
}

	   public static void recursiveDelete(File file) {
       if (!file.exists())
           return;

       if (file.isDirectory()) {
           for (File f : file.listFiles()) {
               recursiveDelete(f);
           }
       }
     //  System.out.println("chhavi-4- files deleted");
       file.delete();
   }


  private void mProcessRFaxLocationTxtFiles() {
    String sQueueid = "";
    String sRowArr[] = null;
    String sBatchName = comm.getBatchString();
    String sLineNo="";
    File fDir=null;
    //As decided for Rfax, No Batch name:
    try {
      sBatchName = "";
      sDstLoc = mFetchRFaxLocation();
      rfaxLogger.debug("Location from the Table: " + sDstLoc);
      if (!sDstLoc.equals("")) {
        aDSTLocation = sDstLoc.split("~");
        if (aDSTLocation.length > 0) {
          for (int g = 0; g < aDSTLocation.length; g++) {
        	try
        	{
            sRowArr = aDSTLocation[g].split("@");
            sPath = sRowArr[0];
            sQueueid = sRowArr[1];
            rfaxLogger.debug("Rfax Path for this processing :" + sPath);
            rfaxLogger.debug("RFax  Qid:" + sQueueid);
            fDir=new File(sPath);
            if (fDir.exists())
            {
              sLineNo=fDir.getName();
            	//logger.debug("LineNo  is " + sLineNo);
              fDirArr = comm.getListingDirInPath(sPath);
              if (fDirArr != null)
              {
                for (int a = 0; a < fDirArr.length; a++) {
                  //logger.debug("Directory for search operation is: " +fDirArr[a]);
                  if (fDirArr[a].isDirectory()) 
                  {
                    try
                    {
                      sBatchName = fDirArr[a].getName();
                      rfaxLogger.debug("Batch Name is" + sBatchName);
                      mProcessTifFilesLeftInTheLocation(sPath, sBatchName,sQueueid,sLineNo);
                    }
                    catch (Exception e) {
                      rfaxLogger.debug("Some problem happened while processing "+sPath+"/"+sBatchName + " Exception is"+ e.toString());
                    }
                  }
                  else 
                  {
                    rfaxLogger.debug(fDirArr[a].getName() +" is not a directory under RFAX Line");
                  }
                }

              }
              else
              {
            	  rfaxLogger.debug("No Date folder found under RFax Location: "+sPath );
              }
            }
            else {
              //Mail Fire for RFax location
              rfaxLogger.debug("RFax Path does not exists " + sPath);
              comm.mProcedureMailFire("RFax Upload Utility Error Found",
                                      sPath + " does not exist. Pls verify.",
                                      rfaxLogger);
            }
            
          }
          catch(Exception f)
          {
        	  rfaxLogger.debug("Path does not exists " + sPath);
          }
            
          }
          
          
          
        }
        else {
          rfaxLogger.debug("No DST location is defined in the Database---");
        }
      }
      else {
        rfaxLogger.debug("No RFax location is defined in the database");
      }
    }
    catch (Exception e) {
      rfaxLogger.info("Exception main function mProcessRFaxLocationTxtFiles" +
                         e);
    }
  }

  public void execute(FunctionsWI objWF) {

    //logger = new Log(WCUtilityHelper.LogPath, WCUtilityHelper.LogName, "WCDSTAutoUpload.class");
	  
	  
    //objWF.log = logger;
    System.out.println("Testing-1");
    rfaxLogger.debug("Launching Thread count :");
    System.out.println("Testing-2");
    Thread RFax = new Thread(new WCRFaxAutoUpload("RFax", objWF));
    System.out.println("Testing-3");
    RFax.setName("RFax upload");
    RFax.start();

  }

  private String mFetchRFaxLocation() {
    String strQuery = "select Imagepath,queueid from NG_ME_RFAX_Config_Master with(nolock) where isactive='A'";
    try
    {
      strQuery = mSelectQueryExecute(strQuery);
      if (!strQuery.equals("")) 
      {
	      InstrumentListXmlResponse.setXmlString(strQuery);
	      int lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal(
	          "TotalRetrieved"));
	      if (lPidRetCount > 0)
	      {
	        strQuery = "";
	        instrumentList = InstrumentListXmlResponse.createList("Records","Record");
	        for (instrumentList.reInitialize(true); instrumentList.hasMoreElements(true);
	             instrumentList.skip(true)) 
	        {
	          //logger.debug("1234567890");
	          strQuery = strQuery + instrumentList.getVal("Imagepath").trim() + "@" +
	              instrumentList.getVal("queueid").trim() + "~";
	          rfaxLogger.debug("DST Location " +
	                             instrumentList.getVal("Imagepath").trim());
	        }
	      }
	      return strQuery;
      }
    }
    catch(Exception e)
    {
    	rfaxLogger.info("Some exception happened while fetching RFaxLocations "+ e);
    }
    return "";
  }

  private void mProcessTifFilesLeftInTheLocation(String sPath,
                                                 String sBatchName, String sQID,String sLineNo) {

    String sResult = "", sNames = "", sVals = "";
    String sDestPath = ""; long modified_time=0;
    String sUploadDec[] = new String[2];
    int NoOfpagesInTiff = 0;
    File fTempFile = null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.mmm");
    SimpleDateFormat format_dcn = new SimpleDateFormat("yyyyMMddhhmmss");
    Date date = null;
    String srecd_date = "",sDCNno="";
    subfile = null;
    boolean bFlag = false;
    String sCompletePath = "";
    try
    {
    sCompletePath = sPath + System.getProperty("file.separator") + sBatchName;
    subfile = searchTifFileInDir(sCompletePath);
    if (subfile != null && subfile.length>0) {
      sDestPath = sDestFolderPath + System.getProperty("file.separator") +
          Common.getDateString();
      rfaxLogger.debug("NO of Tif Files Count Left in Dir is: " +
                         subfile.length + "\n");
      for (int i = 0; i < subfile.length; i++) {
    	  

        rfaxLogger.debug("File Name : " + subfile[i]);
        //bFlag=mChkTifForExistingRecord(subfile[i],"200");
        try
        {
          sUploadDec[0] = "Complete";
          sUploadDec[1] = "";
          sAttributes = "SOURCE" + ( (char) 21) + "RFax" + ( (char) 25);
          sNames = sCompletePath + System.getProperty("file.separator") + subfile[i];
          rfaxLogger.debug("Added By Gaurav Gupta for Rfax Changes ==> " +sNames+"\n");
            try {
		NoOfpagesInTiff = Tif6.getPageCount(sNames);
		rfaxLogger.debug("No. of pages====G==>"+ NoOfpagesInTiff);
		} catch (Exception ex) {
                  rfaxLogger.debug("Error in getting page count.");
            }
          fTempFile = new File(sNames);
          modified_time = fTempFile.lastModified();
          date = new Date(modified_time);
          //System.out.println(date);
          srecd_date = format.format(date);
          sDCNno	=getDCNString(modified_time);
          sAttributes = sAttributes + "DCN" + ( (char) 21) + sDCNno + ( (char) 25);
          sAttributes = sAttributes + "ReceivedDate" + ( (char) 21) + srecd_date + ( (char) 25);
          sAttributes = sAttributes + "ScannedDate" + ( (char) 21) + srecd_date + ( (char) 25);
          sAttributes = sAttributes + "NoOfpagesInTiff" + ( (char) 21) + NoOfpagesInTiff + ( (char) 25);
          sResult = comm.IntroduceWorkItem(sNames, sDestPath, subfile[i],
                                           sProcessDefId, sAttributes, rfaxLogger,
                                           sActivityID, sActivityName, sDocType,
                                           sUploadDec, "", idelay);
          String[] sArr = sResult.split("~");
          if (sArr[0].equals("success"))
          {
//        	Bug Correction/B-00004/20-08-2009/resolved by Shahnawaj //////////////////////////
            //System.out.println(format.format(date));
            bFlag = Common.fileMoveToFolderWithoutSrc(sNames, sDestPath,subfile[i], subfile[i]);
            if (bFlag) {
              rfaxLogger.debug("Tif File Moved from " + sNames +
                                 " to Dest Path " + sDestPath);
            }
//        	Bug Correction/B-00004/20-08-2009/resolved by Shahnawaj //////////////////////////
            sCompletePath=sCompletePath.replaceAll("/","\\");
            sNames = "ImageName,ReceivedDate,Path,Queueid,WIName,ActionDateTime,ErrorDesc,RecordStatus,LINE_NO,DCN";
            sVals = "'" + subfile[i] + "','" + srecd_date + "','" +sCompletePath + "','" + sQID + "','" + sArr[1] +"',getdate(),'" + sUploadDec[1] + "','Complete','"+sLineNo+"','"+sDCNno+"'";
            sResult = XMLGen.APInsert(WCUtilityHelper.cabinetName,WCUtilityHelper.sessionID,"NG_ME_RFax_APPDetails", sNames, sVals);
            rfaxLogger.debug("IN XML: " + sResult);
            sResult = objWF.execute(sResult);
            rfaxLogger.debug("Out XML: " + sResult);
            xmlParser.setInputXML(sResult);
            sResult = xmlParser.getValueOf("MainCode");
            if (sResult.equals("0")) {
              rfaxLogger.debug(
                  "Record is inserted in DB NG_ME_RFax_APPDetails :" + sResult);
            }
            else {
              rfaxLogger.debug(
                  "Record is not inserted in DB NG_ME_RFax_APPDetails table :" +
                  sResult);
            }
          }
          else {
            rfaxLogger.debug("WI not created for the TIF file " + subfile[i]);
          }
        }
        catch(Exception ef) 
        {
        	  rfaxLogger.debug("Some Exception Happened" + ef);
        }
      }
    }
    else
    {
    	try
    	{    	
	    	//For deleting dir which are already processed and having no Tif File
	    	rfaxLogger.debug("No Tif Files in the directory: "+sCompletePath);
	    	bFlag=comm.mCheckForFolderDeleteTime(sCompletePath,iDirAliveNoOfDays,rfaxLogger);
	    	if(bFlag)
	    	rfaxLogger.debug(sCompletePath+ " dir deleted exceeds the max days Time "+iDirAliveNoOfDays);
    	}
    	catch(Exception w)
    	{
    		rfaxLogger.debug("Some Exception happened in Delete directory when tiff are not there in the directory"+w);
    	}
    }
   }
    catch(Exception e)
    {
    	rfaxLogger.info("Some Exception happened while processing path "+sCompletePath+"--"+e);
    }
    
    
  }

 String getDCNString(long date)
 {
	 String sDCNNo="", month="";
	 Calendar now = Calendar.getInstance();
	 try
	 {
		 now.setTimeInMillis(date);
		 month=(now.get(Calendar.MONTH) + 1 )+"";
		 if(month.length()<2)
		 month	=	"0"+month;
		 sDCNNo= ""+now.get(Calendar.YEAR) +month+now.get(Calendar.DATE)+now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.MINUTE)+now.get(Calendar.SECOND)+now.get(Calendar.MILLISECOND) ;
	 }
	 catch(Exception e)
	 {
		 sDCNNo="Not Found";
		 rfaxLogger.info("Exception: getDCNString "+ e);
	 }
	 return sDCNNo;
 }
  
  
  
  private String mSelectQueryExecute(String strQuery) {
    strQuery = XMLGen.APSelectWithColumnNames(WCUtilityHelper.cabinetName,
                                              WCUtilityHelper.sessionID,
                                              strQuery);
    rfaxLogger.debug(strQuery);
    strQuery = objWF.execute(strQuery);
    rfaxLogger.debug(strQuery);
    xmlParser.setInputXML(strQuery);
    String sMainCode = xmlParser.getValueOf("MainCode");
    if (sMainCode.equals("0") &&
        Integer.parseInt(xmlParser.getValueOf("TotalRetrieved")) > 0) {
      return strQuery;
    }
    return "";
  }

  private String[] searchTifFileInDir(String path) {
    try {
      subfile = null;
      File dir = new File(path);
      rfaxLogger.debug(" IN searchTifFileInDir File dir is :" + path);
      FilenameFilter filterFile = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          try {
            return (name.toUpperCase().endsWith(".TIF"));
          }
          catch (Exception e) {
            return false;
          }
        }
      };
      subfile = dir.list(filterFile);
      return subfile;
    }
    catch (Exception e) {
      return null;
    }
  }

  public void readINI() {
    try {
      Properties ini = new Properties();
      System.out.println("***" + System.getProperty("user.dir"));
     
		 

		// **********Read the attributes from log4j.properties****************
      ini.load(new FileInputStream(System.getProperty("user.dir") +
                                   System.getProperty("file.separator") +
                                   "WC_RFax_Config.ini"));

      //threadCount = Integer.parseInt(ini.getProperty("threadCount"));
      //System.out.println("threadCount:\t" + threadCount);

      sProcessDefId = ini.getProperty("ProcessdefID");
      System.out.println("sProcessDefId:\t" + sProcessDefId);

      sDestFolderPath = ini.getProperty("Destpath");
      System.out.println("DestFolderPath:\t" + sDestFolderPath);

      sPath = ini.getProperty("logPath");
      System.out.println("logPath:\t" + sPath);

      WCUtilityHelper.LogPath = ini.getProperty("logPath");
      System.out.println("logPath:\t" + WCUtilityHelper.LogPath);

      WCUtilityHelper.LogName = ini.getProperty("logName");
      System.out.println("logName:\t" + WCUtilityHelper.LogName);

      logFlag = ini.getProperty("logFlag");
      System.out.println("logFlag:\t" + logFlag);

      WCUtilityHelper.DocName = ini.getProperty("DocName");
      System.out.println("DocName:\t" + WCUtilityHelper.DocName);

      WCUtilityHelper.VolumeID = ini.getProperty("VolID");
      System.out.println("DocName:\t" + WCUtilityHelper.VolumeID);

      sSleepTime = ini.getProperty("SleepTime");
      System.out.println("sSleepTime:\t" + sSleepTime);

      sDocType = ini.getProperty("DocName");
      System.out.println("sDocType:\t" + sDocType);

      sActivityID = ini.getProperty("ActivityId");
      System.out.println("ActivityId:\t" + sActivityID);

      sActivityName = ini.getProperty("ActivityName");
      System.out.println("ActivityName:\t" + sActivityName);
      
      iDirAliveNoOfDays =Integer.parseInt( ini.getProperty("DirAliveNoOfDays"));
      System.out.println("iDirAliveNoOfDays:\t" + iDirAliveNoOfDays);

      idelay = Integer.parseInt(ini.getProperty("RISDelay"));
      System.out.println("idelay:\t" + idelay);
      


    //added by chhavi for deleting files from output folder past 7 days
          DeleteFilesIn =ini.getProperty("DeleteFilesIn");
          System.out.println("iDirAliveNoOfDays:\t" + DeleteFilesIn);
    }
    catch (Exception e) {
      System.out.println("Error in reading ini file..." + e);
      e.printStackTrace();
      System.exit(0);
    }
  }

  public void writeORnotLog(String logstring) {
    if (logFlag.equalsIgnoreCase("YES")) {
      rfaxLogger.debug(logstring);
    }
  }
    
}
