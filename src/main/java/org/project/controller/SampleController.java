package org.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.project.domain.SampleVO;

@RestController //@RestController는 별도의 처리가 없어도 객체는 자동으로 JSON으로 처리된다.
@RequestMapping("/sample")
public class SampleController {

  @RequestMapping("/hello")
  public String sayHello() { 
	//@RestController에서 문자열 데이터는 기본적으로 브라우저에서 'text/html'로 처리된다.
	//브라우저에 Hello World가 출력된다.
    return "Hello World ";
  }

  //sendVO 경로를 호출했을 때 브라우저에 JSON이 호출되게하려면 pom.xml에 jackson 라이브러리를 설정해줘야 한다. 
  @RequestMapping("/sendVO")
  public SampleVO sendVO() {

    SampleVO vo = new SampleVO();
    vo.setFirstName("길동");
    vo.setLastName("홍");
    vo.setMno(123);

    return vo;
  }

  @RequestMapping("/sendList")
  public List<SampleVO> sendList() {

    List<SampleVO> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      SampleVO vo = new SampleVO();
      vo.setFirstName("길동");
      vo.setLastName("홍");
      vo.setMno(i);
      list.add(vo);
    }
    return list;
  }

  @RequestMapping("/sendMap")
  public Map<Integer, SampleVO> sendMap() {

    Map<Integer, SampleVO> map = new HashMap<>();

    for (int i = 0; i < 10; i++) {
      SampleVO vo = new SampleVO();
      vo.setFirstName("길동");
      vo.setLastName("홍");
      vo.setMno(i);
      map.put(i, vo);
    }
    return map;
  }

  //@RestController가 별도의 뷰를 제공하지 않는 형태로 서비스를 실행하기 때문에 ResponseEntity 클래스를 사용하여
  //결과데이터 + HTTP 상태코드를 직접제어하여 리턴할 수 있다. 
  @RequestMapping("/sendErrorAuth")
  public ResponseEntity<Void> sendListAuth() {

    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @RequestMapping("/sendErrorNot")
  public ResponseEntity<List<SampleVO>> sendListNot() {

    List<SampleVO> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      SampleVO vo = new SampleVO();
      vo.setFirstName("길동");
      vo.setLastName("홍");
      vo.setMno(i);
      list.add(vo);
    }
    
    //결과데이터와 상태코드를 같이 리턴한다.
    return new ResponseEntity<>(list, HttpStatus.NOT_FOUND);
  }

}
