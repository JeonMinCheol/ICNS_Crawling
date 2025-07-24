import React, { useState } from 'react';
import axios from 'axios';
import "../css/login.css"
import baseUrl from '../svc/baseUrl';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();
        
        try {
            const response = await axios.post(baseUrl + '/api/auth/', {
                email,
                password
            });
            
            // 서버에서 JWT 토큰을 반환하면, 로컬 스토리지에 저장.
            localStorage.setItem('token', response.data.access_token);

            // 성공적으로 로그인하면 다른 페이지로 리디렉션.
            window.location.href = '/search';
        } catch (error) {
            setError('Invalid email or password');
        }
    };
    
    return (
        <div className="login-container">
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                {error && <div className="error-message">{error}</div>}
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default Login;