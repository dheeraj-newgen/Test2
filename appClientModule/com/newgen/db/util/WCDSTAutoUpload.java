/*---------------------------------------------------------------------------------------------------------

//			                   NEWGEN SOFTWARE TECHNOLOGIES LIMITED
//Group                      : Application Projects
//Product / Project          : IBMWC 
//Module                     : Mail Room Vendor Upload           
//File Name                  : WCDSTAutoUpload.java           
//Author                     : SHAHNAWAJ AKHTAR
//Date written (DD/MM/YYYY) :  
//Description                :This auto program will be responsible for creating workitems from DST text file that will be present on the Network Share location..The functionality of this utility is explained in detail in section 2.1. Other salient features of this utility are as follows:Utility will read text file first and get tiff file name which it will search in the folder and if tiff file is found it will create and introduce the workitem in the workflow.Secondly, utility after reading entire text file and created workitems will then read the folder for any tiff files present in that folder. If found any tiff file it will create workitem and introduce in the workflow.
*/

package com.newgen.db.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.newgen.db.commons.*;
import com.newgen.db.services.WCUtilityService;
import com.newgen.srvr.*;
import com.newgen.srvr.XML.*;
import com.newgen.wfdesktop.xmlapi.*;

public class WCDSTAutoUpload
    implements Runnable {
  private static int threadCount;
  //private static Log logger = null;
  public Logger dstlogger=Logger.getLogger("dstlogger");

  private String sSleepTime = "";
  private String sAttributes = "";
  public String sProcessDefId;
  private String sPath = "";
  private String sDestFolderPath = "";
  private String sDuplicateFolderPath = ""; //Added by Dheeraj
  private int iDirAliveNoOfDays = 5;

  private String sActivityID = "";
  private String sActivityName = "";
  private String sDocType = "";
  private int idelay = 0;
  //added by chhavi
  private String  DeleteFilesIn="";

  // private String dateSuccessFolder="";
  //private String dateErrorFolder="";

  FunctionsWI objWF = null;
  XMLParser xmlParser = new XMLParser();
  DataInputStream dis = null;

  
  //added by akash start
  File[] fDirArr = null;

  File[] pdfDirArr = null;
  private String pdfPath = "";
  private String pdfName = "";
  private String strForm= "";
  //private String strFormName= "";
  //private String strFormType= "";
  private String fileFormat= "";
  
  //added by akash end
  
  
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

  HashMap hmp = new HashMap();

  int iNoOfPages = 0;
  private static String logFlag;

  String folderIndex = "";

  Common comm = new Common();

  public WCDSTAutoUpload(FunctionsWI objWF) {
    this.objWF = objWF;
  }
  
  public WCDSTAutoUpload(String threadName, FunctionsWI objWF) {
    try {
    	
      this.objWF = objWF;
      System.out.println("**************WCDSTAutoUpload Running**************");
      dstlogger.debug("**************WCDSTAutoUpload Running**************" );
      readINI();
      System.out.println("DST ini Read operation completed");
      //logger.debug("WCDSTAutoUpload");
      while (! ( (new File(sPath)).exists())) {

        try {
        	dstlogger.debug("The main File Path Doesn't exist :" + sPath);

        }
        catch (Exception ex) {
        	dstlogger.info("Exception : The thread cannot sleep... :" + ex);
        }
      }

    }
    catch (Exception e) {
      System.out.println("exception");
      dstlogger.info("Error in starting ECSUpload Thread ...." + e);
    }
  }

  public void run() {

    while (true) {
      try {
        //System.out.println("call api call"+sPath);
    	  dstlogger.debug("Inside Run" );
        System.out.println(
            "************** WC-DST-AutoUpload Running **************");
        aDSTLocation = null;
        //sDstLoc = "";
        //function for reading pdf and creating workitem
       
        mProcessDSTLocationTxtFiles();
        
        //processFileRecordByRecord("", "", "");
       // DeleteFiles();
        Thread.sleep(Long.parseLong(sSleepTime) * 1000 * 60);
        //function added by chhavi for deleting files in output folder past i week
       DeleteFiles();
      }
      catch (Exception e) {
        dstlogger.debug("Exception in thread start");
        e.printStackTrace();
      }
    }
  }

  private void DeleteFiles() {
	// TODO Auto-generated method stub
	  dstlogger.debug("Delete files from outputfolder function running" );
      readINI();
      int days=Integer.parseInt(DeleteFilesIn);
      String sDestpath=sDestFolderPath;
      File folder = new File(sDestpath);
      dstlogger.debug("Delete files from outputfolder is:"+sDestpath );
      removeDirectory(folder,days);
}

  public void removeDirectory(File dir,long days) {
		
		//System.out.println("chhavi-2-");
      long purgeTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000);
      dstlogger.debug("Delete files from outputfolder purgeTime:"+purgeTime );
      dstlogger.debug("Delete files from outputfolder dir.lastModified():"+dir.lastModified() );

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

	   //modified by akash for pdf workitem
private void mProcessDSTLocationTxtFiles() {
	
	String sBatchName = comm.getBatchString();
    
	dstlogger.debug("sBatchName  " + sBatchName);
    //for dcn date format
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String dcnDateFormat =dateFormat.format(new Date());
    dstlogger.debug("dcnDateFormat akash: " + dcnDateFormat);
    //for other date format
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String currentDate=dtf.format(now);
    sDstLoc = mFetchDSTLocation();
   
    dstlogger.debug("Location from the Table akash: " + sDstLoc);
    
   strForm = comm.mSelectQueryExecute(strForm, dstlogger);
    
   dstlogger.debug("OUT XML"+strForm);
    
   String strFormName="";
   String strFormType="";
    if (!strForm.equals(""))
    {
      InstrumentListXmlResponse.setXmlString(strForm);
      int lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal(
          "TotalRetrieved"));
      if (lPidRetCount > 0)
      {
    	  strForm = "";
        instrumentList = InstrumentListXmlResponse.createList("Records",
            "Record");
        for (instrumentList.reInitialize(true); instrumentList.hasMoreElements(true);
             instrumentList.skip(true)) 
        {
          //logger.debug("1234567890");
        	strFormName=strFormName + instrumentList.getVal("FormName").trim() + ",";
          strFormType = strFormType + instrumentList.getVal("FormType").trim() + ",";
          
        }
      }
      dstlogger.debug("Form Name String akash " +
    		  strFormName);
      dstlogger.debug("Form Type String akash " +
    		  strFormType);
      
    }
    String strForNameArray[]=strFormName.split(",");
    String strForTypeArray[]=strFormType.split(",");
    //String fileFormatArray[]=fileFormat.split(",");
    String sUploadDec[] = new String[2];
    String sValidReqd = "";
    String extension = "";
    String pdfquery=""; //added by akash for lockissue
    boolean processPdfFlag=false;
    boolean movePdfFlag=false;
    boolean insertflag=false;
    String columnNames="";
    String columnValues="";
    String insertPdfResult="";
    String columnWhere="";
    String wiName="";
    String recordStatus="";
    
    sValidReqd = "NG_ME_DST_DCNNO" + ( (char) 21) + "DCNNO" +
            ( (char) 21) + dcnDateFormat + ( (char) 25);
    dstlogger.debug("sDstLoc akash " +
    		sDstLoc);
    if (!sDstLoc.equals("")) 
    {
      aDSTLocation = sDstLoc.split("~");
      //aDSTLocation = ("O:\\ravi jain\\ME Process\\Docusign\\pdf folder").split("~");
      if (aDSTLocation.length > 0) {
        for (int g = 0; g < aDSTLocation.length; g++) 
        {
          try 
          {
            sPath = aDSTLocation[g];//pdf folder
            if (new File(sPath).exists()) 
            {
            
              fDirArr = comm.getListingDirInPath(sPath);//10 pdf files
              dstlogger.debug("Path for folder akash:" + sPath);
              dstlogger.debug("fDirArr lenth is akash:" + fDirArr.length);
             if (fDirArr.length>0) 
              {
                for (int a = 0; a < fDirArr.length; a++)
                {
                	int counter=0;
                    int counterRetrived=0;
                	int i = fDirArr[a].getName().lastIndexOf('.');
                	    if (i > 0) {
                	        extension = fDirArr[a].getName().substring(i+1);
                	        dstlogger.debug("extension of files is akash: " + extension);
       
                	        if(fileFormat.contains(extension))
                    	    {
                	        String finalFormTypeName=""; 
                	        String sAttributesDocuSign=""; 
                	      
                              dstlogger.debug("Directory for folder search operation is akash: " + fDirArr[a]);
                              dstlogger.debug("fDirArr name is akash:" + fDirArr[a].getName());
                              pdfName=fDirArr[a].getName(); //first pdf name 
                              pdfquery="select * from NG_ME_DST_PaperAPPDetails with(nolock) where ImageName = '"+pdfName+"'"; 
                              dstlogger.debug("Select for pdf query"+pdfquery);
                              pdfquery = comm.mSelectQueryExecute(pdfquery, dstlogger);
                              dstlogger.debug("OUT XML for pdf query"+pdfquery);
                              //Added by megha
                            processPdfFlag=false;
                           	insertflag=false;
                           	movePdfFlag=false;
                              //End
                           	
                              if (!pdfquery.equals(""))
                              {
                            	InstrumentListXmlResponse.setXmlString(pdfquery);
                                int lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal(
                                    "TotalRetrieved"));
                                if (lPidRetCount > 0)
                                {
                                	dstlogger.debug("entry found in table--");
                                	 wiName = "";
                                	 recordStatus="";
                                	 instrumentList = InstrumentListXmlResponse.createList("Records",
                                         "Record");
                                     for (instrumentList.reInitialize(true); instrumentList.hasMoreElements(true);
                                          instrumentList.skip(true)) 
                                     {
                                      
                                    	 wiName= instrumentList.getVal("WIName").trim();
                                    	 recordStatus = instrumentList.getVal("RecordStatus").trim();
                                    	counterRetrived=Integer.parseInt(instrumentList.getVal("COUNTER").trim());
                                    	 dstlogger.debug("wiName--"+wiName);
                                    	 dstlogger.debug("recordStatus--"+recordStatus);
                                    	 dstlogger.debug("counterValue--"+counterRetrived);
                                    	 
                                    	
                                     }

                                     if(recordStatus.equals("Success"))
                                	 {
                                		 dstlogger.debug("filepath -- "+sPath);
                                		 if(sPath.contains("Ricoh")) {
                                			 dstlogger.debug("recordStatus is success so moving this duplicate file to -- "+sDuplicateFolderPath);
                                			 boolean bFlag = Common.fileMoveToFolder(sPath, sDuplicateFolderPath,
                                       		  pdfName, pdfName);
                                			 dstlogger.debug("File Move Status: "+bFlag);
                                		 } else {
                                			 dstlogger.debug("duplicate files found but not moved from -- "+sPath);
                                		 }
                                		 continue;
                                	 }
                                	 else
                                	 {
                                		 if(counterRetrived==3)
                                		 {
                                			 dstlogger.debug("counter value is 3 so utility processed this file 3 times already, skipping this file now.");
                                			 continue;
                                			 
                                		 }
                                		 else
                                		 {
                                			 if(wiName.equals(""))
                                			 {
                                				 dstlogger.debug("record has not winame so sending it to create wi");
                                				 processPdfFlag=true;                           				 
                                				 movePdfFlag=true;
                                				 counter=counterRetrived+1;
                                			 }
                                			 else
                                			 {
                                				 dstlogger.debug("record has winame so will try to move it only");
                                				 //Added by megha
                                				 String isDocAttached=getDocList(wiName);
                                				 dstlogger.debug("is doc attached"+isDocAttached);
                                				 if(isDocAttached.equals("NoDoc")){
                                					 folderIndex=getFolderIndex(wiName);
                                					 //Updatde by megha changed sDSTPath to sPath
                                					 String addDoc=comm.addDocument(Short.parseShort(WCUtilityHelper.VolumeID),sPath+System.getProperty("file.separator")+pdfName,folderIndex,"RequestDocument",dstlogger);
                                                 	dstlogger.debug("Add doc status:-"+addDoc);
                                					 if(addDoc.equals("Errorss")){
                                                 		//Update counter
                                                 		String result = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                                                WCUtilityHelper.sessionID, "'"+counterRetrived+1+"'",
                                                                "ImageName='"+pdfName+"'", "NG_ME_DST_PaperAPPDetails",
                                                                "COUNTER");
                                                 		dstlogger.debug("Counter update result"+result);
                                                 		continue;
                                                 	}else{
                                                 		movePdfFlag=true;
                                                 	}
                                				 }else if(isDocAttached.equals("DocAttached")){
                                					 movePdfFlag=true;
                                				 }
                                				 //End
                                				 counter=counterRetrived+1;
                                			 }
                                		 }
                                		 
                                	 }
                                   
                                  
                                }
                                                           
                              }
                              else
                              {                             	
                             	processPdfFlag=true;
                             	insertflag=true;
                             	movePdfFlag=true;
                              	dstlogger.debug("entry not found in table--" +processPdfFlag );
                              }
                     		 dstlogger.debug("pdf name is akash:" + pdfName);
                     		 
                                try 
                                {
                                aa:
                                	for(int b=0;b<strForTypeArray.length;b++)//ccp,b,c
                                	{
                                		           	
                                		dstlogger.debug("strForNameArray name is akash:" + strForNameArray[b]);
                                		 if(pdfName.contains(strForNameArray[b]))
                                		 {
                                			 if(strForNameArray[b].equals("DSNP"))
                                			 {
                                				 sAttributes = "DSNP" + ( (char) 21) + "1" + ( (char) 25); 
                                			 }
                                			 else
                                			 {
                                				 sAttributes="";
                                			 }
                                			 finalFormTypeName=strForTypeArray[b];
                                			 dstlogger.debug("finalFormTypeName is akash:" + finalFormTypeName);
                                			 
                                			 break aa;
                                			 
                                		 }
                                		 else
                                		 {
                                			 finalFormTypeName="";
                                			 dstlogger.debug("finalFormTypeName is akash:" + finalFormTypeName);
                                		 }
                                		 
                                		  
                                	 
                                	}
                                dstlogger.debug("CurrentDate is akash:" + currentDate);
                             // Added by Amit for DSNP Member Association formtype on 18-Jul-2023
                   			 if(sPath.contains("DSNP Validation"))
                   			 {
                   				finalFormTypeName="DSNP Member Association";
                   				if (pdfName.contains("DSNP Member Association")) {
                                    String extractedValue = pdfName.substring(pdfName.lastIndexOf('_') + 1);
                                    int dotIndex = extractedValue.lastIndexOf('.');
                                    if (dotIndex != -1) {
                                        extractedValue = extractedValue.substring(0, dotIndex); 
                                    }
                                    
                                    sAttributesDocuSign = sAttributesDocuSign + "SubscriberID" + ((char) 21) + extractedValue + ((char) 25);
                                }
                   			 }
                                sAttributesDocuSign = sAttributesDocuSign+"SOURCE" + ( (char) 21) + "PaperApplication" + ( (char) 25);
                                sAttributesDocuSign = sAttributesDocuSign + "DCN" + ( (char) 21) + dcnDateFormat +
                                    ( (char) 25);
                               sAttributesDocuSign = sAttributesDocuSign + "ReceivedDate" + ( (char) 21) +
                                		currentDate + ( (char) 25);
                                sAttributesDocuSign = sAttributesDocuSign + "ScannedDate" + ( (char) 21) +
                                		currentDate + ( (char) 25);
                                sAttributesDocuSign = sAttributesDocuSign + "FormType" + ( (char) 21) + finalFormTypeName +
                                    ( (char) 25);
                                // Added by Amit for DSNP Member Association formtype on 18-Jul-2023
                                
                                
                                dstlogger.debug("Attributes are akash:" + sAttributesDocuSign);
                                //sDstLoc=(sDstLoc.trim()+System.getProperty("file.separator")+pdfName).trim();
                                dstlogger.debug("sDstLoc name is akash:" + sDstLoc);
                                dstlogger.debug("sProcessDefId is akash:" + sProcessDefId);
                                dstlogger.debug("sActivityID name is akash:" + sActivityID);
                                dstlogger.debug("sActivityName name is akash:" + sActivityName);
                                //addded by akash for pdflock issue
                                if(processPdfFlag){
                                	if(insertflag)
                                	{
                                columnNames="Source,DCN,FormType,ReceivedDate,ScannedDate,ImageName,WIName,RecordStatus,COUNTER";
                                columnValues="'PaperApplication','" + dcnDateFormat + "','" + finalFormTypeName + "','" + currentDate + "','" + currentDate + "','" + pdfName + "','','Pending',0";
                                
                                insertPdfResult=XMLGen.APInsert(WCUtilityHelper.cabinetName, WCUtilityHelper.sessionID, "NG_ME_DST_PaperAPPDetails", columnNames, columnValues);
       
								  dstlogger.debug("IN XML NG_ME_DST_PaperAPPDetails: " + insertPdfResult);
						      insertPdfResult = objWF.execute(insertPdfResult);
						      dstlogger.debug("Out XML NG_ME_DST_PaperAPPDetails: " + insertPdfResult);
						      xmlParser.setInputXML(insertPdfResult);
						      insertPdfResult = xmlParser.getValueOf("MainCode");
						
						      if (insertPdfResult.equals("0")) {
						        dstlogger.debug(
						            "Image is new so inserted in DB NG_ME_DST_PaperAPPDetails :" +
						            insertPdfResult);
						      }
						      else {
						        dstlogger.debug(
						            "Image is new but is not inserted in DB NG_ME_DST_PaperAPPDetails table due to reason:" +
						            insertPdfResult);
						        //Added by megha
						        if(insertPdfResult.equals("11")){
						        	//reconnect
						        	comm.reconnectToWorkflow();
						        }
						        continue;
						        //End
						      }
                                	}
                              
                                //for creating workitem with pdfs
                                String sResult = comm.IntroduceWorkItemWithoutDoc(sProcessDefId,sAttributesDocuSign,dstlogger,sActivityID,sActivityName,pdfName,sUploadDec,sValidReqd);
                                dstlogger.debug("workitem call result is here:"+sResult);
                                String[] sArr = sResult.split("~");
                    			dstlogger.debug("check code 31 jan ");
                    			
                    			if (sArr[0].equals("Exception: Error in creating workitem")){
                    			dstlogger.debug("Error in creating workitem please debug");
                    			continue;
                    			}
                    			
                                if (sArr[0].equals("success")) {
                                    //fFileTemp=new File(sSrcPath);
                                	dstlogger.debug("workitem created succesfully");

                                	//updated by megha for updating source location
                                	columnNames = "WIName,COUNTER,SourceLocation";
                                	columnValues = "'" + sArr[1] + "','"+counter+"','"+sPath+"'";
                                	columnWhere = "ImageName='"+pdfName+"'";
              					  
              					  sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                                        WCUtilityHelper.sessionID, columnValues,
                                                        columnWhere, "NG_ME_DST_PaperAPPDetails",
                                                        columnNames);
              										  
              										  
              										  dstlogger.debug("IN XML: " + sResult);
                              sResult = objWF.execute(sResult);
                              dstlogger.debug("Out XML: " + sResult);
                              xmlParser.setInputXML(sResult);
                              sResult = xmlParser.getValueOf("MainCode");

                              if (sResult.equals("0")) {
                                dstlogger.debug(
                                    "Winame and counter is Updated in DB PaperAPPDetails :" +
                                    sResult);
                              }
                              else {
                                dstlogger.debug(
                                    "Winame and counter is not inserted in PaperAPPDetails table :" +
                                    sResult);
                              }
                                	
                                	//for adding the document to workitems
                                	String folderIndex=sArr[3];
                        			dstlogger.debug("folderIndex-----"+folderIndex);
                        			//updated by megha from sDstLoc to sPath
                                	String addDoc=comm.addDocument(Short.parseShort(WCUtilityHelper.VolumeID),sPath+System.getProperty("file.separator")+pdfName,folderIndex,"RequestDocument",dstlogger);
                                	
                        			dstlogger.debug("add document call status:"+addDoc);
                                	//Added by megha
                                	//If doc not added successfully update counter and continue
                                	if(addDoc.equals("Errorss")){
                                		//Update counter
                                 		String result = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                                WCUtilityHelper.sessionID, "'"+counterRetrived+1+"'",
                                                "ImageName='"+pdfName+"'", "NG_ME_DST_PaperAPPDetails",
                                                "COUNTER");
                                 		dstlogger.debug("Counter update result"+result);
                                 		continue;
                                	}
                                	//End
                                
                                  }
                                }
                            	if(movePdfFlag){
                            		String sResult="";
                                    try {
                                    	//for moving the pdffile to specific folder
                                    	//updated by megha from sDstLoc to sPath
                                     boolean bFlag = Common.fileMoveToFolder(sPath, sDestFolderPath,
                                    		  pdfName, pdfName);
                                     dstlogger.debug("File Move Status"+bFlag);
                                     dstlogger.debug("counter value-----"+counter);
                                     columnNames = "COUNTER";
                                   	columnValues = "'"+counter+"'";
                                   	columnWhere = "ImageName='"+pdfName+"'";
                 					  
                 					  sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                                           WCUtilityHelper.sessionID, columnValues,
                                                           columnWhere, "NG_ME_DST_PaperAPPDetails",
                                                           columnNames);
                 										  
                 										  
                 				dstlogger.debug("IN XML for statusupdate: " + sResult);
                                 sResult = objWF.execute(sResult);
                                 dstlogger.debug("Out XML: " + sResult);
                                 xmlParser.setInputXML(sResult);
                                 sResult = xmlParser.getValueOf("MainCode");

                                 if (sResult.equals("0")) {
                                   dstlogger.debug(
                                       "counter Staus is Updated in DB PaperAPPDetails :" +
                                       sResult);
                                 }
                                 else {
                                   dstlogger.debug(
                                       "counter Staus not inserted in PaperAPPDetails table :" +
                                       sResult);
                                 }
                                      if (bFlag) {
                                    	columnNames = "RecordStatus";
                                      	columnValues = "'Success'";
                                      	columnWhere = "ImageName='"+pdfName+"'";
                    					  
                    					  sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                                              WCUtilityHelper.sessionID, columnValues,
                                                              columnWhere, "NG_ME_DST_PaperAPPDetails",
                                                              columnNames);
                    										  
                    										  
                    				dstlogger.debug("IN XML for statusupdate: " + sResult);
                                    sResult = objWF.execute(sResult);
                                    dstlogger.debug("Out XML: " + sResult);
                                    xmlParser.setInputXML(sResult);
                                    sResult = xmlParser.getValueOf("MainCode");

                                    if (sResult.equals("0")) {
                                      dstlogger.debug(
                                          "Record Staus is Updated in DB PaperAPPDetails :" +
                                          sResult);
                                    }
                                    else {
                                      dstlogger.debug(
                                          "Record Staus not inserted in PaperAPPDetails table :" +
                                          sResult);
                                    }
                                      	//updated by megha sDst to spath
                                        dstlogger.debug("Tif File Moved from " + sPath +
                                                           " to Dest Path " + sDestFolderPath);
                                      }
                                    }
                                    catch (Exception e) {
                                      dstlogger.info(e.toString());
                                      dstlogger.debug("Erorr in moving file to sucess folder");
                                    }
                                    
                                }
                                  //sBatchName = fDirArr[a].getName();
                                  //dstlogger.debug("Batch Name is" + sBatchName);
                                  //Bug Correction/B-00003/19-08-2009/resolved by Shahnawaj //////////////////////////
                                 // int status = startDSTSearchOperation(sPath, sBatchName);
                                  ////Bug Correction/B-00003/19-08-2009/resolved by Shahnawaj //////////////////////////
                                  
                                   // processFileRecordByRecord("", "", "");
                                    
                                    //if (status == 0) {
                                    //mProcessTifFilesLeftInTheLocation(sPath, sBatchName);
                                  //}
                                    //logger.debug("Shahnawaj Akhtar"+sBatchName);
                                    //checkForTheTiffComesAfterTxtFiles(sPath,sBatchName);
                                    //logger.debug("Malik"+sBatchName);
                                   // For deleting dir which are already processed and having no Tif File
                                   // sBatchName=sPath +System.getProperty("file.separator")+sBatchName;
                                	//dstlogger.debug("No Tif Files in the directory: "+sBatchName);
                                	// boolean bFlag=comm.mCheckForFolderDeleteTime(sBatchName,iDirAliveNoOfDays,dstlogger);
                                	//if(bFlag)
                                	//dstlogger.debug(sBatchName+ " dir deleted exceeds the max days Time "+iDirAliveNoOfDays);
                                	 // For deleting dir which are already processed and having no Tif File
                                }
                                catch (Exception e) {
                                  dstlogger.info("Exception happened " + e.toString());
                                }
                              
                     		 
                    	    }
                	    }
                	    
                }
              }
              else 
              {
                  dstlogger.debug("No File Found in the directory---"+sPath);
                }
            }
            else {
              dstlogger.debug("Path does not exists " + sPath);
              comm.mProcedureMailFire("Mail Room Vendor Upload Utility Error Found",
                                      sPath + " does not exist. Pls verify.",
                                      dstlogger);
              //Mail Fire for RFax location
            }
          }
          catch (Exception e) {
            dstlogger.info("Some problem happned in processing " + e);
          }
        }

      }
      else {
        dstlogger.debug("No DST location is defined in the Database---");
      }
    }
    else {
      dstlogger.debug("No DST location is defined in the Database---");
    }
    
  }
