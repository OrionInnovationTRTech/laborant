import { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export const useAuth = () => {
    return useContext(AuthContext);
};

export const AuthProvider = ({ children }) => {
    const [authState, setAuthState] = useState({
        username: localStorage.getItem('username'),
        isAdmin: !!localStorage.getItem('isAdmin') ,
        isAuthenticated: !!localStorage.getItem('isAuthenticated'),
    });

    return (
        <AuthContext.Provider value={{ authState, setAuthState }}>
            {children}
        </AuthContext.Provider>
    );
};