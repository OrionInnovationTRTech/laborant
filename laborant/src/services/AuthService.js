import React from "react";
import LoginForm from "./LoginForm";

class AuthService extends React.Component {
    state = {
      username: '',
      password: '',
      isAuthenticated: false
    };
  
    handleLogin = (username, password) => {
      // Update the username and password in the AuthService instance
      this.username = username;
      this.password = password;
      
  
      // Perform a GET request to a protected resource
       this.get('http://localhost:8080/v1/login')
      .then(response => {
        console.log("Received response with status code:", response.status);
        if (response.ok) {
          // Store the username and password in the app state
          this.setState({
            username: username,
            password: password,
            isAuthenticated: true
          });
          // Redirect to main page
          this.props.history.push('/main');
        } else {
          throw new Error('Error logging in: ' + response.statusText);
        }
      })
  
      .catch(error => {
        console.log('Error:', error.message);
        alert('Bad credentials.');
      });
    }
  
    handleLogout = () => {
        // Reset the app state
        this.setState({
          username: '',
          password: ''
        });
        // Redirect to login page
        window.location.replace('/login');
      }
    
      // This method can be used by other components to perform an authenticated GET request
      get = (url) => {
        const { username, password } = this.state;
        this.username = username;
        this.password = password;
    
        if (this.username === '' || this.password === '') {
          throw new Error('Username or password is empty');
        }
        console.log('Username:', this.username);
        console.log('Password:', this.password);
    
        const encodedCredentials = btoa(this.username + ':' + this.password);
        console.log('Encoded credentials:', encodedCredentials);
    
        const requestOptions = {
          method: 'GET',
          headers: {
            'Authorization': 'Basic ' + encodedCredentials
          }
        };
    
        return fetch(url, requestOptions);
      }
    
      render() {
        const { username, password } = this.state;
        // If the user is logged in, render the children of the AuthService component and pass the handleLogin and handleLogout methods as props
        if (username && password) {
          return this.props.children({
            handleLogin: this.handleLogin,
            handleLogout: this.handleLogout
          });
        }
        // If the user is not logged in, render the login form and pass the handleLogin method as a prop
        return <LoginForm handleLogin={this.handleLogin} />;
      }
    }
    
    export default AuthService;
    