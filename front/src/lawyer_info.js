import "./lawyer_info.css"
import { useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import axiosInstance from './axiosInstance';
import baseUrl from "./env.js";

const lawyerUrl = baseUrl + '/api/lawyerinfo?lawyer=';

function urlBuild(base, param1) {
  return base + param1
}

function Laywer_Info() {
  // 데이터를 저장할 state 선언
  const navigate = useNavigate();
  const { name } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const fetchData = async () => {
    try {
    // API 호출
      const response = await axiosInstance.get(urlBuild(lawyerUrl, name)).then(response => {
        return response
      })
      .catch(error => {
        console.error('Error:', error);
      });  
      
     // state에 데이터 저장
     setData(response.data);
    } catch (err) {
      // 에러가 발생한 경우 에러 메시지 저장
      setError(err.message);
    } finally {
      // 로딩 상태 해제
      setLoading(false);
    }
  };

  // 컴포넌트가 마운트될 때 실행되는 useEffect
  useEffect(() => {
    fetchData();
  }, []); // 빈 배열을 두 번째 인자로 전달하여 컴포넌트가 마운트될 때만 실행되도록 함

  // 로딩 중일 때 표시할 컴포넌트
  if (loading) return <p>Loading...</p>;

  // 에러가 발생한 경우 표시할 컴포넌트
  else if (error) return <p>Error: {error}</p>;
  console.log(data)
  return (
      <header className="App-header">
        <button onClick={() => navigate(-1)}>Back to Result</button>
        
        <h2>{data.name}</h2>
        <h3 style={{paddingLeft:"10px", margin:0, marginTop:"10px"}}>Record</h3>

        <div className="info-container" >
          <div className="win-lose">
            <div>● Win: {data.case_win}</div>
            <div>● Lose: {data.case_lose}</div>
          </div>
        </div>

        <br/>
        <strong className="number_of_case" style={{margin: "6px"}}>
          Total case : {data.count}
        </strong>

        <div className="list-container">
          {data.caseName.map((_, index) => (
            <div key={index} className="list-item">
              <h3 onClick={() =>navigate("/case/" + data.caseName[index] +"/date/"+ data.date[index])}>{data.caseName[index]}</h3>
              <div><strong>Date:</strong> {data.date[index]}</div>
              <div><strong>Index No:</strong> {data.indexNo[index]}</div>
              <div><strong>▶<a href={data.url[index]} target="_blank" rel="noopener noreferrer">Case Link</a></strong></div>
            </div>
          ))}
        </div>

      </header>
  );
}

export default Laywer_Info;
