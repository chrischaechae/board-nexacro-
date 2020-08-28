package com.board.dao;

import com.board.VO.BoardVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract interface BoardDao {
	
	public abstract List<BoardVO> list(Map<String, Object> paramMap);

	public abstract int getCount(Map<String, Object> paramMap);

	public abstract void write(BoardVO boardVO);

	public abstract BoardVO detail(int gseq);

	public abstract void update(BoardVO boardVO);

	public abstract void delete(int gseq);

	public abstract void cnt(int gseq);

	public abstract List<BoardVO> searchlist(HashMap<String, Object> map);

	public abstract List<BoardVO> filesearchlist(HashMap<String, Object> map);

	public abstract void reply(BoardVO boardVO);

	public abstract void updaterep(BoardVO boardVO);

	public abstract void updatestep(BoardVO boardVO);


}
