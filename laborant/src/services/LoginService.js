class LoginService {
  username = '';
  password = '';

  constructor(){
      this.username = '';
      this.password = '';

  }

  get(url) {
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
  

  // ...
}
export default LoginService;