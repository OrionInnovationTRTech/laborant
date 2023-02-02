import React from 'react';
import axios from "axios";

class Logout extends React.Component {
  state = {
    isAuthenticated: false,
  };

  logout = () => {
    localStorage.clear();
    window.location.replace('http://localhost:3000/login')
    this.setState({
      isAuthenticated: false,
    });
  }

  render() {
    return(
        <button onClick={this.logout}>Logout</button>
    );
  }
}

class LoginForm extends React.Component {
  state = {
    username: '',
    password: '',
    isAuthenticated: false,
  };

  handleChange = (event) => {
    this.setState({
      [event.target.name]: event.target.value,
    });
  }

  handleSubmit = (event) => {
    event.preventDefault();

    const { username, password } = this.state;

    axios.get(`${process.env.REACT_APP_SPRING_HOST}/users/${username}`, {
      headers: {
        'Authorization': 'Basic ' + btoa(username + ':' + password),
      },
    })
      .then((response) => {
       if (response.status === 200) {
           if (response.data.authorities[0].authority.includes("ADMIN")) {
               console.log("admin");
               localStorage.setItem('isAdmin', true);
           } else {
                console.log("not admin");
               localStorage.setItem('isAdmin', false);
           }

          // If the authentication is successful, save the username and password in local storage
          localStorage.setItem('username', username);
          localStorage.setItem('password', password);
          this.setState({
            isAuthenticated: true,
          });
          localStorage.setItem('isAuthenticated', true);
          window.location.replace('/labs');
        }
        else{
          throw new Error('Error logging in: ' + response.statusText);
        }
      })

      .catch(error => {
        console.log('Error:', error.message);
        alert('Could not authenticated');
      });
    }

  render() {
    const { username, password, isAuthenticated } = this.state;

    if (localStorage.getItem('isAuthenticated')) {
      // If the user is authenticated, show a message
      return <p>You are logged in as {localStorage.getItem('username')}.</p>;
    }

    return (
      <form onSubmit={this.handleSubmit}>
        <label>
          username:
          <input type="text" name="username" value={username} onChange={this.handleChange} />
        </label>
        <label>
          password:
          <input type="password" name="password" value={password} onChange={this.handleChange} />
        </label>
        <button type="submit">Login</button>
      </form>
    );
  }
}

export default LoginForm;
export { Logout };