package org.project.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.project.domain.Criteria;
import org.project.domain.ReplyVO;
import org.project.persistence.BoardDAO;
import org.project.persistence.ReplyDAO;

@Service
public class ReplyServiceImpl implements ReplyService {

  @Inject
  private ReplyDAO replyDAO;
  
  @Inject
  private BoardDAO boardDAO;

  //댓글을 등록하고 게시글 테이블에 댓글 개수를 1 증가 시킨다.
  @Transactional
  @Override
  public void addReply(ReplyVO vo) throws Exception {

    replyDAO.create(vo);
    boardDAO.updateReplyCnt(vo.getBno(), 1);
  }
  
  //댓글 삭제 시 댓글 번호에 해당하는 게시물 번호를 가져오고, 해당 댓글을 삭제하고, 게시글 테이블에서 댓글 개수를 -1시킨다.
  @Transactional
  @Override
  public void removeReply(Integer rno) throws Exception {
  
    int bno = replyDAO.getBno(rno);
    replyDAO.delete(rno);
    boardDAO.updateReplyCnt(bno, -1);
  }   



  @Override
  public List<ReplyVO> listReply(Integer bno) throws Exception {

    return replyDAO.list(bno);
  }

  @Override
  public void modifyReply(ReplyVO vo) throws Exception {

    replyDAO.update(vo);
  }



  @Override
  public List<ReplyVO> listReplyPage(Integer bno, Criteria cri) 
      throws Exception {

    return replyDAO.listPage(bno, cri);
  }

  @Override
  public int count(Integer bno) throws Exception {

    return replyDAO.count(bno);
  }

}
