import React from 'react';
import {Link} from 'react-router-dom';
import logo from '../laborant.png';
import {Logout} from './Login';

const Header = () => {
    return (
        <header className="Header">
            <div className="header__content">
                <Link to="/">
                    <img src={logo} alt="logo" width="150" height="75" />
                </Link>
                {localStorage.getItem('username') && (
                    <p className="header__username">
                        You are logged in as: <b>{localStorage.getItem('username')}</b>
                    </p>
                )}
                <div className="header__spacer" />
                {localStorage.getItem('isAdmin') === 'true' && (
                    <button className="header__button">
                        <Link to="/panel">Admin Panel</Link>
                    </button>
                )}
                {localStorage.getItem('isAuthenticated') && (
                    <button className="header__button">
                        <Link to="/change-password">Change Pass</Link>
                    </button>
                )}
                {localStorage.getItem('isAuthenticated') && <Logout />}
            </div>
        </header>
    );
};

export default Header;
