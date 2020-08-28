package com.board.controller;

import com.board.VO.BoardFileVO;
import com.board.VO.BoardVO;
import com.board.dao.BoardDao;
import com.board.dao.BoardFileDao;
import com.board.paging.Paging;
import com.nexacro.xapi.data.*;
import com.nexacro.xapi.tx.*; 
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ListController {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(getClass());
	private int pageSize = 10;
	private int blockCount = 10;
		
	@Autowired
	private BoardDao boardDao;
	@Autowired
	private BoardFileDao boardfileDao;

	@RequestMapping({ "/board/list.do" })
	public void board(HttpServletRequest request, HttpServletResponse response) throws PlatformException{
		
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		/*int pageNum = new Integer(request.getParameter("pageNum"));
		int startNum = new Integer(request.getParameter("startNum"));
		int contentPerPage = new Integer(request.getParameter("contentPerPage"));*/
				
		/*DataSet ds1 = pdata.getDataSet("page");
		
		pagenum =Integer.parseInt(ds1.getString(0,"pagenum"));
		startnum =Integer.parseInt(ds1.getString(0,"startnum"));
		contentperpage =Integer.parseInt(ds1.getString(0,"contentperpage"));*/
		
					// DataSet
					DataSet ds = new DataSet("board");			
				    
				    // DataSet Column setting
					ds.addColumn("no", DataTypes.INT, 256);
   				    ds.addColumn("seq", DataTypes.INT, 256);
				    ds.addColumn("title", DataTypes.STRING,  256);
				    ds.addColumn("name", DataTypes.STRING,  256);
					ds.addColumn("regdate", DataTypes.DATE, 256);
					ds.addColumn("hit", DataTypes.INT, 256);
					ds.addColumn("pass", DataTypes.STRING, 256);
					ds.addColumn("ref", DataTypes.INT, 256);
					ds.addColumn("indent", DataTypes.INT, 256);
					ds.addColumn("step", DataTypes.INT, 256);
					
					// DAO
					BoardVO boardVO;
					HashMap<String, Object> map = new HashMap();
					
					
				   // strErrorMsg  = 0;
					List<BoardVO> list = null;
					list=this.boardDao.list(map);
				   
				   
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   	boardVO = new BoardVO();
					   	boardVO = list.get(i);
					    
						ds.set(row, "no", i+1);
					   	ds.set(row, "seq", boardVO.getSeq());
					    ds.set(row, "title", boardVO.getTitle());
					    ds.set(row, "name", boardVO.getName());
					    ds.set(row, "regdate",boardVO.getRegdate());
					    ds.set(row, "hit", boardVO.getHit());
					    ds.set(row, "pass", boardVO.getPass());
					    ds.set(row, "ref", boardVO.getRef());	
					    ds.set(row, "indent", boardVO.getIndent());	
					    ds.set(row, "step", boardVO.getStep());	
				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	
	
	@RequestMapping({ "/board/write.do" })
	public void write(HttpServletRequest request, HttpServletResponse response) throws PlatformException{
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					DataSet ds = pdata.getDataSet("write");
					
					// DAO
					BoardVO boardVO;
					boardVO = new BoardVO();
					
					boardVO.setTitle(ds.getString(0, "title"));
					boardVO.setName(ds.getString(0, "name"));
					boardVO.setContent(ds.getString(0, "content")); 
					boardVO.setPass(ds.getString(0, "pass"));
					
					
					boardDao.write(boardVO);
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    
			
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data	
		
	}
	@RequestMapping({ "/board/upload.do" })
	public void upload(HttpServletRequest request, HttpServletResponse response) throws PlatformException, UnsupportedEncodingException{
	
	String chkType = request.getHeader("Content-Type");

	if( chkType == null )
	 return;
	request.setCharacterEncoding("utf-8");
	String contextRealPath = request.getSession().getServletContext().getRealPath("/");
	
	String PATH = request.getParameter("PATH");
	String savePath = contextRealPath + PATH;
	int maxSize = 500 * 1024 * 1024; // 최대 업로드 파일 크기 500MB(메가)로 제한
	PlatformData resData = new PlatformData();
	VariableList resVarList = resData.getVariableList();
	String sMsg = " A ";
	try {
	 
	 MultipartRequest multi = new MultipartRequest(request, savePath, maxSize, "utf-8", new DefaultFileRenamePolicy());
	 Enumeration files = multi.getFileNames(); // 파일명 모두 얻기

	 
	 sMsg += "B ";
	 DataSet ds = new DataSet("Dataset00");
	 
	 ds.addColumn(new ColumnHeader("fileName", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileSize", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileType", DataTypes.STRING));
	 
	 sMsg += "C ";
	 String fileName="";
	 String orisFName="";
	 String gdpath="";
	 while (files.hasMoreElements()) {
	  sMsg += "D ";
	  String webpath="http://localhost:8078/nexacro/upload/";
	  String name = (String)files.nextElement();
	  fileName= multi.getFilesystemName(name);//파일이름
	  orisFName=multi.getOriginalFileName(name);
	  String type = multi.getContentType(name);
	  File f = multi.getFile(name);
	  int row = ds.newRow();
	  ds.set(row, "fileName", fileName);
	  ds.set(row, "fileType", type);
	  gdpath=webpath+fileName;
	  
	  HashMap<String, Object> map = new HashMap();
		map.put("upload",fileName);
		map.put("oriupload",orisFName);
		map.put("path", gdpath);
		boardfileDao.addfile(map);
	  if (f != null)
	  {
	  
	   String size = Long.toString(f.length()/1024)+"KB";
	   ds.set(row, "fileSize", size);
	  }  
	  sMsg += row +" ";
	 }
	 
	 resData.addDataSet(ds);
	 resVarList.add("ErrorCode", 200);
	 //resVarList.add("ErrorMsg", savePath+"/"+fileName);
	 resVarList.add("ErrorMsg", fileName);
	} catch (Exception e) {
	 resVarList.add("ErrorCode", -1);
	 resVarList.add("ErrorMsg", sMsg + " " + e);
	}
	HttpPlatformResponse res = new HttpPlatformResponse(response);
	res.setData(resData);
	res.sendData();
	
	}
	@RequestMapping({ "/board/reupload.do" })
	public void reupload(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws PlatformException, UnsupportedEncodingException{
	
	String chkType = request.getHeader("Content-Type");
	//System.out.println(chkType);
	if( chkType == null )
	 return;
	request.setCharacterEncoding("utf-8");
	String contextRealPath = request.getSession().getServletContext().getRealPath("/");
	
	String PATH = request.getParameter("PATH");
	String savePath = contextRealPath + PATH;
	int maxSize = 500 * 1024 * 1024; // 최대 업로드 파일 크기 500MB(메가)로 제한
	PlatformData resData = new PlatformData();
	VariableList resVarList = resData.getVariableList();
	String sMsg = " A ";
	try {
	 
	 MultipartRequest multi = new MultipartRequest(request, savePath, maxSize, "utf-8", new DefaultFileRenamePolicy());
	 Enumeration files = multi.getFileNames(); // 파일명 모두 얻기

	 
	 sMsg += "B ";
	 DataSet ds = new DataSet("Dataset00");
	 
	 ds.addColumn(new ColumnHeader("fileName", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileSize", DataTypes.STRING));
	 ds.addColumn(new ColumnHeader("fileType", DataTypes.STRING));
	 
	 sMsg += "C ";
	 String fileName="";
	 String orisFName="";
	 String gdpath="";
	 while (files.hasMoreElements()) {
	  sMsg += "D ";
	  String name = (String)files.nextElement();
	  String webpath="http://localhost:8078/nexacro/upload/";
	  fileName= multi.getFilesystemName(name);//파일이름
	  orisFName=multi.getOriginalFileName(name);
	  String type = multi.getContentType(name);
	  File f = multi.getFile(name);
	  int row = ds.newRow();
	  ds.set(row, "fileName", fileName);
	  ds.set(row, "fileType", type);
	  gdpath=webpath+fileName;
	  
	  HashMap<String, Object> map = new HashMap();
	  	map.put("fseq",gseq);
		map.put("upload",fileName);
		map.put("oriupload",orisFName);
		map.put("path", gdpath);
		boardfileDao.updatefile(map);
	  if (f != null)
	  {
	  
	   String size = Long.toString(f.length()/1024)+"KB";
	   ds.set(row, "fileSize", size);
	  }  
	  sMsg += row +" ";
	 }
	 
	 resData.addDataSet(ds);
	 resVarList.add("ErrorCode", 200);
	 //resVarList.add("ErrorMsg", savePath+"/"+fileName);
	 resVarList.add("ErrorMsg", fileName);
	} catch (Exception e) {
	 resVarList.add("ErrorCode", -1);
	 resVarList.add("ErrorMsg", sMsg + " " + e);
	}
	HttpPlatformResponse res = new HttpPlatformResponse(response);
	res.setData(resData);
	res.sendData();
	
	}
	@RequestMapping({ "/board/download.do" })
	public void download(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="file",required=false) String filename) throws PlatformException, IOException{
		
		String contextRealPath = request.getSession().getServletContext().getRealPath("/");
		String savePath = contextRealPath + "upload";
		
		String name = request.getParameter("file");
		filename = new String(name.getBytes("iso8859-1"), "UTF-8");
		
		 byte[] buffer = new byte[1024];
		 ServletOutputStream out_stream = null;
		 BufferedInputStream in_stream = null;
		 File fis = new File(savePath + "\\" + filename);
		if(fis.exists()){
		 try{
		  response.setContentType("utf-8");
		  response.setContentType("application/octet;charset=utf-8");
		  response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		  
		  //out.clear();
		  //out = pageContext.pushBody();
		  
		  out_stream = response.getOutputStream();
		  in_stream = new BufferedInputStream(new FileInputStream(fis));
		  int n = 0;
		  while ((n = in_stream.read(buffer, 0, 1024)) != -1) {
		   out_stream.write(buffer, 0, n);
		  }// while
		 } catch (Exception e) {
		  e.printStackTrace();
		 } finally {
		  if (in_stream != null) {
		   try {
		    in_stream.close();
		   } catch (Exception e) {}
		  }
		  if (out_stream != null) {
		   try {
		    out_stream.close();
		   } catch (Exception e) {}
		  }
		 }
		}else{
		  response.sendRedirect("unknownfile");
		}
	}
	
	
	@RequestMapping({ "/board/detail.do" })
	public void detail(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("detail");			
		
	    // DataSet Column setting
	    ds.addColumn("title", DataTypes.STRING,  256);
	    ds.addColumn("name", DataTypes.STRING,  256);
	    ds.addColumn("content", DataTypes.STRING, 256);
	    ds.addColumn("ref", DataTypes.INT, 256);
	    ds.addColumn("indent", DataTypes.INT, 256);
	    ds.addColumn("step", DataTypes.INT, 256);
	    
		
		BoardVO bean=null;
		
		bean=boardDao.detail(gseq);
		boardDao.cnt(gseq);
		
		int row = ds.newRow();
		
		
	    ds.set(row, "title", bean.getTitle());
	    ds.set(row, "name", bean.getName());
	    ds.set(row, "content",bean.getContent());
	    ds.set(row, "ref",bean.getRef());
	    ds.set(row, "indent",bean.getIndent());
	    ds.set(row, "step",bean.getStep());
	    
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/detailfile.do" })
	public void detailfile(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws PlatformException{
		
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					// DataSet
					DataSet ds = new DataSet("dfile");			
				    
				    // DataSet Column setting
   				    ds.addColumn("fseq", DataTypes.INT, 256);
				    ds.addColumn("upload", DataTypes.STRING,  256);
				    ds.addColumn("path", DataTypes.STRING,  256);
				    ds.addColumn("supload", DataTypes.STRING,  256);
					
					// DAO
					BoardFileVO boardfileVO;		
				   // strErrorMsg  = 0;
					HashMap<String, Object> map = new HashMap();
					List<BoardFileVO> list = null;
					list=this.boardfileDao.list(map, gseq);
				   
				   
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   	boardfileVO = new BoardFileVO();
					   	boardfileVO = list.get(i);
					    
					   	
					   	ds.set(row, "fseq", boardfileVO.getFseq());
					 	ds.set(row, "upload", boardfileVO.getOriupload());
					    ds.set(row, "path",boardfileVO.getPath());
					    ds.set(row, "supload",boardfileVO.getUpload());	
				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping(value="/board/edit.do")
	public void edit(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws IOException, PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		DataSet ds = new DataSet("edit");			
	    
	    // DataSet Column setting
	    ds.addColumn("title", DataTypes.STRING,  256);
	    ds.addColumn("name", DataTypes.STRING,  256);
	    ds.addColumn("content", DataTypes.STRING, 256);
	    ds.addColumn("pass", DataTypes.STRING, 256);
		
		BoardVO bean=null;
		
		bean=boardDao.detail(gseq);
		
		int row = ds.newRow();
		
		
	    ds.set(row, "title", bean.getTitle());
	    ds.set(row, "name", bean.getName());
	    ds.set(row, "content",bean.getContent());
	    ds.set(row, "pass",bean.getPass());
		
	    pdata.addDataSet(ds);
		
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		 = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping({ "/board/editfile.do" })
	public void editfile(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws PlatformException{
		
		PlatformData pdata = new PlatformData();
		HttpPlatformRequest req = new HttpPlatformRequest(request); 
			req.receiveData();							
			pdata = req.getData();
		
		int nErrorCode = 0;
		String strErrorMsg = "START";
		
					// DataSet
					DataSet ds = new DataSet("efile");			
				    
				    // DataSet Column setting
   				    ds.addColumn("fseq", DataTypes.INT, 256);
   				    ds.addColumn("ffseq", DataTypes.INT, 256);
				    ds.addColumn("upload", DataTypes.STRING,  256);
				    ds.addColumn("path", DataTypes.STRING,  256);
				    ds.addColumn("supload", DataTypes.STRING,  256);
					
					// DAO
					BoardFileVO boardfileVO;		
				   // strErrorMsg  = 0;
					HashMap<String, Object> map = new HashMap();
					List<BoardFileVO> list = null;
					list=this.boardfileDao.list(map, gseq);
				   
				   
				   // ResultSet -> Show the Row sets (XML) : browser 
				   for (int i=0; i<list.size(); i++) {
					   int row = ds.newRow();
					   
					   	boardfileVO = new BoardFileVO();
					   	boardfileVO = list.get(i);
					    
					   	
					   	ds.set(row, "fseq", i+1);
					 	ds.set(row, "upload", boardfileVO.getOriupload());
					    ds.set(row, "path",boardfileVO.getPath());
					    ds.set(row, "supload", boardfileVO.getUpload());
				   }
					 // for
					pdata.addDataSet(ds);
					
			       // set the ErrorCode and ErrorMsg about success
			       nErrorCode = 0;
			       strErrorMsg = "SUCC";
				    
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data

	}
	@RequestMapping(value="/board/update.do")
	public void update(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		
					// receive client request
					// not need to receive
				    
					// create HttpPlatformRequest for receive data from client
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					DataSet ds = pdata.getDataSet("edit");
					
					// DAO
					BoardVO boardVO;
					boardVO = new BoardVO();
					
					boardVO.setSeq(gseq);
					boardVO.setTitle(ds.getString(0, "title"));
					boardVO.setName(ds.getString(0, "name"));
					boardVO.setContent(ds.getString(0, "content")); 
					boardVO.setPass(ds.getString(0, "pass"));
					
					boardDao.update(boardVO);
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    
			
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	@RequestMapping(value="/board/delete.do")
	public void delete(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws PlatformException{
		
		PlatformData pdata = new PlatformData();

		int nErrorCode = 0;
		String strErrorMsg = "START";
		
		
					// receive client request
					// not need to receive
				    
					// create HttpPlatformRequest for receive data from client
					HttpPlatformRequest req 
					     = new HttpPlatformRequest(request); 

					req.receiveData();							
					pdata = req.getData();
					
					boardDao.delete(gseq);
					
					
				    // set the ErrorCode and ErrorMsg about success
				    nErrorCode = 0;
				    strErrorMsg = "SUCC";
				    
			
		
		// save the ErrorCode and ErrorMsg for sending Client
		VariableList varList = pdata.getVariableList();
				
		varList.add("ErrorCode", nErrorCode);
		varList.add("ErrorMsg", strErrorMsg);
				
		// send the result data(XML) to Client
		HttpPlatformResponse res 
		    = new HttpPlatformResponse(response, 
			       									            PlatformType.CONTENT_TYPE_XML,  
			       									            "UTF-8");
		res.setData(pdata); 
		res.sendData();		// Send Data
	}
	
@RequestMapping(value="/board/delfile.do",method=RequestMethod.POST)
public void delfile(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="seq",required=false) int gseq) throws PlatformException, IOException{
	PlatformData pdata = new PlatformData();

	int nErrorCode = 0;
	String strErrorMsg = "START";
	
	
				// receive client request
				// not need to receive
			    
				// create HttpPlatformRequest for receive data from client
				HttpPlatformRequest req 
				     = new HttpPlatformRequest(request); 

				req.receiveData();							
				pdata = req.getData();
				
				DataSet ds = pdata.getDataSet("dfile");
				
				// DAO
				BoardFileVO BoardFileVO;
				BoardFileVO = new BoardFileVO();
				
				Boolean a=true;
				int i=0;
				while(a){
				BoardFileVO.setUpload(ds.getString(i, "supload"));

				String folder="C:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\nexacro\\upload\\";
				String file=folder+BoardFileVO.getUpload();
				File delfile=new File(file);
				
					if(delfile.exists()){
						delfile.delete();
					}else{
						break;
					}
					i++;
					
				}//while end
				boardfileDao.delete(gseq);
			    // set the ErrorCode and ErrorMsg about success
			    nErrorCode = 0;
			    strErrorMsg = "SUCC";
			    
		
	
	// save the ErrorCode and ErrorMsg for sending Client
	VariableList varList = pdata.getVariableList();
			
	varList.add("ErrorCode", nErrorCode);
	varList.add("ErrorMsg", strErrorMsg);
			
	// send the result data(XML) to Client
	HttpPlatformResponse res 
	    = new HttpPlatformResponse(response, 
		       									            PlatformType.CONTENT_TYPE_XML,  
		       									            "UTF-8");
	res.setData(pdata); 
	res.sendData();		// Send Data	
    
	}

@RequestMapping(value="/board/updelfile.do",method=RequestMethod.POST)
public void updelfile(HttpServletRequest request, HttpServletResponse response) throws PlatformException, IOException{
	PlatformData pdata = new PlatformData();

	int nErrorCode = 0;
	String strErrorMsg = "START";
	
	
				// receive client request
				// not need to receive
			    
				// create HttpPlatformRequest for receive data from client
				HttpPlatformRequest req 
				     = new HttpPlatformRequest(request); 

				req.receiveData();							
				pdata = req.getData();
				
				DataSet ds = pdata.getDataSet("delfile");
				
				// DAO
				BoardFileVO BoardFileVO;
				BoardFileVO = new BoardFileVO();
				
				Boolean a=true;
				int i=0;
				while(a){
				BoardFileVO.setUpload(ds.getString(i, "ssupload"));

				String folder="C:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\nexacro\\upload\\";
				String file=folder+BoardFileVO.getUpload();
				String delfilename=BoardFileVO.getUpload();
				File delfile=new File(file);
				
					if(delfile.exists()){
						delfile.delete();
					}else{
						break;
					}
					i++;
					boardfileDao.updelete(delfilename);	
				}//while end
				
			    // set the ErrorCode and ErrorMsg about success
			    nErrorCode = 0;
			    strErrorMsg = "SUCC";
			    
		
	
	// save the ErrorCode and ErrorMsg for sending Client
	VariableList varList = pdata.getVariableList();
			
	varList.add("ErrorCode", nErrorCode);
	varList.add("ErrorMsg", strErrorMsg);
			
	// send the result data(XML) to Client
	HttpPlatformResponse res 
	    = new HttpPlatformResponse(response, 
		       									            PlatformType.CONTENT_TYPE_XML,  
		       									            "UTF-8");
	res.setData(pdata); 
	res.sendData();		// Send Data	
    
	}

@RequestMapping({ "/board/search.do" })
public void search(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="keyfield",required=false) String keyfieldv,@RequestParam(value="keyword",required=false) String keywordv) throws PlatformException, IOException{
	
	PlatformData pdata = new PlatformData();

	int nErrorCode = 0;
	String strErrorMsg = "START";
			
			
				// DataSet
				DataSet ds = new DataSet("board");			
			    
			    // DataSet Column setting
				ds.addColumn("no", DataTypes.INT, 256);
				ds.addColumn("seq", DataTypes.INT, 256);
			    ds.addColumn("title", DataTypes.STRING,  256);
			    ds.addColumn("name", DataTypes.STRING,  256);
				ds.addColumn("regdate", DataTypes.DATE, 256);
				ds.addColumn("hit", DataTypes.INT, 256);
				ds.addColumn("pass", DataTypes.STRING, 256);
				ds.addColumn("ref", DataTypes.INT, 256);
				ds.addColumn("indent", DataTypes.INT, 256);
				ds.addColumn("step", DataTypes.INT, 256);
	
	
	
	keywordv=new String(keywordv.getBytes("iso-8859-1"), "utf-8");
	keyfieldv=new String(keyfieldv.getBytes("iso-8859-1"), "utf-8");
	
			BoardVO boardVO;
			HashMap<String, Object> map = new HashMap();
			map.put("keyword",keywordv);
			map.put("keyfield", keyfieldv);
			
		   // strErrorMsg  = 0;
			List<BoardVO> list = null;
			list=this.boardDao.searchlist(map);
		   
		   
		   // ResultSet -> Show the Row sets (XML) : browser 
		   for (int i=0; i<list.size(); i++) {
			   int row = ds.newRow();
			   
			   	boardVO = new BoardVO();
			   	boardVO = list.get(i);
			    
			   	ds.set(row, "no", i+1);
			   	ds.set(row, "seq", boardVO.getSeq());
			    ds.set(row, "title", boardVO.getTitle());
			    ds.set(row, "name", boardVO.getName());
			    ds.set(row, "regdate",boardVO.getRegdate());
			    ds.set(row, "hit", boardVO.getHit());
			    ds.set(row, "pass", boardVO.getPass());
			    ds.set(row, "ref", boardVO.getRef());
			    ds.set(row, "indent", boardVO.getIndent());
			    ds.set(row, "step", boardVO.getStep());
			   
		   }
			 // for
			pdata.addDataSet(ds);
			
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

	// save the ErrorCode and ErrorMsg for sending Client
	VariableList varList = pdata.getVariableList();
			
	varList.add("ErrorCode", nErrorCode);
	varList.add("ErrorMsg", strErrorMsg);
			
	// send the result data(XML) to Client
	HttpPlatformResponse res 
	    = new HttpPlatformResponse(response, 
		       									            PlatformType.CONTENT_TYPE_XML,  
		       									            "UTF-8");
	res.setData(pdata); 
	res.sendData();		// Send Data
				
	}
@RequestMapping({ "/board/filesearch.do" })
public void filesearch(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="keyfield",required=false) String keyfield,@RequestParam(value="keyword",required=false) String keyword) throws PlatformException, IOException{
	
	PlatformData pdata = new PlatformData();

	int nErrorCode = 0;
	String strErrorMsg = "START";
			
			
				// DataSet
				DataSet ds = new DataSet("board");			
			    
			    // DataSet Column setting
				
				ds.addColumn("no", DataTypes.INT, 256);
				ds.addColumn("seq", DataTypes.INT, 256);
			    ds.addColumn("title", DataTypes.STRING,  256);
			    ds.addColumn("name", DataTypes.STRING,  256);
				ds.addColumn("regdate", DataTypes.DATE, 256);
				ds.addColumn("hit", DataTypes.INT, 256);
				ds.addColumn("pass", DataTypes.STRING, 256);
				ds.addColumn("ref", DataTypes.INT, 256);
				ds.addColumn("indent", DataTypes.INT, 256);
				ds.addColumn("step", DataTypes.INT, 256);
	
	keyword=new String(keyword.getBytes("iso-8859-1"), "utf-8");
	keyfield=new String(keyfield.getBytes("iso-8859-1"), "utf-8");
		
			BoardVO boardVO;
			HashMap<String, Object> map = new HashMap();
			map.put("keyword",keyword);
			map.put("keyfield", keyfield);
			System.out.println(map.get("keyfield"));
			System.out.println(map.get("keyword"));
		   // strErrorMsg  = 0;
			List<BoardVO> list = null;
			list=this.boardDao.filesearchlist(map);
		   
		   
		   // ResultSet -> Show the Row sets (XML) : browser 
		   for (int i=0; i<list.size(); i++) {
			   int row = ds.newRow();
			   
			   	boardVO = new BoardVO();
			   	boardVO = list.get(i);
			    
				ds.set(row, "no", i+1);
			   	ds.set(row, "seq", boardVO.getSeq());
			    ds.set(row, "title", boardVO.getTitle());
			    ds.set(row, "name", boardVO.getName());
			    ds.set(row, "regdate",boardVO.getRegdate());
			    ds.set(row, "hit", boardVO.getHit());
			    ds.set(row, "pass", boardVO.getPass());
			    ds.addColumn("ref", DataTypes.INT, 256);
				ds.addColumn("indent", DataTypes.INT, 256);
				ds.addColumn("step", DataTypes.INT, 256);
		   }
			 // for
			pdata.addDataSet(ds);
			
	       // set the ErrorCode and ErrorMsg about success
	       nErrorCode = 0;
	       strErrorMsg = "SUCC";
		    

// save the ErrorCode and ErrorMsg for sending Client
VariableList varList = pdata.getVariableList();
		
varList.add("ErrorCode", nErrorCode);
varList.add("ErrorMsg", strErrorMsg);
		
// send the result data(XML) to Client
HttpPlatformResponse res 
    = new HttpPlatformResponse(response, 
	       									            PlatformType.CONTENT_TYPE_XML,  
	       									            "UTF-8");
res.setData(pdata); 
res.sendData();		// Send Data
			
		}

@RequestMapping(value="/board/reply.do")
public void reply(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="ref",required=false) int gref,@RequestParam(value="indent",required=false) int gindent,@RequestParam(value="step",required=false) int gstep) throws PlatformException{
	
	PlatformData pdata = new PlatformData();
	
	int nErrorCode = 0;
	String strErrorMsg = "START";
	
	
				HttpPlatformRequest req 
				     = new HttpPlatformRequest(request); 

				req.receiveData();							
				pdata = req.getData();
				
				DataSet ds = pdata.getDataSet("reply");
				
				// DAO
				BoardVO boardVO;
				boardVO = new BoardVO();
				
				boardVO.setRef(gref);
				boardVO.setStep(gstep);
				boardVO.setIndent(gindent);
				boardVO.setTitle(ds.getString(0, "title"));
				boardVO.setName(ds.getString(0, "name"));
				boardVO.setContent(ds.getString(0, "content")); 
				boardVO.setPass(ds.getString(0, "pass"));
				
				
				boardDao.reply(boardVO);
				boardDao.updaterep(boardVO);
				boardDao.updatestep(boardVO);
			    // set the ErrorCode and ErrorMsg about success
			    nErrorCode = 0;
			    strErrorMsg = "SUCC";
			    
		
	
	// save the ErrorCode and ErrorMsg for sending Client
	VariableList varList = pdata.getVariableList();
			
	varList.add("ErrorCode", nErrorCode);
	varList.add("ErrorMsg", strErrorMsg);
			
	// send the result data(XML) to Client
	HttpPlatformResponse res 
	    = new HttpPlatformResponse(response, 
		       									            PlatformType.CONTENT_TYPE_XML,  
		       									            "UTF-8");
	res.setData(pdata); 
	res.sendData();		// Send Data

	}

}	


