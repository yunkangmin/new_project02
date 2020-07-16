package org.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

//@ControllerAdvice는 컨트롤러 메소드에서 발생된 Exception을 모두 처리하는 역할을 한다.
@ControllerAdvice
public class CommonExceptionAdvice {

  private static final Logger logger = LoggerFactory.getLogger(CommonExceptionAdvice.class);

  //@ExceptionHandler(Exception.class)
  public String common(Exception e) {

    logger.info(e.toString());

    return "error_common";
  }
  
  //Exception타입의 예외를 모두 처리한다.
  //파라미터로 Exception만 사용할수 있고, Model을 쓰지 못하므로 ModelAndView 타입을 사용한다.
  @ExceptionHandler(Exception.class)
  private ModelAndView errorModelAndView(Exception ex) {

    ModelAndView modelAndView = new ModelAndView();
    //error_common페이지에서 에러를 상세하게 확인할 수 있다.
    modelAndView.setViewName("/error_common");
    modelAndView.addObject("exception", ex);

    return modelAndView;
  }

}


