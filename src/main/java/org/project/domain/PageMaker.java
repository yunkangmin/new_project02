package org.project.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

//하단 페이징 처리 클래스
//클래스로 만들어 관리하기 쉽다.
public class PageMaker {

	private int totalCount;
	private int startPage;
	private int endPage;
	private boolean prev;
	private boolean next;

	// 하단에 보여지는 페이징 번호 개수를 설정한다.
	private int displayPageNum = 10;

	private Criteria cri;

	public void setCri(Criteria cri) {
		this.cri = cri;
	}
	
	//컨트롤러에서 totalCount를 세팅한뒤 calcData 메소드를 호출한다.
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;

		calcData();
	}

	//endPage, startPage, prev, next를 계산한다.
	private void calcData() {
		//Math.ceil은 무조건 반올림하는 함수이다. endPage(1~10이면 10을 말한다.)을 계산한다.요청한 페이지 번호에 따라 달라진다.
		endPage = (int) (Math.ceil(cri.getPage() / (double) displayPageNum) * displayPageNum);
		//startPage(1~10이면 1)을 계산한다.
		startPage = (endPage - displayPageNum) + 1;
		//임시endPage를 계산한다. 임시endPage는 전체 endpage이다. 전체 endPage를 구하는 이유는 위에서 구한 endPage가 전체EndPage보다 클 경우
		//endPage변수에 전체 endPage를 세팅하기 위함이다.
		int tempEndPage = (int) (Math.ceil(totalCount / (double) cri.getPerPageNum()));
		//endPage가 전체EndPage보다 클 경우 endPage변수에 전체 endPage를 세팅한다.
		//endPage가 tempEndPage보다 크면 없는 데이터를 요청하기 때문이다.
		if (endPage > tempEndPage) {
			endPage = tempEndPage;
		}
        //이전 버튼이 보일지 말지 유무를 계산한다.
		//startPage가 1이면 보일필요가 없다
		prev = startPage == 1 ? false : true;
		//다음 버튼이 보일지 말지 유무를 계산한다.
		//endPage * cri.getPerPageNum() 값이 totalCount 개수와 같거나 크면 버튼이 안보인다.
		next = endPage * cri.getPerPageNum() >= totalCount ? false : true;

	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getStartPage() {
		return startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public boolean isPrev() {
		return prev;
	}

	public boolean isNext() {
		return next;
	}

	public int getDisplayPageNum() {
		return displayPageNum;
	}

	public Criteria getCri() {
		return cri;
	}

	//하단 페이지 번호 링크를 생성한다.
	public String makeQuery(int page) {
        //UriComponentsBuilder를 사용하기는 이유는 URI 쉽고 편하게 생성하며, 가독성이 좋다. JSP에서 직접 링크를 생성하는 것보다 적은 양의 코드로 개발이 가능하다.
		UriComponents uriComponents = UriComponentsBuilder.newInstance().queryParam("page", page)
				.queryParam("perPageNum", cri.getPerPageNum()).build();

		return uriComponents.toUriString();
	}
	
	//검색기능이 추가된 게시글에서 사용될 URI 생성
	//page, perPageNum, searchType, keyword 4개의 파라미터가 있다.
	public String makeSearch(int page) {

		UriComponents uriComponents = UriComponentsBuilder.newInstance().queryParam("page", page)
				.queryParam("perPageNum", cri.getPerPageNum())
				.queryParam("searchType", ((SearchCriteria) cri).getSearchType())
				.queryParam("keyword", encoding(((SearchCriteria) cri).getKeyword())).build();

		return uriComponents.toUriString();
	}
	
	//한글 인코딩 처리를 한다.
	private String encoding(String keyword) {

		if (keyword == null || keyword.trim().length() == 0) {
			return "";
		}

		try {
			return URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}
