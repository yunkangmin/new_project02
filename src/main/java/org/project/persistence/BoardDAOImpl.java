package org.project.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.project.domain.BoardVO;
import org.project.domain.Criteria;
import org.project.domain.SearchCriteria;
import org.springframework.stereotype.Repository;

@Repository
public class BoardDAOImpl implements BoardDAO {

  @Inject
  private SqlSession session;

  private static String namespace = "org.project.mapper.BoardMapper";

  @Override
  public void create(BoardVO vo) throws Exception {
    session.insert(namespace + ".create", vo);
  }

  @Override
  public BoardVO read(Integer bno) throws Exception {
    return session.selectOne(namespace + ".read", bno);
  }

  @Override
  public void update(BoardVO vo) throws Exception {
    session.update(namespace + ".update", vo);
  }

  @Override
  public void delete(Integer bno) throws Exception {
    session.delete(namespace + ".delete", bno);
  }

  @Override
  public List<BoardVO> listAll() throws Exception {
    return session.selectList(namespace + ".listAll");
  }
  
  //게시글 목록 페이징 처리 
  @Override
  public List<BoardVO> listPage(int page) throws Exception {
	//요청한 page가 0이거나 0보다 작으면 1을 디폴트로 세팅한다.
    if (page <= 0) {
      page = 1;
    }
    
    //시작페이지를 계산한다.
    page = (page - 1) * 10;

    return session.selectList(namespace + ".listPage", page);
  }
  
  @Override
  public List<BoardVO> listCriteria(Criteria cri) throws Exception {

    return session.selectList(namespace + ".listCriteria", cri);
  }

  @Override
  public int countPaging(Criteria cri) throws Exception {

    return session.selectOne(namespace + ".countPaging", cri);
  }

  @Override
  public List<BoardVO> listSearch(SearchCriteria cri) throws Exception {

    return session.selectList(namespace + ".listSearch", cri);
  }

  @Override
  public int listSearchCount(SearchCriteria cri) throws Exception {

    return session.selectOne(namespace + ".listSearchCount", cri);
  }
  
  @Override
  public void updateReplyCnt(Integer bno, int amount) throws Exception {

    Map<String, Object> paramMap = new HashMap<String, Object>();

    paramMap.put("bno", bno);
    paramMap.put("amount", amount);

    session.update(namespace + ".updateReplyCnt", paramMap);
  }

  @Override
  public void updateViewCnt(Integer bno) throws Exception {
    
    session.update(namespace+".updateViewCnt", bno);
    
  }
  
  @Override
  public void addAttach(String fullName) throws Exception {
    
    session.insert(namespace+".addAttach", fullName);
    
  }
  
  @Override
  public List<String> getAttach(Integer bno) throws Exception {
    
    return session.selectList(namespace +".getAttach", bno);
  }
 
  //특정 게시물 번호에 해당하는 모든 첨부파일 삭제
  @Override
  public void deleteAttach(Integer bno) throws Exception {

    session.delete(namespace+".deleteAttach", bno);
    
  }
  
  //새로운 첨부파일 추가.
  //첨부파일 이름과 게시물번호가 파라미터로 온다.
  @Override
  public void replaceAttach(String fullName, Integer bno) throws Exception {
    
    Map<String, Object> paramMap = new HashMap<String, Object>();
    
    paramMap.put("bno", bno);
    paramMap.put("fullName", fullName);
    
    session.insert(namespace+".replaceAttach", paramMap);
    
  }

}
