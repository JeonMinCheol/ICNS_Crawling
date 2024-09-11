import React from 'react';
import ReactDOM from 'react-dom/client';
import Laywer_Info from './lawyer_info';
import reportWebVitals from './reportWebVitals';
import Search from './search'
import Case from './case'
import { BrowserRouter, Routes, Route } from 'react-router-dom';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <BrowserRouter>
				<Routes>
					<Route path="/" element={<Search />}></Route>
					<Route path="/lawyer/:name" element={<Laywer_Info />}></Route>
          			<Route path="/case/:name/date/:date" element={<Case />}></Route>
				</Routes>
			</BrowserRouter>
);

reportWebVitals();
