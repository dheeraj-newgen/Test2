/**
 * <p>Title: Auto Create Server</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Newgen Software Technologies Ltd.</p>
 * @author Piyush Pratap Singh
 * @version 1.0
 */

package com.newgen.srvr;

import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
//import java.io.BufferedReader;
//import java.io.FileReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.*;


public class Log {

    private FileWriter out;
    private  String logName = "Log";
   private   String logDate = "";
    private static Calendar calendar;
    private  String logPath ="";
	

    public  void generateLog(String strOutput) {
        try {
						 
			String sNewFileName = "";
			//System.out.println("\n\nlogName: "+logName);
		//System.out.println("\n\nlogDate: "+logDate);

			if(!logDate.equals(""))
			{
				//System.out.println("\n\nInside logName is ...."+logName);
				if(logName.indexOf("DST_log") > -1)
				sNewFileName = logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml";
				else if(logName.indexOf("RFax_log") > -1)
					sNewFileName = logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_RFax+".xml";
				else if(logName.indexOf("RFaxConcil_log") > -1)
				sNewFileName = logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Concil+".xml";
				
				/*else if(logName.indexOf("autoReinitialize_log") > -1)
					sNewFileName = logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Rein+".xml";
				*/		
//				else
//					sNewFileName = logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount+".xml";

			//System.out.println("\n\nInside ... sNewFileName : "+sNewFileName);
			}

			File ff = new File(sNewFileName);

			
				if(!ff.exists())
				{
//				System.out.println("\nFile Exist No....");
				/*if(logName.indexOf("AoaofUpdate_log") > -1)
				{
					GlobalVar.iCount_Aao++;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Aao+".xml", true);
				}
				else if(logName.indexOf("exportImportData") > -1)
				{
					GlobalVar.iCount_Exp++;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Exp+".xml", true);
				}
				else */
				if(logName.indexOf("DST_log") > -1)
				{
					GlobalVar.iCount_auto++;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
				}
				else if(logName.indexOf("RFax_log") > -1)
				{
					GlobalVar.iCount_RFax++;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
				}
				else if(logName.indexOf("RFaxConcil_log") > -1)
				{
					GlobalVar.iCount_Concil++;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
				}
				
			}
		
			long sNewFileNamelength = 0;
			sNewFileNamelength = ff.length();
//			System.out.println("\n sNewFileName = sNewFileNamelength : "+sNewFileName+" = "+sNewFileNamelength);

            if (logDate.equals(getDateString())) 
			{
				if(!(sNewFileNamelength > (5*1024*1024)))
				{
//					System.out.println("\n\nInside ExportImportData ....... Normal.....");
					StringBuffer str = new StringBuffer();
					str.append(DateFormat.getDateTimeInstance(2, 2).format(new Date()));
					str.append("\n");
					str.append(strOutput);
					str.append("\n");
				   println(str.toString());
				}
				else
				{
//					System.out.println("\n In length > Else part\nIncrementing iCount....");
//					System.out.println("\n Inside ExportImportData ....... incrementer.....");

					/*if(logName.indexOf("AoaofUpdate_log") > -1)
					{
						GlobalVar.iCount_Aao++;				
	//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Aao+".xml", true);
					}
					else if(logName.indexOf("exportImportData") > -1)
					{
						GlobalVar.iCount_Exp++;
	//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Exp+".xml", true);
					}
					else */
					if(logName.indexOf("DST_log") > -1)
					{
						GlobalVar.iCount_auto++;
	//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
					}
					if(logName.indexOf("RFax_log") > -1)
					{
						GlobalVar.iCount_RFax++;
	//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
					}
					else if(logName.indexOf("RFaxConcil_log") > -1)
						{
							GlobalVar.iCount_Concil++;
		//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
						}
					
					
//					GlobalVar.iCount++;
					initilizeLog(); // with Current date only
					println(strOutput);
				}
            } 
			else 
			{/*
				if(logName.indexOf("AoaofUpdate_log") > -1)
				{
					GlobalVar.iCount_Aao=1;				
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Aao+".xml", true);
				}
				else if(logName.indexOf("exportImportData") > -1)
				{
					GlobalVar.iCount_Exp=1;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_Exp+".xml", true);
				}
				else */if(logName.indexOf("DST_log") > -1)
				{
					GlobalVar.iCount_auto=1;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
				}
				else if(logName.indexOf("RFax_log") > -1)
				{
					GlobalVar.iCount_RFax=1;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
				}
				else if(logName.indexOf("RFaxConcil_log") > -1)
				{
					GlobalVar.iCount_Concil=1;
//					out = new FileWriter(logPath+"\\"+logName+"_"+logDate+"_"+GlobalVar.iCount_auto+".xml", true);
				}
				
				initilizeLog();
				println(strOutput);
            }
        } catch (FileNotFoundException fnf) {
            initilizeLog();
            generateLog(strOutput + "\n" + fnf.toString());
        } catch (IOException io) {
            initilizeLog();
            generateLog(strOutput + "\n" + io.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Log(String fileName, String dateLog) throws FileNotFoundException,
            IOException {
/*
		System.out.println("\n\n Inside Log 2 param..."+fileName + "_" + dateLog + "_" +GlobalVar.iCount+ ".xml");
		out = new FileWriter(fileName + "_" + dateLog + "_" +GlobalVar.iCount+ ".xml", true); //*/
			/*if(fileName.indexOf("AoaofUpdate_log") > -1)
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_Aao+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_Aao+ ".xml", true);
//				GlobalVar.iCount_Aao ++;
			}
			else if(fileName.indexOf("exportImportData") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_Exp+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_Exp+ ".xml", true);
//				GlobalVar.iCount_Exp ++;
			} //
			else */if(fileName.indexOf("DST_log") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml", true);
//				GlobalVar.iCount ++;
			}
			else if(fileName.indexOf("RFax_log") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_RFax+ ".xml", true);
//				GlobalVar.iCount ++;
			}
			else if(fileName.indexOf("RFaxConcil_log") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_Concil+ ".xml", true);
//				GlobalVar.iCount ++;
			}
			
			logDate = dateLog;
          }
    public Log(String path, String logname,String serviceClass){
    
    	logPath = path;
    	logName = logname;
    	
    	File dir = new File(logPath);
      if (!dir.exists()) {
    	
          boolean  bl= dir.mkdir();
         
      }
    }

    private void println(String x) throws FileNotFoundException, IOException {
        if (x != null) {
            out.write(x);
            out.write("\n");
            out.flush();
        }
    }

    public void closePrintStream() throws IOException {
        out.close();
    }

    private  void initilizeLog() {
        StringBuffer strFilePath = null;
        try {
            strFilePath = new StringBuffer(50);
            strFilePath.append(logPath);
            strFilePath.append(File.separatorChar);
            strFilePath.append(logName);
        
            initializeFile(strFilePath.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            strFilePath = null;
        }
    }

    private  void initializeFile(String fileName) {
      StringBuffer strBuff = new StringBuffer();
        strBuff.append("\n*************************************************************\nCreated On: ");
        strBuff.append(DateFormat.getDateTimeInstance(2, 2).format(new Date()));
        try {
           String dateLog = getDateString();

			
			/*if(fileName.indexOf("AoaofUpdate_log") > -1)
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_Aao+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_Aao+ ".xml", true);
//				GlobalVar.iCount_Aao ++;
			}
			else if(fileName.indexOf("exportImportData") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_Exp+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_Exp+ ".xml", true);
//				GlobalVar.iCount_Exp ++;
			} ///
			else */if(fileName.indexOf("DST_log") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml", true);
//				GlobalVar.iCount ++;
			}
			else if(fileName.indexOf("RFax_log") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_RFax+ ".xml", true);
//				GlobalVar.iCount ++;
			}
			else if(fileName.indexOf("RFaxConcil_log") > -1) // exportImportData AoaofUpdate_log
			{
//				System.out.println("\n\nfileName : In initialize File"+fileName + "_" + dateLog +"_"+GlobalVar.iCount_auto+ ".xml");
				out = new FileWriter(fileName + "_" + dateLog +"_"+GlobalVar.iCount_Concil+ ".xml", true);
//				GlobalVar.iCount ++;
			}
			
            logDate = dateLog;
           println(strBuff.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            fileName = null;
            strBuff = null;

       }
    }

    public static String getDateString() {
        calendar = new GregorianCalendar();
		return "" + calendar.get(5) + (calendar.get(2) + 1) + calendar.get(1);

    }
 
}
