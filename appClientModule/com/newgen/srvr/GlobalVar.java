/**
 * <p>Title: Auto Create Server</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Newgen Software Technologies Ltd.</p>
 * @author Piyush Pratap Singh
 * @version 1.0
 */

package com.newgen.srvr;

public class GlobalVar {
    public static boolean successLog = false;
    public static boolean errorLog = false;
    public static String logPath = System.getProperty("user.dir");
    public static int pollInterval = 20;
    public static String titleName = "Auto Create Server";
    public static String sortTitleName = "AutoCreate";
    public static boolean printScreenflag = false;
    public static String ConstAOFDoc="AOF_";
    public static String ConstDataClassName="AcOpening";
    public static String ConstIntroMedAUTO="AUTO";
    public static String ConstIntroMedOC="OC";
//	public static int iCount=0;
	public static int iCount_RFax=0;
	public static int iCount_Concil=0;
	public static int iCount_auto=0;
	public static int iCount_Rein=0;
	public static int iCount_CSV=0;
	public static int iCount_fin=0;
	public static int iCount_imageexp=0;
	public static int iCount_serv=0;
	//Added By Amit Yadav
	public static int iCount_SIGInt=0;
	public static int iCount_SIGImage=0;
	public static int iCount_SIGFinUpload=0;
	public static int iCount_SIGFinStatus=0;
	//Added by Amit Pradhan
	public static int iCount_ECSAtoInitialize=0;
	public static int iCount_ECSSplitWorkItem=0;
	public static int iCount_ECSSplitRejectworkItem=0;
	public static int iCount_ECSSplitSuccessworkItem=0;
	public static int iCount_ECSSplitHoldcompleteworkItem=0;
	public static int iCount_ECSCSVUpload=0;
	public static int iCount_ECSCRMUpload=0;

	 
}
