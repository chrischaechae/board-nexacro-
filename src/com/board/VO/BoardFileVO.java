package com.board.VO;

public class BoardFileVO {
	
	private int fseq;
	private String path;
	private String upload;
	private String oriupload;
	
	public int getFseq() {
		return fseq;
	}
	public void setFseq(int fseq) {
		this.fseq = fseq;
	}
	public String getUpload() {
		return upload;
	}
	public void setUpload(String upload) {
		this.upload = upload;
	}
	public String getOriupload() {
		return oriupload;
	}
	public void setOriupload(String oriupload) {
		this.oriupload = oriupload;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}	
	
}
