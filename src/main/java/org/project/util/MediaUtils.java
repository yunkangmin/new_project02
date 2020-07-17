package org.project.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

//이미지 파일인지 확인해주는 클래스
//확장자에 맞는 MIME 타입을 리턴한다. -> image/gif
public class MediaUtils {
	
	private static Map<String, MediaType> mediaMap;
	
	static{
		
		mediaMap = new HashMap<String, MediaType>();		
		mediaMap.put("JPG", MediaType.IMAGE_JPEG);
		mediaMap.put("GIF", MediaType.IMAGE_GIF);
		mediaMap.put("PNG", MediaType.IMAGE_PNG);
	}
	
	public static MediaType getMediaType(String type){
		
		return mediaMap.get(type.toUpperCase());
	}
}



