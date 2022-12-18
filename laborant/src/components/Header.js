import React from 'react';
import { Link } from 'react-router-dom';
import logo from '../laborant.png'
import { Logout } from './Login';


const Header = () => {
  return (
    <header className="Header">
      <div style={{display: 'flex', alignItems: 'center'}}>
  <Link to="/"><img src={logo} alt="logo" width="150" height="75" /></Link>
  <div style={{flex: 1}}></div>
  <Logout />
</div>

      
    </header>
  );
}

export default Header;
