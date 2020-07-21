package org.project.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.project.util.MediaUtils;
import org.project.util.UploadFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

//파일업로드 컨트롤러
@Controller
public class UploadController {

  private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

  //파일업로드 경로
  @Resource(name = "uploadPath")
  private String uploadPath;

  @RequestMapping(value = "/uploadForm", method = RequestMethod.GET)
  public void uploadForm() {
  }
  
  //jsp에서 파일업로드를 하면 servlet-context.xml에서 설정한 multipartResolver 설정을 통해서 처리되고 아래 메소드가 실행된다.
  @RequestMapping(value = "/uploadForm", method = RequestMethod.POST)
  public String uploadForm(MultipartFile file, Model model) throws Exception {
	
	//log가 안찍힌다면 src/main/resources/log4j.xml 파일을 확인해본다.
	//전송된 파일이름
    logger.info("originalName: " + file.getOriginalFilename());
    //파일 크기
    logger.info("size: " + file.getSize());
    //MIME 타입
    logger.info("contentType: " + file.getContentType());
                       //uploadFile()는 파일이름과 파일데이터(byte[])를 가지고 실제 파일을 업로드한다.
    String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
    //파일이름 저장.
    model.addAttribute("savedName", savedName);
    //uploadForm 페이지 내부에 iframe 태그에 uploadResult 페이지 반환.
    return "uploadResult";
  }

  @RequestMapping(value = "/uploadAjax", method = RequestMethod.GET)
  public void uploadAjax() {
  }

  private String uploadFile(String originalName, byte[] fileData) throws Exception {
	//고유키 생성. 이름 중복을 방지한다.
    UUID uid = UUID.randomUUID();
    //UUID + "-" + 파일이름을 합쳐 파일이름을 만든다.
    //1ba20d73-7865-4a78-87f7-8793ebbfcd3a_생각.txt
    String savedName = uid.toString() + "_" + originalName;
    //파일이 생성된다.
    File target = new File(uploadPath, savedName);
    //스프링에서 제공하는 FileCopyUtils.
    //파일데이터를 파일객체에 기록한다. 실제 파일이 생성된다.
    FileCopyUtils.copy(fileData, target);

    return savedName;

  }
  
  @ResponseBody
  //produces = "text/plain;charset=UTF-8"는 한글을 정상적으로 클라이언트에게 전송하기 위한 설정이다.
  @RequestMapping(value ="/uploadAjax", method=RequestMethod.POST, 
                  produces = "text/plain;charset=UTF-8")
  public ResponseEntity<String> uploadAjax(MultipartFile file)throws Exception{
    
    logger.info("originalName: " + file.getOriginalFilename());
    
   
    return 
      new ResponseEntity<>(
    	  // /2020/07/21/s_aa6bd1e8-7f04-43c5-9eca-096ccf0ab3f3_994BEF355CD0313D05.png가 리턴됨.
    	  // 파일을 업로드하고 업로드한 경로를 리턴함.
          UploadFileUtils.uploadFile(uploadPath, 
                file.getOriginalFilename(), 
                file.getBytes()), 
          HttpStatus.CREATED); //HttpStatus.CREATED는 원하는 리소스가 정상적으로 생성되었다는 상태코드이다.
  }
  
