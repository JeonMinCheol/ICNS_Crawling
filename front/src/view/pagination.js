import '../css/pagination.css'; // 스타일링 파일을 import

const PaginationComponent = ({ currentPage, totalPages, onPageChange, fetchData, setData, SearchUrl, name = null }) => {
  // 페이지 번호 범위 지정 (예: 현재 페이지를 기준으로 -5, +5 범위)
  const startPage = Math.max(currentPage - 2, 1);      // 최소 1페이지
  const endPage = Math.min(currentPage + 2, totalPages); // 최대 totalPages까지

  const createRangeArray = (start, end) => {
    return Array.from({ length: end - start + 1 }, (_, index) => start + index);
  };

  const pageNumbers = createRangeArray(startPage, endPage);

  const handlePrevious = () => {
    if (currentPage > 1) {
      onPageChange(currentPage - 1);
      fetchData(SearchUrl, name, null, setData, null, null, null, currentPage - 1)
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages) {
      onPageChange(currentPage + 1);
      fetchData(SearchUrl, name, null, setData, null, null, null, currentPage + 1)
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
            fetchData(SearchUrl, name, null, setData, null, null, null, pageNumber)
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
