/**
 * <p>Title: Auto Create Server</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Newgen Software Technologies Ltd.</p>
 * @author Shahnawaj Akhtar
 * @version 1.0
 */


package com.newgen.srvr.XML;

import com.newgen.wfdesktop.xmlapi.WFInputXml;


public class XMLGen {


    public XMLGen() {
    }

    public static String WMGetWorkItem(String strEngineName,
                                       String strSessionId,
                                       String strProcessInstanceId,
                                       String strWorkItemId) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMGetWorkItem", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
        wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
        wfInputXml.appendEndCallName("WMGetWorkItem", "Input");
        return wfInputXml.toString();
    }


    public static String WMFetchWorkItemAttribute(String strEngineName,
                                                  String strSessionId,
                                                  String strProcessInstanceId,
                                                  String strWorkItemId,
                                                  String strCountFlag,
                                                  String strType,
                                                  String strComparison,
                                                  String strFilterString,
                                                  String strLength,
                                                  String strNoOfRecordsToFetch,
                                                  String strLastValue) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMFetchWorkItemAttribute", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
        wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
        wfInputXml.appendTagAndValue("ZipBuffer", "Y");
        wfInputXml.appendTagAndValue("CountFlag", strCountFlag);
        wfInputXml.appendTagStart("Filter");
        wfInputXml.appendTagAndValue("Type", strType);
        wfInputXml.appendTagAndValue("Comparison", strComparison);
        wfInputXml.appendTagAndValue("FilterString", strFilterString);
        wfInputXml.appendTagAndValue("Length", strLength);
        wfInputXml.appendTagEnd("Filter");
        wfInputXml.appendTagStart("BatchInfo");
        wfInputXml.appendTagAndValue("NoOfRecordsToFetch",
                                     strNoOfRecordsToFetch);
        wfInputXml.appendTagAndValue("LastValue", strLastValue);
        wfInputXml.appendTagEnd("BatchInfo");
        wfInputXml.appendEndCallName("WMFetchWorkItemAttribute", "Input");
        return wfInputXml.toString();
    }

    public static String APSelect(String strEngineName, String strSessionId, String strQuery) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("APSelect", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("Query", strQuery);
        wfInputXml.appendEndCallName("APSelect", "Input");
        return wfInputXml.toString();
    }
    public static String WMTerminateProcessInstance(String strEngineName,
            String strSessionId,
            String strProcessInstanceId) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMTerminateProcessInstance", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceID", strProcessInstanceId);
        wfInputXml.appendEndCallName("WMTerminateProcessInstance", "Input");
        return wfInputXml.toString();
    }

    public static String WMStartProcess(String strEngineName,
                                        String strSessionId,
                                        String strProcessInstanceId) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMStartProcess", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceID", strProcessInstanceId);
        wfInputXml.appendEndCallName("WMStartProcess", "Input");
        return wfInputXml.toString();
    }

    public static String WFFetchInstrumentsList(String strEngineName,
                                                String strSessionId,
                                                String strCountFlag,
                                                String strQueueId,
                                                String strType,
                                                String strComparison,
                                                String strAttributeName,
                                                String strFilterString,
                                                String strLength,
                                                String strNoOfRecordsToFetch,
                                                String strOrderBy,
                                                String strSortOrder,
                                                String strLastValue,
                                                String strLastProcessInstance,
                                                String strLastWorkItemId,
                                                String strDataFlag) {
    	System.out.println("QueueId>>>>>>>>>"+strQueueId);
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMFetchWorkList", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("CountFlag", strCountFlag);
        wfInputXml.appendTagAndValue("DataFlag", strDataFlag);
        wfInputXml.appendTagAndValue("ZipBuffer", "Y");
        wfInputXml.appendTagStart("Filter");
        wfInputXml.appendTagAndValue("QueueId", strQueueId);
        wfInputXml.appendTagAndValue("Type", strType);
        wfInputXml.appendTagAndValue("Comparison", strComparison);
        wfInputXml.appendTagAndValue("AttributeName", strAttributeName);
        wfInputXml.appendTagAndValue("FilterString", strFilterString);
        wfInputXml.appendTagAndValue("Length", strLength);
        wfInputXml.appendTagEnd("Filter");
        wfInputXml.appendTagStart("BatchInfo");
        wfInputXml.appendTagAndValue("NoOfRecordsToFetch",
                                     strNoOfRecordsToFetch);
        wfInputXml.appendTagAndValue("FetchLockedFlag","Y");
        wfInputXml.appendTagAndValue("OrderBy", strOrderBy);
        wfInputXml.appendTagAndValue("SortOrder", strSortOrder);
        wfInputXml.appendTagAndValue("LastValue", strLastValue);
        wfInputXml.appendTagAndValue("LastProcessInstance",
                                     strLastProcessInstance);
        wfInputXml.appendTagAndValue("LastWorkItem", strLastWorkItemId);
        wfInputXml.appendTagEnd("BatchInfo");
        wfInputXml.appendEndCallName("WMFetchWorkList", "Input");
        return wfInputXml.toString();
    }

    public static String WMCompleteWorkItem(String strEngineName,
                                            String strSessionId,
                                            String strProcessInstanceId,
                                            String strWorkItemId,
                                            String strAuditStatus) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMCompleteWorkItem", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
        wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
        wfInputXml.appendTagAndValue("AuditStatus", strAuditStatus);
        wfInputXml.appendEndCallName("WMCompleteWorkItem", "Input");
        return wfInputXml.toString();
    }

    public static String WFSetAttributes(String strEngineName,
                                         String strSessionId,
                                         String strProcessInstanceId,
                                         String strWorkItemId,
                                         String strAttributeData) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMAssignWorkItemAttributes", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
        wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
        wfInputXml.appendTagAndValue("Attributes", strAttributeData);
        wfInputXml.appendEndCallName("WMAssignWorkItemAttributes", "Input");
        return wfInputXml.toString();
    }

    public static String WMCreateProcessInstance(String strEngineName,
                                                 String strSessionId,
                                                 String strProcessId) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMCreateProcessInstance", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessDefinitionID", strProcessId);
        wfInputXml.appendEndCallName("WMCreateProcessInstance", "Input");
        return wfInputXml.toString();
    }

    public static String get_NGOAddFolder(String strEngineName,
                                          String strUserDBId,
                                          String strFolderName,
                                          String strParentFolderIndex) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("NGOAddFolder", "Input");
        wfInputXml.appendTagAndValue("CabinetName", strEngineName);
        wfInputXml.appendTagAndValue("UserDBId", strUserDBId);
        wfInputXml.appendTagAndValue("FolderName", strFolderName);
        wfInputXml.appendTagAndValue("ParentFolderIndex", strParentFolderIndex);
        wfInputXml.appendEndCallName("NGOAddFolder", "Input");
        return wfInputXml.toString();
    }
    public static String NGOGetDocumentListExt(String cabinetName,
               String sessionID, String folderID) {
    return "<?xml Version=\"1.0\"?>\n"
                + "<NGOGetDocumentListExt_Input>\n"
                + "<Option>NGOGetDocumentListExt</Option>\n"
                + "<LinkDocFlag>N</LinkDocFlag>\n"
                + "<AnnotationFlag>N</AnnotationFlag>\n"
                + "<DocumentType>B</DocumentType>\n"
                + "<CabinetName>" + cabinetName +
                "</CabinetName>\n"
                + "<UserDBId>" + sessionID + "</UserDBId>\n"
                + "<FolderIndex>" + folderID + "</FolderIndex>\n"
                + "<StartPos>1</StartPos>\n"
                + "<RecursiveFlag>N</RecursiveFlag>\n"
                + "<NoOfRecordsToFetch>100</NoOfRecordsToFetch>\n"
                + "<TotalRecCountFlag>Y</TotalRecCountFlag>\n"
                + "<SortOrder>D</SortOrder>\n"
                + "<OrderBy>5</OrderBy>\n"
                + "<DataAlsoFlag>N</DataAlsoFlag>\n"
                + "<ReferenceFlag>B</ReferenceFlag>\n"
                + "</NGOGetDocumentListExt_Input>";



       }

    public static String get_NGOCopyDocumentExt(String strCabinetName,
                                                 String strSessionId,
                                                 String strFolderIndex,
                                                 String strDocumentId,
                                                 String strParentFolderId)
     {
         return "<?xml version=\"1.0\"?>"+
                 "<NGOCopyDocumentExt_Input>"+
                 "<Option>NGOCopyDocumentExt</Option>"+
                 "<CabinetName>"+strCabinetName+"</CabinetName>"+
                 "<UserDBId>"+strSessionId+"</UserDBId>"+
                 "<DestFolderIndex>"+strFolderIndex+"</DestFolderIndex>"+
                 "<Documents>"+
                 "<Document>"+
                 "<DocumentIndex>"+strDocumentId+"</DocumentIndex>"+
                 "<ParentFolderIndex>"+strParentFolderId+"<ParentFolderIndex>"+
                 "</Documents>"+
                 "</NGOCopyDocumentExt_Input>";
}
 public static String APInsert(String strEngineName,String strSessionId,String strFileNetTable,String columns, String strValues) {
return "<?xml version=\"1.0\"?>"+
"<WMTestSelect_Input><Option>APInsert</Option>"+
"<TableName>"+strFileNetTable+"</TableName>"+
"<ColName>"+columns+"</ColName>"+
"<Values>"+strValues+"</Values>"+
"<EngineName>"+strEngineName+"</EngineName>"+
"<SessionId>"+strSessionId+"</SessionId>"+
"</WMTestSelect_Input>";
}

 public static String get_NGOChangeDocumentProperty(String strEngineName,String strUserDBId,
         String strFolderIndex, String strDataDefIndex, String FieldTag,String strDocIndex) {

return "<?xml version=\"1.0\"?>\n"
+"<NGOChangeDocumentProperty_Input>\n"
+"<Option>NGOChangeDocumentProperty</Option>\n"
+"<CabinetName>" + strEngineName + "</CabinetName>\n"
+"<UserDBId>" + strUserDBId + "</UserDBId>\n"
+"<ParentFolderIndex>"+ strFolderIndex + "</ParentFolderIndex>\n"
+"<Document><DocumentIndex>" + strDocIndex + "</DocumentIndex>\n"
+"<NoOfPages></NoOfPages>\n"
+"<DataDefinition><DataDefIndex>" + strDataDefIndex + "</DataDefIndex>\n"
+"<Fields>" + FieldTag + "</Fields>\n"
+"</DataDefinition></Document>\n"
+"</NGOChangeDocumentProperty_Input>";

}


