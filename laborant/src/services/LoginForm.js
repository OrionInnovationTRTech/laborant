import React, { useState } from 'react';
import { Form, Input, Button } from 'antd';

const LoginForm = (props) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleUsernameChange = (event) => {
    setUsername(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };

  const handleSubmit = (event) => {
    console.log("Submitting login form");
    event.preventDefault();
    console.log("Submitting login form with username:", username, "and password:", password);
    props.handleLogin(username, password);
  };

  return (
    <Form>
      <Form.Item label="Username">
        <Input
          type="text"
          value={username}
          onChange={handleUsernameChange}
          placeholder="Enter your username"
          required
        />
      </Form.Item>
      <Form.Item label="Password">
        <Input
          type="password"
          value={password}
          onChange={handlePasswordChange}
          placeholder="Enter your password"
          required
        />
      </Form.Item>
      <Form.Item>
        <Button onClick={handleSubmit} type="primary" htmlType="submit">
          Login
        </Button>
      </Form.Item>
    </Form>
  );
};

export default LoginForm;
