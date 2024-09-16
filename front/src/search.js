import "./search.css"
import { useNavigate } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import PaginationComponent from './pagination';
import axiosInstance from './axiosInstance';
import baseUrl from "./env.js";

const lawyerSearchUrl = baseUrl + '/api/search/lawyer?lawyer=';
const indexSearchUrl = baseUrl + '/api/search/index?indexNo=';
const keywordSearchUrl = baseUrl + '/api/search/keyword?keyword=';
const countSearchUrl = baseUrl + '/api/count';

function urlBuild(base, param1, page) {
  if(page != null)
    return base + param1 + "&page=" + page

  return base + param1
}

const fetchData = async (searchUrl, name = null, setLoading = null, setData = null, nullData1 = null, nullData2 = null, setError = null, page = null) => {
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
    if (nullData1 != null)
      nullData1(null);
    if (nullData2 != null)
      nullData2(null);
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
      param.setUrl(lawyerSearchUrl)
      fetchData(lawyerSearchUrl, searchTerm, param.setLoading, param.setData, param.nullData1, param.nullData2, null, 1); 
      fetchData(countSearchUrl+"/lawyer?lawyer=", searchTerm, null, param.setData2);
      }}>search</button>
      </div>
    </div>
  )
}

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
          param.setUrl(indexSearchUrl)
          fetchData(indexSearchUrl, searchTerm, param.setLoading, param.setData, param.nullData1, param.nullData2, null, 1); 
          fetchData(countSearchUrl + "/indexNo?indexNo=", searchTerm, null, param.setData2);
      }}>search</button>
      </div>
    </div>
  )
}

function KeywordSearchBar(param) {
  const [searchTerm, setSearchTerm] = useState(null);
  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  return(
    <div>
      <div style={{display:"flex"}}>
        <input
          type="text"
          placeholder="Search By a Keyword"
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <button onClick={() => {
          param.setLoading(true)
          param.setUrl(keywordSearchUrl)
          param.setPage(1)
          fetchData(keywordSearchUrl, searchTerm, param.setLoading, param.setData, param.nullData1, param.nullData2, null, 1); 
          fetchData(countSearchUrl + "/keyword?keyword=", searchTerm, null, param.setData2);
      }}>search</button>
      </div>
    </div>
  )
}

function Search() {
  const [lawyerData, setLawyerData] = useState(null);
  const [indexData, setIndexData] = useState(null);
  const [keywordData, setKeywordData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalcases, setTotalcases] = useState(0);
  const [url, setUrl] = useState(lawyerSearchUrl);


  const navigate = useNavigate();
  let i = 1;

  // 컴포넌트가 마운트될 때 실행되는 useEffect
  useEffect(() => {
    fetchData(lawyerSearchUrl, "", setLoading, setLawyerData, setIndexData, setKeywordData, setError, currentPage);
    fetchData(countSearchUrl+"/lawyer?lawyer=", null, null, setTotalcases)
  }, []); // 빈 배열을 두 번째 인자로 전달하여 컴포넌트가 마운트될 때만 실행되도록 함

  if (error) return <p>Error: {error}</p>;
  else if (loading) return <p>Loading...</p>;

  return (
    <>
      <div style={{display:"flex", justifyContent:"center", margin:"1vh 0",}}>
        <LawyerSearchBar data={lawyerData} setPage={setCurrentPage} setData={setLawyerData} setData2={setTotalcases} setUrl = {setUrl} setLoading = {setLoading} nullData1 = {setIndexData} nullData2 = {setKeywordData}/>
        <div style={{width:"1vw"}}/>
        <IndexSearchBar data={indexData} setPage={setCurrentPage} setData={setIndexData} setData2={setTotalcases} setUrl = {setUrl} setLoading = {setLoading} nullData1 = {setLawyerData} nullData2 = {setKeywordData}/>
        <div style={{width:"1vw"}}/>
        <KeywordSearchBar data={keywordData} setPage={setCurrentPage} setData={setKeywordData} setData2={setTotalcases} setUrl = {setUrl} setLoading = {setLoading} nullData1 = {setLawyerData} nullData2 = {setIndexData}/>
      </div>

      {
      
      lawyerData != null ? <div className="scroll-container">
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
                    <td>{(currentPage - 1) * 20 + i++}</td>
                    <td >{data.name}</td>
                    <td>{data.count}</td>
                    <td>{data.case_win}</td>
                    <td>{data.case_lose}</td>
                    <td>{(Number(data.case_win) / Number(data.count) * 100).toPrecision(3)}%</td>
              </tr>
            ))}
          </tbody>
          
      </table>
      </div> : null}

      {lawyerData == null && indexData != null ? 
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
                <div><strong>▶<a href={indexData[index].url} target="_blank" rel="noopener noreferrer">Case Link</a></strong></div>
              </div>) )
              }
              
          </div>
        </header> : null
      }

      {lawyerData == null && keywordData != null ? 
        <header className="App-header">
          <br/>
          <strong className="number_of_case" style={{margin: "6px"}}>
            Keyword Data founded : {totalcases}
          </strong>
            
          <div className="list-container">
          {keywordData.map((_, index) => (
              <div key={index} className="list-item">
                <h3 onClick={() =>navigate("/case/" + keywordData[index].caseName +"/date/" + keywordData[index].decisionDate)}>{keywordData[index].caseName}</h3>
                <div><strong>Date:</strong> {keywordData[index].decisionDate}</div>
                <div><strong>Index No:</strong> {keywordData[index].indexNo}</div>
                <div><strong>▶<a href={keywordData[index].url} target="_blank" rel="noopener noreferrer">Case Link</a></strong></div>
              </div>
            ))} 
            </div>
        </header> 
       : null
      }

      <PaginationComponent
        currentPage={currentPage}
        totalPages={totalcases / 20}
        onPageChange={setCurrentPage}
        fetchData = {fetchData}
        setData = {
          (data) => {
            if(url === lawyerSearchUrl)
              setLawyerData(data)
            else if(url === indexSearchUrl)
              setIndexData(data)
            else
              setKeywordData(data)
          }
        }
        SearchUrl = {url}
      />   
    </>
  );
}


export default Search;
