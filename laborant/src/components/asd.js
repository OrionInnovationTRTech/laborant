import { WindowSharp, WindPower } from '@mui/icons-material';
import React from 'react';
// import Cookies from 'js-cookie';
import Cookies from 'universal-cookie';

class Logout extends React.Component {
  state = {
    isAuthenticated: false,
  };

  logout = () => {
    localStorage.clear();
    window.location.replace('http://localhost:3000/login')
    console.log('local: '+ localStorage.getItem('sessionId') );
    console.log('cookies: ' + Cookies.get('JSESSIONID'));
    Cookies.remove('JSESSIONID');
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

    fetch('http://localhost:8080/v1/login', {
      method: 'GET',
      headers: {
        'Authorization': 'Basic ' + btoa(username + ':' + password),
      },
    })
      .then((response) => {
       if (response.ok) {
        console.log(localStorage.getItem('isAuthenticated'));

          // If the authentication is successful, save the username and password in local storage
          localStorage.setItem('username', username);
          localStorage.setItem('password', password);
          this.setState({
            isAuthenticated: true,
          });
          localStorage.setItem('isAuthenticated', true);

          
        return response.json();
        }
        else{
          throw new Error('Error logging in: ' + response.statusText);
        }
      })
      .then((data) => {
        const cookies = new Cookies ();
        // Save the session ID in local storage
        if (localStorage.getItem('isAuthenticated')) {
        localStorage.setItem('sessionId', data.sessionId);
        console.log('first:' +localStorage.getItem('sessionId'));
        cookies.set('sasd',data.sessionId);
        console.log('cookies: '+cookies.get('sasd'))
        }
      })
  
      .catch(error => {
        console.log('Error:', error.message);
        alert('Couldn not authenticated');
      });
    }

  render() {
    const { username, password, isAuthenticated } = this.state;

    if (localStorage.getItem('isAuthenticated')) {
      // If the user is authenticated, show a message
     // window.location.replace('/labs')
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