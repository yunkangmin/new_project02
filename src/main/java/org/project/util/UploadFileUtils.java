package org.project.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

//파일 업로드용 클래스
public class UploadFileUtils {

  private static final Logger logger = 
      LoggerFactory.getLogger(UploadFileUtils.class);

//  public static String uploadFile(String uploadPath, 
//      String originalName, 
//      byte[] fileData)throws Exception{
//    
//    return null;
//  }
//  

  //파일업로드
  //파일업로드를 하기 위해 저장경로, 파이이름, 파일 데이터 3개의 파라미터를 받는다. 
  // /2020/07/17/58248f05-9732-48c1-a4b7-ba2a7a24875a_994BEF355CD0313D05.png가 리턴됨.
  public static String uploadFile(String uploadPath, 
                              String originalName, 
                              byte[] fileData)throws Exception{
    //UUID 생성
    UUID uid = UUID.randomUUID();
    //파일이름 생성
    String savedName = uid.toString() +"_"+originalName;
    //년/월/일 경로에 대한 폴더를 만들고 경로를 리턴한다. -> /2020/07/17
    String savedPath = calcPath(uploadPath);
    // C:/zzz/upload/2020/07/17 + 파일이름 에 대한 파일객체 생성.
    File target = new File(uploadPath +savedPath,savedName);
    //파일객체에 파일데이터를 복사한다. 실제 파일이 생성된다.
    FileCopyUtils.copy(fileData, target);
    //파일 확장자 추출.
    String formatName = originalName.substring(originalName.lastIndexOf(".")+1);
    
    String uploadedFileName = null;
    //확장자로 이미지 파일인지 확인한다.
    //null이 아니면 이미지파일이다.
    //업로드한 파일이 이미지 파일일 때
    if(MediaUtils.getMediaType(formatName) != null){
    	                 //썸네일 생성함수
      uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName);
    //업로드한 파일이 이미지 파일이 아닐 때  
    }else{
    	                 //단순히 파일 경로에서 '\'를 '/'로 치환하는 용도.
      uploadedFileName = makeIcon(uploadPath, savedPath, savedName);
    }
    
    return uploadedFileName;
    
  }
  
  //이미지 파일이 아닐 때 '\'을 '/'로 치환하는 용도
  private static  String makeIcon(String uploadPath, 
      String path, 
      String fileName)throws Exception{
	// File.separator(String)과 File.separatorChar(char)은 반환 타입이 다를 뿐 출력내용은 같다.
	// '\' 역 슬래쉬를 의미한다.
    String iconName = 
        uploadPath + path + File.separator+ fileName;
    // '\'를 '/'로 변경한다. 
    return iconName.substring(
        uploadPath.length()).replace(File.separatorChar, '/');
  }
  
  //이미지 업로드 시 썸네일 이미지 생성하는 메소드.
  //파라미터는 기본경로, 년/월/일 경로, 업로드된 파일이름이 사용된다.
  private static  String makeThumbnail(
              String uploadPath, 
              String path, 
              String fileName)throws Exception{
    
	//BufferedImage는 실제 이미지가 아닌 메모리상의 이미지를 의미한다.
	// ImageIO.read()로 실제 이미지 파일을 메모리로 불러온다.
    BufferedImage sourceImg = 
        ImageIO.read(new File(uploadPath + path, fileName));
    
    //원본 이미지의 높이를 FIT_TO_HEIGHT를 이용하여 100px로 지정하여 썸네일 규격을 정한다.
    BufferedImage destImg = 
        Scalr.resize(sourceImg, 
            Scalr.Method.AUTOMATIC, 
            Scalr.Mode.FIT_TO_HEIGHT,100);
    //썸네일은 파일이름이 "s_"로 시작한다.
    String thumbnailName = 
        uploadPath + path + File.separator +"s_"+ fileName;
    //썸네일 파일 객체를 생성한다.
    File newFile = new File(thumbnailName);
    //원본 파일이름에서 확장자를 가져온다.
    String formatName = 
        fileName.substring(fileName.lastIndexOf(".")+1);
    
    //설정한 썸네일 설정과 확장자, 지정한 이름을 가지고 경로에 썸네일 이미지 파일을 생성한다.
    ImageIO.write(destImg, formatName.toUpperCase(), newFile);
    //'\'를 '/'로 변경한다. 브라우저에서 정상적으로 인식되지 않는다.
    return thumbnailName.substring(
        uploadPath.length()).replace(File.separatorChar, '/');
  } 
  
  //년/월/일로 된 경로를 생성하고 그 경로를 리턴한다.
  private static String calcPath(String uploadPath){
    //java.util.Calendar 클래스를 이용해 Calendar 객체를 생성한다.
    Calendar cal = Calendar.getInstance();
    // "\" + 년도
    String yearPath = File.separator+cal.get(Calendar.YEAR);
    // "\" + 년도 + 월(포맷 -> 00) 
    String monthPath = yearPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);
    // "\" + 년도 + 월(포맷 -> 00) + 일(포맷 -> 00)
    String datePath = monthPath + 
        File.separator + 
        new DecimalFormat("00").format(cal.get(Calendar.DATE));
    
    //실제 년/월/일에 폴더를 생성해줌.
    makeDir(uploadPath, yearPath,monthPath,datePath);
    
    logger.info(datePath);
    // 년/월/일 경로 리턴
    return datePath;
  }
  
  //실제로 파일저장경로/년/월/일에 해당 하는 폴더를 생성한다.
  //매개변수로 가변인자를 사용한다(컴파일 시 배열로 변경된다.)
  private static void makeDir(String uploadPath, String... paths){

	//경로에 대한 파일이 이미 존재한다면 종료. paths[paths.length-1]는 dataPath를 의미.
	// 예) \2020\07\17  
    if(new File(paths[paths.length-1]).exists()){
      return;
    }
    
    // 1. \2020
    // 2. \2020\07
    // 3. \2020\07\17
    // 순차적으로 돌면서 폴더가 있는지 검사하고 없으면 생성한다.
    for (String path : paths) {
      File dirPath = new File(uploadPath + path);
      
      if(! dirPath.exists() ){
        dirPath.mkdir();
      } 
    }
  }
  
  
}
