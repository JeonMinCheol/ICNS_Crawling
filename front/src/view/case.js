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
        <div>
    {Array.isArray(data) ? (
      data.map((caseData, caseIndex) => (
        <div key={caseIndex}>
          {/* 케이스 이름 */}
          <div
            className="case-container-1"
            style={{ display: "flex", alignItems: "center", flexDirection: "column" }}
          >
            <h1 className="case_name">
              {name}, {caseData.caseKind[0].toUpperCase()} 
              {caseData.caseKind.slice(1).toLowerCase()}, #{caseIndex + 1}
            </h1>
          </div>

          {/* 케이스 정보 */}
          <div
            className="case-container-1"
            style={{ display: "flex", alignItems: "center", flexDirection: "column" }}
          >
            <h2 style={{ margin: "1em" }}>Case Information</h2>
            <div className="court" style={{ marginBottom: "1vh", fontSize: "1.2em" }}>
              <strong>Court: </strong> {caseData.courtName}
            </div>
            <div className="judge" style={{ marginBottom: "1vh", fontSize: "1.2em" }}>
              <strong>Judge: </strong> {caseData.judgeName}
            </div>
            <div
              className="decisionDate"
              style={{ marginBottom: "1vh", fontSize: "1.2em" }}
            >
              <strong>Decision Date: </strong> {caseData.decisionDate}
            </div>
            <div className="index_no" style={{ marginBottom: "1vh", fontSize: "1.2em" }}>
              <strong>Index No: </strong> {caseData.indexNo}
            </div>
            <div
              className="slip_op_no"
              style={{ marginBottom: "1vh", fontSize: "1.2em" }}
            >
              <strong>Slip Opinion No: </strong> {caseData.slipOp}
            </div>
            <div className="result" style={{ fontSize: "1.2em" }}>
              <strong>Case Win: </strong> {caseData.result[0].toUpperCase()}
              {caseData.result.slice(1).toLowerCase()}
            </div>
          </div>

          {/* 원고 정보 */}
          <div
            className="case-container-2"
            style={{ display: "flex", alignItems: "center", flexDirection: "column" }}
          >
            <br />
            <h2 style={{ margin: "1em" }}>Plaintiff Information</h2>
            <div className="plaintiff" style={{ marginBottom: "1vh", fontSize: "1.2em" }}>
              <strong className="plaintiff">Plaintiff: </strong> {caseData.plaintiff}
            </div>
            {caseData.plaintiffLawyerName ? (
              caseData.plaintiffLawyerName.map((d, index) => (
                <div
                  key={index}
                  className="plaintiffLawyerName"
                  style={{
                    marginBottom: "1vh",
                    paddingLeft: "20px",
                    fontSize: "1.2em",
                  }}
                >
                  <strong className="plaintiff">lawyer {index + 1}: </strong>
                  {d}
                </div>
              ))
            ) : (
              <div style={{ marginBottom: "1vh", fontSize: "1.2em" }} />
            )}

            <br />
            <h2 style={{ margin: "1em" }}>Defendant Information</h2>
            <div
              className="defendant"
              style={{ marginBottom: "1vh", fontSize: "1.2em" }}
            >
              <strong className="plaintiff">Defendant: </strong> {caseData.defendant}
            </div>
            {caseData.defendantLawyerName ? (
              caseData.defendantLawyerName.map((d, index) => (
                <div
                  key={index}
                  className="defendantLawyerName"
                  style={{
                    marginBottom: "1vh",
                    paddingLeft: "20px",
                    fontSize: "1.2em",
                  }}
                >
                  <strong className="defendant">lawyer {index + 1}: </strong>
                  {d}
                </div>
              ))
            ) : (
              <div style={{ marginBottom: "1vh", fontSize: "1.2em" }} />
            )}

            <br />
            <h2 style={{ margin: "1em" }}>Incident Reason</h2>
            <strong
              className="reason"
              style={{ marginBottom: "1vh", fontSize: "1.2em", width: "95vw" }}
            >
              {caseData.incidentReason}
            </strong>
          </div>

          {/* 문단 및 분석 */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              flexDirection: "column",
              justifyContent: "center",
            }}
          >
            {caseData.sentences.map((_, index) => (
              <div
                key={index}
                className="list-item"
                style={{
                  border: "1px solid black",
                  paddingLeft: "1em",
                  width: "95vw",
                }}
              >
                <h2 style={{ marginBottom: "1vh" }}>Paragraph {index + 1}</h2>
                <div
                  className="paragraph"
                  style={{ width: "93vw", fontSize: "1.2em" }}
                >
                  {caseData.paragraphs[index]}
                </div>
                <h3>Analysis</h3>
                <div
                  className="Analysis"
                  style={{ fontSize: "1.2em" }}
                >
                  {caseData.sentences[index]}
                </div>
              </div>
            ))}
          </div>

          {/* 요약 */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              flexDirection: "column",
            }}
          >
            <h2>Summary</h2>
            <strong
              className="summary"
              style={{
                marginBottom: "10vh",
                fontSize: "1.2em",
                width: "95vw",
                margin: "1em",
              }}
            >
              {caseData.summary}
              <br/>
              <br/>
            </strong>
            {/* 구분선 추가 */}
            <hr
              style={{
                width: "95vw",
                border: "1px solid black",
                marginTop: "1em",
                marginBottom: "2em",
              }}
            />
          </div>
        </div>
      ))
    ) : (
      <p>No data available or data is not in the correct format.</p>
    )}
  </div>
  
  {/* Footer 추가 */}
  <footer
    style={{
      paddingBottom: "1em",
      textAlign: "center",
      color: "#6c757d",
      width: "100%",
    }}
  >
    © {new Date().getFullYear()} Intelligent Computing and Security Laboratory (ICNS Lab), Khuynghee University. All rights reserved.
  </footer>
  </header>
  );
}

export default Case;
