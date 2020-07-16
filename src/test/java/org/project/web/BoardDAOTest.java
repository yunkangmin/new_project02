package org.project.web;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.project.domain.BoardVO;
import org.project.domain.Criteria;
import org.project.domain.SearchCriteria;
import org.project.persistence.BoardDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/root-context.xml" })
public class BoardDAOTest {

  @Inject
  private BoardDAO dao;

  private static Logger logger = LoggerFactory.getLogger(BoardDAOTest.class);

  @Test
  public void testCreate() throws Exception {

    BoardVO board = new BoardVO();
    board.setTitle("새로운 글을 넣습니다. ");
    board.setContent("새로운 글을 넣습니다. ");
    board.setWriter("user00");
    dao.create(board);
  }

  @Test
  public void testRead() throws Exception {

    logger.info(dao.read(1).toString());
  }

  @Test
  public void testUpdate() throws Exception {

    BoardVO board = new BoardVO();
    board.setBno(1);
    board.setTitle("수정된 글입니다.");
    board.setContent("수정 테스트 ");
    dao.update(board);
  }

  @Test
  public void testDelete() throws Exception {

    dao.delete(1);
  }
  
  //페이징 처리 테스트. Criteria 클래스 생성 전.
  @Test
  public void testListPage() throws Exception {
	//page 3 설정
    int page = 3;
    
    //Mapper 파일에서 listPage 쿼리를 탄다.
    List<BoardVO> list = dao.listPage(page);
    
    //list에서 하나씩 출력
    for (BoardVO boardVO : list) {
      logger.info(boardVO.getBno() + ":" + boardVO.getTitle());
    }
  }
  
  //Criteria 클래스를 사용한 게시글 페이징 처리 목록 테스트
  @Test
  public void testListCriteria() throws Exception {

    Criteria cri = new Criteria();
    //요청한 page를 세팅한다.
    //설정을 하면 시작페이지가 계산된다. 
    cri.setPage(2);
    //페이지 당 보여줄 게시글 개수 설정.
    cri.setPerPageNum(20);

    //select * from tbl_board where bno > 0 order by bno desc limit 20, 20;
    List<BoardVO> list = dao.listCriteria(cri);

    //Workbench 결과와 콘솔에 출력되는 결과가 일치하는지 확인한다.
    for (BoardVO boardVO : list) {
      logger.info(boardVO.getBno() + ":" + boardVO.getTitle());
    }
    
    
  }
  
  //UriComponentsBuilder 테스트
  @Test
  public void testURI() throws Exception {

    UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/board/read").queryParam("bno", 12)
        .queryParam("perPageNum", 20).build();
    
    //uri가 같은지 비교
    logger.info("/board/read?bno=12&perPageNum=20");
    logger.info(uriComponents.toString());

  }

  @Test
  public void testURI2() throws Exception {
	//특정 URI를 지정하고 테스트  
    UriComponents uriComponents = UriComponentsBuilder.newInstance().path("/{module}/{page}").queryParam("bno", 12)
        .queryParam("perPageNum", 20).build().expand("board", "read").encode();

    logger.info("/board/read?bno=12&perPageNum=20");
    logger.info(uriComponents.toString());
  }

  //검색처리를 위헤 동적 쿼리 사용전  쿼리 테스트
  @Test
  public void testDynamic1() throws Exception {

    SearchCriteria cri = new SearchCriteria();
    cri.setPage(1);
    cri.setKeyword("글");
    cri.setSearchType("t");

    logger.info("=====================================");

    List<BoardVO> list = dao.listSearch(cri);

    for (BoardVO boardVO : list) {
      logger.info(boardVO.getBno() + ": " + boardVO.getTitle());
    }

    logger.info("=====================================");

    logger.info("COUNT: " + dao.listSearchCount(cri));
  }

}
