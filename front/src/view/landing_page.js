import React, { useState } from "react";
import emailjs from "emailjs-com";
import "../css/landing_page.css"

const Landing = () => {

    const [formData, setFormData] = useState({
        name: "",
        email: "",
      });
    
      const [status, setStatus] = useState("");
    
      const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
      };
    
      const handleSubmit = (e) => {
        e.preventDefault();
        setStatus("Sending...");
    
        const serviceID = "";
        const templateID = "";
        const pubKey = ""; // Replace with your EmailJS public key
    
        emailjs
          .send(serviceID, templateID, formData, pubKey)
          .then(
            (response) => {
              console.log("SUCCESS!", response.status, response.text);
              setStatus("Message sent successfully!");
              setFormData({ name: formData.name, email: formData.email});
            },
            (error) => {
              console.error("FAILED...", error);
              setStatus("Failed to send message. Please try again.");
            }
          );
      };

    const go = () => {
        window.location.href = '/login';
    };
    
    return (
    <div className='landing_page'>
        <div style={styles.container}>
            <h2 style={styles.h2}>Contact Us</h2>
            <form onSubmit={handleSubmit} style={styles.form}>
                <label style={styles.label}>Your Name</label>
                <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                style={styles.input}
                />

                <label style={styles.label}>Your Email</label>
                <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                style={styles.input}
                />

                <button type="submit" style={styles.button}>
                Send Message
                </button>
            </form>
            {status && <p style={styles.status}>{status}</p>}
        </div>

        <button className = "landing_button" onClick={go} style={{background:"black", color:"white",  fontFamily: "Arial, sans-serif", fontSize:"14px"}}>로그인하러 가기</button>
    </div>
    );
};

const styles = {
    
    container: {
      fontFamily: "Arial, sans-serif",
      margin: "20px auto",
      width: "42.5vw",
      padding: "20px",
      borderRadius: "12px",
      backgroundColor: "#f9f9f9",
      boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
      position:"absolute",
      bottom:"0",
      left:"6vw"
    },

    h2: {
        padding: "0",
        marginTop:"0",
        marginBottom: "14px",
        textAlign:"center"
    },

    form: {
      display: "flex",
      flexDirection: "column",
    },

    label: {
      color:"black",
      marginBottom: "5px",
    },

    input: {
      padding: "10px",
      marginBottom: "15px",
      borderRadius: "5px",
      border: "1px solid #e0e0e0",
      backgroundColor: "#f5f5f5",
    },
    
    textarea: {
      padding: "10px",
      marginBottom: "15px",
      borderRadius: "5px",
      border: "1px solid #e0e0e0",
      backgroundColor: "#f5f5f5",
    },

    button: {
      padding: "10px",
      background: "linear-gradient(90deg, #00cfff, #ff4f81)", /* 그라디언트 버튼 */
      color: "white",
      fontWeight: "bold",
      border: "none",
      borderRadius: "5px",
      cursor: "pointer",
    },

    buttonHover: {
      backgroundColor: "#0056b3",
    },

    status: {
      marginTop: "10px",
      fontWeight: "bold",
    },
  };

export default Landing;