package org.project.service;

import org.project.domain.MessageVO;

public interface MessageService {

  public void addMessage(MessageVO vo) throws Exception;

  public MessageVO readMessage(String uid, Integer mno) throws Exception;
}
