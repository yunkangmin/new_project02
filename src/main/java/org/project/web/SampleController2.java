package org.project.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SampleController2 {

	private static final Logger logger = 
			LoggerFactory.getLogger(SampleController2.class);
	
	@RequestMapping("doA")
	public void doA(){
		
		logger.info("doA called....................");
		
	}
	
	@RequestMapping("doB")
	public void doB(){
		
		logger.info("doB called....................");
		
	}
	
}


