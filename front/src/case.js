import "./lawyer_info.css"
import { useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import axiosInstance from './axiosInstance.js';
import baseUrl from "./env.js";

const caseUrl = baseUrl + '/api/caseinfo?casename=';

function urlBuild(base, param1, param2) {
  return base + param1 + "&date=" + param2
}

function Case() {
  // 데이터를 저장할 state 선언
  const navigate = useNavigate();
  const { name, date } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const fetchData = async () => {
    try {
      const response = await axiosInstance.get(urlBuild(caseUrl, name, date)).then(response => {
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
  if (error) return <p>Error: {error}</p>;

  console.log(data)
  return (
      <header className="App-header">
        <button onClick={() => navigate(-1)}>Back to Result</button>
          <h1 className="case_name" style={{marginBottom:"1vh"}}>{name}</h1>

          <div className="case-container-1" style={{display: "flex", alignItems: "flex-start", flexDirection: "column", paddingLeft: "10px"}}>
            <div className="index_no" style={{marginBottom:"1vh"}}><strong>▶ Index No: </strong> {data.indexNo}</div>
            <div className="court" style={{marginBottom:"1vh"}}><strong>▶ Court: </strong> {data.courtName}</div>
            <div className="decisionDate" style={{marginBottom:"1vh"}}><strong>▶ Decision Date: </strong> {data.decisionDate}</div>
          </div>

          <div className="case-container-2" style={{paddingLeft: "10px"}}>
            <div className="plaintiff" style={{marginBottom:"1vh"}}><strong className="plaintiff">▶ Plaintiff: </strong> {data.plaintiff} ({data.plaintiffLawyerWin}, {data.plaintiffLawyerLose})</div>
            {
              data.plaintiffLawyerName ? 
              data.plaintiffLawyerName.map(
                (d, index) => (<div className="plaintiffLawyerName" style={{marginBottom:"1vh", paddingLeft: "20px"}}>
                  <strong className="plaintiff">● Plaintiff's lawyer: </strong>{d}</div>) 
              ) : <div style={{marginBottom:"1vh"}}/>
            }
            
            <div className="defendant" style={{marginBottom:"1vh"}}><strong className="plaintiff">▶ Defendant: </strong> {data.defendant} ({data.defendantLawyerWin}, {data.defendantLawyerLose})</div>
            {
              data.defendantLawyerName ? 
              data.defendantLawyerName.map(
                (d, index) => (<div className="defendantLawyerName" style={{marginBottom:"1vh", paddingLeft: "20px"}}>
                  <strong className="defendant">● Defendant's lawyer: </strong>{d}</div>) 
              ) : <div style={{marginBottom:"1vh"}}/>
            }
           
            <p/>
          </div>

          {data.win}
          {data.lose}

          {data.keyword.map((_, index) => (
            <div key={index} className="list-item" style={{border:"1px solid green"}}>
              <h2 style={{paddingLeft:"10px", marginBottom:"10px"}}>Paragraph</h2>
              <div className="paragraph">{data.paragraph[index]}</div>

              <h3>Keywords</h3>
              <div className="keyword">{data.keyword[index]}</div>
            </div>
          ))}
          

      </header>
  );
}

export default Case;
