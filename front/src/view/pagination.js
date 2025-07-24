import '../css/pagination.css'; // 스타일링 파일을 import

// http://localhost:8090/api/search/lawyer?lawyer=null&page=1
// 페이지네이션 버튼과 페이지 이동을 처리하는 로직을 포함하고 있으며, 검색 결과를 API로 요청하고 갱신하는 데 사용

const PaginationComponent = ({ currentPage, totalPages, onPageChange, fetchData, setData, SearchUrl, name = null }) => {
  // 페이지 번호 범위 지정 (예: 현재 페이지를 기준으로 -5, +5 범위)
  const startPage = Math.max(currentPage - 2, 1);      // 최소 1페이지
  const endPage = Math.min(currentPage + 2, totalPages); // 최대 totalPages까지

  // start ~ end 범위의 숫자 배열 생성
  const createRangeArray = (start, end) => {
    return Array.from({ length: end - start + 1 }, (_, index) => start + index);
  };

  const pageNumbers = createRangeArray(startPage, endPage);
  
  // 이전 버튼 클릭 시: 이전 페이지로 이동 + 데이터 재요청
  const handlePrevious = () => {
    if (currentPage > 1) {
      onPageChange(currentPage - 1);
      fetchData(SearchUrl, name, null, setData, null, null, currentPage - 1)
    }
  };

  // 다음 버튼 클릭 시: 다음 페이지로 이동 + 데이터 재요청
  const handleNext = () => {
    if (currentPage < totalPages) {
      onPageChange(currentPage + 1);
      fetchData(SearchUrl, name, null, setData, null, null, currentPage + 1)
    }
  };

  return (
    <div className="pagination-container">
      <button 
        className="pagination-btn" 
        onClick={handlePrevious} 
        disabled={currentPage === 1}>
        Previous
      </button>

      {pageNumbers.map((pageNumber) => (
        <button
          key={pageNumber}
          onClick={() => {
            onPageChange(pageNumber)
            fetchData(SearchUrl, name, null, setData, null, null, pageNumber)
          }}
          className={`pagination-btn ${currentPage === pageNumber ? 'active' : ''}`}
        >
          {pageNumber}
        </button>
      ))}

      <button 
        className="pagination-btn" 
        onClick={handleNext} 
        disabled={currentPage === totalPages}>
        Next
      </button>
    </div>
  );
};

export default PaginationComponent;