public String getFolderIndex(String pid){
	String query = "SELECT distinct FolderIndex FROM PDBFolder with(nolock) where Name='"
			+ pid + "'";
	String inXml = XMLGen.APSelect(WCUtilityHelper.cabinetName,
			WCUtilityHelper.sessionID, query);
	dstlogger.debug(inXml);
	String outXml = this.objWF.execute(inXml);
	dstlogger.debug(outXml);
	this.xmlParser.setInputXML(outXml);
	String mainCode = this.xmlParser.getValueOf("MainCode");
	if (this.xmlParser.getValueOf("NoOfRecordsFetched").equals("0")) {
		dstlogger.debug("No data in PDBFolder");
		return "error";

	} else if (!mainCode.equals("0")) {
		dstlogger.debug("Error Fetching Folder index");
		return "error";
	} else {
		return this.xmlParser.getValueOf("Output");
	}
}

public String getDocList(String pid) {
	dstlogger.debug("Inside get doc list method");
	try {
		String query = "SELECT distinct FolderIndex FROM PDBFolder with(nolock) where Name='"
				+ pid + "'";
		String inXml = XMLGen.APSelect(WCUtilityHelper.cabinetName,
				WCUtilityHelper.sessionID, query);
		dstlogger.debug(inXml);
		String outXml = this.objWF.execute(inXml);
		dstlogger.debug(outXml);
		this.xmlParser.setInputXML(outXml);
		String mainCode = this.xmlParser.getValueOf("MainCode");
		if (this.xmlParser.getValueOf("NoOfRecordsFetched").equals("0")) {
			dstlogger.debug("No data in PDBFolder");
			
			//update couter and try to add doc if success move pdf
			return "NoDoc";
			//else updtae the counter

		} else if (!mainCode.equals("0")) {
			dstlogger.debug("Error Fetching Folder index");
			return "error";
		} else {
			//just move pdf
			String folderIndex = this.xmlParser.getValueOf("Output");
			inXml = XMLGen.NGOGetDocumentListExt(WCUtilityHelper.cabinetName,
					WCUtilityHelper.sessionID, folderIndex);
			dstlogger.debug(inXml);
			outXml = this.objWF.execute(inXml);
			dstlogger.debug(outXml);
			this.xmlParser.setInputXML(outXml);
			String status = this.xmlParser.getValueOf("Status");
			if(status.equals("0")){
				if(this.xmlParser.getValueOf("NoOfRecordsFetched").equals("0")){
					return "NoDoc";
				}else{
					return "DocAttached";
				}
			}else{
				return "error";
			}
			
			}
	
	} catch (Exception e) {
		dstlogger.debug("Inside catch getDocList"+e);
		return "error";
	}
}

  void checkForTheTiffComesAfterTxtFiles(String sPath, String sBatchName)
  {
	  int count=0;
	  String sQry="SELECT COUNT(*)AS COUNT FROM NG_ME_DST_APPDetails WITH(NOLOCK) WHERE " 
		  		+"BATCHNAME='"+sBatchName.trim()+"' AND DATEDIFF(HH,ACTIONDATETIME,GETDATE())>2 AND FILEPATH='"+sPath+"' AND DATEDIFF(d,ACTIONDATETIME,GETDATE())=0 ";
	  try
	  {
		  sQry = comm.mSelectQueryExecute(sQry, dstlogger);
          if (!sQry.equals(""))
          {
            xmlParser.setInputXML(sQry);
            sQry = xmlParser.getValueOf("COUNT");
            if(! "".equals(sQry))
            {
            	count=Integer.parseInt(sQry);
            	if(count>0)
            	{
            		dstlogger.debug("GOing for checkForTheTiffComesAfterTxtFiles Success ");
            		mProcessTifFilesLeftInTheLocation(sPath, sBatchName);
            	}
            }
            
          }
	  }
	  catch (Exception e) {
	        dstlogger.debug("Exception in checkForTheTiffComesAfterTxtFiles");
	        e.printStackTrace();
	      }
  }
  
  
  public void execute(FunctionsWI objWF) {

    //logger = new Log(WCUtilityHelper.LogPath, WCUtilityHelper.LogName, "WCDSTAutoUpload.class");
	  
		 
	  
    //objWF.log = logger;
    System.out.println("Testing-1");
    dstlogger.debug("Launching Thread count :");
    System.out.println("Testing-2");
    Thread DST = new Thread(new WCDSTAutoUpload("DST", objWF));
    System.out.println("Testing-3");
    DST.setName("DST Upload");
    DST.start();

  }

  private String mFetchDSTLocation() {
    String strQuery = "select Imagepath from NG_ME_Paper_FTP_Config_Master with(nolock) where isactive='A'";
    dstlogger.debug("In XML"+strQuery);
    
    strQuery = comm.mSelectQueryExecute(strQuery, dstlogger);
    dstlogger.debug("OUT XML"+strQuery);
    if (!strQuery.equals(""))
    {
   
    	InstrumentListXmlResponse.setXmlString(strQuery);
      
    	int lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal(
          "TotalRetrieved"));
      if (lPidRetCount > 0)
      {
        strQuery = "";
        
        instrumentList = InstrumentListXmlResponse.createList("Records",
            "Record");
        
        for (instrumentList.reInitialize(true); instrumentList.hasMoreElements(true);
             instrumentList.skip(true)) 
        {
        	
          //logger.debug("1234567890");
          strQuery = strQuery + instrumentList.getVal("Imagepath").trim() + "~";
          
          dstlogger.debug("DST Location " +
                             instrumentList.getVal("Imagepath").trim());
        }
      }
      
      return strQuery;
    }
    
    return "";
  }

  private String[] searchTxtFileInDir(String path) {
    try {
      subfile = null;
      File dir = new File(path);
      dstlogger.debug(" IN searchTxtFileInDir File dir is :" + path);
      FilenameFilter filterFile = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          try {
            return (name.toUpperCase().endsWith(".TXT"));
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

  private int startDSTSearchOperation(String sPath, String sBatchName) {
    String sDestPath = ""; ;
    String sDataString = "";
    String aBatchArr[] = null;
    String sInXML = "", sParam = "";
    String sCompletePath = "";
    boolean bStatus = false;
    int iCountBatch = 0;
    sCompletePath = sPath + "/" + sBatchName;
    dstlogger.debug(" In startDSTSearchOperation " + sPath);
    dstlogger.debug("Diectory For operation: " + sPath);
    subfile = searchTxtFileInDir(sCompletePath);
    if (subfile != null) 
    {
    	try
    	{
      sDataString = "";
      sDestPath = "";
      dstlogger.debug("NO of Files Count in Dir is: " + subfile.length + "\n");
      for (int i = 0; i < subfile.length; i++)
      {
        dstlogger.debug("File Name : " + subfile[i]);
        sDestPath = sDestFolderPath + System.getProperty("file.separator") + Common.getDateString() + "_" + sBatchName;
        //sDestPath=sDestFolderPath+System.getProperty("file.separator")+"\\sa";
        aBatchArr = null;

        dstlogger.debug("File Name " + subfile[i] + " is Moved to DestinationPath" + sDestPath);

        sDataString = readTxtFile(sCompletePath +
                                  System.getProperty("file.separator") +
                                  subfile[i]);
        dstlogger.debug("Data From TXT :" + sDataString);
        if (sDataString.length() > 2) {
          aBatchArr = sDataString.split("#");
          dstlogger.debug("No Of Batch data in txt= " + aBatchArr.length);
          for (int a = 0; a < aBatchArr.length; a++) {
            sParam = "'" + aBatchArr[a] + "','" + subfile[i] + "','" +
                sBatchName + "','" + sPath + "'";
            sInXML = XMLGen.APProcedure(WCUtilityHelper.cabinetName,
                                        WCUtilityHelper.sessionID, "", sParam,
                                        "NG_ME_SP_DST_FILE_UPLOAD");
            dstlogger.debug(sInXML);
            sInXML = objWF.execute(sInXML);
            dstlogger.debug(sInXML);
            xmlParser.setInputXML(sInXML);
            sInXML = xmlParser.getValueOf("MainCode");
            if (sInXML.equals("0")) {
              iCountBatch++;
            }
            else
            {
            	sDestPath = sDestFolderPath + System.getProperty("file.separator") +
                Common.getDateString() + "_Error"  ;
            	// bStatus = Common.fileMoveToFolder(sCompletePath, sDestPath,subfile[i], subfile[i]);
            	 dstlogger.debug("TXT file is present in folder= " + sCompletePath);
            	 comm.mProcedureMailFire("Mail Room Vendor Upload Utility Improper FILE Found",
            			 "File is present at path " + sCompletePath + " at Server. Pls action.",
                         dstlogger);
            	 break;
            }
          }
          dstlogger.debug("iCountBatch= " + iCountBatch +
                             " aBatchArr.length= " + aBatchArr.length);
          if (iCountBatch == aBatchArr.length) {
            dstlogger.debug(sPath);
            dstlogger.debug(sDestPath);
            dstlogger.debug(subfile[i]);
            bStatus = Common.fileMoveToFolder(sCompletePath, sDestPath,
                                              subfile[i], subfile[i]);
            dstlogger.debug("TXT file moved to success folder=" + bStatus);
            //TXT File moved to temporary folder
            return 0;
          }
        }
        else {
          dstlogger.debug("File can't be read or Empty file " + sPath);
          return 2;
        }

      }
      /////
    	}
    	catch(Exception e)
    	{
    		dstlogger.debug("Some problem happened at the time processing txt file " + sPath);
    	}
    }
    else {
      dstlogger.debug("No File Found in the Directory " + sPath);
      return 1;
    }
    return 3;
  }
  
  private void processFileRecordByRecord(String sDataStr, String fTxtFilename,
                                         String sDestPath) {
	  System.out.println(" 414iinside processfile record by record" );
	  dstlogger.debug("415iinside processfile record by record ");
    String sDcnNo = "", sTifFileName = "", sFrmCode = "", sRecDate = "",
        sScandate = "", sFormType = "", strQuery = "";
    String sUploadDec[] = new String[2];
    String sNames = "";
    String sVals = "";
    int a = 0;
    String sWhere = "";
    String sActionDate = "";
    String sSysDate = "";
    String sBatchName = "";
    String sValidReqd = "";
    //String aStringArr []=sDataStr.trim().split("~");
    hmp.clear();
    String sDestpath = "";
    String sSrcPath = "";
    
    boolean bFlag = true;
    //Added condition  counter<4 by chhavi
   // System.out.println("iinside processfile record by record scounter" +scounter);
    strQuery = "select ImageName,BatchName,DCN,formcode,receiveddate,scanneddate,FILEPATH,Actiondatetime,datediff(HH,Actiondatetime,getDate()) as sysdate,COUNTER,logid from NG_ME_DST_APPDetails with(nolock) where RecordStatus='created' and IS_Duplicate='N' and counter<4 order by Actiondatetime ";
    //added by chhavi
    //System.out.println("In XML " + strQuery);
 
    dstlogger.debug("In XML " + strQuery);
    strQuery = comm.mSelectQueryExecute(strQuery, dstlogger);
    System.out.println("Out XML "+strQuery);
    dstlogger.debug("Out XML " + strQuery);
    dstlogger.info("Out XML " + strQuery);
    InstrumentListXmlResponse.setXmlString(strQuery);
    if (!strQuery.equals("")) {
      int lPidRetCount = Integer.parseInt(InstrumentListXmlResponse.getVal(
          "TotalRetrieved"));
      if (lPidRetCount > 0) {
        dstlogger.debug("No of rows in file=" + lPidRetCount);
        strQuery = "";
        a = 0;
        instrumentList = InstrumentListXmlResponse.createList("Records", "Record");
        for (instrumentList.reInitialize(true); instrumentList.hasMoreElements(true);
             instrumentList.skip(true)) {
          try 
          {
        	  System.out.println("in try ");
            sDcnNo = "";
            sTifFileName = "";
            sFrmCode = "";
            sRecDate = "";
            sScandate = "";
            sFormType = "";
            sUploadDec[0] = "Complete";
            sUploadDec[1] = "";
            sNames = "";
            sVals = "";
            sSrcPath = "";
//          Bug Correction/B-00002/19-08-2009/resolved by Shahnawaj //////////////////////////

            //System.out.println("aStringArr[a]="+aStringArr[a]);
            sTifFileName = instrumentList.getVal("ImageName").trim();
            sDcnNo = instrumentList.getVal("DCN").trim();
            sFrmCode = instrumentList.getVal("formcode").trim();
            sRecDate = instrumentList.getVal("receiveddate").trim();
            sScandate = instrumentList.getVal("scanneddate").trim();
            sSrcPath = instrumentList.getVal("FILEPATH").trim();
            sActionDate = instrumentList.getVal("Actiondatetime").trim();
            sSysDate = instrumentList.getVal("sysdate").trim();
            sBatchName = instrumentList.getVal("BatchName").trim();      
            sSrcPath=(sSrcPath.trim()+System.getProperty("file.separator")+sBatchName.trim()+System.getProperty("file.separator")+sTifFileName).trim();
            /*logger.debug("sTifFileName "+sTifFileName);logger.debug("sDcnNo "+sDcnNo);
                   logger.debug("sFrmCode "+sFrmCode);logger.debug("sRecDate"+sRecDate);
                   logger.debug("sScandate "+sScandate);
                   //break;*/
//          Bug Correction/B-00002/19-08-2009/resolved by Shahnawaj //////////////////////////
            dstlogger.debug("sSrcPath = "+sSrcPath);
            //addewd by chhavi
           int  scounter = Integer.parseInt(this.instrumentList.getVal("COUNTER").trim());
            int slogID = Integer.parseInt(this.instrumentList.getVal("logid").trim());
            System.out.println("Out XML "+scounter);
            scounter++;
            
         //   System.out.println("chhaviiiiiiiiiiiiiiiiiiiiii111111111111111 "+scounter);
            updateCounterInDB(String.valueOf(scounter), String.valueOf(slogID));
         //   System.out.println("chhaviiiiiiiiiiiiiiiiiiiiii22222222222222222 ");
            dstlogger.debug("value of conter-" + scounter);
         //   System.out.println("value of counter ater update from table-" + Integer.parseInt(this.instrumentList.getVal("COUNTER").trim()));
            if (scounter == 4) 
            {
              this.comm.mProcedureMailFire_UploadUtility("Utility tried processing 3 times ",sTifFileName+ " Utility tried processing 3 times",  dstlogger);
            }
          }
          catch (Exception f) 
          {
            dstlogger.debug("Some data is missing in File for record no -" +
                               a);
            sUploadDec[1] = "In Complete : Some data is missing in TXT file";
            sUploadDec[0] = "InComplete";
          }
          strQuery = "";
          if (strQuery.equals(""))
          {
            if (!sFrmCode.equals(""))
            {
              strQuery =
                  "select FormType from NG_ME_FormType_Master with(nolock) where FormCode='" +
                  sFrmCode + "'";
              sFormType = comm.mSelectQueryExecute(strQuery, dstlogger);
              if (!sFormType.equals(""))
              {
                xmlParser.setInputXML(sFormType);
                sFormType = xmlParser.getValueOf("FormType");
              }
              if (!sFormType.equals("")) 
              {
                sUploadDec[0] = "Complete";
              }
              else
              {
            	sFormType="NONE";
                sUploadDec[0] = "Unmapped";
                sUploadDec[1] = "Form type is not found in DB for form code =" +
                    sFrmCode;
              }
            }
            else 
            {
              sUploadDec[0] = "InComplete";
              sUploadDec[1] = "Form Code is not found in txt file " +
                  fTxtFilename;
            }
            sAttributes = "SOURCE" + ( (char) 21) + "Mail Room Vendor" + ( (char) 25);
            sAttributes = sAttributes + "DCN" + ( (char) 21) + sDcnNo +
                ( (char) 25);
            sAttributes = sAttributes + "FormCode" + ( (char) 21) + sFrmCode +
                ( (char) 25);
            sAttributes = sAttributes + "ReceivedDate" + ( (char) 21) +
                sRecDate + ( (char) 25);
            sAttributes = sAttributes + "ScannedDate" + ( (char) 21) +
                sScandate + ( (char) 25);
            sAttributes = sAttributes + "FormType" + ( (char) 21) + sFormType +
                ( (char) 25);

            dstlogger.debug("FormType= "+sFormType);

            sValidReqd = "NG_ME_DST_DCNNO" + ( (char) 21) + "DCNNO" +
                ( (char) 21) + sDcnNo + ( (char) 25);

            String sResult = comm.IntroduceWorkItem(sSrcPath, sDestPath,
                sTifFileName, sProcessDefId, sAttributes, dstlogger, sActivityID,
                sActivityName, sDocType, sUploadDec, sValidReqd, idelay);
            //System.out.println(sResult);
            String[] sArr = sResult.split("~");
			dstlogger.debug("check code 31 jan ");
			if (sArr[0].equals("Exception: Error in creating workitem")){
			dstlogger.debug("check code 31 jan 2nd check");
			continue;
			}
			
            if (sArr[0].equals("success")) {
              //fFileTemp=new File(sSrcPath);
              //logger.debug("getAbsolutePath "+fFileTemp.getPath());
              sDestpath = sDestFolderPath + System.getProperty("file.separator") +
                  Common.getDateString() + "_" + sBatchName;
              try {
                bFlag = Common.fileMoveToFolderWithoutSrc(sSrcPath, sDestpath,
                    sTifFileName, sTifFileName);
                if (bFlag) {
                  dstlogger.debug("Tif File Moved from " + sSrcPath +
                                     " to Dest Path " + sDestpath);
                }
              }
              catch (Exception e) {
                dstlogger.info(e.toString());
                dstlogger.debug("Erorr in moving file to sucess folder");
              }
              try {
            	  strQuery="";
            	  if (sArr[1].trim().equals("WI-Already-Exists"))
            	  {
            		  strQuery ="select WINAME from NG_ME_DST_APPDetails with(nolock) where DCN='" +
            		  sDcnNo + "' AND WINAME IS NOT NULL AND WINAME <> 'null' AND WINAME <> ''";
            		  strQuery = comm.mSelectQueryExecute(strQuery, dstlogger);
                      if (!strQuery.equals("")) {
                        xmlParser.setInputXML(strQuery);
                        sArr[1] = xmlParser.getValueOf("WINAME");
                        //strQuery="";               
                        sNames = "WIName,ErrorDesc,RecordStatus,IS_Duplicate";
                        sVals = "'" + sArr[1] + "','" + sUploadDec[1] + "','DUPLICATE','Y'";
                        sWhere = "DCN='" + sDcnNo + "' AND IS_Duplicate='N' AND ActionDateTime='"+sActionDate+"'";
                      }
            		  
            	  }
            	  else
            	  {
            		  sNames = "WIName,ErrorDesc,RecordStatus";
                      sVals = "'" + sArr[1] + "','" + sUploadDec[1] + "','" +sUploadDec[0] + "'";
                      sWhere = "DCN='" + sDcnNo + "' AND IS_Duplicate='N' AND ActionDateTime='"+sActionDate+"'";
            	  }
            	  
            	  
                
                sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
                                          WCUtilityHelper.sessionID, sVals,
                                          sWhere, "NG_ME_DST_APPDetails",
                                          sNames);

                dstlogger.debug("IN XML: " + sResult);
                sResult = objWF.execute(sResult);
                dstlogger.debug("Out XML: " + sResult);
                xmlParser.setInputXML(sResult);
                sResult = xmlParser.getValueOf("MainCode");

                if (sResult.equals("0")) {
                  dstlogger.debug(
                      "Record is Updated in DB NG_ME_DST_APPDetails :" +
                      sResult);
                }
                else {
                  dstlogger.debug(
                      "Record is not inserted in DB NG_ME_DST_APPDetails table :" +
                      sResult);
                }
              }
              catch (Exception f) {
                dstlogger.debug(f.toString());
                dstlogger.debug("Erorr in Ap Update procedure block");
              }
            }
            else if (sArr[1].equals("DocNotFound")) {
              bFlag = false;
              dstlogger.debug("GOing for checking 2 hours time for the case");

              bFlag = Integer.parseInt(sSysDate) > 2;

              if (bFlag) {
                sUploadDec[0] = "InComplete";
                sUploadDec[1] ="Doc not Found Even After 2 hours of TXT upload Case Uploaded";
                sAttributes = "SOURCE" + ( (char) 21) + "Mail Room Vendor" + ( (char) 25);
                sAttributes = sAttributes + "DCN" + ( (char) 21) + sDcnNo +( (char) 25);
	            sAttributes = sAttributes + "FormCode" + ( (char) 21) + sFrmCode +( (char) 25);
	            sAttributes = sAttributes + "ReceivedDate" + ( (char) 21) +sRecDate + ( (char) 25);
	            sAttributes = sAttributes + "ScannedDate" + ( (char) 21) +sScandate + ( (char) 25);
	            sAttributes = sAttributes + "FormType" + ( (char) 21) + sFormType + ( (char) 25);
	            sValidReqd = "NG_ME_DST_DCNNO" + ( (char) 21) + "DCNNO" +
                ( (char) 21) + sDcnNo + ( (char) 25);
                sResult = comm.IntroduceWorkItemWithoutDoc(sProcessDefId,sAttributes, dstlogger, sActivityID, sActivityName, sDocType,sUploadDec,sValidReqd);
                sArr = sResult.split("~");
                if (sArr[0].equals("success")) {
                	try
                	{     
                	  if (sArr[1].trim().equals("WI-Already-Exists"))
                  	  {
                  		  strQuery ="select WINAME from NG_ME_DST_APPDetails with(nolock) where DCN='" +
                  		  sDcnNo + "' AND  WINAME IS NOT NULL AND WINAME <> 'null' AND WINAME <> ''";
                  		  strQuery = comm.mSelectQueryExecute(strQuery, dstlogger);
                            if (!strQuery.equals("")) {
                              xmlParser.setInputXML(strQuery);
                              sArr[1] = xmlParser.getValueOf("WINAME");
                              //strQuery="";
                              sNames = "WIName,ErrorDesc,RecordStatus,IS_Duplicate";
                              sVals = "'" + sArr[1] + "','" + sUploadDec[1] + "','DUPLICATE','Y'";
                              sWhere = "DCN='" + sDcnNo + "' AND IS_Duplicate='N' AND ActionDateTime='"+sActionDate+"'";
                            }
                  		  
                  	  }
                  	  else
                  	  {
                  		  sNames = "WIName,ErrorDesc,RecordStatus";
                            sVals = "'" + sArr[1] + "','" + sUploadDec[1] + "','" +
                                sUploadDec[0] + "'";
                            sWhere = "DCN='" + sDcnNo + "' AND IS_Duplicate='N' AND ActionDateTime='"+sActionDate+"'";
                  	  }
                		
		                  /*sNames = "WIName,ErrorDesc,RecordStatus";
		                  sVals = "'" + sArr[1] + "','" + sUploadDec[1] + "','" + sUploadDec[0] + "'";
		                  sWhere = "DCN='" + sDcnNo + "' AND IS_Duplicate='N'";*/
		                  
		                  
		                  
		                  sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,
		                                            WCUtilityHelper.sessionID, sVals,
		                                            sWhere, "NG_ME_DST_APPDetails",
		                                            sNames);
		
		                  dstlogger.debug("IN XML: " + sResult);
		                  sResult = objWF.execute(sResult);
		                  dstlogger.debug("Out XML: " + sResult);
		                  xmlParser.setInputXML(sResult);
		                  sResult = xmlParser.getValueOf("MainCode");
		
		                  if (sResult.equals("0")) {
		                    dstlogger.debug("Record is Updated in DB NG_ME_DST_APPDetails :" +sResult);
		                  }
		                  else {
		                    dstlogger.debug(
		                        "Record is not inserted in DB NG_ME_DST_APPDetails table :" +
		                        sResult);
		                  }
		                }
            	 catch(Exception e)
                 {
                 	dstlogger.debug("Some Exception happened while processing case which has completed 2 hors time" + e);
                 }
                }
               
              }
              else {
                dstlogger.debug("2 hours are not completed for the entry");
                hmp.put(sTifFileName, sBatchName);
              }
            }
            else {
              hmp.put(sTifFileName, sBatchName);
              dstlogger.debug("WI not created for the TIF file " +
                                 sTifFileName);
            }
          }
          else {
            dstlogger.debug("WI already existing for the Record " +
                               sTifFileName);
            dstlogger.debug("And workitem name is : " + strQuery);
          }
        }
      }
    }
    else {
      dstlogger.debug("No Records " + sTifFileName);
    }
  }

  private void updateCounterInDB(String scounter, String logID)
  {
	 // System.out.println("chhaviiiiiiiiiiiiiiiiiiiiii33333333333333 "+scounter); 
//    String sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName, 
//      WCUtilityHelper.sessionID, scounter, 
//      "logID=" + logID, "NG_ME_DST_APPDetails", 
//      "COUNTER");
	  String sResult = "", sNames = "", sVals = "" ,sWhere = "";
	  sNames = "COUNTER";
      sVals = "'" + scounter + "'";
      sWhere = "logid=" + logID + " ";

	    sResult = XMLGen.APUpdate(WCUtilityHelper.cabinetName,WCUtilityHelper.sessionID, sVals,sWhere, "NG_ME_DST_APPDetails", sNames);
	    
	    System.out.println("update called "+sResult); 
	    sResult = objWF.execute(sResult);
	    System.out.println("Out XML: " + sResult);
        xmlParser.setInputXML(sResult);
        sResult = xmlParser.getValueOf("MainCode");
        if (sResult.equals("0")) {
        	System.out.println(
              "Record is inserted in DB NG_ME_DST_APPDetails :" + sResult);
        }
        else {
        	System.out.println(
              "Record is not inserted in DB NG_ME_DST_APPDetails table :" +
              sResult);
        }
				  
  }

private void mProcessTifFilesLeftInTheLocation(String sPath,
                                                 String sBatchName) {

    String sResult = "", sNames = "", sVals = "";
    String sDestPath = "";
    String sUploadDec[] = new String[2];
    subfile = null;
    boolean bFlag = false;
    String sCompletePath = "";
    sCompletePath = sPath + "/" + sBatchName;
    dstlogger.debug("mProcessTifFilesLeftInTheLocation= "+sCompletePath);
    subfile = searchTxtFileInDir(sCompletePath);
    if (subfile.length<1) {
    subfile = searchTifFileInDir(sCompletePath);
    if (subfile .length>0) {
      sDestPath = sDestFolderPath + System.getProperty("file.separator") +
          Common.getDateString() + "_" + sBatchName;
      dstlogger.debug("NO of Tif Files Count Left in Dir is: " +
                         subfile.length + "\n");
      for (int i = 0; i < subfile.length; i++) {

        dstlogger.debug("File Name : " + subfile[i]);
        bFlag = mChkTifForExistingRecord(subfile[i], sBatchName);
        if (bFlag) {
          sUploadDec[0] = "InComplete";
          sUploadDec[1] = "";
          sAttributes = "SOURCE" + ( (char) 21) + "Mail Room Vendor" + ( (char) 25);
          sNames = sCompletePath + "/" + subfile[i];
          sResult = comm.IntroduceWorkItem(sNames, sDestPath, subfile[i],
                                           sProcessDefId, sAttributes, dstlogger,
                                           sActivityID, sActivityName, sDocType,
                                           sUploadDec, "", idelay);
          String[] sArr = sResult.split("~");
          if (sArr[0].equals("success")) {
            bFlag = Common.fileMoveToFolderWithoutSrc(sNames, sDestPath,
                subfile[i], subfile[i]);
            if (bFlag) {
              dstlogger.debug("Tif File Moved from " + sNames +
                                 " to Dest Path " + sDestPath);
            }
            sNames = "BatchName,DCN,FormCode,ReceivedDate,ScannedDate,ImageName,FileName,ActionDateTime,WIName,ErrorDesc,RecordStatus,is_duplicate";
            sVals = "'"+sBatchName+"','','','','','" + subfile[i] + "','" + subfile[i] +
                "',getdate(),'" + sArr[1] + "','" + sUploadDec[1] + "','" +
                sUploadDec[0] + "','N'";
            sResult = XMLGen.APInsert(WCUtilityHelper.cabinetName,
                                      WCUtilityHelper.sessionID,
                                      "NG_ME_DST_APPDetails", sNames, sVals);
            dstlogger.debug("IN XML: " + sResult);
            sResult = objWF.execute(sResult);
            dstlogger.debug("Out XML: " + sResult);
            xmlParser.setInputXML(sResult);
            sResult = xmlParser.getValueOf("MainCode");
            if (sResult.equals("0")) {
              dstlogger.debug(
                  "Record is inserted in DB NG_ME_DST_APPDetails :" + sResult);
            }
            else {
              dstlogger.debug(
                  "Record is not inserted in DB NG_ME_DST_APPDetails table :" +
                  sResult);
            }
          }
          else {
            dstlogger.debug("WI not created for the TIF file " + subfile[i]);
          }
        }
        else {

        }
      }
    }
    else
    {
//    	For deleting dir which are already processed and having no Tif File
    	dstlogger.debug("No Tif Files in the directory: "+sCompletePath);
    	bFlag=comm.mCheckForFolderDeleteTime(sCompletePath,iDirAliveNoOfDays,dstlogger);
    	if(bFlag)
    	dstlogger.debug(sCompletePath+ " dir deleted exceeds the max days Time "+iDirAliveNoOfDays);
    }
    }
    else
    {
    	dstlogger.debug("Processing Tiff file left in the folder: TXT file still present in the folder");
    }
  }

  private boolean mChkTifForExistingRecord(String sFileName, String sBatchName) {
    dstlogger.debug("-- in ChkTifForExistingRecord method--");
    dstlogger.debug("Key Hash Map is--" + sFileName);
    dstlogger.debug("Batch Name is--" + sFileName);
    String sHashRetVal = "";
    try {
      dstlogger.debug("Hash map size is: " + hmp.size());
      sHashRetVal = hmp.get(sFileName).toString();
      dstlogger.debug("Hash Returned Batch Name is--" + sHashRetVal);
      if (sBatchName.equalsIgnoreCase(sHashRetVal)) {
        return false;
      }
      else {
        return true;
      }
    }
    catch (Exception e) {
      dstlogger.debug("mChkTifForExistingRecord: Exception");
      dstlogger.info(e.toString());
      return true;
    }
    //return false;
  }

  private String readTxtFile(String file) {
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
        	record=record.trim();
        	if(! "".equals(record))
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
      dstlogger.info(e.toString());
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

  private String[] searchTifFileInDir(String path) {
    try {
      subfile = null;
      File dir = new File(path);
      dstlogger.debug(" IN searchTifFileInDir File dir is :" + path);
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
      dstlogger.debug("***" + System.getProperty("user.dir"));
     

		// **********Read the attributes from log4j.properties****************


      ini.load(new FileInputStream(System.getProperty("user.dir") +
              System.getProperty("file.separator")
              +"WC_DST_Config.ini"));
      //ini.load(new FileInputStream("O:\\ravi jain\\ME Process\\NG_ME_UploadToWF\\config INI\\WC_Server_Config.ini"));

      //threadCount = Integer.parseInt(ini.getProperty("threadCount"));
      //System.out.println("threadCount:\t" + threadCount);
      
      sDstLoc=ini.getProperty("MailPath");
      dstlogger.debug("akashMailPath:\t" + sDstLoc);
      
      strForm=ini.getProperty("StrForm");
      dstlogger.debug("akashstrForm:\t" + strForm);
      
      fileFormat=ini.getProperty("fileFormat");
      dstlogger.debug("akashfileFormat:\t" + fileFormat);
      
      sProcessDefId = ini.getProperty("ProcessdefID");
      dstlogger.debug("sProcessDefId:\t" + sProcessDefId);

      sDestFolderPath = ini.getProperty("Destpath");
      dstlogger.debug("DestFolderPath:\t" + sDestFolderPath);
      
      //Added by Dheeraj to move items to Duplicate Folder Nov-22
      sDuplicateFolderPath = ini.getProperty("Duplicatepath");
      dstlogger.debug("DuplicateFolderPath:\t" + sDuplicateFolderPath);

      sPath = ini.getProperty("logPath");
      System.out.println("DSTFilePath:\t" + sPath);

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
      
      idelay = Integer.parseInt(ini.getProperty("RISDelay"));
      
      iDirAliveNoOfDays =Integer.parseInt( ini.getProperty("DirAliveNoOfDays"));
      System.out.println("iDirAliveNoOfDays:\t" + iDirAliveNoOfDays);

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
      dstlogger.debug(logstring);
    }
  }
  
}
