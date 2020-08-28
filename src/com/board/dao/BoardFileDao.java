package com.board.dao;

import java.util.HashMap;
import java.util.List;

import com.board.VO.BoardFileVO;

public interface BoardFileDao {

	void addfile(HashMap<String, Object> map);

	List<BoardFileVO> list(HashMap<String, Object> map,int gseq);

	void delete(int gseq);

	void updatefile(HashMap<String, Object> map);

	void deleterefile(String delfile);

	void updelete(String delfilename);

	


	

}
