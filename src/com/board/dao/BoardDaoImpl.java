package com.board.dao;

import com.board.VO.BoardVO;
import com.board.dao.BoardDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Component;

@Component
public class BoardDaoImpl extends SqlSessionDaoSupport implements BoardDao {
	
	public List<BoardVO> list(Map<String, Object> map) {
		List<BoardVO> list = getSqlSession().selectList("boardList", map);
		return list;
	}

	public int getCount(Map<String, Object> map) {
		return ((Integer) getSqlSession().selectOne("boardCount", map)).intValue();
	}

	@Override
	public void write(BoardVO boardVO) {
		getSqlSession().selectList("write", boardVO);
	}

	@Override
	public BoardVO detail(int gseq) {
		BoardVO bean=getSqlSession().selectOne("detail",gseq);
		return bean;
	}

	@Override
	public void update(BoardVO boardVO) {
		getSqlSession().update("update", boardVO);
		
	}

	@Override
	public void delete(int gseq) {
		getSqlSession().update("delete", gseq);
		
	}

	@Override
	public void cnt(int gseq) {
		getSqlSession().update("cnt", gseq);
		
	}

	@Override
	public List<BoardVO> searchlist(HashMap<String, Object> map) {
		System.out.println("dao"+map.get("keyword"));
		System.out.println("dao"+map.get("keyfield"));
		List<BoardVO> list = getSqlSession().selectList("searchlist", map);
		
		return list;
	}

	@Override
	
	public List<BoardVO> filesearchlist(HashMap<String, Object> map) {
		System.out.println("dao"+map.get("keyword"));
		System.out.println("dao"+map.get("keyfield"));
		List<BoardVO> list = getSqlSession().selectList("filesearchlist", map);
		return list;
	}

	@Override
	public void reply(BoardVO boardVO) {
		getSqlSession().selectList("reply", boardVO);
		
	}

	@Override
	public void updaterep(BoardVO boardVO) {
		getSqlSession().update("updaterep",boardVO);	
		
	}

	@Override
	public void updatestep(BoardVO boardVO) {
		getSqlSession().update("updatestep",boardVO);
		
	}

}
