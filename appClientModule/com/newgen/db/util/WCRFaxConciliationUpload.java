/*---------------------------------------------------------------------------------------------------------

//			                   NEWGEN SOFTWARE TECHNOLOGIES LIMITED
//Group                      : Application Projects
//Product / Project          : IBMWC 
//Module                     : RFax Conciliation Upload           
//File Name                  : WCRFaxConciliationUpload.java           
//Author                     : SHAHNAWAJ AKHTAR
//Date written (DD/MM/YYYY) :  
//Description                :This auto program will be responsible for creating workitems from tiff present in RFax Network Share location. This utility will be running all the time and will have access to this network share location.The functionality of this utility is explained in detail in section 2.1. Other salient features of this utility are as follows:Utility will read tiff file to create workitems. It will also read the windows property of the tiff file which is created date to capture �Date Received� field.
*/
package com.newgen.db.util;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.newgen.db.commons.*;
import com.newgen.db.services.WCUtilityService;
import com.newgen.srvr.*;
import com.newgen.srvr.XML.*;
import com.newgen.wfdesktop.xmlapi.*;

public class WCRFaxConciliationUpload
    implements Runnable {
  private static int threadCount;
  //private static Log logger = null;
  private Logger  rfaxConcLogger=Logger.getLogger("rfaxConcil");

  private String sSleepTime = "";
  private String sAttributes = "";
  public String sProcessDefId;
  private String sPath = "";
  private String sDestFolderPath = "";
  private String sActivityID = "";
  private String sActivityName = "";
  private String sDocType = "";
  //added by chhavi
  private String  DeleteFilesIn="";

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
  WFXmlList instrumentList1 = new WFXmlList();

  int iNoOfPages = 0;
  private static String logFlag;

  String folderIndex = "";

  Common comm = new Common();

  public WCRFaxConciliationUpload(FunctionsWI objWF) {
    this.objWF = objWF;
  }
 
  public WCRFaxConciliationUpload(String threadName, FunctionsWI objWF) {
    try {
    	 
      this.objWF = objWF;
      System.out.println(
          "**************Thread WCRFaxReconciliationUpload Running**************");
      readINI();
      System.out.println("Log file created" + sPath);
      //rfaxConcLogger.debug("WCDSTAutoUpload");
      while (! ( (new File(sPath)).exists())) {

        try {
          rfaxConcLogger.debug("The main File Path Doesn't exist :" + sPath);

        }
        catch (Exception ex) {
          rfaxConcLogger.info("Exception : The thread cannot sleep... :" + ex);
        }
      }

    }
    catch (Exception e) {
      System.out.println("exception");
      rfaxConcLogger.info("Error in starting ECSUpload Thread ...." + e);
    }
  }

  public void run() {
    int status = 0;
    File file=null;
    while (true) {
      try {
        //System.out.println("call api call"+sPath);
        System.out.println("************** Thread WCRFaxReconciliationUpload Running **************");
        aDSTLocation = null;
        sDstLoc = "";
        file=new File(sPath);
        //mProcessRFaxLocationTxtFiles();
        if (file.exists()) {
          status = startCSVSearch_UpdateOperation(sPath, "");
        }
          else {
         
          rfaxConcLogger.debug("Path does not exists " + sPath);
          comm.mProcedureMailFire("RFax Conciliation Upload Utility Error Found",sPath + " does not exist. Pls verify.", rfaxConcLogger);
          file.mkdir();
        }
        // if (status == 0) {
        processCSVDataForUpload(sPath, "", "");
     // }
        mGenerateCSVFile(sDestFolderPath);
        Thread.sleep(Long.parseLong(sSleepTime) * 1000 * 60);
        DeleteFiles();
      }
      catch (Exception e) {
        rfaxConcLogger.debug("Exception in thread start");
        e.printStackTrace();
      }
    }
  }

  private void DeleteFiles() {
		// TODO Auto-generated method stub
		 // dstlogger.debug("Delete files from outputfolder function running" );
	      readINI();
	      int days=Integer.parseInt(DeleteFilesIn);
	      String sDestpath=sDestFolderPath;
	      File folder = new File(sDestpath);
	      removeDirectory(folder,days);
	}

	  public void removeDirectory(File dir,long days) {
			
			//System.out.println("chhavi-2-");
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
	        // System.out.println("chhavi-4- files deleted");
	         file.delete();
	     }


  private void mProcessRFaxLocationTxtFiles() {
    String sQueueid = "";
    String sRowArr[] = null;
    String sBatchName = comm.getBatchString();
    //sDstLoc=mFetchRFaxLocation();
    rfaxConcLogger.debug("Location from the Table: " + sDstLoc);
    //aDSTLocation=sDstLoc.split("~");
    if (aDSTLocation.length > 0) {
      for (int g = 0; g < aDSTLocation.length; g++) {
        sRowArr = aDSTLocation[g].split("@");
        sPath = sRowArr[0];
        sQueueid = sRowArr[1];
        rfaxConcLogger.debug("Location Path:" + sPath);
        rfaxConcLogger.debug("Location Qid:" + sQueueid);
        processCSVDataForUpload(sPath, sBatchName, sQueueid);
        

      }
    }
    else {
      rfaxConcLogger.debug("No DST location is defined in the Database---");
    }
  }
  
  
   void mGenerateCSVFile(String path)
  {
	   
	   String sCsvFileContent="";String sCsvFile="";String sLogID="";
	   try
	   {
      	String strQuery ="SELECT  DISTINCT LOGID FROM NG_ME_RFAXCONCIL_DETAILS WITH(NOLOCK) WHERE FILE_GEN='NO' ";//ORDER BY ACTIONDATETIME";
      	strQuery = mSelectQueryExecute(strQuery);
          if(!"".equals(strQuery)) 
          {
        	  InstrumentListXmlResponse.setXmlString(strQuery);
        	  int lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal("TotalRetrieved"));
          if(lPidRetCount > 0)
          {
            rfaxConcLogger.debug("No of rows in file=" + lPidRetCount);
            strQuery = "";
            instrumentList = InstrumentListXmlResponse.createList("Records","Record");
            for (instrumentList.reInitialize(true); instrumentList.hasMoreElements(true);instrumentList.skip(true))
            {
            	try
            	{
            	strQuery = instrumentList.getVal("LOGID").trim();
            	sLogID= strQuery;
            	strQuery ="SELECT BATCHNAME,ReceivedDate,IMAGENAME,FILEPATH,WINAME,RECORDSTATUS,ActionDateTime,RfaxQ,RfaxNo  FROM NG_ME_RFAXCONCIL_DETAILS WITH(NOLOCK) WHERE LOGID='"+strQuery+"' ORDER BY ACTIONdateTImE DESC";
              	strQuery = mSelectQueryExecute(strQuery);
              	 if (!"".equals(strQuery)) 
                 {
              		 try
              		 {
              		InstrumentListXmlResponse.setXmlString(strQuery);
              		 lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal("TotalRetrieved"));
	                 if (lPidRetCount > 0)
	                 {
	                   rfaxConcLogger.debug("No of rows in file=" + lPidRetCount);
	                   strQuery = "";
	                   instrumentList1 = InstrumentListXmlResponse.createList("Records","Record");
	                   sCsvFileContent="";
	                  
	                   for (instrumentList1.reInitialize(true); instrumentList1.hasMoreElements(true);instrumentList1.skip(true))
	                   { 
	                	   sCsvFile=instrumentList1.getVal("BATCHNAME").trim();
	                	   sCsvFileContent = sCsvFileContent +instrumentList1.getVal("RfaxQ").trim() + ","+ instrumentList1.getVal("RfaxNo").trim() + ","+ instrumentList1.getVal("ReceivedDate").trim() + ","+instrumentList1.getVal("IMAGENAME").trim() + "," +  instrumentList1.getVal("FILEPATH").trim()  + "," + instrumentList1.getVal("WINAME").trim() +"," + instrumentList1.getVal("RECORDSTATUS").trim()+"," + instrumentList1.getVal("ActionDateTime").trim() + "\n";
	                   }
	                   rfaxConcLogger.debug("sCsvFileContent=" + sCsvFileContent);
	                   if(!"".equals(sCsvFileContent))
	                   {
	                	   ////Bug Correction/B-00005/21-08-2009/resolved by Shahnawaj //////////////////////////
	                       
	                	   rfaxConcLogger.debug("path=" + path);
	                	   mWriteCsvFile(path,sCsvFile,  sCsvFileContent);
	                	   
	                	   ////Bug Correction/B-00005/21-08-2009/resolved by Shahnawaj //////////////////////////
	                       
	                	   try
	                	   {
	                	    sCsvFileContent = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
	                                                 WCUtilityHelper.sessionID, "'YES'",
	                                                 "LOGID='"+sLogID+"'", "NG_ME_RFAXCONCIL_DETAILS",
	                                                 "FILE_GEN");

	                       rfaxConcLogger.debug("IN XML: " + sCsvFileContent);
	                       sCsvFileContent = objWF.execute(sCsvFileContent);
	                       rfaxConcLogger.debug("Out XML: " + sCsvFileContent);
	                       xmlParser.setInputXML(sCsvFileContent);
	                       sCsvFileContent = xmlParser.getValueOf("MainCode");
	                	   }
	                	   catch(Exception e)
	                	   {
	                		   rfaxConcLogger.info("IExeption happened while updatting FILE_GEN FLAG : " + e);
	                	   }
	                	   //send mail for confirmation
	                	   comm.mProcedureMailFireWithoutChk("RFax Concilliation File "+sCsvFile +" Procesed ",
	                			   "RFax Concilliation File "+sCsvFile+" is placed at "+ path + ".",
	                			   rfaxConcLogger);
	                	   
	                   }
	                }
	                 
              		}catch(Exception d)
	                 {
              			rfaxConcLogger.info("IExeption happened while updatting FILE_GEN FLAG : " + d);
	                	   
	                 }
	             }
            	/////////////////////////////////
            	}
            	catch(Exception e)
         	    {
         		   rfaxConcLogger.info("Some Excep : processing NG_ME_RFAXCONCIL_DETAILS for LOGID  "+instrumentList.getVal("LOGID").trim()+" " + e);
         	    }
            }
            
            
            
            
          }
         }  
	   }
	   catch(Exception e)
	   {
		   rfaxConcLogger.info("Some Excep : mGenerateCSVFile function " + e);
	   }
  }

  public void execute(FunctionsWI objWF) {

    //rfaxConcLogger.debugew Log(WCUtilityHelper.LogPath, WCUtilityHelper.LogName, "WCDSTAutoUpload.class");
    //objWF.log = logger;
	  
    System.out.println("Testing-1");
    rfaxConcLogger.debug("Launching Thread count :");
    System.out.println("Testing-2");
    Thread RFaxConcil = new Thread(new WCRFaxConciliationUpload("RFaxConcil",
        objWF));
    System.out.println("Testing-3");
    RFaxConcil.setName("RFaxConciliation upload");
    RFaxConcil.start();

  }

  private int startCSVSearch_UpdateOperation(String sPath, String sBatchName) {
    String sDestPath = ""; ;
    String sDataString = "";
    String aBatchArr[] = null;
    String sInXML = "", sParam = "",sLOGID="";
    String sCompletePath = "";
    boolean bStatus = false;
    int iCountBatch = 0;
    sCompletePath = sPath + "/" + sBatchName;
    rfaxConcLogger.debug(" In startDSTSearchOperation " + sPath);
    rfaxConcLogger.debug("Diectory For operation: " + sPath);
////Bug Correction/B-00006/21-08-2009/resolved by Shahnawaj //////////////////////////
    subfile = searchCSVFileInDir(sCompletePath);
    if (subfile != null && subfile.length>0) {
      sDataString = "";
      sDestPath = "";
      rfaxConcLogger.debug("NO of Files Count in Dir is: " + subfile.length +"\n");
      for (int i = 0; i < subfile.length; i++) {
    	  try
    	  {
        rfaxConcLogger.debug("File Name : " + subfile[i]);
        sDestPath = sDestFolderPath ;//+ System.getProperty("file.separator") + Common.getDateString();
        aBatchArr = null;
        rfaxConcLogger.debug("File Name " + subfile[i] +" is Moved to DestinationPath" + sDestPath);
        sDataString = readCsvFile(sCompletePath + System.getProperty("file.separator") + subfile[i]);
        rfaxConcLogger.debug("Data From CSV :" + sDataString);
    	  }
////    	Bug Correction/B-00006/21-08-2009/resolved by Shahnawaj //////////////////////////
    	  catch(Exception e)
    	  {
    		  rfaxConcLogger.debug("Exception : Reading CSV file " + subfile[i]);
    	  }
        if (sDataString.length() > 2) {
        	
        	sInXML="SELECT CAST(DATEPART(year,GETDATE()) AS VARCHAR(4))+CAST(DATEPART(Month,GETDATE()) AS VARCHAR(4))+CAST(DATEPART(Day,GETDATE()) AS VARCHAR(4))+CAST(DATEPART(HOUR,GETDATE()) AS VARCHAR(4))++CAST(DATEPART(MINUTE,GETDATE()) AS VARCHAR(4))+CAST(DATEPART(SECOND,GETDATE()) AS VARCHAR(4))+CAST(DATEPART(MS,GETDATE()) AS VARCHAR(4)) AS LOGID";
        	sInXML = comm.mSelectQueryExecute(sInXML, rfaxConcLogger);
            if (!sInXML.equals("")) {
              xmlParser.setInputXML(sInXML);
              sLOGID = xmlParser.getValueOf("LOGID");
            }
          aBatchArr = sDataString.split("#");
          rfaxConcLogger.debug("Length Of Batch data in txt= " + aBatchArr.length);
          for (int a = 0; a < aBatchArr.length; a++) {
          try
          {
	            sParam = "'" + aBatchArr[a] + "','" + subfile[i] + "','" +
	                sBatchName + "','" + sPath + "','"+sLOGID+"'";
	            sInXML = XMLGen.APProcedure(WCUtilityHelper.cabinetName,
	                                        WCUtilityHelper.sessionID, "", sParam,
	                                        "NG_ME_SP_Concil_CSV_UPLOAD");
	            rfaxConcLogger.debug(sInXML);
	            sInXML = objWF.execute(sInXML);
	            rfaxConcLogger.debug(sInXML);
	            xmlParser.setInputXML(sInXML);
	            sInXML = xmlParser.getValueOf("MainCode");
	            if (sInXML.equals("0"))
	            {
	              iCountBatch++;
	            }
	            else
	            {
	            	sDestPath = sDestFolderPath + System.getProperty("file.separator") +
	                Common.getDateString() + "_Error"  ;
	            	// bStatus = Common.fileMoveToFolder(sCompletePath, sDestPath,subfile[i], subfile[i]);
	            	 rfaxConcLogger.debug("CSV file is present in folder= " + sCompletePath);
	            	 comm.mProcedureMailFire("RFAX CONCILLIATION Upload Utility Improper FILE Found",
	            			 "File is present at path " + sCompletePath + " at Server. Pls action.",
	            			 rfaxConcLogger);
	            	 bStatus = Common.fileMoveToFolder(sCompletePath, sDestPath,
	                         subfile[i], subfile[i]);
	            	 rfaxConcLogger.debug("TXT file moved to Error folder=" + bStatus);
	            	 break;
	            }
        	}
	    	catch(Exception e)
	    	{
	    		  sDestPath = sDestFolderPath + System.getProperty("file.separator") +
	                Common.getDateString() + "_Error"  ;
	            	// bStatus = Common.fileMoveToFolder(sCompletePath, sDestPath,subfile[i], subfile[i]);
	            	 rfaxConcLogger.debug("CSV file is present in folder= " + sCompletePath);
	            	 comm.mProcedureMailFire("RFAX CONCILLIATION Upload Utility Improper FILE Found",
	            			 "File is present at path " + sCompletePath + " at Server. Pls action.",
	            			 rfaxConcLogger);
	            	 bStatus = Common.fileMoveToFolder(sCompletePath, sDestPath, subfile[i], subfile[i]);
	            	 rfaxConcLogger.debug("TXT file moved to Error folder=" + bStatus);
	            	 break;
	    	}
            
          }
          rfaxConcLogger.debug("iCountBatch= " + iCountBatch +
                             " aBatchArr.length= " + aBatchArr.length);
          if (iCountBatch == aBatchArr.length) {
            rfaxConcLogger.debug(sPath);
            rfaxConcLogger.debug(sDestPath);
            rfaxConcLogger.debug(subfile[i]);
            bStatus = Common.fileMoveToFolder(sCompletePath, sDestPath,
                                              subfile[i], subfile[i]);
            rfaxConcLogger.debug("TXT file moved to success folder=" + bStatus);
            //TXT File moved to temporary folder
            return 0;
          }
        }
        else {
          rfaxConcLogger.debug("File can't be read or Empty file " + sPath);
          return 2;
        }

      }
    }
    else {
      rfaxConcLogger.debug("No File Found in the Directory " + sPath);
      return 1;
    }
    return 3;
  }

  private String[] searchCSVFileInDir(String path) {
    try {
      subfile = null;
      File dir = new File(path);
      rfaxConcLogger.debug(" IN searchTifFileInDir File dir is :" + path);
      FilenameFilter filterFile = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          try {
            return (name.toUpperCase().endsWith(".CSV"));
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

  private String readCsvFile(String file) {
    String sFileData = "";
    FileInputStream fis = null;
    try {
      String record = "";
      File f = new File(file);
      String filename = f.getName();
      fis = new FileInputStream(f);
      BufferedInputStream bis = new BufferedInputStream(fis);
      dis = new DataInputStream(bis);
      int i = 1;
      while ( (record = dis.readLine()) != null) {
        if (sFileData.length() < 3500 * i) {
          sFileData = sFileData + record.trim() + "~";
        }
        else {
          sFileData = sFileData + record.trim() + "~#";
          i++;
        }
      }
      sFileData = sFileData + "#";
    }
    catch (Exception e) {
      rfaxConcLogger.info(e.toString());
      sFileData = "";
    }
    finally {
      try {
        fis.close();
      }
      catch (Exception e) {}

    }
    return sFileData;
  }

  private void processCSVDataForUpload(String sDataStr, String fTxtFilename, String sDestPath) 
  {
    String sCsvFileContent = "", sTifFileName = "", sFILEPATH = "",
        sRecDate = "", sScandate = "", sFormType = "", strQuery = "";
    String sUploadDec[] = new String[2];
    String sNames = "";
    String sVals = "";
    int a = 0;
    String sWhere = "";
    String sActionDate = "";
    String sRFaxNo = "";
    String sBatchName = "";
    String sFileName = "";
    String sLOGID="";
    //String aStringArr []=sDataStr.trim().split("~");
    //hmp.clear();
    String sDestpath = sDestFolderPath + System.getProperty("file.separator") +
        Common.getDateString();
    String RFaxQ = "";
    boolean bFlag = true;
    String sResult = "";
    strQuery = "select ImageName,receiveddate,filename,RFaxQ,FILEPATH,RFaxNo,LOGID from NG_ME_RFAXCONCIL_DETAILS with(nolock) where RecordStatus='created'";
   // rfaxConcLogger.debug("In XML " + strQuery);
    strQuery = mSelectQueryExecute(strQuery);
    //rfaxConcLogger.debug("Out XML "+strQuery);
    
    if (!strQuery.equals(""))
    {
      InstrumentListXmlResponse.setXmlString(strQuery);
      int lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal(
          "TotalRetrieved"));
      if (lPidRetCount > 0) {
        rfaxConcLogger.debug("No of rows in found in Database=" + lPidRetCount);
        strQuery = "";
        a = 0;
        instrumentList = InstrumentListXmlResponse.createList("Records", "Record");
        for (instrumentList.reInitialize(true); instrumentList.hasMoreElements(true);
             instrumentList.skip(true))
        {
          try {
            sTifFileName = "";
            sFILEPATH = "";
            sRecDate = "";
            sScandate = "";
            sFormType = "";
            sUploadDec[0] = "Reconcil Uploaded";
            sUploadDec[1] = "";
            sNames = "";
            sVals = "";
           // sSrcPath = "";

            sTifFileName = instrumentList.getVal("ImageName").trim();
            sRecDate = instrumentList.getVal("receiveddate").trim();
            sFileName = instrumentList.getVal("filename").trim();
            RFaxQ = instrumentList.getVal("RFaxQ").trim();
            sFILEPATH=instrumentList.getVal("FILEPATH").trim();
            sRFaxNo=instrumentList.getVal("RFaxNo").trim();
            sLOGID=instrumentList.getVal("LOGID").trim();
            rfaxConcLogger.debug("sTifFileName " + sTifFileName);
            rfaxConcLogger.debug("sRecDate" + sRecDate);
          }
          catch (Exception f) {
            rfaxConcLogger.debug("Some data is missing in File for record no -" +
                               a);
            sUploadDec[1] = "In Complete : Some data is missing in TXT file";
            sUploadDec[0] = "InComplete";
          }
          strQuery = "";
          if (strQuery.equals("")) {
            if (!sTifFileName.equals("")) {
            	try
            	{
		              strQuery ="select WIName,RecordStatus,ActionDateTime from NG_ME_RFax_APPDetails with(nolock) where imagename='" +
		                  sTifFileName + "' and datediff(d,ReceivedDate,'" + sRecDate +"')=0";
		              sFormType = mSelectQueryExecute(strQuery);
		              if (!sFormType.equals("")) {
		                xmlParser.setInputXML(sFormType);
		               // sSrcPath
		                sFormType = xmlParser.getValueOf("WIName");
		              }
            	}
            	catch(Exception e)
            	{
            		rfaxConcLogger.info("Exception"+e);
            		sFormType="";
            	}
              if (sFormType.equals("")) 
              {
            	  try
            	  {
	                sAttributes = "SOURCE" + ( (char) 21) + "RFax" + ( (char) 25);
	                sAttributes = sAttributes + "ReceivedDate" + ( (char) 21) +
	                    sRecDate + ( (char) 25);
	                
	                sAttributes += "Rcon_RFaxQ" + ( (char) 21) + RFaxQ + ( (char) 25);
	                sAttributes += "Rcon_FaxNo" + ( (char) 21) + sRFaxNo + ( (char) 25);
	                sAttributes += "Rcon_TiffName" + ( (char) 21) + sTifFileName + ( (char) 25);
	                sAttributes += "Rcon_FilePath" + ( (char) 21) + sFILEPATH + ( (char) 25);
	                //sAttributes = sAttributes + "DSTUploadDecision" + ( (char) 21) +"INCOMPLETE" + ( (char) 25);
	                sUploadDec[0]="InComplete";
	                sResult = comm.IntroduceWorkItemWithoutDoc(sProcessDefId,
	                    sAttributes, rfaxConcLogger, sActivityID, sActivityName, sDocType,
	                    sUploadDec,"");
            	  }
            	  catch(Exception e)
            	  {
            		  rfaxConcLogger.info(""+e);
            		  sResult="Exception~"+e;
            	  }
                
                
                String[] sArr = sResult.split("~");
                if (sArr[0].equals("success"))
                {
                  try 
                  {
                	  sUploadDec[0]="Reconcil Uploaded";
                    sFormType = sArr[1];
                    //sNames = "WIName,ErrorDesc,RecordStatus,ReceivedDate,ImageName";
                    sVals = "'" + sArr[1] + "','" + sUploadDec[1] + "','" +
                        sUploadDec[0] + "','"+sRecDate+"','"+sTifFileName+"','"+RFaxQ+"','"+sFILEPATH+"','"+sRFaxNo+"'";
                   // sWhere = "imagename='" + sTifFileName +"' AND ReceivedDate='" + sRecDate + "'";
                   /* sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                              WCUtilityHelper.sessionID, sVals,
                                              sWhere,
                                              "NG_ME_RFAXCONCIL_DETAILS",
                                              sNames);*/
                    sResult=XMLGen.APProcedure(WCUtilityHelper.cabinetName,
                            WCUtilityHelper.sessionID, "", sVals,
                            "NG_ME_CSV_RFAXCONCIL_RECORD_UPDATE");

                    rfaxConcLogger.debug("IN XML: " + sResult);
                    sResult = objWF.execute(sResult);
                    rfaxConcLogger.debug("Out XML: " + sResult);
                    xmlParser.setInputXML(sResult);
                    sResult = xmlParser.getValueOf("MainCode");

                    if (sResult.equals("0")) {
                      rfaxConcLogger.debug(
                          "Record is Updated in DB NG_ME_RFAXCONCIL_DETAILS :" +
                          sResult);
                    }
                    else {
                      rfaxConcLogger.debug(
                          "Record is not inserted in DB NG_ME_RFAXCONCIL_DETAILS table :" +
                          sResult);
                    }
                  }
                  catch (Exception f) {
                    rfaxConcLogger.info(f.toString());
                    rfaxConcLogger.debug("Erorr in Ap Update procedure block");
                  }
                }
                else
                {
                  //hmp.put(sTifFileName,sBatchName);
                  rfaxConcLogger.debug("WI not created for the TIF file " + sTifFileName);
                }
              }
              else
              {
                try 
                {
                	sUploadDec[1] = xmlParser.getValueOf("RecordStatus").trim();
                	sActionDate=xmlParser.getValueOf("ActionDateTime");
                	if("Complete".equalsIgnoreCase(sUploadDec[1]))
                	{
                		sUploadDec[1]="RFAX Uploaded";
                	}
                  sNames = "WIName,ErrorDesc,RecordStatus,FILE_GEN,ActionDateTime";
                  //sUploadDec[0] = "Already RFax Uploaded";
                  sVals = "'" + sFormType + "','" + sUploadDec[0] + "','" +
                      sUploadDec[1] + "','NO','"+sActionDate+"'";
                  sWhere = "imagename='" + sTifFileName +
                      "' AND ReceivedDate='" + sRecDate + "' AND LOGID='"+sLOGID+"'";
                  sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                            WCUtilityHelper.sessionID, sVals,
                                            sWhere, "NG_ME_RFAXCONCIL_DETAILS",
                                            sNames);

                  rfaxConcLogger.debug("IN XML: " + sResult);
                  sResult = objWF.execute(sResult);
                  rfaxConcLogger.debug("Out XML: " + sResult);
                  xmlParser.setInputXML(sResult);
                  sResult = xmlParser.getValueOf("MainCode");

                  if (sResult.equals("0")) {
                    rfaxConcLogger.debug(
                        "Record is Updated in DB NG_ME_RFAXCONCIL_DETAILS :" +
                        sResult);
                  }
                  else {
                    rfaxConcLogger.debug(
                        "Record is not inserted in DB NG_ME_RFAXCONCIL_DETAILS table :" +
                        sResult);
                  }
                }
                catch (Exception f) {
                  rfaxConcLogger.info(f.toString());
                  rfaxConcLogger.debug("Erorr in Ap Update procedure block");
                }

              }
             

            }
            else
            {
              sUploadDec[0] = "InComplete";
              sUploadDec[1] = "Tiff File Name from is not valid " +fTxtFilename;
            }

            

          }
          
        }
        // System.out.println(sCsvFileContent);
        

      }
    }
    else {
      rfaxConcLogger.debug("No Records found  in DB for processing " );
    }
  }

  boolean mWriteCsvFile(String path, String fileName, String sCsvContent) {
    BufferedWriter out = null;
    File checkdirectory = new File(path);

    if (!checkdirectory.exists()) {
      checkdirectory.mkdir();
    }
    try {
      out = new BufferedWriter(new FileWriter(path +
                                              System.getProperty("file.separator") +
                                              fileName));
      out.write("" + sCsvContent + "");
    }
    catch (Exception e) {

    }
    finally {
      try {
        /**
         * close the @out object
         */
        out.close();

      }
      catch (Exception e) {

      }
    }

    return true;
  }

  private String mSelectQueryExecute(String strQuery) {
	  try
	  {
	    strQuery = XMLGen.APSelectWithColumnNames(WCUtilityHelper.cabinetName,WCUtilityHelper.sessionID,
	                                              strQuery);
	    rfaxConcLogger.debug(strQuery);
	    strQuery = objWF.execute(strQuery);
	    rfaxConcLogger.debug(strQuery);
	    xmlParser.setInputXML(strQuery);
	    String sMainCode = xmlParser.getValueOf("MainCode");
	    if (sMainCode.equals("0") && Integer.parseInt(xmlParser.getValueOf("TotalRetrieved")) > 0) 
	    return strQuery;
	    else
	    return "";
	   }
	  catch(Exception e)
	  {
		  return "";
	  }
    
  }

  public void readINI() {
    try {
      Properties ini = new Properties();
      System.out.println("***" + System.getProperty("user.dir"));
  	

	// **********Read the attributes from log4j.properties****************
      ini.load(new FileInputStream(System.getProperty("user.dir") +
                                   System.getProperty("file.separator") +
                                   "WC_RFaxConcil_Config.ini"));

      //threadCount = Integer.parseInt(ini.getProperty("threadCount"));
      //System.out.println("threadCount:\t" + threadCount);

      sProcessDefId = ini.getProperty("ProcessdefID");
     // System.out.println("sProcessDefId:\t" + sProcessDefId);

      sDestFolderPath = ini.getProperty("Destpath");
      System.out.println("DestFolderPath:\t" + sDestFolderPath);

      sPath = ini.getProperty("RFaxFilePath");
      System.out.println("RFaxFilePath:\t" + sPath);

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
      rfaxConcLogger.debug(logstring);
    }
  }

}
