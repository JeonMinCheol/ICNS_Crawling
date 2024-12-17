import React from 'react';
import ReactDOM from 'react-dom/client';
import Laywer_Info from './view/lawyer_info.js';
import Search from './view/search'
import Login from './view/login'
import Case from './view/case'
import { BrowserRouter, Routes, Route } from 'react-router-dom';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <BrowserRouter>
				<Routes>
					<Route path="/" element={<Login />}></Route>
					<Route path="/search" element={<Search />}></Route>
					<Route path="/lawyer/:name" element={<Laywer_Info />}></Route>
          			<Route path="/case/:name/date/:date" element={<Case />}></Route>
				</Routes>
			</BrowserRouter>
);