  //이미지 업로드하면 브라우저에 썸네일 이미지가 보이게 한다.
  //파라미터로 파일이름을 받는다.파일이름은 '/년/월/일/파일명'형식이다.
  //결과는 byte[]로 실제 파일 데이터이다.
  @ResponseBody
  @RequestMapping("/displayFile")
  public ResponseEntity<byte[]>  displayFile(String fileName)throws Exception{
    
    InputStream in = null; 
    ResponseEntity<byte[]> entity = null;
    
    logger.info("FILE NAME: " + fileName);
    
    try{
      //파일이름에서 확장자를 추출한다.
      String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
      //확장자에 해당하는 MIME 타입을 리턴받는다.
      MediaType mType = MediaUtils.getMediaType(formatName);
      
      HttpHeaders headers = new HttpHeaders();
      
      in = new FileInputStream(uploadPath+fileName);
      
      //이미지 파일이라면
      if(mType != null){
    	//헤더에 MIME 타입을 설정한다. 
        headers.setContentType(mType);
      //이미지 파일이 아니라면
      }else{
        
        fileName = fileName.substring(fileName.indexOf("_")+1);
        //MIME 타입을 다운로드 용으로 사용하는 'application/octet-stream'으로 지정한다.
        //브라우저는 이 MIME 타입을 보고 사용자에게 자동으로 다운로드 창을 열어준다.
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition", "attachment; filename=\""+ 
		  //파일이름이 한글인 경우 깨지지 않게 하기 위해 인코딩 처리를 한다.
          new String(fileName.getBytes("UTF-8"), "ISO-8859-1")+"\"");
      }
      										//commons 라이브러리를 이용해 파일로 부터 실제 파일데이터를 읽는다.	byte[]가 리턴된다.						
        entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), 
          headers, 
          HttpStatus.CREATED);
    }catch(Exception e){
      e.printStackTrace();
      entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
    }finally{
      in.close();
    }
      return entity;    
  }
    
  //파일 삭제
  @ResponseBody
  @RequestMapping(value="/deleteFile", method=RequestMethod.POST)
  public ResponseEntity<String> deleteFile(String fileName){
    
    logger.info("delete file: "+ fileName);
    //확장자 추출
    String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
    //이미지 파일일 시 MIME 타입 반환.
    MediaType mType = MediaUtils.getMediaType(formatName);
    
    //이미지 파일이라면
    if(mType != null){      
      //원본파일 이름 추출. /2020/07/17/
      //substring은 index가 0부터 시작하며 두번째 매개변수가 12라면 그 전인 11까지 가져온다.
      String front = fileName.substring(0,12);
      //58248f05-9732-48c1-a4b7-ba2a7a24875a_994BEF355CD0313D05.png
      String end = fileName.substring(14);
      //원본 파일 지우기.
      new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete();
    }
    //썸네일, 일반 파일 지우기.
    new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();
    
    
    return new ResponseEntity<String>("deleted", HttpStatus.OK);
  }  
  
  //실제로 폴더 경로 상에 첨부파일 삭제
  @ResponseBody
  @RequestMapping(value="/deleteAllFiles", method=RequestMethod.POST)
  //files[]로 배열을 파라미터로 받을 수 있다.
  public ResponseEntity<String> deleteFile(@RequestParam("files[]") String[] files){
    
    logger.info("delete all files: "+ files);
    
    if(files == null || files.length == 0) {
      return new ResponseEntity<String>("deleted", HttpStatus.OK);
    }
    
    for (String fileName : files) {
      //확장자 가져오기.
      String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
      //MIME 타입가져오기. MIME 타입이란 웹을 통해 전달되는 다양한 형태의 파일을 표현하기 위해 사용된다.
      MediaType mType = MediaUtils.getMediaType(formatName);
      //이미지 파일이라면
      if(mType != null){      
        // /년/월/일
        String front = fileName.substring(0,12);
        // 's_'를 뺀 원본파일이름.
        String end = fileName.substring(14);
        //원본이미지파일 삭제
        new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete();
      }
      //썸네일이미지, 일반파일 삭제
      new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();
      
    }
    return new ResponseEntity<String>("deleted", HttpStatus.OK);
  }  

}
//  @ResponseBody
//  @RequestMapping(value = "/uploadAjax", 
//                 method = RequestMethod.POST, 
//                 produces = "text/plain;charset=UTF-8")
//  public ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {
//
//    logger.info("originalName: " + file.getOriginalFilename());
//    logger.info("size: " + file.getSize());
//    logger.info("contentType: " + file.getContentType());
//
//    return 
//        new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.CREATED);
//  }

// @RequestMapping(value = "/uploadForm", method = RequestMethod.POST)
// public void uploadForm(MultipartFile file, Model model) throws Exception {
//
// logger.info("originalName: " + file.getOriginalFilename());
// logger.info("size: " + file.getSize());
// logger.info("contentType: " + file.getContentType());
//
// String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
//
// model.addAttribute("savedName", savedName);
//
// }
//
// private String uploadFile(String originalName, byte[] fileData)throws
// Exception{
//
// UUID uid = UUID.randomUUID();
//
// String savedName = uid.toString() + "_"+ originalName;
//
// File target = new File(uploadPath,savedName);
//
// FileCopyUtils.copy(fileData, target);
//
// return savedName;
//
// }