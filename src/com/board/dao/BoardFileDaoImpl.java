package com.board.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Component;

import com.board.VO.BoardFileVO;
import com.board.VO.BoardVO;

@Component
public class BoardFileDaoImpl extends SqlSessionDaoSupport implements BoardFileDao{

	@Override
	public void addfile(HashMap<String, Object> map) {
		System.out.println("dao1"+map.get("oriupload"));
		System.out.println("dao2"+map.get("upload"));
		System.out.println("dao3"+map.get("path"));
		
		getSqlSession().insert("addfile",map);	
	}

	@Override
	public List<BoardFileVO> list(HashMap<String, Object> map,int gseq) {
		List<BoardFileVO> list = getSqlSession().selectList("boardflieList",gseq);
		return list;
	}

	@Override
	public void delete(int gseq) {
		getSqlSession().delete("delfile",gseq);	
		
	}

	@Override
	public void updatefile(HashMap<String, Object> map) {
		getSqlSession().insert("updatefile",map);
		
	}

	@Override
	public void deleterefile(String delfile) {
		getSqlSession().delete("delrefile",delfile);	
		
	}

	@Override
	public void updelete(String delfilename) {
		getSqlSession().delete("delfilename",delfilename);
	}

	
}