public static String get_NGOChangeDocumentProperty(String strCabinetName, String strSessionId, String strFields)
{
    return "<?xml version=\"1.0\"?><NGOChangeDocumentProperty_Input><Option>NGOChangeDocumentProperty</Option>"
                +"<CabinetName>"+strCabinetName+"</CabinetName>"
                +"<UserDBId>"+strSessionId+"</UserDBId>"
                +"<Document>"+strFields+"<DuplicateName>N</DuplicateName>"
                +"</Document>"
                +"</NGOChangeDocumentProperty_Input>";
 }

    public static String get_NGOGetDataDefIdForName(String strEngineName,
            String strUserDBId, String strDataDefName) {
    	System.out.println("In XMLGen : get_NGOGetDataDefIdForName :"+strDataDefName);
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("NGOGetDataDefIdForName", "Input");
        wfInputXml.appendTagAndValue("CabinetName", strEngineName);
        wfInputXml.appendTagAndValue("UserDBId", strUserDBId);
        wfInputXml.appendTagAndValue("DataDefName", strDataDefName);
        wfInputXml.appendEndCallName("NGOGetDataDefIdForName", "Input");
        return wfInputXml.toString();
    }

    public static String get_NGOChangeFolderProperty(String strEngineName,
            String strUserDBId,
            String strFolderName,
            String strFolderIndex,
            String strDataDefIndex,
            String FieldTag) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("NGOChangeFolderProperty", "Input");
        wfInputXml.appendTagAndValue("CabinetName", strEngineName);
        wfInputXml.appendTagAndValue("UserDBId", strUserDBId);
        wfInputXml.appendTagStart("Folder");
        wfInputXml.appendTagAndValue("FolderIndex", strFolderIndex);
        wfInputXml.appendTagAndValue("FolderName", strFolderName);
        wfInputXml.appendTagStart("DataDefinition");
        wfInputXml.appendTagAndValue("DataDefIndex", strDataDefIndex);
        wfInputXml.appendTagAndValue("Fields", FieldTag);
        wfInputXml.appendTagEnd("DataDefinition");
        wfInputXml.appendTagEnd("Folder");
        wfInputXml.appendEndCallName("NGOChangeFolderProperty", "Input");
        return wfInputXml.toString();
    }

    public static String get_NGOGetDataDefProperty(String strEngineName,
            String strSessionId,
            String strDataDefIndex) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("NGOGetDataDefProperty", "Input");
        wfInputXml.appendTagAndValue("CabinetName", strEngineName);
        wfInputXml.appendTagAndValue("UserDBId", strSessionId);
        wfInputXml.appendTagAndValue("DataDefIndex", strDataDefIndex);
        wfInputXml.appendEndCallName("NGOGetDataDefProperty", "Input");
        return wfInputXml.toString();
    }

    public static String WMUnlockWorkItem(String strEngineName,String strSessionId,String strProcessInstanceId,String strWorkItemId) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMUnlockWorkItem", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceID", strProcessInstanceId);
        wfInputXml.appendTagAndValue("WorkItemID", strWorkItemId);
        wfInputXml.appendEndCallName("WMUnlockWorkItem", "Input");
        return wfInputXml.toString();
    }
    public static String WFLinkWorkItem(String strEngineName, String strSessionId, String strWorkItemId, String strChildPID, String strParentPID)
    {
        return "<? Xml version=\"1.0\"?>"+
                "<WFLinkWorkitem_Input>"+
                "<Option>WFLinkWorkitem</Option>"+
                "<EngineName>"+strEngineName+"</EngineName>"+
                "<SessionId>"+strSessionId+"</SessionId>"+
                "<WorkItemID>"+strWorkItemId+"</WorkItemID>"+
                "<ProcessInstanceID>"+strChildPID+"</ProcessInstanceID>"+
                "<LinkedProcessInstanceID>"+strParentPID+"</LinkedProcessInstanceID>"+
                "</WFLinkWorkitem _Input>";


    }
  public static String APUpdate(String strEngineName,
            String strSessionId, String strValues, String sWhere,String tableName, String columnName) {
return  "<?xml version=\"1.0\"?>"+
"<APUpdate_Input><Option>APUpdate</Option>"+
"<TableName>"+tableName+"</TableName>"+
"<ColName>"+columnName+"</ColName>"+
"<Values>"+strValues+"</Values>"+
"<WhereClause>"+sWhere+"</WhereClause>"+
"<EngineName>"+strEngineName+"</EngineName>"+
"<SessionId>"+strSessionId+"</SessionId>"+
"</APUpdate_Input>";
}


    public static String APProcedure(String strEngineName, String strSessionId, String strWIName, String strParam,String procName)
    {
       return "<?xml version=\"1.0\"?>" +
                "<APProcedure_Input>" +
                "<Option>APProcedure</Option>" +
                "<SessionId>"+strSessionId+"</SessionId>\n"+
                "<ProcName>"+procName+"</ProcName>\n" +
                "<Params>"+strParam+"</Params>"+
                "<EngineName>"+strEngineName+"</EngineName>" +
                "<APProcedure_Input>";

    }



    public static String get_WMConnect_Input(String cabinetName,
                                             String userName, String password,
                                             String forceful) {
        return
                "<?xml version=\"1.0\"?><WMConnect_Input><Option>WMConnect</Option><UserExist>" +
                forceful + "</UserExist><EngineName>" +
                cabinetName + "</EngineName>\n" +
                "<Particpant>\n" +
                "<Name>" + userName + "</Name>\n" +
                "<Password>" +
                password + "</Password>\n" +
                "<Scope>USER</Scope>\n" +
                "<ParticipantType>U</ParticipantType>\n" +
                "</Participant>\n" + "</WMConnect_Input>";
    }

    public static String get_NGOConnectCabinet_Input(String
            cabinetName, String userName, String password, String forceful) {
        return "<?xml version=\"1.0\"?>\n<NGOConnectCabinet_Input>\n<Option>NGOConnectCabinet</Option>\n<UserExist>" +
                forceful + "</UserExist>\n<CabinetName>" +
                cabinetName + "</CabinetName>\n" + "<UserName>" + userName +
                "</UserName>\n" + "<UserPassword>" + password +
                "</UserPassword>\n" + "</NGOConnectCabinet_Input>";
    }

    public static String get_WMDisConnect_Input(String cabinetName,
                                                String sessionID) {
        return "<? Xml Version=\"1.0\"?>"
                + "<WMDisConnect_Input>"
                + "<Option>WMDisConnect</Option>"
                + "<EngineName>" + cabinetName +
                "</EngineName>"
                + "<SessionID>" + sessionID +
                "</SessionID>"
                + "</WMDisConnect_Input>";
    }

    public static String get_WMFetchProcessDefinitions_Input(String cabinetName,
            String sessionID) {
        return "<? Xml Version=\"1.0\"?>\n"
                + "<WMFetchProcessDefinitions_Input>\n"
                +
                "<Option>WMFetchProcessDefinitions</Option>\n"
                + "<EngineName>" + cabinetName +
                "</EngineName>\n"
                + "<SessionID>" + sessionID +
                "</SessionID>\n"
                + "<CountFlag>Y</CountFlag>\n"
                +
                "<BatchInfo><NoOfRecordsToFetch></NoOfRecordsToFetch>\n"
                + "<LastValue>0</LastValue></BatchInfo>\n"
                + "</WMFetchProcessDefinitions_Input>";
    }

    public static String NGOCreateReferences(String cabinetName,
                                             String sessionID,
                                             String destFolderId,
                                             String srcDocumentId) {
        return "<?xml version=\"1.0\"?>\n"
                + "<NGOCreateReferences_Input>\n"
                + "<Option>NGOCreateReferences</Option>\n"
                + "<CabinetName>" + cabinetName +
                "</CabinetName>\n"
                + "<UserDBId>" + sessionID +
                "</UserDBId>\n"
                + "<DestinationFolder>" + destFolderId +
                "</DestinationFolder>\n"
                + "<ObjectType>D</ObjectType>\n"
                + "<ObjectIndex>" + srcDocumentId + "</ObjectIndex>\n"
                + "</NGOCreateReferences_Input>";
    }


    public static String get_NGOGetListOfCabinets_Input() {
        return "<?xml version=\"1.0\"?><NGOGetListOfCabinets_Input><Option>NGOGetListOfCabinets</Option></NGOGetListOfCabinets_Input>";
    }

    public static String get_NGOISGetSitesList_Input(String cabinetName) {
        return "<?xml version=\"1.0\"?><NGOISGetSitesList_Input>\n<Option>NGOISGetSitesList</Option>\n<CabinetName>" +
                cabinetName + "</CabinetName>\n</NGOISGetSitesList_Input>";
    }

    public static String get_NGOISGetVolumesList_Input(String cabinetName) {
        return "<?xml version=\"1.0\"?><NGOISGetVolumesList_Input>\n<Option>NGOISGetVolumesList</Option>\n<CabinetName>" +
                cabinetName + "</CabinetName>\n</NGOISGetVolumesList_Input>";
    }


    public static String get_WMConnectCS_Input(String cabinetName, String name) {
        return "<?xml version=\"1.0\"?><WMConnect_Input>\n<Option>WMConnect</Option><EngineName>" +
                cabinetName + "</EngineName>\n<Name>" + name +
                "</Name>\n<ParticipantType>C</ParticipantType>\n</WMConnect_Input>";
    }

    public static String get_WMGetProcessList_Input(String cabinetName,
            String sessionID) {
        return "<?xml version=\"1.0\"?><WMGetProcessList_Input>\n<Option>WMGetProcessList</Option>\n<EngineName>" +
                cabinetName + "</EngineName>\n<SessionID>" + sessionID + "</SessionID>\n<DataFlag>N</DataFlag>\n<LatestVersionFlag>N</LatestVersionFlag>\n<OrderBy>2</OrderBy>\n<BatchInfo><NoOfRecordsToFetch>9999</NoOfRecordsToFetch></BatchInfo>\n</WMGetProcessList_Input>";
    }

    public static String get_WMGetActivityList_Input(String cabinetName,
            String sessionID, String processDefID) {
        return "<?xml version=\"1.0\"?><WMGetActivityList_Input>\n<Option>WMGetActivityList</Option>\n<EngineName>" +
                cabinetName + "</EngineName>\n<SessionId>" + sessionID +
                "</SessionId>\n<ProcessDefinitionID>" + processDefID +
                "</ProcessDefinitionID>\n<BatchInfo>\n<SortOrder>A</SortOrder>\n<OrderBy>2</OrderBy>\n</BatchInfo>\n</WMGetActivityList_Input>";
    }

    public static String get_WFGetUserQueueDetails_Input(String cabinetName,
            String sessionID, String processDefID, String activityID,
            String userID) {
        return "<?xml version=\"1.0\"?><WFGetUserQueueDetails_Input>\n<Option>WFGetUserQueueDetails</Option>\n<EngineName>" +
                cabinetName + "</EngineName>\n<SessionId>" + sessionID +
                "</SessionId>\n<UserId>" + userID +
                "</UserId>\n<ProcessDefinitionId>" + processDefID +
                "</ProcessDefinitionId>\n<ActivityId>" + activityID +
                "</ActivityId>\n<DataFlag>N</DataFlag>\n</ WFGetUserQueueDetails _Input>";
    }

    public static String get_WFGetIdforName_Input(String cabinetName,
                                                  String sessionID,
                                                  String processDefID,
                                                  String objectType,
                                                  String objectName) {
        return "<?xml version=\"1.0\"?><WFGetIdforName_Input>\n<Option>WFGetIdforName</Option>\n<EngineName>"
                + cabinetName + "</EngineName>\n<SessionId>" + sessionID +
                "</SessionId>\n<ObjectType>" + objectType +
                "</ObjectType>\n<ObjectName>" + objectName +
                "</ObjectName>\n<ProcessDefID>" + processDefID +
                "</ProcessDefID>\n</WFGetIdforName_Input>";

    }

   /*
     public static String get_NGOGetDocumentListExtView_Input(String cabinetName,
            String sessionID, String folderID) {
        return "<? Xml Version=\"1.0\"?>\n"
                + "<NGOGetDocumentListExtView_Input>\n"
                + "<Option>NGOGetDocumentListExt</Option>\n"
                + "<LinkDocFlag>N</LinkDocFlag>\n"
                + "<AnnotationFlag>N</AnnotationFlag>\n"
                + "<DocumentType>B</DocumentType>\n"
                + "<CabinetName>" + cabinetName +"</CabinetName>\n"
                + "<UserDBId>" + sessionID + "</UserDBId>\n"
                + "<FolderIndex>" + folderID + "</FolderIndex>\n"
                + "<StartPos>1</StartPos>\n"
                + "<RecursiveFlag>N</RecursiveFlag>\n"
                + "<NoOfRecordsToFetch>100</NoOfRecordsToFetch>\n"
                + "<TotalRecCountFlag>Y</TotalRecCountFlag>\n"
                + "<SortOrder>D</SortOrder>\n"
                + "<OrderBy>5</OrderBy>\n"
                + "<DataAlsoFlag>N</DataAlsoFlag>\n"
                + "<ReferenceFlag>B</ReferenceFlag>\n"
                + "</NGOGetDocumentListExtView_Input>";
    }


  public static String NGOGetDocumentListExt(String cabinetName,
                                               String sessionID,
                                               String folderID) {
        return "<? Xml Version=\"1.0\"?>\n"
                + "<NGOGetDocumentListExt_Input>\n"
                + "<Option>NGOGetDocumentListExt</Option>\n"
                + "<CabinetName>" + cabinetName + "</CabinetName>\n"
                + "<UserDBId>" + sessionID + "</UserDBId>\n"
                + "<FolderIndex>" + folderID + "</FolderIndex>\n"
                + "<StartPos>1</StartPos>\n"
                + "<RecursiveFlag>N</RecursiveFlag>\n"
                + "<TotalRecCountFlag>Y</TotalRecCountFlag>\n"
                + "<SortOrder>D</SortOrder>\n"
                + "<OrderBy>5</OrderBy>\n"
                + "<DataAlsoFlag>N</DataAlsoFlag>\n"
                + "</NGOGetDocumentListExt_Input>";
    }
*/

    public static String get_WMCompleteWorkItem_Input(String cabinetName,
            String sessionID,
            String workitemNo,
            String workitemID) {
        return "<?xml version=\"1.0\"?>"
                + "<WMCompleteWorkItem_Input>\n"
                + "<Option>WMCompleteWorkItem</Option>\n"
                + "<EngineName>" + cabinetName + "</EngineName>\n"
                + "<SessionId>" + sessionID + "</SessionId>\n"
                + "<ProcessInstanceId>" + workitemNo + "</ProcessInstanceId>\n"
                + "<WorkItemId>" + workitemID + "</WorkItemId>\n"
                + "</WMCompleteWorkItem_Input>";
    }

    public static String APSelectWithColumnNames(String strEngineName,
            String strSessionId, String strQuery) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("APSelectWithColumnNames", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("Query", strQuery);
		wfInputXml.appendEndCallName("APSelectWithColumnNames", "Input");
		return wfInputXml.toString();
	}


    public static String WFUploadWorkItem(String strEngineName,
            String strSessionId,String strProcessDefId,String strValidationRequired,
            String strDocuments,String strAttributes,String actID,String actName)
    {
    	WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WFUploadWorkItem", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessDefId", strProcessDefId);
		//commented by akash for workitemcall
		//wfInputXml.appendTagAndValue("ValidationRequired", strValidationRequired);
		//wfInputXml.appendTagAndValue("Documents", strDocuments);
		wfInputXml.appendTagAndValue("Attributes", strAttributes);
		wfInputXml.appendTagAndValue("InitiateFromActivityId", actID);
		wfInputXml.appendTagAndValue("InitiateFromActivityName", actName);

		wfInputXml.appendEndCallName("WFUploadWorkItem", "Input");
		return wfInputXml.toString();
		
		

    }

    public static String NGOAddDocument_WF(String strEngineName,
            String strSessionId,
            String strParentFID,
            String strDocumentName,
            int iVolId,
            String strFilePath,
			   String strISIndex,
				String strDocSize,
				String strNoOfPages,
				String count,String Doctype,String ext) {


return "<?xml version=\"1.0\"?>"+
"<NGOAddDocument_Input><Option>NGOAddDocument</Option>" +
"<CabinetName>"+strEngineName+"</CabinetName>" +
"<UserDBId>"+strSessionId+"</UserDBId>" +
"<GroupIndex>0</GroupIndex><ParentFolderIndex>"+strParentFID+"</ParentFolderIndex>" +
"<DocumentName>"+Doctype+count+"</DocumentName><CreatedByAppName>"+ext+"</CreatedByAppName>" +
"<Comment>"+strDocumentName+"</Comment><VolumeIndex>"+iVolId+"</VolumeIndex>" +
"<FilePath>"+strFilePath+"</FilePath>" +
"<ISIndex>"+strISIndex+"</ISIndex><NoOfPages>"+strNoOfPages+"</NoOfPages><DocumentType>N</DocumentType>" +
"<DocumentSize>"+strDocSize+"</DocumentSize></NGOAddDocument_Input>";
}


}
