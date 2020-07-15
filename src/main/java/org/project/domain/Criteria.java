package org.project.domain;

//파라미터가 여러개로 늘어나면 관리하기가 어려워지기 때문에 클래스로 만든다.
//SQL Mapper에서 쓰인다.
public class Criteria {

  private int page;
  private int perPageNum;

  public Criteria() {
	//기본으로 지정
    this.page = 1;
    this.perPageNum = 10;
  }
 
  //요청한 페이지가 0이거나 0보다 작으면 1로 설정한다.
  public void setPage(int page) {

    if (page <= 0) {
      this.page = 1;
      return;
    }

    this.page = page;
  }
  
  //요청한 페이지 당 보여줄 페이지 개수가 0보다 같거나 작거나 100보다 큰 수를 요청하면 10으로 세팅한다. 
  //사용자가 perPageNum을 100000으로 하게 되면 DB에서 많은 시간을 소모하게 되므로 제한을 한다. 
  public void setPerPageNum(int perPageNum) {

    if (perPageNum <= 0 || perPageNum > 100) {
      this.perPageNum = 10;
      return;
    }

    this.perPageNum = perPageNum;
  }

  public int getPage() {
    return page;
  }
  
  //SQL에서 시작페이지를 계산한다.
  //예를들어 1페이지를 요청하면 시작페이지는 0이된다.
  // method for MyBatis SQL Mapper -
  public int getPageStart() {

    return (this.page - 1) * perPageNum;
  }

  // method for MyBatis SQL Mapper
  public int getPerPageNum() {

    return this.perPageNum;
  }

  @Override
  public String toString() {
    return "Criteria [page=" + page + ", "
        + "perPageNum=" + perPageNum + "]";
  }
}
