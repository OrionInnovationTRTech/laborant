import React, { useState } from 'react';
import {Navigate} from 'react-router-dom';

const LoginForm = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const handleSubmit = event => {
    event.preventDefault();

    fetch('http://localhost:8080/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: new URLSearchParams({
        username,
        password,
      }).toString(),
    })
    .then(response => {
      if (response.ok) {
        setIsAuthenticated(true);
        console.log('Authentication successful');
      } else {
        throw new Error('Authentication failed');
      }
    })
    .catch(error => console.error(error));
};

if (isAuthenticated) {
  return <Navigate to="/labs" />;
}
  return (
    <form onSubmit={handleSubmit}>
      <label>
        Username:
        <input
          type="text"
          value={username}
          onChange={event => setUsername(event.target.value)}
        />
      </label>
      <label>
        Password:
        <input
          type="password"
          value={password}
          onChange={event => setPassword(event.target.value)}
        />
      </label>
      <button type="submit">Log in</button>
    </form>
  );
};

export default LoginForm;
