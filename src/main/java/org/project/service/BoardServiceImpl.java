package org.project.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.project.domain.BoardVO;
import org.project.domain.Criteria;
import org.project.domain.SearchCriteria;
import org.project.persistence.BoardDAO;

@Service
public class BoardServiceImpl implements BoardService {

  @Inject
  private BoardDAO dao;
  
  //게시글 등록시 첨부파일이 같이 등록된다.
  @Transactional
  @Override
  public void regist(BoardVO board) throws Exception {
	//게시물 등록
    dao.create(board);
    
    String[] files = board.getFiles();
    
    if(files == null) { return; } 
    
    for (String fileName : files) {
    	//첨부파일 등록
    	dao.addAttach(fileName);
    }   
  }

//  @Override
//  public void regist(BoardVO board) throws Exception {
//    dao.create(board);
//  }

  //@Override
  //public BoardVO read(Integer bno) throws Exception {
  //  return dao.read(bno);
  //}
  
  //게시물 조회 시 조회수 증가 후 조회한다.
  //isolation=Isolation.READ_COMMITTED는 다른 트랜잭션이 커밋하지 않은 데이터는 볼수 없다.
  @Transactional(isolation=Isolation.READ_COMMITTED)
  @Override
  public BoardVO read(Integer bno) throws Exception {
    dao.updateViewCnt(bno);
    return dao.read(bno);
  }
  
  //
  @Transactional
  @Override
  public void modify(BoardVO board) throws Exception {
	//게시글 수정
	dao.update(board);
    //게시물 번호
    Integer bno = board.getBno();
    //게시물 첨부파일 삭제
    dao.deleteAttach(bno);
    //새로 업로드된 첨부파일 이름 가져오기 
    String[] files = board.getFiles();
    //새로 업로드된 첨부파일이 없다면 return;
    if(files == null) { return; } 
    //새로 업로드된 첨부파일이 있다면 테이블에 하나씩 새 첨부파일 추가.
    for (String fileName : files) {
      dao.replaceAttach(fileName, bno);
    }
  }
  
//  @Override
//  public void modify(BoardVO board) throws Exception {
//    dao.update(board);
//  }
  
  @Transactional
  @Override
  public void remove(Integer bno) throws Exception {
	//첨부파일 먼저 삭제. FK 때문.
    dao.deleteAttach(bno);
    dao.delete(bno);
  } 

//  @Override
//  public void remove(Integer bno) throws Exception {
//    dao.delete(bno);
//  }

  @Override
  public List<BoardVO> listAll() throws Exception {
    return dao.listAll();
  }

  @Override
  public List<BoardVO> listCriteria(Criteria cri) throws Exception {

    return dao.listCriteria(cri);
  }

  @Override
  public int listCountCriteria(Criteria cri) throws Exception {

    return dao.countPaging(cri);
  }

  @Override
  public List<BoardVO> listSearchCriteria(SearchCriteria cri) throws Exception {

    return dao.listSearch(cri);
  }

  @Override
  public int listSearchCount(SearchCriteria cri) throws Exception {

    return dao.listSearchCount(cri);
  }
  
  @Override
  public List<String> getAttach(Integer bno) throws Exception {
    
    return dao.getAttach(bno);
  }   

}
