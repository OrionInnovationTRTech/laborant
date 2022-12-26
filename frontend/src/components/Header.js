import React from 'react';
import { Link } from 'react-router-dom';
import logo from '../laborant.png'
import { Logout } from './Login';


const Header = () => {
  return (
    <header className="Header">
      <div style={{display: 'flex', alignItems: 'center'}}>
  <Link to="/"><img src={logo} alt="logo" width="150" height="75" /></Link>
  {localStorage.getItem('username') !== undefined && localStorage.getItem('username')!== null && (
  <text style={{color: 'whitesmoke', fontSize: '20px', marginLeft: '250px'}}>
    You are logged in as: <b>{localStorage.getItem('username')}</b>
  </text>
  )}
  <div style={{flex: 1}}></div>
  {localStorage.getItem('username') === 'admin' && (
          <button style={{marginRight: '20px'}}>
            <Link to="/users">Users</Link>
          </button>
        )}
  <Logout />  
</div>

      
    </header>
  );
}

export default Header;