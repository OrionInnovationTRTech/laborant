import React, { useEffect, useState } from 'react';
import {Link} from 'react-router-dom';
import logo from '../laborant.png';
import {Logout} from './Login';
import { useAuth } from '../services/AuthContext';

const Header = () => {
    const { authState } = useAuth();

    return (
        <header className="Header">
            <div className="header__content">
                <Link to="/">
                    <img src={logo} alt="logo" width="150" height="75" />
                </Link>
                {authState.username && (
                    <p className="header__username">
                        You are logged in as: <b>{authState.username}</b>
                    </p>
                )}
                <div className="header__spacer" />
                {authState.isAdmin && (
                    <button className="header__button">
                        <Link to="/panel">Admin Panel</Link>
                    </button>
                )}
                {authState.isAuthenticated && (
                    <button className="header__button">
                        <Link to="/dashboard">Dashboard</Link>
                    </button>
                )}
                {authState.isAuthenticated && <Logout />}
            </div>
        </header>
    );
};

export default Header;
