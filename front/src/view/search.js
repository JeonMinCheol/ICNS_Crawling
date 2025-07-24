import "../css/search.css"
import { useNavigate } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import PaginationComponent from './pagination.js';
import axiosInstance from '../svc/axiosInstance.js';
import baseUrl from "../svc/baseUrl.js";

// 변호사명/사건번호를 기반으로 검색 기능과 결과 렌더링을 담당

// 검색용 URL 상수 정의
const lawyerSearchUrl = baseUrl + '/api/search/lawyer?lawyer=';
const indexSearchUrl = baseUrl + '/api/search/index?indexNo=';
const countSearchUrl = baseUrl + '/api/count';

// 페이지 정보 포함된 URL 생성 함수
function urlBuild(base, param1, page) {
  if(page != null)
    return base + param1 + "&page=" + page

  return base + param1
}

// API 데이터 fetch 함수 (공통 사용)
const fetchData = async (searchUrl, name = null, setLoading = null, setData = null, nullData = null, setError = null, page = null) => {
  try {
    // API 호출
    const response = await axiosInstance.get(urlBuild(searchUrl, name, page)).then(response => {
      return response
    })
    .catch(error => {
      console.error('Error:', error);
    });
    
    if (setData != null)
      setData(response.data);
    if (nullData != null)
      nullData(null);
  } 

  catch (err) {
    if (setError != null)
      setError(err)
    if (setData != null)
      setData(null);
  } finally {
    if (setLoading != null)
      setLoading(false)
  }
};

// 변호사명 검색 컴포넌트
function LawyerSearchBar(param) {
  const [searchTerm, setSearchTerm] = useState('');
  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  return(
    <div>
      <div style={{display:"flex"}}>
        <input
          type="text"
          placeholder="Search By a Lawyer"
          value={searchTerm}
          onChange={handleSearchChange}
        />
    
    <button onClick={() => {
      param.setLoading(true)
      param.setPage(1)
      param.setUrl(lawyerSearchUrl + searchTerm)
      fetchData(lawyerSearchUrl, searchTerm, param.setLoading, param.setData, param.nullData, null, 1); 
      fetchData(countSearchUrl+"/lawyer?lawyer=", searchTerm, null, param.setData2);
      }}>search</button>
      </div>
    </div>
  )
}

// 사건번호 검색 컴포넌트
function IndexSearchBar(param) {
  const [searchTerm, setSearchTerm] = useState('');
  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  return(
    <div >
      <div style={{display:"flex"}}>
        <input
          type="text"
          placeholder="Search By a Index No"
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <button onClick={() => {
          param.setLoading(true)
          param.setPage(1)
          param.setUrl(indexSearchUrl + searchTerm)
          fetchData(indexSearchUrl, searchTerm, param.setLoading, param.setData, param.nullData, null, 1); 
          fetchData(countSearchUrl + "/indexNo?indexNo=", searchTerm, null, param.setData2);
      }}>search</button>
      </div>
    </div>
  )
}

// 메인 검색 컴포넌트
function Search() {
  const [lawyerData, setLawyerData] = useState(null);
  const [indexData, setIndexData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalcases, setTotalcases] = useState(0);
  const [url, setUrl] = useState(lawyerSearchUrl);
  const [searchTerm, setSearchTerm] = useState('');
  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  const navigate = useNavigate();
  let i = 1;

  // 컴포넌트 마운트 시 초기 데이터 로딩
  useEffect(() => {
    fetchData(lawyerSearchUrl, "", setLoading, setLawyerData, setIndexData, setError, currentPage);
    fetchData(countSearchUrl+"/lawyer?lawyer=", null, null, setTotalcases)
  }, []); // 빈 배열을 두 번째 인자로 전달하여 컴포넌트가 마운트될 때만 실행되도록 함

  if (error) return <p>Error: {error}</p>;
  else if (loading) return <p>Loading...</p>;

  return (
    <>
      {/* 검색창 영역 */}
      <div style={{display:"flex", justifyContent:"center", margin:"1vh 0"}}>
        <LawyerSearchBar searchTerm={searchTerm} handleSearchChange={handleSearchChange} data={lawyerData} setPage={setCurrentPage} setData={setLawyerData} setData2={setTotalcases} setUrl = {setUrl} setLoading = {setLoading} nullData = {setIndexData}/>
        <div style={{width:"1vw"}}/>
        <IndexSearchBar searchTerm={searchTerm} handleSearchChange={handleSearchChange} data={indexData} setPage={setCurrentPage} setData={setIndexData} setData2={setTotalcases} setUrl = {setUrl} setLoading = {setLoading} nullData = {setLawyerData}/>
        <div style={{width:"1vw"}}/>
      </div>

      {/* 변호사 검색 결과 테이블 */}
      {lawyerData != null ? <div className="scroll-container">
        <table>
          <thead>
            <tr>
              <th>No</th>
              <th>Lawyer</th>
              <th>Case</th>
              <th>Win</th>
              <th>Lose</th>
              <th>Rate</th>
            </tr>
          </thead>

          <tbody>
            {lawyerData.map(data => (
              <tr className = "nav-container"onClick={() => navigate('/lawyer/' + data.name)}>
                    <td>{(currentPage - 1) * 50 + i++}</td>
                    <td >{data.name}</td>
                    <td>{data.count}</td>
                    <td>{data.win}</td>
                    <td>{data.lose}</td>
                    <td>{(Number(data.win) / Number(data.count) * 100).toPrecision(3)}%</td>
              </tr>
            ))}
          </tbody>
          
      </table>
      </div> : null}
      
      {/* 사건번호 검색 결과 카드 */}
      {lawyerData == null && indexData != null && indexData.length > 0 ? 
        <header className="App-header">
          <br/>
          <strong className="number_of_case" style={{margin: "6px"}}>
          Index Data founded : {totalcases}
          </strong>
          <div className="list-container">
              {indexData.map((_, index) => (
                <div key={index} className="list-item">
                <h3 onClick={() =>navigate("/case/" + indexData[index].caseName + "/date/" + indexData[index].decisionDate)}>{indexData[index].caseName}</h3>
                <div><strong>Date:</strong> {indexData[index].decisionDate}</div>
                <div><strong>Index No:</strong> {indexData[index].indexNo}</div>
              </div>) )
              }
          </div>
        </header> : null
      }

      {/* 페이지네이션 컴포넌트 */}
      <PaginationComponent
        currentPage={currentPage}
        totalPages={totalcases / 50} // 한 페이지당 50건
        onPageChange={setCurrentPage}
        fetchData = {fetchData}
        setData = {
          (data) => {
            if(url.slice(0, url.indexOf("=") + 1) === lawyerSearchUrl)
              setLawyerData(data)
            else if(url.slice(0, url.indexOf("=") + 1) === indexSearchUrl){
              setIndexData(data)
            }
          }
        }
        SearchUrl = {url}
        name={url.slice(0, url.indexOf("=") + 1) === lawyerSearchUrl  ? searchTerm : ''}
      />   
    </>
  );
}

export default Search;
