import "../css/lawyer_info.css"
import { useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import axiosInstance from '../svc/axiosInstance.js';
import baseUrl from "../svc/baseUrl.js";

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
        <div className="case-container-1" style={{display: "flex", alignItems: "center", flexDirection: "column"}}>
          <h1 className="case_name">{name}, {data.caseKind[0].toUpperCase()}{data.caseKind.slice(1).toLowerCase()}</h1>
        </div>

          <div className="case-container-1" style={{display: "flex", alignItems: "center", flexDirection: "column"}}>
            <h2 style={{margin:"1em"}}>Case Information</h2>
            <div className="court" style={{marginBottom:"1vh",  fontSize:"1.2em"}}><strong>Court: </strong> {data.courtName}</div>
            <div className="judge" style={{marginBottom:"1vh",  fontSize:"1.2em"}}><strong>Judge: </strong> {data.judgeName}</div>
            <div className="decisionDate" style={{marginBottom:"1vh",  fontSize:"1.2em"}}><strong>Decision Date: </strong> {data.decisionDate}</div>
            <div className="index_no" style={{marginBottom:"1vh",  fontSize:"1.2em"}}><strong>Index No: </strong> {data.indexNo}</div>
            <div className="slip_op_no" style={{ marginBottom:"1vh", fontSize:"1.2em"}}><strong>Slip Opinion No: </strong> {data.slipOp}</div>
            <div className="result" style={{ fontSize:"1.2em"}}><strong>Case Win: </strong> {data.result[0].toUpperCase()}{data.result.slice(1).toLowerCase()}</div>
          </div>

          <div className="case-container-2" style={{display: "flex", alignItems: "center", flexDirection: "column"}}>
            <br/>
            <h2 style={{margin:"1em"}}>Plaintiff Information</h2>
            <div className="plaintiff" style={{marginBottom:"1vh",  fontSize:"1.2em"}}><strong className="plaintiff">Plaintiff: </strong> {data.plaintiff}</div>
            {
              data.plaintiffLawyerName ? 
              data.plaintiffLawyerName.map(
                (d, index) => (<div className="plaintiffLawyerName" style={{marginBottom:"1vh", paddingLeft: "20px", fontSize:"1.2em"}}>
                  <strong className="plaintiff">lawyer {index + 1}: </strong>{d}</div>) 
              ) : <div style={{marginBottom:"1vh",  fontSize:"1.2em"}}/>
            }
            
            <br/>
            <h2 style={{margin:"1em"}}>Defendant Information</h2>
            <div className="defendant" style={{marginBottom:"1vh",  fontSize:"1.2em"}}><strong className="plaintiff">Defendant: </strong> {data.defendant}</div>
            {
              data.defendantLawyerName ? 
              data.defendantLawyerName.map(
                (d, index) => (<div className="defendantLawyerName" style={{marginBottom:"1vh", paddingLeft: "20px", fontSize:"1.2em"}}>
                  <strong className="defendant">lawyer {index + 1}: </strong>{d}</div>) 
              ) : <div style={{marginBottom:"1vh",  fontSize:"1.2em"}}/>
            }
           
           <br/>
           <h2 style={{margin:"1em"}}>Incident Reason</h2>
           <strong className="reason" style={{marginBottom:"1vh", fontSize:"1.2em", width:"95vw"}}>{data.incidentReason}</strong>
            
          <p/>
        </div>
        
        <div style={{display: "flex", alignItems: "center", flexDirection: "column", justifyContent:"center"}}>
          {data.sentences.map((_, index) => (
              <div key={index} className="list-item" style={{border:"1px solid black", paddingLeft:"1em", width:"95vw"}}>
                <h2 style={{marginBottom:"1vh"}}>Paragraph {index + 1}</h2>
                <div className="paragraph" style={{width:"93vw", fontSize:"1.2em"}}>{data.paragraphs[index]}</div>

                <h3>Analysis</h3>
                <div className="Analysis" style={{fontSize:"1.2em"}}>{data.sentences[index]}</div>
              </div>
            ))}
        </div>

        <div style={{display: "flex", alignItems: "center", flexDirection: "column"}}>
          <h2 >Summary</h2>
          <strong className="summary" style={{marginBottom:"10vh", fontSize:"1.2em", width:"95vw", margin:"1em"}}>{data.summary}</strong>
        </div>
      </header>
  );
}

export default Case;
