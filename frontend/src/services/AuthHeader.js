
  // This function will return the headers object with the authorization header
  export const getHeaders = () => {
    const username = localStorage.getItem("username");
    const password = localStorage.getItem("password");
  
  
    return {
      headers: {
       'Authorization': 'Basic ' + btoa(username + ':' + password),
      }
    };
  };

  