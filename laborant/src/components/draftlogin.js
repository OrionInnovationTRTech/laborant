import React from 'react';
import LoginService from '../services/LoginService';


class Login extends React.Component {
  state = {
    username: '',
    password: ''
  };

  loginService = new LoginService();

  handleChange = (event) => {
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  handleSubmit = (event) => {
    event.preventDefault();
    const { username, password } = this.state;

    console.log('Username:', username);
    console.log('Password:', password);

    // Update the username and password in the loginService instance
    this.loginService.username = username;
    this.loginService.password = password;

    // Perform a GET request to a protected resource
     this.loginService.get('http://localhost:8080/v1/login')
    .then(response => {
      if (response.ok) {
        console.log('Response status:', response.status);
      } else {
        throw new Error('Error logging in: ' + response.statusText);
      }
    })
    .then(data => {
      console.log('Response data:', data);
    })
    .catch(error => {
      console.log('Error:', error.message);
    });
  }

  render() {

    return (
      <form onSubmit={this.handleSubmit}>
        <label>
          Username:
          <input type="text" name="username" onChange={this.handleChange} />
        </label>
        <label>
          Password:
          <input type="password" name="password" onChange={this.handleChange} />
        </label>
        <button type="submit">Login</button>
      </form>
    );
  }
}

export default Login;
