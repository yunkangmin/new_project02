package org.project.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.project.domain.BoardVO;
import org.project.domain.Criteria;
import org.project.domain.PageMaker;
import org.project.service.BoardService;

//검색기능이 없는 게시판
//페이징처리가 되어있다.
@Controller
@RequestMapping("/board/*")
public class BoardController {

  private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

  @Inject
  private BoardService service;

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public void registerGET(BoardVO board, Model model) throws Exception {

    logger.info("register get ...........");
    
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public String registPOST(BoardVO board, RedirectAttributes rttr) throws Exception {

    logger.info("regist post ...........");
    logger.info(board.toString());

    service.regist(board);

    rttr.addFlashAttribute("msg", "SUCCESS");
    
    return "redirect:/board/listAll";
  }
  
  //페이징 처리전 게시글 목록 가져오기
  @RequestMapping(value = "/listAll", method = RequestMethod.GET)
  public void listAll(Model model) throws Exception {

    logger.info("show all list......................");
    model.addAttribute("list", service.listAll());
  }
  
  @RequestMapping(value = "/read", method = RequestMethod.GET)
  public void read(@RequestParam("bno") int bno, Model model) throws Exception {

    model.addAttribute(service.read(bno));
  }
  
  @RequestMapping(value = "/remove", method = RequestMethod.POST)
  public String remove(@RequestParam("bno") int bno, RedirectAttributes rttr) throws Exception {

    service.remove(bno);

    rttr.addFlashAttribute("msg", "SUCCESS");

    return "redirect:/board/listAll";
  }
  
  @RequestMapping(value = "/modify", method = RequestMethod.GET)
  public void modifyGET(int bno, Model model) throws Exception {

    model.addAttribute(service.read(bno));
  }

  @RequestMapping(value = "/modify", method = RequestMethod.POST)
  public String modifyPOST(BoardVO board, RedirectAttributes rttr) throws Exception {

    logger.info("mod post............");

    service.modify(board);
    rttr.addFlashAttribute("msg", "SUCCESS");

    return "redirect:/board/listAll";
  }
  
  //파라미터로 Criteria 클래스가 사용됬다. 페이징 처리가 됬다.
  //http://localhost:8080/board/listCri?page=3로 요청시 Criteria 클래스에 page 변수에 3이 설정된다.
  ///board/listCri?page=3&perPageNum=20으로 테스트 시 3번째 페이지에 게시글 20개가 나온다.
  @RequestMapping(value = "/listCri", method = RequestMethod.GET)
  public void listAll(Criteria cri, Model model) throws Exception {

    logger.info("show list Page with Criteria......................");
    
    model.addAttribute("list", service.listCriteria(cri));
  }
  
  
  //하단 페이징 처리된 페이지 호출
  @RequestMapping(value = "/listPage", method = RequestMethod.GET)
  public void listPage(@ModelAttribute("cri") Criteria cri, Model model) throws Exception {

    logger.info(cri.toString());

    model.addAttribute("list", service.listCriteria(cri));
    PageMaker pageMaker = new PageMaker();
    pageMaker.setCri(cri);
    //pageMaker.setTotalCount(131);
    
    //전체 게시글 개수를 세팅한다. 하단에 페이징 처리를 위해 필요함.
    pageMaker.setTotalCount(service.listCountCriteria(cri));

    model.addAttribute("pageMaker", pageMaker);
  }
  
  //조회페이지에서 다시 목록으로 갈 때 페이징 번호를 유지하기 위해 페이지번호(page), 페이지당 데이터의 수(perPageNum), 게시물 번호(bno)를 파라미터로 받는다.
  //Criteria는 페이징 처리를 위해 존재하는 클래스이므로 bno를 추가하지 않았다.
  @RequestMapping(value = "/readPage", method = RequestMethod.GET)
  public void read(@RequestParam("bno") int bno, @ModelAttribute("cri") Criteria cri, Model model) throws Exception {

    model.addAttribute(service.read(bno));
  }
  
  //페이지 번호를 유지하는 삭제처리
  //삭제 처리후 목록으로 이동 시 페이지 번호를 유지한다.
  @RequestMapping(value = "/removePage", method = RequestMethod.POST)
  public String remove(@RequestParam("bno") int bno, Criteria cri, RedirectAttributes rttr) throws Exception {

    service.remove(bno);

    rttr.addAttribute("page", cri.getPage());
    rttr.addAttribute("perPageNum", cri.getPerPageNum());
    rttr.addFlashAttribute("msg", "SUCCESS");
    
    //redirect는 서버에서 브라우저로 해당 경로로 다시 요청하라고 응답을 보낸다.
    //그러면 브라우저는 해당 경로로 다시 요청하며 RedirectAttributes 객체를 이용하여 해당경로에서 데이터를 한번만 사용할 수 있다.
    //RedirectAttributes를 사용하면 브라우저경로에 쿼리스트링이 표시되지 않고 데이터가 한 번만 사용되서 새로고침시 데이터를 계속 사용할 수 없다.
    return "redirect:/board/listPage";
  }
  
  //수정 페이지를 출력하는 메소드
  @RequestMapping(value = "/modifyPage", method = RequestMethod.GET)
  public void modifyPagingGET(@RequestParam("bno") int bno, @ModelAttribute("cri") Criteria cri, Model model)
      throws Exception {

    model.addAttribute(service.read(bno));
  }
  
  //수정처리 메소드
  //remove메소드와 비슷한 로직이다.
  @RequestMapping(value = "/modifyPage", method = RequestMethod.POST)
  public String modifyPagingPOST(BoardVO board, Criteria cri, RedirectAttributes rttr)
      throws Exception {
	  
	  service.modify(board);
	  
	  rttr.addAttribute("page", cri.getPage());
	  rttr.addAttribute("perPageNum", cri.getPerPageNum());
	  rttr.addFlashAttribute("msg", "SUCCESS");
	
	  return "redirect:/board/listPage";
  }

}
